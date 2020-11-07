package com.github.piotrostrow.eo.graphics;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;

/**
 * Overrides the disposing code of the superclass to not dispose of the color texture.
 */
public class CustomFrameBuffer extends FrameBuffer {

	protected CustomFrameBuffer(GLFrameBufferBuilder<? extends GLFrameBuffer<Texture>> bufferBuilder) {
		super(bufferBuilder);
	}

	public CustomFrameBuffer(Pixmap.Format format, int width, int height, boolean hasDepth) {
		super(format, width, height, hasDepth);
	}

	public CustomFrameBuffer(Pixmap.Format format, int width, int height, boolean hasDepth, boolean hasStencil) {
		super(format, width, height, hasDepth, hasStencil);
	}

	@Override
	protected void disposeColorTexture(Texture colorTexture) {

	}
}
