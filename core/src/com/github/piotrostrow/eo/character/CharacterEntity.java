package com.github.piotrostrow.eo.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.github.piotrostrow.eo.util.PacketTimestamp;

public abstract class CharacterEntity implements Disposable, Comparable<CharacterEntity> {

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
	public abstract int getID();

	protected abstract TextureRegion getTextureRegion(CharacterState characterState, int direction, int frame);

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
		System.out.println(position.dst2(destX, destY));
		if(position.dst2(destX, destY) > 1) {
			switch (direction) {
				case Direction.UP	: position.set(destX, destY + 1); break;
				case Direction.DOWN	: position.set(destX, destY - 1); break;
				case Direction.LEFT	: position.set(destX + 1, destY); break;
				case Direction.RIGHT: position.set(destX - 1, destY); break;
			}
		}

		movePositionOffset.set(0, 0);
		move(direction);
	}

	public final void update(){
		animationTimer += Gdx.graphics.getDeltaTime();

		// TODO: adjust timing, so that every packet sends a timestamp with 480 delta ?
		if(characterState == CharacterState.MOVE){
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
		}
	}

	public final void render(SpriteBatch batch, float x, float y){
		int frame = 0;
		switch(characterState) {
			case MOVE:
				frame = (int) (Math.abs(movePositionOffset.x) / 32 * 4);
				break;
		}


		TextureRegion textureRegion = getTextureRegion(characterState, direction, frame);

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

	public GridPoint2 getRenderingGridPosition() {
		return renderingPosition;
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
				return 1;
			} else if (p1.y > p2.y) {
				return -1;
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
