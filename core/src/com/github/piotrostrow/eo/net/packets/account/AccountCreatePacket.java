package com.github.piotrostrow.eo.net.packets.account;

import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.PacketAction;
import com.github.piotrostrow.eo.net.PacketFamily;
import com.github.piotrostrow.eo.util.HWID;

public class AccountCreatePacket extends Packet {

	public AccountCreatePacket(String username, String password, String email) {
		super(PacketFamily.PACKET_ACCOUNT, PacketAction.PACKET_CREATE);
		writeEncodedShort(0); // account creation session ID?
		writeEncodedByte(0); // ?

		addBreakString(username);
		addBreakString(password);
		addBreakString("Noname");
		addBreakString("Nomansland");
		addBreakString("random@address.com");
		addBreakString("computer");

		addBreakString(HWID.get());
	}
}
