package com.github.piotrostrow.eo.map.emf;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.piotrostrow.eo.character.CharacterEntity;
import com.github.piotrostrow.eo.shaders.GfxShader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

	private List<CharacterEntity> characters;

	/**
	 * Shader reference to dispose, can be null
	 */
	private ShaderProgram shader;

	/**
	 * Creates an instance where the shader is managed internally
	 */
	public EmfMapRenderer(EmfMap map) {
		this(map, new ArrayList<>());
	}

	/**
	 * Creates an instance where the shader is managed internally
	 */
	public EmfMapRenderer(EmfMap map, List<CharacterEntity> characters) {
		this.map = map;
		this.characters = characters;
		this.batch = new SpriteBatch();
		this.shader = new GfxShader();
		this.batch.setShader(shader);

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
		Collections.sort(characters);

		setView();
		prepareRender();
		batch.begin();

		renderLayer(map.groundLayer);
		renderShadowLayer();

		renderMiddleLayers();

		renderLayer(map.ceilingLayer, 0, 64);
		renderLayer(map.roofLayer, -132, 0);

		batch.end();
	}

	private void renderMiddleLayers() {
		int entityCounter = 0;
		int itemCounter = 0;
		for (int col = col1; col <= col2; col++) {
			for (int row = row2; row >= row1; row--) {
				float x = (col * 32) - (row * 32);
				float y = -((row * 16) + (col * 16));

				MapTile wallsSouthLayerTile = map.wallsSouthLayer.getTile(col, row);
				if (wallsSouthLayerTile != null)
					batch.draw(wallsSouthLayerTile.getTextureRegion(), x, y);

				MapTile wallsEastLayertile = map.wallsEastLayer.getTile(col, row);
				if (wallsEastLayertile != null)
					batch.draw(wallsEastLayertile.getTextureRegion(), x + 32, y);

				MapTile elevatedLayertile = map.elevatedLayer.getTile(col, row);
				if (elevatedLayertile != null)
					batch.draw(elevatedLayertile.getTextureRegion(), x, y + 32);

				MapTile objectsLayerTile = map.objectsLayer.getTile(col, row);
				if (objectsLayerTile != null) {
					TextureRegion textureRegion = objectsLayerTile.getTextureRegion();
					//check if null because of traps
					if(textureRegion != null)
						//dividing by integer on purpose, otherwise the object jitters left and right
						batch.draw(textureRegion, x + (32f - textureRegion.getRegionWidth() / 2), y);
				}

				while (entityCounter < characters.size()) {
					CharacterEntity entity = characters.get(entityCounter);
					GridPoint2 position = entity.getRenderingGridPosition();
					if (position.x == col && position.y == row) {
						entity.render(batch, x, y);
					} else if (position.x > col || (position.x == col && position.y < row)) {
						break;
					}
					entityCounter++;
				}


				MapTile objectsOverlayLayerTile = map.objectsOverlayLayer.getTile(col, row);
				if (objectsOverlayLayerTile != null) {
					TextureRegion textureRegion = objectsOverlayLayerTile.getTextureRegion();
					batch.draw(textureRegion, x + (32 - textureRegion.getRegionWidth() / 2f), y);
				}
			}
		}
	}

	private void renderShadowLayer() {
		batch.setColor(1, 1, 1, 0.5f);
		for(int col = col2; col >= col1; col--) {
			for (int row = row2; row >= row1; row--) {
				float x = (col * 32) - (row * 32);
				float y = -((row * 16) + (col * 16));

				MapTile tile = map.shadowLayer.getTile(col, row);
				if (tile == null) continue;
				TextureRegion region = tile.getTextureRegion();

				x = x - 24 + ((64f - region.getRegionWidth()) / 2);
				y = y + 12 - ((region.getRegionHeight() - 32));

				x -= 32 - (region.getRegionWidth() / 2);

				batch.draw(region, x, y);
			}
		}
		batch.setColor(1, 1, 1, 1);
	}

	private void renderLayer(MapLayer layer) {
		renderLayer(layer, 0, 0);
	}

	private void renderLayer(MapLayer layer, float xOffset, float yOffset) {
		for(int col = col2; col >= col1; col--) {
			for (int row = row2; row >= row1; row--) {
				float x = (col * 32) - (row * 32);
				float y = -((row * 16) + (col * 16));

				MapTile tile = layer.getTile(col, row);

				if (tile == null)
					continue;

				TextureRegion textureRegion = tile.getTextureRegion();
				if (textureRegion != null)
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

		col1 = 0;
		col2 = map.getWidth() - 1;

		row1 = 0;
		row2 = map.getWidth() - 1;
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
		if(shader != null)
			shader.dispose();
	}
}
