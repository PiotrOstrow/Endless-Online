package com.github.piotrostrow.eo.net.structs;

import com.github.piotrostrow.eo.net.Packet;

public class NpcData {

	public final int index;
	public final int id;
	public final int x, y;
	public final int direction;

	public NpcData(Packet packet) {
		this.index = packet.readEncodedByte();
		this.id = packet.readEncodedShort();
		this.x = packet.readEncodedByte();
		this.y = packet.readEncodedByte();
		this.direction = packet.readEncodedByte();
	}

	public NpcData(int index, int id, int x, int y, int direction) {
		this.index = index;
		this.id = id;
		this.x = x;
		this.y = y;
		this.direction = direction;
	}
}
