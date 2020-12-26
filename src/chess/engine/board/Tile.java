package chess.engine.board;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import chess.engine.Allegiance;
import chess.engine.piece.Piece;

public abstract class Tile
{

	protected final int m_position;

	private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllEmptyTiles();

	private static Map<Integer, EmptyTile> createAllEmptyTiles()
	{
		final Map<Integer, EmptyTile> tiles = new HashMap<>();
		for (int i = 0; i < BoardUtils.NUM_TILES; i++)
		{
			tiles.put(i, new EmptyTile(i));
		}
		return Collections.unmodifiableMap(tiles);
	}

	public static Tile createTile(final int position, final Piece piece)
	{
		return piece == null ? EMPTY_TILES_CACHE.get(position) : new OccupiedTile(position, piece);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + m_position;
		return result;
	}
	
	public abstract char getSymbol();

	public int getPosition()
	{
		return m_position;
	}

	private Tile(final int position)
	{
		m_position = position;
	}

	public abstract boolean isOccupied();

	public abstract Piece getPiece();

	public static final class EmptyTile extends Tile
	{
		private EmptyTile(final int position)
		{
			super(position);
		}

		@Override
		public boolean isOccupied()
		{
			return false;
		}

		@Override
		public Piece getPiece()
		{
			return null;
		}

		@Override
		public char getSymbol()
		{
			return '-';
		}
	}

	public static final class OccupiedTile extends Tile
	{
		private final Piece m_piece;

		private OccupiedTile(final int position, final Piece piece)
		{
			super(position);
			m_piece = piece;
		}

		@Override
		public boolean isOccupied()
		{
			return true;
		}

		@Override
		public Piece getPiece()
		{
			return m_piece;
		}

		@Override
		public char getSymbol()
		{
			return m_piece.getSymbol();
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + m_piece.getSymbol();
			result = prime * result + (m_piece.getAllegiance() == Allegiance.WHITE ? 0 : 137);
			return result;
		}

	}

}
