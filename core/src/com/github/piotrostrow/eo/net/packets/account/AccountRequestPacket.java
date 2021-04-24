package com.github.piotrostrow.eo.net.packets.account;

import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.PacketAction;
import com.github.piotrostrow.eo.net.PacketFamily;

public class AccountRequestPacket extends Packet {

	public AccountRequestPacket(String username) {
		super(PacketFamily.PACKET_ACCOUNT, PacketAction.PACKET_REQUEST);
		buffer.put(username.getBytes());
	}
}
