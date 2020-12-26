package chess.engine.piece;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import chess.engine.Allegiance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Tile;
import chess.engine.piece.move.Move;

public class Knight extends Piece
{

	private final static int[] CANDIDATE_MOVE_POSITIONS = { -17, -15, -10, -6, 6, 10, 15, 17 };

	public Knight(final int position, final Allegiance allegiance, final boolean isFirstMove)
	{
		super(PieceType.KNIGHT, position, allegiance, isFirstMove);
	}

	@Override
	public String toString()
	{
		return "N";
	}

	@Override
	public List<Move> getLegalMoves(final Board board)
	{
		final List<Move> legalMoves = new ArrayList<>();

		for (final int candidateOffset : CANDIDATE_MOVE_POSITIONS)
		{
			int candidateDestinationPosition = m_position + candidateOffset;

			if (BoardUtils.isValidTilePosition(candidateDestinationPosition))
			{
				if (isInvalidEdgeCase(m_position, candidateOffset))
				{
					continue;
				}

				final Tile candidateDestinationTile = board.getTile(candidateDestinationPosition);

				if (!candidateDestinationTile.isOccupied())
				{
					legalMoves.add(new Move.MajorMove(board, this, candidateDestinationPosition));
				}
				else
				{
					final Piece pieceAtDestination = candidateDestinationTile.getPiece();
					final Allegiance pieceAtDestinationAllegiance = pieceAtDestination.getAllegiance();

					if (m_allegiance != pieceAtDestinationAllegiance)
					{
						legalMoves.add(
								new Move.AttackingMove(board, this, candidateDestinationPosition, pieceAtDestination));
					}
				}

			}
		}

		return Collections.unmodifiableList(legalMoves);
	}

	private static boolean isInvalidEdgeCase(final int currentPosition, final int candidateOffset)
	{
		return isInvalidFirstColumn(currentPosition, candidateOffset)
				|| isInvalidSecondColumn(currentPosition, candidateOffset)
				|| isInvalidSeventhColumn(currentPosition, candidateOffset)
				|| isInvalidEighthColumn(currentPosition, candidateOffset);
	}

	private static boolean isInvalidFirstColumn(final int currentPosition, final int candidateOffset)
	{
		return BoardUtils.IS_FIRST_FILE[currentPosition] && ((candidateOffset == -17) || (candidateOffset == -10)
				|| (candidateOffset == 6) || (candidateOffset == 15));
	}

	private static boolean isInvalidSecondColumn(final int currentPosition, final int candidateOffset)
	{
		return BoardUtils.IS_SECOND_FILE[currentPosition] && ((candidateOffset == -10) || (candidateOffset == 6));
	}

	private static boolean isInvalidSeventhColumn(final int currentPosition, final int candidateOffset)
	{
		return BoardUtils.IS_SEVENTH_FILE[currentPosition] && ((candidateOffset == -6) || (candidateOffset == 10));
	}

	private static boolean isInvalidEighthColumn(final int currentPosition, final int candidateOffset)
	{
		return BoardUtils.IS_EIGHTH_FILE[currentPosition] && ((candidateOffset == -15) || (candidateOffset == -6)
				|| (candidateOffset == 10) || (candidateOffset == 17));
	}

	@Override
	public Piece move(Move move)
	{
		return new Knight(move.getDestination(), move.getPiece().getAllegiance(), false);
	}

}
