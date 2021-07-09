package com.github.piotrostrow.eo.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.github.piotrostrow.eo.assets.Assets;

public class HitLabel {

	private final TextureRegion[] textureRegions = new TextureRegion[10];

	private final TextureRegion[] redGlyphs = new TextureRegion[10];
	private final TextureRegion miss;

	private int numberOfDigits;
	private long shownAt;

	public HitLabel() {
		Texture atlas = Assets.gfx(2, 158);

		for(int i = 0; i < 10; i++)
			redGlyphs[i] = new TextureRegion(atlas, 40 + i * 9, 28, 8, 11);

		miss = new TextureRegion(atlas, 132, 28, 30, 11);
	}

	public void hit(int amount) {
		shownAt = System.currentTimeMillis();

		if(amount <= 0) {
			numberOfDigits = 1;
			textureRegions[0] = miss;
		} else {
			numberOfDigits = (int) Math.log10(amount) + 1;
			for(int i = numberOfDigits - 1, number = amount; i >= 0; i--, number /= 10) {
				int digit = number % 10;
				textureRegions[i] = redGlyphs[digit];
			}
		}
	}

	public void heal(int amount) {

	}

	public void render(Batch batch, int x, int y) {
		int width = textureRegions[0].getRegionWidth() * numberOfDigits;

		int offsetX = 20 - width / 2;
		int offsetY = 8 + Math.round((System.currentTimeMillis() - shownAt) * 0.0125f);

		for(int i = 0; i < numberOfDigits; i++, offsetX += 8) {
			batch.draw(textureRegions[i], x + offsetX, y + offsetY);
		}
	}
}
