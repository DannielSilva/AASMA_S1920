package packagedelivery;

import java.util.Random;

/**
 * @author Rui Henriques
 */
public class Entity extends Thread {

	protected Random random;

	public Entity() {
		this.random = new Random();
	}
}
