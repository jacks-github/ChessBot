package chess.gui.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import chess.engine.board.Board;
import chess.engine.piece.move.Move;
import chess.gui.Game;
import chess.gui.Window;

public class GUIPanel extends JPanel
{
	private static final long serialVersionUID = 4781728031895691895L;

	private final GameHistoryPanel m_historyPanel;
	private final AIPanel m_evaluationPanel;
	private final Window m_window;

	public GUIPanel(final Window window)
	{
		m_window = window;

		this.setBackground(BoardPanel.BG_COLOUR);
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(0, 0, 23, 20));

		JPanel centerPanel = new JPanel();
		this.add(centerPanel);
		centerPanel.setLayout(new BorderLayout());

		m_historyPanel = new GameHistoryPanel();
		centerPanel.add(m_historyPanel, BorderLayout.WEST);

		m_evaluationPanel = new AIPanel(window.getGame());
		centerPanel.add(m_evaluationPanel, BorderLayout.CENTER);

		addButtons();
	}

	private void addButtons()
	{

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(BoardPanel.BG_COLOUR);
		buttonPanel.setLayout(new GridLayout(1, 4));
		this.add(buttonPanel, BorderLayout.SOUTH);

		JButton undoButton = new JButton("Undo");
		undoButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				m_window.c_undoMove();
			}
		});
		buttonPanel.add(undoButton);

		JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				m_window.c_resetBoard();
			}
		});
		buttonPanel.add(resetButton);

		JButton loadButton = new JButton("Import PGN");
		loadButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				m_window.c_importPGN();
			}
		});
		buttonPanel.add(loadButton);

		JButton saveButton = new JButton("Export PGN");
		saveButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				m_window.c_exportPGN();
			}
		});
		buttonPanel.add(saveButton);

	}

	public void popHistoryNotation(final Board board)
	{
		m_historyPanel.pop(board);
	}

	public void clearHistory()
	{
		m_historyPanel.clearMoves();
	}

	public void addMoveToHistory(Move move, Board board)
	{
		m_historyPanel.addMove(move, board);
	}

	public void updateAILabel(final Game game)
	{
		m_evaluationPanel.updateAILabel(game);
	}

	public JTextArea getEngineOutputArea()
	{
		return m_evaluationPanel.getEngineOutputArea();
	}

	public String getPGN()
	{
		return m_historyPanel.getPGN();
	}

	public void c_windowResize(int width, int height)
	{
		int panelWidth = -1;
		int panelHeight = -1;

		if (width > height)
		{
			panelWidth = width - height;
			panelHeight = height;

			setLocation(height, 0);
		}
		else
		{
			panelWidth = width;
			panelHeight = height - width;

			setLocation(0, width);
		}

		setSize(panelWidth, panelHeight);

		m_historyPanel.c_windowResize(panelWidth, panelHeight);
		m_evaluationPanel.c_windowResize(panelWidth, panelHeight);

		validate();
	}

}
