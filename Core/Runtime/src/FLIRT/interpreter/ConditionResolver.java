package Composestar.RuntimeCore.FLIRT.Interpreter;

import Composestar.RuntimeCore.FLIRT.Exception.InvalidConditionException;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: ConditionResolver.java,v 1.2 2006/02/13 12:01:32 composer Exp $
 */
public interface ConditionResolver {
    
    /**
     * @param cond
     * @return boolean
     * @roseuid 40DDD6A502F1
     */
    public boolean resolve(String cond);
}
