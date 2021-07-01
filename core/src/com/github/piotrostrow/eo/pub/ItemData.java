package com.github.piotrostrow.eo.pub;

import com.badlogic.gdx.graphics.Texture;
import com.github.piotrostrow.eo.assets.Assets;
import com.github.piotrostrow.eo.map.emf.EmfFileInputStream;

import java.io.IOException;

public class ItemData {

	public final int itemID;
	public final String name;
	public final int icon;
	public final int type;
	public final int subtype;
	public final int special;
	public final int hp;
	public final int tp;
	public final int apMin;
	public final int apMax;
	public final int accuracy;
	public final int evade;
	public final int armor;
	public final int str;
	public final int intl;
	public final int wis;
	public final int agi;
	public final int con;
	public final int cha;
	public final int gfxID;
	public final int gender;
	public final int spec3;
	public final int levelReq;
	public final int classReq;
	public final int strReq;
	public final int intlReq;
	public final int wisReq;
	public final int agiReq;
	public final int conReq;
	public final int chaReq;
	public final int element;
	public final int elementPower;
	public final int weight;
	public final int size;

	int[] unknowns = new int[8];

	protected ItemData(EmfFileInputStream stream, int itemID) throws IOException {
		this.itemID = itemID;
		this.name = stream.readString();
		this.icon = stream.readUnsignedShort();
		this.type = stream.read();
		this.subtype = stream.read();
		this.special = stream.read();
		this.hp = stream.readUnsignedShort();
		this.tp = stream.readUnsignedShort();
		this.apMin = stream.readUnsignedShort();
		this.apMax = stream.readUnsignedShort();
		this.accuracy = stream.readUnsignedShort();
		this.evade = stream.readUnsignedShort();
		this.armor = stream.readUnsignedShort();
		this.unknowns[0] = stream.read();
		this.str = stream.read();
		this.intl = stream.read();
		this.wis = stream.read();
		this.agi = stream.read();
		this.con = stream.read();
		this.cha = stream.read();
		this.unknowns[1] = stream.read();
		this.unknowns[2] = stream.read();
		this.unknowns[3] = stream.read();
		this.unknowns[4] = stream.read();
		this.unknowns[5] = stream.read();
		this.unknowns[6] = stream.read();
		this.gfxID = stream.threeByteInt();
		this.gender = stream.read();
		this.spec3 = stream.read();
		this.levelReq = stream.readUnsignedShort();
		this.classReq = stream.readUnsignedShort();
		this.strReq = stream.readUnsignedShort();
		this.intlReq = stream.readUnsignedShort();
		this.wisReq = stream.readUnsignedShort();
		this.agiReq = stream.readUnsignedShort();
		this.conReq = stream.readUnsignedShort();
		this.chaReq = stream.readUnsignedShort();
		this.element = stream.read();
		this.elementPower = stream.read();
		this.weight = stream.read();
		this.unknowns[7] = stream.read();
		this.size = stream.read();
	}

	public Texture getMapTexture() {
		return Assets.gfx(23, 2 * icon + 99);
	}

	public Texture getInventoryTexture() {
		return Assets.gfx(23, 2 * icon + 100);
	}
}
