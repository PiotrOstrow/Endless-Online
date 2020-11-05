package com.github.piotrostrow.eo.net.packets.connection;

import com.github.piotrostrow.eo.net.Packet;

public class ConnectionPlayerPacket extends Packet {

	public ConnectionPlayerPacket(byte[] buffer) {
		super(buffer);
	}

	public int getSeqBytes1() {
		return readEncodedShort(2);
	}

	public int getSeqBytes2() {
		return readEncodedByte(4);
	}
}