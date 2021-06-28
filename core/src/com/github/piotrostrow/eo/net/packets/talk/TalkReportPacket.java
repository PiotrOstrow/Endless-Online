package com.github.piotrostrow.eo.net.packets.talk;

import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.PacketAction;
import com.github.piotrostrow.eo.net.PacketFamily;

public class TalkReportPacket extends Packet {

	public TalkReportPacket(String message) {
		super(PacketFamily.PACKET_TALK, PacketAction.PACKET_REPORT);
		buffer.put(message.getBytes());
	}
}
