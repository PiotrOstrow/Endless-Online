package com.github.piotrostrow.eo.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.GridPoint2;
import com.github.piotrostrow.eo.assets.Assets;
import com.github.piotrostrow.eo.pub.ItemData;

public class MapItem {

	private final int itemID;
	private final int uid;
	private final int amount;

	private final GridPoint2 position;

	private final ItemData itemData;

	public MapItem(int itemID, int uid, int amount, int x, int y) {
		this.itemID = itemID;
		this.uid = uid;
		this.amount = amount;
		this.position = new GridPoint2(x, y);

		this.itemData = Assets.getItemData(itemID);
	}

	public GridPoint2 getPosition() {
		return position;
	}

	public Texture getMapTexture() {
		return itemData.getMapTexture();
	}
}
