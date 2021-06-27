package com.github.piotrostrow.eo.ui.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.piotrostrow.eo.assets.Assets;

public class ChatBar extends WidgetGroup {

	private TextField chatTextField;
	private Image chatTextFieldLeft, chatTextFieldRight;

	public ChatBar() {
		Texture chatBarTexture = Assets.gfx(2, 130);

		Texture chatBarSides = Assets.gfx(2, 131);
		TextureRegion[][] chatBarSidesTR = TextureRegion.split(chatBarSides, chatBarSides.getWidth(), chatBarSides.getHeight() / 8);

		TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
		textFieldStyle.font = Assets.getFont("fonts/ms_ss/14.fnt");
		textFieldStyle.fontColor = Color.BLACK;
		textFieldStyle.cursor = Assets.getTextCursor(textFieldStyle.font, Color.valueOf("#7d5321"));
		textFieldStyle.selection = Assets.getTextSelector(textFieldStyle.font, Color.valueOf("#7d5321"));
		textFieldStyle.background = new TextureRegionDrawable(new TextureRegion(chatBarTexture, chatBarTexture.getWidth(), chatBarTexture.getHeight()));
		textFieldStyle.background.setLeftWidth(4);

		chatTextField = new TextField("", textFieldStyle) {

		};
		chatTextField.setWidth(chatBarTexture.getWidth());
		chatTextField.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if(focused) {
					chatTextField.setColor(1, 1, 1, 1);
					chatTextFieldLeft.setColor(1, 1, 1, 1);
					chatTextFieldRight.setColor(1, 1, 1, 1);
				} else {
					chatTextField.setColor(1, 1, 1, 0.5f);
					chatTextFieldLeft.setColor(1, 1, 1, 0.5f);
					chatTextFieldRight.setColor(1, 1, 1, 0.5f);
				}
			}
		});

		addActor(chatTextField);
		chatTextField.setPosition(Gdx.graphics.getWidth() / 2 - chatTextField.getWidth() / 2, 12);

		for (TextureRegion[] textureRegions : chatBarSidesTR)
			textureRegions[0].setRegionHeight(textureRegions[0].getRegionHeight() - 3);

		chatTextFieldLeft = new Image(chatBarSidesTR[1][0]);

		TextureRegion rightTR = new TextureRegion(chatBarSidesTR[0][0]);
		rightTR.flip(true, false);
		chatTextFieldRight = new Image(rightTR);

		addActor(chatTextFieldLeft);
		addActor(chatTextFieldRight);

		chatTextFieldLeft.setPosition(chatTextField.getX() - chatBarSidesTR[0][0].getRegionWidth(), chatTextField.getY());
		chatTextFieldRight.setPosition(chatTextField.getX() + chatTextField.getWidth(), chatTextField.getY());

		chatTextField.setColor(1, 1, 1, 0.5f);
		chatTextFieldLeft.setColor(1, 1, 1, 0.5f);
		chatTextFieldRight.setColor(1, 1, 1, 0.5f);

		ClickListener sideClickListener = new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				getStage().setKeyboardFocus(chatTextField);
			}
		};

		chatTextFieldLeft.addListener(sideClickListener);
		chatTextFieldRight.addListener(sideClickListener);
	}

	public void onEnterPressed() {
		if (getStage().getKeyboardFocus() == chatTextField) {
			getStage().setKeyboardFocus(null);
			chatTextField.setText("");
		} else {
			getStage().setKeyboardFocus(chatTextField);
		}
	}

	public void onEscapePressed() {
		if(getStage().getKeyboardFocus() == chatTextField)
			getStage().setKeyboardFocus(null);
	}
}
