package com.github.piotrostrow.eo.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.TimeUtils;

public class AnimatedMapTile implements MapTile {

	private Animation<TextureRegion> animation;
	private long lastSync;
	private float animationTime;

	/**
	 * @param texture texture split in 4 horizontally
	 * @param timestamp a timestamp to synchronize the animation across all animated map tiles
	 */
	public AnimatedMapTile(Texture texture, long timestamp) {
		this.lastSync = timestamp;

		TextureRegion[] regions = new TextureRegion[4];
		for(int i = 0; i < 4; i++){
			regions[i] = new TextureRegion(texture, i * texture.getWidth() / 4, 0, texture.getWidth() / 4, texture.getHeight());
		}

		animation = new Animation<TextureRegion>(0.25f, regions);
		animation.setPlayMode(Animation.PlayMode.LOOP);
	}

	@Override
	public TextureRegion getTextureRegion() {
		long current = TimeUtils.millis();
		float delta = (float) (current - lastSync) / 1000f;
		animationTime += delta;
		lastSync = current;
		return animation.getKeyFrame(animationTime);
	}
}
