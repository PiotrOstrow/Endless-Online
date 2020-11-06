package com.github.piotrostrow.eo.net.packets.init;

import com.github.piotrostrow.eo.net.Packet;

/**
 * Server to client Init Init packet, unencoded
 */
public class InitResponsePacket extends Packet {

	public InitResponsePacket(byte[] buffer) {
		super(buffer);
	}

	public int getInitReply() {
		return buffer.get(2);
	}

	public byte getSeqBytes1() {
		return buffer.get(3);
	}

	public byte getSeqBytes2() {
		return buffer.get(4);
	}

	public byte getSendMultiplier() {
		return buffer.get(5);
	}

	public byte getReceiveMultiplier() {
		return buffer.get(6);
	}

	public short getClientID() {
		return buffer.getShort(7);
	}
}
