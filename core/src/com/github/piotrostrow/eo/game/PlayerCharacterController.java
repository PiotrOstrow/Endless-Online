package com.github.piotrostrow.eo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.GridPoint2;
import com.github.piotrostrow.eo.Main;
import com.github.piotrostrow.eo.character.CharacterState;
import com.github.piotrostrow.eo.character.Direction;
import com.github.piotrostrow.eo.character.PlayerCharacter;
import com.github.piotrostrow.eo.map.pathfinder.AStarPathFinder;
import com.github.piotrostrow.eo.map.pathfinder.PathFinder;
import com.github.piotrostrow.eo.net.Packet;
import com.github.piotrostrow.eo.net.packets.walk.AttackUsePacket;
import com.github.piotrostrow.eo.net.packets.walk.FacePlayerPacket;
import com.github.piotrostrow.eo.net.packets.walk.WalkPlayerPacket;

import java.util.LinkedList;
import java.util.Queue;

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

	private final boolean[] pressedKeys = new boolean[256];

	private final GridPoint2 temp = new GridPoint2();

	private final PathFinder pathFinder;

	/**
	 * If true, the path is recalculated when the next grid point that was previously open is now blocked
	 */
	private boolean recalculatePath = true;

	private boolean isFollowingPath;
	private Queue<GridPoint2> path = new LinkedList<>();
	private GridPoint2 pathGoal = new GridPoint2();

	public PlayerCharacterController(GameScreen gameScreen, PlayerCharacter player) {
		this.gameScreen = gameScreen;
		this.player = player;
		this.pathFinder = new AStarPathFinder();
	}

	public void update() {
		if (!lock && player.getCharacterState() == CharacterState.IDLE) {
			// any input breaks path following
			boolean wasFollowingPath = isFollowingPath;
			isFollowingPath = false;

			if (pressedKeys[Input.Keys.CONTROL_LEFT] && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) attack();
			else if (pressedKeys[Input.Keys.W] && Gdx.input.isKeyPressed(Input.Keys.W)) move(Direction.UP);
			else if (pressedKeys[Input.Keys.S] && Gdx.input.isKeyPressed(Input.Keys.S)) move(Direction.DOWN);
			else if (pressedKeys[Input.Keys.A] && Gdx.input.isKeyPressed(Input.Keys.A)) move(Direction.LEFT);
			else if (pressedKeys[Input.Keys.D] && Gdx.input.isKeyPressed(Input.Keys.D)) move(Direction.RIGHT);
			else if(wasFollowingPath) isFollowingPath = true;
		}

		if(path.size() == 0)
			isFollowingPath = false;

		if (isFollowingPath && player.getCharacterState() == CharacterState.IDLE) {
			GridPoint2 from = player.getPosition();
			GridPoint2 to = path.poll();

			if(from.x - to.x < 0) move(Direction.RIGHT);
			if(from.x - to.x > 0) move(Direction.LEFT);
			if(from.y - to.y < 0) move(Direction.DOWN);
			if(from.y - to.y > 0) move(Direction.UP);
		}

		previousFrameState = player.getCharacterState();
	}

	public void goTo(int mouseMapX, int mouseMapY) {
		goTo(new GridPoint2(mouseMapX, mouseMapY));
	}

	public void goTo(GridPoint2 point) {
		pathGoal = point;
		path = pathFinder.getPath(gameScreen.getZone(), player.getPosition(), point);

		if(path.size() > 0)
			isFollowingPath = true;
	}

	private void attack() {
		player.attack();
		Main.client.sendEncodedPacket(new AttackUsePacket(player.getDirection()));
	}

	private void move(int direction) {
		if(!isFollowingPath) {
			if (lastTurn + TURN_DELAY > System.currentTimeMillis())
				return;

			if (player.getDirection() != direction && previousFrameState != CharacterState.MOVE) {
				lastTurn = System.currentTimeMillis();
				player.setDirection(direction);
				Main.client.sendEncodedPacket(new FacePlayerPacket(direction));
				return;
			}
		}

		temp.set(player.getPosition());

		switch (direction) {
			case Direction.UP	: temp.y--; break;
			case Direction.DOWN	: temp.y++; break;
			case Direction.LEFT	: temp.x--; break;
			case Direction.RIGHT: temp.x++; break;
		}

		if(gameScreen.getZone().isBlocked(temp)) {
			if(isFollowingPath) {
				isFollowingPath = false;
				if(recalculatePath)
					goTo(pathGoal);
			}

			return;
		}

		player.move(direction);

		Packet packet = new WalkPlayerPacket(direction, player.getPosition().x, player.getPosition().y);
		Main.client.sendEncodedPacket(packet);
	}

	@Override
	public boolean keyDown(int keycode) {
		pressedKeys[keycode] = true;
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		pressedKeys[keycode] = false;
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
		this.isFollowingPath = false;
	}
}
