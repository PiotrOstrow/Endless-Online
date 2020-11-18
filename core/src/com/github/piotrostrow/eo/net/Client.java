package com.github.piotrostrow.eo.net;

import com.badlogic.gdx.Gdx;
import com.github.piotrostrow.eo.net.packets.connection.ConnectionAcceptPacket;
import com.github.piotrostrow.eo.net.packets.connection.ConnectionPlayerPacket;
import com.github.piotrostrow.eo.net.packets.init.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import static com.github.piotrostrow.eo.util.NumberEncoder.*;

public class Client {

	public static final int MAX_BUFFER_SIZE = 8192;

	private Socket socket;
	private BufferedOutputStream bufferedOutputStream;

	private PacketEncoder packetEncoder;

	private ConnectionListener connectionListener;

	private boolean isConnected;
	private boolean isConnecting;

	public Client() {
		packetEncoder = new PacketEncoder();
	}

	public void setConnectionListener(ConnectionListener connectionListener) {
		this.connectionListener = connectionListener;
	}

	public void connect() {
		if(!isConnected() && !isConnecting()) {
			isConnecting = true;
			Thread thread = new Thread(this::networkLoop);
			thread.start();
		}
	}

	public void disconnect() {
		if(isConnected() && socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				isConnected = false;
				packetEncoder = new PacketEncoder();
				if(connectionListener != null)
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

	private void sendData(byte[] buffer, int length) throws IOException{
		bufferedOutputStream.write(encodeNumber(length, 2));
		bufferedOutputStream.write(buffer, 0, length);
		bufferedOutputStream.flush();
	}

	private void handleInitResponsePacket(InitResponsePacket packet){
		if(packet.getPacketActionByte() != PacketAction.PACKET_A_INIT || packet.getPacketFamilyByte() != PacketFamily.PACKET_F_INIT) {
			throw new RuntimeException("Received packet other than InitResponsePacket before packet encoder was initialized. " +
					" Packet action: " + packet.getPacketActionName() + ", packet family: " + packet.getPacketFamilyName());
		}

		packetEncoder.setInitialSequenceNumber(packet.getSeqBytes1(), packet.getSeqBytes2());
		packetEncoder.setEncodeMultiples(packet.getSendMultiplier(), packet.getReceiveMultiplier());

		Packet responsePacket = new ConnectionAcceptPacket(packet);
		sendEncodedPacket(responsePacket);
	}

	private void read(byte[] buffer) throws IOException {
		int read = socket.getInputStream().read(buffer);
		if(read < 0)
			throw new SocketException("Connection has been closed");
	}

	private boolean shouldHandle(Packet packet) {
		if(packet.equals(PacketFamily.PACKET_CONNECTION, PacketAction.PACKET_PLAYER))
			return true;

		return false;
	}

	private void handle(Packet packet) {
		if(packet instanceof ConnectionPlayerPacket) {
			ConnectionPlayerPacket connectionPlayerPacket = (ConnectionPlayerPacket) packet;
			packetEncoder.updateSequenceNumber(connectionPlayerPacket.getSeqBytes1(), connectionPlayerPacket.getSeqBytes2());

			// TODO: check what is sent here
			Packet reply = new Packet(PacketFamily.PACKET_CONNECTION, PacketAction.PACKET_PING);
			reply.writeEncodedByte(106);
			sendEncodedPacket(reply);
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
		if(connectionListener != null)
			connectionListener.onConnect();

		sendPacket(new InitPacket());
		byte[] sizeBuffer = new byte[2];

		while (isConnected()) {
			try {
				read(sizeBuffer);
				int size = decodeNumber(sizeBuffer);
				byte[] buffer = new byte[size];
				read(buffer);

				if(!packetEncoder.isInitialized()){
					InitResponsePacket initResponsePacket = new InitResponsePacket(buffer);
					handleInitResponsePacket(initResponsePacket);
				} else {
					byte[] output = packetEncoder.decode(buffer, size);
					Packet packet = PacketFactory.serverToClient(output);

					if(shouldHandle(packet))
						handle(packet);
					else if (connectionListener != null)
						Gdx.app.postRunnable(() -> connectionListener.handlePacket(packet)); // quick fix
				}
			} catch (IOException e) {
				disconnect();
				e.printStackTrace();
				return;
			}
		}
	}
}
