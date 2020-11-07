package com.github.piotrostrow.eo.map;

import com.badlogic.gdx.utils.Disposable;
import com.github.piotrostrow.eo.character.CharacterEntity;
import com.github.piotrostrow.eo.map.emf.EmfMap;
import com.github.piotrostrow.eo.map.emf.EmfMapRenderer;

import java.util.ArrayList;

public class Zone implements Disposable {

	private final EmfMapRenderer mapRenderer;

	private final ArrayList<CharacterEntity> characters = new ArrayList<>();

	public Zone(EmfMap map) {
		mapRenderer = new EmfMapRenderer(map, characters);
	}

	public void addCharacter(CharacterEntity character) {
		characters.add(character);
	}

	public void update(){
		for(CharacterEntity character : characters)
			character.update();
	}

	public void render() {
		mapRenderer.render();
	}

	@Override
	public void dispose() {
		mapRenderer.dispose();
	}
}
