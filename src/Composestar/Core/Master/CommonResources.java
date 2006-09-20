//Source file: C:\\local\\staijen\\composestar\\src\\Composestar\\core\\Master\\CommonResources.java

/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.Master;

import Composestar.Core.RepositoryImplementation.DataMap;
import java.util.Properties;
import java.io.Serializable;

/**
 * This class holds the shared resources between the modules e.g the repository 
 * object.
 * @todo Add hashmap for storing non-global stuff.
 */
public class CommonResources implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 2652039710117430543L;

	/**
     * The main project configuration file. This resource is initialized by Master
     */
    //public Properties ProjectConfiguration;
    
    /**
     * Information about the Custom Filters that are used in this project.
     */
    public Properties CustomFilters;
    
    /**
     * HashMap holding all private resources.
     */
    private DataMap ResourceMap;
    
    /**
     * Default ctor.
     * @roseuid 401B9C5201F7
     */
    public CommonResources() {
        ResourceMap = new DataMap();     
    }
    
    /**
     * Add a resource with a key.
     * 
     * @param key An identifier for this resource
     * @param object The object to store for this key.
     * @roseuid 401D4BEF01E0
     */
    public void addResource(String key, Object object) {
        ResourceMap.put( key, object );     
    }
    
    /**
     * Fetch a resource with a key.
     * 
     * @param key String key for the object you want to retrieve.
     * @return An object pointer if an object with the specified key was found or null 
     * if the key is invalid.
     * @roseuid 401D4C0F0196
     */
    public Object getResource(String key) {
        if( !ResourceMap.containsKey( key ) ) { // if not in map
            return null;
        }

        return ResourceMap.get( key );     
    }
}
