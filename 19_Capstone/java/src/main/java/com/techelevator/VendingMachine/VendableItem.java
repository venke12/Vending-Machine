package com.techelevator.VendingMachine;

public class VendableItem {
	private String slotID;
	private String product;
	private double price;
	private String productType;
	private int stockCount;

	public VendableItem(String slotID, String product, String price, String productType) {
		super();
		this.slotID = slotID;
		this.product = product;
		this.price = Double.parseDouble(price);
		this.productType = productType;
		this.stockCount = 5;
	}

	public String getSlotID() {
		return slotID;
	}

	public String getProduct() {
		return product;
	}

	public double getPrice() {
		return price;
	}

	public String getProductType() {
		return productType;
	}
	
	public int getStockCount() {
		return stockCount;
	}
	
	public void vendProduct() {
		stockCount -= 1;
	}
	
	public String getYumMessage(String productType) {
		switch(productType) {
		case "Chip" : 
			return "Crunch Crunch, Yum!";
		case "Candy" : 
			return "Munch Munch, Yum!";
		case "Drink" : 
			return "Glug Glug, Yum!";
		case "Gum" : 
			return "Chew Chew, Yum!";
		}
		return "";
	}

}
