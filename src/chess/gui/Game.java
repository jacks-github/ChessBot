package chess.gui;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JOptionPane;

import chess.engine.Allegiance;
import chess.engine.ai.MoveStrategy;
import chess.engine.board.Board;
import chess.engine.piece.Piece;
import chess.engine.piece.PieceType;
import chess.engine.piece.move.Move;
import chess.gui.panels.BoardPanel;
import chess.gui.utils.MouseHandler;

public class Game
{

	private Piece m_draggedPiece;
	private Collection<Move> m_draggedPieceLegalMoves;
	private ArrayList<Board> m_boards = new ArrayList<>();
	private boolean m_drawBoardFlipped;
	private Window m_window;

	private MoveStrategy m_whiteMoveStrategy = null;
	private MoveStrategy m_blackMoveStrategy = null;

	private boolean m_autoMove = false;
	private boolean m_flagShouldMove = false;
	private int m_AIDepth = 1;

	public Game()
	{
		reset();
	}

	public void makeNextMove()
	{
		m_flagShouldMove = true;
	}

	public void setWhiteMoveStrategy(final MoveStrategy strategy)
	{
		m_whiteMoveStrategy = strategy;
	}

	public void setBlackMoveStrategy(final MoveStrategy strategy)
	{
		m_blackMoveStrategy = strategy;
	}

	public String getWhiteMoveStrategyString()
	{
		if (m_whiteMoveStrategy == null)
		{
			return "Human";
		}
		else
		{
			return m_whiteMoveStrategy.toString();
		}
	}

	public String getBlackMoveStrategyString()
	{
		if (m_blackMoveStrategy == null)
		{
			return "Human";
		}
		else
		{
			return m_blackMoveStrategy.toString();
		}
	}

	public void setWindow(Window window)
	{
		m_window = window;
	}

	public void reset()
	{
		m_boards.clear();
		m_boards.add(Board.createStandardBoard());
	}

	public void popBoard()
	{
		if (m_boards.size() > 1)
		{
			m_boards.remove(m_boards.size() - 1);
		}
	}

	public Board getCurrentBoard()
	{
		return m_boards.get(m_boards.size() - 1);
	}

	public void tick(final int width, final int height)
	{

		if (m_autoMove || m_flagShouldMove)
		{
			if (this.getCurrentBoard().currentPlayer().getAllegiance() == Allegiance.WHITE)
			{
				nextMoveWhite();
			}
			else
			{
				nextMoveBlack();
			}
		}

		MouseHandler.tick();

		// the user can click pieces and drag them around to make moves
		if (MouseHandler.buttonClicked(MouseEvent.BUTTON1))
		{
			int boardSize = Math.min(width, height) - BoardPanel.BOARD_PADDING * 2;
			int squareSize = boardSize / 8;

			if (MouseHandler.getX() >= BoardPanel.BOARD_PADDING
					&& MouseHandler.getX() < Math.min(width, height) - BoardPanel.BOARD_PADDING - 1
					&& MouseHandler.getY() >= BoardPanel.BOARD_PADDING
					&& MouseHandler.getY() < Math.min(width, height) - BoardPanel.BOARD_PADDING - 2)
			{
				int mouseX = (MouseHandler.getX() - BoardPanel.BOARD_PADDING) / squareSize;
				int mouseY = (MouseHandler.getY() - BoardPanel.BOARD_PADDING) / squareSize;

				int relativeX = m_drawBoardFlipped ? 7 - mouseX : mouseX;
				int relativeY = m_drawBoardFlipped ? 7 - mouseY : mouseY;

				int clickedPosition = relativeX + relativeY * 8;

				if (getCurrentBoard().getTile(clickedPosition).isOccupied())
				{
					m_draggedPiece = getCurrentBoard().getTile(clickedPosition).getPiece();

					if (getCurrentBoard().currentPlayer().getActivePieces().contains(m_draggedPiece))
					{
						m_draggedPieceLegalMoves = getCurrentBoard().getLegalMoves(m_draggedPiece);
					}
				}
			}
		}

		if (!MouseHandler.buttonDown(MouseEvent.BUTTON1) && m_draggedPiece != null)
		{
			// Make move
			Move move = getMove(width, height);

			if (move != null)
			{
				if (move.isPromotingMove())
				{
					Object[] options = { "Queen", "Rook", "Bishop", "Knight" };
					int n = JOptionPane.showOptionDialog(m_window.getFrame(), "Choose a piece to promote to",
							"Promotion", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
							null);

					switch (n)
					{
					case 0:
						move.setPromotionType(PieceType.QUEEN);
						break;
					case 1:
						move.setPromotionType(PieceType.ROOK);
						break;
					case 2:
						move.setPromotionType(PieceType.BISHOP);
						break;
					case 3:
						move.setPromotionType(PieceType.KNIGHT);
						break;
					}
				}
				executeMove(move);

			}

			m_draggedPiece = null;
			m_draggedPieceLegalMoves = null;
		}

	}

	public final Board getBoard()
	{
		return getCurrentBoard();
	}

	public void toggleDrawFlipped()
	{
		m_drawBoardFlipped = !m_drawBoardFlipped;
	}

	public final boolean drawBoardFlipped()
	{
		return m_drawBoardFlipped;
	}

	public Piece getDraggedPiece()
	{
		return m_draggedPiece;
	}

	private Move getMove(int width, int height)
	{
		if (m_draggedPieceLegalMoves != null)
		{

			int boardSize = Math.min(width, height) - BoardPanel.BOARD_PADDING * 2;
			int squareSize = boardSize / 8;

			int mouseX = (MouseHandler.getX() - BoardPanel.BOARD_PADDING) / squareSize;
			int mouseY = (MouseHandler.getY() - BoardPanel.BOARD_PADDING) / squareSize;

			int relativeX = m_drawBoardFlipped ? 7 - mouseX : mouseX;
			int relativeY = m_drawBoardFlipped ? 7 - mouseY : mouseY;

			int mousePosition = relativeX + relativeY * 8;

			for (var move : m_draggedPieceLegalMoves)
			{
				if (move.getDestination() == mousePosition)
				{
					return move;
				}
			}
		}

		return null;
	}

	public boolean isInDraggedPieceLegalMoves(int position)
	{
		if (m_draggedPieceLegalMoves != null)
		{
			for (var move : m_draggedPieceLegalMoves)
			{
				if (move.getDestination() == position)
				{
					return true;
				}
			}
		}

		return false;
	}

	private void executeMove(final Move move)
	{
		if (move != null)
		{
			m_window.addMoveToHistory(move, getCurrentBoard());
			m_boards.add(move.execute());
		}
	}

	public void nextMoveWhite()
	{
		if (m_whiteMoveStrategy != null && !getCurrentBoard().currentPlayer().isInCheckmate()
				&& !getCurrentBoard().currentPlayer().isInStalemate())
		{
			executeMove(m_whiteMoveStrategy.execute(getCurrentBoard(), m_window.getPGN()));
		}
		m_flagShouldMove = false;
	}

	public void nextMoveBlack()
	{
		if (m_blackMoveStrategy != null && !getCurrentBoard().currentPlayer().isInCheckmate()
				&& !getCurrentBoard().currentPlayer().isInStalemate())
		{
			executeMove(m_blackMoveStrategy.execute(getCurrentBoard(), m_window.getPGN()));
		}
		m_flagShouldMove = false;
	}

	public void setAutoMove(boolean value)
	{
		m_autoMove = value;
	}

	public int getAIDepth()
	{
		return m_AIDepth;
	}

	public void setAIDepth(int depth)
	{
		m_AIDepth = depth;
		if (m_whiteMoveStrategy != null)
		{
			m_whiteMoveStrategy.setDepth(depth);
		}
		if (m_blackMoveStrategy != null)
		{
			m_blackMoveStrategy.setDepth(depth);
		}
	}

}
