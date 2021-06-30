package com.github.piotrostrow.eo.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.github.piotrostrow.eo.assets.Assets;

public class MapCursor {

	public enum CursorType { // TODO: change naming on these to reflect what they actually represent?
		ORANGE, WHITE, YELLOW
	}

	/**
	 * Amount of time a click animation takes in milliseconds TODO: check and adjust
	 */
	public static int ANIMATION_TIME = 650;

	private final GridPoint2 position = new GridPoint2();
	private final GridPoint2 clickPosition = new GridPoint2();

	private final TextureRegion[] cursors;
	private TextureRegion currentTextureRegion;

	private long clickedTimestamp = 0;

	public MapCursor() {
		Texture texture = Assets.gfx(2, 124);

		cursors = new TextureRegion[5];
		for(int i = 0; i < cursors.length; i++)
			cursors[i] = new TextureRegion(texture, texture.getWidth() / 5 * i, 0, texture.getWidth() / 5, texture.getHeight());

		currentTextureRegion = cursors[0];
	}

	public void setCursorType(CursorType cursorType) {
		switch(cursorType) {
			case ORANGE: 	currentTextureRegion = cursors[0]; break;
			case WHITE: 	currentTextureRegion = cursors[1]; break;
			case YELLOW:	currentTextureRegion = cursors[2]; break;
		}
	}

	public void clickAnimation(int x, int y) {
		clickPosition.set(x, y);
		clickedTimestamp = System.currentTimeMillis();
	}

	public GridPoint2 getPosition() {
		return position;
	}

	public GridPoint2 getClickPosition() {
		return clickPosition;
	}

	public TextureRegion getTextureRegion() {
		return currentTextureRegion;
	}

	public float getAnimationProgress() {
		return (float) (System.currentTimeMillis() - clickedTimestamp) / ANIMATION_TIME;
	}

	public TextureRegion getClickTextureRegion() {
		int delta = (int) (System.currentTimeMillis() - clickedTimestamp);
		if(delta > ANIMATION_TIME)
			return null;
		if(delta < ANIMATION_TIME / 2)
			return cursors[3];
		return cursors[4];
	}
}
