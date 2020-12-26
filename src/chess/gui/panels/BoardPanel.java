package chess.gui.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.gui.Game;
import chess.gui.utils.MouseHandler;

public class BoardPanel extends JPanel
{
	private static final long serialVersionUID = -1580229007174958827L;

	private static Color SQUARE_LIGHT_COLOUR = new Color(0x94, 0xaa, 0xc0, 0xff);
	private static Color SQUARE_DARK_COLOUR = new Color(0x64, 0x7a, 0x8e, 0xff);
	private static Color SQUARE_LIGHT_HIGHLIGHT_COLOUR = new Color(0x94, 0xaa, 0xc0, 0x88);
	private static Color SQUARE_DARK_HIGHLIGHT_COLOUR = new Color(0x64, 0x7a, 0x8e, 0x88);
	public static final Color BG_COLOUR = new Color(0x22, 0x25, 0x2b, 0xff);
	public static final Color MG_COLOUR = new Color(0x29, 0x2c, 0x34, 0xff);
	public static final Color FG_COLOUR = new Color(0x30, 0x32, 0x3d, 0xff);
	public static final int BOARD_PADDING = 20;

	public static final HashMap<Character, BufferedImage> pieceImages = new HashMap<>();

	private final Game m_game;

	public BoardPanel(final Game game)
	{
		this.setBackground(BoardPanel.BG_COLOUR);

		this.addMouseListener(MouseHandler.get());
		this.addMouseMotionListener(MouseHandler.get());

		m_game = game;
	}

	public void loadSpriteSheet(String path)
	{
		try
		{
			pieceImages.clear();

			BufferedImage spriteSheet = ImageIO.read(new File(path));
			int spriteWidth = spriteSheet.getWidth() / 6;
			int spriteHeight = spriteSheet.getHeight() / 2;

			pieceImages.put('k', spriteSheet.getSubimage(0 * spriteWidth, 0 * spriteHeight, spriteWidth, spriteHeight));
			pieceImages.put('q', spriteSheet.getSubimage(1 * spriteWidth, 0 * spriteHeight, spriteWidth, spriteHeight));
			pieceImages.put('b', spriteSheet.getSubimage(2 * spriteWidth, 0 * spriteHeight, spriteWidth, spriteHeight));
			pieceImages.put('n', spriteSheet.getSubimage(3 * spriteWidth, 0 * spriteHeight, spriteWidth, spriteHeight));
			pieceImages.put('r', spriteSheet.getSubimage(4 * spriteWidth, 0 * spriteHeight, spriteWidth, spriteHeight));
			pieceImages.put('p', spriteSheet.getSubimage(5 * spriteWidth, 0 * spriteHeight, spriteWidth, spriteHeight));

			pieceImages.put('K', spriteSheet.getSubimage(0 * spriteWidth, 1 * spriteHeight, spriteWidth, spriteHeight));
			pieceImages.put('Q', spriteSheet.getSubimage(1 * spriteWidth, 1 * spriteHeight, spriteWidth, spriteHeight));
			pieceImages.put('B', spriteSheet.getSubimage(2 * spriteWidth, 1 * spriteHeight, spriteWidth, spriteHeight));
			pieceImages.put('N', spriteSheet.getSubimage(3 * spriteWidth, 1 * spriteHeight, spriteWidth, spriteHeight));
			pieceImages.put('R', spriteSheet.getSubimage(4 * spriteWidth, 1 * spriteHeight, spriteWidth, spriteHeight));
			pieceImages.put('P', spriteSheet.getSubimage(5 * spriteWidth, 1 * spriteHeight, spriteWidth, spriteHeight));

		}
		catch (IOException e)
		{
			System.err.println("Error loading sprite sheet!");
			e.printStackTrace();
		}
	}

	@Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(this.getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());

		drawBoard(g);
	}

	private void drawBoard(Graphics g)
	{
		final Board board = m_game.getBoard();

		int boardSize = Math.min(getWidth(), getHeight()) - BOARD_PADDING * 2;

		boolean lightColour = true;
		int squareSize = boardSize / 8;
		for (int x = 0; x < 8; x++)
		{
			int relativeX = m_game.drawBoardFlipped() ? 7 - x : x;

			lightColour = !lightColour;
			for (int y = 0; y < 8; y++)
			{
				// Draw tile
				g.setColor(lightColour ? SQUARE_DARK_COLOUR : SQUARE_LIGHT_COLOUR);
				int relativeY = m_game.drawBoardFlipped() ? 7 - y : y;

				lightColour = !lightColour;

				g.fillRect(x * squareSize + BOARD_PADDING, y * squareSize + BOARD_PADDING, squareSize, squareSize);

				// Draw possible moves
				if (m_game.getDraggedPiece() != null && m_game.isInDraggedPieceLegalMoves(relativeX + relativeY * 8))
				{
					g.setColor(x % 2 == 0 ? y % 2 == 0 ? SQUARE_DARK_HIGHLIGHT_COLOUR : SQUARE_LIGHT_HIGHLIGHT_COLOUR
							: y % 2 == 0 ? SQUARE_LIGHT_HIGHLIGHT_COLOUR : SQUARE_DARK_HIGHLIGHT_COLOUR);

					g.fillOval(x * squareSize + BOARD_PADDING + squareSize / 4,
							y * squareSize + BOARD_PADDING + squareSize / 4, squareSize / 2, squareSize / 2);
				}

				// Draw tile position indicators
				if (squareSize > 32)
				{
					g.setColor(lightColour ? SQUARE_DARK_COLOUR : SQUARE_LIGHT_COLOUR);
					g.drawString("" + ((char) (relativeX + 'A')) + (7 - relativeY + 1), x * squareSize + BOARD_PADDING,
							y * squareSize + BOARD_PADDING + squareSize);
				}

				// Draw pieces (if they are not being dragged)
				if (m_game.getDraggedPiece() != board.getTile(relativeX + relativeY * BoardUtils.BOARD_WIDTH)
						.getPiece())
				{
					g.drawImage(
							pieceImages.get(board.getTile(relativeX + relativeY * BoardUtils.BOARD_WIDTH).getSymbol()),
							x * squareSize + BOARD_PADDING, y * squareSize + BOARD_PADDING, squareSize, squareSize,
							null);
				}
			}
		}

	}

	public void c_windowResize(int width, int height)
	{
		setSize(width, height);
		validate();
	}

	public void setDarkColour(Color colour)
	{
		SQUARE_DARK_COLOUR = colour;
		SQUARE_DARK_HIGHLIGHT_COLOUR = new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), 0x88);
	}

	public static Color getDarkColour()
	{
		return SQUARE_DARK_COLOUR;
	}

	public void setLightColour(Color colour)
	{
		SQUARE_LIGHT_COLOUR = colour;
		SQUARE_LIGHT_HIGHLIGHT_COLOUR = new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), 0x88);
	}

	public static Color getLightColour()
	{
		return SQUARE_LIGHT_COLOUR;
	}

}
