package pieces;

import java.util.List;

import enaum.PieceType;
import enaum.PlayerColour;
import game.Board;
import game.Spot;
import movement.Move;

public  class Bishop extends Piece {

	public Bishop(PlayerColour playerColour, PieceType pieceType, Board board, Spot spot) {
		super(playerColour, pieceType, board, true, 300, spot);

	}
	@Override
	public void setCandidateMovements() {
		diagonalBottomLeftMovement();
		diagonalBottomRightMovement();
		diagonalTopLeftMovement();
		diagonalTopRightMovement();
	}

	@Override
	public List<Move> getCastleMovements() {
		System.out.println("shouldn't reach here, castling not allowed for this type.");
		return null;
		
	}
	@Override
	public boolean isPawnPromotionMove(int rowDestinaition) {
		throw new RuntimeException("Shouldn't reach here, not a pawn piece");
	}
	
	

}
