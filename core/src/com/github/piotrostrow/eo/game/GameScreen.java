package com.github.piotrostrow.eo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.github.piotrostrow.eo.Main;
import com.github.piotrostrow.eo.assets.Assets;
import com.github.piotrostrow.eo.character.NonPlayerCharacter;
import com.github.piotrostrow.eo.character.PlayerCharacter;
import com.github.piotrostrow.eo.map.Zone;
import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.packets.login.WelcomeReplyPacket1;
import com.github.piotrostrow.eo.net.packets.login.WelcomeReplyPacket2;
import com.github.piotrostrow.eo.net.structs.NpcData;
import com.github.piotrostrow.eo.net.structs.PlayerData;

public class GameScreen implements Screen {

	private Zone currentZone;

	private PlayerCharacter player;

	private PlayerCharacterController characterController;

	public GameScreen(WelcomeReplyPacket1 welcomeReplyPacket1, WelcomeReplyPacket2 welcomeReplyPacket2) {
		Main.client.setConnectionListener(new PacketHandler(this));

		currentZone = new Zone(Assets.getMap(welcomeReplyPacket1.getMapID()));

		for(PlayerData character : welcomeReplyPacket2.characters){
			PlayerCharacter characterEntity = new PlayerCharacter(character);
			currentZone.addPlayer(characterEntity);

			if(character.playerID == welcomeReplyPacket1.getPlayerID())
				this.player = characterEntity;
		}

		for(NpcData npcData : welcomeReplyPacket2.npcs) {
			NonPlayerCharacter npc = new NonPlayerCharacter(npcData);
			currentZone.addNpc(npc);
		}

		// TODO: use input multiplexer when ui is implemented
		characterController = new PlayerCharacterController(this, player);
		Gdx.input.setInputProcessor(characterController);
	}

	protected void warpAgree(Packet packet) {
		int hardcodedByte = packet.readEncodedByte();
		int mapID = packet.readEncodedShort();
		int animation = packet.readEncodedByte();
		int characterCount = packet.readEncodedByte();

		// 0xFF hardcoded byte
		packet.skip(1);

		Zone zone = new Zone(Assets.getMap(mapID));

		for (int i = 0; i < characterCount; i++) {
			PlayerData playerData = new PlayerData(packet);

			if(playerData.playerID == player.getPlayerID()){
				player.updateData(playerData);
				zone.addPlayer(player);
				// remove own player object from current zone before swapping so the texture doesn't get disposed
				currentZone.removePlayer(player);
			} else {
				PlayerCharacter characterEntity = new PlayerCharacter(playerData);
				zone.addPlayer(characterEntity);
			}
		}

		while (packet.readUnencodedByte() != 0xFF) {
			packet.skip(-1);
			NpcData npcData = new NpcData(packet);
			zone.addNpc(new NonPlayerCharacter(npcData));
		}

		currentZone.dispose();
		currentZone = zone;
		characterController.lock(false);
	}

	private void input() {
	}

	private void update(){
		currentZone.update();
		characterController.update();

		// set camera position on character
		Camera camera = currentZone.getMapRenderer().camera;
		camera.position.x = player.getRenderingGridPosition().x * 32 - player.getRenderingGridPosition().y * 32 + player.getMovePositionOffset().x;
		camera.position.y = -(player.getRenderingGridPosition().y * 16 + player.getRenderingGridPosition().x * 16) + player.getMovePositionOffset().y;
		camera.update();
	}

	@Override
	public void render(float delta) {
		input();
		update();

		currentZone.render();
	}

	protected PlayerCharacterController getCharacterController() {
		return characterController;
	}

	@Override
	public void show() {

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		if(currentZone != null)
			currentZone.dispose();
	}

	Zone getZone() {
		return currentZone;
	}
}
