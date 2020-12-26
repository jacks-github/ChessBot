package chess.engine.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import chess.engine.Allegiance;
import chess.engine.board.Board;
import chess.engine.board.Tile;
import chess.engine.piece.Piece;
import chess.engine.piece.PieceType;
import chess.engine.piece.Rook;
import chess.engine.piece.move.Move;

public class BlackPlayer extends Player
{

	public BlackPlayer(final Board board, final Collection<Move> whiteLegalMoves,
			final Collection<Move> blackLegalMoves, final boolean isCastled)
	{
		super(board, blackLegalMoves, whiteLegalMoves, isCastled);
	}

	@Override
	public String toString()
	{
		return "Black Player";
	}

	@Override
	public int hashCode()
	{
		return 37; 
	}

	@Override
	public Collection<Piece> getActivePieces()
	{
		return m_board.getBlackPieces();
	}

	@Override
	public Allegiance getAllegiance()
	{
		return Allegiance.BLACK;
	}

	@Override
	public Player getOpponent()
	{
		return m_board.getWhitePlayer();
	}

	@Override
	public boolean hasBishopPair()
	{
		int numBishops = 0;
		for (final Piece piece : m_board.getBlackPieces())
		{
			if (piece.getType() == PieceType.BISHOP)
			{
				numBishops++;
			}
		}
		return numBishops == 2;
	}

	@Override
	protected Collection<Move> calculateCastleMoves(final Collection<Move> playerLegalMoves,
			final Collection<Move> oppopnentLegalMoves)
	{
		final List<Move> castleMoves = new ArrayList<>();

		if (m_playerKing.isFirstMove() && !isInCheck())
		{
			if (!m_board.getTile(5).isOccupied() && !m_board.getTile(6).isOccupied())
			{
				final Tile rookTile = m_board.getTile(7);
				if (rookTile.isOccupied() && rookTile.getPiece().getType() == PieceType.ROOK
						&& rookTile.getPiece().isFirstMove())
				{
					if (Player.calculateAttacksOnTile(5, oppopnentLegalMoves).isEmpty()
							&& Player.calculateAttacksOnTile(6, oppopnentLegalMoves).isEmpty())
					{
						castleMoves.add(new Move.KingsideCastleMove(m_board, m_playerKing, 6,
								(Rook) rookTile.getPiece(), rookTile.getPosition(), 5));
					}
				}
			}

			if (!m_board.getTile(1).isOccupied() && !m_board.getTile(2).isOccupied()
					&& !m_board.getTile(3).isOccupied())
			{
				final Tile rookTile = m_board.getTile(0);
				if (rookTile.isOccupied() && rookTile.getPiece().getType() == PieceType.ROOK
						&& rookTile.getPiece().isFirstMove())
				{
					if (Player.calculateAttacksOnTile(1, oppopnentLegalMoves).isEmpty()
							&& Player.calculateAttacksOnTile(2, oppopnentLegalMoves).isEmpty()
							&& Player.calculateAttacksOnTile(3, oppopnentLegalMoves).isEmpty())
					{
						// TODO: add castling move here
						castleMoves.add(new Move.QueensideCastleMove(m_board, m_playerKing, 2,
								(Rook) rookTile.getPiece(), rookTile.getPosition(), 3));
					}
				}
			}
		}

		return Collections.unmodifiableList(castleMoves);
	}

}
