package packagedelivery;

import java.awt.Color;
import java.awt.Point;
import java.util.*;

public class PackBox extends Entity2 {

	private final Station destiny;
	private final Station source;
	private final int reward;
	private int costs = 0;
	private List<Station> path;

	public PackBox(Station destiny, Station source, int reward) {
		this.destiny = destiny;
		this.source = source;
		this.reward = reward;
	}

	/*****************************
	 ***** AUXILIARY METHODS *****
	 *****************************/

	/*
	 * public void grabBox(Point newpoint) { Board.removeEntity(point); point =
	 * newpoint; }
	 * 
	 * public void dropBox(Point newpoint) { Board.insertEntity(this, newpoint);
	 * point = newpoint; }
	 * 
	 * public void moveBox(Point newpoint) { point = newpoint; }
	 */

	public Station getDestiny() {
		return destiny;
	}

	public Station getSource() {
		return source;
	}

	public int getReward() {
		return reward;
	}

	public void addTransportationCost(int cost) {
		this.costs += cost;
	}

	public int getCost() {
		return this.costs;
	}

	public void addToPath(Station s) {
		if (path == null)
			path = new ArrayList<Station>();
		path.add(s);
	}

	public List<Station> getPath() {
		return path;
	}

	@Override
	public String toString() {
		return "[costs=" + costs + ", destiny=" + destiny.getStationId() + ", path=" + path + ", reward=" + reward + ", source="
				+ source.getStationId() + "]";
	}

}
