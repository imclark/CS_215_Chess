import java.util.ArrayList;
import java.util.List;

/**
 * Represents an abstract chess piece. Each piece belongs to a unique board
 * and a unique player (which are immutable after construction).
 * <p>
 * A piece is responsible for knowing its own rules of movement and capture.
 * A piece does not know its own location; the board is the authority about
 * where a piece currently is on the board.
 */
abstract public class Piece {
    private Board board;
    private Player player;

    public Piece(Board board, Player player) {
        this.board = board;
        this.player = player;
    }

    // ------------------------------------------------------------------------
    // Status
    // ------------------------------------------------------------------------

    public Board getBoard() {
        return board;
    }

    public Player getPlayer() {
        return player;
    }

    // ------------------------------------------------------------------------
    // Predictive Movement
    // ------------------------------------------------------------------------

    /**
     * Return an array list of all the possible moves this piece can make if it
     * is currently located at `from`.
     * <p>
     * It does this by looking at each of the 64 tiles on the board and asking
     * if the piece could move here. It's not the most clever solution, but it
     * is bound by the size of the board, which is a rather small constant.
     *
     * @param from The piece's current tile.
     * @return A list of possible tiles this piece can move to.
     */
    public List<Tile> getAllMoves(Tile from) {
        List<Tile> moves = new ArrayList<>();

        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                Tile t = new Tile(rank, file);

                if (canMove(from, t)) {
                    moves.add(t);
                }
            }
        }

        return moves;
    }

    // ------------------------------------------------------------------------
    // Movement
    // ------------------------------------------------------------------------

    /**
     * Determines if a piece can move to the given tile.
     *
     * @param t The target tile.
     * @return true if the tile is empty, false otherwise.
     */
    protected boolean canMove(Tile t) {
        return !board.isOccupied(t);
    }

    /**
     * Determines if a piece can capture the given tile.
     *
     * @param t The target file.
     * @return true if the tile has an opponent piece, false otherwise.
     */
    protected boolean canCapture(Tile t) {
        return board.isOccupiedByPlayer(t, player.opposite());
    }

    /**
     * Determines if a piece can move or capture a tile.
     *
     * @param t The target file.
     * @return true if the tile is empty or has an opponent piece, false otherwise.
     */
    protected boolean canMoveOrCapture(Tile t) {
        return canMove(t) || canCapture(t);
    }

    /**
     * Determines if there is a piece on the path between `from` and `to`. This method
     * assumes that `from` and `to` are positioned so that there is an axis-aligned or
     * diagonal path between the two (and may behave strangely otherwise). This method
     * does not check the source and destination tiles - only the tiles in between.
     * <p>
     * This method works iteratively by "sliding" the source tile one tile at a time
     * towards the destination tile. After each move, the new source tile is checked
     * to see if it's unoccupied.
     * <p>
     * Each iteration of the loop increases the source rank and file by `dr` and `df`,
     * respectively. These values are the step value (in the set {-1, 0, +1}) required
     * to move from the source tile to the destination tile. This is where this method
     * can fail - there may not be a `dr` and `df` value to get from the source to the
     * destination tile (e.g. a knight's move), and we don't check for validity first.
     *
     * @param from The source tile.
     * @param to   The destination tile.
     * @return true if the path is blocked by a piece, false otherwise.
     */
    protected boolean isPathBlocked(Tile from, Tile to) {
        int dr = from.getRank() == to.getRank() ? 0 : (from.getRank() < to.getRank() ? +1 : -1);
        int df = from.getFile() == to.getFile() ? 0 : (from.getFile() < to.getFile() ? +1 : -1);

        int rank = from.getRank() + dr;
        int file = from.getFile() + df;

        while (rank != to.getRank() || file != to.getFile()) {
            if (getBoard().isOccupied(new Tile(rank, file))) {
                return true;
            }

            rank += dr;
            file += df;
        }

        return false;
    }

    /**
     * Determines if a piece can move from the source tile to the destination tile.
     *
     * @param from The source (current) tile.
     * @param to   The destination tile.
     * @return true if the piece can move from the source to the destination tile, false otherwise.
     */
    abstract public boolean canMove(Tile from, Tile to);
    
    public boolean isKing(){		// check if king
    	return false;
    }
    
   public List<Tile> getAllSafeMoves(Tile from){			// will get all moves that wont cause you to go into check or check mate
	   List<Tile> santa = new ArrayList<>();				// create a list
    	for (int i = 0; i < 8; i++) {						// cycle the tiles
            for (int j = 0; j < 8; j++) {
                Tile t = new Tile(i, j);					// create the tiles
                if(canMove(from, t)){						// if canMove to that spot continue
                	if(!board.wouldPutInCheck(from, t)){	// if the move doesn't put it in check then add it to the list
                		santa.add(t);
                	}
                }											
                
            }
        }
    	return santa;			// at the end return the list
    }
}
