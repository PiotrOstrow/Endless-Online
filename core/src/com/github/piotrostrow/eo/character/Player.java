package com.github.piotrostrow.eo.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.github.piotrostrow.eo.graphics.PlayerTextureAtlas;
import com.github.piotrostrow.eo.graphics.PlayerTextureAtlasFactory;
import com.github.piotrostrow.eo.net.packets.login.WelcomeReplyPacket2;

public class Player extends CharacterEntity {

	private PlayerTextureAtlas textureAtlas;

	WelcomeReplyPacket2.Character character; // temp

	public Player(WelcomeReplyPacket2.Character character) {
		this.character = character;
		position.set(character.x, character.y);
		direction = character.direction;

		textureAtlas = PlayerTextureAtlasFactory.create(character);
	}

	public float getXOffset(int direction) {
		return 0;
	}

	public float getYOffset(int direction) {
		return -24;
	}

	@Override
	public int getID() {
		return character.playerID;
	}

	@Override
	public void update() {
		if(Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
			textureAtlas.dispose();
			textureAtlas = PlayerTextureAtlasFactory.create(character);
		}

		if(Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
			if(characterState == CharacterState.SIT_GROUND)
				characterState = CharacterState.IDLE;
			else if(characterState == CharacterState.IDLE)
				characterState = CharacterState.SIT_GROUND;
		}
	}

	@Override
	public void render(SpriteBatch batch, float x, float y) {
//		TextureRegion textureRegion = textureAtlas.get();
//		batch.draw(textureRegion, (Gdx.graphics.getWidth() - textureRegion.getRegionWidth()) / 2, 10);

		TextureRegion textureRegion = textureAtlas.get(characterState, direction, 0);

//		float xOffset = getXOffset(direction) + (32 - textureRegion.getRegionWidth() / 2f) +  (position.x - positionPrevious.x);
//		float yOffset = getYOffset(direction) + (position.y - positionPrevious.y);

//		System.out.println(position);
		float xOffset = getXOffset(direction) + (32 - textureRegion.getRegionWidth() / 2f);
		float yOffset = getYOffset(direction);

		batch.draw(textureRegion, x + xOffset, y + yOffset);
	}

	@Override
	public void dispose() {
		super.dispose();
		textureAtlas.dispose();
	}
}
