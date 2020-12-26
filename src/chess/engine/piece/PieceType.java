package chess.engine.piece;

public enum PieceType
{
	PAWN
	{
		@Override
		public char getSymbol()
		{
			return 'P';
		}
	},

	BISHOP
	{
		@Override
		public char getSymbol()
		{
			return 'B';
		}
	},

	ROOK
	{
		@Override
		public char getSymbol()
		{
			return 'R';
		}
	},

	KNIGHT
	{
		@Override
		public char getSymbol()
		{
			return 'N';
		}
	},

	QUEEN
	{
		@Override
		public char getSymbol()
		{
			return 'Q';
		}
	},

	KING
	{
		@Override
		public char getSymbol()
		{
			return 'K';
		}
	};

	public abstract char getSymbol();

	public static PieceType getType(final char c)
	{
		switch (c)
		{
		case 'p', 'P':
			return PAWN;
		case 'n', 'N':
			return KNIGHT;
		case 'b', 'B':
			return BISHOP;
		case 'r', 'R':
			return ROOK;
		case 'q', 'Q':
			return QUEEN;
		case 'k', 'K':
			return KING;
		default:
			throw new RuntimeException("ERROR: Tried to get an invalid PieceType");
		}
	}

}
