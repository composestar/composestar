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

import Composestar.Core.CKRET.Config.ConflictRule;
import Composestar.Core.CKRET.Config.Resource;
import Composestar.Core.CKRET.Config.ResourceType;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.util.regex.Labeler;
import Composestar.Core.FIRE2.util.regex.Matcher;

/**
 * 
 */
public class AbstractVM
{
	public static List<Conflict> analyze(Concern concern, ExecutionModel model, SECRETResources resources)
	{
		Labeler labeler = resources.getLabeler();
		labeler.setCurrentConcern(concern);

		List<Conflict> conflicts = new ArrayList<Conflict>();

		for (ConflictRule rule : resources.getRules())
		{
			for (Resource resource : resources.getResources())
			{
				if (rule.getResource().getType() == ResourceType.Wildcard || rule.getResource().equals(resource))
				{
					labeler.setCurrentResource(resource);
					Matcher matcher = new Matcher(rule.getPattern(), model, labeler);

					if (matcher.matches())
					{
						Conflict conflict = new Conflict();
						conflict.setResource(resource.getName());
						conflict.setMsg(rule.getMessage());
						// conflict.setSequence(matcher.matchTrace());
						conflict.setExpr(rule.getPattern().toString());
						conflicts.add(conflict);
					}
				}
			}
		}
		return conflicts;
	}
}
