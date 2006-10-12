package Composestar.Core.REXREF;

import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.COPPER.Splitter;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.CORfilterElementCompOper;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElementAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModuleAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPartAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPattern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPatternAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelectorAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Method;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.NameMatchingType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ParameterizedInternal;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ParameterizedMessageSelector;
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
import Composestar.Utils.Debug;

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
    
    // change ??method to {meth1, method2 ...}
    transformParameterList();
    
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
    
    resolveFilterModuleParameters();
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
    	  ref.getClass();
    	  ref.getClass();
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
  
  private void resolveFilterModuleParameters(){
	  Iterator i = ds.getAllInstancesOf(ParameterizedMessageSelector.class);
	  while(i.hasNext()){
		  ParameterizedMessageSelector pms = (ParameterizedMessageSelector) i.next();
		  if(!pms.isList())
		  {
			  try
			  {
				  FilterModule mp = (FilterModule)((Filter)((FilterElement)((MatchingPattern) ((MatchingPart) pms.getParent()).getParent()).getParent()).getParent()).getParent();
				  String paraValue = (String) ((Vector) mp.getParameter(pms.getName()).getValue()).get(0);
				  pms.setName(paraValue);
			  }
			  catch(Exception e)
			  {
				  Debug.out(Debug.MODE_ERROR, "REXREF", "Getting the value for a message selector parameter failed, probably the parent chain is broken.");
			  }
		  }
	  }
  }
  
  /*
   * Transforms the parameterlists of the message selector to a nested message set
   */
  private void transformParameterList()throws ModuleException{
	  Iterator i = ds.getAllInstancesOf(ParameterizedMessageSelector.class);
	  //the FilterModuleAST where we want to perform the transformation 
	  FilterModule fm;
	  Filter f;
	  
	  while(i.hasNext()){
		  ParameterizedMessageSelector pms = (ParameterizedMessageSelector) i.next();
		  if(pms.isList()){
			  // we only allow this transformation on name matching, <inner.?? methods> is qua semantics nonsens
			  if(!(((MatchingPart) pms.getParent()).getMatchType() instanceof NameMatchingType)){
				  throw new ModuleException("Filter Module Parameter List" + pms.getName() + "' cannot be transformed (<inner.??methods> is not allowed).", "REXREF");
			  }
			  /* on this part we change fromt he orignal idea, which was to convert the list to a nested
			   * matching, however the nesting is buggy and temporary it will be trnaformed to a full filter element.
			   */
			  MatchingPattern mp = (MatchingPattern) ((MatchingPart) pms.getParent()).getParent();
			  FilterElement fe = (FilterElement) mp.getParent();
			  
			  // get the filtermodule to get the list of methods.
			  f = (Filter) fe.getParent();
			  fm =  (FilterModule) f.getParent();			  
			  Vector selectors = (Vector)fm.getParameter(pms.getName()).getValue();
			  
			  /* we got all we want, the filter, the filterelement
			   * so the first one in the list gets the orignal FE
			   */
			  Iterator sel = selectors.iterator();
			  String s = (String) sel.next();
			  pms.setName(s);
			  
			  // the second one out of the vector and the rest
			  while(sel.hasNext()){
				  FilterElement copy = this.copyFilterElement(fe);
				  
				  //placing and and afetr the first one
				  CORfilterElementCompOper cfeco = new CORfilterElementCompOper();
				  cfeco.setDescriptionFileName(fe.getDescriptionFileName());
				  cfeco.setDescriptionLineNumber(fe.getDescriptionLineNumber());
			      cfeco.setParent(fe.getFilterElementAST());
			      fe.setRightOperator(cfeco);
			      ds.addObject(cfeco);
			      
			      f.addFilterElement(copy);
			      ds.addObject(copy);
			      
			      // setting the selector
			      Iterator mpi  = copy.getMatchingPatternIterator();
			      while(mpi.hasNext())
			      {
			    	  MatchingPattern mp2 = (MatchingPattern) mpi.next();
			    	  Iterator match = mp2.getMatchingPartsIterator();
			    	  while(match.hasNext())
			    	  {
			    		  MatchingPart matchpart = (MatchingPart) match.next();
			    		  matchpart.getSelector().setName((String)sel.next());
			    	  }
			      }
			      
			      // this for gettign , , between them
			      fe = copy;
			  }
			  
			  int fd = 0;
			  fd++;
		  }
	  }
  }
  
  /*
   * Copying a filter element out of the FilterElementAST of the orginal FE
   */
  private FilterElement copyFilterElement(FilterElement original)
  {
	  //temporary check by cloning the AST strcutruee
	  FilterElementAST oldAST = original.getFilterElementAST();
	  FilterElementAST newAST = new FilterElementAST();
	  newAST.setConditionPart(oldAST.getConditionPart());
	  newAST.setDescriptionFileName(oldAST.getDescriptionFileName());
	  newAST.setDescriptionLineNumber(oldAST.getDescriptionLineNumber());
	  newAST.setEnableOperatorType(oldAST.getEnableOperatorType());
	  newAST.setParent(oldAST.getParent());
	  newAST.setRightOperator(oldAST.getRightOperator());
	  
	  Vector mps = new Vector();
	  Iterator mpi = oldAST.getMatchingPatterns().iterator();
	  while(mpi.hasNext()){
		  MatchingPatternAST newmpa = new MatchingPatternAST();
		  MatchingPatternAST oldmpa = (MatchingPatternAST) mpi.next();
		  
		  newmpa.setDescriptionFileName(oldmpa.getDescriptionFileName());
		  newmpa.setDescriptionLineNumber(oldmpa.getDescriptionLineNumber());
		  newmpa.setParent(oldmpa.getParent());
		  newmpa.setSubstitutionParts(oldmpa.getSubstitutionParts());
		  
		  Vector ms = new Vector();
		  Iterator mi = oldmpa.getMatchingPartsIterator();
		  while(mi.hasNext()){
			  MatchingPartAST newm = new MatchingPartAST();
			  MatchingPartAST oldm = (MatchingPartAST) mi.next();
			  
			  newm.setDescriptionFileName(oldm.getDescriptionFileName());
			  newm.setDescriptionLineNumber(oldm.getDescriptionLineNumber());
			  newm.setMatchType(oldm.getMatchType());
			  newm.setParent(newm.getParent());
			  newm.setTarget(oldm.getTarget());
			  
			  MessageSelectorAST newsel = new MessageSelectorAST();
			  newsel.setDescriptionFileName(oldm.getSelector().getDescriptionFileName());
			  newsel.setDescriptionLineNumber(oldm.getSelector().getDescriptionLineNumber());
			  newsel.setName("zoink");
			  newsel.setParent(newm);
			  ds.addObject(newsel);
			  
			  newm.setSelector(newsel);
			  ms.add(newm);
			  ds.addObject(newm);
		  }
		  
		  newmpa.setMatchingParts(ms);
		  mps.add(newmpa);
		  ds.addObject(newmpa);
	  }	  
	  
	  newAST.setMatchingPatterns(mps);
	  ds.addObject(newAST);
	  //end test
	  
	  FilterElement copy = new FilterElement(newAST);
	  copy.setConditionPart(original.getConditionPart());
	  copy.setDescriptionFileName(original.getDescriptionFileName());
	  copy.setDescriptionLineNumber(original.getDescriptionLineNumber());
	  copy.setEnableOperatorType(copy.getEnableOperatorType());
	  copy.setParent(copy.getParent());
	  copy.setRightOperator(copy.getRightOperator());
	  return copy;
  }
}
