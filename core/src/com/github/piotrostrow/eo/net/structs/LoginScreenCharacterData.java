package com.github.piotrostrow.eo.net.structs;

import com.github.piotrostrow.eo.net.Packet;

public class LoginScreenCharacterData {

	public final String name;
	public final int id;
	public final int level;
	public final int gender;
	public final int hairstyle;
	public final int haircolor;
	public final int race;
	public final int admin;

	public final int boots;
	public final int armor;
	public final int hat;
	public final int shield;
	public final int weapon;

	public LoginScreenCharacterData(Packet packet) {
		name = packet.readBreakString();
		id = packet.readEncodedInt();
		level = packet.readEncodedByte();
		gender = packet.readEncodedByte();
		hairstyle = packet.readEncodedByte();
		haircolor = packet.readEncodedByte();
		race = packet.readEncodedByte();
		admin = packet.readEncodedByte();

		boots = packet.readEncodedShort();
		armor = packet.readEncodedShort();
		hat = packet.readEncodedShort();
		shield = packet.readEncodedShort();
		weapon = packet.readEncodedShort();
	}
}
