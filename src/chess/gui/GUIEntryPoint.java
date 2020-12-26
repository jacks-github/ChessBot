package chess.gui;

public class GUIEntryPoint
{
	private static final int TARGET_FPS = 60;

	public static void main(String[] args)
	{
		Game game = new Game();
		Window window = new Window(game);

		double delta = 0;
		long lastTime = System.nanoTime();
		double nsPerFrame = 1000000000.0 / TARGET_FPS;

		while (window.isOpen())
		{
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerFrame;
			lastTime = now;
			while (delta >= 1)
			{
				delta--;
				if (delta <= 1)
				{
					game.tick(window.getWidth(), window.getHeight());
					window.tick();
					window.draw();
				}
			}
		}

	}

}
