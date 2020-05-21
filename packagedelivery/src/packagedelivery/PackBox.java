package packagedelivery;

import java.awt.Color;
import java.awt.Point;

public class PackBox extends Entity2 {

	private final Station destiny;
	private final Station source;
	private final int reward;
	// sitios onde passou
	// custo que ja gastaram cmg

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
}
