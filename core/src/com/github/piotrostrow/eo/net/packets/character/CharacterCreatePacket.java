package com.github.piotrostrow.eo.net.packets.character;

import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.PacketAction;
import com.github.piotrostrow.eo.net.PacketFamily;

public class CharacterCreatePacket extends Packet {

	public CharacterCreatePacket(String name, int gender, int hairStyle, int hairColor, int race) {
		super(PacketFamily.PACKET_CHARACTER, PacketAction.PACKET_CREATE);

		// see handlers/Character.cpp in eoserv source, possibly sending the same value that was received
		// from the server in character reply packet, which is hardcoded to 1000
		writeEncodedShort(1000);

		writeEncodedShort(gender);
		writeEncodedShort(hairStyle);
		writeEncodedShort(hairColor);
		writeEncodedShort(race);

		writeEncodedByte(0); // skipped byte ?

		addBreakString(name);
	}
}
