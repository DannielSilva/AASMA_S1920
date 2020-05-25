package packagedelivery;

import packagedelivery.Route.RouteType;
import java.util.*;

/**
 * Agent behavior
 * 
 */
public class Station extends Entity implements Comparable<Station> {

	public int direction = 90;
	private int id;
	private double points = 0;
	private int delivered = 0;
	private StationMode mode;
	private int continentId;

	private int energy = 100; // everyone has the same?
	private List<Vehicle> vehicles = new ArrayList<Vehicle>();
	private List<Route> reachables = new ArrayList<Route>(); // ver isto ainda
	private Map<Station, Map<Route, List<Integer>>> memory = new TreeMap<Station, Map<Route, List<Integer>>>();
	private Map<Station, Pair<Route, Integer>> bestTable = new TreeMap<Station, Pair<Route, Integer>>();
	private double memory_factor = 2;

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

		List<PreProcessedPackBox> orderedPackages = chooseBox();

		for (PreProcessedPackBox prep : orderedPackages) {
			PackBox pack = prep.getPack();
			Station destiny = readPackageDestiny(pack);
			// somos a estacao?
			if (destiny.getStationId() == getStationId()) {
				deliverHere(pack);
				continue;
			}

			Route route = prep.getRoute();
			if (route != null) {
				Vehicle vehicle = prep.getVehicle();
				if (vehicle != null) {
					sendThrough(route, vehicle, pack, false);
					break;
				}
				// System.out.println("JD ficou sem brinquedos " + prep);

			} else {
				// experimenta um vizinho
				Route guess = mode.findNewRoute(destiny);
				Vehicle vehicle = findVehicle(guess);
				if (vehicle != null) {

					sendThrough(guess, vehicle, pack, true);
					break;
				}
				// System.out.println("JD ficou sem brinquedos random " + prep);
			}
		}

		// System.out.println("Station " + getStationId() + " made " + points + " points
		// and has " + energy + " resources left " + "ratio : " +
		// (points/(100-energy)));
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

	// retornar varias ordenadas por utilidade
	public List<PreProcessedPackBox> chooseBox() {
		List<PreProcessedPackBox> set = new ArrayList<PreProcessedPackBox>();
		PreProcessedPackBox prep;
		for (PackBox p : packages) {
			prep = new PreProcessedPackBox(p);
			prep.setRate(evaluateBox(p, prep));
			set.add(prep);
		}
		set.sort(null);
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

	// FIXME escolher o que gasta menos ainda: acho q n
	public Vehicle findVehicle(Route r) {
		for (Vehicle v : vehicles) {
			if (v.canGoThrough(r))
				return v;
		}
		return null;
	}

	public boolean checkIfImContinentBridge() {
		for (Route r : reachables) {
			if (!r.getType().equals(RouteType.LAND))
				return true;
		}
		return false;
	}

	// FIXME meter preferencia de rota: acho q n
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
				total += (1 / mean);
				aux.put(r, (1 / mean));
			} else {
				nullRoutes.add(r);
			}
		}

		for (Route r : nullRoutes) {
			if (max == 0) {
				max = 1;
			}
			double points = mode.satisfiesHeuristic(r, destiny) ? max * (memory_factor * 1 / 2) : max * memory_factor;
			double inv = 1 / points;
			total += inv;
			// System.out.println("max" + max + "points" + points + "total" + total + "inv"
			// + inv);
			aux.put(r, inv);
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

	public Route findBestRoute(Station destiny) {
		return findBestRoute(new Request(destiny)).getRoute();
	}

	public Request findBestRoute(Request request) {
		if (request.alreadyVisited(this)) {
			request.setRoute(null);
			return request;
		}

		Route min_r = null;
		int min_c = Integer.MAX_VALUE;
		Station destiny = request.getDestiny();
		Pair<Route, Integer> pair = bestTable.get(destiny);

		if (pair != null) {
			min_r = pair.getK();
			min_c = pair.getV();
		} else {
			for (Route r : reachables) {
				if (r.getOther(this).equals(destiny) && r.cost() < min_c) {
					min_r = r;
					min_c = r.cost();
				}
			}
		}
		if (min_r == null) {
			for (Route r : reachables) {
				Request new_req = request.buildNewWithPath(this);
				Request response = r.getOther(this).findBestRoute(new_req);

				if (response.getRoute() == null)
					continue;

				int cost = r.cost() + response.getCost();
				if (cost != -1 && cost < min_c) {
					min_r = r;
					min_c = cost;
				}
			}
			if (min_r != null)
				bestTable.put(destiny, new Pair<Route, Integer>(min_r, min_c));
		}
		request.setRoute(min_r);
		request.setCost(min_c);
		return request;
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

	public boolean sameSContinent(Station destiny) {
		return this.getContinentId() == destiny.getContinentId();
	}

	/**********************/
	/**** C: actuators ****/
	/**********************/

	public void sendThrough(Route r, Vehicle vehicle, PackBox pack, boolean b) {
		vehicles.remove(vehicle);
		r.sendVehicleFrom(vehicle, this);

		packages.remove(pack);
		r.sendPackageFrom(pack, this);

		decreaseEnergyBy(vehicle.getCost());

		pack.addToPath(this);
		mode.sendPackage(pack, vehicle);
		// display(this, r.getOther(this), vehicle, pack, b);
	}

	public void receiveVehicle(Vehicle v, Route r) {
		vehicles.add(v);
		r.setCost(v.getCost());
	}

	public void receivePackage(PackBox b, Route r) {
		packages.add(b);
		mode.receivePackage(b, r);
	}

	public void addPackage(PackBox b) {
		packages.add(b);

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

	public void increasePointsBy(double p) {
		this.points += p;
	}

	public void decreaseEnergyBy(int e) {
		this.energy -= e;
	}

	public void increaseDelivered() {
		this.delivered += 1;
	}

	public int getDelivered() {
		return this.delivered;
	}

	public void setBehaviour(StationMode behaviour) {
		this.mode = behaviour;
	}

	public void setContinentId(int id) {
		this.continentId = id;
	}

	private void deliverHere(PackBox pack) {
		// System.out.println("Estou a entregar: " + pack.toString());
		Station source = pack.getSource();
		source.increaseDelivered();
		double reward = pack.getReward() / pack.getPath().size();
		for (Station s : pack.getPath()) {
			s.increasePointsBy(reward);
		}
		packages.remove(pack);
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

	public void display(Station from, Station to, Vehicle vehicle, PackBox pack, boolean b) {
		System.out.println("---------------------------------");
		System.out.println("From: " + from + " To: " + to);
		System.out.println("Package: " + pack);
		System.out.println("Vehicle: " + vehicle);
		System.out.println("Rota random: " + b);
		System.out.println("---------------------------------");
	}

	@Override
	public String toString() {
		return id + "";
	}

	public double ratio() {
		return points / (100 - energy);
	}

	class Pair<T, Z> {
		private T _t;
		private Z _z;

		public Pair(T _t, Z _z) {
			this._t = _t;
			this._z = _z;
		}

		public T getK() {
			return _t;
		}

		public void set_t(T _t) {
			this._t = _t;
		}

		public Z getV() {
			return _z;
		}

		public void set_z(Z _z) {
			this._z = _z;
		}

	}

	@Override
	public int compareTo(Station o) {
		return ((Integer) getStationId()).compareTo((Integer) o.getStationId());
	}

	public boolean canAct() {
		return packages.size() > 0 && energy > 0;
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