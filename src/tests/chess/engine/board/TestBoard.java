package tests.chess.engine.board;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import chess.engine.board.Board;
import chess.engine.piece.Piece;
import chess.engine.piece.move.Move;

class TestBoard
{

	@Test
	void test()
	{
		final Board board = Board.createStandardBoard();
		assertEquals(board.currentPlayer().getLegalMoves().size(), 20);
		assertEquals(board.currentPlayer().getOpponent().getLegalMoves().size(), 20);
		assertFalse(board.currentPlayer().isInCheck());
		assertFalse(board.currentPlayer().isInCheckmate());
		assertFalse(board.currentPlayer().isCastled());
		assertEquals(board.currentPlayer(), board.getWhitePlayer());
		assertEquals(board.currentPlayer().getOpponent(), board.getBlackPlayer());
		assertFalse(board.currentPlayer().getOpponent().isInCheck());
		assertFalse(board.currentPlayer().getOpponent().isInCheckmate());
		assertFalse(board.currentPlayer().getOpponent().isCastled());

		final ArrayList<Piece> allPieces = new ArrayList<>(board.getAllPieces());
		final ArrayList<Move> allMoves = new ArrayList<>(board.getAllLegalMoves());
		
		for (final Move move : allMoves)
		{
			assertFalse(move.isAttack());
			assertFalse(move.isCastlingMove());
		}

		assertEquals(allMoves.size(), 40);
		assertEquals(allPieces.size(), 32);
	}

}
