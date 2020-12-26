package chess.engine.piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import chess.engine.Allegiance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Tile;
import chess.engine.piece.move.Move;

public class Rook extends Piece
{
	private final static int[] CANDIDATE_MOVE_VECTOR_POSITIONS = { -8, -1, 1, 8 };

	public Rook(int position, Allegiance allegiance, final boolean isFirstMove)
	{
		super(PieceType.ROOK, position, allegiance, isFirstMove);
	}

	@Override
	public String toString()
	{
		return "R";
	}

	@Override
	public Collection<Move> getLegalMoves(final Board board)
	{
		final List<Move> legalMoves = new ArrayList<>();

		for (final int candidateOffset : CANDIDATE_MOVE_VECTOR_POSITIONS)
		{
			int candidateDestinationPosition = m_position;

			while (BoardUtils.isValidTilePosition(candidateDestinationPosition))
			{
				if (isInvalidEdgeCase(candidateDestinationPosition, candidateOffset))
				{
					break;
				}

				candidateDestinationPosition += candidateOffset;
				if (BoardUtils.isValidTilePosition(candidateDestinationPosition))
				{
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
							legalMoves.add(new Move.AttackingMove(board, this, candidateDestinationPosition,
									pieceAtDestination));
						}
						break;
					}
				}
			}
		}

		return Collections.unmodifiableList(legalMoves);
	}

	private static boolean isInvalidEdgeCase(final int currentPosition, final int candidateOffset)
	{
		return isInvalidFirstColumn(currentPosition, candidateOffset)
				|| isInvalidEighthColumn(currentPosition, candidateOffset);
	}

	private static boolean isInvalidFirstColumn(final int currentPosition, final int candidateOffset)
	{
		return BoardUtils.IS_FIRST_FILE[currentPosition] && (candidateOffset == -1);
	}

	private static boolean isInvalidEighthColumn(final int currentPosition, final int candidateOffset)
	{
		return BoardUtils.IS_EIGHTH_FILE[currentPosition] && (candidateOffset == 1);
	}

	@Override
	public Piece move(Move move)
	{
		return new Rook(move.getDestination(), move.getPiece().getAllegiance(), false);
	}

}
