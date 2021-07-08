package com.github.piotrostrow.eo.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.piotrostrow.eo.assets.Assets;

public class HPBar extends WidgetGroup {

	private static final int VISIBLE_DURATION = 1000;

	private final Image foregroundImage;

	private final TextureRegion green;
	private final TextureRegion yellow;
	private final TextureRegion red;

	private final TextureRegionDrawable textureRegionDrawable;

	private long shownAt;

	public HPBar() {
		Texture atlas = Assets.gfx(2, 158);

		TextureRegion empty = new TextureRegion(atlas, 0, 56, 40, 7);

		green = new TextureRegion(atlas, 2, 37, 36, 3);
		yellow = new TextureRegion(atlas, 2, 44, 36, 3);
		red = new TextureRegion(atlas, 2, 51, 36, 3);

		textureRegionDrawable = new TextureRegionDrawable(green);

		Image backgroundImage = new Image(empty);
		foregroundImage = new Image(green);
		foregroundImage.setPosition(2, 2);

		addActor(backgroundImage);
		addActor(foregroundImage);

		setSize(backgroundImage.getWidth(), backgroundImage.getHeight());
	}

	public void show(float value) {
		shownAt = System.currentTimeMillis();

		if(value < .15f)
			textureRegionDrawable.setRegion(red);
		else if(value < .35f)
			textureRegionDrawable.setRegion(yellow);
		else
			textureRegionDrawable.setRegion(green);

		int newWidth = Math.round(36.0f * value);

		textureRegionDrawable.getRegion().setRegionWidth(newWidth);
		foregroundImage.setDrawable(textureRegionDrawable);
		foregroundImage.setWidth(newWidth);
	}

	@Override
	public boolean isVisible() {
		return shownAt + VISIBLE_DURATION > System.currentTimeMillis();
	}
}
