package com.github.piotrostrow.eo.ui.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.piotrostrow.eo.assets.Assets;
import com.github.piotrostrow.eo.shaders.GfxShader;
import com.github.piotrostrow.eo.ui.actors.LoginWindow;
import com.github.piotrostrow.eo.ui.actors.RegisterWindow;

public class MainMenuStage extends Stage {

	public final Button createAccountButton, playButton, creditButton, exitButton;

	private RegisterWindow registerPanel;
	private LoginWindow loginWindow;

	public MainMenuStage() {
		super(new ScreenViewport());
		getBatch().setShader(new GfxShader());

		Texture buttons1 = Assets.gfx(1, 113);
		TextureRegion[][] buttons1Split = TextureRegion.split(buttons1, buttons1.getWidth() / 2, buttons1.getHeight() / 4);

		Table table = new Table();
		table.setPosition(50, 50);
		addActor(table);

		createAccountButton = createButton(buttons1Split[0][0], buttons1Split[0][1]);
		playButton = createButton(buttons1Split[1][0], buttons1Split[1][1]);
		creditButton = createButton(buttons1Split[2][0], buttons1Split[2][1]);
		exitButton = createButton(buttons1Split[3][0], buttons1Split[3][1]);

		table.add(createAccountButton).pad(0);
		table.row();
		table.add(playButton).pad(0);
		table.row();
		table.add(creditButton).pad(0);
		table.row();
		table.add(exitButton).pad(0);

		table.pack();

		exitButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
	}

	private Button createButton(TextureRegion up, TextureRegion down){
		Button.ButtonStyle style = new Button.ButtonStyle();
		style.up = new TextureRegionDrawable(up);
		style.over = style.down = new TextureRegionDrawable(down);
		return new Button(style);
	}

	@Override
	public void dispose() {
		// batch was set to use a custom shader that we have to dispose
		getBatch().getShader().dispose();
		super.dispose();
	}
}