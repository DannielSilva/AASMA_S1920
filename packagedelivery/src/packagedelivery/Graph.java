package packagedelivery;

import java.util.*;
import java.util.Random;

public class Graph { 
	
	// A utility function to add an edge in an 
	// undirected graph 
    private int nIslands = 2;
	private	int nStations; 
    private int nAirports = 3;
    private int nSeaports = 3;
    private ArrayList<ArrayList<Integer> > landAux;
    private ArrayList<ArrayList<Integer> > land;
    private ArrayList<ArrayList<Integer> > sea;  
    private ArrayList<ArrayList<Integer> > sky;   

    public Graph(int stations) {
		nStations = stations;
        landAux = new ArrayList<ArrayList<Integer> >(nIslands); 
        land    = new ArrayList<ArrayList<Integer> >(nStations); 
        sea     = new ArrayList<ArrayList<Integer> >(nStations); 
        sky     = new ArrayList<ArrayList<Integer> >(nStations); 

	}
    
	public static void addEdge(ArrayList<ArrayList<Integer> > adj, 
						int u, int v) 
	{ 
		adj.get(u).add(v); 
		adj.get(v).add(u); 
	} 

	// A utility function to print the adjacency list 
	// representation of graph 
	public static void printGraph(ArrayList<ArrayList<Integer> > adj) 
	{ 
		for (int i = 0; i < adj.size(); i++) { 
            if(adj.get(i).size() == 0) continue;
			System.out.println("\nNeighbour list of station " + i); 
			for (int j = 0; j < adj.get(i).size(); j++) { 
				System.out.print(" -> "+adj.get(i).get(j)); 
			} 
			System.out.println(); 
		} 
        System.out.println(); 
	} 

	// Driver Code 
	public void buildGraph() 
	{   
        Random rand = new Random();
        Combination comb = new Combination();

		// Creating a graph with 10 stations and 2 continents to divide them into

        int air_per_island = nAirports / nIslands;
        int extra_air = nAirports % nIslands;
        int sea_per_island = nSeaports / nIslands;
        int extra_sea = nSeaports % nIslands;

        // Groups the stations into continents

        for (int i = 0; i < nIslands; i++)
			landAux.add(new ArrayList<Integer>()); 

        // Denotes which stations have seaports (At least one from each continent)
        ArrayList<Integer> seaAux = new ArrayList<Integer>(nSeaports);

        // Denotes which stations have airports (At least one from each continent) 
        ArrayList<Integer> skyAux = new ArrayList<Integer>(nAirports);
    
		
		for (int i = 0; i < nStations; i++){
			land.add(new ArrayList<Integer>()); 
            sea.add(new ArrayList<Integer>());
            sky.add(new ArrayList<Integer>());

            //Grouping the stations
            landAux.get(rand.nextInt(nIslands)).add(i);
        }

        // Choosing both airports and seaports
        for (int i = 0; i < nIslands; i++){

            int assigningSea = sea_per_island;
            if (i == nIslands - 1) assigningSea += extra_sea;
            while (assigningSea > 0){
                int novo = landAux.get(i).get(rand.nextInt(landAux.get(i).size()));
                if(!seaAux.contains(novo)){
                    seaAux.add(novo);
                    assigningSea--;
                }
            }

            int assigningAir = air_per_island;
            if (i == 0) assigningAir += extra_air;
            while (assigningAir > 0){
                int novo = landAux.get(i).get(rand.nextInt(landAux.get(i).size()));
                if(!skyAux.contains(novo)){
                    skyAux.add(novo);
                    assigningAir--;
                }
            }
        }

        System.out.print("Continents as follows");
        System.out.println();
        printGraph(landAux);

        // Sea graph
        System.out.println("-----------------");
        System.out.println("Seaports selected");
        for( int element : seaAux){
            System.out.print(" -> " + element);
        }
        System.out.println();
        ArrayList<ArrayList<Integer>> seaedges = new ArrayList<ArrayList<Integer>>(2);
        seaedges = comb.printCombination(seaAux, seaAux.size(), 2);
        for (int indice = 0; indice < seaedges.size(); indice ++){
            addEdge(sea,seaedges.get(indice).get(0), seaedges.get(indice).get(1) );
        }
        System.out.println("Sea graph :");
        printGraph(sea);

        // Air Graph
        System.out.println("-----------------");
        System.out.println("Airports selected");
        for( int element : skyAux){
            System.out.print(" -> " + element);
        }
        System.out.println();
        ArrayList<ArrayList<Integer>> skyedges = new ArrayList<ArrayList<Integer>>(2);
        skyedges = comb.printCombination(skyAux, skyAux.size(), 2);
        for (int indice = 0; indice < skyedges.size(); indice ++){
            addEdge(sky,skyedges.get(indice).get(0), skyedges.get(indice).get(1) );
        }
        System.out.println("Sky graph :");
        printGraph(sky);



        for (int i = 0; i < nIslands; i++){
            for(int j = 0; j < landAux.get(i).size(); j++){
                for(int k = 0; k < 2; k++){
                    int connectTo = landAux.get(i).get(rand.nextInt(landAux.get(i).size()));
                    if(connectTo != landAux.get(i).get(j)){
                        if(!land.get(landAux.get(i).get(j)).contains(connectTo))
                            addEdge(land, landAux.get(i).get(j), connectTo );
                    }
                }
            }
        }
        System.out.println("-----------------");
        System.out.println("Land graph :");
        printGraph(land);

	} 

	/****************************
	 ***** B: MAP METHODS *****
	 ****************************/


    public static ArrayList<ArrayList<Integer>> getContinents() {
		return landAux;
	}

	public static ArrayList<ArrayList<Integer> > getSeaMap() {
		return sea;
	}

	public static ArrayList<ArrayList<Integer>> getLandGraph() {
		return land;
	}

	public static ArrayList<ArrayList<Integer>> getSkyGraph() {
		return sky;
	}

} 
