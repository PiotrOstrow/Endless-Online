package com.github.piotrostrow.eo.ui.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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
		textFieldStyle.font = Assets.getFont("fonts/Arial/14.fnt");
		textFieldStyle.fontColor = Color.BLACK;
		textFieldStyle.cursor = Assets.getTextCursor(textFieldStyle.font, new Color(0, 0, 1f / 255f, 1f));
		textFieldStyle.selection = Assets.getTextCursor(textFieldStyle.font, new Color(1, 1, 1, 0.5f));

		textFieldStyle.background.setRightWidth(3);
		textFieldStyle.background.setLeftWidth(3);

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

		loginField.setPosition(140, 105);
		passwordField.setPosition(140, 69);

		addActor(loginField);
		addActor(passwordField);

		ClickListener clickListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				LoginWindow.this.setVisible(false);
				String username = loginField.getText().trim().length() == 0 ? "pepega131" : loginField.getText();
				String password = passwordField.getText().trim().length() == 0 ? "asdasd" : passwordField.getText();
				if(event.getTarget() == loginButton){
					Main.client.sendEncodedPacket(new LoginPacket(username, password));
				}

				loginField.setText("");
				passwordField.setText("");
			}
		};

		cancelButton.addListener(clickListener);
		loginButton.addListener(clickListener);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if(visible)
			getStage().setKeyboardFocus(loginField);
	}
}
