package com.github.piotrostrow.eo.net.packets.init;

import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.util.HWID;

import java.nio.charset.StandardCharsets;

public class InitPacket extends Packet {

	public InitPacket() {
		super((byte) 255, (byte) 255);

		//hash TODO
		buffer.put(new byte[]{123, 53, 122});

		// Version Major:
		buffer.put((byte) 1);
		// Version Minor:
		buffer.put((byte) 1);
		// Version Build:
		buffer.put((byte) 29);
		// hardcoded
		buffer.put((byte) 113);

		String serial = HWID.get();
		buffer.put((byte) serial.length());
		buffer.put(serial.getBytes(StandardCharsets.US_ASCII));
	}
}
