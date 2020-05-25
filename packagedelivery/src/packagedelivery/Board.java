package packagedelivery;

import java.util.ArrayList;
import java.util.List;

import packagedelivery.Route.RouteType;
import java.util.Random;
import java.io.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

/**
 * Environment
 * 
 * @author Rui Henriques
 */
public class Board {

	/** The environment */
	private static Graph map;
	private static List<Station> stations = new ArrayList<Station>();
	private static int iteration = 0;

	private static List<Double> info = new ArrayList<Double>();
	private static int countPackages = 0;
	private static double deliverdPerc;
	// isto tem de ir po reset

	/****************************
	 ***** A: SETTING BOARD
	 * 
	 ****************************/

	public static void initialize() {

		// To set
		int agents;
		// int conti = 6;
		// int airports = 0;
		// int seaports = 8;

		int transportCost = 1;

		// map = new Graph(agents, conti, airports, seaports);

		// first impl
		// map.buildGraph1();

		// ringContinents impl
		// map.buildGraphRingWorld();

		// ring cities
		// map.buildGraphRingIslands();

		// chain world and cities
		// map.buildGraphRingss();

		// Save map
		// String graph_type = "allRing";
		// String graph_name = graph_type + agents + "stations_" + conti +"islands";
		// String fich = "src/packagedelivery/graphs/" + graph_name + ".dat";
		// ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new
		// FileOutputStream(fich)));
		// out.writeObject(map);
		// out.close();
		// System.out.println("''''''''''''");
		// System.out.println("Saved");
		// System.out.println("''''''''''''");

		// Get map
		String type = "Noob";
		String filename = "graphs/" + "graph" + type + "8stations_2islands" + ".dat";
		ObjectInputStream in;
		try {
			in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)));
			map = (Graph) in.readObject();
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		System.out.println("''''''''''''");
		System.out.println("Recovered");
		System.out.println("''''''''''''");
		map.printWholeGraph();
		// Whole Map / Continent disposal
		// Auxiliary for intercontinent communication and for self location
		ArrayList<ArrayList<Integer>> continents = map.getContinents();

		// Sea Map
		ArrayList<ArrayList<Integer>> sea = map.getSeaMap();

		// Air Map
		ArrayList<ArrayList<Integer>> air = map.getSkyGraph();

		// Land Map
		ArrayList<ArrayList<Integer>> land = map.getLandGraph();

		agents = map.getnStations();

		for (int i = 0; i < agents; i++) {
			Station station = new Station(i);
			StationMode mode = new StationComunication(station);
			station.setBehaviour(mode);
			stations.add(station);

		}

		for (int i = 0; i < agents; i++) {

			// Sea Routes
			// FIXME routes bidirecionais?
			if (map.getnSeaports() != 0 && sea.get(i).size() != 0) {
				Station from = stations.get(i);

				for (int j = 0; j < sea.get(i).size(); j++) {

					Station to = stations.get(sea.get(i).get(j));
					Route r = new Route(from, to, RouteType.SEA);
					from.initStationRoutes(r);

					// Probably not necessary to add both edges right away, check for duplicates
					// to.initStationRoutes(r);

					// Assigning 2*cost to sea and 1 boat for each edge
					for (int k = 0; k < agents / 4; k++) {
						Vehicle boat = new Vehicle(transportCost * 2, RouteType.SEA);
						from.receiveVehicle(boat, r);
					}

				}

			}

			// Air Routes
			if (map.getnAirports() != 0 && air.get(i).size() != 0) {
				Station from = stations.get(i);
				for (int j = 0; j < air.get(i).size(); j++) {

					Station to = stations.get(air.get(i).get(j));
					Route r = new Route(from, to, RouteType.AIR);
					from.initStationRoutes(r);

					// Probably not necessary to add both edges right away, check for duplicates
					// to.initStationRoutes(r);

					// Assigning 3*cost to sea and 1 airplane for each edge
					for (int k = 0; k < agents / 4; k++) {
						Vehicle airplane = new Vehicle(transportCost * 3, RouteType.AIR);
						from.receiveVehicle(airplane, r);
					}

				}
			}

			// Land/IntraContinental Routes
			if (land.get(i).size() != 0) {
				Station from = stations.get(i);
				for (int j = 0; j < land.get(i).size(); j++) {

					Station to = stations.get(land.get(i).get(j));
					Route r = new Route(from, to, RouteType.LAND);
					from.initStationRoutes(r);

					// Probably not necessary to add both edges right away, check for duplicates
					// to.initStationRoutes(r);

					// Assigning cost to land and 1 car for each edge
					for (int k = 0; k < agents / 4; k++) {
						Vehicle triciclo = new Vehicle(transportCost, RouteType.LAND);
						from.receiveVehicle(triciclo, r);
					}
				}
			}

			// Continent id assignement
			Station s = stations.get(i);
			for (int j = 0; j < continents.size(); j++) {

				if (continents.get(j).contains(s.getStationId())) {
					s.setContinentId(j);
					break;

				}
			}

			// Init some packages

			// Fixme jd pode n gostar
			int packagesToDeliver = 20;
			Random rand = new Random();

			while (packagesToDeliver > 0) {
				int endStation = rand.nextInt(stations.size());
				while (endStation == s.getStationId()) {
					endStation = rand.nextInt(stations.size());
				}

				Station end = stations.get(endStation);

				int reward = 1;
				// assign diferent rewards to packages according to the effort that needs to be
				// done
				// Lowest reward - reachables/direct
				if (s.findReachableRoute(end) != null) {
					if (s.sameSContinent(end)) {
						reward *= 1;
					} else {
						reward *= 3;
					}
				} else {
					// non reachables
					if (s.sameSContinent(end)) {
						// easier compound routes
						if (type.equals("rindWorld") || type.equals("Noob")) {
							reward *= 3;
						} else {
							reward *= 4;
						}
					} else {
						// harder compound routes (chain like) but easy to navigate between continents
						if (type.equals("Noob") || type.equals("ringContinents")) {
							if (s.checkIfImContinentBridge()) {
								reward *= 4;
							} else {

								reward *= 6;
							}
							// harder compound routes (chain like) but few connections between continents
						} else {
							reward *= 6 + Math.abs(s.getContinentId() - end.getContinentId());
						}

					}
				}

				// Fixme jd pode n gostar
				PackBox pack = new PackBox(end, s, reward);
				// PackBox pack2 = new PackBox(s, end, reward);
				s.addPackage(pack);
				countPackages += 1;
				// end.addPackage(pack2);
				packagesToDeliver--;
			}

		}

	}

	/***********************************
	 ***** B: ELICIT AGENT ACTIONS *****
	 ***********************************/

	private static RunThread runThread;
	private static GUI GUI;

	public static class RunThread extends Thread {

		int time;

		public RunThread(int time) {
			this.time = time;
		}

		public void run() {
			while (true) {
				step();
				try {
					sleep(time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void run(int time) {
		Board.runThread = new RunThread(time);
		Board.runThread.start();
	}

	public static void reset() {
		stations = new ArrayList<Station>();
		info = new ArrayList<Double>();
		iteration = 0;
		countPackages = 0;
		initialize();
	}

	public static void step() {
		System.out.println();
		System.out.println("______________ ITERAÇÂO " + iteration + "______________");
		iteration++;
		double sum = 0;
		boolean proceed = false;
		int delivered = 0;
		for (Station s : stations) {
			boolean canAct = s.canAct();
			if (canAct) {
				s.agentDecision();
			}
			proceed = proceed || canAct;
			sum += s.ratio();
			delivered += s.getDelivered();
			// // Adds 1 new package
			// Random rand = new Random();
			// int endStation = rand.nextInt(stations.size());
			// while (endStation == s.getStationId()) {
			// endStation = rand.nextInt(stations.size());
			// }

			// Station end = stations.get(endStation);
			// int reward = 2;

			// // Lowest reward
			// if (s.sameSContinent(end)) {
			// reward *= 1;
			// } else {
			// // Medium minus reward
			// if (s.findReachableRoute(end) != null) {
			// reward *= 2;
			// } else {
			// // Medium plus reward
			// if (s.checkIfImContinentBridge()) {
			// reward *= 3;
			// } else {
			// // Highest reward
			// reward *= 5;
			// }
			// }
			// }

			// PackBox pack = new PackBox(end, s, reward);
			// s.addPackage(pack);
		}
		if (!proceed) {
			stop();
			System.out.println("CONVERGED");
		}
		double mean = (sum / stations.size());
		info.add(mean);
		deliverdPerc = (((double) delivered) / ((double) map.getnStations() * 20));
		// System.out.println(iteration + " " + mean + " " + deliverdPerc + "%" +
		// countPackages);
		System.out.println("Amount Delvered: " + deliverdPerc);
	}

	public static void stop() {
		runThread.interrupt();
		runThread.stop();
	}

	public static void associateGUI(GUI graphicalInterface) {
		GUI = graphicalInterface;
	}

	public static void createCSVfile() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH_mm");
		String filename = "reports/info" + LocalDateTime.now().format(formatter) + ".csv";
		try (PrintWriter writer = new PrintWriter(new File(filename))) {

			StringBuilder sb = new StringBuilder();
			sb.append("iter");
			sb.append(',');
			sb.append("ratio");
			sb.append('\n');

			int iter = 1;
			for (Double mean : info) {
				sb.append(iter++);
				sb.append(',');
				sb.append(mean);
				sb.append('\n');
			}

			sb.append("delivered");
			sb.append(',');
			sb.append(deliverdPerc);
			sb.append('\n');

			writer.write(sb.toString());
			writer.close();
			System.out.println("done!");

		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
}
