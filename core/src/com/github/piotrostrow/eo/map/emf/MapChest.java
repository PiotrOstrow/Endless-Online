package com.github.piotrostrow.eo.map.emf;

import java.io.IOException;

public class MapChest {

	public final int chestID;

	public final int x, y;
	public final int key_required;
	public final int chest_slot;
	public final int item_id;
	public final int spawn_time;
	public final int item_amount;

	MapChest(EmfFileInputStream stream, int mapHeight, int chestID) throws IOException {
		this.chestID = chestID;
		x = stream.readUnsignedByte();
		y = mapHeight - 1 - stream.readUnsignedByte();
		key_required = stream.readUnsignedShort();
		chest_slot = stream.readUnsignedByte();
		item_id = stream.readUnsignedShort();
		spawn_time = stream.readUnsignedShort();
		item_amount = stream.threeByteInt();
	}
}
