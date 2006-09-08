package Composestar.RuntimeCore.FLIRT.Interpreter;

import Composestar.RuntimeCore.FLIRT.Exception.InvalidConditionException;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: ConditionResolver.java 27 2006-02-16 23:14:57Z pascal_durr $
 */
public interface ConditionResolver {
    
    /**
     * @param cond
     * @return boolean
     * @roseuid 40DDD6A502F1
     */
    public boolean resolve(String cond);
}
