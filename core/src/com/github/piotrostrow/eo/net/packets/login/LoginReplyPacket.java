package com.github.piotrostrow.eo.net.packets.login;

import com.github.piotrostrow.eo.net.Packet;

public class LoginReplyPacket extends Packet {

	private Character[] characters;

	public LoginReplyPacket(byte[] buffer) {
		super(buffer);
	}

	public Character[] getCharacters() {
		if(characters == null){
			characters = new Character[getCharCount()];

			buffer.mark();
			buffer.position(7);
			for(int i = 0; i < characters.length; i++){
				// data read in the constructor
				characters[i] = new Character();

				// hard coded byte
				skip(1);
			}
			buffer.reset();
		}
		return characters;
	}

	public int getCharCount() {
		return readEncodedByte(4) & 0xFF;
	}

	public int getReplyCode() {
		return readEncodedShort(2) & 0xFFFF;
	}

	public class Character {

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

		private Character() {
			name = readBreakString();
			id = readEncodedInt();
			level = readEncodedByte();
			gender = readEncodedByte();
			hairstyle = readEncodedByte();
			haircolor = readEncodedByte();
			race = readEncodedByte();
			admin = readEncodedByte();

			boots = readEncodedShort();
			armor = readEncodedShort();
			hat = readEncodedShort();
			shield = readEncodedShort();
			weapon = readEncodedShort();
		}
	}
}
