package chess.engine.ai.alphabeta;

import java.util.ArrayList;
import java.util.Map;

import javax.swing.JTextArea;

import chess.engine.ai.book.OpeningBook;
import chess.engine.board.Board;
import chess.engine.piece.move.Move;

public class AlphaBetaBookStrategy extends AlphaBetaStrategy
{

	public AlphaBetaBookStrategy(final int depth, final JTextArea outputTextArea)
	{
		super(depth, outputTextArea);
	}

	@Override
	public String toString()
	{
		return "AlphaBeta (Book)";
	}

	@Override
	protected Move calculateBestMove(final Board board, final String pgn)
	{
		final Move bookMove = pgnLookup(board, pgn);
		if (bookMove != null)
		{
			writeOutputLine("A response to this position is in the opening book.");
			return bookMove;
		}

		writeOutputLine("A response to this position is not in the opening book. Analysing as normal...");

		ArrayList<Move> moves = calculateMoves(board);
		Map<Move, Integer> moveValues = calculateMoveValues(moves, board);

		return chooseMoveFromList(moves, moveValues, board.currentPlayer().getAllegiance());
	}

	private Move pgnLookup(final Board board, final String pgn)
	{
		writeOutputLine("Checking opening book...");
		return OpeningBook.pgnLookup(board, pgn);
	}

}
