package com.github.piotrostrow.eo.ui.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.github.piotrostrow.eo.assets.Assets;

public class StatusBar extends WidgetGroup {

	private final TextureRegion foreground;
	private final Image foregroundImage;
	private final Image dropdownImage;

	private final Label label;

	private long clickedAt;

	public StatusBar(int textureRegionIndex) {
		Texture atlas = Assets.gfx(2, 158);

		TextureRegion background = new TextureRegion(atlas, 110 * textureRegionIndex, 0, 110, 14);
		Image backgroundImage = new Image(background);

		foreground = new TextureRegion(atlas, 110 * textureRegionIndex, 14, 110, 14);
		foregroundImage = new Image(foreground);

		TextureRegion dropdownTextureRegion = new TextureRegion(atlas, 220, 29, 110, 21);
		dropdownImage = new Image(dropdownTextureRegion);
		dropdownImage.setPosition(0, -17);

		Label.LabelStyle labelStyle = new Label.LabelStyle();
		labelStyle.font = Assets.getFont("fonts/ms_ss/14.fnt");
		labelStyle.fontColor = Color.valueOf("#c8c8c8");
		label = new Label("10/10", labelStyle);
		label.setPosition(4, -13);

		addActor(backgroundImage);
		addActor(foregroundImage);
		addActor(dropdownImage);
		addActor(label);

		setSize(background.getRegionWidth(), background.getRegionHeight());

		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setDropdownVisible(!isDropdownVisible());

				if(isDropdownVisible())
					clickedAt = System.currentTimeMillis();
			}
		});
	}

	private void setDropdownVisible(boolean visible) {
		dropdownImage.setVisible(visible);
		label.setVisible(visible);
	}

	private boolean isDropdownVisible() {
		return dropdownImage.isVisible();
	}

	@Override
	public float getPrefWidth() {
		return getWidth();
	}

	@Override
	public float getPrefHeight() {
		return getHeight();
	}

	public void setValue(float value) {
		foreground.setRegionWidth(24 + Math.round(79.0f * value));
		foregroundImage.setSize(foreground.getRegionWidth(), foreground.getRegionHeight());
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		if(clickedAt + 5000 < System.currentTimeMillis())
			setDropdownVisible(false);
	}
}
