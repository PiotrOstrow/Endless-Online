package com.github.piotrostrow.eo.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.piotrostrow.eo.Main;
import com.github.piotrostrow.eo.assets.Assets;
import com.github.piotrostrow.eo.character.NonPlayerCharacter;
import com.github.piotrostrow.eo.character.PlayerCharacter;
import com.github.piotrostrow.eo.game.GameScreen;
import com.github.piotrostrow.eo.map.Zone;
import com.github.piotrostrow.eo.net.ConnectionListener;
import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.PacketAction;
import com.github.piotrostrow.eo.net.PacketFamily;
import com.github.piotrostrow.eo.net.constants.AccountReply;
import com.github.piotrostrow.eo.net.constants.CharacterReply;
import com.github.piotrostrow.eo.net.constants.LoginReply;
import com.github.piotrostrow.eo.net.packets.account.AccountCreatePacket;
import com.github.piotrostrow.eo.net.packets.login.WelcomeMsgPacket;
import com.github.piotrostrow.eo.net.structs.LoginScreenCharacterData;
import com.github.piotrostrow.eo.net.structs.NpcData;
import com.github.piotrostrow.eo.net.structs.PlayerData;
import com.github.piotrostrow.eo.ui.actors.Dialog;
import com.github.piotrostrow.eo.ui.actors.RegisterWindow;
import com.github.piotrostrow.eo.ui.stages.CharacterSelectStage;
import com.github.piotrostrow.eo.ui.stages.MainMenuStage;

import java.util.ArrayList;

public class MainMenuScreen implements Screen, ConnectionListener {

	private Stage currentStage;

	private final MainMenuStage mainMenuStage;
	private final CharacterSelectStage characterSelectStage;

	private final MainMenuBackground background;

	private Packet welcomeReplyPacket1;

	public MainMenuScreen() {
		background = new MainMenuBackground();
		mainMenuStage = new MainMenuStage();
		characterSelectStage = new CharacterSelectStage();

		Main.client.setConnectionListener(this);

		setStage(mainMenuStage);
	}

	@Override
	public void onConnect() {
		mainMenuStage.connected();
		Main.client.registerPacketHandler(PacketFamily.PACKET_LOGIN, PacketAction.PACKET_REPLY, this::handleLoginReplyPacket);
		Main.client.registerPacketHandler(PacketFamily.PACKET_WELCOME, PacketAction.PACKET_REPLY, this::handleWelcomeReplyPacket);
		Main.client.registerPacketHandler(PacketFamily.PACKET_ACCOUNT, PacketAction.PACKET_REPLY, this::handleAccountReplyPacket);
		Main.client.registerPacketHandler(PacketFamily.PACKET_CHARACTER, PacketAction.PACKET_REPLY, this::handleCharacterReplyPacket);
	}

	@Override
	public void onDisconnect() {
		mainMenuStage.disconnected();
		setStage(mainMenuStage);
	}

	private void handleCharacterReplyPacket(Packet packet) {
		int replyCode = packet.readEncodedShort();
		switch(replyCode) {
			case CharacterReply.CHARACTER_OK:
				int charCount = packet.readEncodedByte();
				LoginScreenCharacterData[] loginScreenCharacterData = new LoginScreenCharacterData[charCount];

				packet.skip(2); // hard coded 0x01FF

				for(int i = 0; i < loginScreenCharacterData.length; i++)
					loginScreenCharacterData[i] = new LoginScreenCharacterData(packet);

				characterSelectStage.setCharacters(loginScreenCharacterData);
				characterSelectStage.hideCreateCharacterWindow();
				break;
			default: // continue case, packet received after sending character request packet when clicking on create
				System.err.println("Character reply code: " + replyCode);
				break;
		}
	}

	private void handleAccountReplyPacket(Packet packet) {
		int accountReply = packet.readEncodedShort();

		switch(accountReply) {
			case AccountReply.ACCOUNT_EXISTS:
				Dialog.display("Already exists", "The account name you provided already exists in our database.", currentStage);
				break;
			case AccountReply.ACCOUNT_NOT_APPROVED:
				Dialog.display("Not approved", "The account name you provided is not approved, try another name.", currentStage);
				break;
			case AccountReply.ACCOUNT_CHANGE_FAILED:
			case AccountReply.ACCOUNT_CHANGED:
				// TODO: dialog
				System.err.println("Error code: " + accountReply);
				break;
			case AccountReply.ACCOUNT_CREATED:
				Dialog.display("Welcome", "Use your new account name and password to login to the game.", currentStage);
				break;
			// using default together with continue here because the value is uncertain (based on eoserv source)
			case AccountReply.ACCOUNT_CONTINUE:
			default:
				if(packet.getSize() == 7) {
					int seqStart = packet.readEncodedByte();
					Main.client.getPacketEncoder().updateInitlaSequenceNumberAfterAccountReply(seqStart);
				}
				String reply = packet.readFixedString(2);
				if(reply.equalsIgnoreCase("OK")) {

					String title = "Account accepted";
					String message = "Please wait a few minutes for creation.";
					Dialog.displayWithProgressBar(title, message, 5000, currentStage, completed -> {
						if(completed) {
							RegisterWindow registerWindow = mainMenuStage.getRegisterWindow();
							String username = registerWindow.getUsername();
							String password = registerWindow.getPassword();
							String email = registerWindow.getEmail();
							Main.client.sendEncodedPacket(new AccountCreatePacket(username, password, email));

							registerWindow.clearTextFields();
						}
					});
				} else {
					// TODO: show dialog
				}
				break;
		}
	}

	private void handleLoginReplyPacket(Packet packet) {
		int replyCode = packet.readEncodedShort();

		//TODO: handle errors
		switch (replyCode) {
			case LoginReply.LOGIN_OK:
				int charCount = packet.readEncodedByte();
				LoginScreenCharacterData[] characters = new LoginScreenCharacterData[charCount];

				packet.skip(2); // 2 hardcoded bytes

				for(int i = 0; i < characters.length; i++) {
					characters[i] = new LoginScreenCharacterData(packet);
					packet.skip(1); // hard coded byte
				}

				characterSelectStage.setCharacters(characters);
				setStage(characterSelectStage);
				break;
			default:
				System.err.println("Login error code: " + replyCode);
				break;
		}
	}

	private void handleWelcomeReplyPacket(Packet packet) {
		int subID = packet.readEncodedShort();
		if(subID == 1){
			welcomeReplyPacket1 = packet;
			int characterID = packet.readEncodedShort();
			Main.client.sendEncodedPacket(new WelcomeMsgPacket(characterID));
		} else if(subID == 2) {
			int mapID = welcomeReplyPacket1.readEncodedShort(10);
			int playerID = welcomeReplyPacket1.readEncodedShort(4);

			packet.skip(1); // hardcoded byte
			for(int i = 0; i < 9; i++)
				packet.readBreakString();

			int weight = packet.readEncodedByte();
			int maxWeight = packet.readEncodedByte();

			while(!packet.peekAndSkipUnencodedByte()){
				int itemID = packet.readEncodedShort();
				int amount = packet.readEncodedInt();
			}

			while(!packet.peekAndSkipUnencodedByte()){
				int spellID = packet.readEncodedShort();
				int spellLevel = packet.readEncodedShort();
			}

			PlayerData[] characters = new PlayerData[packet.readEncodedByte()];
			packet.skip(1); 	// hard coded 0xFF byte

			for(int i = 0; i < characters.length; i++)
				characters[i] = new PlayerData(packet);

			ArrayList<NpcData> npcs = new ArrayList<>();
			while(!packet.peekAndSkipUnencodedByte())
				npcs.add(new NpcData(packet));

			Zone zone = new Zone(Assets.getMap(mapID));
			PlayerCharacter player = null;

			for(PlayerData character : characters){
				PlayerCharacter characterEntity = new PlayerCharacter(character);
				zone.addPlayer(characterEntity);

				if(character.playerID == playerID)
					player = characterEntity;
			}

			if(player == null)
				throw new RuntimeException("own player object has not been initialized");

			for(NpcData npcData : npcs) {
				NonPlayerCharacter npc = new NonPlayerCharacter(npcData);
				zone.addNpc(npc);
			}

			Main.instance.setScreen(new GameScreen(zone, player));
		} else {
			throw new RuntimeException("Unknown welcome reply packet subID: " + subID);
		}
	}

	private void setStage(Stage stage) {
		this.currentStage = stage;
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		background.update();
		currentStage.act(Gdx.graphics.getDeltaTime());

		background.render();
		currentStage.draw();
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		background.dispose();
		characterSelectStage.dispose();
	}
}
