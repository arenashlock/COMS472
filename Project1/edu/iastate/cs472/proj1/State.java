package edu.iastate.cs472.proj1;

import java.io.FileNotFoundException;
import java.io.File;
import java.util.Scanner;

/**
 * @author Aren Ashlock
 */

/**
 * This class represents a board configuration in the 8-puzzle.  Only the initial configuration is generated by a constructor, 
 * while intermediate configurations will be generated via calling the method successorState().  State objects will form two 
 * circular doubly-linked lists OPEN and CLOSED, which will be used by the A* algorithm to search for a path from a given 
 * initial board configuration to the final board configuration below: 
 * 
 *  1 2 3 
 *  8   4
 *  7 6 5
 *
 * The final configuration (i.e., the goal state) above is not explicitly represented as an object of the State class. 
 */

public class State implements Cloneable, Comparable<State> {
	public int[][] board; 		// configuration of tiles 
	
	public State previous;    	// previous node on the OPEN/CLOSED list
	public State next; 			// next node on the OPEN/CLOSED list
	public State predecessor; 	// predecessor node on the path from the initial state 
	
	public Move move;           // the move that generated this state from its predecessor
	public int numMoves; 	    // number of moves from the initial state to this state

	public static Heuristic heu; // heuristic used. shared by all the states. 
	
	private int numMismatchedTiles = -1;    // number of mismatched tiles between this state and the goal state; negative if not computed yet.
	private int ManhattanDistance = -1;     // Manhattan distance between this state and the goal state; negative if not computed yet. 
	private int numSingleDoubleMoves = -1;  // number of single and double moves with each double move counted as one; negative if not computed yet. 

	
	/**
	 * Constructor (for the initial state).  
	 * 
	 * It takes a 2-dimensional array representing an initial board configuration. The empty square is represented by the number 0.  
	 * 
	 *     a) Initialize all three links previous, next, and predecessor to null.  
	 *     b) Set move to null and numMoves to zero.
	 * 
	 * @param board
	 * @throws IllegalArgumentException		if board is not a 3X3 array or its nine entries are not respectively the digits 0, 1, ..., 8.
	 */
    public State(int[][] board) throws IllegalArgumentException {
        // Construct the board
        this.board = new int[3][3];

        // Array for checking duplicate values
        int[] duplicateCheck = {0, 0, 0, 0, 0, 0, 0, 0, 0};

        if(board.length != 3 || board[0].length != 3) {
            throw new IllegalArgumentException("Dimensions are incorrect, it should be a 3X3 array");
        }
        else {
            for(int i = 0; i < 3; i++) {
                for(int j = 0; j < 3; j++) {
                    int boardValue = board[i][j];

                    // Value isn't within bounds
                    if(boardValue < 0 || boardValue > 8) {
                        throw new IllegalArgumentException("Array contains an element not within 0,...,8");
                    }
                    // Value already exists in the board configuration
                    else if(duplicateCheck[boardValue] == 1) {
                        throw new IllegalArgumentException("Duplicate numbers found");
                    }
                    // Allowed value for board spot
                    else {
                        this.board[i][j] = boardValue;
                        duplicateCheck[boardValue]++;
                    }
                }
            }
        }

        previous = null;
        next = null;
        predecessor = null;

        move = null;
        numMoves = 0;
	}
    
    
    /**
     * Constructor (for the initial state) 
     * 
     * It takes a state from an input file that has three rows, each containing three digits separated by exactly one blank.
     * Every row starts with a digit. The nine digits are from 0 to 8 with no duplicates.  
     * 
     * Do the same initializations as for the first constructor. 
     * 
     * @param inputFileName
     * @throws FileNotFoundException
     * @throws IllegalArgumentException  if the file content does not meet the above requirements. 
     */
    public State(String inputFileName) throws FileNotFoundException, IllegalArgumentException {
    	// Find the file (throw an exception if it cannot be found)
        try {
            // Initialize the file tools
            File inputFile = new File(inputFileName);
            Scanner fileScanner = new Scanner(inputFile);

            // Construct the board
            board = new int[3][3];

            // Array for checking duplicate values
            int[] duplicateCheck = {0, 0, 0, 0, 0, 0, 0, 0, 0};

            // Iterate through the board (checking that the dimensions and values are correct)
            int i;
            for(i = 0; fileScanner.hasNextLine(); i++) {
                // Still in for loop means >= 3 rows, which is not allowed
                if(i == 3) {
                    throw new IllegalArgumentException("Dimensions are incorrect, it should be a 3X3 array");
                }
                else {
                    String fileLine = fileScanner.nextLine();
                    Scanner lineScanner = new Scanner(fileLine);

                    int j;
                    for(j = 0; lineScanner.hasNextInt(); j++) {
                        // Still in for loop means >= 3 columns, which is not allowed
                        if(j == 3) {
                            throw new IllegalArgumentException("Dimensions are incorrect, it should be a 3X3 array");
                        }
                        else {
                            int boardValue = lineScanner.nextInt();

                            // Value isn't within bounds
                            if(boardValue < 0 || boardValue > 8) {
                                throw new IllegalArgumentException("Array contains an element not within 0,...,8");
                            }
                            // Value already exists in the board configuration
                            else if(duplicateCheck[boardValue] == 1) {
                                throw new IllegalArgumentException("Duplicate numbers found");
                            }
                            // Allowed value for board spot
                            else {
                                board[i][j] = boardValue;
                                duplicateCheck[boardValue]++;
                            }
                        }
                    }
                    // Less than 3 columns
                    if(j < 3) {
                        throw new IllegalArgumentException("Dimensions are incorrect, it should be a 3X3 array");
                    }

                    lineScanner.close();
                }
            }
            // Less than 3 rows
            if(i < 3) {
                throw new IllegalArgumentException("Dimensions are incorrect, it should be a 3X3 array");
            }

            previous = null;
            next = null;
            predecessor = null;

            move = null;
            numMoves = 0;

            fileScanner.close();
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File not found, run this code again with a new filepath");
        }
	}
    
    
    /**
     * Generate the successor state resulting from a given move.  Throw an exception if the move cannot be executed. Besides 
     * setting the array board[][] properly, you also need to do the following:
     *     a) set the predecessor of the successor state to this state;
     *     b) set the private instance variable move of the successor state to the parameter m; 
     *     c) Set the links next and previous to null;  
     *     d) Set the variable numMoves for the successor state to this.numMoves + 1. 
     * 
     * @param m  one of the moves LEFT, RIGHT, UP, DOWN, DBL_LEFT, DBL_RIGHT, DBL_UP, and DBL_DOWN
     * @return null  			if the successor state is this.predecessor
     *         successor state  otherwise 
     * @throws IllegalArgumentException if LEFT when the empty square is in the right column, or  
     *                                  if RIGHT when the empty square is in the left column, or
     *                                  if UP when the empty square is in the bottom row, or 
     *                                  if DOWN when the empty square is in the top row, or
     *                                  if DBL_LEFT when the empty square is not in the left column, or 
     *                                  if DBL_RIGHT when the empty square is not in the right column, or 
     *                                  if DBL_UP when the empty square is not in the top row, or 
     *                                  if DBL_DOWN when the empty square is not in the bottom row. 
     */                                  
    public State successorState(Move m) throws IllegalArgumentException, CloneNotSupportedException {
        // Clone the state
        State sucState = (State) this.clone();

        // Find the 0's position to check if a move is legal and to perform it (if it is legal)
        int zeroPosition = this.findZero();
        int zeroRow = zeroPosition / 3;
        int zeroCol = zeroPosition % 3;

        // Check the move and perform it (if it is legal)
        if(m == Move.LEFT) {
            if(zeroCol == 2) {
                throw new IllegalArgumentException("Cannot perform LEFT move (0 is in the right column)");
            }
            else {
                int swap = sucState.board[zeroRow][zeroCol + 1];
                sucState.board[zeroRow][zeroCol + 1] = 0;
                sucState.board[zeroRow][zeroCol] = swap;
            }
        }
        else if(m == Move.RIGHT) {
            if(zeroCol == 0) {
                throw new IllegalArgumentException("Cannot perform RIGHT move (0 is in the left column)");
            }
            else {
                int swap = sucState.board[zeroRow][zeroCol - 1];
                sucState.board[zeroRow][zeroCol - 1] = 0;
                sucState.board[zeroRow][zeroCol] = swap;
            }
        }
        else if(m == Move.UP) {
            if(zeroRow == 2) {
                throw new IllegalArgumentException("Cannot perform UP move (0 is in the bottom row)");
            }
            else {
                int swap = sucState.board[zeroRow + 1][zeroCol];
                sucState.board[zeroRow + 1][zeroCol] = 0;
                sucState.board[zeroRow][zeroCol] = swap;
            }
        }
        else if(m == Move.DOWN) {
            if(zeroRow == 0) {
                throw new IllegalArgumentException("Cannot perform DOWN move (0 is in the top row)");
            }
            else {
                int swap = sucState.board[zeroRow - 1][zeroCol];
                sucState.board[zeroRow - 1][zeroCol] = 0;
                sucState.board[zeroRow][zeroCol] = swap;
            }
        }
        else if(m == Move.DBL_LEFT) {
            if(zeroCol != 0) {
                throw new IllegalArgumentException("Cannot perform DBL_LEFT move (0 is not in the left column)");
            }
            else {
                int firstSwap = sucState.board[zeroRow][zeroCol + 1];
                int secondSwap = sucState.board[zeroRow][zeroCol + 2];
                sucState.board[zeroRow][zeroCol + 2] = 0;
                sucState.board[zeroRow][zeroCol + 1] = secondSwap;
                sucState.board[zeroRow][zeroCol] = firstSwap;
            }
        }
        else if(m == Move.DBL_RIGHT) {
            if(zeroCol != 2) {
                throw new IllegalArgumentException("Cannot perform DBL_RIGHT move (0 is not in the right column)");
            }
            else {
                int firstSwap = sucState.board[zeroRow][zeroCol - 1];
                int secondSwap = sucState.board[zeroRow][zeroCol - 2];
                sucState.board[zeroRow][zeroCol - 2] = 0;
                sucState.board[zeroRow][zeroCol - 1] = secondSwap;
                sucState.board[zeroRow][zeroCol] = firstSwap;
            }
        }
        else if(m == Move.DBL_UP) {
            if(zeroRow != 0) {
                throw new IllegalArgumentException("Cannot perform DBL_UP move (0 is not in the top row)");
            }
            else {
                int firstSwap = sucState.board[zeroRow + 1][zeroCol];
                int secondSwap = sucState.board[zeroRow + 2][zeroCol];
                sucState.board[zeroRow + 2][zeroCol] = 0;
                sucState.board[zeroRow + 1][zeroCol] = secondSwap;
                sucState.board[zeroRow][zeroCol] = firstSwap;
            }
        }
        else {
            if(zeroRow != 2) {
                throw new IllegalArgumentException("Cannot perform DBL_DOWN move (0 is not in the bottom row)");
            }
            else {
                int firstSwap = sucState.board[zeroRow - 1][zeroCol];
                int secondSwap = sucState.board[zeroRow - 2][zeroCol];
                sucState.board[zeroRow - 2][zeroCol] = 0;
                sucState.board[zeroRow - 1][zeroCol] = secondSwap;
                sucState.board[zeroRow][zeroCol] = firstSwap;
            }
        }

        // Update the successor variables
        sucState.predecessor = this;
        sucState.move = m;
        sucState.numMoves = numMoves + 1;

        // Update the predecessor variables
        next = null;
        previous = null;

    	return sucState; 
    }
    
        
    /**
     * Determines if the board configuration in this state can be rearranged into the goal configuration. According to the 
     * PowerPoint notes that introduce the 8-puzzle, we check if this state has an odd number of inversions. 
     * 
     * @return true if the puzzle starting in this state can be rearranged into the goal state.
     */
    public boolean solvable() {
        // Flatten the matrix to make inversion counting easier
        int[] solvableArray = this.flattenBoard();

        // In the goal state, there are 7 inversions
		int goalInversions = 7;
		// We will count the number of inversions in the starting state
		int inversions = 0;

        // Counting the inversions
		for(int i = 0; i < 9; i++) {
			int currVal = solvableArray[i];

            // We don't care about the blank space
            if(currVal != 0) {
                for(int j = i + 1; j < 9; j++) {
                    if((currVal > solvableArray[j]) && (solvableArray[j] != 0)) {
                        inversions++;
                    }
                }
            }
		}
    
        // Odd number of inversions means it is NOT solvable
		if(((inversions - goalInversions) % 2) != 0) {
			return false;
		}
        else { // Even number of inversions
            return true;
        }
    }
    
    
    /**
     * Check if this state is the goal state, namely, if the array board[][] stores the following contents: 
     * 
     * 		1 2 3 
     * 		8 0 4 
     * 		7 6 5 
     * 
     * @return
     */
    public boolean isGoalState() {
        int[][] goalState = {{1, 2, 3}, {8, 0 ,4}, {7, 6, 5}};

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(board[i][j] != goalState[i][j]) {
                    return false;
                }
            }
        }

    	return true; 
    }
    
    
    /**
     * Write the board configuration according to the following format:
     *     a) Output row by row in three lines with no indentations.  
     *     b) Two adjacent tiles in each row have exactly one blank in between. 
     *     c) The empty square is represented by a blank.  
     *     
     * For example, 
     * 
     * 2   3
     * 1 8 4
     * 7 6 5  
     */
    @Override 
    public String toString() {
        String stateString = "";

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                // Character/number control
                if(board[i][j] != 0) {
                    stateString += board[i][j];
                } else {
                    stateString += " ";
                }

                // Whitespace control
                if((j == 2) && (i < 2)) {
                    stateString += "\n";
                } else {
                    stateString += " ";
                }
            }
        }

    	return stateString; 
    }
    
    
    /**
     * Create a clone of this State object by copying over the board[][]. Set the links previous, next, and predecessor to null. 
     * 
     * The method is called by SuccessorState(); 
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        Object clonedObject = super.clone();

        State clonedState = (State) clonedObject;

        clonedState.board = new int[3][3];
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                clonedState.board[i][j] = board[i][j];
            }
        }

        // MIGHT NEED TO SET HEURISTIC VALUES TO -1 (idk if they will be updated or not)
        clonedState.numMismatchedTiles = -1;
	    clonedState.ManhattanDistance = -1;
	    clonedState.numSingleDoubleMoves = -1;
        // -----------------------------------------------------------------------------

        clonedState.previous = null;
        clonedState.next = null;
        clonedState.predecessor = null;

    	return clonedState; 
    }
  

    /**
     * Compare this state with the argument state.  Two states are equal if their arrays board[][] have the same content.
     */
    @Override 
    public boolean equals(Object o) {
    	// TODO 
        State otherState = (State) o;

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(board[i][j] != otherState.board[i][j]) {
                    return false;
                }
            }
        }

    	return true; 
    }
        
    
    /**
     * Evaluate the cost of this state as the sum of the number of moves from the initial state and the estimated number of 
     * moves to the goal state using the heuristic stored in the instance variable heu. 
     * 
     * If heu == TileMismatch, add up numMoves and the return values from computeNumMismatchedTiles().
     * If heu == MahattanDist, add up numMoves and the return values of computeMahattanDistance(). 
     * If heu == DoubleMoveHeuristic, add up numMoves and the return value of computeNumSingleDoubleMoves(). 
     * 
     * @return estimated number of moves from the initial state to the goal state via this state.
     * @throws IllegalArgumentException if heuristic is none of TileMismatch, MahattanDist, DoubleMoveHeuristic. 
     */
    public int cost() throws IllegalArgumentException {
    	int estimatedCost = numMoves;

        if(heu == Heuristic.TileMismatch) {
            estimatedCost += computeNumMismatchedTiles();
        }
        else if(heu == Heuristic.ManhattanDist) {
            estimatedCost += computeManhattanDistance();
        }
        else if(heu == Heuristic.DoubleMoveHeuristic) {
            estimatedCost += computeNumSingleDoubleMoves();
        }
        else {
            throw new IllegalArgumentException("Heuristic not defined, cannot calculate the cost");
        }
        
    	return estimatedCost; 
    }

    
    /**
     * Compare two states by the cost. Let c1 and c2 be the costs of this state and the argument state s.
     * 
     * @return -1 if c1 < c2 
     *          0 if c1 = c2 
     *          1 if c1 > c2 
     *          
     * Call the method cost(). This comparison will be used in maintaining the OPEN list by the A* algorithm.
     */
    @Override
    public int compareTo(State s) {
        int c1 = this.cost();
        int c2 = s.cost();

        if(c1 < c2) {
            return -1;
        }
        else if(c1 == c2) {
            return 0;
        }
        else {
            return 1;
        }
    }
    

    /**
     * Return the value of the private variable numMismatchedTiles if it is non-negative, and compute its value otherwise. 
     * 
     * @return the number of mismatched tiles between this state and the goal state. 
     */
	private int computeNumMismatchedTiles() {
        if(numMismatchedTiles < 0) {
            // COMPUTE
            int[] goalArray = {1, 2, 3, 8, 0, 4, 7, 6, 5};
            int[] flatBoard = this.flattenBoard();
            int mismatched = 0;

            for(int i = 0; i < 9; i++) {
                if((flatBoard[i] != 0) && (flatBoard[i] != goalArray[i])) {
                    mismatched++;
                }
            }

            numMismatchedTiles = mismatched;
        }

		return numMismatchedTiles; 
	}

	
	/**
	 * Return the value of the private variable ManhattanDistance if it is non-negative, and compute its value otherwise.
	 * 
	 * @return the Manhattan distance between this state and the goal state. 
	 */
	private int computeManhattanDistance() {
        if(ManhattanDistance < 0) {
            // The positions each tile should be in (in an array) : 0->4, 1->0, 2->1, ..., 8->3
            int[] goalSpots = {4, 0, 1, 2, 5, 8, 7, 6, 3};
            int[] flatBoard = this.flattenBoard();
            int manhattan = 0;

            for(int i = 0; i < 9; i++) {
                int currVal = flatBoard[i];
                if(currVal != 0) {
                    int currRow = i / 3;
                    int currCol = i % 3;

                    int goalPosition = goalSpots[currVal];
                    int goalRow = goalPosition / 3;
                    int goalCol = goalPosition % 3;

                    int colsOff = currCol - goalCol;
                    if(colsOff < 0) {
                        colsOff *= -1;
                    }
                    int rowsOff = currRow - goalRow;
                    if(rowsOff < 0) {
                        rowsOff *= -1;
                    }

                    manhattan += (colsOff + rowsOff);
                }
            }

            ManhattanDistance = manhattan;
        }

		return ManhattanDistance; 
	}
	
	
	/**
	 * Return the value of the private variable numSingleDoubleMoves if it is non-negative, and compute its value otherwise. 
	 * 
	 * @return the value of the private variable numSingleDoubleMoves that bounds from below the number of moves, 
	 *         single or double, which will take this state to the goal state.
	 */
	private int computeNumSingleDoubleMoves() {
		// TODO 
        if(numSingleDoubleMoves < 0) {
            // COMPUTE
        }

		return numSingleDoubleMoves; 
	}

    /**
     * Helper function added to flatten a board from a matrix to an array. Meant to make it easier to determine lexicographical order and solvability.
     */
    public int[] flattenBoard() {
        int[] matrixToArray = new int[9];
        int idx = 0;

        for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				matrixToArray[idx] = board[i][j];
				idx++;
			}
		}

        return matrixToArray;
    }

    /**
     * Helper function for the successorState() method. Finds where the 0 (empty square) is so I can check for illegal moves and to conduct a move.
     */
    public int findZero() {
        int[] boardArray = this.flattenBoard();
        int i;

        for(i = 0; i < 9; i++) {
            if(boardArray[i] == 0) {
                break;
            }
        }

        return i;
    }
}