package com.github.piotrostrow.eo.ui.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.github.piotrostrow.eo.net.packets.login.LoginReplyPacket;

public class CharacterPanel extends WidgetGroup {

	public Image characterImage;
	public Button loginButton;
	public Button deleteButton;
	public Label nameLabel;

	private LoginReplyPacket.Character characterData;

	public void reset() {
		characterImage.setDrawable(null);
		setNameLabel("");
		characterData = null;
	}

	public void setCharacter(LoginReplyPacket.Character characterData) {
		this.characterData = characterData;
		setNameLabel(characterData.name);
	}

	private void setNameLabel(String name) {
		if (name.length() > 14) {
			String f = name.substring(0, 12) + "...";
			nameLabel.setText(f);
		} else {
			nameLabel.setText(name);
		}
	}

	public boolean hasCharacter() {
		return characterData != null;
	}

	public int getCharacterID() {
		if(characterData == null)
			return 0;
		return characterData.id;
	}
}