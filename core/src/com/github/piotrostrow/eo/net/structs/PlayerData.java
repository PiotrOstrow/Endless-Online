package com.github.piotrostrow.eo.net.structs;

import com.github.piotrostrow.eo.net.Packet;

public class PlayerData {

	public final String name;
	public final int playerID;
	public final int mapID;
	public final int x, y;
	public final int direction;
	public final int unknownValue; // unknown value hardcoded 6
	public final String guildTag;
	public final int level, gender, hairstyle, haircolor, race;
	public final int maxHP, hp, maxTP, tp;

	public final int boots, armor, hat, shield, weapon;
	public final int sitting, hidden;

	public PlayerData(Packet packet) {
		name = packet.readBreakString();
		playerID = packet.readEncodedShort();
		mapID = packet.readEncodedShort();
		x = packet.readEncodedShort();
		y = packet.readEncodedShort();
		direction = packet.readEncodedByte();
		unknownValue = packet.readEncodedByte();
		guildTag = packet.readFixedString(3);
		level = packet.readEncodedByte();
		gender = packet.readEncodedByte();
		hairstyle = packet.readEncodedByte();
		haircolor = packet.readEncodedByte();
		race = packet.readEncodedByte();
		maxHP = packet.readEncodedShort();
		hp = packet.readEncodedShort();
		maxTP = packet.readEncodedShort();
		tp = packet.readEncodedShort();

		boots = packet.readEncodedShort();
		packet.skip(3 * 2); 	// 3x 0 values
		armor = packet.readEncodedShort();
		packet.skip(2);			// 1x 0 value
		hat = packet.readEncodedShort();
		shield = packet.readEncodedShort();
		weapon = packet.readEncodedShort();

		sitting = packet.readEncodedByte();
		hidden = packet.readEncodedByte();

		packet.skip(1); 		// hard coded 0xFF byte
	}
}
