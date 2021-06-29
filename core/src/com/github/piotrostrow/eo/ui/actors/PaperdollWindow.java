package com.github.piotrostrow.eo.ui.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.piotrostrow.eo.assets.Assets;

public class PaperdollWindow extends WidgetGroup {

	public PaperdollWindow() {
		Texture backgroundTexture = Assets.gfx(2, 149);
		Texture buttonAtlas = Assets.gfx(1, 115);

		TextureRegion[][] buttonsTR = TextureRegion.split(buttonAtlas, buttonAtlas.getWidth() / 2, buttonAtlas.getHeight() / 10);

		TextureRegion femaleBackground = new TextureRegion(backgroundTexture, 0, 0, backgroundTexture.getWidth(), backgroundTexture.getHeight() / 2);
		TextureRegion maleBackground = new TextureRegion(backgroundTexture, 0, backgroundTexture.getHeight() / 2, backgroundTexture.getWidth(), backgroundTexture.getHeight() / 2);

		Button.ButtonStyle okButtonStyle = new Button.ButtonStyle();
		okButtonStyle.up = new TextureRegionDrawable(buttonsTR[4][0]);
		okButtonStyle.down = okButtonStyle.over = new TextureRegionDrawable(buttonsTR[4][1]);

		Image backgroundImage = new Image(maleBackground);
		addActor(backgroundImage);

		// TODO: process texture
//		Button okButton = new Button(okButtonStyle);
//		okButton.setPosition(100, 10);
//		addActor(okButton);

		setSize(backgroundTexture.getWidth(), backgroundTexture.getHeight() / 2);
		setPosition(50, Gdx.graphics.getHeight() - getHeight() - 50);
	}
}
