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
				if(x < width && y < height) {
					specs[x][y] = (byte) (c + 1);
				}
			}
		}
	}

	public byte get(int x, int y){
		if(x < 0 || y < 0) return 0;
		if(x >= specs.length || y >= specs[0].length) return 0;
		return specs[x][y];
	}

	public boolean blocked(int x, int y){
		switch(specs[x][y]) {
			case 1: //Wall
			case 2: //ChairDown
			case 3: //ChairLeft
			case 4: //ChairRight
			case 5: //ChairUp
			case 6: //ChairDownRight
			case 7: //ChairUpLeft
			case 8: //ChairAll
			case 9: //JammedDoor
			case 10: //Chest
			case 17: //BankVault
			case 21: //Board1
			case 22: //Board2
			case 23: //Board3
			case 24: //Board4
			case 25: //Board5
			case 26: //Board6
			case 27: //Board7
			case 28: //Board8
			case 29: //Jukebox
			case 19: //MapEdge
			case 16: //Unknown, map 0 campfire
				return true;
			case 0: // nothing - default value for element in array
			case 18: //NPCBoundary
			case 20: //FakeWall
			case 30: //Jump
			case 31: //Water
			case 33: //Arena
			case 34: //AmbientSource
			case 35: //SpikesTimed
			case 36: //SpikesStatic
			case 37: //SpikesTrap
				return false;
			default:
				System.err.println("Unknown tile spec: " + specs[x][y] + "(keep in mind all specs were incremented by 1, for instance wall is 1 instead of 0)");
				return false;
		}
	}
}
