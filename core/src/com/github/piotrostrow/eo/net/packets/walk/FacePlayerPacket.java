package com.github.piotrostrow.eo.net.packets.walk;

import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.PacketAction;
import com.github.piotrostrow.eo.net.PacketFamily;

/**
 * Client to server packet to turn own character
 */
public class FacePlayerPacket extends Packet {

	public FacePlayerPacket(int direction) {
		super(PacketFamily.PACKET_FACE, PacketAction.PACKET_PLAYER);
		writeEncodedByte(direction);
	}
}
