package com.github.piotrostrow.eo.net;

import com.badlogic.gdx.Gdx;
import com.github.piotrostrow.eo.net.packets.connection.ConnectionAcceptPacket;
import com.github.piotrostrow.eo.net.packets.init.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

import static com.github.piotrostrow.eo.util.NumberEncoder.*;

public class Client {

	public static final int MAX_BUFFER_SIZE = 8192;

	private Socket socket;
	private BufferedOutputStream bufferedOutputStream;

	private PacketEncoder packetEncoder;

	private ConnectionListener connectionListener;
	private final HashMap<Integer, PacketHandler> internalHandlers = new HashMap<>();
	private final HashMap<Integer, PacketHandler> packetHandlers = new HashMap<>();

	private boolean isConnected;
	private boolean isConnecting;

	public Client() {
		packetEncoder = new PacketEncoder();

		registerInternalPacketHandler(PacketFamily.PACKET_CONNECTION, PacketAction.PACKET_PLAYER, this::handleConnectionPlayerPacket);
	}

	public void setConnectionListener(ConnectionListener connectionListener) {
		this.connectionListener = connectionListener;
	}

	public void registerPacketHandler(byte packetFamily, byte packetAction, PacketHandler packetHandler) {
		int hash = ((packetAction << 8) | packetFamily) & 0xFFFF;
		packetHandlers.put(hash, packetHandler);
	}

	private void registerInternalPacketHandler(byte packetFamily, byte packetAction, PacketHandler packetHandler) {
		int hash = ((packetAction << 8) | packetFamily) & 0xFFFF;
		internalHandlers.put(hash, packetHandler);
	}

	public void connect() {
		if (!isConnected() && !isConnecting()) {
			isConnecting = true;
			Thread thread = new Thread(this::networkLoop);
			thread.setDaemon(true);
			thread.start();
		}
	}

	public void disconnect() {
		if (isConnected() && socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				isConnected = false;
				packetEncoder = new PacketEncoder();
				packetHandlers.clear();
				if (connectionListener != null)
					connectionListener.onDisconnect();
			}
		}
	}

	public boolean isConnecting() {
		return isConnecting;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public PacketEncoder getPacketEncoder() {
		return packetEncoder;
	}

	public void sendEncodedPacket(Packet packet) {
		byte[] encodedData = packetEncoder.encode(packet);

		try {
			sendData(encodedData, encodedData.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendPacket(Packet packet) {
		try {
			sendData(packet.getBytes(), packet.getSize());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendData(byte[] buffer, int length) throws IOException {
		bufferedOutputStream.write(encodeNumber(length, 2));
		bufferedOutputStream.write(buffer, 0, length);
		bufferedOutputStream.flush();
	}

	private void read(byte[] buffer) throws IOException {
		int read = socket.getInputStream().read(buffer);
		if (read < 0)
			throw new SocketException("Connection has been closed");
	}

	private void handleInitResponsePacket(Packet packet) {
		if (packet.getPacketActionByte() != PacketAction.PACKET_A_INIT || packet.getPacketFamilyByte() != PacketFamily.PACKET_F_INIT) {
			throw new RuntimeException("Received packet other than InitResponsePacket before packet encoder was initialized. " +
					" Packet action: " + packet.getPacketActionName() + ", packet family: " + packet.getPacketFamilyName());
		}

		int reply = packet.readUnencodedByte();
		int seqBytes1 = packet.readUnencodedByte();
		int seqBytes2 = packet.readUnencodedByte();
		int recvMultiplier = packet.readUnencodedByte();
		int sendMultiplier = packet.readUnencodedByte();
		int clientID = packet.readUnencodedShort();

		packetEncoder.setInitialSequenceNumber(seqBytes1, seqBytes2);
		packetEncoder.setEncodeMultiples(sendMultiplier, recvMultiplier);

		Packet responsePacket = new ConnectionAcceptPacket(sendMultiplier, recvMultiplier, (short) clientID);
		sendEncodedPacket(responsePacket);
	}

	private void handleConnectionPlayerPacket(Packet packet) {
		packetEncoder.updateSequenceNumber(packet.readEncodedShort(), packet.readEncodedByte());

		// TODO: check what is sent here
		Packet reply = new Packet(PacketFamily.PACKET_CONNECTION, PacketAction.PACKET_PING);
		reply.writeEncodedByte(106);
		sendEncodedPacket(reply);
	}

	private void handle(Packet packet) {
		int hash = packet.hash();
		PacketHandler internalHandler = internalHandlers.get(hash);

		if (internalHandler != null) {
			internalHandler.handle(packet);
		} else {
			final PacketHandler packetHandler = packetHandlers.get(packet.hash());
			if (packetHandler == null) {
				System.out.println("Unhandled packet: " + packet.getPacketFamilyName() + ", " + packet.getPacketActionName());
			} else {
				Gdx.app.postRunnable(() -> {  // quick fix
					packetHandler.handle(packet);
				});
			}
		}
	}

	private void networkLoop() {
		try {
			socket = new Socket();
//			socket.connect(new InetSocketAddress("localhost", 8078));
			socket.connect(new InetSocketAddress("localhost", 8077));

			bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			isConnecting = false;
			return;
		}

		isConnected = true;
		isConnecting = false;
		if (connectionListener != null)
			connectionListener.onConnect();

		sendPacket(new InitPacket());
		byte[] sizeBuffer = new byte[2];

		while (isConnected()) {
			try {
				read(sizeBuffer);
				int size = decodeNumber(sizeBuffer);
				byte[] buffer = new byte[size];
				read(buffer);

				if (!packetEncoder.isInitialized()) {
					handleInitResponsePacket(new Packet(buffer));
				} else {
					byte[] decodedBuffer = packetEncoder.decode(buffer, size);
					handle(new Packet(decodedBuffer));
				}
			} catch (IOException e) {
				disconnect();
				e.printStackTrace();
				return;
			}
		}
	}
}
