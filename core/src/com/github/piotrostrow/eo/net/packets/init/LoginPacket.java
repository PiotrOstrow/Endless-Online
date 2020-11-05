package com.github.piotrostrow.eo.net.packets.init;

import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.PacketAction;
import com.github.piotrostrow.eo.net.PacketFamily;

public class LoginPacket extends Packet {

	public LoginPacket(String username, String password) {
		super(PacketFamily.PACKET_LOGIN, PacketAction.PACKET_REQUEST);

		addBreakString(username);
		addBreakString(password);
	}
}
