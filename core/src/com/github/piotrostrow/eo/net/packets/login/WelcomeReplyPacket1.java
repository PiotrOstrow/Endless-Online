package com.github.piotrostrow.eo.net.packets.login;

import com.github.piotrostrow.eo.net.Packet;

/**
 * Reply packet to {@link WelcomeRequestPacket}
 */
public class WelcomeReplyPacket1 extends Packet {

	public WelcomeReplyPacket1(byte[] buffer) {
		super(buffer);
	}

	public int getMapID() {
		return readEncodedShort(10);
	}

	public int getPlayerID() {
		return readEncodedShort(4);
	}

	public int getCharacterID() {
		return readEncodedInt(6);
	}
}