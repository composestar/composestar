package Composestar.C.wrapper;

import Composestar.C.wrapper.parsing.TNode;

public class GlobalIntroductionPoint extends WeaveblePoint
{
	private TNode node = null;
	
	public GlobalIntroductionPoint(String file)
	{
		super(file);
	}

	public void setNode(TNode node) {
		this.node = node;
	}

	public TNode getNode() {
		return node;
	}
}
