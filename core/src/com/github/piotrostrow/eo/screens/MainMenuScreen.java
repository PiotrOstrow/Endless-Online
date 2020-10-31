package com.github.piotrostrow.eo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.piotrostrow.eo.ui.stages.MainMenuStage;

public class MainMenuScreen implements Screen {

	private Stage currentStage;

	private final MainMenuStage mainMenuStage;

	private final MainMenuBackground background;

	public MainMenuScreen() {
		background = new MainMenuBackground();
		mainMenuStage = new MainMenuStage();

		setStage(mainMenuStage);
	}

	private void setStage(Stage stage) {
		this.currentStage = stage;
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		background.update();
		currentStage.act(Gdx.graphics.getDeltaTime());

		background.render();
		currentStage.draw();
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
