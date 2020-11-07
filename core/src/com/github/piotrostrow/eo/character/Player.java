package com.github.piotrostrow.eo.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

	@Override
	public void update() {
		if(Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
			textureAtlas.dispose();
			textureAtlas = PlayerTextureAtlasFactory.create(character);
		}
	}

	@Override
	public void render(SpriteBatch batch, float x, float y) {
//		TextureRegion textureRegion = textureAtlas.get();
//		batch.draw(textureRegion, (Gdx.graphics.getWidth() - textureRegion.getRegionWidth()) / 2, 10);
		batch.draw(textureAtlas.get(CharacterState.IDLE, direction, 0), x, y);
	}

	@Override
	public void dispose() {
		super.dispose();
		textureAtlas.dispose();
	}
}
