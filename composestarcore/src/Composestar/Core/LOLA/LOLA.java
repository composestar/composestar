/*
 * LOgic predicate LAnguage Facade/API 
 * 
 * Controls the prolog query engine and the language meta model
 * 
 * Created on Oct 26, 2004 by havingaw
 */
package Composestar.Core.LOLA;


import Composestar.Core.LOLA.metamodel.UnitDictionary;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import java.util.ArrayList;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef.PredicateSelector;
import Composestar.Core.Exception.ModuleException;

public abstract class LOLA implements CTCommonModule
{
	public boolean initialized; // Initialize only once
	public DataStore dataStore;
	public UnitDictionary unitDict;
	public static ArrayList selectors;
  
	public abstract String initLanguageModel() throws ModuleException;
	
	public abstract void initPrologEngine(CommonResources resources, String generatedPredicatesFilename) throws ModuleException;
	
	public abstract void createUnitIndex() throws ModuleException;
	
	public abstract ArrayList splitSelectors(ArrayList selectors) throws ModuleException ;

	public abstract void moveSelectors(ArrayList list,ArrayList from, ArrayList to);
  
	public abstract void moveSelector(PredicateSelector predSel,ArrayList from, ArrayList to);
}
