package com.github.piotrostrow.eo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.github.piotrostrow.eo.assets.Assets;
import com.github.piotrostrow.eo.map.emf.EmfMap;
import com.github.piotrostrow.eo.map.emf.EmfMapRenderer;
import com.github.piotrostrow.eo.shaders.GfxShader;

public class MainMenuBackground implements Disposable {

	private final EmfMapRenderer mapRenderer;

	private SpriteBatch batch;
	private Texture texture;

	private EmfMap map;

	public MainMenuBackground() {
		batch = new SpriteBatch();
		batch.setShader(new GfxShader());
		texture = Assets.gfx(21, 4345);

		map = Assets.getMap(222);

		mapRenderer = new EmfMapRenderer(map);
	}

	public void update() {

	}

	public void render() {
		if(Gdx.input.isTouched()){
			mapRenderer.camera.position.add(-Gdx.input.getDeltaX(), Gdx.input.getDeltaY(), 0);
			mapRenderer.camera.update();
		}

		mapRenderer.render();

		batch.begin();
		batch.draw(texture, 0, 0);
		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();

	}
}
