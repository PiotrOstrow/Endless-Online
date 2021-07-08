package com.github.piotrostrow.eo.character;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.github.piotrostrow.eo.assets.Assets;
import com.github.piotrostrow.eo.net.structs.NpcData;
import com.github.piotrostrow.eo.pub.EnfFile;

public class NonPlayerCharacter extends CharacterEntity{

	private int index;

	private EnfFile.Npc enfData;

	private final TextureRegion[] idle 		= new TextureRegion[8];
	private final TextureRegion[] move 		= new TextureRegion[16];
	private final TextureRegion[] attack 	= new TextureRegion[8];

	public NonPlayerCharacter(NpcData npcData) {
		super(npcData.x, npcData.y, npcData.direction);
		updateData(npcData);
	}

	public void updateData(NpcData npcData) {
		this.index = npcData.index;

		if(this.enfData == null || this.enfData.npcID != npcData.id) { // in case NPCs can morph or something
			initialize(npcData.id);
		}

		setPosition(npcData.x, npcData.y);
		setDirection(npcData.direction);
	}

	private void initialize(int npcID) {
		this.enfData = Assets.getNpcData(npcID);
		for(int i = 0; i < 4; i++) {
			idle[i] = new TextureRegion(Assets.gfx(21, enfData.gfxID + i));
			attack[i] = new TextureRegion(Assets.gfx(21, enfData.gfxID + i + 12));
		}

		for(int i = 0; i < 8; i++)
			move[i] = new TextureRegion(Assets.gfx(21, enfData.gfxID + i + 4));

		if(!enfData.hasIdleAnimation())
			for(int i = 0; i < idle.length; i+= 2)
				idle[i + 1] = idle[i];

		setMirrorRegions(idle);
		setMirrorRegions(move);
		setMirrorRegions(attack);
	}

	private void setMirrorRegions(TextureRegion[] regions) {
		for(int i = 0; i < regions.length / 4; i++){
			TextureRegion tr1 = new TextureRegion(regions[i]);
			tr1.flip(true, false);
			regions[regions.length - regions.length / 4 + i] = tr1;

			TextureRegion tr2 = new TextureRegion(regions[regions.length / 4 + i]);
			tr2.flip(true, false);
			regions[regions.length / 2 + i] = tr2;
		}
	}

	@Override
	public int getXOffset(int direction) {
		return 0;
	}

	@Override
	public int getYOffset(int direction) {
		return 0;
	}

	public int getIndex() {
		return index;
	}

	@Override
	protected TextureRegion getTextureRegion(CharacterState characterState, int direction, int frame) {
		switch(characterState) {
			case IDLE: return idle[direction * 2 + frame];
			case MOVE: return move[direction * 4 + frame];
			case ATTACK_RANGE:
			case ATTACK_MELEE: return attack[direction * 2 + frame];
		}
		return idle[direction];
	}

	@Override
	public String getName() {
		return enfData.name;
	}
}
