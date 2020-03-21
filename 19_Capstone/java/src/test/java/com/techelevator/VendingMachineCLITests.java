package com.techelevator;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.techelevator.VendingMachine.VendableItem;

public class VendingMachineCLITests {
	
	@Test
	public void test_that_account_has_sufficient_funds_returns_true() {
		double customerAccount = 5.00;
		double price = 3.05;
		assertTrue(VendingMachineCLI.checkCustomerAccount(customerAccount, price));
	}
	
	@Test
	public void test_filetolistdummy_converts_to_list() {
		File dummyFile = new File("filetolistdummy.csv");
		List<VendableItem> actual = null;
		try {
			actual = VendingMachineCLI.convertFileToList(dummyFile);
		} catch (FileNotFoundException e) {
			System.out.println("filetolistdummy.csv does not exist");
			e.printStackTrace();
		}
		List<VendableItem> expected = new ArrayList<>();
		VendableItem item1 = new VendableItem("A1", "Potato Crisps", "3.05", "Chip");
		VendableItem item2 = new VendableItem("A2", "Stackers", "1.45", "Chip");
		expected.add(item1);
		expected.add(item2);
		
		assertTrue(expected.get(0).getSlotID().equals(actual.get(0).getSlotID()));
		assertTrue(expected.get(0).getProduct().equals(actual.get(0).getProduct()));
		assertTrue(expected.get(0).getPrice() == actual.get(0).getPrice());
		assertTrue(expected.get(0).getProductType().equals(actual.get(0).getProductType()));
	}
	
	@Test
	public void test_convertfiletomap_converts_file_to_map() {
		File dummyFile = new File("temp.txt");
		Map<String, Integer> actual = null;
		try {
			actual = VendingMachineCLI.convertFileToMap(dummyFile);
		} catch (FileNotFoundException e) {
			System.out.println("temp.txt does not exist");
			e.printStackTrace();
		}
		Map<String, Integer> expected = new HashMap<>();
		expected.put("Potato Crisps", 0);
		expected.put("Stackers", 0);
		
		assertTrue(expected.get("Potato Crisps").equals(actual.get("Potato Crisps")));
		assertTrue(expected.get("Stackers").equals(actual.get("Stackers")));
	}

}
