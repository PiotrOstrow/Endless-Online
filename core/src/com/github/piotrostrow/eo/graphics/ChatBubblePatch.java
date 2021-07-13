package com.github.piotrostrow.eo.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.github.piotrostrow.eo.assets.Assets;

public class ChatBubblePatch extends BaseDrawable {

	private final TextureRegion topLeft, topRight, bottomLeft, bottomRight;
	private final TextureRegion top, right, bottom, left;
	private final TextureRegion center;
	private final TextureRegion bottomMiddle;

	public ChatBubblePatch() {
		Texture bubbleTexture = Assets.getTexture("chat_bubble.png");

		topLeft = new TextureRegion(bubbleTexture, 1, 1, 5, 5);
		topRight = new TextureRegion(bubbleTexture, 19, 1, 5, 5);
		bottomLeft = new TextureRegion(bubbleTexture, 1, 19, 5, 5);
		bottomRight = new TextureRegion(bubbleTexture, 19, 19, 5, 5);

		top = new TextureRegion(bubbleTexture, 6, 1, 1, 5);
		left = new TextureRegion(bubbleTexture, 1, 6, 5, 1);

		bottom = new TextureRegion(top);
		bottom.flip(false, true);

		right = new TextureRegion(left);
		right.flip(true, false);

		center = new TextureRegion(bubbleTexture, 6, 6, 1, 1);
		bottomMiddle = new TextureRegion(bubbleTexture, 10, 19, 5, 9);

		setMinWidth(10);
	}

	@Override
	public void draw(Batch batch, float x, float y, float width, float height) {
		if(width % 2 == 0)
			++width;

		batch.draw(topLeft, x - topLeft.getRegionWidth(), y + height);
		batch.draw(topRight, x + width, y + height);
		batch.draw(bottomRight, x + width, y - bottomRight.getRegionHeight());
		batch.draw(bottomLeft, x - bottomRight.getRegionWidth(), y - bottomRight.getRegionWidth());

		batch.draw(top, x, y + height, width, top.getRegionHeight());
		batch.draw(right, x + width, y, right.getRegionWidth(), height);
		batch.draw(left, x - left.getRegionWidth(), y, left.getRegionWidth(), height);
		batch.draw(center, x, y, width, height);

		int bottomSidesWidth = (int) ((width - bottomMiddle.getRegionWidth()) / 2);
		batch.draw(bottom, x, y - bottom.getRegionHeight(), bottomSidesWidth, bottom.getRegionHeight());
		batch.draw(bottomMiddle, x + bottomSidesWidth, y - bottomMiddle.getRegionHeight());
		batch.draw(bottom, x + bottomSidesWidth + bottomMiddle.getRegionWidth(), y - bottom.getRegionHeight(), bottomSidesWidth, bottom.getRegionHeight());
	}
}
