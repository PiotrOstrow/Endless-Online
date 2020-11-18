package com.github.piotrostrow.eo.net.packets.login;

import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.structs.NpcData;
import com.github.piotrostrow.eo.net.structs.PlayerData;

import java.util.ArrayList;

/**
 * Reply packet to {@link WelcomeMsgPacket}
 */
public class WelcomeReplyPacket2 extends Packet {

	public final String[] MOTD = new String[9];

	public final int weight, maxWeight;

	public final ArrayList<Item> inventory = new ArrayList<>();
	public final ArrayList<Spell> spells = new ArrayList<>();

	public final PlayerData[] characters;
	public final NpcData[] npcs;

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

		characters = new PlayerData[readEncodedByte()];
		skip(1); 	// hard coded 0xFF byte

		for(int i = 0; i < characters.length; i++) {
			characters[i] = new PlayerData(this);
		}

		ArrayList<NpcData> npcs = new ArrayList<>();
		while(readUnencodedByte() != 0xFF){
			skip(-1);
			npcs.add(new NpcData(this));
		}
		this.npcs = npcs.toArray(new NpcData[0]);
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

