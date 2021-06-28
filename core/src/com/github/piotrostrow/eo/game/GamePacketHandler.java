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
import com.github.piotrostrow.eo.net.packets.warp.WarpAcceptPacket;
import com.github.piotrostrow.eo.net.structs.NpcData;
import com.github.piotrostrow.eo.net.structs.PlayerData;

public class GamePacketHandler implements ConnectionListener {

	private final GameScreen game;

	GamePacketHandler(GameScreen game) {
		this.game = game;

		Main.client.registerPacketHandler(PacketFamily.PACKET_NPC, PacketAction.PACKET_PLAYER, this::handleNpcPlayerPacket);
		Main.client.registerPacketHandler(PacketFamily.PACKET_REFRESH, PacketAction.PACKET_REPLY, this::handleRefreshReplyPacket);
		Main.client.registerPacketHandler(PacketFamily.PACKET_WALK, PacketAction.PACKET_PLAYER, this::handleWalkPlayerPacket);
		Main.client.registerPacketHandler(PacketFamily.PACKET_FACE, PacketAction.PACKET_PLAYER, this::handleFacePlayerPacket);
		Main.client.registerPacketHandler(PacketFamily.PACKET_APPEAR, PacketAction.PACKET_REPLY, this::handleAppearReplyPacket);
		Main.client.registerPacketHandler(PacketFamily.PACKET_ATTACK, PacketAction.PACKET_PLAYER, this::handleAttackPlayerPacket);
		Main.client.registerPacketHandler(PacketFamily.PACKET_WARP, PacketAction.PACKET_REQUEST, this::handleWarpRequestPacket);
		Main.client.registerPacketHandler(PacketFamily.PACKET_TALK, PacketAction.PACKET_PLAYER, this::handleTalkPlayerPacket);
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

	private void handleTalkPlayerPacket(Packet packet) {
		int playerID = packet.readEncodedShort();
		String message = packet.readEndString();

		CharacterEntity character = game.getZone().getPlayer(playerID);

		if(character != null)
			game.getGameUI().getChatWindow().addMessage(character.getName(), message);
		else
			System.err.println("Cannot find player with ID " + playerID + " received in TalkPlayer packet");
	}

	private void handleNpcPlayerPacket(Packet packet) {

		int c = 0;
		while (packet.readUnencodedByte() == 0xFF)
			c++;
		packet.skip(-1);

		NonPlayerCharacter npc = game.getZone().getNPC(packet.readEncodedByte());

		if (npc != null) {
			switch (c) {
				case 0: // movement
					int x = packet.readEncodedByte();
					int y = packet.readEncodedByte();
					int direction = packet.readEncodedByte();
					npc.move(direction, x, y);
					break;
				case 1: // attack
					int isAlive = packet.readEncodedByte();
					direction = packet.readEncodedByte();
					int targetPlayerID = packet.readEncodedShort();
					int amount = packet.readEncodedThreeByteInt();
					int targetHealthPrc = packet.readEncodedThreeByteInt();

					npc.setDirection(direction);
					npc.attack();
					break;
			}
		}
	}

	private void handleRefreshReplyPacket(Packet packet) {
		int playerCount = packet.readEncodedByte();
		packet.skip(1); // unencoded 0xFF

		for (int i = 0; i < playerCount; i++) {
			PlayerData playerData = new PlayerData(packet);

			PlayerCharacter player = game.getZone().getPlayer(playerData.playerID);
			if (player != null)
				player.updateData(playerData);
			else
				game.getZone().addPlayer(new PlayerCharacter(playerData));
		}

		while (!packet.peekAndSkipUnencodedByte()) {
			NpcData npcData = new NpcData(packet);

			NonPlayerCharacter npc = game.getZone().getNPC(npcData.index);
			if (npc != null)
				npc.updateData(npcData);
			else
				game.getZone().addNpc(new NonPlayerCharacter(npcData));
		}
	}

	private void handleWalkPlayerPacket(Packet packet) {
		int playerID = packet.readEncodedShort();
		int direction = packet.readEncodedByte();
		int x = packet.readEncodedByte();
		int y = packet.readEncodedByte();
		PlayerCharacter player = game.getZone().getPlayer(playerID);
		if (player != null)
			player.move(direction, x, y);
	}

	private void handleFacePlayerPacket(Packet packet) {
		int playerID = packet.readEncodedShort();
		int direction = packet.readEncodedByte();
		PlayerCharacter player = game.getZone().getPlayer(playerID);
		if (player != null)
			player.setDirection(direction);

	}

	private void handleAppearReplyPacket(Packet packet) {
		packet.skip(2);
		NpcData npcData = new NpcData(packet);
		NonPlayerCharacter npc = game.getZone().getNPC(npcData.index);
		if (npc != null)
			npc.updateData(npcData);
		else
			game.getZone().addNpc(new NonPlayerCharacter(npcData));
	}

	private void handleAttackPlayerPacket(Packet packet) {
		int playerID = packet.readEncodedShort();
		int direction = packet.readEncodedByte();

		PlayerCharacter player = game.getZone().getPlayer(playerID);
		if (player != null) {
			player.setDirection(direction);
			player.attack();
		}
	}

	private void handleWarpRequestPacket(Packet packet) {
		game.getCharacterController().lock(true);
		int warpType = packet.readEncodedByte();
		int mapID = 0;
		mapID = packet.readEncodedShort();
		if (warpType == 1) { // same map
			// TODO: map gets disposed of here too, maybe reuse zone object?
			int x = packet.readEncodedByte();
			int y = packet.readEncodedByte();
		} else {
			// TODO: check if map up to date
		}
		Packet response = new WarpAcceptPacket(mapID);
		Main.client.sendEncodedPacket(response);
	}
}
