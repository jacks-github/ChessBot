package chess.engine.ai;

import chess.engine.board.Board;
import chess.engine.piece.move.Move;

public interface MoveStrategy
{

	Move execute(final Board board, final String pgn);

	void setDepth(final int depth);

}
