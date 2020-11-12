package com.github.piotrostrow.eo.character;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.github.piotrostrow.eo.graphics.PlayerTextureAtlas;
import com.github.piotrostrow.eo.graphics.PlayerTextureAtlasFactory;
import com.github.piotrostrow.eo.net.packets.login.WelcomeReplyPacket2;

public class Player extends CharacterEntity {

	private PlayerTextureAtlas textureAtlas;

	WelcomeReplyPacket2.Character character; // temp

	public Player(WelcomeReplyPacket2.Character character) {
		super(character.x, character.y, character.direction);
		this.character = character;
		this.textureAtlas = PlayerTextureAtlasFactory.create(character);
	}

	@Override
	protected TextureRegion getTextureRegion(CharacterState characterState, int direction, int frame) {
		return textureAtlas.get(characterState, direction, frame);
	}

	@Override
	public int getXOffset(int direction) {
		return 0;
	}

	@Override
	public int getYOffset(int direction) {
		return -24;
	}

	@Override
	public int getID() {
		return character.playerID;
	}

	@Override
	public void dispose() {
		super.dispose();
		textureAtlas.dispose();
	}
}
