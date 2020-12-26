package chess.engine.ai.alphabeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JTextArea;

import chess.engine.Allegiance;
import chess.engine.ai.minimax.MiniMaxStrategy;
import chess.engine.board.Board;
import chess.engine.piece.PieceType;
import chess.engine.piece.move.Move;
import chess.engine.player.MoveStatus;
import chess.engine.player.MoveTransition;

public class AlphaBetaStrategy extends MiniMaxStrategy
{

	public AlphaBetaStrategy(final int depth, final JTextArea outputTextArea)
	{
		super(depth, outputTextArea);
	}

	@Override
	public String toString()
	{
		return "AlphaBeta";
	}

	/*
	 * When alpha beta pruning a minimax tree, we should sort the moves first to
	 * improve efficiency. Moves can be prioritised according to a number of
	 * different factors, but most importantly, attacking moves come first since
	 * these are more likely to result in active lines of play
	 */
	protected final static Comparator<Move> movePriorityComparator = new Comparator<>()
	{
		@Override
		public int compare(Move a, Move b)
		{
			if (a.isAttack() && !b.isAttack())
			{
				return -1;
			}
			else if (b.isAttack() && !a.isAttack())
			{
				return 1;
			}

			if (a.isAttack() && b.isAttack())
			{
				if (a.getAttackedPiece().getType() == PieceType.KING)
				{
					return -1;
				}
				else if (b.getAttackedPiece().getType() == PieceType.KING)
				{
					return 1;
				}
			}

			return 0;
		}
	};

	@Override
	protected ArrayList<Move> calculateMoves(final Board board)
	{
		ArrayList<Move> moves = new ArrayList<>();
		moves.addAll(board.currentPlayer().getLegalMoves());
		Collections.sort(moves, movePriorityComparator);
		return moves;
	}

	@Override
	protected int calculateValue(final Board board, final Board transitionBoard)
	{
		return board.currentPlayer().getAllegiance() == Allegiance.WHITE
				? alphaBetaMin(transitionBoard, m_depth, Integer.MIN_VALUE, Integer.MAX_VALUE)
				: alphaBetaMax(transitionBoard, m_depth, Integer.MIN_VALUE, Integer.MAX_VALUE);

		// Alpha = max value found so far
		// Beta  = min value found so far
	}

	protected int alphaBetaMin(final Board board, final int depth, final int alpha, int beta)
	{
		if (depth <= 1 || isGameOverScenario(board))
		{
			return m_boardEvaluator.evaluate(board, depth);
		}

		for (final Move move : calculateMoves(board))
		{
			final MoveTransition transition = board.currentPlayer().makeMove(move);
			if (transition.getMoveStatus() == MoveStatus.DONE)
			{
				final int score = alphaBetaMax(transition.getBoard(), depth - 1, alpha, beta);

				if (score <= alpha)
				{
					return alpha; // fail hard alpha cutoff
				}

				if (score < beta)
				{
					beta = score;
				}
			}
		}

		return beta;
	}

	protected int alphaBetaMax(final Board board, final int depth, int alpha, final int beta)
	{
		if (depth <= 1 || isGameOverScenario(board))
		{
			return m_boardEvaluator.evaluate(board, depth);
		}

		for (final Move move : calculateMoves(board))
		{
			final MoveTransition transition = board.currentPlayer().makeMove(move);
			if (transition.getMoveStatus() == MoveStatus.DONE)
			{
				final int score = alphaBetaMin(transition.getBoard(), depth - 1, alpha, beta);

				if (score >= beta)
				{
					return beta; // fail hard beta cutoff
				}

				if (score > alpha)
				{
					alpha = score;
				}
			}
		}

		return alpha;
	}

}
