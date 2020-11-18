package com.github.piotrostrow.eo.game;

import com.badlogic.gdx.Gdx;
import com.github.piotrostrow.eo.Main;
import com.github.piotrostrow.eo.character.CharacterEntity;
import com.github.piotrostrow.eo.character.NonPlayerCharacter;
import com.github.piotrostrow.eo.character.PlayerCharacter;
import com.github.piotrostrow.eo.mainmenu.MainMenuScreen;
import com.github.piotrostrow.eo.net.ConnectionListener;
import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.PacketAction;
import com.github.piotrostrow.eo.net.PacketFamily;
import com.github.piotrostrow.eo.net.structs.NpcData;
import com.github.piotrostrow.eo.net.structs.PlayerData;

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

	private void handleNpcPlayerPacket(Packet packet) {
		int c = 0;
		while(packet.readUnencodedByte() == 0xFF)
			c++;
		packet.skip(-1);

		NonPlayerCharacter npc = game.getZone().getNPC(packet.readEncodedByte());

		if(npc != null) {
			switch (c) {
				case 0: // movement
					int x = packet.readEncodedByte();
					int y = packet.readEncodedByte();
					int direction = packet.readEncodedByte();
					npc.move(direction, x, y);
					break;
			}
		}
	}

	private void handleRefreshReplyPacket(Packet packet) {
		int playerCount = packet.readEncodedByte();
		packet.skip(1); // unencoded 0xFF

		System.out.println("Refreshed " + playerCount + " playersw");
		for(int i = 0; i < playerCount; i++) {
			PlayerData playerData = new PlayerData(packet);

			PlayerCharacter player = game.getZone().getPlayer(playerData.playerID);
			if(player != null)
				player.updateData(playerData);
			else
				game.getZone().addPlayer(new PlayerCharacter(playerData));
		}

		while(packet.readUnencodedByte() != 0xFF){
			System.out.println("Refreshed NPC");
			packet.skip(-1);
			NpcData npcData = new NpcData(packet);

			NonPlayerCharacter npc = game.getZone().getNPC(npcData.index);
			if(npc != null)
				npc.updateData(npcData);
			else
				game.getZone().addNpc(new NonPlayerCharacter(npcData));
		}
	}

	@Override
	public void handlePacket(Packet packet) {
		if(packet.equals(PacketFamily.PACKET_WALK, PacketAction.PACKET_PLAYER)) {
			int playerID = packet.readEncodedShort();
			int direction = packet.readEncodedByte();
			int x = packet.readEncodedByte();
			int y = packet.readEncodedByte();
			PlayerCharacter player = game.getZone().getPlayer(playerID);
			if(player != null)
				player.move(direction, x, y);
		} else if(packet.equals(PacketFamily.PACKET_FACE, PacketAction.PACKET_PLAYER)) {
			int playerID = packet.readEncodedShort();
			int direction = packet.readEncodedByte();
			PlayerCharacter player = game.getZone().getPlayer(playerID);
			if(player != null)
				player.setDirection(direction);
		} else if(packet.equals(PacketFamily.PACKET_NPC, PacketAction.PACKET_PLAYER)) {
			handleNpcPlayerPacket(packet);
		} else if(packet.equals(PacketFamily.PACKET_REFRESH, PacketAction.PACKET_REPLY)){
			handleRefreshReplyPacket(packet);
		} else if(packet.equals(PacketFamily.PACKET_APPEAR, PacketAction.PACKET_REPLY)) {
			packet.skip(2);
			NpcData npcData = new NpcData(packet);
			NonPlayerCharacter npc = game.getZone().getNPC(npcData.index);
			if(npc != null)
				npc.updateData(npcData);
			else
				game.getZone().addNpc(new NonPlayerCharacter(npcData));
		}
	}
}
