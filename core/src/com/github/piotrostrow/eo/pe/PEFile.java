package com.github.piotrostrow.eo.pe;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Barebones class to extract bitmaps from gfx PE files from original EO client
 */
public class PEFile {

	private final ByteBuffer byteBuffer;

	private Section resourceSection;

	private List<ResourceDataEntry> bitmapDataEntries = new ArrayList<>();

	public PEFile(String path) throws IOException {
		// read in the whole file
		SeekableByteChannel byteChannel = Files.newByteChannel(new File(path).toPath(), StandardOpenOption.READ);
		byteBuffer = ByteBuffer.allocate((int) byteChannel.size());
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		byteChannel.read(byteBuffer);
		byteChannel.close();
		byteBuffer.rewind();

		// PE Header
		int headerOffset = readInt(0x3c, 0);
		int format = readShort(headerOffset, 24);
		int dataDirectoryOffset = headerOffset + (format == 0x10b ? 120 : 136);
		int sectionTableOffset = dataDirectoryOffset + 8 * readInt(headerOffset, format == 0x10b ? 116 : 132);
		int sectionCount = readShort(headerOffset, 6);

		for(int i = 0; i < sectionCount; i++) {
			Section section = new Section(this, sectionTableOffset + i * 40);
			String name = section.getName();
			if (name.equalsIgnoreCase(".rsrc")) {
				this.resourceSection = section;
			}
		}

		if(this.resourceSection == null)
			throw new RuntimeException(".rsrc section was not found");

		// Root, level 1 directory, .rsrc
		ResourceDirectoryEntry root = new ResourceDirectoryEntry(this, 0, resourceSection.getPointerToRawData());

		// Level 2 directory, looking for the level 3 directory with ID 2 (BITMAP)
		ResourceDirectoryEntry levelTwoDirectory = null;
		for(int i = 0; i < root.getNumberOfIDEntries(); i++){
			int integerID = readInt(resourceSection.getPointerToRawData(), 16 + root.getNumberOfNamedEntries() * 8 + i * 8);
			int subdirectoryOffset = readInt(resourceSection.getPointerToRawData(), 20 + root.getNumberOfNamedEntries() * 8 + i * 8) & ~0x80000000;

			if(integerID == 2){ // 2 = BITMAP
				levelTwoDirectory = new ResourceDirectoryEntry(this, 0, resourceSection.getPointerToRawData() + subdirectoryOffset);
				break;
			}
		}

		if(levelTwoDirectory == null)
			throw new RuntimeException("Bitmap resource directory entry not found");

		// Level 3 directory, pointing to resource entries
		ResourceDirectoryEntry[] bitmapDirectoryEntries = new ResourceDirectoryEntry[levelTwoDirectory.getNumberOfIDEntries()];
		for(int i = 0; i < levelTwoDirectory.getNumberOfIDEntries(); i++){
			int integerID = readInt(levelTwoDirectory.getPointer(), 16 + levelTwoDirectory.getNumberOfNamedEntries() * 8 + i * 8);
			int subdirectoryOffset = readInt(levelTwoDirectory.getPointer(), 20 + levelTwoDirectory.getNumberOfNamedEntries() * 8 + i * 8) & ~0x80000000;
			bitmapDirectoryEntries[i] = new ResourceDirectoryEntry(this, integerID, resourceSection.getPointerToRawData() + subdirectoryOffset);
		}

		// resource entries containing the pointer to the bitmap data
		for(ResourceDirectoryEntry entry : bitmapDirectoryEntries) {
			// this ID here reads 0, the directory above has the correct one
//			int integerID = readInt(entry.getPointer(), 16 + entry.getNumberOfNamedEntries() * 8) & ~0x80000000;
			int subdirectoryOffset = readInt(entry.getPointer(), 20 + entry.getNumberOfNamedEntries() * 8) & ~0x80000000;
			ResourceDataEntry bitmapDataEntry = new ResourceDataEntry(this, entry.getID(), resourceSection.getPointerToRawData() + subdirectoryOffset);
			bitmapDataEntries.add(bitmapDataEntry);

		}
	}

	public List<ResourceDataEntry> getBitmapDataEntries() {
		return bitmapDataEntries;
	}

	public Texture loadTexture(ResourceDataEntry entry) {
		int bitmapDataPointer = resourceSection.getPointerToRawData() + entry.getPointer() - resourceSection.getVirtualAddress();

		// This is writing over the data of the previous bitmap, but the data is loaded and copied to the GPU anyway.
		// As long as we iterate forwards and never reload files from this class/buffer it's fine
		byteBuffer.position(bitmapDataPointer - 14);
		byteBuffer.put("BM".getBytes(StandardCharsets.US_ASCII));
		byteBuffer.putInt(entry.getSize() + 14);
		byteBuffer.putInt(0);
		byteBuffer.putInt(54);

		return new Texture(new Pixmap(byteBuffer.array(), bitmapDataPointer - 14, entry.getSize() + 14));
	}

	int readInt(int base, int offset) {
		byteBuffer.position(base + offset);
		return byteBuffer.getInt();
	}

	int readShort(int base, int offset) {
		byteBuffer.position(base + offset);
		return byteBuffer.getShort() & 0xFFFF;
	}

	void read(byte[] buffer, int base){
		byteBuffer.position(base);
		this.byteBuffer.get(buffer);
	}
}
