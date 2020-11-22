package com.github.piotrostrow.eo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.GridPoint2;
import com.github.piotrostrow.eo.Main;
import com.github.piotrostrow.eo.character.CharacterState;
import com.github.piotrostrow.eo.character.Direction;
import com.github.piotrostrow.eo.character.PlayerCharacter;
import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.packets.walk.AttackUsePacket;
import com.github.piotrostrow.eo.net.packets.walk.FacePlayerPacket;
import com.github.piotrostrow.eo.net.packets.walk.WalkPlayerPacket;

public class PlayerCharacterController implements InputProcessor {

	/**
	 * The minimum time between turns in ms, may need adjusting, cannot exceed 480
	 */
	private static final int TURN_DELAY = 250;

	private boolean lock = false;

	private final GameScreen gameScreen;
	private final PlayerCharacter player;

	private long lastTurn;
	private CharacterState previousFrameState = CharacterState.IDLE;

	private final GridPoint2 temp = new GridPoint2();

	public PlayerCharacterController(GameScreen gameScreen, PlayerCharacter player) {
		this.gameScreen = gameScreen;
		this.player = player;
	}

	public void update() {
		if(!lock) {
			if (player.getCharacterState() == CharacterState.IDLE) {
				if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) attack();
				else if (Gdx.input.isKeyPressed(Input.Keys.W)) move(Direction.UP);
				else if (Gdx.input.isKeyPressed(Input.Keys.S)) move(Direction.DOWN);
				else if (Gdx.input.isKeyPressed(Input.Keys.A)) move(Direction.LEFT);
				else if (Gdx.input.isKeyPressed(Input.Keys.D)) move(Direction.RIGHT);
			}
		}

		previousFrameState = player.getCharacterState();
	}

	private void attack() {
		player.attack();
		Main.client.sendEncodedPacket(new AttackUsePacket(player.getDirection()));
	}

	private void move(int direction) {
		if(lastTurn + TURN_DELAY > System.currentTimeMillis())
			return;

		if(player.getDirection() != direction && previousFrameState != CharacterState.MOVE) {
			lastTurn = System.currentTimeMillis();
			player.setDirection(direction);
			Main.client.sendEncodedPacket(new FacePlayerPacket(direction));
			return;
		}

		temp.set(player.getPosition());

		switch (direction) {
			case Direction.UP	: temp.y--; break;
			case Direction.DOWN	: temp.y++; break;
			case Direction.LEFT	: temp.x--; break;
			case Direction.RIGHT: temp.x++; break;
		}

		if(!gameScreen.getZone().isBlocked(temp)) {
			player.move(direction);

			Packet packet = new WalkPlayerPacket(direction, player.getPosition().x, player.getPosition().y);
			Main.client.sendEncodedPacket(packet);
		}
	}

	@Override
	public boolean keyDown(int keycode) {


		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	public void lock(boolean lock) {
		this.lock = lock;
	}
}
