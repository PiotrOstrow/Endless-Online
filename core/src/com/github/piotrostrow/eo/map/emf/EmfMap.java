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

	public final MapLayer groundLayer;
	public final MapLayer shadowLayer;
	public final MapLayer wallsSouthLayer;
	public final MapLayer wallsEastLayer;
	public final MapLayer objectsOverlayLayer;
	public final MapLayer objectsLayer;
	public final MapLayer elevatedLayer;
	public final MapLayer ceilingLayer;
	public final MapLayer roofLayer;

	public final ArrayList<MapChest> mapChests;
	public final TileSpecs tileSpecs;
	public final ArrayList<Warp> warps;
	public final ArrayList<SignMessage> msgs = new ArrayList<>();

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
			MapChest mapChest = new MapChest(stream, i);
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


		MapLayer[] layers = new MapLayer[9];
		for (int i = 0; i < layers.length; i++) {
			if(stream.available() > 0) {
				layers[i] = new MapLayer(stream, width, height, fillTile, i);
			}
		}

		this.groundLayer = layers[0];
		this.objectsLayer = layers[1];
		this.objectsOverlayLayer = layers[2];
		this.wallsSouthLayer = layers[3];
		this.wallsEastLayer = layers[4];
		this.ceilingLayer = layers[5];
		this.elevatedLayer = layers[6];
		this.shadowLayer = layers[7];
		this.roofLayer = layers[8];

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
}
