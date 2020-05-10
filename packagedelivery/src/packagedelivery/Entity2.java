package packagedelivery;

import java.awt.Color;
import java.awt.Point;
import java.util.Random;

/**
 * @author Rui Henriques
 */
public class Entity2 extends Thread {

	public Point point;
	public Color color;
	protected Random random;

	public Entity2() {
		this.random = new Random();
	}
}
