package Composestar.Java.LOLA;

import Composestar.Core.LOLA.LOLA;
import Composestar.Java.LOLA.metamodel.JavaLanguageModel;

public class JavaLOLA extends LOLA
{
	/**
	 * Default constructor; uses the Java language model
	 */
	public JavaLOLA()
	{
		super(JavaLanguageModel.class);
	}
}
