package chess.engine.piece;

import java.util.Collection;

import chess.engine.Allegiance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.piece.move.Move;

public abstract class Piece
{

	protected int m_position;
	protected final Allegiance m_allegiance;
	protected final boolean m_isFirstMove;
	protected final PieceType m_type;
	private final int m_cachedHashCode;

	Piece(final PieceType type, final int position, final Allegiance allegiance, final boolean isFirstMove)
	{
		m_type = type;
		m_position = position;
		m_allegiance = allegiance;
		m_isFirstMove = isFirstMove;
		m_cachedHashCode = computeHashCode();
	}

	public abstract Collection<Move> getLegalMoves(final Board board);

	public abstract Piece move(Move move);

	public void setPosition(final int position)
	{
		m_position = position;
	}

	@Override
	public boolean equals(final Object other)
	{
		if (this == other)
		{
			return true;
		}

		if (!(other instanceof Piece))
		{
			return false;
		}

		final Piece otherPiece = (Piece) other;
		return m_position == otherPiece.getPosition() && m_type == otherPiece.getType()
				&& m_allegiance == otherPiece.getAllegiance() && m_isFirstMove == otherPiece.isFirstMove();
	}

	private int computeHashCode()
	{
		int result = m_type.hashCode();
		result = 31 * result + m_allegiance.hashCode();
		result = 31 * result + m_position;
		result = 31 * result + (m_isFirstMove ? 1 : 0);
		return result;
	}

	@Override
	public int hashCode()
	{
		return m_cachedHashCode;
	}

	public char getSymbol()
	{
		return m_allegiance == Allegiance.WHITE ? Character.toLowerCase(m_type.getSymbol()) : m_type.getSymbol();
	}

	public int getPosition()
	{
		return m_position;
	}

	public int getX()
	{
		return m_position % BoardUtils.BOARD_WIDTH;
	}

	public int getY()
	{
		return m_position / BoardUtils.BOARD_WIDTH;
	}

	public char getFile()
	{
		return (char) ('1' + (char) (7 - getY()));
	}

	public char getRank()
	{
		return (char) ('a' + (char) (getX()));
	}

	public Allegiance getAllegiance()
	{
		return m_allegiance;
	}

	public boolean isFirstMove()
	{
		return m_isFirstMove;
	}

	public PieceType getType()
	{
		return m_type;
	}

	public int getZobristConstant()
	{
		switch (m_type)
		{
		case PAWN:
			return m_allegiance == Allegiance.WHITE ? 0 : 1;
		case KNIGHT:
			return m_allegiance == Allegiance.WHITE ? 2 : 3;
		case BISHOP:
			return m_allegiance == Allegiance.WHITE ? 4 : 5;
		case ROOK:
			return m_allegiance == Allegiance.WHITE ? 6 : 7;
		case QUEEN:
			return m_allegiance == Allegiance.WHITE ? 8 : 9;
		case KING:
			return m_allegiance == Allegiance.WHITE ? 10 : 11;
		default:
			throw new RuntimeException("ERROR: Tried to get an invalid PieceType");
		}
	}

}
