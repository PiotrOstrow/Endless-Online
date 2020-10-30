package com.github.piotrostrow.eo.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class GfxShader extends ShaderProgram {

	public GfxShader() {
		super(Gdx.files.internal("shaders/gfx_shader.vert"), Gdx.files.internal("shaders/gfx_shader.frag"));
	}
}
