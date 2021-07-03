package com.github.piotrostrow.eo.map.pathfinder;

import com.badlogic.gdx.math.GridPoint2;
import com.github.piotrostrow.eo.map.Zone;

import java.util.*;

public class AStarPathFinder implements PathFinder {

	private final Queue<Node> open = new PriorityQueue<>();
	private final Map<Node, Float> costSoFar = new HashMap<>();

	private Zone zone;

	private GridPoint2 end;

	private final LinkedList<GridPoint2> path = new LinkedList<>();

	@Override
	public Queue<GridPoint2> getPath(Zone zone, GridPoint2 start, GridPoint2 end) {
		this.zone = zone;
		this.end = end;

		open.clear();
		path.clear();
		costSoFar.clear();

		if(zone.isBlocked(end))
			return path;

		Node startingNode = new Node(start.x, start.y, 0, calculateHeuristic(start.x, start.y));
		open.add(startingNode);
		costSoFar.put(startingNode, startingNode.score);

		while(!open.isEmpty()) {
			Node current = open.poll();

			if(current.x == end.x && current.y == end.y) {
				while(current.previous != null) {
					path.addFirst(current);
					current = current.previous;
				}
				return path;
			}

			for(Node adjacent : getAdjacentNodes(current)) {
				float newCost = costSoFar.get(current) + 1;
				if(!costSoFar.containsKey(adjacent) || newCost < costSoFar.get(adjacent)){
					costSoFar.put(adjacent, newCost);
					adjacent.previous = current;
					open.add(adjacent);
				}
			}
		}

		return path;
	}

	private List<Node> getAdjacentNodes(Node node) {
		List<Node> adjacentNodes = new ArrayList<>();

		if(!zone.isBlocked(node.x + 1, node.y))
			adjacentNodes.add(new Node(node.x + 1, node.y, node.score + 1, calculateHeuristic(node.x + 1, node.y)));

		if(!zone.isBlocked(node.x - 1, node.y))
			adjacentNodes.add(new Node(node.x - 1, node.y, node.score + 1, calculateHeuristic(node.x - 1, node.y)));

		if(!zone.isBlocked(node.x, node.y + 1))
			adjacentNodes.add(new Node(node.x, node.y + 1, node.score + 1, calculateHeuristic(node.x, node.y + 1)));

		if(!zone.isBlocked(node.x, node.y - 1))
			adjacentNodes.add(new Node(node.x, node.y - 1, node.score + 1, calculateHeuristic(node.x, node.y - 1)));

		return adjacentNodes;
	}

	private float calculateHeuristic(int x, int y) {
		int dx = Math.abs(x - end.x);
		int dy = Math.abs(y - end.y);
		return dx + dy;
	}

	private static class Node extends GridPoint2 implements Comparable<Node>{

		Node previous;

		final float score;
		final float heuristic;

		Node(int x, int y, float score, float heuristic) {
			super(x, y);
			this.score = score;
			this.heuristic = heuristic;
		}

		@Override
		public int compareTo(Node o) {
			int result = Float.compare(this.score + this.heuristic, o.score + o.heuristic);
			if(result == 0)
				return Float.compare(this.heuristic, o.heuristic);
			return result;
		}
	}
}
