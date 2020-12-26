package chess.engine;

import chess.engine.board.BoardUtils;

public enum Allegiance
{
	WHITE
	{

		@Override
		public int getDirection()
		{
			return -1;
		}

		@Override
		public boolean isPromotionSquare(int position)
		{
			return BoardUtils.IS_FIRST_RANK[position];
		}

	},

	BLACK
	{

		@Override
		public int getDirection()
		{
			return 1;
		}

		@Override
		public boolean isPromotionSquare(int position)
		{
			return BoardUtils.IS_EIGHTH_RANK[position];
		}

	};

	public abstract int getDirection();

	public abstract boolean isPromotionSquare(int position);

}
