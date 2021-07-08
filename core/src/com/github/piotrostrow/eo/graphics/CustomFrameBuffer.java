package com.github.piotrostrow.eo.graphics;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

/**
 * Overrides the disposing code of the superclass to not dispose of the color texture.
 */
public class CustomFrameBuffer extends FrameBuffer {

	public CustomFrameBuffer(Pixmap.Format format, int width, int height) {
		super(format, width, height, false);

		getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
	}

	@Override
	protected void disposeColorTexture(Texture colorTexture) {

	}
}
