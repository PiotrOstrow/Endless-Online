package com.github.piotrostrow.eo.map.emf;

import java.io.IOException;

public class Warp {

	public final int x, y;
	public final int destinationMapID;
	public final int destinationMapX, destinationMapY;
	public final int levelRequired;
	public final int doorType;

	Warp(EmfFileInputStream stream, int x, int y) throws IOException {
		this.x = x;
		this.y = y;
		destinationMapID = stream.readUnsignedShort();
		destinationMapX = stream.readUnsignedByte();
		destinationMapY = stream.readUnsignedByte();
		levelRequired = stream.readUnsignedByte();
		doorType = stream.readUnsignedShort();
	}
}
