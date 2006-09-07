package Composestar.Core.REXREF;

import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.COPPER.Splitter;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModuleAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModuleParameter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Method;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ParameterizedInternal;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConditionReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.DeclaredObjectReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.LabeledConcernReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.MethodReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.SelectorReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SelectorDefinition;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.RepositoryImplementation.DataStore;

/**
 * Tries to resolves all references in the repository
 */
public class DoResolve {
  private DataStore ds;    //the datastore containing the objects
  private boolean ignoreCase = true;   //for stupid typing errors in concernspecifications


  /**
   * Constructor
   */
  public DoResolve() {
  }


  /**
   * Does the actual resolving
 * @throws ModuleException 
   */
  public void go(DataStore d) throws ModuleException {
    if (d == null) {
      //System.out.println("invalid repository");
      return;
    }
    ds = d;
    
    //  filtermodulreferences
    resolveFilterModuleReferences();
    
    //concernreferences
    resolveConcernReferences();
    resolveLabeledConcernReferences();

     //    resolveCompiledConcernReprRefs();
    resolveSelectorReferences();

    //filtermodulelementreferences
    resolveMethodReferences();
    resolveFilterReferences();
    resolveConditionReferences();
    resolveDeclaredObjectReferences();
  }


  /**
   * Tries to resolve all ConcernReferences
 * @throws ModuleException 
   */
  private void resolveConcernReferences() throws ModuleException {
    // iterate over all instances of ConcernReference
    for (Iterator it = ds.getAllInstancesOf(ConcernReference.class); it.hasNext();) {
      ConcernReference ref = (ConcernReference) it.next();

      // fetch the Concern with the same name as the reference references to
      //System.out.println("Concernrefs: "+ref.getQualifiedName()+" == "+ref.getName());
      Concern concern = (Concern)ds.getObjectByID(ref.getQualifiedName());
      if (concern != null) {
        ref.setRef(concern);
        ref.setResolved(true);
      } else {
        throw new ModuleException("ConcernReference '" + ref.getQualifiedName() + "' cannot be resolved (are you referencing a non-existent concern or is the startup object incorrect?)", "REXREF", ref);
      }
    }
  }


  /**
   * Tries to resolve all LabeledConcernReferences
 * @throws ModuleException 
   */
  private void resolveLabeledConcernReferences() throws ModuleException {
    for (Iterator it = ds.getAllInstancesOf(LabeledConcernReference.class); it.hasNext();) {
      Object obj = it.next();
      LabeledConcernReference ref = (LabeledConcernReference) obj;

      Concern reffedConcern = (Concern) ds.getObjectByID(ref.getQualifiedName());

      if (reffedConcern != null) {
        ref.setRef(reffedConcern);
        ref.setResolved(true);
      } else {
          throw new ModuleException("LabeledConcernReference '" + ref.getName() + "' cannot be resolved (are you referencing a non-existent concern or is the startup object incorrect?)", "REXREF", ref);
      }
    }
  }


  /**
   * Tries to resolve all FilterModuleReferences
 * @throws ModuleException 
   */
  private void resolveFilterModuleReferences() throws ModuleException {
    /* The names of DeclaredRepositoryEntities do need to be unique, so the
     * FM_AST does keeps its orignal name, but all FM (Inst) do need a unique identifier
     * Instead of using a nice Singlton construct, I just use int counters, more because
     * this is the only method that actually creates all the instantances of FilterModules, Internals, etc.
     */
	int fmCounter = 1;
	  
	//for (Iterator it = ds.getAllInstancesOf(FilterModuleReference.class); it.hasNext();) {
    Iterator it = ds.getAllInstancesOf(FilterModuleReference.class);
    while(it.hasNext()){
      FilterModuleReference ref = (FilterModuleReference) it.next();
      FilterModuleAST fm_ast = (FilterModuleAST) ds.getObjectByID(ref.getQualifiedName());
      if (fm_ast != null) {
    	FilterModule fm = new FilterModule(fm_ast, ref.getArgs(),fmCounter);
    	fmCounter++;
        CpsConcern concern = (CpsConcern) fm.getParent();
        concern.addFilterModule(fm);
        ds.addObject(fm);
        
        //add all the newly created internal instances to the repository
        Iterator iter = fm.getInternalIterator();
        while(iter.hasNext()){
        	Internal o = (Internal) iter.next();
        	
        	if(o instanceof ParameterizedInternal){
        		ParameterizedInternal pi = (ParameterizedInternal) o; 
        		Vector pack = (Vector)fm.getParameter(pi.getParameter()).getValue();
        		ConcernReference cref = new ConcernReference();
        		ds.removeObject(cref);
        		Splitter split = new Splitter(); 
        		split.splitConcernReference(pack);
        	    cref.setPackage(split.getPack());
        	    cref.setName(split.getConcern());
        	    pi.setType(cref);
        	    ds.addObject(cref);
        		ds.addObject(pi.getType());
        	}
        }
        //adding the new parameters instances to the repository 
        Iterator iterParams = fm.getParameterIterator();
        while(iterParams.hasNext()){
        	Object o = iterParams.next();
        	ds.addObject(o);
        }
        ref.setRef(fm);
        ref.setResolved(true);
      } else {
        throw new ModuleException("FilterModuleReference '" + ref.getName() + "' cannot be resolved (are you referencing a non-existent filtermodule?)", "REXREF", ref);
      }
    }
  }


  //removed: class renamed to PlatformRepresentation

  //  private void resolveCompiledConcernReprRefs() {
  //    CompiledConcernRepRef ref;
  //    Concern temp;
  //    CompiledConcernRepr temp2;
  //    Iterator it, it2;
  //    Object obj, obj2;
  //    boolean found = false;
  //
  //    for(it = ds.getIterator(); it.hasNext(); ) {
  //      obj = it.next();
  //      if(obj instanceof CompiledConcernRepRef) { //ok, try to resolve
  //        ref = (CompiledConcernRepRef) obj;
  //
  //        //first try to find the concern
  //        for(it2 = ds.getIterator(); it2.hasNext(); ) {
  //          obj2 = it2.next();
  //          if(obj2 instanceof Concern) {
  //            temp = (Concern) obj2;
  //            if(temp.getName().compareTo(ref.getConcern()) == 0) {
  //              //ok, found, now see if it contains the compiledconcernrepr
  //              temp2 = temp.getPlatformRepresentation();
  //              if (temp2 == ref.getName()) {    //fixme
  //                ref.setRef(temp2);
  //                ref.resolved = true;
  //                if (Main.debug) System.out.println("resolveCompiledConcernReprRefs: resolved " + ref.getName());
  //                found = true;
  //              }
  //            }
  //          }
  //        }
  //        if(found == false) System.out.println("resolveCompiledConcernReprRefs: " + ref.getName() + " not resolved");
  //      }
  //    }
  //  }


  /**
   * Tries to resolve all SelectorReferences
 * @throws ModuleException 
   */
  private void resolveSelectorReferences() throws ModuleException {
    SelectorReference ref;
    CpsConcern temp;
    SelectorDefinition temp2;
    Iterator it, it2, it3;
    Object obj, obj2;
    boolean found;

    for (it = ds.getIterator(); it.hasNext();) {
      obj = it.next();
      if (obj instanceof SelectorReference) { //ok, try to resolve
        ref = (SelectorReference) obj;
        found = false;

        //first try to find the concern
        for (it2 = ds.getIterator(); it2.hasNext() && !found;) {
          obj2 = it2.next();
          if (obj2 instanceof CpsConcern) {
            temp = (CpsConcern) obj2;
            if (temp.getName().compareTo(ref.getConcern()) == 0) {

              //ok, found, now try to find the selector
              for (it3 = temp.getSuperImposition().getSelectorIterator(); it3.hasNext() && !found;) {
                temp2 = (SelectorDefinition) it3.next();
                if ((ignoreCase) && (temp2.getName().compareToIgnoreCase(ref.getName()) == 0)) {
                  found = true;
                } else if ((!ignoreCase) && (temp2.getName().compareTo(ref.getName()) == 0)) found = true;
                if (found) {
                  ref.setRef(temp2);
                  ref.setResolved(true);
                }
              }
            }
          }
        }
        if (!ref.getResolved()) {
          throw new ModuleException("SelectorReference '" + ref.getName() + "' cannot be resolved (are you referencing a non-existent selector?)", "REXREF", ref);
        }
      }
    }
  }


  /**
   * Tries to resolve all MethodReferences
   */
  private void resolveMethodReferences() {
    MethodReference ref;
    CpsConcern temp;
    FilterModule temp2;
    Method temp3;
    Iterator it, it2, it3, it4;
    Object obj, obj2;
    boolean found;

    for (it = ds.getIterator(); it.hasNext();) {
      obj = it.next();
      if (obj instanceof MethodReference) { //ok, try to resolve
        ref = (MethodReference) obj;
        found = false;

        //first try to find the concern
        for (it2 = ds.getIterator(); it2.hasNext() && !found;) {
          obj2 = it2.next();
          if (obj2 instanceof CpsConcern) {
            temp = (CpsConcern) obj2;
            if (temp.getName().compareTo(ref.getConcern()) == 0) {

              //ok, found, now try to find the filtermodule
              for (it3 = temp.getFilterModuleIterator(); it3.hasNext() && !found;) {
                temp2 = (FilterModule) it3.next();
                if (temp2.getName().compareTo(ref.getFilterModule()) == 0) {

                  //ok, found, now try to find the method
                  for (it4 = temp2.getMethodIterator(); it4.hasNext() && !found;) {
                    temp3 = (Method) it4.next();
                    if ((ignoreCase) && (temp3.getName().compareToIgnoreCase(ref.getName()) == 0)) {
                      //ok, name matches, now check parameters (types only)
                      //if (Main.debug) System.out.println("resolveMethodReferences: " + ref.getName() + "found, checking parameters");
                      if (compareParameters(temp3.getParameterIterator(), ref.getParameterIterator())) {
                        ref.setRef(temp3);
                        ref.setResolved(true);
                        //if (Main.debug) System.out.println("resolveMethodReferences: resolved " + ref.getName());
                      }
                    } else if ((!ignoreCase) && (temp3.getName().compareTo(ref.getName()) == 0)) found = true;
                    //ok, name matches, now check parameters (types only)
                    //if (Main.debug) System.out.println("resolveMethodReferences: " + ref.getName() + "found, checking parameters");
                    if (compareParameters(temp3.getParameterIterator(), ref.getParameterIterator())) {
                      ref.setRef(temp3);
                      ref.setResolved(true);
                      //if (Main.debug) System.out.println("resolveMethodReferences: resolved " + ref.getName());
                    }
                  }
                }
              }
            }
          }
        }
        //if(found == false) System.out.println("resolveMethodReferences: " + ref.getName() + " not resolved");
      }
    }
  }


  /**
   * Compares the sigantures methods of two methods to see if they match
   *
   * @param it  Iterator to the first collection of parameters
   * @param it2 Iterator to the second collection of parameters
   */
  private boolean compareParameters(Iterator it, Iterator it2) {
    int i = 0, j = 0;
    Iterator it3, it4; //backups, used for counting
    LabeledConcernReference l;
    ConcernReference c;

    it3 = it;
    it4 = it2;
    while (it3.hasNext()) {
      it3.next();
      i++;
    }
    while (it4.hasNext()) {
      it4.next();
      j++;
    }
    if (i != j) return (false); //not the same number of parameters

    //start comparing
    for (; it.hasNext();) {
      l = (LabeledConcernReference) it.next();
      c = (ConcernReference) it2.next();

      if (!l.getName().equals(c.getName())) return (false);
    }

    //ok, passed all the parameters
    return (true);
  }


  /**
   * Tries to resolve all FilterReferences
 * @throws ModuleException 
   */
  private void resolveFilterReferences() throws ModuleException {
    FilterReference ref;
    CpsConcern temp;
    FilterModule temp2;
    Filter temp3;
    Iterator it, it2, it3, it4, it5;
    Object obj, obj2;
    boolean found;

    for (it = ds.getIterator(); it.hasNext();) {
      obj = it.next();
      if (obj instanceof FilterReference) { //ok, try to resolve
        ref = (FilterReference) obj;
        found = false;

        //first try to find the concern
        for (it2 = ds.getIterator(); it2.hasNext();) {
          obj2 = it2.next();
          if (obj2 instanceof CpsConcern) {
            temp = (CpsConcern) obj2;
            if (temp.getName().compareTo(ref.getConcern()) == 0) {

              //ok, found, now try to find the filtermodule
              for (it3 = temp.getFilterModuleIterator(); it3.hasNext() && !found;) {
                temp2 = (FilterModule) it3.next();
                if (temp2.getName().compareTo(ref.getFilterModule()) == 0) {

                  //ok, found, now try to find the filter
                  //first try inputfilters
                  for (it4 = temp2.getInputFilterIterator(); it4.hasNext() && !found;) {
                    temp3 = (Filter) it4.next();
                    if ((ignoreCase) && (temp3.getName().compareToIgnoreCase(ref.getName()) == 0)) {
                      found = true;
                    } else if ((!ignoreCase) && (temp3.getName().compareTo(ref.getName()) == 0)) found = true;
                    if (found) {
                      ref.setRef(temp3);
                      ref.setResolved(true);
                      //if (Main.debug) System.out.println("resolveFilterReferences: resolved " + ref.getName());
                    }
                  }
                  //now try the outputfilters
                  for (it5 = temp2.getOutputFilterIterator(); it5.hasNext() && !found;) {
                    temp3 = (Filter) it5.next();
                    if ((ignoreCase) && (temp3.getName().compareToIgnoreCase(ref.getName()) == 0)) {
                      found = true;
                    } else if ((!ignoreCase) && (temp3.getName().compareTo(ref.getName()) == 0)) found = true;
                    if (found) {
                      ref.setRef(temp3);
                      ref.setResolved(true);
                      //if (Main.debug) System.out.println("resolveFilterReferences: resolved " + ref.getName());
                    }
                  }
                }
              }
            }
          }
        }
        if (!ref.getResolved()) {
            throw new ModuleException("FilterReference '" + ref.getName() + "' cannot be resolved (are you referencing a non-existent filter?)", "REXREF", ref);
        }
      }
    }
  }


  /**
   * Tries to resolve all ConditionReferences
   */
  private void resolveConditionReferences() {
    ConditionReference ref;
    CpsConcern concern;
    FilterModuleAST filtermod;
    Condition condition;
    Iterator conditionrefiterator, concerniterator, fmiterator, conditerator;
    boolean found;

    for (conditionrefiterator = ds.getAllInstancesOf(ConditionReference.class); conditionrefiterator.hasNext();) 
    {
      ref = (ConditionReference)conditionrefiterator.next();
      found = false;
      for (concerniterator = ds.getAllInstancesOf(CpsConcern.class); concerniterator.hasNext() && !found;)
      {
        concern = (CpsConcern) concerniterator.next();
        if (concern.getName().compareTo(ref.getConcern()) == 0)
        {
        	for (fmiterator = concern.getFilterModuleASTIterator(); fmiterator.hasNext() && !found;)
        	{
	            filtermod = (FilterModuleAST) fmiterator.next();
	            if (filtermod.getName().compareTo(ref.getFilterModule()) == 0)
	            {
		              for (conditerator = filtermod.getConditionIterator(); conditerator.hasNext() && !found;)
		              {
		              		condition = (Condition) conditerator.next();
		              		if ((ignoreCase) && (condition.getName().compareToIgnoreCase(ref.getName()) == 0))
		              			found = true;
		              		else if ((!ignoreCase) && (condition.getName().compareTo(ref.getName()) == 0)) 
		              			found = true;
		              		if (found) 
		              		{
		              			ref.setRef(condition);
		              			ref.setResolved(true);
		              		}
		              }
	            }
        	}
        }
      }
    }
  }


  /**
   * Tries to resolve all DeclaredObjectReferences (i.e. internals and externals)
 * @throws ModuleException 
   */
  private void resolveDeclaredObjectReferences() throws ModuleException {
    for(Iterator dorit = ds.getAllInstancesOf(DeclaredObjectReference.class); dorit.hasNext(); )
    {
    	DeclaredObjectReference ref = (DeclaredObjectReference) dorit.next();
        boolean found = false;
        if (ref.getName().compareTo("inner") != 0)
        {
	        //first try to find the concern
        	for(Iterator cpsit = ds.getAllInstancesOf(CpsConcern.class); cpsit.hasNext();)
        	{
	          	CpsConcern cpsconcern = (CpsConcern) cpsit.next();
	          	if (cpsconcern.getName().compareTo(ref.getConcern()) == 0)
	          	{
	              //ok, found, now try to find the filtermodule
	              for (Iterator fmit = cpsconcern.getFilterModuleIterator(); fmit.hasNext() && !found;) {
	                FilterModule fm = (FilterModule) fmit.next();
	                if (fm.getFm_ast().getName().compareTo(ref.getFilterModule()) == 0)
	                {
	                  //ok, found
	                  //now see if an internal matches
	                  for (Iterator interalit = fm.getInternalIterator(); interalit.hasNext() && !found;) {
	                    Internal internal = (Internal) interalit.next();
	                    if ((ignoreCase) && (internal.getName().compareToIgnoreCase(ref.getName()) == 0)) {
	                      found = true;
	                    } else if ((!ignoreCase) && (internal.getName().compareTo(ref.getName()) == 0)){
	                    	found = true;
	                    }
	                    if (found) {
	                      ref.setRef(internal);
	                      ref.setResolved(true);
	                    }
	                  }
	                  //see if an external matches
	                  for (Iterator externalit = fm.getExternalIterator(); externalit.hasNext() && !found;) {
	                    External external = (External) externalit.next();
	                    if ((ignoreCase) && (external.getName().compareToIgnoreCase(ref.getName()) == 0)) {
	                      found = true;
	                    } else if ((!ignoreCase) && (external.getName().compareTo(ref.getName()) == 0)) found = true;
	                    if (found) {
	                      ref.setRef(external);
	                      ref.setResolved(true);
	                    }
	                  }
	                }
	              }
	            }
	          }
	        }
        if (!ref.getResolved()) {
			if (!ref.getName().equals("*") && !ref.getName().equals("inner"))
                throw new ModuleException("DeclaredObjectReference '" + ref.getName() + "' cannot be resolved (are you referencing a non-existent object?)", "REXREF", ref);
          }
      }
  }
}
