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
import movement.ProgressMove;
import pieces.*;
import player.Player;
import player.PlayerBlack;
import player.PlayerWhite;

public  class Board {
	public static final int NUM_ROWS = 8;
	public static final int NUM_COLS = 8;
	private static final int PAWN_SIZE = 8;
	private static final int BISHOP_SIZE = 2;
	private static final int KNIGHT_SIZE = 2;
	private static final int ROOK_SIZE = 2;
	static final int OFFSET_CLEAN_ROW_COUNT = 8; 
	private final Spot[][] boardSpots;
	private final ArrayList<Piece> blackPlayerPieces;
	private final  ArrayList<Piece> whitePlayerPieces;
	private static ArrayList<Piece> takenPieces = new ArrayList<Piece>();
	private static Move lastMove = null;

	private ArrayList<Move> legalMovesWhite;
	private ArrayList<Move> legalMovesBlack;
	
	private  PlayerBlack playerBlack;
	private  PlayerWhite playerWhite;
	private static Player currPlayer;
	private ContextGameLogic contextGameLogic;
	
	

	/*******************
	 * This is the Board class, it is keeping the current state of the board, e.g pieces positions, active remaining piece etc.
	 * Building this board using Builder pattern(a nested class you may find at the bottom in here).
	 *******************/
	
	public Board(BoardBuilder boardBuilder) {
		this.blackPlayerPieces = boardBuilder.blackPlayerPieces;
		this.whitePlayerPieces = boardBuilder.whitePlayerPieces;
		this.boardSpots = boardBuilder.boardSpots;
		this.legalMovesBlack = new ArrayList<Move>();
		this.legalMovesWhite = new ArrayList<Move>();
		this.playerBlack = new PlayerBlack(blackPlayerPieces, this);
		this.playerWhite = new PlayerWhite(whitePlayerPieces, this);
	}
	
	public void updateTakenPieceList(Piece piece) {
		takenPieces.add(piece);
		
	}
	
	public boolean getEnPassantTargetAvailability() {
		if(lastMove == null) {
			return false;
		}
		return lastMove.isPawnJumpMove();
	}
	
	public void setLastMove(Move move) {
		lastMove = move;
	}
	
	public Move getLastMove() {
		return lastMove;
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
	
	public void setStartingPlayer() {
		this.currPlayer = playerWhite;
		
	}	

	public void setCurrentPlayer() {	
			currPlayer = ((currPlayer.getPlayerColour() == PlayerColour.WHITE) ? playerBlack : playerWhite);
		
	}
	

	
	public Spot getSpot(int x, int y) {
		return boardSpots[x][y].getSpot();
	}
	public Spot getSpot(Spot spot) {
		
		return boardSpots[spot.getX()] [spot.getY()];
	}
	
	
	public ArrayList<Piece> getPiecesWhite(){
		return this.whitePlayerPieces;
	}
	
	public ArrayList<Piece> getPiecesBlack(){
		return this.blackPlayerPieces;
	}
	
	//This method cast numeric coordinates to algebric notion coordinates.
	public String castToBoardCordinate(Spot destSpot) {
		
		char intToAlgebric =(char) (destSpot.getY() + 'a'); // Cast row number to letter
		return intToAlgebric + Integer.toString(Math.abs(destSpot.getX() - OFFSET_CLEAN_ROW_COUNT)) ;
	}
	

	public ArrayList<Piece> getTakenPieces(){
		return takenPieces;
	}
	
	public List<Move> getAllLegalMoves(PlayerColour playerColour) {
		contextGameLogic = new ContextGameLogic(playerColour, this);
		return contextGameLogic.getLegalMoves();
				
	}
	
	public boolean isInCheck(PlayerColour playerColour) {
		contextGameLogic = new ContextGameLogic(playerColour, this);
		return contextGameLogic.getInCheckStatus();
	}
	
	public boolean isCastleNotAllowed(Move move, PlayerColour playerColour) {
		contextGameLogic = new ContextGameLogic(playerColour, this);
		return contextGameLogic.isCastleNotAllowed(move);
	}
	
	
	public boolean isInCheckMate(PlayerColour playerColour) {
		contextGameLogic = new ContextGameLogic(playerColour, this);
		return contextGameLogic.getInCheckMateStatus();	
	}
	
	
	public boolean isInStaleMate(PlayerColour playerColour) {
		contextGameLogic = new ContextGameLogic(playerColour, this);
		return contextGameLogic.getInStaleMateStatus();
	}
	
	public boolean getKingSideCastleCapeableBlack() {
		contextGameLogic = new ContextGameLogic(PlayerColour.BLACK, this);
		return contextGameLogic.getKingSideCastleCapeable();
	}
	
	public boolean getQueenSideCastleCapeableBlack() {
		contextGameLogic = new ContextGameLogic(PlayerColour.BLACK, this);
		return contextGameLogic.getQueenSideCastleCapeable();
	}
	
	public boolean getKingSideCastleCapeableWhite() {
		contextGameLogic = new ContextGameLogic(PlayerColour.WHITE, this);
		return contextGameLogic.getKingSideCastleCapeable();
	}
	
	public boolean getQueenSideCastleCapeableWhite() {
		contextGameLogic = new ContextGameLogic(PlayerColour.WHITE, this);
		return contextGameLogic.getQueenSideCastleCapeable();
	}
		
	public Player getWhitePlayer() {
		return this.playerWhite;
	}
	
	public Player getBlackPlayer() { 
		return this.playerBlack;
	}
	
	public Piece getPiece(int x, int y) {
		return boardSpots[x][y].getPiece();
	}
	
	
	
	/**
	 * This is the board builder, sole purpose building board(Board spots and Player's pieces) based on requests.
	 * Using builder design pattern.
	 * 
	 * */
	public static class BoardBuilder {


		PieceFactory pieceFactory;
		private final ArrayList<Piece>blackPlayerPieces;
		private final ArrayList<Piece>whitePlayerPieces;
		private final Spot boardSpots[][];
		
		
		//Creating new standard board
		public BoardBuilder() {
			this.blackPlayerPieces = new ArrayList<Piece>();
			this.whitePlayerPieces = new ArrayList<Piece>();
			this.boardSpots = new Spot[NUM_ROWS][NUM_COLS];
			this.pieceFactory = new PieceFactory();
			createBoardSpots();
			createBlackPieces();
			createWhitePieces();
			updateBoardSpots();	
			
		}
		//Creating board based on pre made white and black pieces locations
		public BoardBuilder(ArrayList<Piece>blackPlayerPieces, ArrayList<Piece>whitePlayerPieces) {
			this.blackPlayerPieces = new ArrayList<Piece>();
			this.whitePlayerPieces = new ArrayList<Piece>();
			this.boardSpots = new Spot[NUM_ROWS][NUM_COLS];
			this.pieceFactory = new PieceFactory();
			createBoardSpots();			
			createPieces(blackPlayerPieces, whitePlayerPieces);		
			updateBoardSpots();			
		}
		
		
		
		private void createPieces(ArrayList<Piece> blackPlayerPieces, ArrayList<Piece> whitePlayerPieces) {
			for(Piece piece : blackPlayerPieces) {
				this.blackPlayerPieces.add(pieceFactory.createPiece(PlayerColour.BLACK, piece.getPieceType(), piece.getSpot(), piece.isFirstMove()));
			}
			
			for(Piece piece : whitePlayerPieces) {
				this.whitePlayerPieces.add(pieceFactory.createPiece(PlayerColour.WHITE, piece.getPieceType(), piece.getSpot(), piece.isFirstMove()));
			}
			
		}
		
		private void updateBoardSpots() {
			
			for(Piece piece : whitePlayerPieces) {
				boardSpots[piece.getX()][piece.getY()].setPieceOnSpot(piece);
			}
			
			for(Piece piece : blackPlayerPieces) {
				boardSpots[piece.getX()][piece.getY()].setPieceOnSpot(piece);
			}
		}

		private void createBoardSpots() {
			for(int i = 0; i < NUM_ROWS; i++) {
				for(int j = 0; j < NUM_COLS; j++) {
					boardSpots[i][j] = new Spot(i, j);
				}
			}			
		}
		
		
		private Spot getSpot(int x, int y) {
			return boardSpots[x][y].getSpot();
		}
		
		private Spot getSpot(Spot spot) {
			
			return boardSpots[spot.getX()] [spot.getY()];
		}
		
		private void createBlackPieces() {
			blackPlayerPieces.add(pieceFactory.createPiece(PlayerColour.BLACK, PieceType.KING, getSpot(0, 4), true));
			blackPlayerPieces.add(pieceFactory.createPiece(PlayerColour.BLACK, PieceType.QUEEN, getSpot(0, 3), true));
			blackPlayerPieces.add(pieceFactory.createPiece(PlayerColour.BLACK, PieceType.BISHOP, getSpot(0, 2), true));
			blackPlayerPieces.add(pieceFactory.createPiece(PlayerColour.BLACK, PieceType.BISHOP, getSpot(0, 5), true));
			blackPlayerPieces.add(pieceFactory.createPiece(PlayerColour.BLACK, PieceType.KNIGHT, getSpot(0, 1), true));
			blackPlayerPieces.add(pieceFactory.createPiece(PlayerColour.BLACK, PieceType.KNIGHT, getSpot(0, 6), true));
			blackPlayerPieces.add(pieceFactory.createPiece(PlayerColour.BLACK, PieceType.ROOK, getSpot(0, 0), true));
			blackPlayerPieces.add(pieceFactory.createPiece(PlayerColour.BLACK, PieceType.ROOK, getSpot(0, 7), true));
			
			for(int i = 0; i < PAWN_SIZE; i++) {
				blackPlayerPieces.add(pieceFactory.createPiece(PlayerColour.BLACK, PieceType.PAWN, getSpot(1, i), true));
			}
		}
		
		private void createWhitePieces() {
			whitePlayerPieces.add(pieceFactory.createPiece(PlayerColour.WHITE, PieceType.KING, getSpot(7, 4), true));
			whitePlayerPieces.add(pieceFactory.createPiece(PlayerColour.WHITE, PieceType.QUEEN, getSpot(7, 3), true));
			whitePlayerPieces.add(pieceFactory.createPiece(PlayerColour.WHITE, PieceType.BISHOP, getSpot(7, 2), true));
			whitePlayerPieces.add(pieceFactory.createPiece(PlayerColour.WHITE, PieceType.BISHOP, getSpot(7, 5), true));
			whitePlayerPieces.add(pieceFactory.createPiece(PlayerColour.WHITE, PieceType.KNIGHT, getSpot(7, 1), true));
			whitePlayerPieces.add(pieceFactory.createPiece(PlayerColour.WHITE, PieceType.KNIGHT, getSpot(7, 6), true));
			whitePlayerPieces.add(pieceFactory.createPiece(PlayerColour.WHITE, PieceType.ROOK, getSpot(7, 0), true));
			whitePlayerPieces.add(pieceFactory.createPiece(PlayerColour.WHITE, PieceType.ROOK, getSpot(7, 7), true));
			
			for(int i = 0; i < PAWN_SIZE; i++) {
				whitePlayerPieces.add(pieceFactory.createPiece(PlayerColour.WHITE, PieceType.PAWN, getSpot(6, i), true));
			}
		}
		
		public Board  build() {
			return new Board(this);
		}
	
	}




}




