package com.github.piotrostrow.eo.net.packets.warp;

import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.PacketAction;
import com.github.piotrostrow.eo.net.PacketFamily;

public class WarpAcceptPacket extends Packet {

	public WarpAcceptPacket(int mapID) {
		super(PacketFamily.PACKET_WARP, PacketAction.PACKET_ACCEPT);
		writeEncodedShort(mapID);
		writeEncodedShort(0);
	}
}
