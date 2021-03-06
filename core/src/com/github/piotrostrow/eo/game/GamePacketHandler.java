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
		Main.client.registerPacketHandler(PacketFamily.PACKET_ITEM, PacketAction.PACKET_ADD, this::handleItemAddPacket);
		Main.client.registerPacketHandler(PacketFamily.PACKET_DOOR, PacketAction.PACKET_OPEN, this::handleDoorOpenPacket);
		Main.client.registerPacketHandler(PacketFamily.PACKET_NPC, PacketAction.PACKET_REPLY, this::handleNpcReplyPacket);
		Main.client.registerPacketHandler(PacketFamily.PACKET_NPC, PacketAction.PACKET_SPEC, this::handleNpcSpecPacket);
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

	/**
	 * Packet sent by server when an NPC is killed, or removed from view (?)
	 */
	private void handleNpcSpecPacket(Packet packet) {
		// Packet of this size is sent when removing the NPC from view, but the NPCs disappear anyway and i don't
		// remember why. Should also be of size 5, but the method returns size of the whole packet including headers,
		// TODO: make new method for size without headers
		if (packet.getSize() <= 7) {
			System.out.println("NpcSpec packet with size of 5 or less not handled");
		} else {
			// TODO: has a spellID first if is hit by spell

			int killerID = packet.readEncodedShort();
			int killerDirection = packet.readEncodedByte();
			int npcID = packet.readEncodedShort();
			int groupID = packet.readEncodedShort();
			int dropID = packet.readEncodedShort();
			int x = packet.readEncodedByte();
			int y = packet.readEncodedByte();
			int dropAmount = packet.readEncodedInt();
			int hitAmount = packet.readEncodedThreeByteInt();

			if (packet.remaining() >= 4) {
				int xp = packet.readEncodedInt();

				if (packet.remaining() > 0) {
					int level = packet.readEncodedShort();
					int statPoints = packet.readEncodedShort();
					int skillPoints = packet.readEncodedShort();
					int maxHP = packet.readEncodedShort();
					int maxTP = packet.readEncodedShort();
					int maxSP = packet.readEncodedShort();
				}
			}

			CharacterEntity npc = game.getZone().getNPC(npcID);
			if (npc != null) {
				npc.die(hitAmount);
			}
		}
	}

	/**
	 * Sent when an NPC gets hit by attack or spell
	 */
	private void handleNpcReplyPacket(Packet packet) {
		// has a spellID first if is hit by spell?
		int playerID = packet.readEncodedShort();
		int direction = packet.readEncodedByte();
		int npcID = packet.readEncodedShort();
		int amount = packet.readEncodedThreeByteInt();
		int npcHealthPercent = packet.readEncodedShort();

		CharacterEntity npc = game.getZone().getNPC(npcID);
		if (npc != null)
			npc.hit(amount, npcHealthPercent / 100.0f);

	}

	private void handleDoorOpenPacket(Packet packet) {
		int x = packet.readEncodedByte();
		int y = packet.readEncodedShort();
		game.getZone().openDoor(x, y);
	}

	private void handleItemAddPacket(Packet packet) {
		int id = packet.readEncodedShort();
		int uid = packet.readEncodedShort();
		int amount = packet.readEncodedThreeByteInt();
		int x = packet.readEncodedByte();
		int y = packet.readEncodedByte();

		game.getZone().addItem(new MapItem(id, uid, amount, x, y));
	}

	private void handleTalkPlayerPacket(Packet packet) {
		int playerID = packet.readEncodedShort();
		String message = packet.readEndString();

		CharacterEntity character = game.getZone().getPlayer(playerID);

		if (character != null) {
			game.getGameUI().getChatWindow().addMessage(character.getName(), message);
			character.getChatBubble().show(message);
		} else {
			System.err.println("Cannot find player with ID " + playerID + " received in TalkPlayer packet");
		}
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
					int targetHealthPercent = packet.readEncodedThreeByteInt();

					npc.setDirection(direction);
					npc.attack();

					PlayerCharacter target = game.getZone().getPlayer(targetPlayerID);

					if (target != null)
						target.hit(amount, targetHealthPercent / 100.0f);

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
			if (player != null) {
				// If updating own player, i suppose you should check whether or not the position is the same before
				// and after. If not then stop the players character by locking the controller for a bit, otherwise weird
				// things happen when spamming buttons, produces bugs most likely due to server not refreshing often enough
				if (player == game.getOwnCharacter() && (player.getPosition().x != playerData.x || player.getPosition().y != playerData.y))
					game.getCharacterController().lock(500);

				player.updateData(playerData);
			} else {
				game.getZone().addPlayer(new PlayerCharacter(playerData));
			}
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
		if (npc != null) {
			npc.updateData(npcData);
			npc.setAlive();
		} else {
			game.getZone().addNpc(new NonPlayerCharacter(npcData));
		}
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
