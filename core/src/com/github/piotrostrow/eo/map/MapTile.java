package com.github.piotrostrow.eo.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public interface MapTile {

	TextureRegion getTextureRegion();

	/**
	 * Meant for triggering animation on tiles such as doors or spikes
	 */
	default void trigger() {

	}
}
