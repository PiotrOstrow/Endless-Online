package com.github.piotrostrow.eo.screens;

import com.badlogic.gdx.Screen;

public class MainMenuScreen implements Screen {

	private final MainMenuBackground background;

	public MainMenuScreen() {
		background = new MainMenuBackground();
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		background.update();
		background.render();
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
		dispose();
	}

	@Override
	public void dispose() {
		background.dispose();
	}
}
