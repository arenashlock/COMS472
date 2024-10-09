package edu.iastate.cs472.proj1;

import java.io.FileNotFoundException;
import java.io.File;
import java.util.Scanner;

/**
 * @author Aren Ashlock
 */

public class PuzzleSolver {
	/**
	 *  Read an initial state from the input file.  Solve the eight puzzle three times:
	 *  
	 *      1) The first solution allows single moves only and uses the heuristic based on the number of mismatched tiles. 
	 *         
	 *      2) The second solution also allows single moves only but uses the heuristic based on the Manhattan distance. 
	 *         
	 *      3) The third solution allows single and double moves and uses the admissible heuristic designed by yourself.  
	 *         
	 *  Each solution is printed out as a sequence of states, generated by single/double moves, from the initial state to the 
	 *  goal state. If no solution exists, report it.  
	 * 
	 * @param args
	 * @throws FileNotFoundException if the input file does not exist 
	 * @throws IllegalArgumentException if the initial state from the file is not in the correct format
	 */
	public static void main(String[] args) throws FileNotFoundException, IllegalArgumentException {	
		// Read an initial board configuration from a file.
		State fileState = new State(args[0]);
			



		// JUST FOR TESTING THE DOUBLE MOVE HEURISTIC (the only thing left to check in State.java)
		//System.out.println("File state:\n" + fileState.toString());
		//State.heu = Heuristic.DoubleMoveHeuristic;
		//System.out.println("Cost = " + fileState.cost());





		// Call EightPuzzle.solve8puzzle() to solve the puzzle.
		//System.out.println(EightPuzzle.solve8Puzzle(fileState));
	}
}