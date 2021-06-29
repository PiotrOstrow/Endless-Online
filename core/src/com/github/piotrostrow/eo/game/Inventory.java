package com.github.piotrostrow.eo.game;

import java.util.List;

public class Inventory {

	private final List<Item> itemList;
	private int weight;
	private int maxWeight;

	public Inventory(List<Item> itemList, int weight, int maxWeight) {
		this.itemList = itemList;
		this.weight = weight;
		this.maxWeight = maxWeight;
	}

	public List<Item> getItemList() {
		return itemList;
	}

	public int getWeight() {
		return weight;
	}

	public int getMaxWeight() {
		return maxWeight;
	}
}
