package chess.engine.board;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardUtils
{

	public static final int BOARD_WIDTH = 8;
	public static final int BOARD_HEIGHT = 8;
	public static final int NUM_TILES = BOARD_WIDTH * BOARD_WIDTH;

	public static final boolean[] IS_FIRST_FILE = initColumn(0);
	public static final boolean[] IS_SECOND_FILE = initColumn(1);
	public static final boolean[] IS_SEVENTH_FILE = initColumn(6);
	public static final boolean[] IS_EIGHTH_FILE = initColumn(7);

	public final static List<String> ALGEBRAIC_NOTATION = initializeAlgebraicNotation();
	public final static Map<String, Integer> POSITION_TO_COORDINATE = initializePositionToCoordinateMap();

	private static final boolean[] initColumn(int columnNumber)
	{
		final boolean[] tiles = new boolean[NUM_TILES];
		do
		{
			tiles[columnNumber] = true;
			columnNumber += BOARD_WIDTH;
		}
		while (columnNumber < NUM_TILES);
		return tiles;
	}

	public static final boolean[] IS_FIRST_RANK = initRow(0);
	public static final boolean[] IS_SECOND_RANK = initRow(1);
	public static final boolean[] IS_SEVENTH_RANK = initRow(6);
	public static final boolean[] IS_EIGHTH_RANK = initRow(7);

	private static final boolean[] initRow(int rowNumber)
	{
		final boolean[] tiles = new boolean[NUM_TILES];
		for (int x = 0; x < 8; x++)
		{
			for (int y = 0; y < 8; y++)
			{
				if (y == rowNumber)
				{
					tiles[x + y * BOARD_WIDTH] = true;
				}
			}
		}
		return tiles;
	}

	private BoardUtils()
	{
		throw new RuntimeException("BoardUtils should not be instantiated!");
	}

	public static boolean isValidTilePosition(final int position)
	{
		return position >= 0 && position < 64;
	}

	public static String getAlgebraicNotation(int position)
	{
		return ALGEBRAIC_NOTATION.get(position);
	}

	public static String getFileNotation(int position)
	{
		int x = position % BOARD_WIDTH;

		return "" + ((char) (x + 'a'));
	}

	private static List<String> initializeAlgebraicNotation()
	{
		return Collections.unmodifiableList(Arrays.asList("a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8", "a7", "b7",
				"c7", "d7", "e7", "f7", "g7", "h7", "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6", "a5", "b5", "c5",
				"d5", "e5", "f5", "g5", "h5", "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4", "a3", "b3", "c3", "d3",
				"e3", "f3", "g3", "h3", "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2", "a1", "b1", "c1", "d1", "e1",
				"f1", "g1", "h1"));
	}

	private static Map<String, Integer> initializePositionToCoordinateMap()
	{
		final Map<String, Integer> positionToCoordinate = new HashMap<>();
		for (int i = 0; i < NUM_TILES; i++)
		{
			positionToCoordinate.put(ALGEBRAIC_NOTATION.get(i), i);
		}
		return Collections.unmodifiableMap(positionToCoordinate);
	}

	public static int getCoordinateAtPosition(final String position)
	{
		return POSITION_TO_COORDINATE.get(position);
	}

	public static String getPositionAtCoordinate(final int coordinate)
	{
		return ALGEBRAIC_NOTATION.get(coordinate);
	}

}
