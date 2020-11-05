package com.github.piotrostrow.eo.net;

import java.lang.reflect.Field;

public class PacketAction {
	public static final byte PACKET_REQUEST = 1;
	public static final byte PACKET_ACCEPT = 2;
	public static final byte PACKET_REPLY = 3;
	public static final byte PACKET_REMOVE = 4;
	public static final byte PACKET_AGREE = 5;
	public static final byte PACKET_CREATE = 6;
	public static final byte PACKET_ADD = 7;
	public static final byte PACKET_PLAYER = 8;
	public static final byte PACKET_TAKE = 9;
	public static final byte PACKET_USE = 10;
	public static final byte PACKET_BUY = 11;
	public static final byte PACKET_SELL = 12;
	public static final byte PACKET_OPEN = 13;
	public static final byte PACKET_CLOSE = 14;
	public static final byte PACKET_MSG = 15;
	public static final byte PACKET_SPEC = 16;
	public static final byte PACKET_ADMIN = 17;
	public static final byte PACKET_LIST = 18;
	public static final byte PACKET_TELL = 20;
	public static final byte PACKET_REPORT = 21;
	public static final byte PACKET_ANNOUNCE = 22;
	public static final byte PACKET_SERVER = 23;
	public static final byte PACKET_DROP = 24;
	public static final byte PACKET_JUNK = 25;
	public static final byte PACKET_OBTAIN = 26;
	public static final byte PACKET_GET = 27;
	public static final byte PACKET_KICK = 28;
	public static final byte PACKET_RANK = 29;
	public static final byte PACKET_TARGET_SELF = 30;
	public static final byte PACKET_TARGET_OTHER = 31;
	public static final byte PACKET_TARGET_GROUP = 33; // Tentative name
	public static final byte PACKET_DIALOG = 34;
	public static final byte PACKET_INTERNAL_NULL = (byte) 128;
	public static final byte PACKET_INTERNAL_WARP = (byte) 129;
	public static final byte PACKET_PING = (byte) 240;
	public static final byte PACKET_PONG = (byte) 241;
	public static final byte PACKET_NET3 = (byte) 242;
	public static final byte PACKET_A_INIT = (byte) 255;

	public static String getName(int b) {
		try {
			for (Field field : PacketAction.class.getFields())
				if (field.getByte(null) == b)
					return field.getName();
		}catch (Exception e){
			e.printStackTrace();
		}
		return "Unknown";
	}
}
