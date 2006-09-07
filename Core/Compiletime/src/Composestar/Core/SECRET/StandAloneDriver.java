//Source file: F:\\composestar\\src\\Composestar\\core\\SECRET\\StandAloneDriver.java

//Source file: H:\\composestar\\src\\Composestar\\core\\SECRET\\StandAloneDriver.java

package Composestar.Core.SECRET;

import java.util.ArrayList;
import java.util.Properties;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.*;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: Composestar.Core.SECRET.cat,v 1.15 2004/03/11 15:05:07 pascal_durr 
 * Exp $
 * 
 * This class is just for testen/demoing perposes.
 */
public class StandAloneDriver {
    
    /**
     * @roseuid 4045E0C703CC
     */
    public StandAloneDriver() {
     
    }
    
    /**
     * @param args[]
     * @roseuid 4045E080036E
     */
    public static void main(String args[]) {
    	CommonResources cr = new CommonResources();
    	/*
		FilterType rttype = new FilterType();
    	rttype.setType("Realtime");
		Filter filter1 = new Filter();
		filter1.setName("check");
		filter1.setFilterType(rttype);

		FilterType waittype = new FilterType();
    	waittype.setType("Wait");
    	Filter filter2 = new Filter();
    	filter2.setName("sync");
    	filter2.setFilterType(waittype);
    	
		ArrayList list = new ArrayList();
    	FilterModule fm1 = new FilterModule();
    	fm1.setName("EatBeforeSix");
    	fm1.addInputFilter(filter1);
    	FilterModule fm2 = new FilterModule();
    	fm1.setName("PrinceWantsToEat");
    	fm1.addInputFilter(filter2);
		 */
		
		FilterType rttype = new FilterType();
    	rttype.setType("Realtime");
		Filter filter1 = new Filter();
		filter1.setName("check");
		filter1.setFilterType(rttype);

		FilterType waittype = new FilterType();
    	waittype.setType("Wait");
    	Filter filter2 = new Filter();
    	filter2.setName("sync");
    	filter2.setFilterType(waittype);
    	
		ArrayList list = new ArrayList();
    	FilterModule fm1 = new FilterModule();
    	fm1.setName("CheckFood");
    	fm1.addInputFilter(filter1);
    	FilterModule fm2 = new FilterModule();
    	fm2.setName("DietCheck");
    	fm2.addInputFilter(filter2);

		CpsConcern concern = new CpsConcern();
		concern.setName("concern1");
		concern.addFilterModule(fm1);
		fm1.setParent(concern);
		
		concern = new CpsConcern();
		concern.setName("concern2");
		concern.addFilterModule(fm2);
		fm2.setParent(concern);

		System.out.println("***Hallo1: "+fm1.getQualifiedName());
		System.out.println("***Hallo2: "+fm2.getQualifiedName());
    	
		FilterModuleReference fmr1 = new FilterModuleReference();
		fmr1.setRef(fm1);
		FilterModuleReference fmr2 = new FilterModuleReference();
		fmr2.setRef(fm2);
		
    	list.add(fmr1);
    	list.add(fmr2);
    	
    	cr.addResource("filters",list);
    	SECRET secret = new SECRET();
    	Properties props = new Properties();
    	props.setProperty("SECRET_CONFIG","filterdesc.xml");
    	props.setProperty("SECRET_AVAILABLE_ELEMENTS","System/Scheduler");
    	props.setProperty("SECRET_DEBUG","false");
    	props.setProperty("SECRET_output_html_file","SECRET");
		DataStore ds = DataStore.instance();
    	ds.addObject("config",props);
		cr.addResource("currentconcern","Diet");
		cr.addResource("repository",ds);
    	try
		{
			SecretRepository sr = SecretRepository.instance();
			sr.run(cr);
			secret.run(cr);
		}
     	catch(Exception e){e.printStackTrace();}     
    }
}
