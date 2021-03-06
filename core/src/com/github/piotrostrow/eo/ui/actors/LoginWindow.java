package com.github.piotrostrow.eo.ui.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.piotrostrow.eo.Main;
import com.github.piotrostrow.eo.assets.Assets;
import com.github.piotrostrow.eo.net.packets.login.LoginPacket;

public class LoginWindow extends WidgetGroup {

	private final TextField loginField;
	private final TextField passwordField;

	public LoginWindow() {
		Texture background = Assets.gfx(1, 102);
		setSize(background.getWidth(), background.getHeight());
		addActor(new Image(background));

		Texture textFieldTexture = Assets.getTexture("text_field.png");
		TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
		textFieldStyle.background = new TextureRegionDrawable(textFieldTexture);
		textFieldStyle.font = Assets.getFont("fonts/ms_ss/14.fnt");
		textFieldStyle.fontColor = Color.BLACK;
		textFieldStyle.cursor = Assets.getTextCursor(textFieldStyle.font, Color.valueOf("#7d5321"));
		textFieldStyle.selection = Assets.getTextSelector(textFieldStyle.font, Color.valueOf("#7d5321"));

		textFieldStyle.background.setRightWidth(3);
		textFieldStyle.background.setLeftWidth(6);
		textFieldStyle.background.setBottomHeight(1);

		Texture buttonsTexture = Assets.gfx(1, 115);
		TextureRegion[][] buttonsTR = TextureRegion.split(buttonsTexture, buttonsTexture.getWidth() / 2, buttonsTexture.getHeight() / 10);

		Button.ButtonStyle style = new Button.ButtonStyle();
		style.up = new TextureRegionDrawable(buttonsTR[2][0]);
		style.over = style.down = new TextureRegionDrawable(buttonsTR[2][1]);
		Button loginButton = new Button(style);

		style = new Button.ButtonStyle();
		style.up = new TextureRegionDrawable(buttonsTR[1][0]);
		style.over = style.down = new TextureRegionDrawable(buttonsTR[1][1]);
		Button cancelButton = new Button(style);

		cancelButton.setPosition(getWidth() - cancelButton.getWidth() - 19, 25);
		loginButton.setPosition(getWidth() - loginButton.getWidth() - cancelButton.getWidth() - 19, 25);

		addActor(loginButton);
		addActor(cancelButton);

		loginField = new TextField("", textFieldStyle);
		passwordField = new TextField("", textFieldStyle);

		loginField.setSize(textFieldTexture.getWidth(), textFieldTexture.getHeight());
		passwordField.setSize(textFieldTexture.getWidth(), textFieldTexture.getHeight());

		passwordField.setPasswordMode(true);
		passwordField.setPasswordCharacter('*');

		loginField.setPosition(142, 106);
		passwordField.setPosition(142, 70);

		addActor(loginField);
		addActor(passwordField);

		loginField.addListener(new InputListener() {
			@Override
			public boolean keyTyped(InputEvent event, char character) {
				if(character == '\r' || character == '\n') {
					getStage().setKeyboardFocus(passwordField);
					return true;
				}
				return false;
			}
		});

		passwordField.addListener(new InputListener() {
			@Override
			public boolean keyTyped(InputEvent event, char character) {
				if(character == '\r' || character == '\n') {
					onLogin();
					clearFields();
					return true;
				}
				return false;
			}
		});

		ClickListener clickListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(event.getTarget() == loginButton)
					onLogin();
				clearFields();
			}
		};

		cancelButton.addListener(clickListener);
		loginButton.addListener(clickListener);
	}

	private void onLogin() {
		LoginWindow.this.setVisible(false);
		getStage().setKeyboardFocus(null);

		String username = loginField.getText();
		String password = passwordField.getText();

		Main.client.sendEncodedPacket(new LoginPacket(username, password));
	}

	private void clearFields() {
		loginField.setText("");
		passwordField.setText("");
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if(visible)
			getStage().setKeyboardFocus(loginField);
	}
}
