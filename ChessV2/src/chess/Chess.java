package chess;

import game.Board;
import game.GamePanel;
import gui.GuiTable;
import player.Player;

public class Chess {
	
	public static void main(String[] args) {

		Board board = Board.startNewBoard();
		new GuiTable();
		
	}

}
