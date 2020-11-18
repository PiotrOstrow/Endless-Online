package com.github.piotrostrow.eo.pub;

import com.github.piotrostrow.eo.map.emf.EmfFileInputStream;

import java.io.File;
import java.io.IOException;

public class EnfFile {

	private final Npc[] npcs;

	public EnfFile(File file) throws IOException {
		EmfFileInputStream stream = new EmfFileInputStream(file);

		stream.skip(7);

		int records = stream.readUnsignedShort() - 1;
		stream.skip(1);

		npcs = new Npc[records];
		for(int i = 0; i < records; i++) {
			npcs[i] = new Npc(stream, i + 1);
		}

		stream.close();
	}

	public Npc getNpcData(int npcID) {
		return npcs[npcID - 1];
	}

	public static class Npc {

		public final int npcID;

		public final String name;
		public final int gfxID;
		public final int boss;
		public final int child;
		public final int type;
		public final int vendorID;
		public final int hp;
		public final int minAP;
		public final int maxAP;
		public final int accuracy;
		public final int evade;
		public final int armor;
		public final int elementWeak;
		public final int elementWeakPower;
		public final int xp;

		public final int[] unknowns = new int[6];

		public Npc(EmfFileInputStream stream, int npcID) throws IOException {
			this.npcID = npcID;
			name = stream.readString();
			gfxID = (stream.readUnsignedShort() - 1) * 40 + 101;
			unknowns[0] = stream.read();
			boss = stream.readUnsignedShort();
			child = stream.readUnsignedShort();
			type = stream.readUnsignedShort();
			vendorID = stream.readUnsignedShort();
			hp = stream.threeByteInt();
			unknowns[1] = stream.readUnsignedShort();
			minAP = stream.readUnsignedShort();
			maxAP = stream.readUnsignedShort();
			accuracy = stream.readUnsignedShort();
			evade = stream.readUnsignedShort();
			armor = stream.readUnsignedShort();
			unknowns[2] = stream.read();
			unknowns[3] = stream.readUnsignedShort();
			unknowns[4] = stream.readUnsignedShort();
			elementWeak = stream.readUnsignedShort();
			elementWeakPower= stream.readUnsignedShort();
			unknowns[5] = stream.read();
			xp = stream.threeByteInt();
		}
	}
}
