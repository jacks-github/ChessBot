package chess.engine.ai.book;

import java.util.LinkedList;
import java.util.List;

class MoveNode
{
	public String data;
	public List<MoveNode> children;
	public MoveNode parent;

	public MoveNode(final String data)
	{
		this.data = data;
		this.children = new LinkedList<MoveNode>();
	}

	public void addChild(final MoveNode child)
	{
		children.add(child);
		child.parent = this;
	}

}
