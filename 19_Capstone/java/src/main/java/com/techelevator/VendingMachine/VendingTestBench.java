package com.techelevator.VendingMachine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VendingTestBench {

	public static void main(String[] args) throws FileNotFoundException {
		
		List<VendableItem> testList = new ArrayList<>();
		
		File inputFile = getFile();
		Scanner userInput = new Scanner(System.in);
		Scanner fileScanner = new Scanner(inputFile);
		while (fileScanner.hasNextLine()) {
			String line = fileScanner.nextLine();
			String[] dataArray = line.split("\\|");
			VendableItem product = new VendableItem(dataArray[0], dataArray[1], dataArray[2], dataArray[3]);
			testList.add(product);
		}
		printInventory(testList);
		
		boolean validId = false;
		while(!validId) {
			System.out.println("Enter ID of item to dispense: ");
			String id = userInput.nextLine();
	
			for (VendableItem product : testList) {
				if (product.getSlotID().equalsIgnoreCase(id)) {
					if (product.getStockCount() > 0) {
						product.vendProduct(); // subtracts 1 from stockCount of product
						validId = true;
						continue;
					} else if (product.getStockCount() == 0) {
						System.out.println("Out of stock");
						continue;
					}
//					System.out.println("Not an item");
				}
//				} else {
//					System.out.println("Not an item");
//				}
			}
//			System.out.println("Not an item");
		}
		printInventory(testList);
		

	}
	
	private static File getFile() {
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
	
	private static void printInventory(List<VendableItem> inventory) {
		for (VendableItem product : inventory) {
			System.out.printf("%-4s %20s %6.2f %14s %3d\n", product.getSlotID(), product.getProduct(), 
					product.getPrice(), product.getProductType(), product.getStockCount());
		}
	}

}
