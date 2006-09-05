/*
 * Created on 20-feb-2006
 *
 */
package Composestar.Core.FIRE2.model;

import java.util.Enumeration;
import java.util.Iterator;

import Composestar.Core.RepositoryImplementation.RepositoryEntity;

/**
 * This class is a node in a graph, which has some annotation.
 * 
 * @author Arjan de Roo
 */
public interface FlowNode{
    public final static int CONTEXT_NODE = 1;
	public final static int PROCEDURE_NODE = 2;
	public final static int PREDICATE_NODE = 3;
	public final static int ACTION_NODE = 4;
	
	
	public Enumeration getTransitions();

	/**
     * Returns the (first) transition from this startnode to the given endnode, or
     * null when no such transition exists.
     * 
     * @return
     */
    public FlowTransition getTransition(FlowNode endNode);
    
    
    
    /**
     * @return Returns the names.
     */
    public Iterator getNames();
    
    public boolean containsName( String name );
    
    /**
     * @return Returns the repositoryLink.
     */
    public RepositoryEntity getRepositoryLink();
}
