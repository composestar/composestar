/*
* This file is part of Composestar project [http://composestar.sf.net].
* Copyright (C) 2003 University of Twente.
*
* Licensed under LGPL v2.1 or (at your option) any later version.
* [http://www.fsf.org/copyleft/lgpl.html]
*
* $Id$
*/
package Composestar.Core.COPPER;

import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.CompiledImplementation;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.Source;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SelectorDefinition;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SuperImposition;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef.SelClass;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;
import Composestar.Utils.StringConverter;


/**
 * Expands syntactic sugar
 */
public class SyntacticSugarExpander {
	AstManipulator am;
	
	/* deprecated
    private void AddDefault()
    {
	    String concernName;
	    String fmName;
	
	    concernName = am.getValue("concern\\@0");
	    fmName = concernName + "_interface";
	    am.add("concern\\filtermodule\\" + fmName);
	    am.add("concern\\filtermodule\\inputfilters\\inputfilter\\disp");
	    am.add("concern\\filtermodule\\inputfilters\\inputfilter\\type\\Dispatch");
	    am.add("concern\\filtermodule\\inputfilters\\inputfilter\\filterelements\\filterelement\\objectset\\object\\[");   	//name matching //fixme: should be signature!
	    am.add("concern\\filtermodule\\inputfilters\\inputfilter\\filterelements\\filterelement\\objectset\\object\\selector\\inner");
	    am.add("concern\\filtermodule\\inputfilters\\inputfilter\\filterelements\\filterelement\\objectset\\object\\target\\*");
	    am.add("concern\\filtermodule\\inputfilters\\inputfilter\\;");
	
	    //si on self toevoegen
	    am.add("concern\\superimposition\\filtermodules\\filtermodule@lastnew\\self");    //maak een nieuwe
	    am.add("concern\\superimposition\\filtermodules\\filtermodule@last\\filtermodule set\\filtermodule element\\" + fmName); //fmname staat nu al goed
	  }
	   */

    /* deprecated     
	  private void ExpandOnSelf() {
	    String fmName;
	    boolean check = true;

        int i = 0;
	    while(check) {
	      check = am.checkifExists("concern\\filtermodule@" + i);
	      if (!check) break;
	      fmName = am.getValue("concern\\filtermodule@" + i + "\\@0");        //get the name
	
	      check = am.checkifExists("concern\\filtermodule@" + i + "\\@1");
	      if (!check) break;    //fixme: don't show error msg
	      if ("on".equals(am.getValue("concern\\filtermodule@" + i + "\\@1"))) {    	//we have "on self"
	        am.add("concern\\superimposition\\filtermodule@lastnew\\self");    //maak een nieuwe
	        am.add("concern\\superimposition\\filtermodule@last\\filtermodule set\\filtermodule element\\" + fmName);
	      }
          i++;
	    }
	  }
	  */


	  /**
	   * Starts expanding
	   */
	  public void expand() {
	  	resolveEmptySuperImposition();
	  	addVoidOperator();
	  	
	  	/*ASTFrame frame;
	
	    am = new AstManipulator();
	    am.attach(COPPER.getParseTree());
	    
	    //    ExpandOnSelf();       //fixme: add checks to make sure this is not inserted unneccesary
	    //    AddDefault();
	    // AddSelfReference();
	    
	    if (COPPER.isShowtree()) {
	      frame = new ASTFrame("The tree (Syntactic Sugar)", COPPER.getParseTree());
	      frame.setSize(800, 600);
	      frame.setVisible(true);
	    }*/
	  }


	  /* deprecated 
	  private void AddSelfReference() {
	    String concernName;
	
	    concernName = am.getValue("concern\\@0");
	    am.add("concern\\superimposition\\selectors\\selector@lastnew\\self");
	    am.add("concern\\superimposition\\selectors\\selector@last\\selectorsexpression\\=");
	    am.add("concern\\superimposition\\selectors\\selector@last\\selectorsexpression\\" + concernName);
	  }
	  */
  
  	public void resolveEmptySuperImposition()
	{
		DataStore ds = DataStore.instance();
		Iterator cpsIterator = ds.getAllInstancesOf(CpsConcern.class);
		while(cpsIterator.hasNext())
		{
			CpsConcern concern = (CpsConcern)cpsIterator.next();
			//Debug.out(Debug.MODE_DEBUG,"COPPER[EmpSuperImpResolver]","Checking for valid concern: "+concern);
			if(concern != null)
			{ // We have a valid concern!
				//Debug.out(Debug.MODE_DEBUG,"COPPER[EmpSuperImpResolver]","Found valid concern["+concern.getQualifiedName()+"]: "+concern.getSuperImposition());
				if(concern.getSuperImposition() == null)
				{ // We do not have a superimposition part
					//Debug.out(Debug.MODE_DEBUG,"COPPER[EmpSuperImpResolver]","Found valid superimp["+concern.getQualifiedName()+"]: "+concern.getImplementation());
					if(concern.getImplementation() != null)
					{ // We do have an implementation part!
						//Debug.out(Debug.MODE_DEBUG,"COPPER[EmpSuperImpResolver]","Creating self superimposition for: "+concern.getQualifiedName());
						Debug.out(Debug.MODE_DEBUG,"COPPER[EmpSuperImpResolver]","Creating self superimposition for: "+concern.getQualifiedName());
						SuperImposition si = new SuperImposition();
						si = new SuperImposition();
					    si.setParent(concern);
					    concern.setSuperImposition(si);
					    ds.addObject(si);
					    
					    SelectorDefinition sd = new SelectorDefinition();
					    sd.setName("self");
					    sd.setParent(si);
					    si.addSelectorDefinition(sd);
					    ds.addObject(sd);
					    
					    Vector vtemp = new Vector();
					    if(concern.getImplementation() instanceof Source)
					    {
					    	//System.out.println("Impl is of type Source: "+((Source)concern.getImplementation()).getClassName());
					    	Iterator it = StringConverter.stringToStringList(((Source)concern.getImplementation()).getClassName(),".");
					    	while(it.hasNext()) 
					    	{
					    		vtemp.add(it.next());
					    	}
					    }
					    else if(concern.getImplementation() instanceof CompiledImplementation)
					    {
					    	//System.out.println("Impl is of type CompiledImplementation: "+((CompiledImplementation)concern.getImplementation()).getClassName());
					    	Iterator it = StringConverter.stringToStringList(((CompiledImplementation)concern.getImplementation()).getClassName(),".");
					    	while(it.hasNext())
					    	{
					    		vtemp.add(it.next());
					    	}
						}
					    else
					    {
					    	//System.out.println("Impl is of type ???: This is not supported!");
					    }
					    
					    ConcernReference cref = new ConcernReference();
					    Splitter split = new Splitter();
					    split.splitConcernReference(vtemp);
					    cref.setPackage(split.getPack());
					    cref.setName(split.getConcern());
					    ds.addObject(cref);
					    
					    SelClass sc = new SelClass();
					    sc.setClass(cref);
					    sc.setClassName(concern.getQualifiedName());
					    sc.setParent(sd);
					    sd.addSelExpression(sc);
					    ds.addObject(sc);
					}
				}
			}
		}
	}

  	private void addVoidOperator(){
  	  DataStore ds = DataStore.instance();
  	  Iterator filters = ds.getAllInstancesOf( Filter.class);
  	  Filter tempFilter;
  	  
  	  while(filters.hasNext()){
        tempFilter = (Filter) filters.next();
        if(tempFilter.getRightOperator() == null){
        	VoidFilterCompOper operator = new VoidFilterCompOper();
        	tempFilter.setRightOperator(operator);
        	ds.addObject(operator);
        }
  	  }  	  
  	}
}

