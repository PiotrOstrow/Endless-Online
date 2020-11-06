package com.github.piotrostrow.eo.net.packets.login;

import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.PacketAction;
import com.github.piotrostrow.eo.net.PacketFamily;

/**
 * Client to server packet sent after the loading animation in character select stage
 */
public class WelcomeMsgPacket extends Packet {

	public WelcomeMsgPacket(int characterID) {
		super(PacketFamily.PACKET_WELCOME, PacketAction.PACKET_MSG);

		// TODO: unknown 3 bytes
		skip(3);

		writeEncodedInt(characterID);
	}
}
