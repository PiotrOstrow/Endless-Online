package com.github.piotrostrow.eo.character;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Disposable;

public abstract class CharacterEntity implements Disposable, Comparable<CharacterEntity> {

	protected final GridPoint2 position = new GridPoint2();
	protected int direction;

	protected CharacterState characterState = CharacterState.IDLE;

	public abstract void update();

	public abstract void render(SpriteBatch batch, float x, float y);

	public abstract int getID();

	public void move(int direction, int x, int y){
		position.set(x, y);
		this.direction = direction;
	}

	public GridPoint2 getPosition() {
		return position;
	}

	public GridPoint2 getRenderingGridPosition() {
		return position;
	}

	@Override
	public void dispose() {
		// Overridden in player class to dispose of PlayerTextureAtlas
	}

	@Override
	public int compareTo(CharacterEntity o) {
		// Sort for rendering order
		return 0;
	}
}
