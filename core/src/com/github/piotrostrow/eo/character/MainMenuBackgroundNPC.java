package com.github.piotrostrow.eo.character;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.github.piotrostrow.eo.map.Zone;
import com.github.piotrostrow.eo.net.structs.NpcData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Extends NonPlayerCharacter class to have random movement on it's own, for main menu background
 */
public class MainMenuBackgroundNPC extends NonPlayerCharacter {

	// min and max amount of time between moves in milliseconds
	private static final int MIN = 480;
	private static final int MAX = 5000;

	private final Zone zone;
	private final GridPoint2 temp = new GridPoint2();
	private final List<Integer> directions = Arrays.asList(0, 1, 2, 3);

	private long nextMove = System.currentTimeMillis() + MathUtils.random(MIN, MAX);

	public MainMenuBackgroundNPC(NpcData npcData, Zone zone) {
		super(npcData);
		this.zone = zone;
	}

	@Override
	public void update() {
		long now = System.currentTimeMillis();
		if(now > nextMove){
			nextMove = System.currentTimeMillis() + MathUtils.random(MIN, MAX);

			Collections.shuffle(directions);

			for(Integer direction : directions){
				temp.set(super.position);
				switch (direction) {
					case Direction.UP	: temp.y--; break;
					case Direction.DOWN	: temp.y++; break;
					case Direction.LEFT	: temp.x--; break;
					case Direction.RIGHT: temp.x++; break;
				}

				if(!zone.isBlocked(temp)) {
					move(direction);
					break;
				}
			}
		}

		super.update();
	}
}
