package com.github.piotrostrow.eo.net.packets.warp;

import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.PacketAction;
import com.github.piotrostrow.eo.net.PacketFamily;

public class DoorOpenPacket extends Packet {

	public DoorOpenPacket(int x, int y) {
		super(PacketFamily.PACKET_DOOR, PacketAction.PACKET_OPEN);
		writeEncodedByte(x);
		writeEncodedByte(y);
	}
}
