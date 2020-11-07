package com.github.piotrostrow.eo.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.github.piotrostrow.eo.Main;
import com.github.piotrostrow.eo.assets.Assets;
import com.github.piotrostrow.eo.character.Player;
import com.github.piotrostrow.eo.map.Zone;
import com.github.piotrostrow.eo.net.ConnectionListener;
import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.packets.login.WelcomeReplyPacket1;
import com.github.piotrostrow.eo.net.packets.login.WelcomeReplyPacket2;

public class GameScreen implements Screen, ConnectionListener {

	private Zone currentZone;

	private Player player;

	public GameScreen(WelcomeReplyPacket1 welcomeReplyPacket1, WelcomeReplyPacket2 welcomeReplyPacket2) {
		currentZone = new Zone(Assets.getMap(welcomeReplyPacket1.getMapID()));
		Main.client.setConnectionListener(this);

		for(WelcomeReplyPacket2.Character character : welcomeReplyPacket2.characters){
			Player characterEntity = new Player(character);
			currentZone.addCharacter(characterEntity);

			if(character.playerID == welcomeReplyPacket1.getPlayerID())
				this.player = characterEntity;
		}
	}

	@Override
	public void show() {

	}

	private void input() {
		if(Gdx.input.isKeyJustPressed(Input.Keys.W))
			player.getPosition().y++;
		if(Gdx.input.isKeyJustPressed(Input.Keys.S))
			player.getPosition().y--;
		if(Gdx.input.isKeyJustPressed(Input.Keys.A))
			player.getPosition().x--;
		if(Gdx.input.isKeyJustPressed(Input.Keys.D))
			player.getPosition().x++;
	}

	private void update(){
		currentZone.update();
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

	@Override
	public void onConnect() {

	}

	@Override
	public void onDisconnect() {
		Gdx.app.postRunnable(() -> {
			dispose();
			Main.instance.setScreen(new MainMenuScreen());
		});
	}

	@Override
	public void handlePacket(Packet packet) {

	}
}
