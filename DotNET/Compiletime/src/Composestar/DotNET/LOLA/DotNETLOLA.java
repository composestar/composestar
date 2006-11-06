package Composestar.DotNET.LOLA;

import Composestar.Core.LOLA.*;
import Composestar.Core.LOLA.metamodel.UnitDictionary;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.DotNET.LOLA.metamodel.*;

import java.util.ArrayList;

public class DotNETLOLA extends LOLA
{
	/**
	 * Default constructor; uses the .NET language model
	 */
	public DotNETLOLA()
	{
		this(DotNETLanguageModel.instance());
	}

	/**
	 * Constructor
	 * 
	 * @param model the language model to be used by this instance of the logic
	 *            language
	 */
	public DotNETLOLA(DotNETLanguageModel model)
	{
		this.initialized = false;
		this.langModel = model;
		this.dataStore = DataStore.instance();
		this.unitDict = new UnitDictionary(model);
		selectors = new ArrayList();
	}
}
