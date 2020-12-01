package com.github.piotrostrow.eo.net.packets.connection;

import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.PacketAction;
import com.github.piotrostrow.eo.net.PacketFamily;

import static com.github.piotrostrow.eo.util.NumberEncoder.encodeNumber;

public class ConnectionAcceptPacket extends Packet {

	public ConnectionAcceptPacket(int sendMultiplier, int receiveMultiplier, short clientID) {
		super(PacketFamily.PACKET_CONNECTION, PacketAction.PACKET_ACCEPT/*, size*/);

		buffer.put(encodeNumber(receiveMultiplier & 0xFF, 2));
		buffer.put(encodeNumber(sendMultiplier & 0xFF, 2));

		//TODO: should this be encoded ??
		buffer.putShort(clientID);
	}
}
