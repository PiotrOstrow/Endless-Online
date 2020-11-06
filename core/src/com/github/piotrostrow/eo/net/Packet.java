package com.github.piotrostrow.eo.net;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.github.piotrostrow.eo.util.NumberEncoder.*;

public class Packet {

	private static final byte BREAK_CHAR = (byte) 0xFF;

	protected final ByteBuffer buffer;

	/**
	 * Constructor meant for incoming packets, where the first 2 bytes are packet action and packet family
	 */
	public Packet(byte[] buffer) {
		this.buffer = ByteBuffer.wrap(buffer);
		this.buffer.position(2); // skip packet action and packet family bytes
	}

	protected Packet(byte packetFamily, byte packetAction) {
		this(packetFamily, packetAction, 1024);
	}

	/**
	 * @param size The exact size of the packet, excluding the first 4 bytes (2 for size, 2 for packet identifiers)
	 */
	protected Packet(byte packetFamily, byte packetAction, int size) {
		buffer = ByteBuffer.allocate(Math.max(2, size + 2));
		buffer.put(packetAction);
		buffer.put(packetFamily);
	}

	/**
	 * skip n bytes in the buffer
	 */
	public void skip(int n) {
		buffer.position(buffer.position() + n);
	}

	public String readBreakString() {
		int startPosition = buffer.position();
		int endPosition = startPosition + 1;

		// for loop with null statement to search for the end char string marker
		for (; endPosition < buffer.capacity() && buffer.get() != BREAK_CHAR; endPosition++) ;

		int length = endPosition - startPosition - 1;
		return new String(buffer.array(), startPosition, length, StandardCharsets.US_ASCII);
	}

	public int readByte() {
		return buffer.get() & 0xFF;
	}

	public int readEncodedByte() {
		return decodeNumber(buffer.get(), 254, 254, 254) & 0xFF;
	}

	public int readEncodedByte(int position) {
		return decodeNumber(buffer.get(position), 254, 254, 254) & 0xFF;
	}

	public int readEncodedShort() {
		return decodeNumber(buffer.get(), buffer.get(), 254, 254) & 0xFFFF;
	}

	public int readEncodedShort(int position) {
		return decodeNumber(buffer.get(position), buffer.get(position + 1), 254, 254) & 0xFFFF;
	}

	public int readEncodedInt() {
		return decodeNumber(buffer.get(), buffer.get(), buffer.get(), buffer.get());
	}

	protected void writeEncodedShort(int value) {
		buffer.put(encodeNumber(value, 2));
	}

	protected void writeEncodedInt(int value) {
		buffer.put(encodeNumber(value, 4));
	}

	/**
	 * @return Signed PacketAction as byte value
	 */
	public byte getPacketActionByte() {
		return buffer.get(0);
	}

	/**
	 * @return Signed PacketFamily as byte value
	 */
	public byte getPacketFamilyByte() {
		return buffer.get(1);
	}

	/**
	 * @return Unsigned PacketAction as an int value
	 */
	public int getPacketAction() {
		return buffer.get(0) & 0xFF;
	}

	/**
	 * @return Unsigned PacketFamily as an int value
	 */
	public int getPacketFamily() {
		return buffer.get(1) & 0xFF;
	}

	public String getPacketActionName() {
		return PacketAction.getName(buffer.get(0));
	}

	public String getPacketFamilyName() {
		return PacketFamily.getName(buffer.get(1));
	}

	protected void addBreakString(String s) {
		byte[] bytes = s.getBytes(StandardCharsets.US_ASCII);
		for (int i = 0; i < bytes.length; i++)
			if (bytes[i] == (byte) 0xFF)
				bytes[i] = 121;
		buffer.put(bytes);
		buffer.put((byte) 0xFF);
	}

	public int getSize() {
		return buffer.position();
	}

	protected void resetReadingPosition() {
		buffer.position(2);
	}

	byte[] getBytes() {
		return buffer.array();
	}

	public boolean equals(byte packetFamily, byte packetAction) {
		return packetFamily == getPacketFamilyByte() && packetAction == getPacketAction();
	}
}
