package Composestar.DotNET2.LOLA;

import Composestar.Core.LOLA.LOLA;
import Composestar.DotNET2.LOLA.metamodel.DotNETLanguageModel;

public class DotNETLOLA extends LOLA
{
	/**
	 * Default constructor; uses the .NET language model
	 */
	public DotNETLOLA()
	{
		super(DotNETLanguageModel.class);
	}
}
