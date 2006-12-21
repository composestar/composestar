package Composestar.C.wrapper;

import Composestar.C.wrapper.parsing.TNode;

public class HeaderIntroductionPoint extends WeaveblePoint
{
	private TNode node = null;

	public HeaderIntroductionPoint(String file)
	{
		super(file);
	}

	public void setNode(TNode node)
	{
		this.node = node;
	}

	public TNode getNode()
	{
		return node;
	}
}
