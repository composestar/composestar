package Composestar.RuntimeCore.FLIRT.Exception;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: TargetNotFoundException.java,v 1.2 2006/02/13 12:01:32 composer Exp $
 * 
 * Exception thrown when a pattern calls for a target - selector pair not existing
 * within the enclosing filter module at the time of interpretation
 * (for example *.messageNotImplementedByInnerInternalOrExternal)
 */
public class TargetNotFoundException extends InvalidPatternExpressionException {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -3068254562496318841L;
	/**
     * Name of the internal, external or pseudo variable that was not found
     */
    private String target;
    
    /**
     * @inheritDoc
     * @param message
     * @roseuid 3F3652A2017E
     */
    public TargetNotFoundException(String message) {
        super(message);     
    }
    
    /**
     * Default constructor
     * @roseuid 3F3652A20156
     */
    public TargetNotFoundException() {
     
    }

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
}
