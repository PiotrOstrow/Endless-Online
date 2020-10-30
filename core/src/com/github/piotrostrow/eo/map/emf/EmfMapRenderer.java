package com.github.piotrostrow.eo.map.emf;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.piotrostrow.eo.shaders.GfxShader;

public class EmfMapRenderer implements Disposable {

	private static final int NUM_VERTICES = 20;

	private final float vertices[] = new float[NUM_VERTICES];

	private Matrix4 isoTransform = new Matrix4();
	private Matrix4 invIsotransform;
	private Vector3 screenPos = new Vector3();

	private Rectangle viewBounds = new Rectangle();

	private Vector2 topRight = new Vector2();
	private Vector2 bottomLeft = new Vector2();
	private Vector2 topLeft = new Vector2();
	private Vector2 bottomRight = new Vector2();

	public final OrthographicCamera camera;
	private final Viewport viewport;
	private SpriteBatch batch;

	private final EmfMap map;

	private int row1, row2, col1, col2;

	public EmfMapRenderer(EmfMap map){
		this.map = map;
		this.batch = new SpriteBatch();
		this.batch.setShader(new GfxShader());

		float width = 1280;
		float height = 720;
		this.viewport = new StretchViewport(width, height);
		this.camera = new OrthographicCamera(width, height);
		this.viewport.setCamera(this.camera);

		// center the camera
		camera.position.x = map.getWidth() * 64 / 2;
		camera.update();

		// create the isometric transform
		isoTransform.idt();
		isoTransform.scale((float) (Math.sqrt(2.0) / 2.0), (float) (Math.sqrt(2.0) / 4.0), 1.0f);
		isoTransform.rotate(0.0f, 0.0f, 1.0f, -45);

		// ... and the inverse matrix
		invIsotransform = new Matrix4(isoTransform);
		invIsotransform.inv();
	}

	public void render() {
		setView();
		prepareRender();
		batch.begin();

		renderLayer(map.getMapLayer(0));

		batch.end();
	}

	private void renderLayer(MapLayer layer){
		renderLayer(layer, 0, 0);
	}

	private void renderLayer(MapLayer layer, float xOffset, float yOffset) {
//		if (!layer.isVisible())
//			return;

		for (int row = row2; row >= row1; row--) {
			for (int col = col1; col <= col2; col++) {
				float x = (col * 32) + (row * 32);
				float y = (row * 16) - (col * 16);

				MapTile tile = layer.getTile(col, row);

				if (tile == null)
					continue;

				TextureRegion textureRegion = tile.getTextureRegion();
				if(textureRegion != null)
					batch.draw(textureRegion, x + xOffset, y + yOffset);
			}
		}
	}

	private void prepareRender() {
		// transforming screen coordinates to iso coordinates
		row1 = (int) (translateScreenToIso(topLeft).y / 64) - 2;
		row2 = (int) (translateScreenToIso(bottomRight).y / 64) + 2;

		col1 = (int) (translateScreenToIso(bottomLeft).x / 64) - 2;
		col2 = (int) (translateScreenToIso(topRight).x / 64) + 2;

		row1 = Math.max(0, row1);
		row2 = Math.min(map.getHeight() - 1, row2);

		col1 = Math.max(0, col1);
		col2 = Math.min(map.getWidth() - 1, col2);
	}

	private Vector3 translateScreenToIso(Vector2 vec) {
		screenPos.set(vec.x, vec.y, 0);
		screenPos.mul(invIsotransform);
		return screenPos;
	}

	private void setView() {
		batch.setProjectionMatrix(camera.combined);
		float width = camera.viewportWidth * camera.zoom;
		float height = camera.viewportHeight * camera.zoom;
		float w = width * Math.abs(camera.up.y) + height * Math.abs(camera.up.x);
		float h = height * Math.abs(camera.up.y) + width * Math.abs(camera.up.x);
		viewBounds.set(camera.position.x - w / 2, camera.position.y - h / 2, w, h);

		// setting up the screen points TODO: move this up, or remove, after rendering everything
		// COL1
		topRight.set(viewBounds.x + viewBounds.width, viewBounds.y);
		// COL2
		bottomLeft.set(viewBounds.x, viewBounds.y + viewBounds.height);
		// ROW1
		topLeft.set(viewBounds.x, viewBounds.y);
		// ROW2
		bottomRight.set(viewBounds.x + viewBounds.width, viewBounds.y + viewBounds.height);
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
}
