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
import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.packets.account.AccountRequestPacket;

public class RegisterWindow extends WidgetGroup {

	private final TextField loginField;
	private final TextField passwordField;
	private final TextField confirmPasswordField;
	private final TextField	emailField;

	public RegisterWindow() {
		Texture background = Assets.getTexture("register_panel.png");
		setSize(background.getWidth(), background.getHeight());
		addActor(new Image(background));

		TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
		textFieldStyle.font = Assets.getFont("fonts/ms_ss/14.fnt");
		textFieldStyle.fontColor = Color.BLACK;
		textFieldStyle.cursor = Assets.getTextCursor(textFieldStyle.font, Color.valueOf("#7d5321"));
		textFieldStyle.selection = Assets.getTextSelector(textFieldStyle.font, Color.valueOf("#7d5321"));

		loginField = new TextField("", textFieldStyle);
		passwordField = new TextField("", textFieldStyle);
		confirmPasswordField = new TextField("", textFieldStyle);
		emailField = new TextField("", textFieldStyle);

		passwordField.setPasswordMode(true);
		confirmPasswordField.setPasswordMode(true);

		passwordField.setPasswordCharacter('*');
		confirmPasswordField.setPasswordCharacter('*');

		loginField.setPosition(174, 143);
		passwordField.setPosition(174, 118);
		confirmPasswordField.setPosition(174, 93);
		emailField.setPosition(174, 68);

		addActor(loginField);
		addActor(passwordField);
		addActor(confirmPasswordField);
		addActor(emailField);

		Texture buttonsTexture = Assets.gfx(1, 115);
		TextureRegion[][] buttonsTR = TextureRegion.split(buttonsTexture, buttonsTexture.getWidth() / 2, buttonsTexture.getHeight() / 10);

		Button.ButtonStyle style = new Button.ButtonStyle();
		style.up = new TextureRegionDrawable(buttonsTR[4][0]);
		style.over = style.down = new TextureRegionDrawable(buttonsTR[4][1]);
		Button okButton = new Button(style);

		style = new Button.ButtonStyle();
		style.up = new TextureRegionDrawable(buttonsTR[1][0]);
		style.over = style.down = new TextureRegionDrawable(buttonsTR[1][1]);
		Button cancelButton = new Button(style);

		addActor(okButton);
		addActor(cancelButton);

		okButton.setPosition(148, 21);
		cancelButton.setPosition(239, 21);

		okButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String username = loginField.getText().trim();
				String password = passwordField.getText().trim();
				String email = emailField.getText().trim();

				if(!confirmPasswordField.getText().equals(password)){
					// TODO: display dialog
					System.err.println("Passwords do not match");
					return;
				}

				if(username.isEmpty() || password.isEmpty() || email.isEmpty()){
					// TODO: display dialog
					System.err.println("Empty field");
					return;
				}

				Packet packet = new AccountRequestPacket(username);
				Main.client.sendEncodedPacket(packet);
			}
		});

		cancelButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				RegisterWindow.super.setVisible(false);
			}
		});
	}

	public String getUsername() {
		return loginField.getText();
	}

	public String getPassword() {
		return passwordField.getText();
	}

	public String getEmail() {
		return emailField.getText();
	}

	public void clearTextFields() {
		loginField.setText("");
		passwordField.setText("");
		confirmPasswordField.setText("");
		emailField.setText("");
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if(visible)
			getStage().setKeyboardFocus(loginField);
	}
}
