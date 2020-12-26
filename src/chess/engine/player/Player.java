package chess.engine.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import chess.engine.Allegiance;
import chess.engine.board.Board;
import chess.engine.piece.King;
import chess.engine.piece.Piece;
import chess.engine.piece.PieceType;
import chess.engine.piece.move.Move;

public abstract class Player
{

	protected final Board m_board;
	protected final King m_playerKing;
	protected final Collection<Move> m_legalMoves;
	private final boolean m_isInCheck;
	private final boolean m_isCastled;

	protected Player(final Board board, final Collection<Move> legalMoves, final Collection<Move> opponentLegalMoves,
			final boolean isCastled)
	{
		m_isCastled = isCastled;
		m_board = board;
		m_playerKing = findKing();
		m_isInCheck = !Player.calculateAttacksOnTile(m_playerKing.getPosition(), opponentLegalMoves).isEmpty();
		m_legalMoves = Collections.unmodifiableCollection(
				Stream.concat(legalMoves.stream(), calculateCastleMoves(legalMoves, opponentLegalMoves).stream())
						.collect(Collectors.toList()));
	}

	public static Collection<Move> calculateAttacksOnTile(final int position, final Collection<Move> moves)
	{
		final List<Move> attackMoves = new ArrayList<>();
		for (final Move move : moves)
		{
			if (position == move.getDestination())
			{
				attackMoves.add(move);
			}
		}
		return Collections.unmodifiableList(attackMoves);
	}

	public Collection<Move> getLegalMoves()
	{
		return m_legalMoves;
	}

	private King findKing()
	{
		for (final Piece piece : getActivePieces())
		{
			if (piece.getType() == PieceType.KING)
			{
				return (King) piece;
			}
		}

		throw new RuntimeException("No king found on the board!\n" + m_board.toString());
	}

	public boolean isMoveLegal(final Move move)
	{
		return m_legalMoves.contains(move);
	}

	public boolean isInCheck()
	{
		return m_isInCheck;
	}

	protected boolean hasEscapeMoves()
	{
		for (final Move move : m_legalMoves)
		{
			final MoveTransition transition = makeMove(move);
			if (transition.getMoveStatus() == MoveStatus.DONE)
			{
				return true;
			}
		}
		return false;
	}

	public abstract boolean hasBishopPair();
	
	public boolean isInCheckmate()
	{
		return isInCheck() && !hasEscapeMoves();
	}

	public boolean isInStalemate()
	{
		return !isInCheck() && !hasEscapeMoves();
	}

	public boolean isCastled()
	{
		return m_isCastled;
	}

	public MoveTransition makeMove(final Move move)
	{
		if (!isMoveLegal(move))
		{
			return new MoveTransition(m_board, move, MoveStatus.ILLEGAL);
		}

		final Board transitionBoard = move.execute();

		final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(
				transitionBoard.currentPlayer().getOpponent().findKing().getPosition(),
				transitionBoard.currentPlayer().getLegalMoves());

		if (!kingAttacks.isEmpty())
		{
			return new MoveTransition(m_board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
		}

		return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
	}

	public abstract Collection<Piece> getActivePieces();

	public abstract Allegiance getAllegiance();

	public abstract Player getOpponent();

	protected abstract Collection<Move> calculateCastleMoves(Collection<Move> playerLegalMoves,
			Collection<Move> oppopnentLegalMoves);

}
