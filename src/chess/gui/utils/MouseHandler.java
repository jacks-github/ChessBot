package chess.gui.utils;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseHandler implements MouseListener, MouseMotionListener
{

	private static final MouseHandler m_staticInstance = new MouseHandler();

	public static MouseHandler get()
	{
		return m_staticInstance;
	}

	private MouseHandler()
	{
	}

	private static byte[] buttons = new byte[10];
	private static int[] startX = new int[10];
	private static int[] startY = new int[10];
	private static int x, y;

	public static int getX()
	{
		return x;
	}

	public static int getY()
	{
		return y;
	}

	public static Point getPosition()
	{
		return new Point(x, y);
	}

	public static int getStartX(int button)
	{
		return startX[button];
	}

	public static int getStartY(int button)
	{
		return startY[button];
	}

	public static Point getStartPosition(int button)
	{
		return new Point(startX[button], startY[button]);
	}

	public static boolean buttonDown(int button)
	{
		return buttons[button] > 0;
	}

	public static boolean buttonClicked(int button)
	{
		return buttons[button] == 2;
	}

	public static void tick()
	{
		for (int i = 0; i < buttons.length; i++)
		{
			if (buttons[i] > 0 && buttons[i] < 5)
			{
				buttons[i]++;
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		x = e.getX();
		y = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		x = e.getX();
		y = e.getY();
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{

	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (buttons[e.getButton()] == 0)
		{
			buttons[e.getButton()] = 1;

			startX[e.getButton()] = e.getX();
			startY[e.getButton()] = e.getY();
		}
	}

	public static int numButtonsDown()
	{
		int numButtonsDown = 0;
		for (var button : buttons)
		{
			if (button > 0)
				numButtonsDown++;
		}
		return numButtonsDown;
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		buttons[e.getButton()] = 0;
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{

	}

	@Override
	public void mouseExited(MouseEvent e)
	{

	}

}
