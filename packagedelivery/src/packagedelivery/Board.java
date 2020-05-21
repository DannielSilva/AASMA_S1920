package packagedelivery;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import packagedelivery.Block.Shape;
import packagedelivery.Route.RouteType;

/**
 * Environment
 * 
 * @author Rui Henriques
 */
public class Board {

	/** The environment */

	private static Graph map;
	private static List<Station> stations;
	private static List<PackBox> boxes;

	/****************************
	 ***** A: SETTING BOARD *****
	 ****************************/

	public static void initialize() {
		
		//To set
		int agents = 10;


		map = new Graph(agents);
		map.buildGraph();

		//Whole Map / Continent disposal
		//Auxiliary for intercontinent communication and for self location
		ArrayList<ArrayList<Integer>> continents = map.getContinents();
		
		//Sea Map
		ArrayList<ArrayList<Integer>> sea = map.getSeaMap();

		//Air Map
		ArrayList<ArrayList<Integer>> air = map.getSkyGraph();

		//Land Map
		ArrayList<ArrayList<Integer>> land = map.getLandGraph();

		for( int i = 0; i < agents; i++){
			station = new Station(i);
			stations.add(station);
		}

		for( int i = 0; i < agents; i++){

			//Sea Routes
			if(sea.get(i).size() != 0){
				Station from = stations.get(i);
				for (int j = 0; j < sea.get(i).size(); j++) { 
					Station to = stations.get(j);
					Route r = new Route(from, to, RouteType.SEA);
					from.initStationRoutes(r);
					to.initStationRoutes(r);
				} 
			}

			//Air Routes
			if(air.get(i).size() != 0){
				Station from = stations.get(i);
				for (int j = 0; j < air.get(i).size(); j++) { 
					Station to = stations.get(j);
					Route r = new Route(from, to, RouteType.AIR);
					from.initStationRoutes(r);
					to.initStationRoutes(r);
				} 
			}

			//Land/IntraContinental Routes
			if(land.get(i).size() != 0){
				Station from = stations.get(i);
				for (int j = 0; j < air.get(i).size(); j++) { 
					Station to = stations.get(j);
					Route r = new Route(from, to, RouteType.LAND);
					from.initStationRoutes(r);
					to.initStationRoutes(r);
				} 
			}


			//Continent id assignement
			Station s = stations.get(i);
			for(int i = 0; i < continents.size(); i++){
				if(continents.get(i).contains(s.getStationId()){
					s.setContinentId(i);
					break;
				}
			}

		}



	}

	/****************************
	 ***** B: BOARD METHODS *****
	 ****************************/


	/***********************************
	 ***** C: ELICIT AGENT ACTIONS *****
	 ***********************************/

	private static RunThread runThread;
	private static GUI GUI;

	public static class RunThread extends Thread {

		int time;

		public RunThread(int time) {
			this.time = time * time;
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

	// public static void reset() {
	// 	removeObjects();
	// 	initialize();
	// 	GUI.displayBoard();
	// 	displayObjects();
	// 	GUI.update();
	// }

	// public static void step() {
	// 	removeObjects();
	// 	for (Station a : robots)
	// 		a.agentDecision();
	// 	displayObjects();
	// 	GUI.update();
	// }

	// public static void stop() {
	// 	runThread.interrupt();
	// 	runThread.stop();
	// }

	// public static void displayObjects() {
	// 	for (Station agent : robots)
	// 		GUI.displayObject(agent);
	// 	for (Box box : boxes)
	// 		GUI.displayObject(box);
	// }

	// public static void removeObjects() {
	// 	for (Station agent : robots)
	// 		GUI.removeObject(agent);
	// 	for (Box box : boxes)
	// 		GUI.removeObject(box);
	// }

	// public static void associateGUI(GUI graphicalInterface) {
	// 	GUI = graphicalInterface;
	// }
}
