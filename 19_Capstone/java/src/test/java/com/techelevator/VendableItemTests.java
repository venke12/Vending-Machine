package com.techelevator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.techelevator.VendingMachine.VendableItem;

public class VendableItemTests {

	VendableItem input = new VendableItem("A1","Potato Crisps","3.05", "Chip");
	@Test
	public void test_whether_getslotID_returns_string_A1() {
		String expected = "A1";
		assertEquals(expected, input.getSlotID());
	}
	@Test
	public void test_whether_getProduct_returns_string_Potato_Crisps() {
		String expected = "Potato Crisps";
		assertEquals(expected, input.getProduct());
	}
	@Test
	public void test_whether_getPrice_returns_string_3_05() {
		double expected = 3.05;
		assertEquals(expected, input.getPrice(),001);
	}
	@Test
	public void test_whether_getProductType_returns_string_Chip() {
		String expected = "Chip";
		assertEquals(expected, input.getProductType());
	}
	@Test
	public void test_whether_getStockCount_returns_int_5() {
		int expected = 5;
		assertEquals(expected, input.getStockCount());
	}
	@Test
	public void test_whether_vendProduct_reduces_stockCount_by_1_to_int_4() {
		int expected = 4;
		input.vendProduct();
		assertEquals(expected, input.getStockCount());
	}
	@Test
	public void test_whether_get_yum_message_returns_Crunch() {
	String expected = "Crunch Crunch, Yum!";
	assertEquals(expected, input.getYumMessage("Chips"));
	}
	@Test
	public void test_whether_get_yum_message_returns_Munch() {
	String expected = "Munch Munch, Yum!";
	assertEquals(expected, input.getYumMessage("Candy"));
	}
	@Test
	public void test_whether_get_yum_message_returns_Glug() {
	String expected = "Glug Glug, Yum!";
	assertEquals(expected, input.getYumMessage("Drink"));
	}
	@Test
	public void test_whether_get_yum_message_returns_Chew() {
	String expected = "Chew Chew, Yum!";
	assertEquals(expected, input.getYumMessage("Gum"));
	}
}
