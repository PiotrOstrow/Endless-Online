package com.github.piotrostrow.eo.map.emf;

import java.io.IOException;

public class TileSpecs {

	public static final byte BANK_VAULT = 17;
	public static final byte SPIKES_STATIC = 36;
	public static final byte SPIKES_TRAP = 37;

	private final byte[][] specs;

	TileSpecs(EmfFileInputStream stream, int width, int height) throws IOException {
		specs = new byte[width][height];

		int rows = stream.readUnsignedByte();
		for(int i = 0; i < rows; ++i){
			int y = stream.readUnsignedByte();
			int tiles = stream.readUnsignedByte();
			for(int j = 0; j < tiles; ++j){
				int x = stream.readUnsignedByte();
				int c = stream.readUnsignedByte();
				if(x < width && y < height) { // TODO: check negatives? lol
					//tileSpecs.add(new EmfTileSpec(x, y, c));
					specs[x][y] = (byte) (c + 1);
				}
			}
		}
	}
}
