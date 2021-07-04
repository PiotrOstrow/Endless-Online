package com.github.piotrostrow.eo.map.emf;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.piotrostrow.eo.assets.Assets;
import com.github.piotrostrow.eo.map.AnimatedMapTile;
import com.github.piotrostrow.eo.map.DoorMapTile;
import com.github.piotrostrow.eo.map.MapTile;
import com.github.piotrostrow.eo.map.StaticMapTile;

import java.io.IOException;

public class MapLayer {

	private final MapTile[][] tiles;

	MapLayer(EmfFileInputStream stream, int width, int height, int fill, int layerIndex) throws IOException {
		short[][] gfx = new short[height][width];
		tiles = new MapTile[width][height];

		if(layerIndex == 0)
			for(int y = 0; y < height; y++)
				for(int x = 0; x < width; x++)
					gfx[y][x] = (short) fill;

		int rows = stream.readUnsignedByte();
		for (int i = 0; i < rows; ++i) {
			int y = stream.readUnsignedByte();
			int tiles = stream.readUnsignedByte();
			for (int j = 0; j < tiles; ++j) {
				int x = stream.readUnsignedByte();
				int s =  stream.readUnsignedShort();

				if (x < width && y < height) {
					gfx[y][x] = (short) s;
				}
			}
		}

		long timestamp = TimeUtils.millis();
		for(int y = 0; y < gfx.length; y++)
			for(int x = 0; x < gfx[y].length; x++)
				if(gfx[y][x] != 0)
					setTile(gfx[y][x] + 100, layerIndex, x, y, timestamp);
	}

	void setTile(int gfxID, int layerIndex, int x, int y, long timestamp){
		Texture texture = null;
		switch(layerIndex) {
			case 6: //elevated
			case 0: //ground layer
				texture = Assets.gfx(3, gfxID);
				break;
			case 1: //objects layer
				texture = Assets.gfx(4, gfxID);
				break;
			case 2: //objects overlay layer
			case 8: //roof layer
				texture = Assets.gfx(5, gfxID);
				break;
			case 3: //walls south
			case 4: //walls east
				texture = Assets.gfx(6, gfxID);
				break;
			case 5: //ceiling
				texture = Assets.gfx(7, gfxID);
				break;
			case 7: //shadow
				texture = Assets.gfx(22, gfxID);
				break;
			default:
				throw new RuntimeException("Unknown layer index: " + layerIndex);
		}

		// ground layer
		if(texture != null) {
			if (layerIndex == 0 ||layerIndex == 6) {
				// all the animated tiles from folder 3, guess it's easier to just check texture width instead of hardcoding indices
				if(texture.getWidth() > 64) {
					tiles[x][y] = new AnimatedMapTile(texture, timestamp);
					return;
				}
			} else if(layerIndex == 3 || layerIndex == 4) {
				switch (gfxID) {
					case 304: case 432: case 434: case 436: case 438: case 440: case 442: case 444: case 446: case 448:
					case 531: case 584: case 586: case 588: case 590: case 674: case 676: case 742: case 750: case 791:
					case 806: case 824: case 826: case 834: case 836: case 852: case 886: case 879: case 881: case 900:
					case 908: case 929: case 941: case 949: case 1163:
						tiles[x][y] = new DoorMapTile(texture, Assets.gfx(6, gfxID + 1));
						return;
				}
			}

			tiles[x][y] = new StaticMapTile(texture);
		}
	}

	public MapTile getTile(int x, int y){
		if(x < 0 || x >= tiles.length || y < 0 || y >= tiles[0].length)
			return null;
		return tiles[x][y];
	}
}
