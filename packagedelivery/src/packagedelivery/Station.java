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
	private int continentId;

	private int energy = 100; // everyone has the same?
	private List<Vehicle> vehicles = new ArrayList<Vehicle>();
	private List<Route> reachables = new ArrayList<Route>(); // ver isto ainda
	private Map<Station, Map<Route, List<Integer>>> memory = new TreeMap<Station, Map<Route, List<Integer>>>();
	private double memory_factor = 2;

	// FIXME: lista de processamentos de caixa
	private List<PackBox> packages = new ArrayList<PackBox>();

	public Station(int identifier) {
		super();
		id = identifier;
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

	public Route getRouteFromMemory(Station destiny) {
		Map<Route, List<Integer>> routesInfo = memory.get(destiny);
		if (routesInfo == null) {
			routesInfo = new TreeMap<Route, List<Integer>>();
		}
		Map<Route, Double> aux = new TreeMap<Route, Double>();
		double total = 0;
		double max = 0;
		List<Route> nullRoutes = new ArrayList<Route>();
		for (Route r : reachables) {
			List<Integer> costs = routesInfo.get(r);
			if (costs != null) {
				double mean = calcMean(costs);
				if (mean > max)
					max = mean;
				total += 1 / mean;
				aux.put(r, 1 / mean);
			} else {
				nullRoutes.add(r);
			}
		}

		for (Route r : nullRoutes) {
			double points = mode.satisfiesHeuristic(r, destiny) ? max * (memory_factor * 1 / 2) : max * memory_factor;
			total += 1 / points;
			aux.put(r, 1 / points);
		}

		double p = Math.random() * total;
		double cumulativeProbability = 0;
		for (Route r : aux.keySet()) {
			cumulativeProbability += aux.get(r);
			if (p <= cumulativeProbability) {
				return r;
			}
		}
		return randomRoute();
	}

	private double calcMean(List<Integer> costs) {
		int total = 0;
		for (Integer c : costs)
			total += c;
		return total / costs.size();
	}

	public boolean sameContinent(Route r, Station destiny) {
		Station neigh = r.getOther(this);
		return neigh.getContinentId() == destiny.getContinentId();
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
		mode.sendPackage(pack, vehicle);
		display(this, r.getOther(this), vehicle, pack);
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

	public int getContinentId() {
		return continentId;
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

	public void setContinentId(int id) {
		this.continentId = id;
	}

	private void deliverHere(PackBox pack) {
		// FIXME reward and remove from list
		Station source = pack.getSource();
		source.increaseDelivered();
		source.increasePointsBy(pack.getReward());
	}

	public void addStationRoute(Station source, Route r, int cost) {
		Map<Route, List<Integer>> routesInfo = memory.get(source);
		if (routesInfo == null) {
			routesInfo = new TreeMap<Route, List<Integer>>();
			memory.put(source, routesInfo);
		}
		List<Integer> routeCosts = routesInfo.get(r);
		if (routeCosts == null)
			routesInfo.put(r, new ArrayList<Integer>());
		routesInfo.get(r).add(cost);

	}

	public void initStationRoutes(Route r) {
		reachables.add(r);
	}

	public void display(Station from, Station to, Vehicle vehicle, PackBox pack) {
		System.out.println("---------------------------------");
		System.out.println("From: " + from.getStationId() + " To: " + to.getStationId());
		System.out.println("Package: " + pack);
		System.out.println("Vehicle: " + vehicle);
		System.out.println("---------------------------------");
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