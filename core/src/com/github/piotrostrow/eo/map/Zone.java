package com.github.piotrostrow.eo.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Disposable;
import com.github.piotrostrow.eo.character.CharacterEntity;
import com.github.piotrostrow.eo.character.NonPlayerCharacter;
import com.github.piotrostrow.eo.character.PlayerCharacter;
import com.github.piotrostrow.eo.map.emf.EmfMap;
import com.github.piotrostrow.eo.map.emf.EmfMapRenderer;

import java.util.ArrayList;

public class Zone implements Disposable {

	private final EmfMap map;
	private final EmfMapRenderer mapRenderer;

	// separate list, because NPC IDs and player IDs can overlap
	private final ArrayList<PlayerCharacter> players = new ArrayList<>();
	private final ArrayList<NonPlayerCharacter> npcs = new ArrayList<>();

	// list for rendering
	private final ArrayList<CharacterEntity> characters = new ArrayList<>();

	public Zone(EmfMap map) {
		this.map = map;
		this.mapRenderer = new EmfMapRenderer(map, characters);
	}

	public void addNpc(NonPlayerCharacter npc) {
		npcs.add(npc);
		characters.add(npc);
	}

	public void addPlayer(PlayerCharacter player) {
		players.add(player);
		characters.add(player);
	}

	public PlayerCharacter getPlayer(int playerID) {
		for(PlayerCharacter player : players)
			if(player.getPlayerID() == playerID)
				return player;
		return null;
	}

	public NonPlayerCharacter getNPC(int index) {
		for(NonPlayerCharacter npc : npcs)
			if(npc.getIndex() == index)
				return npc;
		return null;
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

	public EmfMapRenderer getMapRenderer() {
		return mapRenderer;
	}

	@Override
	public void dispose() {
		mapRenderer.dispose();
	}
}
