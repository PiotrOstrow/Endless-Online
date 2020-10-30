package com.github.piotrostrow.eo.map.emf;

import java.io.IOException;
import java.util.ArrayList;

public class EmfMap {

	private String mapName;

	private final int mapType;
	private final int effect;
	private final int musicID;
	private final int musicExtra;
	private final int ambientSoundID;
	private final int width;
	private final int height;
	private final int fillTile;
	private final int mapAvailable;
	private final int canScroll;
	private final int relogX;
	private final int relogY;

	private final MapLayer[] layers = new MapLayer[9];

	private final ArrayList<MapChest> mapChests;
	private final TileSpecs tileSpecs;
	private final ArrayList<Warp> warps;
	private final ArrayList<SignMessage> msgs = new ArrayList<>();

	EmfMap(EmfFileInputStream stream) throws IOException {
		// skip magic number
		stream.skip(3);

		//skip revision number
		stream.skip(4);

		String mapName = stream.readEncodedString(24);
		for(int i = 0; i < mapName.length(); i++){
			if((byte)mapName.charAt(i) == -3) {
				this.mapName = mapName.substring(0, i);
				break;
			}
		}

		mapType = stream.readUnsignedByte();
		effect = stream.readUnsignedByte();
		musicID = stream.readUnsignedByte();
		musicExtra = stream.readUnsignedByte();
		ambientSoundID = stream.readUnsignedShort();
		width = stream.read();
		height = stream.read();
		fillTile = stream.readUnsignedShort();
		mapAvailable = stream.readUnsignedByte();
		canScroll = stream.readUnsignedByte();
		relogX = stream.readUnsignedByte();
		relogY = stream.readUnsignedByte();

		//skip unknown value
		stream.skip(1);

		//npc count
		int npcCount = stream.readUnsignedByte();
		stream.skip(npcCount * 8);

		int unknownCount = stream.readUnsignedByte();
		stream.skip(unknownCount * 4);

		int mapChestCount = stream.readUnsignedByte();
		mapChests = new ArrayList<MapChest>(mapChestCount);
		for(int i = 0; i < mapChestCount; i++){
			MapChest mapChest = new MapChest(stream, height, i);
			mapChests.add(mapChest);
		}

		tileSpecs = new TileSpecs(stream, width, height);

		warps = new ArrayList<Warp>();
		int rows = stream.readUnsignedByte();
		for (int i = 0; i < rows; i++) {
			int y = stream.readUnsignedByte();
			int tiles = stream.readUnsignedByte();
			for (int j = 0; j < tiles; j++) {
				int x = stream.readUnsignedByte();
				Warp warp = new Warp(stream, x, y);
				if (x < width && y < height)
					warps.add(warp);
			}
		}


		for (int i = 0; i < layers.length; i++) {
			if(stream.available() > 0) {
				layers[i] = new MapLayer(stream, width, height, fillTile, i);
			}
		}


		if (stream.available() > 0){
			int signs = stream.readUnsignedByte();
			for (int i = 0; i < signs; i++){
				int x = stream.readUnsignedByte();
				int y = stream.readUnsignedByte();

				int length = stream.readUnsignedShort() - 1;
				String data = stream.readEncodedString(length);

				int titleLength = stream.readUnsignedByte();
				String title = data.substring(0, titleLength);
				String message = data.substring(titleLength);

				msgs.add(new SignMessage(x, y, title, message));
			}
		}

		stream.close();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public MapLayer getMapLayer(int index){
		return layers[index];
	}
}
