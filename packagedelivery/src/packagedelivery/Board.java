package packagedelivery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import packagedelivery.Route.RouteType;
import java.util.Random;
import java.io.*;

/**
 * Environment
 * 
 * @author Rui Henriques
 */
public class Board{

	/** The environment */
	private static Graph map;
	private static List<Station> stations = new ArrayList<Station>();
	private static int iteration = 0;

	/****************************
	 ***** A: SETTING BOARD
	 * 
	 * @throws ClassNotFoundException*****
	 ****************************/

	public static void initialize() throws IOException, ClassNotFoundException {

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
		//map.buildGraphRingss();



		// Save map
		// String graph_type = "allRing";
		// String graph_name = graph_type + agents + "stations_" + conti +"islands";
		// String fich =  "src/packagedelivery/graphs/" + graph_name + ".dat";
		// ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fich)));
		// out.writeObject(map);
		// out.close();
		// System.out.println("''''''''''''");
		// System.out.println("Saved");
		// System.out.println("''''''''''''");


		// Get map
		
		String filename =  "src/packagedelivery/graphs/" + "graphNoob5stations_1islands" + ".dat";
		ObjectInputStream in = new ObjectInputStream( new BufferedInputStream(new FileInputStream(filename)));
		map = (Graph)in.readObject();
		System.out.println("''''''''''''");
		System.out.println("Recovered");
		System.out.println("''''''''''''");



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
			StationMode mode = new StationSingleReceiveMemory(station);
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
					Vehicle boat = new Vehicle(transportCost * 2, RouteType.SEA);
					from.receiveVehicle(boat, r);
					Vehicle boat2 = new Vehicle(transportCost * 2, RouteType.SEA);
					from.receiveVehicle(boat2, r);
					Vehicle boat3 = new Vehicle(transportCost * 2, RouteType.SEA);
					from.receiveVehicle(boat3, r);
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
					Vehicle airplane = new Vehicle(transportCost * 3, RouteType.AIR);
					from.receiveVehicle(airplane, r);
					Vehicle airplane2 = new Vehicle(transportCost * 3, RouteType.AIR);
					from.receiveVehicle(airplane2, r);
					Vehicle airplane3 = new Vehicle(transportCost * 3, RouteType.AIR);
					from.receiveVehicle(airplane3, r);
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
					Vehicle triciclo = new Vehicle(transportCost, RouteType.LAND);
					from.receiveVehicle(triciclo, r);
					Vehicle triciclo2 = new Vehicle(transportCost, RouteType.LAND);
					from.receiveVehicle(triciclo2, r);
					Vehicle triciclo3 = new Vehicle(transportCost, RouteType.LAND);
					from.receiveVehicle(triciclo3, r);
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

			//Fixme jd pode n gostar
			int packagesToDeliver = 10;
			Random rand = new Random();

			while (packagesToDeliver > 0) {
				int endStation = rand.nextInt(stations.size());
				while (endStation == s.getStationId()) {
					endStation = rand.nextInt(stations.size());
				}

				Station end = stations.get(endStation);

				int reward = 1;

				// Lowest reward
				if (s.sameSContinent(end)) {
					reward *= 1;
				} else {
					// Medium minus reward
					if (s.findReachableRoute(end) != null) {
						reward *= 2;
					} else {
						// Medium plus reward
						if (s.checkIfImContinentBridge()) {
							reward *= 3;
						}
						// Highest reward
						else {
							reward *= 5;
						}
					}
				}

				//Fixme jd pode n gostar
				PackBox pack = new PackBox(end, s, reward);
				PackBox pack2 = new PackBox(s, end, reward);
				s.addPackage(pack);
				end.addPackage(pack2);
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

	public static void reset() throws IOException, ClassNotFoundException {
		stations = new ArrayList<Station>();
		initialize();
	}

	public static void step() {
		System.out.println();
		System.out.println("______________ ITERAÇÂO " + iteration++ + " ______________");
		for (Station s : stations) {
			s.agentDecision();

			// // Adds 1 new package
			// Random rand = new Random();
			// int endStation = rand.nextInt(stations.size());
			// while (endStation == s.getStationId()) {
			// 	endStation = rand.nextInt(stations.size());
			// }

			// Station end = stations.get(endStation);
			// int reward = 2;

			// // Lowest reward
			// if (s.sameSContinent(end)) {
			// 	reward *= 1;
			// } else {
			// 	// Medium minus reward
			// 	if (s.findReachableRoute(end) != null) {
			// 		reward *= 2;
			// 	} else {
			// 		// Medium plus reward
			// 		if (s.checkIfImContinentBridge()) {
			// 			reward *= 3;
			// 		} else {
			// 			// Highest reward
			// 			reward *= 5;
			// 		}
			// 	}
			// }

			// PackBox pack = new PackBox(end, s, reward);
			// s.addPackage(pack);
		}
	}

	public static void stop() {
		runThread.interrupt();
		runThread.stop();
	}

	public static void associateGUI(GUI graphicalInterface) {
		GUI = graphicalInterface;
	}
}
