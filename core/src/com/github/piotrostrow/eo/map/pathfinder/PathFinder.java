package com.github.piotrostrow.eo.map.pathfinder;

import com.badlogic.gdx.math.GridPoint2;
import com.github.piotrostrow.eo.map.Zone;

import java.util.Queue;

public interface PathFinder {

	//@NotNull
	/**
	 * @return A queue where the first element is the next position the character should move to, not it's
	 * 			current (start) position, or an empty Queue if no path was found
	 */
	Queue<GridPoint2> getPath(Zone zone, GridPoint2 start, GridPoint2 end);
}
