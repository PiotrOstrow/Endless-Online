package com.github.piotrostrow.eo.pe;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Section {

	private PEFile peFile;
	private int sectionOffset;

	private String name;

	private int virtualAddress = -1;
	private int pointerToRawData = -1;

	Section(PEFile peFile, int sectionOffset) {
		this.peFile = peFile;
		this.sectionOffset = sectionOffset;
	}

	public String getName() {
		if(name == null) {
			byte[] buffer = new byte[8];
			peFile.read(buffer, sectionOffset);
			name = new String(buffer, StandardCharsets.UTF_8);
			if (name.indexOf(0) != -1)
				name = name.substring(0, name.indexOf(0));
		}
		return name;
	}

	public int getPointerToRawData(){
		if(pointerToRawData < 0){
			pointerToRawData = peFile.readInt(sectionOffset, 20);
		}
		return pointerToRawData;
	}

	public int getSectionOffset() {
		return sectionOffset;
	}

	public int getVirtualAddress(){
		if(virtualAddress < 0) {
			virtualAddress = peFile.readInt(sectionOffset, 12);
		}
		return virtualAddress;
	}
}