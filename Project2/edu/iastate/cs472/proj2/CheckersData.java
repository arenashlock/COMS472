package edu.iastate.cs472.proj2;

import java.util.ArrayList;

/**
 * @author Aren Ashlock
 */

/**
 * An object of this class holds data about a game of checkers.
 * It knows what kind of piece is on each square of the checkerboard.
 * Note that RED moves "up" the board (i.e. row number decreases) while BLACK moves "down" the board (i.e. row number increases).
 * Methods are provided to return lists of available legal moves.
 */
public class CheckersData {
    // The following constants represent the possible contents of a square on the board. 
    // The constants RED and BLACK also represent players in the game.
    static final int
        EMPTY = 0,
        RED = 1,
        RED_KING = 2,
        BLACK = 3,
        BLACK_KING = 4;

    int[][] board;  // board[r][c] is the contents of row r, column c.

    /**
     * Constructor.  Create the board and set it up for a new game.
     */
    CheckersData() {
        board = new int[8][8];
        setUpGame();
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < board.length; i++) {
            int[] row = board[i];
            sb.append(8 - i).append(" ");
            for (int n : row) {
                if (n == 0) {
                    sb.append(" ");
                } else if (n == 1) {
                    sb.append(ANSI_RED + "R" + ANSI_RESET);
                } else if (n == 2) {
                    sb.append(ANSI_RED + "K" + ANSI_RESET);
                } else if (n == 3) {
                    sb.append(ANSI_YELLOW + "B" + ANSI_RESET);
                } else if (n == 4) {
                    sb.append(ANSI_YELLOW + "K" + ANSI_RESET);
                }
                sb.append(" ");
            }
            sb.append(System.lineSeparator());
        }
        sb.append("  a b c d e f g h");

        return sb.toString();
    }

    /**
     * Set up the board with checkers in position for the beginning of a game.
     * Note that checkers can only be found in squares that satisfy  row % 2 == col % 2.
     * At the start of the game, all such squares in the first three rows contain black squares and all such squares in 
     * the last three rows contain red squares.
     */
    void setUpGame() {
        // TODO - Done
    	// Set up the board with pieces BLACK, RED, and EMPTY
        for(int row = 0; row < 8; row++) {
            for(int col = 0; col < 8; col++) {
                if(row % 2 == col % 2) {
                    if(row < 3) {
                        board[row][col] = BLACK;
                    } else if(row > 4) {
                        board[row][col] = RED;
                    } else {
                        board[row][col] = EMPTY;
                    }
                } else {
                    board[row][col] = EMPTY;
                }
            }
        }
    }

    /**
     * Return the contents of the square in the specified row and column.
     */
    int pieceAt(int row, int col) {
        return board[row][col];
    }

    /**
     * Make the specified move.
     * It is assumed that move is non-null and that the move it represents is legal.
     *
     * Make a single move or a sequence of jumps recorded in rows and cols.
     */
    void makeMove(CheckersMove move) {
        int l = move.rows.size();
        for(int i = 0; i < l - 1; i++)
            makeMove(move.rows.get(i), move.cols.get(i), move.rows.get(i + 1), move.cols.get(i + 1));
    }

    /**
     * Make the move from (fromRow,fromCol) to (toRow,toCol).
     * It is assumed that this move is legal.
     * If the move is a jump, the jumped piece is removed from the board.
     * If a piece moves to the last row on the opponent's side of the board, the piece becomes a king.
     *
     * @param fromRow row index of the from square
     * @param fromCol column index of the from square
     * @param toRow   row index of the to square
     * @param toCol   column index of the to square
     */
    void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        // TODO - Done
    	// Update the board for the given move. You need to take care of the following situations:
        // 1. move the piece from (fromRow,fromCol) to (toRow,toCol)
        // 2. if this move is a jump, remove the captured piece
        // 3. if the piece moves into the kings row on the opponent's side of the board, crowned it as a king
        int movedPiece;
        boolean isJump = false;

        // Get the kind of piece
        if(board[fromRow][fromCol] == RED) {
            movedPiece = RED;
        } else if(board[fromRow][fromCol] == RED_KING) {
            movedPiece = RED_KING;
        } else if(board[fromRow][fromCol] == BLACK) {
            movedPiece = BLACK;
        } else {
            movedPiece = BLACK_KING;
        }

        // (1) Move the piece
        board[fromRow][fromCol] = EMPTY;
        board[toRow][toCol] = movedPiece;

        // Figure out if the move is a jump
        if(fromRow % 2 == toRow % 2) {
            isJump = true;
        }

        // (2) Remove the piece that got captures
        if(isJump) {
            if((toRow - fromRow) > 0) {
                if((toCol - fromCol) > 0) {
                    board[toRow - 1][toCol - 1] = EMPTY;
                } else {
                    board[toRow - 1][toCol + 1] = EMPTY;
                }
            } else {
                if((toCol - fromCol) > 0) {
                    board[toRow + 1][toCol - 1] = EMPTY;
                } else {
                    board[toRow + 1][toCol + 1] = EMPTY;
                }
            }
        }

        // (3) Crown the piece as a king if it reached the opposite side
        if(movedPiece == RED && toRow == 0) {
            board[toRow][toCol] = RED_KING;
        } else if(movedPiece == BLACK && toRow == 7) {
            board[toRow][toCol] = BLACK_KING;
        }
    }

    /**
     * Return an array containing all the legal CheckersMoves for the specified player on the current board.
     * If the player has no legal moves, null is returned.
     * The value of player should be one of the constants RED or BLACK; if not, null is returned.
     * If the returned value is non-null, it consists entirely of jump moves or entirely of regular moves, since if the 
     * player can jump, only jumps are legal moves.
     *
     * @param player color of the player, RED or BLACK
     */
    CheckersMove[] getLegalMoves(int player) {
        // TODO - Done
        // Not a valid player, return null
        if(player != RED && player != BLACK) {
            return null;
        }

        // Use an ArrayList since the number of legal jumps is unknown
        ArrayList<CheckersMove> legalMovesAL = new ArrayList<>();

        // Keep track of whether legal moves are simply moves or if they are jumps
        boolean movesAreJumps = false;

        for(int row = 0; row < 8; row++) {
            for(int col = 0; col < 8; col++) {

                                    // -------------------- RED PLAYER MOVES --------------------
                if(player == RED) {
                    // Check if there are jumps for the piece
                    CheckersMove[] legalJumps = getLegalJumpsFrom(player, row, col);

                    // If there are jumps
                    if(legalJumps != null) {
                        // If these were the first jumps found, reset the ArrayList and update the boolean
                        if(movesAreJumps == false) {
                            legalMovesAL.clear();
                            movesAreJumps = true;
                        }
                            
                        // Add the jumps to the ArrayList
                        for(int i = 0; i < legalJumps.length; i++) {
                            legalMovesAL.add(legalJumps[i]);
                        }
                    }

                    // If there haven't been any jumps found yet, add normal moves
                    if(movesAreJumps == false) {

// ---------------------------------------- Get legal moves for the normal red pieces ----------------------------------------

                        if(board[row][col] == RED) {
                            // In the left column (can only move right)
                            if(col == 0) {
                                // Check if the square directly NE is empty
                                if(board[row - 1][col + 1] == EMPTY) {
                                    legalMovesAL.add(new CheckersMove(row, col, row - 1, col + 1));
                                }
                            }

                            // In the right column (can only move left)
                            else if(col == 7) {
                                // Check if the square directly NW is empty
                                if(board[row - 1][col - 1] == EMPTY) {
                                    legalMovesAL.add(new CheckersMove(row, col, row - 1, col - 1));
                                }
                            }

                            // In any other column (can move right or left)
                            else {
                                // Check if the square directly NE is empty
                                if(board[row - 1][col + 1] == EMPTY) {
                                    legalMovesAL.add(new CheckersMove(row, col, row - 1, col + 1));
                                }

                                // Check if the square directly NW is empty
                                if(board[row - 1][col - 1] == EMPTY) {
                                    legalMovesAL.add(new CheckersMove(row, col, row - 1, col - 1));
                                }
                            }
                        }

// ---------------------------------------------------------------------------------------------------------------------------
                    
// ---------------------------------------- Get legal moves for the red king pieces ----------------------------------------

                        if(board[row][col] == RED_KING) {
                            // In the left column (can only move right)
                            if(col == 0) {
                                // In the top left corner (can only move SE)
                                if(row == 0) {
                                    // Check if the square directly SE is empty
                                    if(board[row + 1][col + 1] == EMPTY) {
                                        legalMovesAL.add(new CheckersMove(row, col, row + 1, col + 1));
                                    }
                                }

                                // In the bottom left corner (can only move NE)
                                else if(row == 7) {
                                    // Check if the square directly NE is empty
                                    if(board[row - 1][col + 1] == EMPTY) {
                                        legalMovesAL.add(new CheckersMove(row, col, row - 1, col + 1));
                                    }
                                }

                                // Anywhere else in the left column (can move up or down)
                                else {
                                    // Check if the square directly SE is empty
                                    if(board[row + 1][col + 1] == EMPTY) {
                                        legalMovesAL.add(new CheckersMove(row, col, row + 1, col + 1));
                                    }

                                    // Check if the square directly NE is empty
                                    if(board[row - 1][col + 1] == EMPTY) {
                                        legalMovesAL.add(new CheckersMove(row, col, row - 1, col + 1));
                                    }
                                }
                            }

                            // In the right column (can only move left)
                            else if(col == 7) {
                                // In the top right corner (can only move SW)
                                if(row == 0) {
                                    // Check if the square directly SW is empty
                                    if(board[row + 1][col - 1] == EMPTY) {
                                        legalMovesAL.add(new CheckersMove(row, col, row + 1, col - 1));
                                    }
                                }

                                // In the bottom right corner (can only move NW)
                                else if(row == 7) {
                                    // Check if the square directly NW is empty
                                    if(board[row - 1][col - 1] == EMPTY) {
                                        legalMovesAL.add(new CheckersMove(row, col, row - 1, col - 1));
                                    }
                                }

                                // Anywhere else in the right column (can move up or down)
                                else {
                                    // Check if the square directly SW is empty
                                    if(board[row + 1][col - 1] == EMPTY) {
                                        legalMovesAL.add(new CheckersMove(row, col, row + 1, col - 1));
                                    }

                                    // Check if the square directly NW is empty
                                    if(board[row - 1][col - 1] == EMPTY) {
                                        legalMovesAL.add(new CheckersMove(row, col, row - 1, col - 1));
                                    }
                                }
                            }

                            // In the top row (can only move down)
                                // NOTE: won't be in a top corner since that's checked already!
                            else if(row == 0) {
                                // Check if the square directly SW is empty
                                if(board[row + 1][col - 1] == EMPTY) {
                                    legalMovesAL.add(new CheckersMove(row, col, row + 1, col - 1));
                                }

                                // Check if the square directly SE is empty
                                if(board[row + 1][col + 1] == EMPTY) {
                                    legalMovesAL.add(new CheckersMove(row, col, row + 1, col + 1));
                                }
                            }

                            // In the bottom row (can only move up)
                                // NOTE: won't be in a bottom corner since that's checked already!
                            else if(row == 7) {
                                // Check if the square directly NW is empty
                                if(board[row - 1][col - 1] == EMPTY) {
                                    legalMovesAL.add(new CheckersMove(row, col, row - 1, col - 1));
                                }

                                // Check if the square directly NE is empty
                                if(board[row - 1][col + 1] == EMPTY) {
                                    legalMovesAL.add(new CheckersMove(row, col, row - 1, col + 1));
                                }
                            }

                            // Anywhere else on the board (can move in all 4 directions)
                            else {
                                // Check if the square directly NW is empty
                                if(board[row - 1][col - 1] == EMPTY) {
                                    legalMovesAL.add(new CheckersMove(row, col, row - 1, col - 1));
                                }

                                // Check if the square directly NE is empty
                                if(board[row - 1][col + 1] == EMPTY) {
                                    legalMovesAL.add(new CheckersMove(row, col, row - 1, col + 1));
                                }

                                // Check if the square directly SW is empty
                                if(board[row + 1][col - 1] == EMPTY) {
                                    legalMovesAL.add(new CheckersMove(row, col, row + 1, col - 1));
                                }

                                // Check if the square directly SE is empty
                                if(board[row + 1][col + 1] == EMPTY) {
                                    legalMovesAL.add(new CheckersMove(row, col, row + 1, col + 1));
                                }
                            }
                        }

// -------------------------------------------------------------------------------------------------------------------------

                    }
                }

                                // -------------------- BLACK PLAYER MOVES --------------------
                else {
                    // Check if there are jumps for the piece
                    CheckersMove[] legalJumps = getLegalJumpsFrom(player, row, col);

                    // If there are jumps
                    if(legalJumps != null) {
                        // If these were the first jumps found, reset the ArrayList and update the boolean
                        if(movesAreJumps == false) {
                            legalMovesAL.clear();
                            movesAreJumps = true;
                        }
                            
                        // Add the jumps to the ArrayList
                        for(int i = 0; i < legalJumps.length; i++) {
                            legalMovesAL.add(legalJumps[i]);
                        }
                    }

                    // If there haven't been any jumps found yet, add normal moves
                    if(movesAreJumps == false) {

// ---------------------------------------- Get legal moves for the normal black pieces ----------------------------------------

                        if(board[row][col] == BLACK) {
                            // In the left column (can only move right)
                            if(col == 0) {
                                // Check if the square directly SE is empty
                                if(board[row + 1][col + 1] == EMPTY) {
                                    legalMovesAL.add(new CheckersMove(row, col, row + 1, col + 1));
                                }
                            }

                            // In the right column (can only move left)
                            else if(col == 7) {
                                // Check if the square directly SW is empty
                                if(board[row + 1][col - 1] == EMPTY) {
                                    legalMovesAL.add(new CheckersMove(row, col, row + 1, col - 1));
                                }
                            }

                            // In any other column (can move right or left)
                            else {
                                // Check if the square directly SE is empty
                                if(board[row + 1][col + 1] == EMPTY) {
                                    legalMovesAL.add(new CheckersMove(row, col, row + 1, col + 1));
                                }

                                // Check if the square directly SW is empty
                                if(board[row + 1][col - 1] == EMPTY) {
                                    legalMovesAL.add(new CheckersMove(row, col, row + 1, col - 1));
                                }
                            }
                        }

// -----------------------------------------------------------------------------------------------------------------------------
                    
// ---------------------------------------- Get legal moves for the black king pieces ----------------------------------------

                        if(board[row][col] == BLACK_KING) {
                            // In the left column (can only move right)
                            if(col == 0) {
                                // In the top left corner (can only move SE)
                                if(row == 0) {
                                    // Check if the square directly SE is empty
                                    if(board[row + 1][col + 1] == EMPTY) {
                                        legalMovesAL.add(new CheckersMove(row, col, row + 1, col + 1));
                                    }
                                }

                                // In the bottom left corner (can only move NE)
                                else if(row == 7) {
                                    // Check if the square directly NE is empty
                                    if(board[row - 1][col + 1] == EMPTY) {
                                        legalMovesAL.add(new CheckersMove(row, col, row - 1, col + 1));
                                    }
                                }

                                // Anywhere else in the left column (can move up or down)
                                else {
                                    // Check if the square directly SE is empty
                                    if(board[row + 1][col + 1] == EMPTY) {
                                        legalMovesAL.add(new CheckersMove(row, col, row + 1, col + 1));
                                    }

                                    // Check if the square directly NE is empty
                                    if(board[row - 1][col + 1] == EMPTY) {
                                        legalMovesAL.add(new CheckersMove(row, col, row - 1, col + 1));
                                    }
                                }
                            }

                            // In the right column (can only move left)
                            else if(col == 7) {
                                // In the top right corner (can only move SW)
                                if(row == 0) {
                                    // Check if the square directly SW is empty
                                    if(board[row + 1][col - 1] == EMPTY) {
                                        legalMovesAL.add(new CheckersMove(row, col, row + 1, col - 1));
                                    }
                                }

                                // In the bottom right corner (can only move NW)
                                else if(row == 7) {
                                    // Check if the square directly NW is empty
                                    if(board[row - 1][col - 1] == EMPTY) {
                                        legalMovesAL.add(new CheckersMove(row, col, row - 1, col - 1));
                                    }
                                }

                                // Anywhere else in the right column (can move up or down)
                                else {
                                    // Check if the square directly SW is empty
                                    if(board[row + 1][col - 1] == EMPTY) {
                                        legalMovesAL.add(new CheckersMove(row, col, row + 1, col - 1));
                                    }

                                    // Check if the square directly NW is empty
                                    if(board[row - 1][col - 1] == EMPTY) {
                                        legalMovesAL.add(new CheckersMove(row, col, row - 1, col - 1));
                                    }
                                }
                            }

                            // In the top row (can only move down)
                                // NOTE: won't be in a top corner since that's checked already!
                            else if(row == 0) {
                                // Check if the square directly SW is empty
                                if(board[row + 1][col - 1] == EMPTY) {
                                    legalMovesAL.add(new CheckersMove(row, col, row + 1, col - 1));
                                }

                                // Check if the square directly SE is empty
                                if(board[row + 1][col + 1] == EMPTY) {
                                    legalMovesAL.add(new CheckersMove(row, col, row + 1, col + 1));
                                }
                            }

                            // In the bottom row (can only move up)
                                // NOTE: won't be in a bottom corner since that's checked already!
                            else if(row == 7) {
                                // Check if the square directly NW is empty
                                if(board[row - 1][col - 1] == EMPTY) {
                                    legalMovesAL.add(new CheckersMove(row, col, row - 1, col - 1));
                                }

                                // Check if the square directly NE is empty
                                if(board[row - 1][col + 1] == EMPTY) {
                                    legalMovesAL.add(new CheckersMove(row, col, row - 1, col + 1));
                                }
                            }

                            // Anywhere else on the board (can move in all 4 directions)
                            else {
                                // Check if the square directly NW is empty
                                if(board[row - 1][col - 1] == EMPTY) {
                                    legalMovesAL.add(new CheckersMove(row, col, row - 1, col - 1));
                                }

                                // Check if the square directly NE is empty
                                if(board[row - 1][col + 1] == EMPTY) {
                                    legalMovesAL.add(new CheckersMove(row, col, row - 1, col + 1));
                                }

                                // Check if the square directly SW is empty
                                if(board[row + 1][col - 1] == EMPTY) {
                                    legalMovesAL.add(new CheckersMove(row, col, row + 1, col - 1));
                                }

                                // Check if the square directly SE is empty
                                if(board[row + 1][col + 1] == EMPTY) {
                                    legalMovesAL.add(new CheckersMove(row, col, row + 1, col + 1));
                                }
                            }
                        }

// ---------------------------------------------------------------------------------------------------------------------------

                    }
                }
            }
        }

        if(legalMovesAL.size() == 0) {
            return null;
        }

        // Convert ArrayList to an array (for the return type)
        CheckersMove[] legalMoves = new CheckersMove[legalMovesAL.size()];
        legalMovesAL.toArray(legalMoves);

        return legalMoves;
    }

    /**
     * Return a list of the legal jumps that the specified player can make starting from the specified row and column.
     * If no such jumps are possible, null is returned.
     * The logic is similar to the logic of the getLegalMoves() method.
     *
     * Note that each CheckerMove may contain multiple jumps. 
     * Each move returned in the array represents a sequence of jumps until no further jump is allowed.
     *
     * @param player The player of the current jump, either RED or BLACK.
     * @param row    row index of the start square.
     * @param col    col index of the start square.
     */
    CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {
        // TODO - Done
        // Use an ArrayList since the number of legal jumps is unknown
        ArrayList<CheckersMove> legalJumpsAL = new ArrayList<>();

                                    // -------------------- RED PLAYER JUMPS --------------------
        if(player == RED) {

// ---------------------------------------- Get legal jumps for the normal red pieces ----------------------------------------

            if(board[row][col] == RED) {
                // Not in the top 2 rows (cannot legally jump if that's the case)
                if(row > 1) {
                    // In the left 2 columns (can only jump right)
                    if(col < 2) {
                        // Check if the square 2 tiles directly NE is empty and there's a black piece in between
                        if(board[row - 2][col + 2] == EMPTY && (board[row - 1][col + 1] == BLACK || board[row - 1][col + 1] == BLACK_KING)) {
                            legalJumpsAL.add(new CheckersMove(row, col, row - 2, col + 2));
                        }
                    }

                    // In the right 2 columns (can only jump left)
                    else if(col > 5) {
                        // Check if the square 2 tiles directly NW is empty and there's a black piece in between
                        if(board[row - 2][col - 2] == EMPTY && (board[row - 1][col - 1] == BLACK || board[row - 1][col - 1] == BLACK_KING)) {
                            legalJumpsAL.add(new CheckersMove(row, col, row - 2, col - 2));
                        }
                    }

                    // In any other column (can jump right or left)
                    else {
                        // Check if the square 2 tiles directly NE is empty and there's a black piece in between
                        if(board[row - 2][col + 2] == EMPTY && (board[row - 1][col + 1] == BLACK || board[row - 1][col + 1] == BLACK_KING)) {
                            legalJumpsAL.add(new CheckersMove(row, col, row - 2, col + 2));
                        }

                        // Check if the square 2 tiles directly NW is empty and there's a black piece in between
                        if(board[row - 2][col - 2] == EMPTY && (board[row - 1][col - 1] == BLACK || board[row - 1][col - 1] == BLACK_KING)) {
                            legalJumpsAL.add(new CheckersMove(row, col, row - 2, col - 2));
                        }
                    }
                }
            }

// ---------------------------------------------------------------------------------------------------------------------------

// ---------------------------------------- Get legal jumps for the red king pieces ----------------------------------------

            if(board[row][col] == RED_KING) {
                // In the left 2 cloumns (can only jump right)
                if(col < 2) {
                    // In the top left corner (can only jump SE)
                    if(row < 2) {
                        // Check if the square 2 tiles directly SE is empty and there's a black piece in between
                        if(board[row + 2][col + 2] == EMPTY && (board[row + 1][col + 1] == BLACK || board[row + 1][col + 1] == BLACK_KING)) {
                            legalJumpsAL.add(new CheckersMove(row, col, row + 2, col + 2));
                        }
                    }

                    // In the bottom left corner (can only move NE)
                    else if(row > 5) {
                        // Check if the square 2 tiles directly NE is empty and there's a black piece in between
                        if(board[row - 2][col + 2] == EMPTY && (board[row - 1][col + 1] == BLACK || board[row - 1][col + 1] == BLACK_KING)) {
                            legalJumpsAL.add(new CheckersMove(row, col, row - 2, col + 2));
                        }
                    }

                    // Anywhere else in the left column (can move up or down)
                    else {
                        // Check if the square 2 tiles directly SE is empty and there's a black piece in between
                        if(board[row + 2][col + 2] == EMPTY && (board[row + 1][col + 1] == BLACK || board[row + 1][col + 1] == BLACK_KING)) {
                            legalJumpsAL.add(new CheckersMove(row, col, row + 2, col + 2));
                        }

                        // Check if the square 2 tiles directly NE is empty and there's a black piece in between
                        if(board[row - 2][col + 2] == EMPTY && (board[row - 1][col + 1] == BLACK || board[row - 1][col + 1] == BLACK_KING)) {
                            legalJumpsAL.add(new CheckersMove(row, col, row - 2, col + 2));
                        }
                    }
                }

                // In the right 2 columns (can only jump left)
                else if(col > 5) {
                    // In the top right corner (can only jump SW)
                    if(row < 2) {
                        // Check if the square 2 tiles directly SW is empty and there's a black piece in between
                        if(board[row + 2][col - 2] == EMPTY && (board[row + 1][col - 1] == BLACK || board[row + 1][col - 1] == BLACK_KING)) {
                            legalJumpsAL.add(new CheckersMove(row, col, row + 2, col - 2));
                        }
                    }

                    // In the bottom right corner (can only move NW)
                    else if(row > 5) {
                        // Check if the square 2 tiles directly NW is empty and there's a black piece in between
                        if(board[row - 2][col - 2] == EMPTY && (board[row - 1][col - 1] == BLACK || board[row - 1][col - 1] == BLACK_KING)) {
                            legalJumpsAL.add(new CheckersMove(row, col, row - 2, col - 2));
                        }
                    }

                    // Anywhere else in the left column (can move up or down)
                    else {
                        // Check if the square 2 tiles directly SW is empty and there's a black piece in between
                        if(board[row + 2][col - 2] == EMPTY && (board[row + 1][col - 1] == BLACK || board[row + 1][col - 1] == BLACK_KING)) {
                            legalJumpsAL.add(new CheckersMove(row, col, row + 2, col - 2));
                        }

                        // Check if the square 2 tiles directly NW is empty and there's a black piece in between
                        if(board[row - 2][col - 2] == EMPTY && (board[row - 1][col - 1] == BLACK || board[row - 1][col - 1] == BLACK_KING)) {
                            legalJumpsAL.add(new CheckersMove(row, col, row - 2, col - 2));
                        }
                    }
                }

                // In the top 2 rows (can only jump down)
                    // NOTE: won't be in a top corner since that's checked already!
                else if(row < 2) {
                    // Check if the square 2 tiles directly SW is empty and there's a black piece in between
                    if(board[row + 2][col - 2] == EMPTY && (board[row + 1][col - 1] == BLACK || board[row + 1][col - 1] == BLACK_KING)) {
                        legalJumpsAL.add(new CheckersMove(row, col, row + 2, col - 2));
                    }

                    // Check if the square 2 tiles directly SE is empty and there's a black piece in between
                    if(board[row + 2][col + 2] == EMPTY && (board[row + 1][col + 1] == BLACK || board[row + 1][col + 1] == BLACK_KING)) {
                        legalJumpsAL.add(new CheckersMove(row, col, row + 2, col + 2));
                    }
                }

                // In the bottom 2 rows (can only jump up)
                    // NOTE: won't be in a top corner since that's checked already!
                else if(row > 5) {
                    // Check if the square 2 tiles directly NW is empty and there's a black piece in between
                    if(board[row - 2][col - 2] == EMPTY && (board[row - 1][col - 1] == BLACK || board[row - 1][col - 1] == BLACK_KING)) {
                        legalJumpsAL.add(new CheckersMove(row, col, row - 2, col - 2));
                    }

                    // Check if the square 2 tiles directly NE is empty and there's a black piece in between
                    if(board[row - 2][col + 2] == EMPTY && (board[row - 1][col + 1] == BLACK || board[row - 1][col + 1] == BLACK_KING)) {
                        legalJumpsAL.add(new CheckersMove(row, col, row - 2, col + 2));
                    }
                }

                // Anywhere else on the board (can jump in all 4 directions)
                else {
                    // Check if the square 2 tiles directly NW is empty and there's a black piece in between
                    if(board[row - 2][col - 2] == EMPTY && (board[row - 1][col - 1] == BLACK || board[row - 1][col - 1] == BLACK_KING)) {
                        legalJumpsAL.add(new CheckersMove(row, col, row - 2, col - 2));
                    }

                    // Check if the square 2 tiles directly NE is empty and there's a black piece in between
                    if(board[row - 2][col + 2] == EMPTY && (board[row - 1][col + 1] == BLACK || board[row - 1][col + 1] == BLACK_KING)) {
                        legalJumpsAL.add(new CheckersMove(row, col, row - 2, col + 2));
                    }

                    // Check if the square 2 tiles directly SW is empty and there's a black piece in between
                    if(board[row + 2][col - 2] == EMPTY && (board[row + 1][col - 1] == BLACK || board[row + 1][col - 1] == BLACK_KING)) {
                        legalJumpsAL.add(new CheckersMove(row, col, row + 2, col - 2));
                    }

                    // Check if the square 2 tiles directly SE is empty and there's a black piece in between
                    if(board[row + 2][col + 2] == EMPTY && (board[row + 1][col + 1] == BLACK || board[row + 1][col + 1] == BLACK_KING)) {
                        legalJumpsAL.add(new CheckersMove(row, col, row + 2, col + 2));
                    }
                }
            }

// -------------------------------------------------------------------------------------------------------------------------

        }

                                // -------------------- BLACK PLAYER JUMPS --------------------

        else {

// ---------------------------------------- Get legal jumps for the normal black pieces ----------------------------------------

            if(board[row][col] == BLACK) {
                // Not in the bottom 2 rows (cannot legally jump if that's the case)
                if(row < 6) {
                    // In the left 2 columns (can only jump right)
                    if(col < 2) {
                        // Check if the square 2 tiles directly SE is empty and there's a red piece in between
                        if(board[row + 2][col + 2] == EMPTY && (board[row + 1][col + 1] == RED || board[row + 1][col + 1] == RED_KING)) {
                            legalJumpsAL.add(new CheckersMove(row, col, row + 2, col + 2));
                        }
                    }

                    // In the right 2 columns (can only jump left)
                    else if(col > 5) {
                        // Check if the square 2 tiles directly SW is empty and there's a red piece in between
                        if(board[row + 2][col - 2] == EMPTY && (board[row + 1][col - 1] == RED || board[row + 1][col - 1] == RED_KING)) {
                            legalJumpsAL.add(new CheckersMove(row, col, row + 2, col - 2));
                        }
                    }

                    // In any other column (can jump right or left)
                    else {
                        // Check if the square 2 tiles directly SE is empty and there's a red piece in between
                        if(board[row + 2][col + 2] == EMPTY && (board[row + 1][col + 1] == RED || board[row + 1][col + 1] == RED_KING)) {
                            legalJumpsAL.add(new CheckersMove(row, col, row + 2, col + 2));
                        }

                        // Check if the square 2 tiles directly SW is empty and there's a red piece in between
                        if(board[row + 2][col - 2] == EMPTY && (board[row + 1][col - 1] == RED || board[row + 1][col - 1] == RED_KING)) {
                            legalJumpsAL.add(new CheckersMove(row, col, row + 2, col - 2));
                        }
                    }
                }
            }

// -----------------------------------------------------------------------------------------------------------------------------

// ---------------------------------------- Get legal jumps for the black king pieces ----------------------------------------

            if(board[row][col] == BLACK_KING) {
                // In the left 2 cloumns (can only jump right)
                if(col < 2) {
                    // In the top left corner (can only jump SE)
                    if(row < 2) {
                        // Check if the square 2 tiles directly SE is empty and there's a red piece in between
                        if(board[row + 2][col + 2] == EMPTY && (board[row + 1][col + 1] == RED || board[row + 1][col + 1] == RED_KING)) {
                            legalJumpsAL.add(new CheckersMove(row, col, row + 2, col + 2));
                        }
                    }

                    // In the bottom left corner (can only move NE)
                    else if(row > 5) {
                        // Check if the square 2 tiles directly NE is empty and there's a red piece in between
                        if(board[row - 2][col + 2] == EMPTY && (board[row - 1][col + 1] == RED || board[row - 1][col + 1] == RED_KING)) {
                            legalJumpsAL.add(new CheckersMove(row, col, row - 2, col + 2));
                        }
                    }

                    // Anywhere else in the left column (can move up or down)
                    else {
                        // Check if the square 2 tiles directly SE is empty and there's a red piece in between
                        if(board[row + 2][col + 2] == EMPTY && (board[row + 1][col + 1] == RED || board[row + 1][col + 1] == RED_KING)) {
                            legalJumpsAL.add(new CheckersMove(row, col, row + 2, col + 2));
                        }

                        // Check if the square 2 tiles directly NE is empty and there's a red piece in between
                        if(board[row - 2][col + 2] == EMPTY && (board[row - 1][col + 1] == RED || board[row - 1][col + 1] == RED_KING)) {
                            legalJumpsAL.add(new CheckersMove(row, col, row - 2, col + 2));
                        }
                    }
                }

                // In the right 2 columns (can only jump left)
                else if(col > 5) {
                    // In the top right corner (can only jump SW)
                    if(row < 2) {
                        // Check if the square 2 tiles directly SW is empty and there's a red piece in between
                        if(board[row + 2][col - 2] == EMPTY && (board[row + 1][col - 1] == RED || board[row + 1][col - 1] == RED_KING)) {
                            legalJumpsAL.add(new CheckersMove(row, col, row + 2, col - 2));
                        }
                    }

                    // In the bottom right corner (can only move NW)
                    else if(row > 5) {
                        // Check if the square 2 tiles directly NW is empty and there's a red piece in between
                        if(board[row - 2][col - 2] == EMPTY && (board[row - 1][col - 1] == RED || board[row - 1][col - 1] == RED_KING)) {
                            legalJumpsAL.add(new CheckersMove(row, col, row - 2, col - 2));
                        }
                    }

                    // Anywhere else in the left column (can move up or down)
                    else {
                        // Check if the square 2 tiles directly SW is empty and there's a red piece in between
                        if(board[row + 2][col - 2] == EMPTY && (board[row + 1][col - 1] == RED || board[row + 1][col - 1] == RED_KING)) {
                            legalJumpsAL.add(new CheckersMove(row, col, row + 2, col - 2));
                        }

                        // Check if the square 2 tiles directly NW is empty and there's a red piece in between
                        if(board[row - 2][col - 2] == EMPTY && (board[row - 1][col - 1] == RED || board[row - 1][col - 1] == RED_KING)) {
                            legalJumpsAL.add(new CheckersMove(row, col, row - 2, col - 2));
                        }
                    }
                }

                // In the top 2 rows (can only jump down)
                    // NOTE: won't be in a top corner since that's checked already!
                else if(row < 2) {
                    // Check if the square 2 tiles directly SW is empty and there's a red piece in between
                    if(board[row + 2][col - 2] == EMPTY && (board[row + 1][col - 1] == RED || board[row + 1][col - 1] == RED_KING)) {
                        legalJumpsAL.add(new CheckersMove(row, col, row + 2, col - 2));
                    }

                    // Check if the square 2 tiles directly SE is empty and there's a red piece in between
                    if(board[row + 2][col + 2] == EMPTY && (board[row + 1][col + 1] == RED || board[row + 1][col + 1] == RED_KING)) {
                        legalJumpsAL.add(new CheckersMove(row, col, row + 2, col + 2));
                    }
                }

                // In the bottom 2 rows (can only jump up)
                    // NOTE: won't be in a top corner since that's checked already!
                else if(row > 5) {
                    // Check if the square 2 tiles directly NW is empty and there's a red piece in between
                    if(board[row - 2][col - 2] == EMPTY && (board[row - 1][col - 1] == RED || board[row - 1][col - 1] == RED_KING)) {
                        legalJumpsAL.add(new CheckersMove(row, col, row - 2, col - 2));
                    }

                    // Check if the square 2 tiles directly NE is empty and there's a red piece in between
                    if(board[row - 2][col + 2] == EMPTY && (board[row - 1][col + 1] == RED || board[row - 1][col + 1] == RED_KING)) {
                        legalJumpsAL.add(new CheckersMove(row, col, row - 2, col + 2));
                    }
                }

                // Anywhere else on the board (can jump in all 4 directions)
                else {
                    // Check if the square 2 tiles directly NW is empty and there's a red piece in between
                    if(board[row - 2][col - 2] == EMPTY && (board[row - 1][col - 1] == RED || board[row - 1][col - 1] == RED_KING)) {
                        legalJumpsAL.add(new CheckersMove(row, col, row - 2, col - 2));
                    }

                    // Check if the square 2 tiles directly NE is empty and there's a red piece in between
                    if(board[row - 2][col + 2] == EMPTY && (board[row - 1][col + 1] == RED || board[row - 1][col + 1] == RED_KING)) {
                        legalJumpsAL.add(new CheckersMove(row, col, row - 2, col + 2));
                    }

                    // Check if the square 2 tiles directly SW is empty and there's a red piece in between
                    if(board[row + 2][col - 2] == EMPTY && (board[row + 1][col - 1] == RED || board[row + 1][col - 1] == RED_KING)) {
                        legalJumpsAL.add(new CheckersMove(row, col, row + 2, col - 2));
                    }

                    // Check if the square 2 tiles directly SE is empty and there's a red piece in between
                    if(board[row + 2][col + 2] == EMPTY && (board[row + 1][col + 1] == RED || board[row + 1][col + 1] == RED_KING)) {
                        legalJumpsAL.add(new CheckersMove(row, col, row + 2, col + 2));
                    }
                }
            }

// ---------------------------------------------------------------------------------------------------------------------------

        }

        // Can check for no jumps available before consecutive since no jumps will never have consecutive jumps
        if(legalJumpsAL.size() == 0) {
            return null;
        }

        int i = 0;

        while(i < legalJumpsAL.size()) {
            // Get the current move of the ArrayList
            CheckersMove currMove = legalJumpsAL.get(i);
            int currRow = currMove.rows.getLast();
            int currCol = currMove.cols.getLast();

            // See if there are any consecutive jumps available
            int[] consecutiveJumps = checkConsecutiveJumps(currMove, player);

            // Keep track if there are further jumps from the currMove
            boolean consecutiveExists = false;

            // Piece can also jump NW
            if(consecutiveJumps[0] == 1) {
                // Get the current jump and add the consecutive jump to it
                CheckersMove jumpNW = currMove.clone();
                jumpNW.addMove(currRow - 2, currCol - 2);
                legalJumpsAL.add(jumpNW);

                // Change boolean since there is a consecutive jump
                consecutiveExists = true;
            }

            // Piece can also jump NE
            if(consecutiveJumps[1] == 1) {
                // Get the current jump and add the consecutive jump to it
                CheckersMove jumpNE = currMove.clone();
                jumpNE.addMove(currRow - 2, currCol + 2);
                legalJumpsAL.add(jumpNE);

                // Change boolean since there is a consecutive jump
                consecutiveExists = true;
            }

            // Piece can also jump SE
            if(consecutiveJumps[2] == 1) {
                // Get the current jump and add the consecutive jump to it
                CheckersMove jumpSE = currMove.clone();
                jumpSE.addMove(currRow + 2, currCol + 2);
                legalJumpsAL.add(jumpSE);

                // Change boolean since there is a consecutive jump
                consecutiveExists = true;
            }

            // Piece can also jump SW
            if(consecutiveJumps[3] == 1) {
                // Get the current jump and add the consecutive jump to it
                CheckersMove jumpSW = currMove.clone();
                jumpSW.addMove(currRow + 2, currCol - 2);
                legalJumpsAL.add(jumpSW);

                // Change boolean since there is a consecutive jump
                consecutiveExists = true;
            }

            if(consecutiveExists == true) {
                // Remove the old jump (since there is a bigger jump available using that path)
                legalJumpsAL.remove(i);
                
                // Move the index back since the jump was removed
                i--;
            }

            // Move to the next jump
            i++;
        }

        // Convert ArrayList to an array (for the return type)
        CheckersMove[] legalJumps = new CheckersMove[legalJumpsAL.size()];
        legalJumpsAL.toArray(legalJumps);
        
        return legalJumps;
    }

    /**
     * Helper method for finding consecutive jumps
     */
    int[] checkConsecutiveJumps(CheckersMove previousJumps, int player) {
        // Reads NW, NE, SE, SW. Starts off as no jumps possible
        int[] legalJumps = {0, 0, 0, 0};

        // Keep track of where the piece has been (to prevent infinite loops of jumping back and forth)
        int[][] pastPositions = new int[8][8];
        for(int i = 0; i < previousJumps.rows.size(); i++) {
            int indexRow = previousJumps.rows.get(i);
            int indexCol = previousJumps.cols.get(i);

            pastPositions[indexRow][indexCol] = 1;
        }

        // Get where the piece currently is
        int lastRow = previousJumps.rows.getLast();
        int lastCol = previousJumps.cols.getLast();

        // King pieces have more possible jumps than normal pieces
        boolean isKing = false;
        
        // We need where the piece was at first to know if it was already deemed a king or not
        int firstRow = previousJumps.rows.getFirst();
        int firstCol = previousJumps.cols.getFirst();

        // Check if the piece is a king piece
        if(board[firstRow][firstCol] == RED_KING || board[firstRow][firstCol] == BLACK_KING) {
            isKing = true;
        }

        // Sometimes a piece becomes a king piece during a sequence of jumps, check if it reached the other end at all
        else {
            // Normal red piece reached the other end (at some point)
            if(board[firstRow][firstCol] == RED && previousJumps.rows.contains(0)) {
                isKing = true;
            }

            // Normal black piece reached the other end (at some point)
            if(board[firstRow][firstCol] == BLACK && previousJumps.rows.contains(7)) {
                isKing = true;
            }
        }

                                // -------------------- RED PLAYER JUMPS --------------------
        if(player == RED) {

// ---------------------------------------- Get legal jumps for the normal red pieces ----------------------------------------

            if(isKing == false) {
                // Not in the top 2 rows (cannot legally jump if that's the case)
                if(lastRow > 1) {
                    // In the left 2 columns (can only jump right)
                    if(lastCol < 2) {
                        // Check if the square 2 tiles directly NE is empty and there's a black piece in between
                        if(board[lastRow - 2][lastCol + 2] == EMPTY && (board[lastRow - 1][lastCol + 1] == BLACK || board[lastRow - 1][lastCol + 1] == BLACK_KING)) {
                            legalJumps[1] = 1;
                        }
                    }

                    // In the right 2 columns (can only jump left)
                    else if(lastCol > 5) {
                        // Check if the square 2 tiles directly NW is empty and there's a black piece in between
                        if(board[lastRow - 2][lastCol - 2] == EMPTY && (board[lastRow - 1][lastCol - 1] == BLACK || board[lastRow - 1][lastCol - 1] == BLACK_KING)) {
                            legalJumps[0] = 1;
                        }
                    }

                    // In any other column (can jump right or left)
                    else {
                        // Check if the square 2 tiles directly NE is empty and there's a black piece in between
                        if(board[lastRow - 2][lastCol + 2] == EMPTY && (board[lastRow - 1][lastCol + 1] == BLACK || board[lastRow - 1][lastCol + 1] == BLACK_KING)) {
                            legalJumps[1] = 1;
                        }

                        // Check if the square 2 tiles directly NW is empty and there's a black piece in between
                        if(board[lastRow - 2][lastCol - 2] == EMPTY && (board[lastRow - 1][lastCol - 1] == BLACK || board[lastRow - 1][lastCol - 1] == BLACK_KING)) {
                            legalJumps[0] = 1;
                        }
                    }
                }
            }

// ---------------------------------------------------------------------------------------------------------------------------

// ---------------------------------------- Get legal jumps for the red king pieces ----------------------------------------

            if(isKing == true) {
                // In the left 2 cloumns (can only jump right)
                if(lastCol < 2) {
                    // In the top left corner (can only jump SE)
                    if(lastRow < 2) {
                        // Check if the square 2 tiles directly SE is empty and there's a black piece in between
                        if(board[lastRow + 2][lastCol + 2] == EMPTY
                        && (board[lastRow + 1][lastCol + 1] == BLACK || board[lastRow + 1][lastCol + 1] == BLACK_KING)
                        && (pastPositions[lastRow + 2][lastCol + 2] == 0)
                        ) {
                            legalJumps[2] = 1;
                        }
                    }

                    // In the bottom left corner (can only move NE)
                    else if(lastRow > 5) {
                        // Check if the square 2 tiles directly NE is empty and there's a black piece in between
                        if(board[lastRow - 2][lastCol + 2] == EMPTY
                        && (board[lastRow - 1][lastCol + 1] == BLACK || board[lastRow - 1][lastCol + 1] == BLACK_KING)
                        && (pastPositions[lastRow - 2][lastCol + 2] == 0)
                        ) {
                            legalJumps[1] = 1;
                        }
                    }

                    // Anywhere else in the left column (can move up or down)
                    else {
                        // Check if the square 2 tiles directly SE is empty and there's a black piece in between
                        if(board[lastRow + 2][lastCol + 2] == EMPTY
                        && (board[lastRow + 1][lastCol + 1] == BLACK || board[lastRow + 1][lastCol + 1] == BLACK_KING)
                        && (pastPositions[lastRow + 2][lastCol + 2] == 0)
                        ) {
                            legalJumps[2] = 1;
                        }

                        // Check if the square 2 tiles directly NE is empty and there's a black piece in between
                        if(board[lastRow - 2][lastCol + 2] == EMPTY
                        && (board[lastRow - 1][lastCol + 1] == BLACK || board[lastRow - 1][lastCol + 1] == BLACK_KING)
                        && (pastPositions[lastRow - 2][lastCol + 2] == 0)
                        ) {
                            legalJumps[1] = 1;
                        }
                    }
                }

                // In the right 2 columns (can only jump left)
                else if(lastCol > 5) {
                    // In the top right corner (can only jump SW)
                    if(lastRow < 2) {
                        // Check if the square 2 tiles directly SW is empty and there's a black piece in between
                        if(board[lastRow + 2][lastCol - 2] == EMPTY
                        && (board[lastRow + 1][lastCol - 1] == BLACK || board[lastRow + 1][lastCol - 1] == BLACK_KING)
                        && (pastPositions[lastRow + 2][lastCol - 2] == 0)
                        ) {
                            legalJumps[3] = 1;
                        }
                    }

                    // In the bottom right corner (can only move NW)
                    else if(lastRow > 5) {
                        // Check if the square 2 tiles directly NW is empty and there's a black piece in between
                        if(board[lastRow - 2][lastCol - 2] == EMPTY
                        && (board[lastRow - 1][lastCol - 1] == BLACK || board[lastRow - 1][lastCol - 1] == BLACK_KING)
                        && (pastPositions[lastRow - 2][lastCol - 2] == 0)
                        ) {
                            legalJumps[0] = 1;
                        }
                    }

                    // Anywhere else in the left column (can move up or down)
                    else {
                        // Check if the square 2 tiles directly SW is empty and there's a black piece in between
                        if(board[lastRow + 2][lastCol - 2] == EMPTY
                        && (board[lastRow + 1][lastCol - 1] == BLACK || board[lastRow + 1][lastCol - 1] == BLACK_KING)
                        && (pastPositions[lastRow + 2][lastCol - 2] == 0)
                        ) {
                            legalJumps[3] = 1;
                        }

                        // Check if the square 2 tiles directly NW is empty and there's a black piece in between
                        if(board[lastRow - 2][lastCol - 2] == EMPTY
                        && (board[lastRow - 1][lastCol - 1] == BLACK || board[lastRow - 1][lastCol - 1] == BLACK_KING)
                        && (pastPositions[lastRow - 2][lastCol - 2] == 0)
                        ) {
                            legalJumps[0] = 1;
                        }
                    }
                }

                // In the top 2 rows (can only jump down)
                    // NOTE: won't be in a top corner since that's checked already!
                else if(lastRow < 2) {
                    // Check if the square 2 tiles directly SW is empty and there's a black piece in between
                    if(board[lastRow + 2][lastCol - 2] == EMPTY
                    && (board[lastRow + 1][lastCol - 1] == BLACK || board[lastRow + 1][lastCol - 1] == BLACK_KING)
                    && (pastPositions[lastRow + 2][lastCol - 2] == 0)
                    ) {
                        legalJumps[3] = 1;
                    }

                    // Check if the square 2 tiles directly SE is empty and there's a black piece in between
                    if(board[lastRow + 2][lastCol + 2] == EMPTY
                    && (board[lastRow + 1][lastCol + 1] == BLACK || board[lastRow + 1][lastCol + 1] == BLACK_KING)
                    && (pastPositions[lastRow + 2][lastCol + 2] == 0)
                    ) {
                        legalJumps[2] = 1;
                    }
                }

                // In the bottom 2 rows (can only jump up)
                    // NOTE: won't be in a top corner since that's checked already!
                else if(lastRow > 5) {
                    // Check if the square 2 tiles directly NW is empty and there's a black piece in between
                    if(board[lastRow - 2][lastCol - 2] == EMPTY
                    && (board[lastRow - 1][lastCol - 1] == BLACK || board[lastRow - 1][lastCol - 1] == BLACK_KING)
                    && (pastPositions[lastRow - 2][lastCol - 2] == 0)
                    ) {
                        legalJumps[0] = 1;
                    }

                    // Check if the square 2 tiles directly NE is empty and there's a black piece in between
                    if(board[lastRow - 2][lastCol + 2] == EMPTY
                    && (board[lastRow - 1][lastCol + 1] == BLACK || board[lastRow - 1][lastCol + 1] == BLACK_KING)
                    && (pastPositions[lastRow - 2][lastCol + 2] == 0)
                    ) {
                        legalJumps[1] = 1;
                    }
                }

                // Anywhere else on the board (can jump in all 4 directions)
                else {
                    // Check if the square 2 tiles directly NW is empty and there's a black piece in between
                    if(board[lastRow - 2][lastCol - 2] == EMPTY
                    && (board[lastRow - 1][lastCol - 1] == BLACK || board[lastRow - 1][lastCol - 1] == BLACK_KING)
                    && (pastPositions[lastRow - 2][lastCol - 2] == 0)
                    ) {
                        legalJumps[0] = 1;
                    }

                    // Check if the square 2 tiles directly NE is empty and there's a black piece in between
                    if(board[lastRow - 2][lastCol + 2] == EMPTY
                    && (board[lastRow - 1][lastCol + 1] == BLACK || board[lastRow - 1][lastCol + 1] == BLACK_KING)
                    && (pastPositions[lastRow - 2][lastCol + 2] == 0)
                    ) {
                        legalJumps[1] = 1;
                    }

                    // Check if the square 2 tiles directly SW is empty and there's a black piece in between
                    if(board[lastRow + 2][lastCol - 2] == EMPTY
                    && (board[lastRow + 1][lastCol - 1] == BLACK || board[lastRow + 1][lastCol - 1] == BLACK_KING)
                    && (pastPositions[lastRow + 2][lastCol - 2] == 0)
                    ) {
                        legalJumps[3] = 1;
                    }

                    // Check if the square 2 tiles directly SE is empty and there's a black piece in between
                    if(board[lastRow + 2][lastCol + 2] == EMPTY
                    && (board[lastRow + 1][lastCol + 1] == BLACK || board[lastRow + 1][lastCol + 1] == BLACK_KING)
                    && (pastPositions[lastRow + 2][lastCol + 2] == 0)
                    ) {
                        legalJumps[2] = 1;
                    }
                }
            }

// -------------------------------------------------------------------------------------------------------------------------

        }

                                // -------------------- BLACK PLAYER JUMPS --------------------

        else {

// ---------------------------------------- Get legal jumps for the normal black pieces ----------------------------------------

            if(isKing == false) {
                // Not in the bottom 2 rows (cannot legally jump if that's the case)
                if(lastRow < 6) {
                    // In the left 2 columns (can only jump right)
                    if(lastCol < 2) {
                        // Check if the square 2 tiles directly SE is empty and there's a red piece in between
                        if(board[lastRow + 2][lastCol + 2] == EMPTY && (board[lastRow + 1][lastCol + 1] == RED || board[lastRow + 1][lastCol + 1] == RED_KING)) {
                            legalJumps[2] = 1;
                        }
                    }

                    // In the right 2 columns (can only jump left)
                    else if(lastCol > 5) {
                        // Check if the square 2 tiles directly SW is empty and there's a red piece in between
                        if(board[lastRow + 2][lastCol - 2] == EMPTY && (board[lastRow + 1][lastCol - 1] == RED || board[lastRow + 1][lastCol - 1] == RED_KING)) {
                            legalJumps[3] = 1;
                        }
                    }

                    // In any other column (can jump right or left)
                    else {
                        // Check if the square 2 tiles directly SE is empty and there's a red piece in between
                        if(board[lastRow + 2][lastCol + 2] == EMPTY && (board[lastRow + 1][lastCol + 1] == RED || board[lastRow + 1][lastCol + 1] == RED_KING)) {
                            legalJumps[2] = 1;
                        }

                        // Check if the square 2 tiles directly SW is empty and there's a red piece in between
                        if(board[lastRow + 2][lastCol - 2] == EMPTY && (board[lastRow + 1][lastCol - 1] == RED || board[lastRow + 1][lastCol - 1] == RED_KING)) {
                            legalJumps[3] = 1;
                        }
                    }
                }
            }

// -----------------------------------------------------------------------------------------------------------------------------

// ---------------------------------------- Get legal jumps for the black king pieces ----------------------------------------

            if(isKing == true) {
                // In the left 2 cloumns (can only jump right)
                if(lastCol < 2) {
                    // In the top left corner (can only jump SE)
                    if(lastRow < 2) {
                        // Check if the square 2 tiles directly SE is empty and there's a red piece in between
                        if(board[lastRow + 2][lastCol + 2] == EMPTY
                        && (board[lastRow + 1][lastCol + 1] == RED || board[lastRow + 1][lastCol + 1] == RED_KING)
                        && (pastPositions[lastRow + 2][lastCol + 2] == 0)
                        ) {
                            legalJumps[2] = 1;
                        }
                    }

                    // In the bottom left corner (can only move NE)
                    else if(lastRow > 5) {
                        // Check if the square 2 tiles directly NE is empty and there's a red piece in between
                        if(board[lastRow - 2][lastCol + 2] == EMPTY
                        && (board[lastRow - 1][lastCol + 1] == RED || board[lastRow - 1][lastCol + 1] == RED_KING)
                        && (pastPositions[lastRow - 2][lastCol + 2] == 0)
                        ) {
                            legalJumps[1] = 1;
                        }
                    }

                    // Anywhere else in the left column (can move up or down)
                    else {
                        // Check if the square 2 tiles directly SE is empty and there's a red piece in between
                        if(board[lastRow + 2][lastCol + 2] == EMPTY
                        && (board[lastRow + 1][lastCol + 1] == RED || board[lastRow + 1][lastCol + 1] == RED_KING)
                        && (pastPositions[lastRow + 2][lastCol + 2] == 0)
                        ) {
                            legalJumps[2] = 1;
                        }

                        // Check if the square 2 tiles directly NE is empty and there's a red piece in between
                        if(board[lastRow - 2][lastCol + 2] == EMPTY
                        && (board[lastRow - 1][lastCol + 1] == RED || board[lastRow - 1][lastCol + 1] == RED_KING)
                        && (pastPositions[lastRow - 2][lastCol + 2] == 0)
                        ) {
                            legalJumps[1] = 1;
                        }
                    }
                }

                // In the right 2 columns (can only jump left)
                else if(lastCol > 5) {
                    // In the top right corner (can only jump SW)
                    if(lastRow < 2) {
                        // Check if the square 2 tiles directly SW is empty and there's a red piece in between
                        if(board[lastRow + 2][lastCol - 2] == EMPTY
                        && (board[lastRow + 1][lastCol - 1] == RED || board[lastRow + 1][lastCol - 1] == RED_KING)
                        && (pastPositions[lastRow + 2][lastCol - 2] == 0)
                        ) {
                            legalJumps[3] = 1;
                        }
                    }

                    // In the bottom right corner (can only move NW)
                    else if(lastRow > 5) {
                        // Check if the square 2 tiles directly NW is empty and there's a red piece in between
                        if(board[lastRow - 2][lastCol - 2] == EMPTY
                        && (board[lastRow - 1][lastCol - 1] == RED || board[lastRow - 1][lastCol - 1] == RED_KING)
                        && (pastPositions[lastRow - 2][lastCol - 2] == 0)
                        ) {
                            legalJumps[0] = 1;
                        }
                    }

                    // Anywhere else in the left column (can move up or down)
                    else {
                        // Check if the square 2 tiles directly SW is empty and there's a red piece in between
                        if(board[lastRow + 2][lastCol - 2] == EMPTY
                        && (board[lastRow + 1][lastCol - 1] == RED || board[lastRow + 1][lastCol - 1] == RED_KING)
                        && (pastPositions[lastRow + 2][lastCol - 2] == 0)
                        ) {
                            legalJumps[3] = 1;
                        }

                        // Check if the square 2 tiles directly NW is empty and there's a red piece in between
                        if(board[lastRow - 2][lastCol - 2] == EMPTY
                        && (board[lastRow - 1][lastCol - 1] == RED || board[lastRow - 1][lastCol - 1] == RED_KING)
                        && (pastPositions[lastRow - 2][lastCol - 2] == 0)
                        ) {
                            legalJumps[0] = 1;
                        }
                    }
                }

                // In the top 2 rows (can only jump down)
                    // NOTE: won't be in a top corner since that's checked already!
                else if(lastRow < 2) {
                    // Check if the square 2 tiles directly SW is empty and there's a red piece in between
                    if(board[lastRow + 2][lastCol - 2] == EMPTY
                    && (board[lastRow + 1][lastCol - 1] == RED || board[lastRow + 1][lastCol - 1] == RED_KING)
                    && (pastPositions[lastRow + 2][lastCol - 2] == 0)
                    ) {
                        legalJumps[3] = 1;
                    }

                    // Check if the square 2 tiles directly SE is empty and there's a red piece in between
                    if(board[lastRow + 2][lastCol + 2] == EMPTY
                    && (board[lastRow + 1][lastCol + 1] == RED || board[lastRow + 1][lastCol + 1] == RED_KING)
                    && (pastPositions[lastRow + 2][lastCol + 2] == 0)
                    ) {
                        legalJumps[2] = 1;
                    }
                }

                // In the bottom 2 rows (can only jump up)
                    // NOTE: won't be in a top corner since that's checked already!
                else if(lastRow > 5) {
                    // Check if the square 2 tiles directly NW is empty and there's a red piece in between
                    if(board[lastRow - 2][lastCol - 2] == EMPTY
                    && (board[lastRow - 1][lastCol - 1] == RED || board[lastRow - 1][lastCol - 1] == RED_KING)
                    && (pastPositions[lastRow - 2][lastCol - 2] == 0)
                    ) {
                        legalJumps[0] = 1;
                    }

                    // Check if the square 2 tiles directly NE is empty and there's a red piece in between
                    if(board[lastRow - 2][lastCol + 2] == EMPTY
                    && (board[lastRow - 1][lastCol + 1] == RED || board[lastRow - 1][lastCol + 1] == RED_KING)
                    && (pastPositions[lastRow - 2][lastCol + 2] == 0)
                    ) {
                        legalJumps[1] = 1;
                    }
                }

                // Anywhere else on the board (can jump in all 4 directions)
                else {
                    // Check if the square 2 tiles directly NW is empty and there's a red piece in between
                    if(board[lastRow - 2][lastCol - 2] == EMPTY
                    && (board[lastRow - 1][lastCol - 1] == RED || board[lastRow - 1][lastCol - 1] == RED_KING)
                    && (pastPositions[lastRow - 2][lastCol - 2] == 0)
                    ) {
                        legalJumps[0] = 1;
                    }

                    // Check if the square 2 tiles directly NE is empty and there's a red piece in between
                    if(board[lastRow - 2][lastCol + 2] == EMPTY
                    && (board[lastRow - 1][lastCol + 1] == RED || board[lastRow - 1][lastCol + 1] == RED_KING)
                    && (pastPositions[lastRow - 2][lastCol + 2] == 0)
                    ) {
                        legalJumps[1] = 1;
                    }

                    // Check if the square 2 tiles directly SW is empty and there's a red piece in between
                    if(board[lastRow + 2][lastCol - 2] == EMPTY
                    && (board[lastRow + 1][lastCol - 1] == RED || board[lastRow + 1][lastCol - 1] == RED_KING)
                    && (pastPositions[lastRow + 2][lastCol - 2] == 0)
                    ) {
                        legalJumps[3] = 1;
                    }

                    // Check if the square 2 tiles directly SE is empty and there's a red piece in between
                    if(board[lastRow + 2][lastCol + 2] == EMPTY
                    && (board[lastRow + 1][lastCol + 1] == RED || board[lastRow + 1][lastCol + 1] == RED_KING)
                    && (pastPositions[lastRow + 2][lastCol + 2] == 0)
                    ) {
                        legalJumps[2] = 1;
                    }
                }
            }

// ---------------------------------------------------------------------------------------------------------------------------

        }

        return legalJumps;
    }
}