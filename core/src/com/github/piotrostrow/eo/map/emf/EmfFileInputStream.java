package com.github.piotrostrow.eo.map.emf;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class EmfFileInputStream extends FilterInputStream {

	public EmfFileInputStream(InputStream stream) {
		super(stream);
	}

	int decodeNumber(int a, int b, int c, int d) {
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

	public int readUnsignedByte() throws IOException {
		int b = in.read();
		return decodeNumber(b, 254, 254, 254);
	}

	public int readUnsignedShort() throws IOException {
		return decodeNumber(in.read(), in.read(), 254, 254);
	}

	public int threeByteInt() throws IOException {
		return decodeNumber(in.read(), in.read(), in.read(), 254);
	}

	public int readInt() throws IOException {
		return decodeNumber(in.read(), in.read(), in.read(), in.read());
	}

	public String readString() throws IOException {
		int length = readUnsignedByte();
		return readString(length);
	}

	public String readString(int length) throws IOException {
		byte[] buffer = new byte[length];
		in.read(buffer);
		return new String(buffer);
	}

	public String readEncodedString() throws IOException {
		int length = readUnsignedByte();
		return readEncodedString(length);
	}

	public String readEncodedString(int length) throws IOException {
		byte[] buffer = new byte[length];
		in.read(buffer);
		return decodeString(buffer);
	}

	public static String decodeString(byte[] data) {
		int n = data.length;
		for (int i = 0; i < n / 2; ++i) {
			//std::swap (data[i], data[n - i - 1]);
			byte temp = data[i];
			data[i] = data[n - i - 1];
			data[n - i - 1] = temp;
		}

		//boolean flippy = n % 2 == 1;
		int flippy = n % 2;

		for (int i = 0; i < n; ++i) {
			int c = data[i];

			int f = 0x2E * flippy * ((c >= 0x50) ? -1 : 1);

			if (c >= 0x22 && c <= 0x7E)
				data[i] = (byte) (0x9F - c - f);

			flippy = (flippy + 1) % 2;
		}

		return new String(data);
	}
}
