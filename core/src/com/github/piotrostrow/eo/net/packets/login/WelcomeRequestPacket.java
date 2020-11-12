package com.github.piotrostrow.eo.net.packets.login;

import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.PacketAction;
import com.github.piotrostrow.eo.net.PacketFamily;

/**
 * Packet sent when clicking connect on one of the characters in character select screen
 */
public class WelcomeRequestPacket extends Packet {
	public WelcomeRequestPacket(int characterID) {
		super(PacketFamily.PACKET_WELCOME, PacketAction.PACKET_REQUEST);
		writeEncodedInt(characterID);
	}
}
