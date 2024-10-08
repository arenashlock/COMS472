package edu.iastate.cs472.proj1;

import java.util.Comparator;

/**
 * @author Aren Ashlock
 */

/**
 * This method compares two states in the lexicographical order of the board configuration. The 3X3 array representing each 
 * board configuration is converted into a sequence of nine digits starting at the 0th row, and within each row, at the 0th 
 * column. For example, the two states
 * 
 * 	   2 0 3        2 8 1 
 *     1 8 4        7 5 3 
 *     7 6 5        6 0 4 
 *
 * are converted into the sequences <2,0,3,1,8,4,7,6,5>, and <2,8,1,7,5,3,6,0,4>, respectively. By definition the first state is 
 * less than the second one.  
 * 
 * The comparator will be used for maintaining the CLOSED list used in the A* algorithm. 
 */
public class StateComparator implements Comparator<State> {
	@Override
	public int compare(State s1, State s2) {
		// TODO
		int[] flattenedS1 = s1.flattenBoard();
		int[] flattenedS2 = s2.flattenBoard();

		for(int i = 0; i < 9; i++) {
			if(flattenedS1[i] < flattenedS2[i]) {
				return -1;
			}
			else if(flattenedS1[i] > flattenedS2[i]) {
				return 1;
			}
		}

	    return 0;
	}  		
}