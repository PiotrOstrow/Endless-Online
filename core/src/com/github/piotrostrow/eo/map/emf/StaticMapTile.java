package com.github.piotrostrow.eo.map.emf;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class StaticMapTile implements MapTile{

	private final TextureRegion textureRegion;

	protected StaticMapTile(Texture texture){
		this.textureRegion = new TextureRegion(texture);
	}

	@Override
	public TextureRegion getTextureRegion(){
		return textureRegion;
	}
}
