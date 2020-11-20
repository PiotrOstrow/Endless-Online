package com.github.piotrostrow.eo.map.emf;

import java.io.IOException;

public class Spawner {

	public final int x;
	public final int y;
	public final int id;
	public final int type;
	public final int spawnTime;
	public final int amount;

	public Spawner(EmfFileInputStream stream) throws IOException {
		this.x = stream.readUnsignedByte();
		this.y = stream.readUnsignedByte();
		this.id = stream.readUnsignedShort();
		this.type = stream.readUnsignedByte();
		this.spawnTime = stream.readUnsignedShort();
		this.amount = stream.readUnsignedByte();
	}
}
