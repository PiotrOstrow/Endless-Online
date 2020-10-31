package com.github.piotrostrow.eo.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.github.piotrostrow.eo.config.Config;
import com.github.piotrostrow.eo.map.emf.EmfMap;
import com.github.piotrostrow.eo.map.emf.EmfMapLoader;
import com.github.piotrostrow.eo.pe.PEFile;
import com.github.piotrostrow.eo.pe.ResourceDataEntry;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Assets {

	private static final HashMap<Integer, Texture> gfx = new HashMap<>();
	private static final AssetManager assetManager = new AssetManager();

	public static Texture gfx(int folder, int ID){
		return gfx.get(folder << 16 | ID);
	}

	public static EmfMap getMap(int mapID) {
		String path = Config.GAME_PATH + "/maps/" + String.format("%05d", mapID) + ".emf";
		if (!assetManager.contains(path)){
			assetManager.load(path, EmfMap.class);
			assetManager.finishLoading();
		}
		return assetManager.get(path);
	}

	public static void init() {
		loadTextures();

		// emf map loader
		assetManager.setLoader(EmfMap.class, new EmfMapLoader());
	}

	private static void loadTextures() {
		try {
			for (int i = 0; i < 25; i++) {
				PEFile peFile = new PEFile(Config.GAME_PATH + "/gfx/gfx" + String.format("%03d", i + 1) + ".egf");
				List<ResourceDataEntry> bitmapDataEntries = peFile.getBitmapDataEntries();

				for(ResourceDataEntry entry : bitmapDataEntries) {
					Texture texture = peFile.loadTexture(entry);
					texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
					gfx.put((i + 1) << 16 | entry.getID(), texture);
				}
			}
		}catch (IOException e){
			e.printStackTrace();
			Gdx.app.exit();
		}
	}
}
