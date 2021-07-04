package com.github.piotrostrow.eo.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.piotrostrow.eo.config.Config;
import com.github.piotrostrow.eo.graphics.CustomFrameBuffer;
import com.github.piotrostrow.eo.graphics.GfxShader;
import com.github.piotrostrow.eo.map.emf.EmfMap;
import com.github.piotrostrow.eo.map.emf.EmfMapLoader;
import com.github.piotrostrow.eo.pe.PEFile;
import com.github.piotrostrow.eo.pe.ResourceDataEntry;
import com.github.piotrostrow.eo.pub.EifFile;
import com.github.piotrostrow.eo.pub.EnfFile;
import com.github.piotrostrow.eo.pub.ItemData;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Assets {

	private static final HashMap<Integer, Texture> gfx = new HashMap<>();
	private static final AssetManager assetManager = new AssetManager();

	public static final TextureLoader.TextureParameter textureParameters;
	private static final BitmapFontLoader.BitmapFontParameter bitmapLoaderParameters;

	private static EnfFile enfFile;
	private static EifFile eifFile;

	static {
		textureParameters = new TextureLoader.TextureParameter();
		textureParameters.magFilter = Texture.TextureFilter.Nearest;
		textureParameters.minFilter = Texture.TextureFilter.Nearest;

		bitmapLoaderParameters = new BitmapFontLoader.BitmapFontParameter();
		bitmapLoaderParameters.minFilter = Texture.TextureFilter.Nearest;
		bitmapLoaderParameters.magFilter = Texture.TextureFilter.Nearest;

		try {
			enfFile = new EnfFile(new File(Config.GAME_PATH + "/pub/dtn001.enf"));
			eifFile = new EifFile(new File(Config.GAME_PATH + "/pub/dat001.eif"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void init() {
		loadTextures();
		processTextures();

		// emf map loader
		assetManager.setLoader(EmfMap.class, new EmfMapLoader());
	}

	private static void processTextures() {
		Texture iconTexture = gfx(2, 132);
		SpriteBatch batch = new SpriteBatch();
		batch.setShader(new GfxShader());

		processTexture(batch, 2, 132);
		processTexture(batch, 2, 158);

		batch.dispose();
	}

	private static void processTexture(Batch batch, int folder, int ID) {
		Texture texture = gfx(folder, ID);
		TextureRegion textureRegion = new TextureRegion(texture);
		textureRegion.flip(false, true);

		CustomFrameBuffer customFrameBuffer = new CustomFrameBuffer(Pixmap.Format.RGBA8888, texture.getWidth(), texture.getHeight(), false);
		customFrameBuffer.bind();

		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		batch.draw(textureRegion, 0, 0);
		batch.end();

		gfx.put((folder << 16) | ID, customFrameBuffer.getColorBufferTexture());
		customFrameBuffer.dispose();
	}

	public static EnfFile.Npc getNpcData(int id) {
		EnfFile.Npc npcData =  enfFile.getNpcData(id);

		if(!npcData.isInitialized())
			npcData.initialize();

		return npcData;
	}

	public static ItemData getItemData(int itemID) {
		return eifFile.getItemData(itemID);
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

	public static Texture gfx(int folder, int ID){
		return gfx.get(folder << 16 | ID);
	}

	public static Texture getTexture(String name) {
		if(!assetManager.contains(name)){
			assetManager.load(name, Texture.class);
			assetManager.finishLoadingAsset(name);
		}
		return assetManager.get(name);
	}

	public static EmfMap getMap(int mapID) {
		String path = Config.GAME_PATH + "/maps/" + String.format("%05d", mapID) + ".emf";
		if (!assetManager.contains(path)){
			assetManager.load(path, EmfMap.class);
			assetManager.finishLoading();
		}
		return assetManager.get(path);
	}

	public static BitmapFont getFont(String path){
		if(!assetManager.contains(path)){
			assetManager.load(path, BitmapFont.class, bitmapLoaderParameters);
			assetManager.finishLoadingAsset(path);
		}
		return assetManager.get(path, BitmapFont.class);
	}

	public static Drawable getTextCursor(BitmapFont font, Color color){
		Label.LabelStyle labelStyle = new Label.LabelStyle();
		labelStyle.font = font;
		labelStyle.fontColor = color;
		Label oneCharSizeCalibrationThrowAway = new Label("|", labelStyle);
		Pixmap cursorPixmap = new Pixmap(3, (int) oneCharSizeCalibrationThrowAway.getHeight(), Pixmap.Format.RGBA8888);
		cursorPixmap.setColor(color);
		cursorPixmap.fill();
		cursorPixmap.setColor(0, 0, 0, 1);
		cursorPixmap.drawLine(0, 0, 0, cursorPixmap.getHeight());
		return new TextureRegionDrawable(new Texture(cursorPixmap));
	}

	public static Drawable getTextSelector(BitmapFont font, Color color) {
		Label.LabelStyle labelStyle = new Label.LabelStyle();
		labelStyle.font = font;
		labelStyle.fontColor = color;
		Label oneCharSizeCalibrationThrowAway = new Label("|", labelStyle);
		Pixmap cursorPixmap = new Pixmap(1, (int) oneCharSizeCalibrationThrowAway.getHeight(), Pixmap.Format.RGBA8888);
		cursorPixmap.setColor(color);
		cursorPixmap.fill();
		return new TextureRegionDrawable(new Texture(cursorPixmap));
	}
}
