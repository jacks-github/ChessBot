package chess.engine.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import chess.engine.Allegiance;
import chess.engine.piece.*;
import chess.engine.piece.move.Move;
import chess.engine.player.BlackPlayer;
import chess.engine.player.MoveStatus;
import chess.engine.player.MoveTransition;
import chess.engine.player.Player;
import chess.engine.player.WhitePlayer;

public class Board
{

	private final List<Tile> m_tiles;
	private final Collection<Piece> m_whitePieces;
	private final Collection<Piece> m_blackPieces;

	private final WhitePlayer m_whitePlayer;
	private final BlackPlayer m_blackPlayer;

	private final Player m_currentPlayer;

	private final Pawn m_enPassantPawn;

	private final int m_currentPly;
	
	private final static long[][] ZOBRIST_BITSTRINGS = initZobristBitstrings();
	private static long[][] initZobristBitstrings()
	{
		long[][] bitstrings = new long[64][12];
		final Random random = new Random(137);
		for (int i = 0; i < 64; i++)
		{
			for (int j = 0; j < 12; j++)
			{
				bitstrings[i][j] = random.nextLong();
			}
		}
		return bitstrings;
	}

	private Board(final Builder builder)
	{

		m_tiles = createBoard(builder);
		m_whitePieces = getAllColouredPieces(m_tiles, Allegiance.WHITE);
		m_blackPieces = getAllColouredPieces(m_tiles, Allegiance.BLACK);
		m_enPassantPawn = builder.getEnPassantPawn();

		final Collection<Move> whiteLegalMoves = calculateLegalMoves(m_whitePieces);
		final Collection<Move> blackLegalMoves = calculateLegalMoves(m_blackPieces);

		m_whitePlayer = new WhitePlayer(this, whiteLegalMoves, blackLegalMoves, builder.isWhitePlayerCastled());
		m_blackPlayer = new BlackPlayer(this, whiteLegalMoves, blackLegalMoves, builder.isBlackPlayerCastled());

		m_currentPlayer = builder.getPlayerToMove() == Allegiance.WHITE ? m_whitePlayer : m_blackPlayer;

		m_currentPly = builder.getCurrentPly();

	}
	
	public long getZobristHash()
	{
		final int prime = 31;
		long h = 1;
		
		for (int i = 0; i < 64; i++)
		{
			if (m_tiles.get(i).isOccupied())
			{
				h = h ^ ZOBRIST_BITSTRINGS[i][m_tiles.get(i).getPiece().getZobristConstant()];
			}
		}
		
		h = prime * h + (m_currentPlayer.getAllegiance() == Allegiance.WHITE ? 0 : 3917);
		h = prime * h + (m_enPassantPawn == null ? 0 : m_enPassantPawn.hashCode());
		return h;
	}

	public Board addPiece(final Piece newPiece, final int position)
	{
		Builder builder = new Builder();

		for (Piece piece : getAllPieces())
		{
			builder.setPiece(piece);
		}

		newPiece.setPosition(position);
		builder.setPiece(newPiece);
		builder.setEnPassantPawn(m_enPassantPawn);
		builder.setPlayerToMove(m_currentPlayer.getAllegiance());

		return builder.build();
	}

	public Board removePieceAt(final int position)
	{
		Builder builder = new Builder();

		for (Piece piece : getAllPieces())
		{
			if (piece.getPosition() != position)
			{
				builder.setPiece(piece);
			}
		}

		builder.setEnPassantPawn(m_enPassantPawn);
		builder.setPlayerToMove(m_currentPlayer.getAllegiance());

		return builder.build();
	}

	public Player currentPlayer()
	{
		return m_currentPlayer;
	}

	public final Collection<Piece> getWhitePieces()
	{
		return m_whitePieces;
	}

	public final Collection<Piece> getBlackPieces()
	{
		return m_blackPieces;
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < BoardUtils.NUM_TILES; i++)
		{
			final String tileText = "" + m_tiles.get(i).getSymbol();
			builder.append(String.format("%3s", tileText));
			if ((i + 1) % BoardUtils.BOARD_WIDTH == 0)
			{
				builder.append("\n");
			}
		}
		return builder.toString();
	}

	public final Player getWhitePlayer()
	{
		return m_whitePlayer;
	}

	public final Player getBlackPlayer()
	{
		return m_blackPlayer;
	}

	// the definitive function that gets all legal moves for a piece
	public Collection<Move> getLegalMoves(final Piece piece)
	{
		List<Move> moves = new ArrayList<>();

		/*
		 * we have to calculate legal moves in two passes to account for castling that
		 * is to say that the white player can only know whether or not they can castle
		 * once we have calculated black's moves to see if they are threatening any of
		 * the squares the king must pass through, and vice versa
		 */
		for (final var move : (piece.getAllegiance() == Allegiance.WHITE ? m_whitePlayer.getLegalMoves()
				: m_blackPlayer.getLegalMoves()))
		{
			if (move.getPiece() == piece)
			{
				moves.add(move);
			}
		}

		// remove moves that would hang check
		for (int i = 0; i < moves.size(); i++)
		{
			final MoveTransition transition = (piece.getAllegiance() == Allegiance.WHITE ? m_whitePlayer
					: m_blackPlayer).makeMove(moves.get(i));
			if (transition.getMoveStatus() != MoveStatus.DONE)
			{
				moves.remove(i);
				i--;
			}
		}

		return moves;
	}

	public Collection<Move> getAllLegalMoves()
	{
		return Collections.unmodifiableCollection(
				Stream.concat(m_whitePlayer.getLegalMoves().stream(), m_blackPlayer.getLegalMoves().stream())
						.collect(Collectors.toList()));
	}

	public Collection<Piece> getAllPieces()
	{
		return Collections.unmodifiableCollection(
				Stream.concat(m_whitePieces.stream(), m_blackPieces.stream()).collect(Collectors.toList()));
	}

	public int getCurrentPly()
	{
		return m_currentPly;
	}

	public Pawn getEnPassantPawn()
	{
		return m_enPassantPawn;
	}

	private Collection<Move> calculateLegalMoves(Collection<Piece> pieces)
	{
		List<Move> legalMoves = new ArrayList<>();

		for (final Piece piece : pieces)
		{
			legalMoves.addAll(piece.getLegalMoves(this));
		}

		return Collections.unmodifiableList(legalMoves);
	}

	private static Collection<Piece> getAllColouredPieces(List<Tile> board, Allegiance allegiance)
	{
		List<Piece> pieces = new ArrayList<>();

		for (final Tile tile : board)
		{
			if (tile.isOccupied())
			{
				final Piece piece = tile.getPiece();
				if (piece.getAllegiance() == allegiance)
				{
					pieces.add(piece);
				}
			}
		}

		return Collections.unmodifiableCollection(pieces);
	}

	public static Board createStandardBoard()
	{
		final Builder builder = new Builder();

		builder.setPiece(new Rook(0, Allegiance.BLACK, true));
		builder.setPiece(new Knight(1, Allegiance.BLACK, true));
		builder.setPiece(new Bishop(2, Allegiance.BLACK, true));
		builder.setPiece(new Queen(3, Allegiance.BLACK, true));
		builder.setPiece(new King(4, Allegiance.BLACK, true));
		builder.setPiece(new Bishop(5, Allegiance.BLACK, true));
		builder.setPiece(new Knight(6, Allegiance.BLACK, true));
		builder.setPiece(new Rook(7, Allegiance.BLACK, true));
		for (int i = 8; i <= 15; i++)
		{
			builder.setPiece(new Pawn(i, Allegiance.BLACK, true));
		}

		builder.setPiece(new Rook(56, Allegiance.WHITE, true));
		builder.setPiece(new Knight(57, Allegiance.WHITE, true));
		builder.setPiece(new Bishop(58, Allegiance.WHITE, true));
		builder.setPiece(new Queen(59, Allegiance.WHITE, true));
		builder.setPiece(new King(60, Allegiance.WHITE, true));
		builder.setPiece(new Bishop(61, Allegiance.WHITE, true));
		builder.setPiece(new Knight(62, Allegiance.WHITE, true));
		builder.setPiece(new Rook(63, Allegiance.WHITE, true));
		for (int i = 48; i <= 55; i++)
		{
			builder.setPiece(new Pawn(i, Allegiance.WHITE, true));
		}

		builder.setPlayerToMove(Allegiance.WHITE);

		builder.setCurrentPly(0);

		return builder.build();
	}

	public Tile getTile(final int position)
	{
		return m_tiles.get(position);
	}

	private static List<Tile> createBoard(final Builder builder)
	{
		final Tile[] tiles = new Tile[BoardUtils.NUM_TILES];
		for (int i = 0; i < BoardUtils.NUM_TILES; i++)
		{
			tiles[i] = Tile.createTile(i, builder.getBoardConfig().get(i));
		}
		return Collections.unmodifiableList(Arrays.asList(tiles));
	}

	public static class Builder
	{
		private Map<Integer, Piece> m_boardConfig;
		private Allegiance m_playerToMove;
		private Pawn m_enPassantPawn;
		private boolean m_isWhitePlayerCastled, m_isBlackPlayerCastled;
		private int m_currentPly;

		public Builder()
		{
			m_boardConfig = new HashMap<>();
		}

		public Builder setPiece(final Piece piece)
		{
			m_boardConfig.put(piece.getPosition(), piece);
			return this;
		}

		public Builder setPlayerToMove(Allegiance playerToMove)
		{
			m_playerToMove = playerToMove;
			return this;
		}

		public Map<Integer, Piece> getBoardConfig()
		{
			return m_boardConfig;
		}

		public Allegiance getPlayerToMove()
		{
			return m_playerToMove;
		}

		public Board build()
		{
			return new Board(this);
		}

		public void setEnPassantPawn(Pawn pawn)
		{
			m_enPassantPawn = pawn;
		}

		public Pawn getEnPassantPawn()
		{
			return m_enPassantPawn;
		}

		public void setWhitePlayerIsCastled(final boolean value)
		{
			m_isWhitePlayerCastled = value;
		}

		public boolean isWhitePlayerCastled()
		{
			return m_isWhitePlayerCastled;
		}

		public void setBlackPlayerIsCastled(final boolean value)
		{
			m_isBlackPlayerCastled = value;
		}

		public boolean isBlackPlayerCastled()
		{
			return m_isBlackPlayerCastled;
		}

		public void setCurrentPly(final int ply)
		{
			m_currentPly = ply;
		}

		public int getCurrentPly()
		{
			return m_currentPly;
		}

	}

}
