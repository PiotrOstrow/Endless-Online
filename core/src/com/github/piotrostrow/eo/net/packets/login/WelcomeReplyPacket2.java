package com.github.piotrostrow.eo.net.packets.login;

import com.github.piotrostrow.eo.net.Packet;

import java.util.ArrayList;

/**
 * Reply packet to {@link WelcomeMsgPacket}
 */
public class WelcomeReplyPacket2 extends Packet {

	public final String[] MOTD = new String[9];

	public final int weight, maxWeight;

	public final ArrayList<Item> inventory = new ArrayList<>();
	public final ArrayList<Spell> spells = new ArrayList<>();

	public final Character[] characters;

	public WelcomeReplyPacket2(byte[] buffer) {
		super(buffer);


		skip(2 + 1);		// subID and hard coded 0xFF byte

		for(int i = 0; i < MOTD.length; i++)
			MOTD[i] = readBreakString();

		weight = readEncodedByte();
		maxWeight = readEncodedByte();

		while(readUnencodedByte() != 0xFF){
			skip(-1);
			int itemID = readEncodedShort();
			int amount = readEncodedInt();
			inventory.add(new Item(itemID, amount));
		}

		while(readUnencodedByte() != 0xFF){
			skip(-1);
			int spellID = readEncodedShort();
			int spellLevel = readEncodedShort();
			spells.add(new Spell(spellID, spellLevel));
		}

		characters = new Character[readEncodedByte()];
		skip(1); 	// hard coded 0xFF byte

		for(int i = 0; i < characters.length; i++) {
			characters[i] = new Character();
		}
	}

	// TODO: not all of these need to be ints but i was lazy
	public class Character {

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

		private Character() {
			name = readBreakString();
			playerID = readEncodedShort();
			mapID = readEncodedShort();
			x = readEncodedShort();
			y = readEncodedShort();
			direction = readEncodedByte();
			unknownValue = readEncodedByte();
			guildTag = readFixedString(3);
			level = readEncodedByte();
			gender = readEncodedByte();
			hairstyle = readEncodedByte();
			haircolor = readEncodedByte();
			race = readEncodedByte();
			maxHP = readEncodedShort();
			hp = readEncodedShort();
			maxTP = readEncodedShort();
			tp = readEncodedShort();

			// equipment B000A0HSW
//			character->AddPaperdollData(reply, "B000A0HSW");
			boots = readEncodedShort();
			skip(3 * 2); 	// 3x 0 values
			armor = readEncodedShort();
			skip(2);			// 1x 0 value
			hat = readEncodedShort();
			shield = readEncodedShort();
			weapon = readEncodedShort();

			sitting = readEncodedByte();
			hidden = readEncodedByte();

			skip(1); 		// hard coded 0xFF byte
		}
	}

	public static class Spell {

		public final int ID;
		public final int level;

		public Spell(int ID, int level) {
			this.ID = ID;
			this.level = level;
		}
	}

	public static class Item {

		public final int ID;
		public final int amount;

		private Item(int ID, int amount) {
			this.ID = ID;
			this.amount = amount;
		}
	}
}

