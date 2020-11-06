package com.github.piotrostrow.eo.net;

import com.github.piotrostrow.eo.net.packets.connection.ConnectionPlayerPacket;
import com.github.piotrostrow.eo.net.packets.login.LoginReplyPacket;
import com.github.piotrostrow.eo.net.packets.login.WelcomeReplyPacket1;
import com.github.piotrostrow.eo.net.packets.login.WelcomeReplyPacket2;

import static com.github.piotrostrow.eo.util.NumberEncoder.decodeNumber;

public class PacketFactory {

	/**
	 *
	 * @param buffer unencrypted packet data
	 * @return Packet object of type appropriate to packet action and packet family, or generic Packet type by default
	 */
	static Packet serverToClient(byte[] buffer) {
		byte action = buffer[0];
		byte family = buffer[1];

		if(action == PacketAction.PACKET_REPLY && family == PacketFamily.PACKET_LOGIN)
			return new LoginReplyPacket(buffer);
		if(action == PacketAction.PACKET_PLAYER && family == PacketFamily.PACKET_CONNECTION)
			return new ConnectionPlayerPacket(buffer);

		if(family == PacketFamily.PACKET_WELCOME && action == PacketAction.PACKET_REPLY) {
			int subID = decodeNumber(buffer[2], buffer[3], 254, 254) & 0xFFFF;
			if(subID == 1)
				return new WelcomeReplyPacket1(buffer);
			else if(subID == 2)
				return new WelcomeReplyPacket2(buffer);
		}

		return new Packet(buffer);
	}
}
