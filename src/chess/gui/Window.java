package chess.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import chess.engine.ai.alphabeta.AlphaBetaBookStrategy;
import chess.engine.ai.alphabeta.AlphaBetaStrategy;
import chess.engine.ai.alphabeta.ParallelAlphaBetaBookStrategy;
import chess.engine.ai.alphabeta.ParallelAlphaBetaStrategy;
import chess.engine.ai.minimax.MiniMaxBookStrategy;
import chess.engine.ai.minimax.MiniMaxStrategy;
import chess.engine.ai.minimax.ParallelMiniMaxBookStrategy;
import chess.engine.ai.minimax.ParallelMiniMaxStrategy;
import chess.engine.board.Board;
import chess.engine.piece.move.Move;
import chess.gui.panels.BoardPanel;
import chess.gui.panels.DraggedItemPanel;
import chess.gui.panels.GUIPanel;
import chess.gui.utils.MouseHandler;

public class Window
{

	private JFrame m_frame;
	private BoardPanel m_boardCanvas;
	private Game m_game;
	private GUIPanel m_guiPanel;
	private DraggedItemPanel m_draggedItemPanel;

	public HashMap<KeyStroke, Action> m_actionMap = new HashMap<>();

	public Window(Game game)
	{
		m_game = game;
		game.setWindow(this);

		m_frame = new JFrame("ChessBot");
		m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		m_frame.setIconImage(new ImageIcon("res/icon.png").getImage());
		m_frame.setLayout(new BorderLayout());
		m_frame.setSize(new Dimension(1280, 720));

		JLayeredPane layeredPane = new JLayeredPane();
		m_frame.add(layeredPane, BorderLayout.CENTER);

		final JMenuBar menuBar = new JMenuBar();
		populateMenuBar(menuBar);
		m_frame.setJMenuBar(menuBar);

		m_frame.setLocationRelativeTo(null);
		m_frame.setVisible(true);

		m_boardCanvas = new BoardPanel(m_game);
		layeredPane.add(m_boardCanvas, Integer.valueOf(0));
		m_boardCanvas.loadSpriteSheet("res/pieces/default.png");

		m_guiPanel = new GUIPanel(this);
		layeredPane.add(m_guiPanel, Integer.valueOf(1));

		m_draggedItemPanel = new DraggedItemPanel(game);
		layeredPane.add(m_draggedItemPanel, Integer.valueOf(2));

		m_frame.addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent componentEvent)
			{
				c_windowResize();
			}
		});

		// Setup dispatcher for keystrokes (such as ctrl-z to undo)
		KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		kfm.addKeyEventDispatcher(new KeyEventDispatcher()
		{

			@Override
			public boolean dispatchKeyEvent(KeyEvent e)
			{
				KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
				if (m_actionMap.containsKey(keyStroke))
				{
					final Action a = m_actionMap.get(keyStroke);
					final ActionEvent ae = new ActionEvent(e.getSource(), e.getID(), null);
					SwingUtilities.invokeLater(new Runnable()
					{
						@Override
						public void run()
						{
							a.actionPerformed(ae);
						}
					});
					return true;
				}
				return false;
			}
		});

		KeyStroke undoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK);
		m_actionMap.put(undoKeyStroke, new AbstractAction("undoAction")
		{
			private static final long serialVersionUID = -103829162054943822L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				c_undoMove();
			}
		});

		c_windowResize();

		m_frame.addMouseListener(MouseHandler.get());
		m_frame.addMouseMotionListener(MouseHandler.get());

	}

	public JFrame getFrame()
	{
		return m_frame;
	}

	public void addMoveToHistory(Move move, Board board)
	{
		m_guiPanel.addMoveToHistory(move, board);
	}

	private void populateMenuBar(JMenuBar menuBar)
	{
		menuBar.add(createFileMenu());
		menuBar.add(createBoardMenu());
		menuBar.add(createCosmeticsMenu());
		menuBar.add(createAIMenu());
	}

	private JMenu createAIMenu()
	{
		final JMenu aiMenu = new JMenu("AI");
		aiMenu.setMnemonic(KeyEvent.VK_A);

		final JMenu whiteMenu = new JMenu("White");
		aiMenu.add(whiteMenu);
		whiteMenu.setMnemonic(KeyEvent.VK_W);

		JMenuItem whiteHumanItem = new JMenuItem("Human");
		whiteMenu.add(whiteHumanItem);
		whiteHumanItem.setMnemonic(KeyEvent.VK_H);
		whiteHumanItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				m_game.setWhiteMoveStrategy(null);
				updateAILabel(m_game);
			}
		});

		JMenu whiteMiniMaxMenu = new JMenu("MiniMax");
		whiteMenu.add(whiteMiniMaxMenu);
		whiteMiniMaxMenu.setMnemonic(KeyEvent.VK_M);

		JMenuItem whiteMiniMaxItem = new JMenuItem("MiniMax");
		whiteMiniMaxMenu.add(whiteMiniMaxItem);
		whiteMiniMaxMenu.setMnemonic(KeyEvent.VK_M);
		whiteMiniMaxItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				m_game.setWhiteMoveStrategy(new MiniMaxStrategy(m_game.getAIDepth(), getEngineOutputArea()));
				updateAILabel(m_game);
			}
		});

		JMenuItem whiteParallelMiniMaxItem = new JMenuItem("Parallel MiniMax");
		whiteMiniMaxMenu.add(whiteParallelMiniMaxItem);
		whiteParallelMiniMaxItem.setMnemonic(KeyEvent.VK_P);
		whiteParallelMiniMaxItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				m_game.setWhiteMoveStrategy(new ParallelMiniMaxStrategy(m_game.getAIDepth(), getEngineOutputArea()));
				updateAILabel(m_game);
			}
		});

		JMenuItem whiteMiniMaxBookItem = new JMenuItem("MiniMax (Book)");
		whiteMiniMaxMenu.add(whiteMiniMaxBookItem);
		whiteMiniMaxBookItem.setMnemonic(KeyEvent.VK_B);
		whiteMiniMaxBookItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				m_game.setWhiteMoveStrategy(new MiniMaxBookStrategy(m_game.getAIDepth(), getEngineOutputArea()));
				updateAILabel(m_game);
			}
		});

		JMenuItem whiteParallelMiniMaxBookItem = new JMenuItem("Parallel MiniMax (Book)");
		whiteMiniMaxMenu.add(whiteParallelMiniMaxBookItem);
		whiteParallelMiniMaxBookItem.setMnemonic(KeyEvent.VK_O);
		whiteParallelMiniMaxBookItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				m_game.setWhiteMoveStrategy(
						new ParallelMiniMaxBookStrategy(m_game.getAIDepth(), getEngineOutputArea()));
				updateAILabel(m_game);
			}
		});

		JMenu whiteAlphaBetaMenu = new JMenu("AlphaBeta");
		whiteAlphaBetaMenu.setMnemonic(KeyEvent.VK_A);
		whiteMenu.add(whiteAlphaBetaMenu);

		JMenuItem whiteAlphaBetaItem = new JMenuItem("AlphaBeta");
		whiteAlphaBetaMenu.add(whiteAlphaBetaItem);
		whiteAlphaBetaItem.setMnemonic(KeyEvent.VK_A);
		whiteAlphaBetaItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				m_game.setWhiteMoveStrategy(new AlphaBetaStrategy(m_game.getAIDepth(), getEngineOutputArea()));
				updateAILabel(m_game);
			}
		});

		JMenuItem whiteParallelAlphaBetaItem = new JMenuItem("Parallel AlphaBeta");
		whiteAlphaBetaMenu.add(whiteParallelAlphaBetaItem);
		whiteParallelAlphaBetaItem.setMnemonic(KeyEvent.VK_P);
		whiteParallelAlphaBetaItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				m_game.setWhiteMoveStrategy(new ParallelAlphaBetaStrategy(m_game.getAIDepth(), getEngineOutputArea()));
				updateAILabel(m_game);
			}
		});

		JMenuItem whiteAlphaBetaBookItem = new JMenuItem("AlphaBeta (Book)");
		whiteAlphaBetaMenu.add(whiteAlphaBetaBookItem);
		whiteAlphaBetaBookItem.setMnemonic(KeyEvent.VK_B);
		whiteAlphaBetaBookItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				m_game.setWhiteMoveStrategy(new AlphaBetaBookStrategy(m_game.getAIDepth(), getEngineOutputArea()));
				updateAILabel(m_game);
			}
		});

		JMenuItem whiteParallelAlphaBetaBookItem = new JMenuItem("Parallel AlphaBeta (Book)");
		whiteAlphaBetaMenu.add(whiteParallelAlphaBetaBookItem);
		whiteParallelAlphaBetaBookItem.setMnemonic(KeyEvent.VK_O);
		whiteParallelAlphaBetaBookItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				m_game.setWhiteMoveStrategy(
						new ParallelAlphaBetaBookStrategy(m_game.getAIDepth(), getEngineOutputArea()));
				updateAILabel(m_game);
			}
		});

		final JMenu blackMenu = new JMenu("Black");
		blackMenu.setMnemonic(KeyEvent.VK_B);
		aiMenu.add(blackMenu);

		JMenuItem blackHumanItem = new JMenuItem("Human");
		blackMenu.add(blackHumanItem);
		blackHumanItem.setMnemonic(KeyEvent.VK_H);
		blackHumanItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				m_game.setBlackMoveStrategy(null);
				updateAILabel(m_game);
			}
		});

		JMenu blackMiniMaxMenu = new JMenu("MiniMax");
		blackMenu.add(blackMiniMaxMenu);
		blackMiniMaxMenu.setMnemonic(KeyEvent.VK_M);

		JMenuItem blackMiniMaxItem = new JMenuItem("MiniMax");
		blackMiniMaxMenu.add(blackMiniMaxItem);
		blackMiniMaxMenu.setMnemonic(KeyEvent.VK_M);
		blackMiniMaxItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				m_game.setBlackMoveStrategy(new MiniMaxStrategy(m_game.getAIDepth(), getEngineOutputArea()));
				updateAILabel(m_game);
			}
		});

		JMenuItem blackParallelMiniMaxItem = new JMenuItem("Parallel MiniMax");
		blackMiniMaxMenu.add(blackParallelMiniMaxItem);
		blackParallelMiniMaxItem.setMnemonic(KeyEvent.VK_P);
		blackParallelMiniMaxItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				m_game.setBlackMoveStrategy(new ParallelMiniMaxStrategy(m_game.getAIDepth(), getEngineOutputArea()));
				updateAILabel(m_game);
			}
		});

		JMenuItem blackMiniMaxBookItem = new JMenuItem("MiniMax (Book)");
		blackMiniMaxMenu.add(blackMiniMaxBookItem);
		blackMiniMaxBookItem.setMnemonic(KeyEvent.VK_B);
		blackMiniMaxBookItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				m_game.setBlackMoveStrategy(new MiniMaxBookStrategy(m_game.getAIDepth(), getEngineOutputArea()));
				updateAILabel(m_game);
			}
		});

		JMenuItem blackParallelMiniMaxBookItem = new JMenuItem("Parallel MiniMax (Book)");
		blackMiniMaxMenu.add(blackParallelMiniMaxBookItem);
		blackParallelMiniMaxBookItem.setMnemonic(KeyEvent.VK_O);
		blackParallelMiniMaxBookItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				m_game.setBlackMoveStrategy(
						new ParallelMiniMaxBookStrategy(m_game.getAIDepth(), getEngineOutputArea()));
				updateAILabel(m_game);
			}
		});

		JMenu blackAlphaBetaMenu = new JMenu("AlphaBeta");
		blackAlphaBetaMenu.setMnemonic(KeyEvent.VK_A);
		blackMenu.add(blackAlphaBetaMenu);

		JMenuItem blackAlphaBetaItem = new JMenuItem("AlphaBeta");
		blackAlphaBetaMenu.add(blackAlphaBetaItem);
		blackAlphaBetaItem.setMnemonic(KeyEvent.VK_A);
		blackAlphaBetaItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				m_game.setBlackMoveStrategy(new AlphaBetaStrategy(m_game.getAIDepth(), getEngineOutputArea()));
				updateAILabel(m_game);
			}
		});

		JMenuItem blackParallelAlphaBetaItem = new JMenuItem("Parallel AlphaBeta");
		blackAlphaBetaMenu.add(blackParallelAlphaBetaItem);
		blackParallelAlphaBetaItem.setMnemonic(KeyEvent.VK_P);
		blackParallelAlphaBetaItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				m_game.setBlackMoveStrategy(new ParallelAlphaBetaStrategy(m_game.getAIDepth(), getEngineOutputArea()));
				updateAILabel(m_game);
			}
		});

		JMenuItem blackAlphaBetaBookItem = new JMenuItem("AlphaBeta (Book)");
		blackAlphaBetaMenu.add(blackAlphaBetaBookItem);
		blackAlphaBetaBookItem.setMnemonic(KeyEvent.VK_B);
		blackAlphaBetaBookItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				m_game.setBlackMoveStrategy(new AlphaBetaBookStrategy(m_game.getAIDepth(), getEngineOutputArea()));
				updateAILabel(m_game);
			}
		});

		JMenuItem blackParallelAlphaBetaBookItem = new JMenuItem("Parallel AlphaBeta (Book)");
		blackAlphaBetaMenu.add(blackParallelAlphaBetaBookItem);
		blackParallelAlphaBetaBookItem.setMnemonic(KeyEvent.VK_O);
		blackParallelAlphaBetaBookItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				m_game.setBlackMoveStrategy(
						new ParallelAlphaBetaBookStrategy(m_game.getAIDepth(), getEngineOutputArea()));
				updateAILabel(m_game);
			}
		});

		return aiMenu;
	}

	private JMenu createCosmeticsMenu()
	{
		final JMenu cosmeticsMenu = new JMenu("Cosmetics");
		cosmeticsMenu.setMnemonic(KeyEvent.VK_C);

		JMenuItem choosePiecesMenuItem = new JMenuItem("Piece Style");
		cosmeticsMenu.add(choosePiecesMenuItem);
		choosePiecesMenuItem.setMnemonic(KeyEvent.VK_P);
		choosePiecesMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				final JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File("./res/pieces"));
				if (fc.showOpenDialog(m_frame) == JFileChooser.APPROVE_OPTION)
				{
					File file = fc.getSelectedFile();
					m_boardCanvas.loadSpriteSheet(file.getAbsolutePath());
				}
			}
		});

		JMenu boardSubMenu = new JMenu("Board Colours");
		boardSubMenu.setMnemonic(KeyEvent.VK_B);
		cosmeticsMenu.add(boardSubMenu);

		JMenuItem darkSquaresMenuItem = new JMenuItem("Dark Squares");
		boardSubMenu.add(darkSquaresMenuItem);
		darkSquaresMenuItem.setMnemonic(KeyEvent.VK_D);
		darkSquaresMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Color c = JColorChooser.showDialog(m_frame, "Choose a colour", new Color(0x64, 0x7a, 0x8e, 0xff));
				if (c != null)
				{
					m_boardCanvas.setDarkColour(c);
				}
			}
		});

		JMenuItem lightSquaresMenuItem = new JMenuItem("Light Squares");
		boardSubMenu.add(lightSquaresMenuItem);
		lightSquaresMenuItem.setMnemonic(KeyEvent.VK_L);
		lightSquaresMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Color c = JColorChooser.showDialog(m_frame, "Choose a colour", new Color(0x94, 0xaa, 0xc0, 0xff));
				if (c != null)
				{
					m_boardCanvas.setLightColour(c);
				}
			}
		});

		return cosmeticsMenu;
	}

	private JMenu createFileMenu()
	{
		final JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);

		final JMenuItem loadPGN = new JMenuItem("Import PGN");
		loadPGN.setMnemonic(KeyEvent.VK_L);
		loadPGN.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				c_importPGN();
			}
		});
		fileMenu.add(loadPGN);

		final JMenuItem savePGN = new JMenuItem("Export PGN");
		savePGN.setMnemonic(KeyEvent.VK_E);
		savePGN.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				c_exportPGN();
			}
		});
		fileMenu.add(savePGN);

		return fileMenu;
	}

	private JMenu createBoardMenu()
	{
		final JMenu boardMenu = new JMenu("Board");
		boardMenu.setMnemonic(KeyEvent.VK_B);

		final JMenuItem resetBoard = new JMenuItem("Reset Board");
		resetBoard.setMnemonic(KeyEvent.VK_R);
		resetBoard.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				c_resetBoard();
			}
		});
		boardMenu.add(resetBoard);

		final JMenuItem undoMove = new JMenuItem("Undo Move (Ctrl+Z)");
		undoMove.setMnemonic(KeyEvent.VK_U);
		undoMove.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				c_undoMove();
			}
		});
		boardMenu.add(undoMove);

		final JMenuItem drawFlipped = new JMenuItem("Flip View");
		drawFlipped.setMnemonic(KeyEvent.VK_F);
		drawFlipped.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				m_game.toggleDrawFlipped();
			}
		});
		boardMenu.add(drawFlipped);

		return boardMenu;
	}

	public int getWidth()
	{
		return m_boardCanvas.getWidth();
	}

	public int getHeight()
	{
		return m_boardCanvas.getHeight();
	}

	public void tick()
	{
		m_draggedItemPanel.tick();
	}

	public void draw()
	{
		m_boardCanvas.repaint();
	}

	public final boolean isOpen()
	{
		return m_frame.isVisible();
	}

	public void c_undoMove()
	{
		m_game.popBoard();
		m_guiPanel.popHistoryNotation(m_game.getBoard());
	}

	public void c_resetBoard()
	{
		m_game.reset();
		m_guiPanel.clearHistory();
	}

	public void c_importPGN()
	{
		JOptionPane.showMessageDialog(null, "TODO: Importing PGN files", "ChessBot", JOptionPane.INFORMATION_MESSAGE);
	}

	public void c_exportPGN()
	{
		JOptionPane.showMessageDialog(null, "TODO: Exporting PGN files", "ChessBot", JOptionPane.INFORMATION_MESSAGE);
	}

	private void c_windowResize()
	{
		int width = m_frame.getContentPane().getWidth();
		int height = m_frame.getContentPane().getHeight();

		m_draggedItemPanel.c_windowResize(width, height);
		m_boardCanvas.c_windowResize(width, height);
		m_guiPanel.c_windowResize(width, height);

		m_frame.validate();
	}

	public Game getGame()
	{
		return m_game;
	}

	public String getPGN()
	{
		return m_guiPanel.getPGN();
	}

	private void updateAILabel(final Game game)
	{
		m_guiPanel.updateAILabel(game);
	}

	public JTextArea getEngineOutputArea()
	{
		return m_guiPanel.getEngineOutputArea();
	}

}
