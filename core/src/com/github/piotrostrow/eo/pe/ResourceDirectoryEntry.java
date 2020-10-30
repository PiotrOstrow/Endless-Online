package com.github.piotrostrow.eo.pe;

import java.io.IOException;

public class ResourceDirectoryEntry {

	private final int ID;

	private final int numberOfNamedEntries;
	private final int numberOfIDEntries;

	private final int pointer;

	protected ResourceDirectoryEntry(PEFile peFile, int ID, int pointer){
		this.pointer = pointer;
		this.ID = ID;

		numberOfNamedEntries = peFile.readShort(pointer, 12);
		numberOfIDEntries = peFile.readShort(pointer, 14);
	}

	public int getNumberOfNamedEntries() {
		return numberOfNamedEntries;
	}

	public int getNumberOfIDEntries() {
		return numberOfIDEntries;
	}

	public int getPointer() {
		return pointer;
	}

	public int getID() {
		return ID;
	}
}
