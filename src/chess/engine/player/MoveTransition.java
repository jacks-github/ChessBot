package chess.engine.player;

import chess.engine.board.Board;
import chess.engine.piece.move.Move;

public class MoveTransition
{
	private final Board m_transitionBoard;
	private final Move m_move;
	private final MoveStatus m_moveStatus;

	public MoveTransition(final Board transitionBoard, final Move move, final MoveStatus moveStatus)
	{
		m_transitionBoard = transitionBoard;
		m_move = move;
		m_moveStatus = moveStatus;
	}

	public MoveStatus getMoveStatus()
	{
		return m_moveStatus;
	}

	public Board getBoard()
	{
		return m_transitionBoard;
	}

	public Move getmove()
	{
		return m_move;
	}

}
