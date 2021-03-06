package com.github.piotrostrow.eo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.github.piotrostrow.eo.assets.Assets;
import com.github.piotrostrow.eo.game.GameUI;
import com.github.piotrostrow.eo.net.Client;
import com.github.piotrostrow.eo.mainmenu.MainMenuScreen;

public class Main extends Game {

	public static final Main instance;

	public static final Client client = new Client();

	static {
		instance = new Main();
	}

	private Main(){

	}

	@Override
	public void create () {
		Assets.init();

		setScreen(new MainMenuScreen());
	}

	@Override
	public void render () {
		Gdx.graphics.setTitle("FPS: " + Gdx.graphics.getFramesPerSecond());

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		super.render();
	}
}
