package chess.engine.ai.minimax;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import javax.swing.JTextArea;

import chess.engine.board.Board;
import chess.engine.piece.move.Move;
import chess.engine.player.MoveStatus;
import chess.engine.player.MoveTransition;

public class ParallelMiniMaxBookStrategy extends MiniMaxBookStrategy
{

	public ParallelMiniMaxBookStrategy(final int depth, final JTextArea outputTextArea)
	{
		super(depth, outputTextArea);
	} 
	
	@Override
	public String toString()
	{
		return "Parallel MiniMax (Book)";
	}
	
	@Override
	protected Map<Move, Integer> calculateMoveValues(final ArrayList<Move> moves, final Board board)
	{
		writeOutputLine("Calculating with " + Runtime.getRuntime().availableProcessors() + " available CPU cores...");
		
		ConcurrentHashMap<Move, Integer> resultCollection = new ConcurrentHashMap<>();

		IntStream.range(0, moves.size()).parallel().forEach(i -> {

			final Move move = moves.get(i);

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
		});

		return resultCollection;
	}

}
