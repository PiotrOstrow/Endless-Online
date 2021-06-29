package com.github.piotrostrow.eo.game;

public class Item {

	private final int itemID;
	private final int amount;

	public Item(int itemID, int amount) {
		this.itemID = itemID;
		this.amount = amount;
	}

	public int getAmount() {
		return amount;
	}

	public int getItemID() {
		return itemID;
	}
}
