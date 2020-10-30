package com.github.piotrostrow.eo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.piotrostrow.eo.assets.Assets;

public class Main extends ApplicationAdapter {

	private SpriteBatch spriteBatch;
	private Texture texture;

	@Override
	public void create () {
		Assets.init();
		spriteBatch = new SpriteBatch();
		texture = Assets.gfx(8, 101);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		spriteBatch.begin();
		spriteBatch.draw(texture, 0, 0);
		spriteBatch.end();
	}
	
	@Override
	public void dispose () {
	}
}
