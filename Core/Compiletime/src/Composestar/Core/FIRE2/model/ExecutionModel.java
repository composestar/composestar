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

import Composestar.Core.CpsRepository2.TypeSystem.CpsMessage;

/**
 * Interface for execution models of filter sets of a concern.
 * 
 * @author Arjan de Roo
 */
public interface ExecutionModel extends Serializable
{

	/**
	 * @return all entrance states
	 */
	Iterator<ExecutionState> getEntranceStates();

	/**
	 * Returns the entrance state for the given selector. If a selector doesn't
	 * have it's own entrance state, the entrance state of the star-trace is
	 * returned.
	 * 
	 * @param message
	 * @return
	 */
	ExecutionState getEntranceState(CpsMessage message);

	/**
	 * Returns all the messages for which there are different entrance states.
	 * 
	 * @return
	 */
	Set<CpsMessage> getEntranceMessages();

	/**
	 * @param message
	 * @return true if the message is an entrance message.
	 */
	boolean isEntranceMessage(CpsMessage message);
}
