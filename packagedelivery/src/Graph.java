import java.util.*;
import java.util.Random;

public class Graph { 
	
	// A utility function to add an edge in an 
	// undirected graph 
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
	public static void main(String[] args) 
	{   
        Random rand = new Random();
        Combination comb = new Combination();

		// Creating a graph with 10 stations and 2 continents to divide them into
        int nIslands = 2;
		int nStations = 10; 
        int nAirports = 3;
        int nSeaports = 3;

        int air_per_island = nAirports / nIslands;
        int extra_air = nAirports % nIslands;
        int sea_per_island = nSeaports / nIslands;
        int extra_sea = nSeaports % nIslands;

        // Groups the stations into continents
        ArrayList<ArrayList<Integer> > landAux = new ArrayList<ArrayList<Integer> >(nIslands); 

        for (int i = 0; i < nIslands; i++)
			landAux.add(new ArrayList<Integer>()); 

        // Denotes which stations have seaports (At least one from each continent)
        ArrayList<Integer> seaAux = new ArrayList<Integer>(nSeaports);

        // Denotes which stations have airports (At least one from each continent) 
        ArrayList<Integer> skyAux = new ArrayList<Integer>(nAirports);

		ArrayList<ArrayList<Integer> > land = new ArrayList<ArrayList<Integer> >(nStations); 
        ArrayList<ArrayList<Integer> > sea  = new ArrayList<ArrayList<Integer> >(nStations); 
        ArrayList<ArrayList<Integer> > sky  = new ArrayList<ArrayList<Integer> >(nStations); 
    
		
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


        // LandGraph
        // for(int i = 0; i < nIslands; i++){
        //     for(int j = 0; j < landAux.get(i).size(); j++){

        //     }
        // }







        // for (int i = 0; i < nIslands; i++){
		// 	for( int j : landAux.get(i)){
        //         for( int k : landAux.get(i)){
        //             if (j < k){
        //                 // double p = (double)1/landAux.get(i).size();
        //                 // double p = (double)1/2;
        //                 if  (rand.nextDouble() <= p)
        //                     addEdge(land, j, k);
        //             }
        //         }
        //     } 
        // }


		// // Adding edges one by one 
		// addEdge(land, 0, 1); 
		
        
        // printGraph(landAux);
		// printGraph(land);
        // printGraph(sea);
        // printGraph(sky); 
	} 
} 
