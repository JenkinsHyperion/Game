package engine;

import java.util.TimerTask;

public class UpdateBoard extends TimerTask {
	
	private Board currentBoard;
	
	public UpdateBoard(Board board) {
		currentBoard = board;
	}

	@Override
	public void run() {

		currentBoard.updateBoard();		
	}

}
