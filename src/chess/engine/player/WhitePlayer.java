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

public class WhitePlayer extends Player
{

	public WhitePlayer(final Board board, final Collection<Move> whiteLegalMoves,
			final Collection<Move> blackLegalMoves, final boolean isCastled)
	{
		super(board, whiteLegalMoves, blackLegalMoves, isCastled);
	}

	@Override
	public String toString()
	{
		return "White Player";
	}
	
	@Override
	public int hashCode()
	{
		return 19; 
	}

	@Override
	public Collection<Piece> getActivePieces()
	{
		return m_board.getWhitePieces();
	}

	@Override
	public Allegiance getAllegiance()
	{
		return Allegiance.WHITE;
	}

	@Override
	public Player getOpponent()
	{
		return m_board.getBlackPlayer();
	}

	@Override
	public boolean hasBishopPair()
	{
		int numBishops = 0;
		for (final Piece piece : m_board.getWhitePieces())
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
			if (!m_board.getTile(61).isOccupied() && !m_board.getTile(62).isOccupied())
			{
				final Tile rookTile = m_board.getTile(63);
				if (rookTile.isOccupied() && rookTile.getPiece().getType() == PieceType.ROOK
						&& rookTile.getPiece().isFirstMove())
				{
					if (Player.calculateAttacksOnTile(61, oppopnentLegalMoves).isEmpty()
							&& Player.calculateAttacksOnTile(62, oppopnentLegalMoves).isEmpty())
					{
						castleMoves.add(new Move.KingsideCastleMove(m_board, m_playerKing, 62,
								(Rook) rookTile.getPiece(), rookTile.getPosition(), 61));
					}
				}
			}

			if (!m_board.getTile(59).isOccupied() && !m_board.getTile(58).isOccupied()
					&& !m_board.getTile(57).isOccupied())
			{
				final Tile rookTile = m_board.getTile(56);
				if (rookTile.isOccupied() && rookTile.getPiece().getType() == PieceType.ROOK
						&& rookTile.getPiece().isFirstMove())
				{
					if (Player.calculateAttacksOnTile(59, oppopnentLegalMoves).isEmpty()
							&& Player.calculateAttacksOnTile(58, oppopnentLegalMoves).isEmpty()
							&& Player.calculateAttacksOnTile(57, oppopnentLegalMoves).isEmpty())
					{
						castleMoves.add(new Move.QueensideCastleMove(m_board, m_playerKing, 58,
								(Rook) rookTile.getPiece(), rookTile.getPosition(), 59));
					}
				}
			}
		}

		return Collections.unmodifiableList(castleMoves);
	}

}
