package com.github.piotrostrow.eo.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class DoorMapTile implements MapTile {

	private final TextureRegion closed;
	private final TextureRegion open;

	private long closedBy = 0;

	public DoorMapTile(Texture closed, Texture open) {
		this.closed = new TextureRegion(closed);
		this.open = new TextureRegion(open);
	}
	public DoorMapTile(TextureRegion closed, TextureRegion open) {
		this.closed = closed;
		this.open = open;
	}

	@Override
	public TextureRegion getTextureRegion() {
		return isOpen() ? open : closed;
	}

	@Override
	public void trigger() {
		closedBy = System.currentTimeMillis() + 3000;
	}

	public boolean isOpen() {
		return closedBy > System.currentTimeMillis();
	}
}
