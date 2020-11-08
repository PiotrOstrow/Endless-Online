package com.github.piotrostrow.eo.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.piotrostrow.eo.Main;
import com.github.piotrostrow.eo.game.GameScreen;
import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.ConnectionListener;
import com.github.piotrostrow.eo.net.PacketAction;
import com.github.piotrostrow.eo.net.PacketFamily;
import com.github.piotrostrow.eo.net.constants.LoginReply;
import com.github.piotrostrow.eo.net.packets.login.WelcomeMsgPacket;
import com.github.piotrostrow.eo.net.packets.login.WelcomeReplyPacket1;
import com.github.piotrostrow.eo.net.packets.login.WelcomeReplyPacket2;
import com.github.piotrostrow.eo.ui.stages.CharacterSelectStage;
import com.github.piotrostrow.eo.ui.stages.MainMenuStage;
import com.github.piotrostrow.eo.net.packets.login.LoginReplyPacket;

public class MainMenuScreen implements Screen, ConnectionListener {

	private Stage currentStage;

	private final MainMenuStage mainMenuStage;
	private final CharacterSelectStage characterSelectStage;

	private final MainMenuBackground background;

	private WelcomeReplyPacket1 welcomeReplyPacket1;

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
		setStage(mainMenuStage);
	}

	@Override
	public void handlePacket(Packet packet) {
		if (packet.equals(PacketFamily.PACKET_LOGIN, PacketAction.PACKET_REPLY)) {
			LoginReplyPacket loginReplyPacket = (LoginReplyPacket) packet;

			//TODO: handle errors
			switch (loginReplyPacket.getReplyCode()) {
				case LoginReply.LOGIN_OK:
					Gdx.app.postRunnable(() -> characterSelectStage.setCharacters(loginReplyPacket));
					setStage(characterSelectStage);
					break;
				default:
					System.err.println("Login error code: " + loginReplyPacket.getReplyCode());
					break;
			}
		} else if (packet.equals(PacketFamily.PACKET_WELCOME, PacketAction.PACKET_REPLY)) {
			if(packet instanceof WelcomeReplyPacket1){
				welcomeReplyPacket1 = (WelcomeReplyPacket1) packet;
				Main.client.sendEncodedPacket(new WelcomeMsgPacket(welcomeReplyPacket1.getCharacterID()));
			}else if(packet instanceof WelcomeReplyPacket2){
				final WelcomeReplyPacket2 welcomeReplyPacket2 = (WelcomeReplyPacket2) packet;
				// GameScreen constructor need openGL context
				Gdx.app.postRunnable(() -> Main.instance.setScreen(new GameScreen(welcomeReplyPacket1, welcomeReplyPacket2)));
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
