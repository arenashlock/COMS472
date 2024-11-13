package edu.iastate.cs472.proj2;

import java.util.ArrayList;

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
        // TODO
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
        // TODO
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
        // TODO
        // Not a valid player, return null
        if(player != RED && player != BLACK) {
            return null;
        }

        // Use an ArrayList since the number of legal jumps is unknown
        ArrayList<CheckersMove> legalMovesAL = new ArrayList<>();

        // RED player
        if(player == RED) {
            for(int row = 0; row < 8; row++) {
                for(int col = 0; col < 8; col++) {
                    // Will check these 2 tiles for both normal and king pieces
                    if(board[row][col] == RED) {
                        if(col == 0) { // On the far left tile
                            // Check if the square directly NW is empty
                            if(board[row - 1][col + 1] == EMPTY) {
                                legalMovesAL.add(new CheckersMove(row, col, row - 1, col + 1));
                            }
                        } else if(col == 7) { // On the far right side
                            // Check if the square directly NE is empty
                            if(board[row - 1][col - 1] == EMPTY) {
                                legalMovesAL.add(new CheckersMove(row, col, row - 1, col - 1));
                            }
                        } else {
                            // Check if the square directly NW is empty
                            if(board[row - 1][col + 1] == EMPTY) {
                                legalMovesAL.add(new CheckersMove(row, col, row - 1, col + 1));
                            }
                            // Check if the square directly NE is empty
                            if(board[row - 1][col - 1] == EMPTY) {
                                legalMovesAL.add(new CheckersMove(row, col, row - 1, col - 1));
                            }
                        }
                    }
                }
            }
        }

        // BLACK player
        if(player == BLACK) {
            for(int row = 0; row < 8; row++) {
                for(int col = 0; col < 8; col++) {
                    // Will check these 2 tiles for both normal and king pieces
                    if(board[row][col] == BLACK) {
                        if(col == 0) { // On the far left tile
                            // Check if the square directly SW is empty
                            if(board[row + 1][col + 1] == EMPTY) {
                                legalMovesAL.add(new CheckersMove(row, col, row + 1, col + 1));
                            }
                        } else if(col == 7) { // On the far right side
                            // Check if the square directly SE is empty
                            if(board[row + 1][col - 1] == EMPTY) {
                                legalMovesAL.add(new CheckersMove(row, col, row + 1, col - 1));
                            }
                        } else {
                            // Check if the square directly SW is empty
                            if(board[row + 1][col + 1] == EMPTY) {
                                legalMovesAL.add(new CheckersMove(row, col, row + 1, col + 1));
                            }
                            // Check if the square directly SE is empty
                            if(board[row + 1][col - 1] == EMPTY) {
                                legalMovesAL.add(new CheckersMove(row, col, row + 1, col - 1));
                            }
                        }
                    }
                }
            }
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
        // TODO 
        // Use an ArrayList since the number of legal jumps is unknown
        ArrayList<CheckersMove> legalJumpsAL = new ArrayList<>();

        // Boolean for a king piece since those have extra jumps to check
        boolean isKing = false;

        // RED player
        if(player == RED) {
            if(board[row][col] == RED_KING) {
                isKing = true;
            }

            // CHECK...
        }

        // BLACK player
        if(player == BLACK) {
            if(board[row][col] == BLACK_KING) {
                isKing = true;
            }

            // CHECK...
        }

        // Convert ArrayList to an array (for the return type)
        CheckersMove[] legalJumps = new CheckersMove[legalJumpsAL.size()];
        legalJumpsAL.toArray(legalJumps);
        
        return legalJumps;
    }
}