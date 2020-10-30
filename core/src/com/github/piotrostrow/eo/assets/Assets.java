package com.github.piotrostrow.eo.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.github.piotrostrow.eo.config.Config;
import com.github.piotrostrow.eo.pe.PEFile;
import com.github.piotrostrow.eo.pe.ResourceDataEntry;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Assets {

	private static final HashMap<Integer, Texture> gfx = new HashMap<>();

	public static Texture gfx(int folder, int ID){
		return gfx.get(folder << 16 | ID);
	}

	public static void init() {
		loadTextures();
	}

	private static void loadTextures() {
		try {
			for (int i = 0; i < 25; i++) {
				PEFile peFile = new PEFile(Config.GAME_PATH + "/gfx/gfx" + String.format("%03d", i + 1) + ".egf");
				List<ResourceDataEntry> bitmapDataEntries = peFile.getBitmapDataEntries();

				for(ResourceDataEntry entry : bitmapDataEntries) {
					Texture texture = peFile.loadTexture(entry);
					gfx.put((i + 1) << 16 | entry.getID(), texture);
				}
			}
		}catch (IOException e){
			e.printStackTrace();
			Gdx.app.exit();
		}
	}
}
