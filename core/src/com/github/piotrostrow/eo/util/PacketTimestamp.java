package com.github.piotrostrow.eo.util;

import java.time.LocalDateTime;

public class PacketTimestamp {
	public static int get() {
		LocalDateTime dateTime = LocalDateTime.now();
		int ms = (int) (System.currentTimeMillis() % 1000);
		return dateTime.getHour() * 360000 + dateTime.getMinute() * 6000 + dateTime.getSecond() * 100 + ms / 10;
	}
}
