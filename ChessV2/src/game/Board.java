package game;

import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import enaum.MoveType;
import enaum.PieceType;
import enaum.PlayerColour;
import game.StrategyGameLogic.*;
import movement.AttackMove;
import movement.Move;
import player.Player;
import player.PlayerBlack;
import player.PlayerWhite;
import soldiers.*;

public class Board {
	public static final int NUM_ROWS = 8;
	public static final int NUM_COLS = 8;
	private static final int PAWN_SIZE = 8;
	private static final int BISHOP_SIZE = 2;
	private static final int KNIGHT_SIZE = 2;
	private static final int ROOK_SIZE = 2;
	private static final int OFFSET_CLEAN_ROW_COUNT = 8; 
	public static Spot[][] spots;
	protected static ArrayList<Piece> piecesPlayerWhite;
	protected  ArrayList<Piece> piecesPlayerBlack;
	protected ArrayList<Piece> takenPieces;
	private static Board board = null;
	public Move lastMove = null;
	protected ArrayList<Move> legalMovesWhite = new ArrayList<Move>();
	protected ArrayList<Move> legalMovesBlack = new ArrayList<Move>();
	private  PlayerBlack playerBlack;
	private  PlayerWhite playerWhite;
	private Player currPlayer;
	private ContextGameLogic contextGameLogic; 
	

	/*******************
	 * Creating the board using singleton design pattern. 
	 *  The board class in responsible for the current state of the board, keeps track of remaining pieces on board, which player's turn is  and so on.
	 *******************/
	private Board() {
		piecesPlayerWhite = new ArrayList<Piece>();
		piecesPlayerBlack = new ArrayList<Piece>();
		takenPieces = new ArrayList<Piece>();
		spots = new Spot[NUM_ROWS][NUM_COLS];
		
		for(int i = 0; i < NUM_ROWS ; i++) {
			for(int j = 0; j < NUM_COLS; j++) {
				spots[i][j] = new Spot(i, j);
				
			}
		}	
		createPieces(piecesPlayerWhite, PlayerColour.WHITE);
		createPieces(piecesPlayerBlack, PlayerColour.BLACK);
		setPiecesPosition(piecesPlayerWhite, PlayerColour.WHITE);
		setPiecesPosition(piecesPlayerBlack, PlayerColour.BLACK);
		
		playerBlack = new PlayerBlack(piecesPlayerBlack, this);
		playerWhite = new PlayerWhite(piecesPlayerWhite, this);
		setCurrPlayer();  // Set starting player.
}
	public void setLastMove(Move lastMove) {
		this.lastMove = lastMove;
	}
	
	public Move getLastMove() {
		return this.lastMove;
	}
	
	public static  Board startNewBoard() {
		if(board == null) {
			board = new Board();	
		 }									
		else {	
			//System.out.println("Attempt to create two boards avoided.");
		}
		return board;	
 }
	
	public PlayerColour getCurrentPlayerColour() {
		if(currPlayer.getPlayerColour() == PlayerColour.BLACK) {
			return PlayerColour.BLACK;
		} 
		else if(currPlayer.getPlayerColour() == PlayerColour.WHITE) {
			return PlayerColour.WHITE;
		}
		else {
			System.out.println("Shouldn't reach here");
			return null;
		}
	}
	
	//TODO  need to realize how to operate this two.
	public Player getCurrPlayer() {
		return currPlayer;
		
	}

	public void setCurrPlayer() {
		// Letting white player play first.
		if(currPlayer == null) {
			/*
			// Optional, i might add random starting player.
			Random randomGenerator = new Random();
			int randomPlayerIndex = randomGenerator.nextInt(2);
			if(randomPlayerIndex == 0) {
				currPlayer = playerWhite;
			}
			else {
				currPlayer = playerBlack;
			}
			*/
			currPlayer = playerWhite;
		}
		
		else {
			
			currPlayer = ((currPlayer == playerWhite) ? playerBlack : playerWhite);
		}
		
	}
	
	public Board getUpdatedBoard() {
		return this;
	}


	private  void createPieces(ArrayList<Piece> piecesPlayer , PlayerColour playerCoulor) {
		
		piecesPlayer.add(new King(playerCoulor,PieceType.KING, this));
		piecesPlayer.add(new Queen(playerCoulor,PieceType.QUEEN, this));
			
		for(int i = 0; i < BISHOP_SIZE; i++) {
			piecesPlayer.add(new Bishop(playerCoulor,PieceType.BISHOP, this));
		}
		for(int i = 0; i < KNIGHT_SIZE; i++) {
			piecesPlayer.add(new Knight(playerCoulor, PieceType.KNIGHT, this));
		}
		for(int i = 0; i < ROOK_SIZE; i++) {
			piecesPlayer.add(new Rook(playerCoulor, PieceType.ROOK, this));
		}
		for(int i = 0; i < PAWN_SIZE; i++) {
			piecesPlayer.add(new Pawn(playerCoulor,PieceType.PAWN, this));
		}
		
					
	}
	
	public Spot getSpot(int x, int y) {
		return spots[x][y].getSpot();
	}
	public Spot getSpot(Spot spot) {
		
		return spots[spot.getX()] [spot.getY()];
	}
	
	//Setting start piece position. Setting them for both player black and white. Using xPos array to determine white/block position on board. 
	private static void setPiecesPosition(ArrayList<Piece> piecesPlayer, PlayerColour playerCoulor) {
		
		int []xPos = new int[2];
		int []yPos = new int[2];
		int i = 0;
		int pieceIndex = 0;
		if(playerCoulor.equals(PlayerColour.WHITE)) { // setting X Axi's based on the player color.
			xPos[0] = 7;
			xPos[1] = 6;
		}
		else{
			xPos[0] = 0;
			xPos[1] = 1;
		}
		
		// setting King's posistion.
		
		piecesPlayer.get(pieceIndex).setPiecePos(spots [xPos[0]][4]);
		spots [xPos[0]][4].setPieceOnSpot(piecesPlayer.get(pieceIndex));
		pieceIndex++;

		
		// setting Queen's position
		piecesPlayer.get(pieceIndex).setPiecePos(spots [xPos[0]][3]);
		spots [xPos[0]][3].setPieceOnSpot(piecesPlayer.get(pieceIndex));
		pieceIndex++;
		
		// setting Y axis for Bishop's piece;
		yPos[0] = 2;
		yPos[1] = 5;
		for(i = 0; i < BISHOP_SIZE; i++) {
			spots [xPos[0]][yPos[i]].setPieceOnSpot(piecesPlayer.get(pieceIndex));
			piecesPlayer.get(pieceIndex++).setPiecePos(spots [xPos[0]] [yPos[i]]);
			

		}
		// setting Y axis for Knight's piece;
		yPos[0] = 1;
		yPos[1] = 6;
		for(i = 0; i < KNIGHT_SIZE; i++) {
			spots [xPos[0]][yPos[i]].setPieceOnSpot(piecesPlayer.get(pieceIndex));
			piecesPlayer.get(pieceIndex++).setPiecePos(spots [xPos[0]] [yPos[i]]);
			

		}
		// setting Y axis for Rook piece;	
		yPos[0] = 0;
		yPos[1] = 7;
		for(i = 0 ;i < ROOK_SIZE; i++) {
			spots [xPos[0]][yPos[i]].setPieceOnSpot(piecesPlayer.get(pieceIndex));
			piecesPlayer.get(pieceIndex++).setPiecePos(spots [xPos[0]] [yPos[i]]);		
			

		}
		
		//Setting Pawns position.
		for(i = 0; i < PAWN_SIZE; i++) {
			spots [xPos[1]][i].setPieceOnSpot(piecesPlayer.get(pieceIndex ));
			piecesPlayer.get(pieceIndex++).setPiecePos(spots [xPos[1]] [i]);
			

		}
		
	}
	
	public ArrayList<Piece> getPiecesWhite(){
		return this.piecesPlayerWhite;
	}
	
	public ArrayList<Piece> getPiecesBlack(){
		return this.piecesPlayerBlack;
	}
	

	

	public List<Move> getAllLegalWhiteMoves() {
		contextGameLogic = new ContextGameLogic(new OperationWhitePlayer());
		return contextGameLogic.getLegalMoves(playerWhite.getKing());
				
	}
	
	public List<Move> getAllLegalBlackMoves() {
		contextGameLogic = new ContextGameLogic(new OperationBlackPlayer());
		return contextGameLogic.getLegalMoves(playerBlack.getKing());
				
	}

	public String castToBoardCordinate(Spot destSpot) {
		
		char intToAlgebric =(char) (destSpot.getY() + 'a'); // Cast row number to letter
		return intToAlgebric + Integer.toString(Math.abs(destSpot.getX() - OFFSET_CLEAN_ROW_COUNT)) ;
	}
	
	public void addPiece(Piece piece) {
		
		if(piece.playerCoulor == PlayerColour.WHITE) {
			piecesPlayerWhite.add(piece);
		}
		else {
			piecesPlayerBlack.add(piece);
		}
	}
	
	public void removePiece(Piece pieceToRemove, boolean isGuiRelated) {
		int index = 0;
		if(pieceToRemove.playerCoulor == PlayerColour.WHITE) {
			for(Piece piece:piecesPlayerWhite) {
				if(checkIfSpotsMatch(pieceToRemove.getSpot(), piece.getSpot())) {
					//Checks if needed to add the removed piece to the GUI taken panel on the left side.
					if(!isGuiRelated) {
						takenPieces.add(piecesPlayerWhite.get(index));
					}
					piecesPlayerWhite.remove(index);
					break;		
				}
				index++;
			}
		}
		else if(pieceToRemove.playerCoulor == PlayerColour.BLACK) {
			for(Piece piece:piecesPlayerBlack) {
				if(checkIfSpotsMatch(pieceToRemove.getSpot(), piece.getSpot())) {
					if(!isGuiRelated) {
						takenPieces.add(piecesPlayerBlack.get(index));
					}
					piecesPlayerBlack.remove(index);
					break;
					
				}
				index++;
			}
		}
		
		else {
			System.out.println("Shouldn't reach here !@#@$");
		}
	}

	
	//Sole purpose serve removeAttackedPiece method 
	private boolean checkIfSpotsMatch(Spot sourceSpot, Spot destSpot) {
		if((sourceSpot.getX() == destSpot.getX() ) && (sourceSpot.getY() == destSpot.getY())) {
			return true;
		}
		return false;
	}
	
	public ArrayList<Piece> getTakenPieces(){
		return this.takenPieces;
	}
	
	
	public boolean getInCheckStatusWhitePlayer() {
		contextGameLogic = new ContextGameLogic(new OperationWhitePlayer());
		return contextGameLogic.getInCheckStatus(playerWhite.getKing());
	}
	
	public boolean getInCheckStatusBlackPlayer() {
		contextGameLogic = new ContextGameLogic(new OperationBlackPlayer());
		return contextGameLogic.getInCheckStatus(playerBlack.getKing());
	}
	
	public boolean isCastleAllowedBlackPlayer(Piece king, Piece rook, MoveType moveType) {
		contextGameLogic = new ContextGameLogic(new OperationBlackPlayer());
		return contextGameLogic.isCastleAllowed(king, rook, moveType);
	}
	
	public boolean isCastleAllowedWhitePlayer(Piece king, Piece rook, MoveType moveType) {
		contextGameLogic = new ContextGameLogic(new OperationWhitePlayer());
		return contextGameLogic.isCastleAllowed(king, rook, moveType);
	}
	
	
	public boolean isInCheckMateBlackPlayer() {
		contextGameLogic = new ContextGameLogic(new OperationBlackPlayer());
		return contextGameLogic.getInCheckMateStatus(playerBlack.getKing());	
		
	}
	
	public boolean isInCheckMateWhitePlayer() {
		contextGameLogic = new ContextGameLogic(new OperationWhitePlayer());
		return contextGameLogic.getInCheckMateStatus(playerWhite.getKing());			
	}
	
	public boolean isInStaleMateBlackPlayer() {
		contextGameLogic = new ContextGameLogic(new OperationBlackPlayer());
		return contextGameLogic.getInStaleMateStatus(playerBlack.getKing());
	}
	
	public boolean isInStaleMateWhitePlayer() {
		contextGameLogic = new ContextGameLogic(new OperationWhitePlayer());
		return contextGameLogic.getInStaleMateStatus(playerWhite.getKing());
	}

	public void pawnToPromote(Move move, PlayerColour playerColour) {
		if(playerColour == PlayerColour.BLACK) {
			contextGameLogic = new ContextGameLogic(new OperationBlackPlayer());
		}
		else {
			contextGameLogic = new ContextGameLogic(new OperationWhitePlayer());
		}
		contextGameLogic.promotePawn(move);
	}
}

