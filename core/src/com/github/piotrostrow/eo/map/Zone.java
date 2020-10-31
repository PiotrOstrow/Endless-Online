package com.github.piotrostrow.eo.map;

import com.badlogic.gdx.utils.Disposable;
import com.github.piotrostrow.eo.map.emf.EmfMap;
import com.github.piotrostrow.eo.map.emf.EmfMapRenderer;

public class Zone implements Disposable {

	private final EmfMapRenderer mapRenderer;

	public Zone(EmfMap map) {
		mapRenderer = new EmfMapRenderer(map);
	}

	public void update(){
	}

	public void render() {
		mapRenderer.render();
	}

	@Override
	public void dispose() {

	}
}
