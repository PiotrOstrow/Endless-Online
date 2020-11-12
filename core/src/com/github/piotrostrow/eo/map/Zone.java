package com.github.piotrostrow.eo.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Disposable;
import com.github.piotrostrow.eo.character.CharacterEntity;
import com.github.piotrostrow.eo.map.emf.EmfMap;
import com.github.piotrostrow.eo.map.emf.EmfMapRenderer;

import java.util.ArrayList;

public class Zone implements Disposable {

	private final EmfMap map;
	private final EmfMapRenderer mapRenderer;

	private final ArrayList<CharacterEntity> characters = new ArrayList<>();

	public Zone(EmfMap map) {
		this.map = map;
		this.mapRenderer = new EmfMapRenderer(map, characters);
	}

	public CharacterEntity getCharacter(int playerID) {
		for(CharacterEntity character : characters)
			if(character.getID() == playerID)
				return character;
		return null;
	}

	public void addCharacter(CharacterEntity character) {
		characters.add(character);
	}

	public boolean isBlocked(GridPoint2 position) {
		if(position.x < 0 || position.y < 0 || position.x > map.getWidth() || position.y > map.getHeight())
			return true;
		if(map.tileSpecs.blocked(position.x, position.y))
			return true;

		for(CharacterEntity character : characters)
			if(character.getPosition().equals(position))
				return true;

		return false;
	}

	public void update(){
		for(CharacterEntity character : characters)
			character.update();

		//debugging
		if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			mapRenderer.camera.position.add(-Gdx.input.getDeltaX(), Gdx.input.getDeltaY(), 0);
			mapRenderer.camera.update();
		}
	}

	public void render() {
		mapRenderer.render();
	}

	@Override
	public void dispose() {
		mapRenderer.dispose();
	}
}
