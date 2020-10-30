package com.github.piotrostrow.eo.map.emf;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import java.io.IOException;

public class EmfMapLoader extends SynchronousAssetLoader<EmfMap, AssetLoaderParameters<EmfMap>> {

	public EmfMapLoader() {
		super(new AbsoluteFileHandleResolver());
	}

	@Override
	public EmfMap load(AssetManager assetManager, String fileName, FileHandle file, AssetLoaderParameters<EmfMap> parameter) {
		EmfMap emfMap = null;

		try {
			EmfFileInputStream stream = new EmfFileInputStream(file.read());
			emfMap = new EmfMap(stream);
			stream.close();
		}catch (IOException e){
			e.printStackTrace();
		}

		return emfMap;
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, AssetLoaderParameters<EmfMap> parameter) {
		return null;
	}
}
