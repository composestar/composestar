package Composestar.Java.LOLA;

import java.util.ArrayList;

import Composestar.Core.LOLA.LOLA;
import Composestar.Core.LOLA.metamodel.UnitDictionary;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Java.LOLA.metamodel.JavaLanguageModel;

public class JavaLOLA extends LOLA
{
	/**
	 * Default constructor; uses the Java language model
	 */
	public JavaLOLA()
	{
		this(JavaLanguageModel.instance());
	}

	/**
	 * Constructor
	 * 
	 * @param model the language model to be used by this instance of the logic
	 *            language
	 */
	public JavaLOLA(JavaLanguageModel model)
	{
		this.initialized = false;
		this.langModel = model;
		this.dataStore = DataStore.instance();
		this.unitDict = new UnitDictionary(model);
		selectors = new ArrayList();
	}
}
