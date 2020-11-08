package com.github.piotrostrow.eo.map.emf;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.piotrostrow.eo.assets.Assets;

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

		if(texture != null) {

			switch (gfxID) {
				//all the animated tiles
				case 396:
				case 397:
				case 555:
				case 561:
				case 562:
				case 563:
				case 564:
				case 565:
				case 601:
				case 614:
				case 648:
				case 649:
				case 685:
				case 716:
				case 751:
				case 752:
				case 760:
					tiles[x][y] = new AnimatedMapTile(texture, timestamp);
					break;
				default:
					tiles[x][y] = new StaticMapTile(texture);
					break;
			}
		}
	}

	public MapTile getTile(int x, int y){
		return tiles[x][y];
	}
}
