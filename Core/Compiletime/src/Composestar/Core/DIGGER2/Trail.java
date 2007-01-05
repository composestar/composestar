/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.DIGGER2;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.FIRE2.model.Message;

/**
 * A trail contains information about the transition to a new breadcrumb.
 * 
 * @author Michiel Hendriks
 */
public class Trail
{
	protected Breadcrumb owner;

	protected ConditionExpression condition;

	protected Message resultMessage;

	protected Concern targetConcern;

	public Trail(Breadcrumb inOwner)
	{
		owner = inOwner;
	}

	public void setCondition(ConditionExpression inCondition)
	{
		condition = inCondition;
	}

	public ConditionExpression getCondition()
	{
		return condition;
	}

	public void setResultMessage(Message inMessage)
	{
		resultMessage = inMessage;
	}

	public Message getResultMessage()
	{
		return resultMessage;
	}

	public void setTargetConcern(Concern inConcern)
	{
		targetConcern = inConcern;
	}

	public Concern getTargetConcern()
	{
		return targetConcern;
	}

}
