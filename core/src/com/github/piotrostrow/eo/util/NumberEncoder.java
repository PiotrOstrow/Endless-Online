package com.github.piotrostrow.eo.util;

public class NumberEncoder {

	public static byte[] encodeNumber(int number, int size) {
		long unsigned = number & 0xFFFFFFFFL;
		byte[] result = new byte[size];
		for(int i = 0; i < result.length; i++)
			result[i] = (byte) 254;
		long original = unsigned;

		if (original >= 16194277 && size >= 4){
			result[3] = (byte) (unsigned / 16194277 + 1);
			unsigned = unsigned % 16194277;
		}

		if (original >= 64009 && size >= 3){
			result[2] = (byte) (unsigned / 64009 + 1);
			unsigned = unsigned % 64009 ;
		}

		if (original >= 253 && size >= 2){
			result[1] = (byte) (unsigned / 253 + 1);
			unsigned = unsigned % 253;
		}

		result[0] = (byte) (unsigned + 1);

		return result;
	}

	public static int decodeNumber(byte[] buffer) {
		switch(buffer.length){
			case 1: return decodeNumber(buffer[0], 254, 254, 254);
			case 2: return decodeNumber(buffer[0], buffer[1], 254, 254);
			case 3: return decodeNumber(buffer[0], buffer[1], buffer[2], 254);
			case 4: return decodeNumber(buffer[0], buffer[1], buffer[2], buffer[3]);
		}
		return 0;
	}

	public static int decodeNumber(int a, int b, int c, int d) {
		a &= 0xFF;
		b &= 0xFF;
		c &= 0xFF;
		d &= 0xFF;
		if (a == 254) a = 1;
		if (b == 254) b = 1;
		if (c == 254) c = 1;
		if (d == 254) d = 1;
		--a;
		--b;
		--c;
		--d;

		return (d * 253 * 253 * 253)
				+ (c * 253 * 253)
				+ (b * 253)
				+ a;
	}
}
