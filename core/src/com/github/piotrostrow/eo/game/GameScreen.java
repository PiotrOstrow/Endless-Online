package com.github.piotrostrow.eo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.GridPoint2;
import com.github.piotrostrow.eo.Main;
import com.github.piotrostrow.eo.assets.Assets;
import com.github.piotrostrow.eo.character.Player;
import com.github.piotrostrow.eo.map.Zone;
import com.github.piotrostrow.eo.map.emf.EmfMapRenderer;
import com.github.piotrostrow.eo.net.ConnectionListener;
import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.packets.login.WelcomeReplyPacket1;
import com.github.piotrostrow.eo.net.packets.login.WelcomeReplyPacket2;
import com.github.piotrostrow.eo.mainmenu.MainMenuScreen;

public class GameScreen implements Screen {

	private Zone currentZone;

	private Player player;
	private PlayerCharacterController characterController;

	public GameScreen(WelcomeReplyPacket1 welcomeReplyPacket1, WelcomeReplyPacket2 welcomeReplyPacket2) {
		Main.client.setConnectionListener(new PacketHandler(this));

		currentZone = new Zone(Assets.getMap(welcomeReplyPacket1.getMapID()));

		for(WelcomeReplyPacket2.Character character : welcomeReplyPacket2.characters){
			Player characterEntity = new Player(character);
			currentZone.addCharacter(characterEntity);

			if(character.playerID == welcomeReplyPacket1.getPlayerID())
				this.player = characterEntity;
		}

		// TODO: use input multiplexer when ui is implemented
		characterController = new PlayerCharacterController(this, player);
		Gdx.input.setInputProcessor(characterController);
	}

	@Override
	public void show() {

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
