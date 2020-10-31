package com.github.piotrostrow.eo.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.piotrostrow.eo.config.Config;
import com.github.piotrostrow.eo.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Config.GAME_PATH = arg[0];

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.vSyncEnabled = false;
		config.foregroundFPS = 240;
		config.resizable = false;
		config.width = 1280;
		config.height = 720;

		new LwjglApplication(new Main(), config);
	}
}
