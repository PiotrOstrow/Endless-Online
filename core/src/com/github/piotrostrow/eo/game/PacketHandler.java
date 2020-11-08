package com.github.piotrostrow.eo.game;

import com.badlogic.gdx.Gdx;
import com.github.piotrostrow.eo.Main;
import com.github.piotrostrow.eo.character.CharacterEntity;
import com.github.piotrostrow.eo.mainmenu.MainMenuScreen;
import com.github.piotrostrow.eo.net.ConnectionListener;
import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.PacketAction;
import com.github.piotrostrow.eo.net.PacketFamily;

public class PacketHandler implements ConnectionListener {

	private final GameScreen game;

	PacketHandler(GameScreen game) {
		this.game = game;
	}

	@Override
	public void onConnect() {

	}

	@Override
	public void onDisconnect() {
		Gdx.app.postRunnable(() -> {
			game.dispose();
			Main.instance.setScreen(new MainMenuScreen());
		});
	}

	@Override
	public void handlePacket(Packet packet) {
		if(packet.equals(PacketFamily.PACKET_WALK, PacketAction.PACKET_PLAYER)) {
			int playerID = packet.readEncodedShort();
			int direction = packet.readEncodedByte();
			int x = packet.readEncodedByte();
			int y = packet.readEncodedByte();
			CharacterEntity character = game.getZone().getCharacter(playerID);
			if(character != null)
				character.move(direction, x, y);
		}
	}
}
