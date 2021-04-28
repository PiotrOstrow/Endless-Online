package com.github.piotrostrow.eo.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public abstract class CharacterEntity implements Disposable, Comparable<CharacterEntity> {

	/**
	 * Duration of attack animation in milliseconds
	 */
	public static int ATTACK_SPEED = 600;

	/**
	 * Duration of a single idle frame for animation NPCs that have idle animation
	 */
	public int IDLE_FRAME_TIME = 200;

	/**
	 * Actual position of the character - the actual grid position that the character occupies
	 */
	protected final GridPoint2 position = new GridPoint2();

	/**
	 * Rendering position of the character, only differs when the character is moving, then the position is the previous
	 * position that the character has been in
	 */
	protected final GridPoint2 renderingPosition = new GridPoint2();

	/**
	 * Rendering offset added to renderingPosition when the character is moving
	 */
	private final Vector2 movePositionOffset = new Vector2();

	private int direction;

	private float animationTimer;

	protected CharacterState characterState = CharacterState.IDLE;

	/**
	 * Timestamp to keep track of movement and have the character that moved last be on top when stacked with another
	 */
	private long lastMoved;

	CharacterEntity(int x, int y, int direction){
		this.position.set(x, y);
		this.renderingPosition.set(x, y);
		this.direction = direction;
	}

	public abstract int getXOffset(int direction);

	public abstract int getYOffset(int direction);

	protected abstract TextureRegion getTextureRegion(CharacterState characterState, int direction, int frame);

	// intended for refresh packets
	protected void setPosition(int x, int y) {
		position.set(x, y);
		renderingPosition.set(x, y);
		movePositionOffset.set(0, 0);
		characterState = CharacterState.IDLE;
	}

	public void attack() {
		characterState = CharacterState.ATTACK_MELEE;
		animationTimer = 0;
	}

	public void move(int direction) {
		renderingPosition.set(position);

		switch (direction) {
			case Direction.UP	: getPosition().y--; break;
			case Direction.DOWN	: getPosition().y++; break;
			case Direction.LEFT	: getPosition().x--; break;
			case Direction.RIGHT: getPosition().x++; break;
		}

		this.direction = direction;
		this.lastMoved = System.currentTimeMillis();
		this.characterState = CharacterState.MOVE;
		this.animationTimer = 0;
	}

	public void move(int direction, int destX, int destY){
		// if character is not in a grid position that's right next to the destination, the position first has to be adjusted
		if(position.dst2(destX, destY) > 1) {
			switch (direction) {
				case Direction.UP	: setPosition(destX, destY + 1); break;
				case Direction.DOWN	: setPosition(destX, destY - 1); break;
				case Direction.LEFT	: setPosition(destX + 1, destY); break;
				case Direction.RIGHT: setPosition(destX - 1, destY); break;
			}
		}

		movePositionOffset.set(0, 0);
		move(direction);
	}

	public void update(){
		animationTimer += Gdx.graphics.getDeltaTime();

		// TODO: adjust timing, so that every packet sends a timestamp with 480 delta ?
		switch(characterState){
			case MOVE:
				// one move animation takes 480 ms, having to traverse 16 pixels means 16 / 0.48 = 33.3333 pixels per second
				float movementSpeed = 100.0f / 3f;
				switch (direction) {
					case Direction.UP:
						movePositionOffset.y += movementSpeed * Gdx.graphics.getDeltaTime();
						movePositionOffset.x += movementSpeed * Gdx.graphics.getDeltaTime() * 2;
						break;
					case Direction.DOWN:
						movePositionOffset.y -= movementSpeed * Gdx.graphics.getDeltaTime();
						movePositionOffset.x -= movementSpeed * Gdx.graphics.getDeltaTime() * 2;
						break;
					case Direction.LEFT:
						movePositionOffset.x -= movementSpeed * Gdx.graphics.getDeltaTime() * 2;
						movePositionOffset.y += movementSpeed * Gdx.graphics.getDeltaTime();
						break;
					case Direction.RIGHT:
						movePositionOffset.x += movementSpeed * Gdx.graphics.getDeltaTime() * 2;
						movePositionOffset.y -= movementSpeed * Gdx.graphics.getDeltaTime();
						break;
				}

				if(Math.abs(movePositionOffset.x) >= 32.0f) {
					characterState = CharacterState.IDLE;
					movePositionOffset.set(0, 0);
					renderingPosition.set(position);
				}
				break;
			case ATTACK_MELEE:
			case ATTACK_RANGE:
				if(animationTimer * 1000 >= ATTACK_SPEED)
					characterState = CharacterState.IDLE;

				break;
		}
	}

	public final void render(SpriteBatch batch, float x, float y){
		TextureRegion textureRegion;
		switch(characterState) {
			case MOVE:
				textureRegion = getTextureRegion(CharacterState.MOVE, direction, (int) (Math.abs(movePositionOffset.x) / 32 * 4));
				break;
			case ATTACK_MELEE:
			case ATTACK_RANGE:
				float oneFourth = ATTACK_SPEED / 4000f;
				if(animationTimer > oneFourth * 3)
					textureRegion = getTextureRegion(CharacterState.IDLE, direction, 0);
				else if(animationTimer > oneFourth)
					textureRegion = getTextureRegion(characterState, direction, 1);
				else
					textureRegion = getTextureRegion(characterState, direction, 0);
				break;
			default:
				int frame = (int) (animationTimer * 1000f / IDLE_FRAME_TIME)  % 2;
				textureRegion = getTextureRegion(CharacterState.IDLE, direction, frame);
				break;
		}

		float xOffset = getXOffset(direction) + (32 - textureRegion.getRegionWidth() / 2f);
		float yOffset = getYOffset(direction);

		batch.draw(textureRegion, x + xOffset + movePositionOffset.x, y + yOffset + movePositionOffset.y);
	}

	public CharacterState getCharacterState() {
		return characterState;
	}

	public GridPoint2 getPosition() {
		return position;
	}

	public Vector2 getMovePositionOffset() {
		return movePositionOffset;
	}

	public GridPoint2 getRenderingGridPosition() {
		return renderingPosition;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getDirection() {
		return direction;
	}

	@Override
	public void dispose() {
		// Overridden in player class to dispose of PlayerTextureAtlas
	}

	@Override
	public int compareTo(CharacterEntity o) {
		GridPoint2 p1 = getRenderingGridPosition();
		GridPoint2 p2 = o.getRenderingGridPosition();

		if(p1.x > p2.x){
			return 1;
		} else if (p1.x == p2.x){
			if(p1.y < p2.y) {
				return -1;
			} else if (p1.y > p2.y) {
				return 1;
			} else if (lastMoved > o.lastMoved) {
				return 1;
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}
}
