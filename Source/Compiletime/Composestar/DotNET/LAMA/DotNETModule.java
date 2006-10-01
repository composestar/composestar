/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.DotNET.LAMA;

import Composestar.Core.RepositoryImplementation.SerializableRepositoryEntity;

/**
 * Corresponds to the Module class in the .NET framework. For more information on 
 * the methods and their meaning please refer to the microsoft documentation:
 * http://msdn.microsoft.com/library/default.asp?url=/library/en-us/cpref/html/frlr
 * fsystemreflectionmoduleclasstopic.asp
 */
public class DotNETModule implements SerializableRepositoryEntity {
    /**
	 * 
	 */
	private static final long serialVersionUID = 9147930667863841248L;
	public String Name;
    public String FullyQualifiedName;
    
    /**
     * @roseuid 401B84CF0227
     */
    public DotNETModule() {
     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 401B84CF0225
     */
    public String name() {
        return Name;     
    }
    
    /**
     * @param name
     * @roseuid 402A07BF002D
     */
    public void setName(String name) {
        Name = name;     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 401B84CF0226
     */
    public String fullyQualifiedName() {
        return FullyQualifiedName;     
    }
    
    /**
     * @param name
     * @roseuid 402A07C4017E
     */
    public void setFullyQualifiedName(String name) {
        // TODO: Rename
        FullyQualifiedName = name;     
    }
}
