package chess.gui.panels;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import chess.engine.piece.Piece;
import chess.gui.Game;
import chess.gui.utils.MouseHandler;

public class DraggedItemPanel extends JPanel
{
	private static final long serialVersionUID = -1014021304880577909L;

	private final Game m_game;

	public DraggedItemPanel(final Game game)
	{
		this.setOpaque(false);

		m_game = game;
	}

	public void tick()
	{
		Piece draggedPiece = m_game.getDraggedPiece();

		if (draggedPiece == null)
		{
			this.setVisible(false);
			// a workaround to prevent flickering when picking up or putting down a piece
			this.setLocation(-99999, -99999);
		}
		else
		{
			this.setVisible(true);

			this.setLocation(MouseHandler.getX() - getSize().width / 2, MouseHandler.getY() - getSize().height / 2);
		}
	}

	@Override
	public void paintComponent(Graphics g)
	{
		Piece draggedPiece = m_game.getDraggedPiece();

		if (draggedPiece != null)
		{
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g.drawImage(BoardPanel.pieceImages.get(draggedPiece.getSymbol()), 0, 0, getWidth(), getHeight(), null);
		}

		super.paintComponent(g);
	}

	public void c_windowResize(final int frameWidth, final int frameHeight)
	{
		int boardSize = Math.min(frameWidth, frameHeight) - BoardPanel.BOARD_PADDING * 2;
		int squareSize = boardSize / 8;
		this.setSize(squareSize, squareSize);
	}

}
