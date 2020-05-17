package packagedelivery;

import java.awt.Color;
import java.awt.Point;
import packagedelivery.Block.Shape;

import java.util.*;

/**
 * Agent behavior
 * 
 * @author Rui Henriques
 */
public class Station extends Entity {

	public int direction = 90;
	public Box cargo;
	private Point ahead;
	private int id;
	private int points = 0;
	private int delivered = 0;

	private int energy = 100; // everyone has the same?
	private List<Vehicle> vehicles = new ArrayList<Vehicle>();
	private List<Route> reachables = new ArrayList<Route>(); // ver isto ainda

	// FIXME: lista de processamentos de caixa
	private List<PackBox> packages = new ArrayList<PackBox>();

	public Station(Point point, Color color) {
		super(point, color);
	}

	/**********************
	 **** A: decision *****
	 **********************/
	/*
	 * decisoes escolher caixa ler destino da caixa somos a estacao? senao se o
	 * destino for reachable: manda senao experimenta um qq
	 * 
	 * sensor destino da caixa és meu vizinho? / por onde te mando saber a melhor
	 * caixa a entregar veiculos disponiveis escolher caixa
	 * 
	 * atuator escolher destino/intermedio vai
	 * 
	 */
	// se n for reachable experimenta outra caixa q possa ser
	public void agentDecision() {
		SortedSet<PreProcessedPackBox> orderedPackages = chooseBox();

		for (PreProcessedPackBox prep : orderedPackages) {
			PackBox pack = prep.getPack();

			Station destiny = readPackageDestiny(pack);
			// somos a estacao?
			if (destiny.getStationId() == this.id) {
				Station source = pack.getSource();
				source.increaseDelivered();
				source.increasePoints(pack.getReward());
			}

			Route route = prep.getRoute();
			if (route != null) {

				Vehicle vehicle = prep.getVehicle();
				if (vehicle != null) {
					send(destiny, vehicle, pack);
					break;
				}

			} else {
				// experimenta um vizinho
				Route guess = randomRoute();
				Vehicle vehicle = findVehicle(guess);
				if (vehicle != null) {
					send(guess.getOther(this), vehicle, pack);
				}
				break;
			}
		}

		/*
		 * ahead = aheadPosition(); if (isWall()) rotateRandomly(); else if (isRamp() &&
		 * isBoxAhead() && !cargo()) grabBox(); else if (isShelf() && !isBoxAhead() &&
		 * cargo() && shelfColor().equals(cargoColor())) dropBox(); else if
		 * (!isFreeCell()) rotateRandomly(); else if (random.nextInt(5) == 0)
		 * rotateRandomly(); else moveAhead();
		 */
	}

	private Route randomRoute() {
		int index = random.nextInt(reachables.size());
		return reachables.get(index);
	}

	/********************/
	/**** B: sensors ****/
	/********************/

	/* read package destiny */
	public Station readPackageDestiny(PackBox pack) {
		return pack.getDestiny();
	}

	// FIXME meter preferencia de rota
	public Route findRoute(Station destiny) {
		for (Route r : reachables) {
			if (r.containsStation(destiny)) {
				return r;
			}
		}
		return null;
	}

	/* FIXME choose best box with utility */
	// retornar varias ordenadas por utilidade
	public SortedSet<PreProcessedPackBox> chooseBox() {
		SortedSet<PreProcessedPackBox> set = new TreeSet<PreProcessedPackBox>();
		PreProcessedPackBox prep;
		for (PackBox p : packages) {
			prep = new PreProcessedPackBox(p);
			prep.setRate(evaluateBox(p, prep));
			set.add(prep);
		}
		return set;
	}

	// entre 0 e 100
	private int evaluateBox(PackBox b1, PreProcessedPackBox prep) {
		if (b1.getDestiny().getStationId() == this.getStationId()) {
			return 0;
		}
		Route r = findRoute(b1.getDestiny());
		prep.setRoute(r);
		if (r != null) {
			Vehicle v = findVehicle(r);
			prep.setVehicle(v);
			if (v != null) {
				return b1.getReward() / v.getCost();
			} else {
				return 100;
			}
		} else {
			return 100;
		}
	}

	// FIXME escolher o que gasta menos ainda
	public Vehicle findVehicle(Route r) {
		for (Vehicle v : vehicles) {
			if (v.canGoThrough(r))
				return v;
		}
		return null;
	}

	/**********************/
	/**** C: actuators ****/
	/**********************/

	public void send(Station s, Vehicle vehicle, PackBox pack) {
		vehicles.remove(vehicle);
		s.receiveVehicle(vehicle);
		packages.remove(pack);
		s.receivePackage(pack);
	}

	public void receiveVehicle(Vehicle v) {
		vehicles.add(v);
	}

	public void receivePackage(PackBox b) {
		packages.add(b);
	}

	/**********************/
	/**** D: auxiliary ****/
	/**********************/
	public int getStationId() {
		return id;
	}

	public void increasePoints(int p) {
		this.points += p;
	}

	public void increaseDelivered() {
		this.delivered += 1;
	}
	/* -------------------------- */

	/* Check if agent is carrying box */
	public boolean cargo() {
		return cargo != null;
	}

	/* Return the color of the box */
	public Color cargoColor() {
		return cargo.color;
	}

	/* Return the color of the shelf ahead or 0 otherwise */
	public Color shelfColor() {
		Point ahead = aheadPosition();
		return Board.getBlock(ahead).color;
	}

	/*
	 * Check if the cell ahead is floor (which means not a wall, not a shelf nor a
	 * ramp) and there are any robot there
	 */
	public boolean isFreeCell() {
		if (Board.getBlock(ahead).shape.equals(Shape.free))
			if (Board.getEntity(ahead) == null)
				return true;
		return false;
	}

	/* Check if the cell ahead contains a box */
	public boolean isBoxAhead() {
		Entity entity = Board.getEntity(ahead);
		return entity != null && entity instanceof Box;
	}

	/* Check if the cell ahead is a shelf */
	public boolean isShelf() {
		Block block = Board.getBlock(ahead);
		return block.shape.equals(Shape.shelf);
	}

	/* Check if the cell ahead is a ramp */
	public boolean isRamp() {
		Block block = Board.getBlock(ahead);
		return block.shape.equals(Shape.ramp);
	}

	/* Check if the cell ahead is a wall */
	private boolean isWall() {
		return ahead.x < 0 || ahead.y < 0 || ahead.x >= Board.nX || ahead.y >= Board.nY;
	}

	/* Rotate agent to right */
	public void rotateRandomly() {
		if (random.nextBoolean())
			rotateLeft();
		else
			rotateRight();
	}

	/* Rotate agent to right */
	public void rotateRight() {
		direction = (direction + 90) % 360;
	}

	/* Rotate agent to left */
	public void rotateLeft() {
		direction = (direction - 90) % 360;
	}

	/* Move agent forward */
	public void moveAhead() {
		Board.updateEntityPosition(point, ahead);
		if (cargo())
			cargo.moveBox(ahead);
		point = ahead;
	}

	/* Cargo box */
	public void grabBox() {
		cargo = (Box) Board.getEntity(ahead);
		cargo.grabBox(point);
	}

	/* Drop box */
	public void dropBox() {
		cargo.dropBox(ahead);
		cargo = null;
	}

	/* Position ahead */
	private Point aheadPosition() {
		Point newpoint = new Point(point.x, point.y);
		switch (direction) {
			case 0:
				newpoint.y++;
				break;
			case 90:
				newpoint.x++;
				break;
			case 180:
				newpoint.y--;
				break;
			default:
				newpoint.x--;
		}
		return newpoint;
	}
}
