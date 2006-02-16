package Composestar.Core.CHKREP;

import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.*;
import Composestar.Core.Exception.ModuleException;

import java.util.*;

public class ExistSelector implements BaseChecker {
	private DataStore ds;
	
	
	public boolean performCheck() {
		Iterator fmbi = ds.getAllInstancesOf(FilterModuleBinding.class);
		boolean nonFatal = true;
		
		while(fmbi.hasNext()){
			FilterModuleBinding fmb = (FilterModuleBinding) fmbi.next();
			String selector = fmb.getSelector().getName();
			boolean exist = false;
			
			SuperImposition si = (SuperImposition) fmb.getParent();
			Iterator sels = si.getSelectorIterator();
			while(sels.hasNext()){
				SelectorDefinition sd = (SelectorDefinition) sels.next();
				if(sd.getName().equals(selector)){
					exist = true;
				}				
			}
			
			if(!exist){
				Debug.out(Debug.MODE_ERROR, "CHKREP", "Selector " + selector + " is used but not declared", fmb.getSelector().getDescriptionFileName(), fmb.getSelector().getDescriptionLineNumber());
				nonFatal = false;
			}
		}
		return nonFatal;
	}

	public void check(DataStore newDs) throws ModuleException {
		ds = newDs;
		boolean nonFatal = performCheck();
		
		if(!nonFatal) {
      throw (new ModuleException("One or more Selectors in filtermodules are not declared", "CHKREP") );
    }
	}

}
