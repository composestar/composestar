/*
 * Created on 22-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Arjan de Roo
 */
public interface ExecutionModel extends Serializable
{

	public Iterator<ExecutionState> getEntranceStates();

	/**
	 * Returns the entrance state for the given selector. If a selector doesn't
	 * have it's own entrance state, the entrance state of the star-trace is
	 * returned.
	 * 
	 * @param message
	 * @return
	 */
	public ExecutionState getEntranceState(Message message);

	/**
	 * Returns all the messages for which there are different entrance states.
	 * 
	 * @return
	 */
	public Set<Message> getEntranceMessages();

	public boolean isEntranceMessage(Message message);

}
