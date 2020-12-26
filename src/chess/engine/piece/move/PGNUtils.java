package chess.engine.piece.move;

import java.util.ArrayList;
import java.util.List;

import chess.engine.board.Board;
import chess.engine.board.BoardUtils;

public class PGNUtils
{

	public static Move extractCastleMove(final Board board, final String castleMove)
	{
		for (final Move move : board.currentPlayer().getLegalMoves())
		{
			if (move.isCastlingMove() && move.toString().equals(castleMove))
			{
				return move;
			}
		}
		return Move.NULL_MOVE;
	}

	public static int deriveCurrentCoordinate(final Board board, final String movedPiece,
			final String destinationSquare, final String disambiguationFile) throws RuntimeException
	{
		final List<Move> currentCandidates = new ArrayList<>();
		final int destinationCoordinate = BoardUtils.getCoordinateAtPosition(destinationSquare);
		for (final Move move : board.currentPlayer().getLegalMoves())
		{
			if (move.getDestination() == destinationCoordinate && move.getPiece().toString().equals(movedPiece))
			{
				currentCandidates.add(move);
			}
		}

		if (currentCandidates.size() == 0)
		{
			return -1;
		}

		return currentCandidates.size() == 1 ? currentCandidates.iterator().next().getCurrentPosition()
				: extractFurther(currentCandidates, movedPiece, disambiguationFile);

	}

	public static int extractFurther(final List<Move> candidateMoves, final String movedPiece,
			final String disambiguationFile)
	{

		final List<Move> currentCandidates = new ArrayList<>();

		for (final Move move : candidateMoves)
		{
			if (move.getPiece().toString().equals(movedPiece))
			{
				currentCandidates.add(move);
			}
		}

		if (currentCandidates.size() == 1)
		{
			return currentCandidates.iterator().next().getCurrentPosition();
		}

		final List<Move> candidatesRefined = new ArrayList<>();

		for (final Move move : currentCandidates)
		{
			final String pos = BoardUtils.getPositionAtCoordinate(move.getCurrentPosition());
			if (pos.contains(disambiguationFile))
			{
				candidatesRefined.add(move);
			}
		}

		if (candidatesRefined.size() == 1)
		{
			return candidatesRefined.iterator().next().getCurrentPosition();
		}

		return -1;

	}

}
