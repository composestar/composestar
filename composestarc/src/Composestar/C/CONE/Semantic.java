package Composestar.C.CONE;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.*;
import Composestar.Core.RepositoryImplementation.DataStore;

public abstract class Semantic {

	private String filtertype;
	
	public abstract String retrieveSemantics();
	
	public abstract String getType();
	
}
