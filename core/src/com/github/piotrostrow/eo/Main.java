package com.github.piotrostrow.eo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.github.piotrostrow.eo.assets.Assets;
import com.github.piotrostrow.eo.screens.MainMenuScreen;
import com.github.piotrostrow.eo.shaders.GfxShader;

public class Main extends Game {

	private SpriteBatch spriteBatch;

	@Override
	public void create () {
		Assets.init();

		spriteBatch = new SpriteBatch();
		spriteBatch.setShader(new GfxShader());

		setScreen(new MainMenuScreen());
	}

	@Override
	public void render () {
		Gdx.graphics.setTitle("FPS: " + Gdx.graphics.getFramesPerSecond());

		if(Gdx.input.isKeyJustPressed(Input.Keys.F5)){
			ShaderProgram shader = spriteBatch.getShader();
			spriteBatch.setShader(new ShaderProgram(
					Gdx.files.internal("C:\\dev\\endless_online\\core\\assets\\shaders\\gfx_shader.vert"),
					Gdx.files.internal("C:\\dev\\endless_online\\core\\assets\\shaders\\gfx_shader.frag")));
			shader.dispose();
			System.out.println("Shader reloaded");
		}

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		super.render();
	}
	
	@Override
	public void dispose () {
	}
}
