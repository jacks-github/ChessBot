package chess.engine.ai;

import java.util.HashMap;
import java.util.Map;

import chess.engine.Allegiance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.piece.Piece;
import chess.engine.piece.PieceType;
import chess.engine.player.Player;

public final class StandardEvaluator implements BoardEvaluator
{

	private static final Map<PieceType, Integer> PIECE_VALUES = createPieceValueMap();

	private static final Map<PieceType, Integer> createPieceValueMap()
	{
		Map<PieceType, Integer> map = new HashMap<>();

		// Piece values are given in centipawns
		map.put(PieceType.PAWN, 100);
		map.put(PieceType.KNIGHT, 300);
		map.put(PieceType.BISHOP, 310);
		map.put(PieceType.ROOK, 500);
		map.put(PieceType.QUEEN, 900);
		map.put(PieceType.KING, 1000000);

		return map;
	}

	@Override
	public int evaluate(final Board board, final int depth)
	{
		return scorePlayer(board, board.getWhitePlayer(), depth) - scorePlayer(board, board.getBlackPlayer(), depth);
	}

	private int scorePlayer(final Board board, final Player player, final int currentDepth)
	{
		// Otherwise, look at some factors to make a decision about who is winning
		return totalPieceValue(player) + checkmateHeuristic(player, currentDepth) + checkHeuristic(player)
				+ mobilityHeuristic(player) + castlingHeuristic(player) + hasBishopPairHeuristic(player)
				+ developmentHeuristic(player, board);
	}

	// Add up the total value of the pieces on the board
	// More material = a better chance of winning
	private static int totalPieceValue(final Player player)
	{
		int score = 0;
		for (final Piece piece : player.getActivePieces())
		{
			score += PIECE_VALUES.get(piece.getType());
		}
		return score;
	}

	/*
	 * We apply bonuses to developing moves for the first 10 turns of the game. This
	 * number is completely arbitrary and should really be replaced with some
	 * algorithm that looks at pieces in context
	 */
	private static int developmentHeuristic(final Player player, final Board board)
	{
		return pawnDevelopmentHeuristic(player, board) + knightDevelopmentHeuristic(player, board)
				+ bishopDevelopmentHeuristic(player, board);
	}

	/*
	 * Evaluating pawn moves is incredibly complex, since many factors can matter
	 * such as pawn structure and passed pawns additionally at the start of the game
	 * pushing central pawns tends to be much more important both to get your pieces
	 * into the center and to prevent your opponent from doing the same.
	 * 
	 * For now, each square is ranked according to how important it is to put a pawn
	 * on. For example, pushing e4 and d4 in the opening is encouraged
	 * 
	 * This table is copied from
	 * https://www.chessprogramming.org/Simplified_Evaluation_Function
	 * 
	 * More advanced analysis is needed later
	 */

	private static final int[] PAWN_DEVELOPMENT_MAP = { 0, 0, 0, 0, 0, 0, 0, 0, 50, 50, 50, 50, 50, 50, 50, 50, 10, 10,
			20, 30, 30, 20, 10, 10, 5, 5, 10, 25, 25, 10, 5, 5, 0, 0, 0, 20, 20, 0, 0, 0, 5, -5, -10, 0, 0, -10, -5, 5,
			5, 10, 10, -20, -20, 10, 10, 5, 0, 0, 0, 0, 0, 0, 0, 0 };

	private static int pawnDevelopmentHeuristic(final Player player, final Board board)
	{

		int heuristic = 0;
		for (final Piece piece : player.getActivePieces())
		{
			if (piece.getType() == PieceType.PAWN)
			{
				int val = PAWN_DEVELOPMENT_MAP[piece.getX()
						+ (player.getAllegiance() == Allegiance.WHITE ? piece.getY() : 7 - piece.getY())
								* BoardUtils.BOARD_WIDTH];

				heuristic += val;
			}
		}

		return heuristic;
	}

	private static final int[] BISHOP_DEVELOPMENT_MAP = { -20, -10, -10, -10, -10, -10, -10, -20, -10, 0, 0, 0, 0, 0, 0,
			-10, -10, 0, 5, 10, 10, 5, 0, -10, -10, 5, 5, 10, 10, 5, 5, -10, -10, 0, 25, 10, 10, 25, 0, -10, -10, 10,
			10, 20, 20, 10, 10, -10, -10, 35, 0, 0, 0, 0, 35, -10, -20, -10, -10, -10, -10, -10, -10, -20, };

	private static int bishopDevelopmentHeuristic(final Player player, final Board board)
	{

		int heuristic = 0;
		for (final Piece piece : player.getActivePieces())
		{
			if (piece.getType() == PieceType.BISHOP)
			{
				heuristic += BISHOP_DEVELOPMENT_MAP[piece.getX()
						+ (player.getAllegiance() == Allegiance.WHITE ? piece.getY() : 7 - piece.getY())
								* BoardUtils.BOARD_WIDTH];
			}
		}
		return heuristic;
	}

	/*
	 * Knights tend to be more useful towards the center of the board, so a lookup
	 * table is used as a quick and dirty way to value centralised knights a little
	 * more highly in the opening. This table is lifted directly from
	 * https://www.chessprogramming.org/Simplified_Evaluation_Function
	 */

	private static final int[] KNIGHT_DEVELOPMENT_MAP =
		{ -50, -40, -30, -30, -30, -30, -40, -50,
			-40, -20, 0, 0, 0, 0, -20, -40,
			-30, 0, 10, 15, 15, 10, 0, -30,
			-30, 5, 15, 20, 20, 15, 5, -30,
			-30, 0, 15, 20, 20, 15, 0, -30,
			-30, 5, 10, 15, 15, 10, 5, -30,
			-40, -20, 0, 5, 5, 0, -20, -40,
		-50, -40, -30, -30, -30, -30, -40, -50 };

	private static int knightDevelopmentHeuristic(final Player player, final Board board)
	{

		int heuristic = 0;
		for (final Piece piece : player.getActivePieces())
		{
			if (piece.getType() == PieceType.KNIGHT)
			{
				heuristic += KNIGHT_DEVELOPMENT_MAP[piece.getX()
						+ (player.getAllegiance() == Allegiance.WHITE ? piece.getY() : 7 - piece.getY())
								* BoardUtils.BOARD_WIDTH];
			}
		}

		return heuristic;
	}

	// give a slight bonus to positions that retain the bishop pair
	private static int hasBishopPairHeuristic(final Player player)
	{
		return player.hasBishopPair() ? 30 : 0;
	}

	// Apply a small bonus to players who are castled
	private static int castlingHeuristic(final Player player)
	{
		// The bonus is currently 110 centipawns
		return player.isCastled() ? 110 : 0;
	}

	// Apply a small bonus to moves that leave a lot of pieces mobile
	// In theory, more options means better moves on average next turn
	private static int mobilityHeuristic(final Player player)
	{
		// Current bonus is 1 centipawn per legal move
		return player.getLegalMoves().size();
	}

	private static int checkHeuristic(final Player player)
	{
		return player.getOpponent().isInCheck() ? 40 : 0;
	}

	// Moves that checkmate the opponent are obviously the best possible moves,
	// and moves that would result in getting checkmated should be avoided
	private static int checkmateHeuristic(final Player player, final int currentDepth)
	{
		// Depth is taken into account because mate in 2 is stronger than mate in 3 for
		// example
		return player.getOpponent().isInCheckmate() ? 1000000 * currentDepth : 0;
	}

}
