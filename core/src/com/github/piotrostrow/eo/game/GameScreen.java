package com.github.piotrostrow.eo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.github.piotrostrow.eo.Main;
import com.github.piotrostrow.eo.assets.Assets;
import com.github.piotrostrow.eo.character.NonPlayerCharacter;
import com.github.piotrostrow.eo.character.PlayerCharacter;
import com.github.piotrostrow.eo.map.Zone;
import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.PacketAction;
import com.github.piotrostrow.eo.net.PacketFamily;
import com.github.piotrostrow.eo.net.packets.warp.DoorOpenPacket;
import com.github.piotrostrow.eo.net.structs.NpcData;
import com.github.piotrostrow.eo.net.structs.PlayerData;

public class GameScreen implements Screen {

	private Zone currentZone;

	private final PlayerCharacter player;
	private final Inventory inventory;

	private final PlayerCharacterController characterController;

	private GameUI gameUI;

	public GameScreen(Zone zone, PlayerCharacter player, Inventory inventory) {
		this.currentZone = zone;
		this.player = player;
		this.inventory = inventory;

		Main.client.setConnectionListener(new GamePacketHandler(this));
		Main.client.registerPacketHandler(PacketFamily.PACKET_WARP, PacketAction.PACKET_AGREE, this::handleWarpAgreePacket);

		// TODO: use input multiplexer when ui is implemented
		characterController = new PlayerCharacterController(this, player);

		gameUI = new GameUI(this);

		InputMultiplexer inputMultiplexer = new InputMultiplexer(gameUI, characterController);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	protected void handleWarpAgreePacket(Packet packet) {
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

		while (!packet.peekAndSkipUnencodedByte()) {
			NpcData npcData = new NpcData(packet);
			zone.addNpc(new NonPlayerCharacter(npcData));
		}

		currentZone.dispose();
		currentZone = zone;
		characterController.lock(false);
	}

	public PlayerCharacter getOwnCharacter() {
		return player;
	}

	private void input() {
		if(Gdx.input.isKeyJustPressed(Input.Keys.F5)) { // for hotswap
			gameUI = new GameUI(this);
			Gdx.input.setInputProcessor(new InputMultiplexer(gameUI, characterController));
		}

		// calculate map grid position of mouse cursor
		Camera camera = currentZone.getMapRenderer().camera;
		float screenX = camera.position.x + Gdx.input.getX() - camera.viewportWidth / 2;
		float screenY = camera.position.y - Gdx.input.getY() + camera.viewportHeight / 2 - 16;
		int mouseMapX = (int)(-(screenY / 16 + -(screenX / 32)) / 2);
		int mouseMapY = (int)-(((screenX / 32 + screenY / 16) / 2) - 1);

		// update map cursor position TODO: and cursor type
		currentZone.getMapRenderer().getMapCursor().getPosition().set(mouseMapX, -mouseMapY);

		// on click when no ui element is hit - open doors, pickup items, go to mouse coords
		if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && gameUI.hit(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), false) == null) {
			if (currentZone.hasDoor(mouseMapX, mouseMapY) && !currentZone.isDoorOpen(mouseMapX, mouseMapY)) {
				Main.client.sendEncodedPacket(new DoorOpenPacket(mouseMapX, mouseMapY));
			} else {
				characterController.goTo(mouseMapX, mouseMapY);
				currentZone.getMapRenderer().getMapCursor().clickAnimation(mouseMapX, -mouseMapY);
			}
		}
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
		gameUI.act(Gdx.graphics.getDeltaTime());

		currentZone.render();
		gameUI.draw();
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
		gameUI.dispose();
	}

	Zone getZone() {
		return currentZone;
	}

	public GameUI getGameUI() {
		return gameUI;
	}

	public Inventory getInventory() {
		return inventory;
	}
}
