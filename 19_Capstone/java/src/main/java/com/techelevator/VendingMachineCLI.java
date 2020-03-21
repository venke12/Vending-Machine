package com.techelevator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.techelevator.VendingMachine.VendableItem;
import com.techelevator.view.Menu;

public class VendingMachineCLI {

	private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "Display Vending Machine Items";
	private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase";
	private static final String MAIN_MENU_OPTION_EXIT = "Exit";
	private static final String MAIN_MENU_OPTION_SALES_REPORT = "";
	private static final String[] MAIN_MENU_OPTIONS = 
		{ MAIN_MENU_OPTION_DISPLAY_ITEMS, MAIN_MENU_OPTION_PURCHASE, MAIN_MENU_OPTION_EXIT, MAIN_MENU_OPTION_SALES_REPORT };
	
	private static final String PURCHASE_MENU_OPTION_FEED_MONEY = "Feed Money";
	private static final String PURCHASE_MENU_OPTION_SELECT_PRODUCT = "Select Product";
	private static final String PURCHASE_MENU_OPTION_FINISH = "Finish Transaction";
	private static final String[] PURCHASE_MENU_OPTIONS = 
		{ PURCHASE_MENU_OPTION_FEED_MONEY, PURCHASE_MENU_OPTION_SELECT_PRODUCT, PURCHASE_MENU_OPTION_FINISH };

	private Menu mainMenu;
	private Menu purchaseMenu;
	
	public VendingMachineCLI() {
		
	}

	public VendingMachineCLI(Menu mainMenu, Menu purchaseMenu) {
		this.mainMenu = mainMenu;
		this.purchaseMenu = purchaseMenu;
	}
	
	// user input required, not tested
	public void runMainMenu(List<VendableItem> inventoryList, Map<String, Integer> inventoryMap) {
		boolean shouldLoop = true;
		while (shouldLoop) {
			String choice = (String) mainMenu.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			switch (choice) {
			case MAIN_MENU_OPTION_DISPLAY_ITEMS: {
				printInventory(inventoryList);
				break;
			}
			case MAIN_MENU_OPTION_PURCHASE: {
				runPurchaseMenu(inventoryList, inventoryMap);
				break;
			}
			case MAIN_MENU_OPTION_EXIT: {
				shouldLoop = false;
				break;	
			}
			case MAIN_MENU_OPTION_SALES_REPORT: {
				
				File reportFile = new File("salesReport.txt");
				File salesFile = new File("salesData.txt");
				Scanner salesScanner = null;
				try {
					salesScanner = new Scanner(salesFile);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				PrintWriter reportWriter = null;
				try {
					reportWriter = new PrintWriter(reportFile);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				
				Map<String, Integer> dataMap = null;
				try {
					dataMap = convertFileToMap(salesFile);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				
				
				double salesTotal = 0;
				while (salesScanner.hasNextLine()) {
					String line = salesScanner.nextLine();
					reportWriter.println(line);
				}
				
				for (VendableItem item : inventoryList) {
					int productCount = dataMap.get(item.getProduct());
					if(productCount > 0) {
						salesTotal += item.getPrice()*productCount;				
					}
				}
				
				String salesLine = String.format("\nTOTAL SALES: $%6.2f", salesTotal);
				reportWriter.append(salesLine);
				System.out.println("File salesReport.txt has been generated");
				reportWriter.close();
				salesScanner.close();
				
				break;
			}
			}
		}
	}
	// user input required, not tested
	public void runPurchaseMenu(List<VendableItem> inventoryList, Map<String, Integer> inventoryMap) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		FileWriter logFile = null;
		try {
			logFile = new FileWriter("log.txt", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter write = new PrintWriter(logFile);
		
		double customerAccount = 0; // convert double
		boolean shouldLoop = true;
		while (shouldLoop) {
			System.out.printf("Current Money Provided: $%4.2f\n", customerAccount);
			String choice = (String) purchaseMenu.getChoiceFromOptions(PURCHASE_MENU_OPTIONS);
			
			switch (choice) {
			case PURCHASE_MENU_OPTION_FEED_MONEY: {
				double previousBalance = customerAccount;
				customerAccount = getCustomerAccount(customerAccount);
				double difference = customerAccount - previousBalance;
				Date date = new Date();
				String dateLine = dateFormat.format(date);
				String logLine = String.format(dateLine + "\t%20s: $%4.2f \tUpdated Balance: $%4.2f\n", "FEED MONEY",
						difference, customerAccount);
				write.append(logLine);
				break;
			}
			// call within the purchase menu, which requires user input
			// manages item selection calls and increments the salesReport.txt file
			case PURCHASE_MENU_OPTION_SELECT_PRODUCT: {
				printInventory(inventoryList);
				VendableItem itemSelection = getUserSelection(inventoryList);
				if (checkCustomerAccount(customerAccount, itemSelection.getPrice())) {
					itemSelection.vendProduct();
					double previousBalance = customerAccount;
					customerAccount -= itemSelection.getPrice();
					System.out.printf("\nProduct: %-20s Cost: $%4.2f \tRemaining Balance: $%6.2f\n", 
							itemSelection.getProduct(), itemSelection.getPrice(), customerAccount);
					System.out.println(itemSelection.getYumMessage(itemSelection.getProductType()) + "\n");
					Date date = new Date();
					String dateLine = dateFormat.format(date);
					String logLine = String.format(dateLine + "\t%20s: $%4.2f \tUpdated Balance: $%4.2f\n", 
							itemSelection.getProduct(), previousBalance, customerAccount);
					write.append(logLine);
					
					try {
						inventoryMap = convertFileToMap(getFile());
						String productName = itemSelection.getProduct();
						int productCount = inventoryMap.get(productName);
						productCount ++;					
						inventoryMap.put(productName, productCount);
						convertMapToFile(inventoryMap, inventoryList);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("Insufficient funds");
				}
				break;
			}
			// calculates the refund in quarters, dimes and nickels
			// ***********************************************************
			// customerAccountRounded//customerAccount is a double which is rounded, then cast as an integer
			// quartersReturned divides customerAccountRounded by 25
			// customerAccountRounded then subtracts the product of quartersReturned * 25
			// dimesReturned divides customerAccountRounded by 10
			// customerAccountRounded then subtracts the product of dimesReturned * 10
			// nickelsReturned divides customerAccountRounded by 10
			// ***********************************************************
			case PURCHASE_MENU_OPTION_FINISH: {
				int customerAccountRounded = (int)Math.round(customerAccount * 100);
				int quartersReturned = customerAccountRounded/25;
				customerAccountRounded -= (quartersReturned * 25);
				int dimesReturned = (customerAccountRounded / 10);
				customerAccountRounded -= (dimesReturned * 10);
				int nickelsReturned = (customerAccountRounded / 5);
				customerAccountRounded -= (nickelsReturned * 5);
				
				System.out.printf("\nReturning $%4.2f as " + quartersReturned + " quarter(s), "
						+ dimesReturned + " dime(s), and " + nickelsReturned + " nickel(s).", customerAccount);
				System.out.println();
				shouldLoop = false;
				
				Date date = new Date();
				String dateLine = dateFormat.format(date);
				String logLine = String.format(dateLine + "\t%20s: $%4.2f \tUpdated Balance: $%4.2f\n", 
				"GIVE CHANGE", customerAccount, (double)customerAccountRounded);
				write.append(logLine);
				
				
				break;
			}
			}
		}
		write.close();
	}
	
	// ensures that customer has sufficient funds for the purchase TESTED
	public static boolean checkCustomerAccount(double customerAccount, double price) { // convert double
		if (customerAccount >= price) {
			return true;
		}
		return false;
	}
	
	// processes customer purchases
	@SuppressWarnings("resource")
	private static VendableItem getUserSelection(List<VendableItem> inventoryList) {
		Scanner userInput = new Scanner(System.in);
		VendableItem userSelection = null;
		boolean validId = false;
		while(!validId) {
			System.out.println("Enter ID of item to dispense: ");
			String id = userInput.nextLine();
	
			for (VendableItem product : inventoryList) {
				if (product.getSlotID().equalsIgnoreCase(id)) {
					if (product.getStockCount() > 0) {
						userSelection = product;
						validId = true;
						continue;
					} else if (product.getStockCount() == 0) {
						System.out.println("Out of stock");
						continue;
					}
				}
			}
			if (validId == false) {
				System.out.println("Not an item");
			}
		}
		return userSelection;
	}
	
	// processes customer $$ transfer
	@SuppressWarnings("resource")
	private static double getCustomerAccount(double customerAccount) { // convert double
		Scanner input = new Scanner(System.in);
		double newAccountBalance = 0; // convert double
		while (newAccountBalance == 0) {
			System.out.println("Enter a whole dollar amount to withdraw from your account: ");
			String userInput = input.nextLine();
			double inputMoney = Double.parseDouble(userInput);
			try {
				newAccountBalance = customerAccount + inputMoney; // convert double
			} catch (NumberFormatException e) {
				System.out.println("NaN entered, please don't be an idiot");
			}
		}
		return newAccountBalance;
	}
	
	// reads the provided .csv data file
	private static File getFile() throws FileNotFoundException {
		String path = "vendingmachine.csv";
		File inputFile = new File(path);
		if (inputFile.exists() == false) {
			System.out.println(path + " does not exist");
			System.exit(0);
		} else if (inputFile.isFile() == false) {
			System.out.println(path + " is not a valid file");
			System.exit(0);
		}
		return inputFile;
	}
	
	//TEST THIS
	@SuppressWarnings("resource")
	public static List<VendableItem> convertFileToList(File inputFile) throws FileNotFoundException {
		List<VendableItem> inventoryList = new ArrayList<>();
		Scanner fileScanner = new Scanner(inputFile);
		
		while (fileScanner.hasNextLine()) {
			String line = fileScanner.nextLine();
			String[] dataArray = line.split("\\|");
			VendableItem product = new VendableItem(dataArray[0], dataArray[1], dataArray[2], dataArray[3]);
			inventoryList.add(product);
		}
		return inventoryList;
	}
	
	// Sales Report - checks existence of a map file, if NOT, creates file and loads default data TESTED
	@SuppressWarnings("resource")
	public static Map<String, Integer> convertFileToMap(File inputFile) throws FileNotFoundException {
		Map<String, Integer> inventoryMap = new HashMap<>();
		
		String path = "salesData.txt";
		File salesInput = new File(path);
		Scanner fileScanner = new Scanner(inputFile);
		if (salesInput.exists() == false) {
			while (fileScanner.hasNextLine()) {
				String line = fileScanner.nextLine();
				String[] dataArray = line.split("\\|");
				inventoryMap.put(dataArray[1], 0);
			}
		} else if (salesInput.exists() == true) {
			Scanner salesReportScanner = new Scanner(salesInput);
			while (salesReportScanner.hasNextLine()) {
				String line = salesReportScanner.nextLine();
				String[] dataArray = line.split("\\|");
				inventoryMap.put(dataArray[0], Integer.parseInt(dataArray[1]));
			}
		}
		return inventoryMap;
	}
	
	private static void convertMapToFile (Map<String, Integer> inventoryMap, List<VendableItem> inventoryList) throws FileNotFoundException {
		String path = "salesData.txt";
		File salesInput = new File(path);
		PrintWriter writer = new PrintWriter (salesInput);
		
		for (VendableItem item : inventoryList) {
			int productCount = inventoryMap.get(item.getProduct());
			writer.println(item.getProduct() + "|" + productCount);
		}
		writer.close();
	}
	
	
	private static void printInventory(List<VendableItem> inventory) {
		for (VendableItem product : inventory) {
			System.out.printf("%-4s %20s %6.2f %14s %3d\n", product.getSlotID(), product.getProduct(), 
					product.getPrice(), product.getProductType(), product.getStockCount());
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		File inputFile = getFile();
		Map<String, Integer> inventoryMap = convertFileToMap(inputFile);
		List<VendableItem> inventoryList = convertFileToList(inputFile);
		Menu mainMenu = new Menu(System.in, System.out);
		Menu purchaseMenu = new Menu(System.in, System.out);
		
		VendingMachineCLI cli = new VendingMachineCLI(mainMenu, purchaseMenu);
		cli.runMainMenu(inventoryList, inventoryMap);
	}
}
