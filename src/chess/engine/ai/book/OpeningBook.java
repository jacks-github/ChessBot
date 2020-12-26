package chess.engine.ai.book;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import chess.engine.board.Board;
import chess.engine.piece.move.Move;
import chess.engine.piece.move.MoveFactory;

public class OpeningBook
{

	private static final MoveNode book = buildOpeningBook();

	public static Move pgnLookup(final Board board, final String pgn)
	{
		String[] pgnArray = pgn.split(" ");

		ArrayList<String> moveList = new ArrayList<>();
		for (final String str : pgnArray)
		{
			if (!str.endsWith("."))
			{
				moveList.add(str);
			}
		}

		return searchForNextMove(board, moveList);
	}

	private static Move searchForNextMove(final Board board, final ArrayList<String> moveList)
	{
		MoveNode currentNode = book;

		for (int depth = 0; depth < moveList.size(); depth++)
		{
			boolean moveFound = false;
			for (final MoveNode node : currentNode.children)
			{
				if (node.data.equals(moveList.get(depth)))
				{
					currentNode = node;
					moveFound = true;
					break;
				}
			}
			if (!moveFound)
			{
				break;
			}
		}

		if (moveList.size() == 0
				|| (moveList.get(moveList.size() - 1).equals(currentNode.data) && currentNode.children.size() > 0))
		{
			return MoveFactory.parsePGN(board, currentNode.children.get(0).data);
		}

		return null;
	}

	private static MoveNode buildOpeningBook()
	{
		MoveNode rootNode = new MoveNode("");

		MoveNode parent = rootNode;
		int lastIndentation = -1;
		int currentIndentation = 0;

		try
		{
			File file = new File("res/opening_book");
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine())
			{
				String line = scanner.nextLine();
				currentIndentation = countOccurences(line, ' ');

				line = line.trim();
				MoveNode node = new MoveNode(line);

				if (lastIndentation == currentIndentation - 1)
				{
					parent.addChild(node);
					parent = node;
					lastIndentation = currentIndentation;
				}
				else
				{
					while (lastIndentation + 1 > currentIndentation)
					{
						lastIndentation--;
						parent = parent.parent;
					}

					parent.addChild(node);
					parent = node;
					lastIndentation = currentIndentation;
				}

			}
			scanner.close();
		}
		catch (FileNotFoundException e)
		{
			System.err.println("ERROR: Could not find the opening book file!");
			e.printStackTrace();
		}

		return rootNode;
	}

	private static int countOccurences(final String str, final char c)
	{
		int count = 0;
		for (int i = 0; i < str.length(); i++)
		{
			if (str.charAt(i) == c)
			{
				count++;
			}
		}
		return count;
	}

}
