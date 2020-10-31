package com.github.piotrostrow.eo.screens;

import com.badlogic.gdx.Screen;
import com.github.piotrostrow.eo.assets.Assets;
import com.github.piotrostrow.eo.map.Zone;

public class GameScreen implements Screen {

	private Zone currentZone;

	public GameScreen() {
		currentZone = new Zone(Assets.getMap(5));
	}

	@Override
	public void show() {

	}

	private void update(){
		currentZone.update();
	}

	@Override
	public void render(float delta) {
		update();

		currentZone.render();
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		if(currentZone != null)
			currentZone.dispose();
	}
}
