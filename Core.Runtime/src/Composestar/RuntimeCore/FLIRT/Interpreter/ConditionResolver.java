package Composestar.RuntimeCore.FLIRT.Interpreter;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * ConditionResolver.java 2072 2006-10-15 12:44:56Z elmuerte $
 */
public interface ConditionResolver
{

	/**
	 * @param cond
	 * @return boolean
	 * @roseuid 40DDD6A502F1
	 */
	public boolean resolve(String cond);
}
