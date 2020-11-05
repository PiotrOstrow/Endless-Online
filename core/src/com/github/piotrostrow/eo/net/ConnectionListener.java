package com.github.piotrostrow.eo.net;

public interface ConnectionListener {
	void onConnect();
	void onDisconnect();
	void handlePacket(Packet packet);
}
