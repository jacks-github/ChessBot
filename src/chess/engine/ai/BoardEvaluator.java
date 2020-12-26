package chess.engine.ai;

import chess.engine.board.Board;

public interface BoardEvaluator
{

	int evaluate(final Board board, final int depth);

}
