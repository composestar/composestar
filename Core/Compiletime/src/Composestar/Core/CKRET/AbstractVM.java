/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2004-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CKRET;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Composestar.Core.CKRET.Config.ResourceHandler;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.FIRE2.util.regex.Matcher;

/**
 * 
 */
public class AbstractVM {

	private Map resources;

	public AbstractVM()
	{
	}
	
	public List analyze(Concern concern, FilterModuleOrder filterModules)
	{
		FireModel fireModel = new FireModel(concern, filterModules, true);
		
		ExecutionModel execModel = fireModel.getExecutionModel();
		
		ResourceOperationLabeler labeler = new ResourceOperationLabeler();
		
		List conflicts = new ArrayList();

		for( Iterator ci = Repository.instance().getConstraints().iterator(); ci.hasNext(); )
		{
			Constraint constraint = (Constraint) ci.next();
			
			for( Iterator ri = ResourceHandler.getResources().iterator(); ri.hasNext(); )
			{
				Resource res = (Resource) ri.next();
				if( constraint.getResource().equals("*") || constraint.getResource().equals(res.getName()))
				{
					labeler.setCurrentResource(res.getName());
					Matcher matcher = new Matcher(constraint.getPattern(), execModel, labeler);
					
					if ( matcher.matches() ){
						Conflict conflict = new Conflict();
						conflict.setResource(res.getName());
						conflict.setMsg(constraint.getMessage());
//						conflict.setSequence(matcher.matchTrace());
						conflict.setExpr(constraint.getPattern().toString());
						conflicts.add(conflict);
					}
				}
			}
		}
		//System.err.println("AVM: done...");
		return conflicts;
	}
}
