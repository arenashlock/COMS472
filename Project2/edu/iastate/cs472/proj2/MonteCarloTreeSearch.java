package edu.iastate.cs472.proj2;

import java.util.Random;

/**
 * @author Aren Ashlock
 */

/**
 * This class implements the Monte Carlo tree search method to find the best move at the current state.
 */
public class MonteCarloTreeSearch extends AdversarialSearch {
	/**
     * The input parameter legalMoves contains all the possible moves.
     * It contains four integers: fromRow, fromCol, toRow, toCol which represents a move from (fromRow, fromCol) to (toRow, toCol).
     * It also provides a utility method `isJump` to see whether this move is a jump or a simple move.
     *
     * Each legalMove in the input now contains a single move or a sequence of jumps:
     * (rows[0], cols[0]) -> (rows[1], cols[1]) -> (rows[2], cols[2]).
     *
     * @param legalMoves All the legal moves for the agent at current step.
     */
    public CheckersMove makeMove(CheckersMove[] legalMoves) {
        // The checker board state can be obtained from this.board, which is an 2D array of the following integers defined below:
        // 0 - empty square, 1 - red man, 2 - red king, 3 - black man, 4 - black king
        System.out.println(board);
        System.out.println();

        // TODO - Done
        MCNode root = new MCNode(board, CheckersData.RED);

        for(int time = 0; time < 1000; time++) {
            // Selection
            MCNode select = selection(root);

            // Expansion
            MCNode expand = expansion(select);

            // Simulation
            int winner = simulation(expand);

            // Back Propagation
            backpropagate(winner, expand);
        }

        // All the possible states resulting from a move by black
        MCNode[] possibleStates = root.children();

        // All the possible moves from the root
        CheckersMove[] allMoves = root.state().getLegalMoves(CheckersData.BLACK);

        // Initialize to the first entry
        double maxPlayouts = possibleStates[0].playouts();
        CheckersMove bestMove = allMoves[0];

        // Find the state with the highest number of playouts
        for(int i = 1; i < possibleStates.length; i++) {
            // Found another state with more playouts
            if(possibleStates[i].playouts() > maxPlayouts) {
                bestMove = allMoves[i];
                maxPlayouts = possibleStates[i].playouts();
            }
        }
        
        // Return the move for the current state.
        return bestMove;
    }
    
    // TODO
    // 
    // Implement your helper methods here.
    // They include at least the methods for selection, expansion, simulation, and back-propagation. 
    // 
    // For representation of the search tree, you are suggested (but limited) to use a child-sibling tree already implemented 
    // in the two classes CSTree and CSNode (which  you may feel free to modify).
    // If you decide not to use the child-sibling tree, simply remove these two classes. 

    private MCNode selection(MCNode root) {
        MCNode select = root;

        // null indicates a leaf (which needs to be expanded)
        while(select.children() != null) {
            // Initialize to the first child
            MCNode potentialSelect = select.children()[0];

            double maxUCB;

            // Match the state initialization with the UCB value
            if(potentialSelect.playouts() != 0) {
                // Assignment-specified C value
                maxUCB = (potentialSelect.wins() / potentialSelect.playouts()) + (Math.sqrt(2) * Math.sqrt(Math.log(potentialSelect.parent().playouts()) / potentialSelect.playouts()));
                // Personally chosen C value
                //maxUCB = (potentialSelect.wins() / potentialSelect.playouts()) + (100 * Math.sqrt(Math.log(potentialSelect.parent().playouts()) / potentialSelect.playouts()));
            } else {
                maxUCB = Double.MAX_VALUE;
            }

            // Check the other children to see if there is a better selection
            for(int i = 1; i < select.children().length; i++) {
                // Go ahead and assume divide by 0 (will be replaced if that's not the case)
                double UCB = Double.MAX_VALUE;

                MCNode currChild = select.children()[i];

                // Need to check for dividing by 0
                if(currChild.playouts() != 0) {
                    UCB = (currChild.wins() / currChild.playouts()) + (Math.sqrt(2) * Math.sqrt(Math.log(currChild.parent().playouts()) / currChild.playouts()));
                }

                if(UCB > maxUCB) {
                    maxUCB = UCB;
                    potentialSelect = currChild;
                }
            }

            select = potentialSelect;
        }

        return select;
    }

    private MCNode expansion(MCNode select) {
        // Gotta switch which player takes the move
        int playerToMove;
        if(select.player() == CheckersData.RED) {
            playerToMove = CheckersData.BLACK;
        }
        else {
            playerToMove = CheckersData.RED;
        }

        CheckersMove[] legalMoves = select.state().getLegalMoves(playerToMove);

        if(legalMoves == null) {
            return select;
        }

        MCNode[] expand = new MCNode[legalMoves.length];

        for(int i = 0; i < expand.length; i++) {
            // Get the state after the move
            CheckersData newState = helperMove(select.state(), legalMoves[i]);

            // Keep track of the player
            int nextPlayer;
            if(select.player() == CheckersData.RED) {
                nextPlayer = CheckersData.BLACK;
            }
            else {
                nextPlayer = CheckersData.RED;
            }

            MCNode newNode = new MCNode(select, newState, nextPlayer);

            expand[i] = newNode;
        }

        // Keep track of all the children for the leaf (making it no longer a leaf)
        select.children(expand);

        Random rand = new Random();
        int selectedMove = rand.nextInt(expand.length);

        // Start the random exploration
        return expand[selectedMove];
    }

    private int simulation(MCNode expand) {
        MCNode simulationNode = expand;

        // Gotta switch which player takes the move
        int playerToMove;
        if(simulationNode.player() == CheckersData.RED) {
            playerToMove = CheckersData.BLACK;
        }
        else {
            playerToMove = CheckersData.RED;
        }

        CheckersMove[] legalMoves = simulationNode.state().getLegalMoves(playerToMove);
        
        while(legalMoves != null) {
            Random rand = new Random();
            int selectedMove = rand.nextInt(legalMoves.length);

            CheckersData newState = helperMove(simulationNode.state(), legalMoves[selectedMove]);

            simulationNode = new MCNode(newState, playerToMove);

            if(simulationNode.player() == CheckersData.RED) {
                playerToMove = CheckersData.BLACK;
            }
            else {
                playerToMove = CheckersData.RED;
            }

            legalMoves = simulationNode.state().getLegalMoves(playerToMove);
        }

        if(playerToMove == CheckersData.RED) {
            return CheckersData.BLACK;
        }
        else {
            return CheckersData.RED;
        }
    }

    private void backpropagate(int winner, MCNode expand) {
        MCNode backState = expand;

        while(backState != null) {
            // -1 symbolizes a draw (which I don't believe is possible...)
            if(winner == -1) {
                backState.draw();
            }

            else if(winner == expand.player()) {
                backState.win();
            }
            
            else {
                backState.loss();
            }

            backState = backState.parent();
        }
    }

    // Helper method for personal use to make a move
    private CheckersData helperMove(CheckersData previousState, CheckersMove move) {
        CheckersData newState = new CheckersData();

        for(int row = 0; row < 8; row++) {
            for(int col = 0; col < 8; col++) {
                newState.board[row][col] = previousState.board[row][col];
            }
        }

        newState.makeMove(move);

        return newState;
    }
}