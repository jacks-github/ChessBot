package chess.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import chess.gui.Game;

public class AIPanel extends JPanel
{
	private static final long serialVersionUID = -5044965592336001907L;

	private final JProgressBar m_evaluationBar;
	private final JTextArea m_textArea;
	private final JLabel m_statusLabel;

	public AIPanel(final Game game)
	{
		this.setBackground(BoardPanel.BG_COLOUR);
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

		JPanel centerPanel = new JPanel();
		this.add(centerPanel, BorderLayout.CENTER);
		centerPanel.setBackground(BoardPanel.BG_COLOUR);
		centerPanel.setLayout(new BorderLayout());

		JPanel evaluationBarPanel = new JPanel();
		evaluationBarPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		evaluationBarPanel.setBackground(BoardPanel.BG_COLOUR);
		evaluationBarPanel.setLayout(new BorderLayout());
		this.add(evaluationBarPanel, BorderLayout.SOUTH);

		m_evaluationBar = new JProgressBar();
		m_evaluationBar.setForeground(Color.WHITE);
		m_evaluationBar.setBackground(Color.BLACK);
		evaluationBarPanel.add(m_evaluationBar, BorderLayout.CENTER);
		m_evaluationBar.setValue(50);

		m_textArea = new JTextArea();
		m_textArea.setBorder(BorderFactory.createRaisedBevelBorder());
		m_textArea.setBackground(BoardPanel.FG_COLOUR);
		m_textArea.setForeground(Color.WHITE);
		m_textArea.setEditable(false);

		JScrollPane scroll = new JScrollPane(m_textArea);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		centerPanel.add(scroll, BorderLayout.CENTER);

		JPanel labelPanel = new JPanel();
		this.add(labelPanel, BorderLayout.NORTH);
		labelPanel.setBackground(BoardPanel.MG_COLOUR);

		m_statusLabel = new JLabel("Human (White) vs Human (Black)");
		m_statusLabel.setForeground(Color.WHITE);
		labelPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		labelPanel.add(m_statusLabel);

		JPanel controlPanel = new JPanel();
		centerPanel.add(controlPanel, BorderLayout.SOUTH);
		controlPanel.setBackground(BoardPanel.BG_COLOUR);
		controlPanel.setLayout(new GridLayout(1, 4));

		JButton nextMoveButton = new JButton("Next Move");
		nextMoveButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				game.makeNextMove();
			}
		});
		controlPanel.add(nextMoveButton);

		JToggleButton autoMoveButton = new JToggleButton("Auto Move");
		autoMoveButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				game.setAutoMove(autoMoveButton.isSelected());
			}
		});
		controlPanel.add(autoMoveButton);

		JLabel depthLabel = new JLabel("AI Depth (Plies):  ", SwingConstants.RIGHT);
		depthLabel.setForeground(Color.WHITE);
		controlPanel.add(depthLabel);

		String[] depths = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17",
				"18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34",
				"35", "36", "37", "38", "39", "40" };
		JComboBox<String> depthComboBox = new JComboBox<>(depths);
		depthComboBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				game.setAIDepth(depthComboBox.getSelectedIndex() + 1);
			}
		});
		controlPanel.add(depthComboBox);
	}

	public void updateAILabel(final Game game)
	{
		m_statusLabel.setText(
				game.getWhiteMoveStrategyString() + " (White) vs " + game.getBlackMoveStrategyString() + " (Black)");
	}

	public JTextArea getEngineOutputArea()
	{
		return m_textArea;
	}

	public void c_windowResize(final int width, final int height)
	{
		setLocation(144, 0);
		setSize(width - 144, height);
		validate();
	}

}
