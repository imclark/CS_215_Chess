import java.util.List;

public class Board {
    private Piece[][] board = new Piece[8][8];

    // ------------------------------------------------------------------------
    // Setup
    // ------------------------------------------------------------------------

    /**
     * Removes all pieces from the board.
     */
    public void clear() {
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                board[rank][file] = null;
            }
        }
    }

    /**
     * Sets the back two ranks of each player to be the starting position. The
     * back rank gets the sequence <R, K, B, Q, K, B, K, N>, and the second to
     * last rank gets eight pawns.
     */
    public void initialize() {
        clear();

        for (int file = 0; file < 8; file++) {
            board[1][file] = new Pawn(this, Player.BLACK);
            board[6][file] = new Pawn(this, Player.WHITE);
        }

        board[0][0] = new Rook(this, Player.BLACK);
        board[0][1] = new Knight(this, Player.BLACK);
        board[0][2] = new Bishop(this, Player.BLACK);
        board[0][3] = new Queen(this, Player.BLACK);
        board[0][4] = new King(this, Player.BLACK);
        board[0][5] = new Bishop(this, Player.BLACK);
        board[0][6] = new Knight(this, Player.BLACK);
        board[0][7] = new Rook(this, Player.BLACK);

        board[7][0] = new Rook(this, Player.WHITE);
        board[7][1] = new Knight(this, Player.WHITE);
        board[7][2] = new Bishop(this, Player.WHITE);
        board[7][3] = new Queen(this, Player.WHITE);
        board[7][4] = new King(this, Player.WHITE);
        board[7][5] = new Bishop(this, Player.WHITE);
        board[7][6] = new Knight(this, Player.WHITE);
        board[7][7] = new Rook(this, Player.WHITE);
    }

    // ------------------------------------------------------------------------
    // Position
    // ------------------------------------------------------------------------

    /**
     * Determines if a tile is occupied.
     *
     * @param t The tile.
     * @return true if the tile is occupied, false otherwise.
     */
    public boolean isOccupied(Tile t) {
        return getPieceAt(t) != null;
    }

    /**
     * Determines if a tile is occupied by a particular player.
     *
     * @param t      The tile.
     * @param player The player.
     * @return true if the tile is occupied by player, false otherwise.
     */
    public boolean isOccupiedByPlayer(Tile t, Player player) {
        return getPieceAt(t) != null && getPieceAt(t).getPlayer() == player;
    }

    /**
     * Retrieves the piece at the given tile.
     *
     * @param t The tile.
     * @return The piece at the given tile, or null if the tile is unoccupied.
     */
    public Piece getPieceAt(Tile t) {
        if (!t.isValid()) {
            return null;
        }

        return board[t.getRank()][t.getFile()];
    }

    /**
     * Updates the piece at the given tile.
     *
     * @param t     The tile.
     * @param piece The piece.
     * @throws RuntimeException If the tile is not valid.
     */
    public void setPieceAt(Tile t, Piece piece) {
        if (!t.isValid()) {
            throw new RuntimeException("Tile is not valid.");
        }

        board[t.getRank()][t.getFile()] = piece;
    }

    // ------------------------------------------------------------------------
    // Movement
    // ------------------------------------------------------------------------

    /**
     * Attempts to move a piece from the source tile to the destination tile. This method
     * may fail for the following reasons:
     * <p>
     * <ul>
     * <li>The source or destination tile refers outside the board.</li>
     * <li>The source tile is unoccupied.</li>
     * <li>The piece at the source tile cannot move or capture the destination tile.</li>
     * </ul>
     *
     * @param from The source tile.
     * @param to   The destination tile.
     * @return true if movement succeeded, false otherwise
     */
    public boolean move(Tile from, Tile to) {
        if (!from.isValid() || !to.isValid() || from.equals(to) || !isOccupied(from) || wouldPutInCheck(from, to)) {
            return false;
        }

        Piece piece = getPieceAt(from);

        if (!piece.canMove(from, to)) {
            return false;
        }

        setPieceAt(to, piece);
        setPieceAt(from, null);
        return true;
    }
    
    
    //--------------------------------------------------------------------------------------------------------
    // is Player in check
    //--------------------------------------------------------------------------------------------------------
    public boolean isPlayerInCheck(Player player){
    	List<Tile> r;									// use a list to store getAllMoves output						
    	for (int rank = 0; rank < 8; rank++) {			
            for (int file = 0; file < 8; file++) {		// so cycle through the tiles
                Tile t = new Tile(rank, file);			// create the tile
                if(getPieceAt(t) != null){				// as long as the piece ins't null continue
                	if(getPieceAt(t).getPlayer() != player){  	// make it so we are only checking the other players pieces and if the tile is empty
                		r = getPieceAt(t).getAllMoves(t);								// save the output of getAllMoves for the current tile										
                		for(int a = 0; a < r.size(); a++){ 								//cycles through the moves of the piece
                			Tile b = r.get(a);											// save the tiles in the list
                			if(getPieceAt(b) != null){									// if the piece ins't null continue
                				if (getPieceAt(b).getPlayer() == player && getPieceAt(b).isKing()){			// check if the piece on the tile is player's and player's king
                					return true;																// if there is a king in it's path it returns true
                				}
                			}
                		}
                	}
                }
            }
        }
    	return false;			// return false if 
    }
    
  //--------------------------------------------------------------------------------------------------------
  // is Player in check mate
  //--------------------------------------------------------------------------------------------------------
    
    public boolean  isPlayerInCheckMate(Player player){			// checks if player is in check mate
    	List<Tile> t;										// make a list *					
    		for(int i =0; i <8; i++){
    			for(int j = 0; j < 8; j++){					// make a new tile
    				Tile jam = new Tile(i,j);
    				if(getPieceAt(jam) != null){			// if the piece isn't null
    					if(getPieceAt(jam).getPlayer() == player){		// and if the peices belong to the player
    						t = getPieceAt(jam).getAllSafeMoves(jam);	// * and check it    					
    						if(t.size() > 0){							// if the number of safe moves greater then 0 then they're not in check mate
    							return false;
    						} 
    					}
    				}
    			}
    		}
    		return true;   	// return true if there are no moves to make
    }
    
    
  //--------------------------------------------------------------------------------------------------------
  // move would put player in check
  //--------------------------------------------------------------------------------------------------------
    
    public boolean wouldPutInCheck(Tile from, Tile to){		// returns true if move would cause the player to be in check
    	
    	Piece piece = getPieceAt(from);		// save piece on from tile
    	Piece alf = getPieceAt(to);			// save piece on to tile
    	if(!piece.canMove(from, to)){		// if it cannot move there then return a false
    		return false;
    	}
        setPieceAt(to, piece);				// move the pieces around 
        setPieceAt(from, null);
    	if(isPlayerInCheck(getPieceAt(to).getPlayer())){	// check to see if the player is now in check
    		setPieceAt(from, piece);		// if so move the pieces back to normal and retrun true
    		setPieceAt(to, alf);
    		return true;
    	}
    	setPieceAt(from, piece);			// else return the pieces back to their original squares
		setPieceAt(to, alf);
        return false;    					// and return false
    }
}
