package com.github.piotrostrow.eo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.piotrostrow.eo.Main;
import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.ConnectionListener;
import com.github.piotrostrow.eo.net.constants.LoginReply;
import com.github.piotrostrow.eo.ui.stages.CharacterSelectStage;
import com.github.piotrostrow.eo.ui.stages.MainMenuStage;
import com.github.piotrostrow.eo.net.packets.login.LoginReplyPacket;

public class MainMenuScreen implements Screen, ConnectionListener {

	private Stage currentStage;

	private final MainMenuStage mainMenuStage;
	private final CharacterSelectStage characterSelectStage;

	private final MainMenuBackground background;

	public MainMenuScreen() {
		background = new MainMenuBackground();
		mainMenuStage = new MainMenuStage();
		characterSelectStage = new CharacterSelectStage();

		Main.client.setConnectionListener(this);

		setStage(mainMenuStage);
	}

	@Override
	public void onConnect() {
		mainMenuStage.connected();
	}

	@Override
	public void onDisconnect() {
		mainMenuStage.disconnected();
	}

	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof LoginReplyPacket){
			LoginReplyPacket loginReplyPacket = (LoginReplyPacket) packet;
			if(loginReplyPacket.getReplyCode() == LoginReply.LOGIN_OK){
				LoginReplyPacket.Character[] characters = loginReplyPacket.getCharacters();
				for(int i = 0; i < characters.length; i++)
					characterSelectStage.getCharacterPanel(i).setName(characters[i].name);
				setStage(characterSelectStage);
			}else{
				System.err.println("Login reply error code: " + loginReplyPacket.getReplyCode());
			}
		}
	}

	private void setStage(Stage stage) {
		this.currentStage = stage;
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		background.update();
		currentStage.act(Gdx.graphics.getDeltaTime());

		background.render();
		currentStage.draw();
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
		dispose();
	}

	@Override
	public void dispose() {
		background.dispose();
	}
}
