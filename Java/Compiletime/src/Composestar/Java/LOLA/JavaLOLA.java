package Composestar.Java.LOLA;

import java.util.ArrayList;

import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef.PredicateSelector;
import Composestar.Core.LOLA.LOLA;
import Composestar.Core.LOLA.metamodel.UnitDictionary;
import Composestar.Java.LOLA.metamodel.JavaLanguageModel;

public class JavaLOLA extends LOLA
{
	/**
	 * Default constructor; uses the Java language model
	 */
	public JavaLOLA()
	{
		this(new JavaLanguageModel());
	}

	/**
	 * Constructor
	 * 
	 * @param model the language model to be used by this instance of the logic
	 *            language
	 */
	public JavaLOLA(JavaLanguageModel model)
	{
		initialized = false;
		langModel = model;
		unitDict = new UnitDictionary(model);
		selectors = new ArrayList<PredicateSelector>();
	}
}
