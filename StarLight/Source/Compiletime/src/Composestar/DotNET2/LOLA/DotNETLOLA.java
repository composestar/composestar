package Composestar.DotNET2.LOLA;

import java.util.ArrayList;

import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef.PredicateSelector;
import Composestar.Core.LOLA.LOLA;
import Composestar.Core.LOLA.metamodel.UnitDictionary;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.DotNET2.LOLA.metamodel.DotNETLanguageModel;

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
		initialized = false;
		langModel = model;
		dataStore = DataStore.instance();
		unitDict = new UnitDictionary(model);
		selectors = new ArrayList<PredicateSelector>();
	}
}
