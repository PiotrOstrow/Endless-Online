package com.github.piotrostrow.eo.net;

import java.lang.reflect.Field;

public class PacketFamily {
	public static final byte PACKET_INTERNAL = 0;
	public static final byte PACKET_CONNECTION = 1;
	public static final byte PACKET_ACCOUNT = 2;
	public static final byte PACKET_CHARACTER = 3;
	public static final byte PACKET_LOGIN = 4;
	public static final byte PACKET_WELCOME = 5;
	public static final byte PACKET_WALK = 6;
	public static final byte PACKET_FACE = 7;
	public static final byte PACKET_CHAIR = 8;
	public static final byte PACKET_EMOTE = 9;
	public static final byte PACKET_ATTACK = 11;
	public static final byte PACKET_SPELL = 12;
	public static final byte PACKET_SHOP = 13;
	public static final byte PACKET_ITEM = 14;
	public static final byte PACKET_STATSKILL = 16;
	public static final byte PACKET_GLOBAL = 17;
	public static final byte PACKET_TALK = 18;
	public static final byte PACKET_WARP = 19;
	public static final byte PACKET_JUKEBOX = 21;
	public static final byte PACKET_PLAYERS = 22;
	public static final byte PACKET_AVATAR = 23;
	public static final byte PACKET_PARTY = 24;
	public static final byte PACKET_REFRESH = 25;
	public static final byte PACKET_NPC = 26;
	public static final byte PACKET_PLAYER_AUTOREFRESH = 27;
	public static final byte PACKET_NPC_AUTOREFRESH = 28;
	public static final byte PACKET_APPEAR = 29;
	public static final byte PACKET_PAPERDOLL = 30;
	public static final byte PACKET_EFFECT = 31;
	public static final byte PACKET_TRADE = 32;
	public static final byte PACKET_CHEST = 33;
	public static final byte PACKET_DOOR = 34;
	public static final byte PACKET_MESSAGE = 35;
	public static final byte PACKET_BANK = 36;
	public static final byte PACKET_LOCKER = 37;
	public static final byte PACKET_BARBER = 38;
	public static final byte PACKET_GUILD = 39;
	public static final byte PACKET_MUSIC = 40;
	public static final byte PACKET_SIT = 41;
	public static final byte PACKET_RECOVER = 42;
	public static final byte PACKET_BOARD = 43;
	public static final byte PACKET_CAST = 44;
	public static final byte PACKET_ARENA = 45;
	public static final byte PACKET_PRIEST = 46;
	public static final byte PACKET_MARRIAGE = 47;
	public static final byte PACKET_ADMININTERACT = 48;
	public static final byte PACKET_CITIZEN = 49;
	public static final byte PACKET_QUEST = 50;
	public static final byte PACKET_BOOK = 51;
	public static final byte PACKET_F_INIT = (byte) 255;

	public static String getName(byte b) {
		try {
			for (Field field : PacketFamily.class.getFields())
				if (field.getByte(null) == b)
					return field.getName();
		}catch (Exception e){
			e.printStackTrace();
		}
		return "Unknown";
	}
}
