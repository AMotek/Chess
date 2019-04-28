package game;

import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import enaum.PieceType;
import enaum.PlayerColour;
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
	private static Board board = null;
	protected ArrayList<Move> legalMovesWhite = new ArrayList<Move>();
	protected ArrayList<Move> legalMovesBlack = new ArrayList<Move>();
	private  PlayerBlack playerBlack;
	private  PlayerWhite playerWhite;
	private Player currPlayer;
	

	
	/*Creating a Singletone board 
	  */
	private Board() {
		piecesPlayerWhite = new ArrayList<Piece>();
		piecesPlayerBlack = new ArrayList<Piece>();
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
		setCurrPlayer();
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
	

		
	private void calcLegalBlackMoves () {
		//Calc legal moves when not in chess.
		legalMovesBlack.clear();
		for(Piece piece : piecesPlayerBlack) {			
			legalMovesBlack.addAll(piece.getLegalMovements());
			}
		
	}
	private void calcLegalWhiteMoves () {
		//Calc legal moves when not in chess.
		legalMovesWhite.clear();
		for(Piece piece : piecesPlayerWhite) {
			legalMovesWhite.addAll(piece.getLegalMovements());		
		}		
	}
	

	public ArrayList<Move> getAllLegalWhiteMoves() {
		calcLegalWhiteMoves();
		return legalMovesWhite;
				
	}
	
	public ArrayList<Move> getAllLegalBlackMoves() {
		calcLegalBlackMoves();
		return legalMovesBlack;
				
	}

	public String castToBoardCordinate(Spot destSpot) {
		
		char intToAlgebric =(char) (destSpot.getY() + 'a'); // Cast row number to letter
		return intToAlgebric + Integer.toString(Math.abs(destSpot.getX() - OFFSET_CLEAN_ROW_COUNT)) ;
	}
	
	public void removeAttackedPiece(Move move) {
		int index = 0;
		System.out.println("Match found :)");
		if(move.getAttackedPiece().playerCoulor == PlayerColour.WHITE) {
			for(Piece piece:piecesPlayerWhite) {
				if(checkIfSpotsMatch(move.getDestSpot(), piece.getSpot())) {
					piecesPlayerWhite.remove(index);
					
				}
				index++;
			}
		}
	}
	
	private boolean checkIfSpotsMatch(Spot sourceSpot, Spot destSpot) {
		if((sourceSpot.getX() == destSpot.getX() ) && (sourceSpot.getY() == destSpot.getY())) {
			return true;
		}
		return false;
	}


	
}

