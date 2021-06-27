package com.github.piotrostrow.eo.ui.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.github.piotrostrow.eo.assets.Assets;

public class Inventory extends WidgetGroup {

	public Inventory() {
		Texture backgroundTexture = Assets.gfx(2, 144);

		Image backgroundImage = new Image(backgroundTexture);
		addActor(backgroundImage);

		setSize(backgroundTexture.getWidth(), backgroundImage.getHeight());
	}
}
