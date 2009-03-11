/*
 * Created on 21-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

/**
 * All classes that can be visited by the instructionvisitor should implement
 * this interface.
 * 
 * @author Arjan
 */
public interface Visitable
{
	/**
	 * Method called when a visitor visits the implementing class.
	 * 
	 * @param visitor
	 * @return
	 */
	Object accept(Visitor visitor);
}
