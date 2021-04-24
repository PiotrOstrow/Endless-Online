package com.github.piotrostrow.eo.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class BlurGfxShader extends ShaderProgram {
	public BlurGfxShader() {
		super(Gdx.files.internal("shaders/blur.vert"), Gdx.files.internal("shaders/blur.frag"));
	}
}
