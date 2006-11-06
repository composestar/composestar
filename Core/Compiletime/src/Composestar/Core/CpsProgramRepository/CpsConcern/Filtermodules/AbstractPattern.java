/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import Composestar.Core.RepositoryImplementation.*;

public abstract class AbstractPattern extends ContextRepositoryEntity {
    public Target target;
    public MessageSelector selector;
    
    public AbstractPatternAST apAST;
    
    /**
     * @roseuid 404DDA71039C
     * @deprecated
     */
    public AbstractPattern() {
    	super();     
    }
    
    public AbstractPattern(AbstractPatternAST anapAST) {
    	super();
    	apAST = anapAST;
    	
    	selector = new MessageSelector(anapAST.getSelector());
    }
    
    /**
     * @return 
     * Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target
     * @roseuid 401FAA670136
     */
    public Target getTarget() {
    return target;     
    }
    
    /**
     * @param targetValue
     * @roseuid 401FAA670140
     */
    public void setTarget(Target targetValue) {
    	target = targetValue;     
    }
    
    /**
     * @return 
     * Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelect
     * or
     * @roseuid 401FAA670154
     */
    public MessageSelector getSelector() {
    return selector;     
    }
    
    /**
     * @param selectorValue
     * @roseuid 401FAA67015E
     */
    public void setSelector(MessageSelector selectorValue) {
    this.selector = selectorValue;     
    }
}
