package chess.engine.ai.minimax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTextArea;

import chess.engine.Allegiance;
import chess.engine.ai.BoardEvaluator;
import chess.engine.ai.MoveStrategy;
import chess.engine.ai.StandardEvaluator;
import chess.engine.board.Board;
import chess.engine.piece.move.Move;
import chess.engine.player.MoveStatus;
import chess.engine.player.MoveTransition;

public class MiniMaxStrategy implements MoveStrategy
{

	protected final BoardEvaluator m_boardEvaluator;
	protected int m_depth;
	private final JTextArea m_outputTextArea;

	public MiniMaxStrategy(final int depth, final JTextArea outputTextArea)
	{
		m_boardEvaluator = new StandardEvaluator();
		m_depth = depth;
		m_outputTextArea = outputTextArea;
	}

	public void setDepth(final int depth)
	{
		m_depth = depth;
	}

	@Override
	public String toString()
	{
		return "MiniMax";
	}

	protected synchronized void writeOutputLine(final String str)
	{
		if (m_outputTextArea != null)
		{
			m_outputTextArea.append(str + "\n");
			m_outputTextArea.setCaretPosition(m_outputTextArea.getText().length() - 1);
		}
		else
		{
			System.out.print("\n" + str);
		}
	}

	@Override
	public Move execute(final Board board, final String pgn)
	{

		final long startTime = System.currentTimeMillis();

		if (m_outputTextArea != null)
		{
			m_outputTextArea.setText("");
		}

		writeOutputLine(board.currentPlayer().toString() + " (" + this.toString() + ") thinking... (depth = " + m_depth
				+ " plies)");

		final Move bestMove = calculateBestMove(board, pgn);

		final long executionTimeMillis = System.currentTimeMillis() - startTime;
		final float executionTimeSeconds = executionTimeMillis / 1000f;
		writeOutputLine("Finished in " + executionTimeSeconds + " seconds");

		if (bestMove != null)
		{
			writeOutputLine("Decided to play " + bestMove.toString());
		}
		else
		{
			writeOutputLine("No moves found.");
		}

		return bestMove;
	}

	protected Move calculateBestMove(final Board board, final String pgn)
	{
		final ArrayList<Move> moves = calculateMoves(board);
		final Map<Move, Integer> moveValues = calculateMoveValues(moves, board);

		return chooseMoveFromList(moves, moveValues, board.currentPlayer().getAllegiance());
	}

	protected Move chooseMoveFromList(final ArrayList<Move> moves, final Map<Move, Integer> moveValues,
			final Allegiance playerAllegiance)
	{
		int highestSeenValue = Integer.MIN_VALUE;
		int lowestSeenValue = Integer.MAX_VALUE;
		int currentValue;

		Move bestMove = null;

		for (final Move move : moveValues.keySet())
		{
			if (moveValues.get(move) != null)
			{
				currentValue = moveValues.get(move);

				if (playerAllegiance == Allegiance.WHITE && currentValue >= highestSeenValue)
				{
					highestSeenValue = currentValue;
					bestMove = move;
				}
				else if (playerAllegiance == Allegiance.BLACK && currentValue <= lowestSeenValue)
				{
					lowestSeenValue = currentValue;
					bestMove = move;
				}
			}
		}

		if (bestMove == null)
		{
			bestMove = moves.get(0);
			writeOutputLine("No good moves found. Choosing " + bestMove.toString() + " for no particular reason.");
		}

		return bestMove;
	}

	protected ArrayList<Move> calculateMoves(final Board board)
	{
		ArrayList<Move> moves = new ArrayList<>();
		moves.addAll(board.currentPlayer().getLegalMoves());
		return moves;
	}

	protected Map<Move, Integer> calculateMoveValues(final ArrayList<Move> moves, final Board board)
	{
		HashMap<Move, Integer> resultCollection = new HashMap<>();

		for (final Move move : moves)
		{
			final MoveTransition transition = board.currentPlayer().makeMove(move);

			if (transition.getMoveStatus() == MoveStatus.DONE)
			{
				final int value = calculateValue(board, transition.getBoard());

				resultCollection.put(move, value);

				writeOutputLine("\t" + move.toString() + " = " + value);
			}
			else
			{
				writeOutputLine("\t" + move.toString() + " is illegal");
			}
		}

		return resultCollection;
	}

	protected int calculateValue(final Board board, final Board transitionBoard)
	{
		return board.currentPlayer().getAllegiance() == Allegiance.WHITE ? min(transitionBoard, m_depth)
				: max(transitionBoard, m_depth);
	}

	private int min(final Board board, final int depth)
	{
		if (depth <= 1 || isGameOverScenario(board))
		{
			return m_boardEvaluator.evaluate(board, depth);
		}

		int lowestSeenValue = Integer.MAX_VALUE;

		for (final Move move : calculateMoves(board))
		{
			final MoveTransition transition = board.currentPlayer().makeMove(move);
			if (transition.getMoveStatus() == MoveStatus.DONE)
			{
				final int currentValue = max(transition.getBoard(), depth - 1);
				if (currentValue <= lowestSeenValue)
				{
					lowestSeenValue = currentValue;
				}
			}
		}

		return lowestSeenValue;
	}

	private int max(final Board board, final int depth)
	{
		if (depth <= 1 || isGameOverScenario(board))
		{
			return m_boardEvaluator.evaluate(board, depth);
		}

		int highestSeenValue = Integer.MIN_VALUE;

		for (final Move move : calculateMoves(board))
		{
			final MoveTransition transition = board.currentPlayer().makeMove(move);
			if (transition.getMoveStatus() == MoveStatus.DONE)
			{
				final int currentValue = min(transition.getBoard(), depth - 1);
				if (currentValue >= highestSeenValue)
				{
					highestSeenValue = currentValue;
				}
			}
		}

		return highestSeenValue;
	}

	protected static boolean isGameOverScenario(final Board board)
	{
		return board.currentPlayer().isInCheckmate() || board.currentPlayer().isInStalemate();
	}

}
