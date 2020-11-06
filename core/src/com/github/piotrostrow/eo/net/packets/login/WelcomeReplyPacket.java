package com.github.piotrostrow.eo.net.packets.login;

import com.github.piotrostrow.eo.net.Packet;

/**
 * Reply packet to {@link WelcomeRequestPacket}
 */
public class WelcomeReplyPacket extends Packet {
	public WelcomeReplyPacket(byte[] buffer) {
		super(buffer);

		// index 2, hardcoded value of 1
		int reply = readEncodedShort();

		// index 4, player ID not same as characterID cus 2 bytes
		int playerID = readEncodedShort();

		// index 6, characterID
		int characterID = readEncodedInt();

		// index 10, mapID
		int mapID = readEncodedShort();

		// index 12, 13, something with PK
		skip(2);


	}
}