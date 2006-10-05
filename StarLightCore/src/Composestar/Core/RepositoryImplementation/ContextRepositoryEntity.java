/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.RepositoryImplementation;


/**
 * This is a RepositoryEntity that is context-sensitive in the sense that it 
 * contains a reference to its encapsulating parent.
 * This parent is typically set during instantiation (i.e. through the 
 * constructor)
 */
public class ContextRepositoryEntity extends RepositoryEntity {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 9219062801100391060L;
	/**
     * = null;       //name of object
     */
    private Object parent;
    
    
    /**
     * @roseuid 404C4B6503AD
     */
    public ContextRepositoryEntity() {
    	super();     
    }
    
    /**
     * @param parent
     * @roseuid 402CB721025D
     */
    public ContextRepositoryEntity(RepositoryEntity parent) {
     	setParent(parent);
    }
    
    /**
     * @return java.lang.Object
     * @roseuid 401FAA620319
     */
    public Object getParent() {
    	return parent;     
    }
    
    /**
     * @param parentValue
     * @roseuid 401FAA62031A
     */
    public void setParent(Object parentValue) {
    	this.parent = parentValue;
    	this.updateRepositoryReference(); 
    }

	public Object clone () throws CloneNotSupportedException
	{
		ContextRepositoryEntity newObject;

		newObject = (ContextRepositoryEntity)super.clone();

			// At this point, the newObject shares all data with the object
			// running clone. If you want newObject to have its own
			// copy of data, you must clone this data yourself.
		
		return newObject;
	}
}
