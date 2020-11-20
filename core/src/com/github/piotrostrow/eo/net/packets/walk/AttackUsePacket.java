package com.github.piotrostrow.eo.net.packets.walk;

import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.PacketAction;
import com.github.piotrostrow.eo.net.PacketFamily;
import com.github.piotrostrow.eo.util.PacketTimestamp;

public class AttackUsePacket extends Packet {

	public AttackUsePacket(int direction) {
		super(PacketFamily.PACKET_ATTACK, PacketAction.PACKET_USE);

		writeEncodedByte(direction);
		writeEncodedThreeByteInt(PacketTimestamp.get());
	}
}
