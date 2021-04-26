package com.github.piotrostrow.eo.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Mesh.VertexDataType;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;

import java.nio.IntBuffer;
import java.util.ArrayList;

public class ZBufferSpriteBatch implements Disposable{

	/**
	 * Number of primitives per single vertex
	 */
	private static final int VERTEX_N_COMPOMNENTS = 6;

	private boolean isDrawing;
	public int renderCalls = 0;

	private final float[] vertices;
	private int index = 0;

	private final Mesh mesh;
	private final ShaderProgram shader;

	private final ArrayList<Texture> textures;

	private final int maxTextureUnits;

	/**
	 * This is actually a projection matrix multiplied with the view matrix, but since the interface that i may
	 * want to implement later is defined as it is, i'll stick with that
	 */
	private Matrix4 projectionMatrix;

	public ZBufferSpriteBatch() {
		int size = 4096;
		mesh = new Mesh(VertexDataType.VertexArray, false, size * 4, size * 6,
				new VertexAttribute(Usage.Position, 3, "position"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "texCoords"),
				new VertexAttribute(Usage.Generic, 1, "textureSamplerID"));

		shader = new ShaderProgram(Gdx.files.internal("shaders/ZBufferRendererShader.vert"), Gdx.files.internal("shaders/ZBufferRendererShader.frag"));

		shader.bind();

		for (int i = 0; i < 32; i++)
			shader.setUniformi("textures[" + i + "]", i);

		vertices = new float[size * VERTEX_N_COMPOMNENTS * 4];

		int len = size * 6;
		short[] indices = new short[len];
		short j = 0;
		for (int i = 0; i < len; i += 6, j += 4) {
			indices[i] = j;
			indices[i + 1] = (short)(j + 1);
			indices[i + 2] = (short)(j + 2);
			indices[i + 3] = (short)(j + 2);
			indices[i + 4] = (short)(j + 3);
			indices[i + 5] = j;
		}
		mesh.setIndices(indices);

		IntBuffer intBuffer = BufferUtils.newIntBuffer(16);
		Gdx.gl.glGetIntegerv(GL20.GL_MAX_TEXTURE_IMAGE_UNITS, intBuffer);
		maxTextureUnits = Math.min(1, intBuffer.get(0));

		textures = new ArrayList<>(maxTextureUnits);
	}

	public void begin() {
		if(isDrawing)
			throw new IllegalStateException("end must be called before begin is called again");

		isDrawing = true;
		renderCalls = 0;

		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glDepthMask(true);
		Gdx.gl.glDepthFunc(GL20.GL_LESS);

		shader.bind();
		shader.setUniformMatrix("projection", projectionMatrix);
	}

	public void end() {
		if(!isDrawing)
			throw new IllegalStateException("begin was not called");

		if(index > 0)
			flush();

		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

		isDrawing = false;
		System.out.println("Render calls: " + renderCalls);
	}

	private int findTextureIndex(Texture texture) {
		for(int i = 0; i < textures.size(); i++)
			if(textures.get(i) == texture)
				return i;

		if(textures.size() >= maxTextureUnits)
			flush();

		int unit = textures.size();
		texture.bind(unit);

		textures.add(texture);

		return unit;
	}

	public void draw(TextureRegion region, float x, float y) {
		// finds or assigns an id to the texture, and flushes if necessary
		float textureID = findTextureIndex(region.getTexture());

		float z =  x / 4 - y; //-50 - (x - y) / 200;
		float width = region.getRegionWidth();
		float height = region.getRegionHeight();

		vertices[index] 	= x;
		vertices[index + 1] = y;
		vertices[index + 2] = z;
		vertices[index + 3] = region.getU();
		vertices[index + 4] = region.getV2();
		vertices[index + 5] = textureID;

		vertices[index + 6] = x;
		vertices[index + 7] = y + height;
		vertices[index + 8] = z;
		vertices[index + 9] = region.getU();
		vertices[index + 10] = region.getV();
		vertices[index + 11] = textureID;

		vertices[index + 12] = x + width;
		vertices[index + 13] = y + height;
		vertices[index + 14] = z;
		vertices[index + 15] = region.getU2();
		vertices[index + 16] = region.getV();
		vertices[index + 17] = textureID;

		vertices[index + 18] = x + width;
		vertices[index + 19] = y;
		vertices[index + 20] = z;
		vertices[index + 21] = region.getU2();
		vertices[index + 22] = region.getV2();
		vertices[index + 23] = textureID;

		index += VERTEX_N_COMPOMNENTS * 4;
	}

	public void flush() {
		if(index == 0)
			return;

		int count = index / (VERTEX_N_COMPOMNENTS * 4) * 6;
		//count = mesh.getIndicesBuffer().capacity();

		mesh.setVertices(vertices, 0, index);
		mesh.getIndicesBuffer().position(0);
		mesh.getIndicesBuffer().limit(count);

		mesh.render(shader, GL20.GL_TRIANGLES, 0, count);

		renderCalls++;
		index = 0;

		textures.clear();
	}

	public void setProjectionMatrix(Matrix4 projection) {
		this.projectionMatrix = projection;
	}

	public boolean isDrawing() {
		return isDrawing;
	}

	public void dispose() {

	}
}
