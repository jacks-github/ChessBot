package chess.engine.piece.move;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chess.engine.board.Board;
import chess.engine.board.BoardUtils;

public class MoveFactory
{
	private static final Pattern KING_SIDE_CASTLE = Pattern.compile("O-O#?\\+?");
	private static final Pattern QUEEN_SIDE_CASTLE = Pattern.compile("O-O-O#?\\+?");
	private static final Pattern PLAIN_PAWN_MOVE = Pattern.compile("^([a-h][0-8])(\\+)?(#)?$");
	private static final Pattern PAWN_ATTACK_MOVE = Pattern.compile("(^[a-h])(x)([a-h][0-8])(\\+)?(#)?$");
	private static final Pattern PLAIN_MAJOR_MOVE = Pattern
			.compile("^(B|N|R|Q|K)([a-h]|[1-8])?([a-h][0-8])(\\+)?(#)?$");
	private static final Pattern MAJOR_ATTACK_MOVE = Pattern
			.compile("^(B|N|R|Q|K)([a-h]|[1-8])?(x)([a-h][0-8])(\\+)?(#)?$");
	private static final Pattern PLAIN_PAWN_PROMOTION_MOVE = Pattern.compile("(.*?)=(.*?)");
	private static final Pattern ATTACK_PAWN_PROMOTION_MOVE = Pattern.compile("(.*?)x(.*?)=(.*?)");

	private MoveFactory()
	{
		throw new RuntimeException("The MoveFactory class should never be instanced!");
	}

	public static Move createMove(final Board board, final int currentPosition, final int destinationPosition)
	{
		for (final Move move : board.getAllLegalMoves())
		{
			if (move.getCurrentPosition() == currentPosition && move.getDestination() == destinationPosition)
			{
				return move;
			}
		}

		return Move.NULL_MOVE;
	}

	public static Move parsePGN(final Board board, final String pgn)
	{
		final Matcher kingSideCastleMatcher = KING_SIDE_CASTLE.matcher(pgn);
		final Matcher queenSideCastleMatcher = QUEEN_SIDE_CASTLE.matcher(pgn);
		final Matcher plainPawnMatcher = PLAIN_PAWN_MOVE.matcher(pgn);
		final Matcher attackPawnMatcher = PAWN_ATTACK_MOVE.matcher(pgn);
		final Matcher pawnPromotionMatcher = PLAIN_PAWN_PROMOTION_MOVE.matcher(pgn);
		final Matcher attackPawnPromotionMatcher = ATTACK_PAWN_PROMOTION_MOVE.matcher(pgn);
		final Matcher plainMajorMatcher = PLAIN_MAJOR_MOVE.matcher(pgn);
		final Matcher attackMajorMatcher = MAJOR_ATTACK_MOVE.matcher(pgn);

		int currentCoordinate;
		int destinationCoordinate;

		if (kingSideCastleMatcher.matches())
		{
			return PGNUtils.extractCastleMove(board, "O-O");
		}
		else if (queenSideCastleMatcher.matches())
		{
			return PGNUtils.extractCastleMove(board, "O-O-O");
		}
		else if (plainPawnMatcher.matches())
		{
			final String destinationSquare = plainPawnMatcher.group(1);
			destinationCoordinate = BoardUtils.getCoordinateAtPosition(destinationSquare);
			currentCoordinate = PGNUtils.deriveCurrentCoordinate(board, "P", destinationSquare, "");
			return MoveFactory.createMove(board, currentCoordinate, destinationCoordinate);
		}
		else if (attackPawnMatcher.matches())
		{
			final String destinationSquare = attackPawnMatcher.group(3);
			destinationCoordinate = BoardUtils.getCoordinateAtPosition(destinationSquare);
			final String disambiguationFile = attackPawnMatcher.group(1) != null ? attackPawnMatcher.group(1) : "";
			currentCoordinate = PGNUtils.deriveCurrentCoordinate(board, "P", destinationSquare, disambiguationFile);
			return MoveFactory.createMove(board, currentCoordinate, destinationCoordinate);
		}
		else if (attackPawnPromotionMatcher.matches())
		{
			final String destinationSquare = attackPawnPromotionMatcher.group(2);
			final String disambiguationFile = attackPawnPromotionMatcher.group(1) != null
					? attackPawnPromotionMatcher.group(1)
					: "";
			destinationCoordinate = BoardUtils.getCoordinateAtPosition(destinationSquare);
			currentCoordinate = PGNUtils.deriveCurrentCoordinate(board, "P", destinationSquare, disambiguationFile);
			return MoveFactory.createMove(board, currentCoordinate, destinationCoordinate);
		}
		else if (pawnPromotionMatcher.find())
		{
			final String destinationSquare = pawnPromotionMatcher.group(1);
			destinationCoordinate = BoardUtils.getCoordinateAtPosition(destinationSquare);
			currentCoordinate = PGNUtils.deriveCurrentCoordinate(board, "P", destinationSquare, "");
			return MoveFactory.createMove(board, currentCoordinate, destinationCoordinate);
		}
		else if (plainMajorMatcher.find())
		{
			final String destinationSquare = plainMajorMatcher.group(3);
			destinationCoordinate = BoardUtils.getCoordinateAtPosition(destinationSquare);
			final String disambiguationFile = plainMajorMatcher.group(2) != null ? plainMajorMatcher.group(2) : "";
			currentCoordinate = PGNUtils.deriveCurrentCoordinate(board, plainMajorMatcher.group(1), destinationSquare,
					disambiguationFile);
			return MoveFactory.createMove(board, currentCoordinate, destinationCoordinate);
		}
		else if (attackMajorMatcher.find())
		{
			final String destinationSquare = attackMajorMatcher.group(4);
			destinationCoordinate = BoardUtils.getCoordinateAtPosition(destinationSquare);
			final String disambiguationFile = attackMajorMatcher.group(2) != null ? attackMajorMatcher.group(2) : "";
			currentCoordinate = PGNUtils.deriveCurrentCoordinate(board, attackMajorMatcher.group(1), destinationSquare,
					disambiguationFile);
			return MoveFactory.createMove(board, currentCoordinate, destinationCoordinate);
		}

		return Move.NULL_MOVE;
	}

}