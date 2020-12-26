package chess.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import chess.engine.Allegiance;
import chess.engine.board.Board;
import chess.engine.piece.PieceType;
import chess.engine.piece.move.Move;

public class GameHistoryPanel extends JPanel
{
	private static final long serialVersionUID = 3146858836319554343L;

	JTextArea m_whiteMoveListArea;
	JTextArea m_blackMoveListArea;

	public GameHistoryPanel()
	{
		this.setBackground(BoardPanel.BG_COLOUR);
		this.setBorder(BorderFactory.createEmptyBorder(20, 5, 5, 5));
		this.setLayout(new BorderLayout());

		JPanel moveListPanel = new JPanel();
		moveListPanel.setLayout(new BorderLayout());
		moveListPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		this.add(moveListPanel, BorderLayout.CENTER);

		JPanel innerMoveListPanel = new JPanel();
		innerMoveListPanel.setLayout(new GridLayout(1, 2));
		innerMoveListPanel.setBackground(BoardPanel.MG_COLOUR);
		moveListPanel.add(innerMoveListPanel, BorderLayout.CENTER);

		JPanel innerMoveListPanelWhite = new JPanel();
		innerMoveListPanelWhite.setBackground(BoardPanel.MG_COLOUR);
		innerMoveListPanelWhite.setBorder(BorderFactory.createEmptyBorder(0, 1, 1, 1));
		innerMoveListPanelWhite.setLayout(new BorderLayout());
		innerMoveListPanel.add(innerMoveListPanelWhite);

		m_whiteMoveListArea = new JTextArea();
		m_whiteMoveListArea.setBackground(BoardPanel.FG_COLOUR);
		m_whiteMoveListArea.setForeground(Color.WHITE);
		m_whiteMoveListArea.setLineWrap(true);
		m_whiteMoveListArea.setEditable(false);
		JScrollPane whiteScroll = new JScrollPane(m_whiteMoveListArea);
		whiteScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		innerMoveListPanelWhite.add(whiteScroll, BorderLayout.CENTER);

		JPanel innerMoveListPanelBlack = new JPanel();
		innerMoveListPanelBlack.setBackground(BoardPanel.MG_COLOUR);
		innerMoveListPanelBlack.setBorder(BorderFactory.createEmptyBorder(0, 1, 1, 1));
		innerMoveListPanelBlack.setLayout(new BorderLayout());
		innerMoveListPanel.add(innerMoveListPanelBlack);

		m_blackMoveListArea = new JTextArea();
		m_blackMoveListArea.setBackground(BoardPanel.FG_COLOUR);
		m_blackMoveListArea.setForeground(Color.WHITE);
		m_blackMoveListArea.setLineWrap(true);
		m_blackMoveListArea.setEditable(false);
		JScrollPane blackScroll = new JScrollPane(m_blackMoveListArea);
		blackScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		innerMoveListPanelBlack.add(blackScroll, BorderLayout.CENTER);

		BoundedRangeModel rangeModel = blackScroll.getVerticalScrollBar().getModel();
		whiteScroll.getVerticalScrollBar().setModel(rangeModel);

		JPanel labelPanel = new JPanel();
		labelPanel.setBackground(BoardPanel.MG_COLOUR);
		labelPanel.setLayout(new GridLayout(1, 2));
		moveListPanel.add(labelPanel, BorderLayout.NORTH);

		JLabel whiteLabel = new JLabel("White", SwingConstants.CENTER);
		whiteLabel.setForeground(Color.WHITE);
		labelPanel.add(whiteLabel, BorderLayout.CENTER);

		JLabel blackLabel = new JLabel("Black", SwingConstants.CENTER);
		blackLabel.setForeground(Color.WHITE);
		labelPanel.add(blackLabel);

	}

	public void clearMoves()
	{
		m_whiteMoveListArea.setText("");
		m_blackMoveListArea.setText("");
	}

	public void addMove(Move move, Board board)
	{
		final Board transitionBoard = move.execute();

		if (move.getPiece().getAllegiance() == Allegiance.WHITE)
		{
			m_whiteMoveListArea.append((m_whiteMoveListArea.getText().length() == 0 ? "" : "\n")
					+ disambiguateMoves(move, board) + calculateCheckSymbols(move, transitionBoard));
		}
		else if (move.getPiece().getAllegiance() == Allegiance.BLACK)
		{
			m_blackMoveListArea.append((m_blackMoveListArea.getText().length() == 0 ? "" : "\n")
					+ disambiguateMoves(move, board) + calculateCheckSymbols(move, transitionBoard));
		}

		m_whiteMoveListArea.setCaretPosition(m_whiteMoveListArea.getText().length() - 1);
	}

	// Removes a line of text from the history panel
	// This method is called when the undo button is clicked
	public void pop(final Board board)
	{
		String moveListText = (board.currentPlayer().getAllegiance() == Allegiance.WHITE ? m_whiteMoveListArea
				: m_blackMoveListArea).getText();
		String[] lines = moveListText.split("\n");
		if (lines.length > 0)
		{
			moveListText = "";
			for (int i = 0; i < lines.length - 1; i++)
			{
				moveListText += lines[i];
				if (i < lines.length - 2)
				{
					moveListText += "\n";
				}
			}
			(board.currentPlayer().getAllegiance() == Allegiance.WHITE ? m_whiteMoveListArea : m_blackMoveListArea)
					.setText(moveListText);
		}
	}

	private String disambiguateMoves(Move move, Board board)
	{

		if (move.getPiece().getType() != PieceType.PAWN && move.getPiece().getType() != PieceType.KING)
		{
			for (final Move otherMove : board.currentPlayer().getLegalMoves())
			{
				if (otherMove.getPiece().getPosition() != move.getPiece().getPosition()
						&& otherMove.getPiece().getType() == move.getPiece().getType()
						&& otherMove.getDestination() == move.getDestination())
				{
					if (otherMove.getPiece().getX() != move.getPiece().getX())
					{
						String str = move.toString();
						return str.substring(0, 1) + move.getPiece().getRank() + str.substring(1);
					}
					else
					{
						String str = move.toString();
						return str.substring(0, 1) + move.getPiece().getFile() + str.substring(1);
					}
				}
			}
		}

		return move.toString();

	}

	private String calculateCheckSymbols(Move move, Board board)
	{

		if (move.getPiece().getAllegiance() == Allegiance.WHITE)
		{
			if (board.getBlackPlayer().isInCheckmate())
			{
				return "#";
			}
			else if (board.getBlackPlayer().isInCheck())
			{
				return "+";
			}
		}
		else if (move.getPiece().getAllegiance() == Allegiance.BLACK)
		{
			if (board.getWhitePlayer().isInCheckmate())
			{
				return "#";
			}
			else if (board.getWhitePlayer().isInCheck())
			{
				return "+";
			}
		}
		return "";
	}

	public void c_windowResize(int width, int height)
	{
		setLocation(0, 0);
		setSize(144, height);
		validate();
	}

	public String getPGN()
	{
		String result = "";
		String[] whiteMoves = m_whiteMoveListArea.getText().split("\n");
		String[] blackMoves = m_blackMoveListArea.getText().split("\n");

		for (int i = 0; i < whiteMoves.length; i++)
		{
			result += (i + 1) + ". ";
			result += whiteMoves[i] + " ";
			if (i < blackMoves.length)
			{
				result += blackMoves[i] + " ";
			}
		}

		return result;
	}

}
