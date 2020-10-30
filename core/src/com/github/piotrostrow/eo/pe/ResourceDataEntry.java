package com.github.piotrostrow.eo.pe;

import java.io.IOException;

public class ResourceDataEntry {

	private final int ID;

	/**
	 * The pointer in the entry that points to the data of the resource
	 */
	private final int pointer;

	private final int size;

	public ResourceDataEntry(PEFile peFile, int ID, int pointer) throws IOException {
		this.ID = ID;
		this.pointer = peFile.readInt(pointer, 0);
		this.size = peFile.readInt(pointer, 4);
	}

	public int getID() {
		return ID;
	}

	public int getPointer() {
		return pointer;
	}

	public int getSize() {
		return size;
	}
}
