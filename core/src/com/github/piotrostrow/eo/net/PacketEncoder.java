package com.github.piotrostrow.eo.net;

import java.nio.ByteBuffer;

import static com.github.piotrostrow.eo.util.NumberEncoder.*;

public class PacketEncoder {

	private final byte[] temp = new byte[Client.MAX_BUFFER_SIZE];

	private int initialSequence;
	private int sequenceIncrement = 0;

	private int sendMultiplier;
	private int receiveMultiplier;

	private int calculateNextSequenceNumber() {
		var sequenceStart = initialSequence;
		sequenceIncrement = (sequenceIncrement + 1) % 10;
		return sequenceStart + sequenceIncrement;
	}

	public synchronized byte[] encode(Packet packet) {
		int sequence = calculateNextSequenceNumber();
		int numberOfSequenceBytes = sequence >= 253 ? 2 : 1;
		byte[] encodedSequenceBytes = encodeNumber(sequence, numberOfSequenceBytes);

		byte[] originalBuffer = packet.getBytes();

		ByteBuffer buffer = ByteBuffer.allocate(packet.getSize() + numberOfSequenceBytes);
		buffer.put(originalBuffer, 0, 2); // packet action and packet family
		buffer.put(encodedSequenceBytes);
		buffer.put(originalBuffer, 2, packet.getSize() - 2);

		//encode
		byte[] output = buffer.array();
		swapMultiples(output, sendMultiplier);
		interleave(output);
		flip(output);

		return output;
	}

	//TODO: return packet object, look at read offsets
	public synchronized byte[] decode(byte[] output, int size) {
		flip(output);
		deinterleave(output);
		swapMultiples(output, receiveMultiplier);

		return output;
	}

	private void flip(byte[] data) {
		for(int i = 0; i < data.length; i++)
			data[i] = (byte) (data[i] ^ 0x80);
	}

	private void swapMultiples(byte[] data, int multi){
		for (int i = 0, n = 0; i <= data.length; ++i){
			if (i != data.length && data[i] % multi == 0){
				++n;
			} else {
				if (n > 1) {
					for (int j = 0; j < n / 2; ++j) {
						byte temp = data[i - n + j];
						data[i - n + j] = data[i - j - 1];
						data[i - j - 1] = temp;
					}
				}
				n = 0;
			}
		}
	}

	private void interleave(byte[] data){
		int n = 0;
		int i;

		for(i = 0;i < data.length; i += 2)
			temp[i] = data[n++];

		for(i -= data.length % 2 == 0 ? 1 : 3; i >= 0; i -= 2)
			temp[i] = data[n++];

		for(i = 0; i < data.length; i++)
			data[i] = temp[i];
	}

	private void deinterleave(byte[] data){
		int n = 0;
		int i;

		for(i = 0; i < data.length; i += 2)
			temp[n++] = data[i];

		for(i -= data.length % 2 == 0 ? 1 : 3; i >= 0; i -= 2)
			temp[n++] = data[i];

		for(i = 0; i < data.length; i++)
			data[i] = temp[i];
	}

	public void setInitialSequenceNumber(int sequence1, int sequence2) {
		this.initialSequence = (sequence1 & 0xFF) * 7 - 11 + (sequence2 & 0xFF) - 2;
	}

	public void updateSequenceNumber(int sequence1, int sequence2) {
		this.initialSequence = (sequence1 & 0xFFFF) - (sequence2 & 0xFFFF);
	}

	public void setEncodeMultiples(byte sendMultiplier, byte receiveMultiplier) {
		this.sendMultiplier = sendMultiplier & 0xFF;
		this.receiveMultiplier = receiveMultiplier & 0xFF;
	}

	public boolean isInitialized() {
		return sendMultiplier != 0 && receiveMultiplier != 0;
	}
}
