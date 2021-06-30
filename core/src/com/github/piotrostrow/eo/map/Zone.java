package com.github.piotrostrow.eo.map;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Disposable;
import com.github.piotrostrow.eo.character.CharacterEntity;
import com.github.piotrostrow.eo.character.NonPlayerCharacter;
import com.github.piotrostrow.eo.character.PlayerCharacter;
import com.github.piotrostrow.eo.map.emf.EmfMap;

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

	public void removePlayer(PlayerCharacter playerCharacter){
		players.remove(playerCharacter);
		characters.remove(playerCharacter);
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
		return isBlocked(position.x, position.y);
	}

	public boolean isBlocked(int x, int y) {
		if(map.tileSpecs.blocked(x, y))
			return true;

		for(CharacterEntity character : characters)
			if(character.getPosition().x == x && character.getPosition().y == y)
				return true;

		return false;
	}

	public void update(){
		for(CharacterEntity character : characters)
			character.update();
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
		for(CharacterEntity character : characters)
			character.dispose();
	}
}
