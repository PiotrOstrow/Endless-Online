package com.github.piotrostrow.eo.net.packets.init;

import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.PacketAction;
import com.github.piotrostrow.eo.net.PacketFamily;

import static com.github.piotrostrow.eo.util.NumberEncoder.*;

public class ConnectionAcceptPacket extends Packet {

	public ConnectionAcceptPacket(InitResponsePacket packet) {
		this(packet.getSendMultiplier(), packet.getReceiveMultiplier(), packet.getClientID());
	}

	public ConnectionAcceptPacket(byte sendMultiplier, byte receiveMultiplier, 	short clientID) {
		super(PacketFamily.PACKET_CONNECTION, PacketAction.PACKET_ACCEPT/*, size*/);

		buffer.put(encodeNumber(sendMultiplier & 0xFF, 2));
		buffer.put(encodeNumber(receiveMultiplier & 0xFF, 2));
		buffer.putShort(clientID);
	}
}