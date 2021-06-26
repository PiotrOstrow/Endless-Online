package com.github.piotrostrow.eo.net;

import java.nio.ByteBuffer;

import static com.github.piotrostrow.eo.util.NumberEncoder.*;

public class PacketEncoder {

	private final byte[] temp = new byte[Client.MAX_BUFFER_SIZE];

	private int initialSequence;
	private int sequenceIncrement = 0;

	private int sendMultiplier;
	private int receiveMultiplier;

	private boolean isInitialized = false;

	private int calculateNextSequenceNumber() {
		int sequenceStart = initialSequence;
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
		for(int i = 0; i < data.length; i++) {
			data[i] = (byte) (data[i] ^ 0x80);
			if(data[i] == (byte) 0x80)
				data[i] = 0;
			else if(data[i] == 0)
				data[i] = (byte) 0x80;
		}
	}

	private void swapMultiples(byte[] data, int multi){
		ByteBuffer buffer = ByteBuffer.wrap(temp);

		int i;
		for(i = 0; i < data.length; i++) {
			if((data[i] & 0xFF) % multi == 0) {
				buffer.put(data[i]);
			} else {
				if(buffer.position() > 1) {
					for(int j = 0; j < buffer.position(); j++){
						data[i - buffer.position() + j] = buffer.get(buffer.position() - 1 - j);
					}
				}
				buffer.position(0);
			}
		}

		if(buffer.position() > 1)
			for(int j = 0; j < buffer.position(); j++)
				data[i - buffer.position() + j] = buffer.get(buffer.position() - 1 - j);
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

	public void updateInitlaSequenceNumberAfterAccountReply(int newSequence) {
		this.initialSequence = newSequence;
	}

	public void setInitialSequenceNumber(int sequence1, int sequence2) {
		this.initialSequence = (sequence1 & 0xFF) * 7 - 11 + (sequence2 & 0xFF) - 2;
	}

	public void updateSequenceNumber(int sequence1, int sequence2) {
		this.initialSequence = (sequence1 & 0xFFFF) - (sequence2 & 0xFFFF);
	}

	public void setEncodeMultiples(int sendMultiplier, int receiveMultiplier) {
		this.sendMultiplier = sendMultiplier;
		this.receiveMultiplier = receiveMultiplier;
		this.isInitialized = true;
	}

	public boolean isInitialized() {
		return isInitialized;
	}
}
