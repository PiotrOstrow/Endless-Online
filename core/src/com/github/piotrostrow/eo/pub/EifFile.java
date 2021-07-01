package com.github.piotrostrow.eo.pub;

import com.github.piotrostrow.eo.map.emf.EmfFileInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class EifFile {

	private final ItemData[] items;

	public EifFile(File file) throws IOException {
		EmfFileInputStream stream = new EmfFileInputStream(new FileInputStream(file));
		stream.skip(7);

		items = new ItemData[stream.readUnsignedShort() - 1];
		stream.skip(1);

		for(int i = 0; i < items.length; i++) {
			items[i] = new ItemData(stream, i + 1);
//			int sex = items[i].spec2; // 1 F, 2 M
//			items[i].itemID = i + 1 + idOffset;
//			int icon = Integer.parseInt(items[i].icon) * 2 + 100 + iconOffset;
//			int ground = icon - 1;
//			items[i].icon = "eo_converted/gfx023_egf/" + icon + ".png";
//			items[i].ground = "eo_converted/gfx023_egf/" + ground + ".png";
		}
		stream.close();
	}

	public ItemData getItemData(int itemID) {
		if(itemID < 0 || itemID > items.length)
			return null;
		return items[itemID - 1];
	}
}
