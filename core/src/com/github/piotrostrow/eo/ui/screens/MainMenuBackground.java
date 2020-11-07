package com.github.piotrostrow.eo.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.piotrostrow.eo.assets.Assets;
import com.github.piotrostrow.eo.map.emf.EmfMapRenderer;
import com.github.piotrostrow.eo.shaders.BlurGfxShader;
import com.github.piotrostrow.eo.shaders.GfxShader;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public class MainMenuBackground implements Disposable {

	private final SpriteBatch batch;
	private final FrameBuffer blurTargetA, blurTargetB;
	private final ShaderProgram blurShader;
	private final ShaderProgram gfxShader;

	private final EmfMapRenderer mapRenderer;

	private final Vector2 position = new Vector2();
	private final Vector2 temp = new Vector2();

	private boolean moving = true;
	private boolean blur = true;

	public MainMenuBackground() {
		mapRenderer = new EmfMapRenderer(Assets.getMap(222));

		batch = new SpriteBatch();
		blurShader = new BlurGfxShader();
		gfxShader = new GfxShader();

		blurTargetA = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		blurTargetB = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		blurTargetA.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		blurTargetB.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

		blurShader.bind();
		blurShader.setUniformf("dir", 0f, 0f);
		blurShader.setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public void update() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.F10))
			moving = !moving;
		if (Gdx.input.isKeyJustPressed(Input.Keys.F9))
			blur = !blur;

		float mouseX = Gdx.input.getX() * 0.25f;
		float mouseY = -Gdx.input.getY() * 0.25f;

		temp.set(mouseX, mouseY).sub(position).scl(Gdx.graphics.getDeltaTime() * 5);
		position.add(temp);

		if (moving) {
			double time = TimeUtils.millis();
			time /= 10000;
			float x = (float) Math.sin(time) * 1000;
			float y = (float) Math.cos(time) * 1000;

			mapRenderer.camera.position.set(position.x + 4300 + x, position.y + 425 + y, 0);
		} else {
			mapRenderer.camera.position.set(position.x + 3050, position.y + 425, 0);
		}

		mapRenderer.camera.update();
	}

	public void render() {
		if (!blur) {
			mapRenderer.render();
		} else {
			float blur = 1.5f;
			int iterations = 8;
			blurTargetA.begin();

			Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1f);
			Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

			batch.setShader(gfxShader);
			batch.begin();
			batch.setProjectionMatrix(mapRenderer.camera.combined);
			mapRenderer.render();
			batch.flush();
			blurTargetA.end();

			FrameBuffer writeBuffer = blurTargetB;
			FrameBuffer readBuffer = blurTargetA;

			float blurX = blur;
			float blurY = 0;

			batch.setShader(blurShader);
			batch.setProjectionMatrix(mapRenderer.camera.projection);
			for (int i = 0; i < iterations; i++) {
				blurShader.setUniformf("dir", blurX, blurY);

				writeBuffer.begin();
				batch.draw(readBuffer.getColorBufferTexture(), -Gdx.graphics.getWidth() / 2, -Gdx.graphics.getHeight() / 2);
				batch.flush();
				writeBuffer.end();

				//swap
				FrameBuffer temp = writeBuffer;
				writeBuffer = readBuffer;
				readBuffer = temp;

				//swap
				float tempBlur = blurX;
				blurX = blurY;
				blurY = tempBlur;
			}

			blurShader.setUniformf("dir", 0, 0);
			batch.setProjectionMatrix(mapRenderer.camera.projection);
			Texture tex = blurTargetA.getColorBufferTexture();
			batch.draw(tex, -tex.getWidth() / 2, -tex.getHeight() / 2);
			batch.end();
		}
	}

	@Override
	public void dispose() {
		mapRenderer.dispose();

		blurShader.dispose();
		gfxShader.dispose();

		blurTargetA.dispose();
		blurTargetB.dispose();
	}
}
