package Composestar.RuntimeCore.FLIRT.Policy;

import Composestar.RuntimeCore.FLIRT.Interpreter.FilterModuleRuntime;
import Composestar.RuntimeCore.FLIRT.Message.Message;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;

import java.util.ArrayList;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: FilterPolicy.java,v 1.1 2006/02/16 23:15:54 pascal_durr Exp $
 * 
 * Models the Way messages are filtered.
 * This deals with the way messages are handled within a FilterModule.
 */
public abstract class FilterPolicy 
{
    
    /**
     * Constructs a new FilterPolicy
     * @roseuid 3F36533000CF
     */
    protected FilterPolicy() {
     
    }
    
    /**
     * Returns the default FilterPolicy
     * Works as a factory method.
     * @return a FilterPolicy
     * @roseuid 3F36533000D0
     */
    public static final FilterPolicy getPolicy() {
		return new MetaFilterPolicy();
    }
    
    /**
     * Executes this filter policy on the message given.
     * @param fm current FilterModule
     * @param filterList list of filter within the filtermodule
     * @param aMessage message to filter
     * @return Composestar.Runtime.FLIRT.policy.PolicyExecutionResult
     * @roseuid 3F36533000D8
     */
    public abstract PolicyExecutionResult executeFilterPolicy(FilterModuleRuntime fm, ArrayList filterList, MessageList aMessage);
}
