package com.github.piotrostrow.eo.net.packets.walk;

import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.PacketAction;
import com.github.piotrostrow.eo.net.PacketFamily;
import com.github.piotrostrow.eo.util.PacketTimestamp;

/**
 * Client to server movement packet
 */
public class WalkPlayerPacket extends Packet {

	public WalkPlayerPacket(int direction, int destX, int destY) {
		super(PacketFamily.PACKET_WALK, PacketAction.PACKET_PLAYER);

		writeEncodedByte(direction);
		writeEncodedThreeByteInt(PacketTimestamp.get());
		writeEncodedByte(destX);
		writeEncodedByte(destY);
	}
}
