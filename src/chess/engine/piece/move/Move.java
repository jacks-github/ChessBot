package chess.engine.piece.move;

import chess.engine.Allegiance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.piece.Bishop;
import chess.engine.piece.Knight;
import chess.engine.piece.Pawn;
import chess.engine.piece.Piece;
import chess.engine.piece.PieceType;
import chess.engine.piece.Queen;
import chess.engine.piece.Rook;

public abstract class Move
{

	final Board m_board;
	final Piece m_piece;
	final int m_destinationPosition;

	public static final Move NULL_MOVE = new NullMove();

	private Move(final Board board, final Piece piece, final int destinationPosition)
	{
		m_board = board;
		m_piece = piece;
		m_destinationPosition = destinationPosition;
	}
	
	public String getSimplifiedPGN()
	{
		return "";
	}

	@Override
	public boolean equals(final Object other)
	{
		if (this == other)
		{
			return true;
		}

		if (!(other instanceof Move))
		{
			return false;
		}

		final Move otherMove = (Move) other;
		return getDestination() == otherMove.getDestination() && getPiece() == otherMove.getPiece();
	}

	@Override
	public int hashCode()
	{
		int result = 31 + m_destinationPosition;
		result = 31 * result + m_piece.hashCode();
		return result;
	}

	public Board execute()
	{
		final Board.Builder builder = new Board.Builder();

		for (final Piece piece : m_board.getAllPieces())
		{
			if (!m_piece.equals(piece))
			{
				builder.setPiece(piece);
			}
		}

		builder.setPiece(m_piece.move(this));

		builder.setPlayerToMove(m_board.currentPlayer().getOpponent().getAllegiance());
		builder.setWhitePlayerIsCastled(m_board.getWhitePlayer().isCastled());
		builder.setBlackPlayerIsCastled(m_board.getBlackPlayer().isCastled());

		builder.setCurrentPly(m_board.getCurrentPly() + 1);

		return builder.build();
	}

	public int getCurrentPosition()
	{
		return m_piece.getPosition();
	}

	public int getDestination()
	{
		return m_destinationPosition;
	}

	public int getDestinationX()
	{
		return m_destinationPosition % BoardUtils.BOARD_WIDTH;
	}

	public int getDestinationY()
	{
		return m_destinationPosition / BoardUtils.BOARD_WIDTH;
	}

	public Piece getPiece()
	{
		return m_piece;
	}

	public Board getBoard()
	{
		return m_board;
	}

	public boolean isAttack()
	{
		return false;
	}

	public boolean isCastlingMove()
	{
		return false;
	}

	public Piece getAttackedPiece()
	{
		return null;
	}

	public boolean isPromotingMove()
	{
		return false;
	}

	public void setPromotionType(PieceType type)
	{
	}

	public static final class MajorMove extends Move
	{

		public MajorMove(final Board board, final Piece piece, final int destinationPosition)
		{
			super(board, piece, destinationPosition);
		}

		@Override
		public String toString()
		{
			return Character.toUpperCase(m_piece.getSymbol()) + BoardUtils.getAlgebraicNotation(m_destinationPosition);
		}

	}

	public static class AttackingMove extends Move
	{

		final Piece m_attackedPiece;

		public AttackingMove(final Board board, final Piece piece, final int destinationPosition,
				final Piece attackedPiece)
		{
			super(board, piece, destinationPosition);
			m_attackedPiece = attackedPiece;
		}

		@Override
		public int hashCode()
		{
			return m_attackedPiece.hashCode() + super.hashCode();
		}

		@Override
		public boolean equals(final Object other)
		{
			if (this == other)
			{
				return true;
			}

			if (!(other instanceof AttackingMove))
			{
				return false;
			}

			final AttackingMove otherMove = (AttackingMove) other;
			return super.equals(otherMove) && getAttackedPiece().equals(otherMove.getAttackedPiece());
		}

		@Override
		public boolean isAttack()
		{
			return true;
		}

		@Override
		public Piece getAttackedPiece()
		{
			return m_attackedPiece;
		}

		@Override
		public String toString()
		{
			return Character.toUpperCase(m_piece.getSymbol()) + "x"
					+ BoardUtils.getAlgebraicNotation(m_destinationPosition);
		}

	}

	public static class PawnMove extends Move
	{

		public PawnMove(final Board board, final Piece piece, final int destinationPosition)
		{
			super(board, piece, destinationPosition);
		}

		@Override
		public String toString()
		{
			return BoardUtils.getAlgebraicNotation(m_destinationPosition);
		}

	}

	public static class PawnPromotionMove extends PawnMove
	{

		PieceType m_promotionType;

		public PawnPromotionMove(final Board board, final Piece piece, final int destinationPosition,
				final PieceType promotionType)
		{
			super(board, piece, destinationPosition);
			m_promotionType = promotionType;
		}

		@Override
		public void setPromotionType(PieceType type)
		{
			m_promotionType = type;
		}

		@Override
		public boolean isPromotingMove()
		{
			return true;
		}

		@Override
		public Board execute()
		{
			final Board.Builder builder = new Board.Builder();

			for (final Piece piece : m_board.getAllPieces())
			{
				if (!m_piece.equals(piece))
				{
					builder.setPiece(piece);
				}
			}

			switch (m_promotionType)
			{
			case QUEEN:
				builder.setPiece(new Queen(this.getDestination(), this.getPiece().getAllegiance(), false));
				break;
			case BISHOP:
				builder.setPiece(new Bishop(this.getDestination(), this.getPiece().getAllegiance(), false));
				break;
			case KNIGHT:
				builder.setPiece(new Knight(this.getDestination(), this.getPiece().getAllegiance(), false));
				break;
			case ROOK:
				builder.setPiece(new Rook(this.getDestination(), this.getPiece().getAllegiance(), false));
				break;
			default:
				throw new RuntimeException("Invalid PieceType (or no PieceType) specified for promoted pawn!");
			}

			builder.setPlayerToMove(m_board.currentPlayer().getOpponent().getAllegiance());
			builder.setWhitePlayerIsCastled(m_board.getWhitePlayer().isCastled());
			builder.setBlackPlayerIsCastled(m_board.getBlackPlayer().isCastled());

			builder.setCurrentPly(m_board.getCurrentPly() + 1);

			return builder.build();
		}

		@Override
		public String toString()
		{
			return BoardUtils.getAlgebraicNotation(m_destinationPosition) + "="
					+ Character.toUpperCase(m_promotionType.getSymbol());
		}

	}

	public static class PawnAttackingMove extends AttackingMove
	{

		public PawnAttackingMove(final Board board, final Piece piece, final int destinationPosition,
				final Piece attackedPiece)
		{
			super(board, piece, destinationPosition, attackedPiece);
		}

		@Override
		public String toString()
		{
			return BoardUtils.getFileNotation(m_piece.getPosition()) + "x"
					+ BoardUtils.getAlgebraicNotation(m_destinationPosition);
		}

	}

	public static class PawnPromotionAttackingMove extends PawnAttackingMove
	{

		PieceType m_promotionType;

		public PawnPromotionAttackingMove(final Board board, final Piece piece, final int destinationPosition,
				final Piece attackedPiece, final PieceType promotionType)
		{
			super(board, piece, destinationPosition, attackedPiece);
			m_promotionType = promotionType;
		}

		@Override
		public void setPromotionType(PieceType type)
		{
			m_promotionType = type;
		}

		@Override
		public boolean isPromotingMove()
		{
			return true;
		}

		@Override
		public Board execute()
		{
			final Board.Builder builder = new Board.Builder();

			for (final Piece piece : m_board.getAllPieces())
			{
				if (!m_piece.equals(piece) && !m_attackedPiece.equals(piece))
				{
					builder.setPiece(piece);
				}
			}

			switch (m_promotionType)
			{
			case QUEEN:
				builder.setPiece(new Queen(this.getDestination(), this.getPiece().getAllegiance(), false));
				break;
			case BISHOP:
				builder.setPiece(new Bishop(this.getDestination(), this.getPiece().getAllegiance(), false));
				break;
			case KNIGHT:
				builder.setPiece(new Knight(this.getDestination(), this.getPiece().getAllegiance(), false));
				break;
			case ROOK:
				builder.setPiece(new Rook(this.getDestination(), this.getPiece().getAllegiance(), false));
				break;
			default:
				throw new RuntimeException("Invalid PieceType (or no PieceType) specified for promoted pawn!");
			}

			builder.setPlayerToMove(m_board.currentPlayer().getOpponent().getAllegiance());
			builder.setWhitePlayerIsCastled(m_board.getWhitePlayer().isCastled());
			builder.setBlackPlayerIsCastled(m_board.getBlackPlayer().isCastled());

			builder.setCurrentPly(m_board.getCurrentPly() + 1);

			return builder.build();
		}

		@Override
		public String toString()
		{
			return BoardUtils.getFileNotation(m_piece.getPosition()) + "x"
					+ BoardUtils.getAlgebraicNotation(m_destinationPosition) + "="
					+ Character.toUpperCase(m_promotionType.getSymbol());
		}

	}

	public static final class EnPassantMove extends PawnAttackingMove
	{

		public EnPassantMove(final Board board, final Piece piece, final int destinationPosition,
				final Piece attackedPiece)
		{
			super(board, piece, destinationPosition, attackedPiece);
		}

		@Override
		public Board execute()
		{
			final Board.Builder builder = new Board.Builder();

			for (final Piece piece : m_board.getAllPieces())
			{
				if (!m_piece.equals(piece) && !m_attackedPiece.equals(piece))
				{
					builder.setPiece(piece);
				}
			}

			builder.setPiece(m_piece.move(this));
			builder.setPlayerToMove(m_board.currentPlayer().getOpponent().getAllegiance());
			builder.setWhitePlayerIsCastled(m_board.getWhitePlayer().isCastled());
			builder.setBlackPlayerIsCastled(m_board.getBlackPlayer().isCastled());

			builder.setCurrentPly(m_board.getCurrentPly() + 1);

			return builder.build();
		}

		@Override
		public String toString()
		{
			return BoardUtils.getFileNotation(m_piece.getPosition()) + "x"
					+ BoardUtils.getAlgebraicNotation(m_destinationPosition) + "e.p.";
		}

	}

	public static final class PawnJump extends PawnMove
	{

		public PawnJump(final Board board, final Piece piece, final int destinationPosition)
		{
			super(board, piece, destinationPosition);
		}

		@Override
		public Board execute()
		{
			final Board.Builder builder = new Board.Builder();

			for (final Piece piece : m_board.getAllPieces())
			{
				if (!m_piece.equals(piece))
				{
					builder.setPiece(piece);
				}
			}

			final Pawn movedPawn = (Pawn) m_piece.move(this);
			builder.setPiece(movedPawn);
			builder.setEnPassantPawn(movedPawn);
			builder.setPlayerToMove(m_board.currentPlayer().getOpponent().getAllegiance());
			builder.setWhitePlayerIsCastled(m_board.getWhitePlayer().isCastled());
			builder.setBlackPlayerIsCastled(m_board.getBlackPlayer().isCastled());

			builder.setCurrentPly(m_board.getCurrentPly() + 1);

			return builder.build();
		}

	}

	public static abstract class CastlingMove extends Move
	{

		protected final Rook m_rook;
		protected final int m_rookStartPosition;
		protected final int m_rookDestination;

		public CastlingMove(final Board board, final Piece piece, final int destinationPosition, final Rook rook,
				final int rookStartPosition, final int rookDestination)
		{
			super(board, piece, destinationPosition);
			m_rook = rook;
			m_rookStartPosition = rookStartPosition;
			m_rookDestination = rookDestination;
		}

		public Rook getRook()
		{
			return m_rook;
		}

		@Override
		public boolean isCastlingMove()
		{
			return true;
		}

		@Override
		public Board execute()
		{
			final Board.Builder builder = new Board.Builder();

			for (final Piece piece : m_board.getAllPieces())
			{
				if (!m_piece.equals(piece) && !m_rook.equals(piece))
				{
					builder.setPiece(piece);
				}
			}

			builder.setPiece(m_piece.move(this));
			builder.setPiece(new Rook(m_rookDestination, m_rook.getAllegiance(), false));

			builder.setPlayerToMove(m_board.currentPlayer().getOpponent().getAllegiance());
			builder.setWhitePlayerIsCastled(m_board.currentPlayer().getAllegiance() == Allegiance.WHITE ? true
					: m_board.getWhitePlayer().isCastled());
			builder.setBlackPlayerIsCastled(m_board.currentPlayer().getAllegiance() == Allegiance.BLACK ? true
					: m_board.getBlackPlayer().isCastled());

			builder.setCurrentPly(m_board.getCurrentPly() + 1);

			return builder.build();
		}

	}

	public static final class KingsideCastleMove extends CastlingMove
	{

		public KingsideCastleMove(final Board board, final Piece piece, final int destinationPosition, final Rook rook,
				final int rookStartPosition, final int rookDestination)
		{
			super(board, piece, destinationPosition, rook, rookStartPosition, rookDestination);
		}

		@Override
		public String toString()
		{
			return "O-O";
		}

	}

	public static final class QueensideCastleMove extends CastlingMove
	{

		public QueensideCastleMove(final Board board, final Piece piece, final int destinationPosition, final Rook rook,
				final int rookStartPosition, final int rookDestination)
		{
			super(board, piece, destinationPosition, rook, rookStartPosition, rookDestination);
		}

		@Override
		public String toString()
		{
			return "O-O-O";
		}

	}

	public static final class NullMove extends Move
	{

		public NullMove()
		{
			super(null, null, -1);
		}

		@Override
		public final String toString()
		{
			return "Null Move";
		}

		@Override
		public Board execute()
		{
			throw new RuntimeException("A NullMove cannot be executed!");
		}

	}

}
