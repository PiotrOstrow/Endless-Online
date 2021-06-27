package com.github.piotrostrow.eo.ui.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.github.piotrostrow.eo.assets.Assets;

public class ChatWindow extends WidgetGroup {

	private Image backgroundImage;

	public ChatWindow() {
		Texture backgroundTexture = Assets.gfx(2, 128);

		backgroundImage = new Image(backgroundTexture);
		addActor(backgroundImage);

		setSize(backgroundTexture.getWidth(), backgroundImage.getHeight());
	}
}
