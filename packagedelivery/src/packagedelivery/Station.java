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
public class Station extends Entity2 {

	public int direction = 90;
	private int id;
	private int points = 0;
	private int delivered = 0;
	private StationMode mode;

	private int energy = 100; // everyone has the same?
	private List<Vehicle> vehicles = new ArrayList<Vehicle>();
	private List<Route> reachables = new ArrayList<Route>(); // ver isto ainda
	private Map<Station, Route> memory = new TreeMap<Station, Route>();

	// FIXME: lista de processamentos de caixa
	private List<PackBox> packages = new ArrayList<PackBox>();

	public Station() {
		super();
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
		if (mode != null) {
			SortedSet<PreProcessedPackBox> orderedPackages = chooseBox();

			for (PreProcessedPackBox prep : orderedPackages) {
				PackBox pack = prep.getPack();
				Station destiny = readPackageDestiny(pack);
				// somos a estacao?
				if (destiny.getStationId() == getStationId())
					deliverHere(pack);

				Route route = prep.getRoute();
				if (route != null) {
					Vehicle vehicle = prep.getVehicle();
					if (vehicle != null) {
						sendThrough(route, vehicle, pack);
						break;
					}

				} else {
					// experimenta um vizinho
					Route guess = mode.findNewRoute(destiny);
					Vehicle vehicle = findVehicle(guess);
					if (vehicle != null) {
						sendThrough(guess, vehicle, pack);
						break;
					}
				}
			}
		}
	}

	public Route randomRoute() {
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
		Route r = findReachableRoute(b1.getDestiny());
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

	// FIXME meter preferencia de rota
	public Route findReachableRoute(Station destiny) {
		for (Route r : reachables) {
			if (r.containsStation(destiny)) {
				return r;
			}
		}
		return null;
	}

	public Route findMemoryRoute(Station destiny) {
		return memory.get(destiny);
	}

	/**********************/
	/**** C: actuators ****/
	/**********************/

	public void sendThrough(Route r, Vehicle vehicle, PackBox pack) {
		vehicles.remove(vehicle);
		r.sendVehicleFrom(vehicle, this);
		packages.remove(pack);
		r.sendPackageFrom(pack, this);
		decreaseEnergyBy(vehicle.getCost());
	}

	public void receiveVehicle(Vehicle v, Route r) {
		vehicles.add(v);
	}

	public void receivePackage(PackBox b, Route r) {
		packages.add(b);
		mode.receivePackage(b, r);
	}

	/**********************/
	/**** D: auxiliary ****/
	/**********************/
	public int getStationId() {
		return id;
	}

	public void increasePointsBy(int p) {
		this.points += p;
	}

	public void decreaseEnergyBy(int e) {
		this.energy -= e;
	}

	public void increaseDelivered() {
		this.delivered += 1;
	}

	public void setBehaviour(StationMode behaviour) {
		this.mode = behaviour;
	}

	private void deliverHere(PackBox pack) {
		// FIXME reward and remove from list
		Station source = pack.getSource();
		source.increaseDelivered();
		source.increasePointsBy(pack.getReward());
	}

	public void addStationRoute(Station source, Route r) {

	}
}

/*
 * PLANEAMENTO
 * 
 * ESTAÇOES NAO COMUNICAM: - Não guardar nada (temos); - Ver continentes; -
 * Associar de onde veio à origem; - Quando recebes, associar onde veio a toda a
 * gente por quem passou;
 * 
 * ESTAÇOES COMUNICAM: - Quando entrega, propaga que entregou; - Perguntar
 * recursivamente como chegar;
 * 
 */