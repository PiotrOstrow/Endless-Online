package com.github.piotrostrow.eo.graphics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.github.piotrostrow.eo.character.CharacterState;

public class PlayerTextureAtlas implements Disposable {

	private final TextureRegion[] idle;
	private final TextureRegion[] movement;
	private final TextureRegion[] attack;
	private final TextureRegion[] spell;
	private final TextureRegion[] sitChair;
	private final TextureRegion[] sitGround;
	private final TextureRegion[] attackBow;

	PlayerTextureAtlas(TextureRegion[] idle,  TextureRegion[] movement, TextureRegion[] attack, TextureRegion[] spell,
					   TextureRegion[] sitChair, TextureRegion[] sitGround, TextureRegion[] attackBow) {
		this.idle = idle;
		this.movement = movement;
		this.attack = attack;
		this.spell = spell;
		this.sitChair = sitChair;
		this.sitGround = sitGround;
		this.attackBow = attackBow;
	}

	public TextureRegion get(CharacterState characterState, int direction, int frame) {
		switch (characterState){
			case IDLE: return idle[direction];
			case MOVE: return movement[direction * 4 + frame];
			case ATTACK_MELEE: return attack[direction * 2 + frame];
			case CAST_SPELL: return spell[direction];
			case SIT_CHAIR: return sitChair[direction];
			case SIT_GROUND: return sitGround[direction];
			case ATTACK_RANGE: return attackBow[direction];
		}

		return idle[0];
	}

	public TextureRegion get() {
		return new TextureRegion(idle[0].getTexture());
	}


	@Override
	public void dispose() {

	}
}
