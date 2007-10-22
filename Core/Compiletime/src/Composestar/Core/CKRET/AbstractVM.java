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
import java.util.List;

import Composestar.Core.CKRET.Config.ResourceHandler;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.util.regex.Matcher;

/**
 * 
 */
public class AbstractVM
{
	// TODO should be stored in common resources
	protected static ResourceOperationLabeler labeler;

	public AbstractVM()
	{
		if (labeler == null)
		{
			createLabeler();
		}
	}

	protected static void createLabeler()
	{
		labeler = new ResourceOperationLabeler();
	}

	public List<Conflict> analyze(Concern concern, ExecutionModel model)
	{
		labeler.setCurrentConcern(concern);

		List<Conflict> conflicts = new ArrayList<Conflict>();

		for (Object o1 : Repository.instance().getConstraints())
		{
			Constraint constraint = (Constraint) o1;

			for (Object o : ResourceHandler.getResources())
			{
				Resource res = (Resource) o;
				if (constraint.getResource().equals("*") || constraint.getResource().equals(res.getName()))
				{
					labeler.setCurrentResource(res.getName());
					Matcher matcher = new Matcher(constraint.getPattern(), model, labeler);

					if (matcher.matches())
					{
						Conflict conflict = new Conflict();
						conflict.setResource(res.getName());
						conflict.setMsg(constraint.getMessage());
						// conflict.setSequence(matcher.matchTrace());
						conflict.setExpr(constraint.getPattern().toString());
						conflicts.add(conflict);
					}
				}
			}
		}
		// System.err.println("AVM: done...");
		return conflicts;
	}
}
