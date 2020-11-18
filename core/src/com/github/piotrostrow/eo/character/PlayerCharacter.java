package com.github.piotrostrow.eo.character;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.github.piotrostrow.eo.graphics.PlayerTextureAtlas;
import com.github.piotrostrow.eo.graphics.PlayerTextureAtlasFactory;
import com.github.piotrostrow.eo.net.structs.PlayerData;

public class PlayerCharacter extends CharacterEntity {

	private PlayerTextureAtlas textureAtlas;

	private PlayerData playerData;

	public PlayerCharacter(PlayerData playerData) {
		super(playerData.x, playerData.y, playerData.direction);
		updateData(playerData);
	}

	public void updateData(PlayerData playerData) {
		this.playerData = playerData;

		// TODO: check if there is any difference
		this.textureAtlas = PlayerTextureAtlasFactory.create(playerData);

		setDirection(playerData.direction);
		setPosition(playerData.x, playerData.y);
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

	public int getPlayerID() {
		return playerData.playerID;
	}

	@Override
	public void dispose() {
		super.dispose();
		textureAtlas.dispose();
	}
}
