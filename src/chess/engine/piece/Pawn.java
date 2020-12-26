package chess.engine.piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import chess.engine.Allegiance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.piece.move.Move;

public class Pawn extends Piece
{

	private final static int[] CANDIDATE_MOVE_POSITIONS = { 7, 8, 9, 16 };

	public Pawn(final int position, final Allegiance allegiance, final boolean isFirstMove)
	{
		super(PieceType.PAWN, position, allegiance, isFirstMove);
	}

	@Override
	public String toString()
	{
		return "P";
	}

	@Override
	public Collection<Move> getLegalMoves(final Board board)
	{
		final List<Move> legalMoves = new ArrayList<>();

		for (final int candidateOffset : CANDIDATE_MOVE_POSITIONS)
		{
			final int candidateDestinationPosition = m_position + (candidateOffset * m_allegiance.getDirection());

			if (!BoardUtils.isValidTilePosition(candidateDestinationPosition))
			{
				continue;
			}

			// Normal moves
			if (candidateOffset == 8 && !board.getTile(candidateDestinationPosition).isOccupied())
			{
				if (m_allegiance.isPromotionSquare(candidateDestinationPosition))
				{
					legalMoves.add(
							new Move.PawnPromotionMove(board, this, candidateDestinationPosition, PieceType.QUEEN));
					legalMoves
							.add(new Move.PawnPromotionMove(board, this, candidateDestinationPosition, PieceType.ROOK));
					legalMoves.add(
							new Move.PawnPromotionMove(board, this, candidateDestinationPosition, PieceType.BISHOP));
					legalMoves.add(
							new Move.PawnPromotionMove(board, this, candidateDestinationPosition, PieceType.KNIGHT));
				}
				else
				{
					legalMoves.add(new Move.PawnMove(board, this, candidateDestinationPosition));
				}
			}
			// Pawn jumps
			else if (candidateOffset == 16 && isFirstMove()
					&& ((BoardUtils.IS_SECOND_RANK[m_position] && m_allegiance == Allegiance.BLACK)
							|| (BoardUtils.IS_SEVENTH_RANK[m_position] && m_allegiance == Allegiance.WHITE)))
			{
				final int behindCandidateDestination = m_position + (8 * m_allegiance.getDirection());
				if (!board.getTile(behindCandidateDestination).isOccupied()
						&& !board.getTile(candidateDestinationPosition).isOccupied())
				{
					legalMoves.add(new Move.PawnJump(board, this, candidateDestinationPosition));
				}
			}
			// Attacking moves
			else if ((candidateOffset == 9
					&& !((BoardUtils.IS_EIGHTH_FILE[m_position] && m_allegiance == Allegiance.BLACK)
							|| (BoardUtils.IS_FIRST_FILE[m_position] && m_allegiance == Allegiance.WHITE)))
					|| (candidateOffset == 7
							&& !((BoardUtils.IS_EIGHTH_FILE[m_position] && m_allegiance == Allegiance.WHITE)
									|| (BoardUtils.IS_FIRST_FILE[m_position] && m_allegiance == Allegiance.BLACK))))
			{

				if (board.getTile(candidateDestinationPosition).isOccupied())
				{
					final Piece candidateAttackedPiece = board.getTile(candidateDestinationPosition).getPiece();
					if (m_allegiance != candidateAttackedPiece.getAllegiance())
					{

						if (m_allegiance.isPromotionSquare(candidateDestinationPosition))
						{
							legalMoves.add(new Move.PawnPromotionAttackingMove(board, this,
									candidateDestinationPosition, candidateAttackedPiece, PieceType.QUEEN));
							legalMoves.add(new Move.PawnPromotionAttackingMove(board, this,
									candidateDestinationPosition, candidateAttackedPiece, PieceType.ROOK));
							legalMoves.add(new Move.PawnPromotionAttackingMove(board, this,
									candidateDestinationPosition, candidateAttackedPiece, PieceType.BISHOP));
							legalMoves.add(new Move.PawnPromotionAttackingMove(board, this,
									candidateDestinationPosition, candidateAttackedPiece, PieceType.KNIGHT));
						}
						else
						{
							legalMoves.add(new Move.PawnAttackingMove(board, this, candidateDestinationPosition,
									candidateAttackedPiece));
						}

					}
				}

				// En passant capture
				if (board.getEnPassantPawn() != null)
				{
					if ((candidateOffset == 7 && board.getEnPassantPawn().getPosition() == m_position
							+ (1 * -m_allegiance.getDirection()))
							|| (candidateOffset == 9 && board.getEnPassantPawn().getPosition() == m_position
									- (1 * -m_allegiance.getDirection())))
					{
						legalMoves.add(new Move.EnPassantMove(board, this, candidateDestinationPosition,
								board.getEnPassantPawn()));
					}
				}

			}
		}

		return Collections.unmodifiableList(legalMoves);
	}

	@Override
	public Piece move(Move move)
	{
		return new Pawn(move.getDestination(), move.getPiece().getAllegiance(), false);
	}

}
