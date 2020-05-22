package packagedelivery;
// Java program to print all combination of size r in an array of si
import java.io.*; 
import java.util.*;
  
public class Combination { 
  
    /* arr[]  ---> Input Array 
    data[] ---> Temporary array to store current combination 
    start & end ---> Staring and Ending indexes in arr[] 
    index  ---> Current index in data[] 
    r ---> Size of a combination to be printed */

    public Combination(){

    }
    private ArrayList<ArrayList<Integer>> allCombs = new ArrayList<ArrayList<Integer>>(2);
    private int combsFound = 0;

    public void combinationUtil(ArrayList<Integer> arr, int n, int r, int index, 
                                ArrayList<Integer> data, int i) 
    { 
        // Current combination is ready to be printed, print it 
        if (index == r) 
        { 
            allCombs.add(combsFound, new ArrayList<Integer>());
            for (int j=0; j<r; j++) {
                // System.out.print(data.get(j) +" "); 
                allCombs.get(combsFound).add(j, data.get(j));
            }
            combsFound++;
            // System.out.println(""); 
            return; 
        } 
    
        // When no more elements are there to put in data[] 
        if (i >= n) 
        return; 
  
        // current is included, put next at next location 
        data.add(index, arr.get(i));
        combinationUtil(arr, n, r, index+1, data, i+1); 
  
        // current is excluded, replace it with next (Note that 
        // i+1 is passed, but index is not changed) 
        combinationUtil(arr, n, r, index, data, i+1); 
    } 
  
    // The main function that prints all combinations of size r 
    // in arr[] of size n. This function mainly uses combinationUtil() 
    public ArrayList<ArrayList<Integer>> printCombination(ArrayList<Integer> arr, int n, int r) 
    { 
        // A temporary array to store all combination one by one 
        allCombs = new ArrayList<ArrayList<Integer>>(2);
        combsFound = 0;
        ArrayList<Integer> data = new ArrayList<Integer>(r); 
  
        // Print all combination using temprary array 'data[]' 
        combinationUtil(arr, n, r, 0, data, 0); 
        return allCombs;
    } 
  
} 
