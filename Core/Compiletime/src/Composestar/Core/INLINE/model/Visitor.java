/*
 * Created on 21-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

/**
 * All instructionvisitors must implement this interface.
 * 
 * @author Arjan
 */
public interface Visitor
{
	/**
	 * Called when a FilterCode instance is visited.
	 * 
	 * @param filterCode
	 * @return
	 */
	Object visitFilterCode(FilterCode filterCode);

	/**
	 * Called when a block is visited.
	 * 
	 * @param block
	 * @return
	 */
	Object visitBlock(Block block);

	/**
	 * Called when a branch is visited.
	 * 
	 * @param branch
	 * @return
	 */
	Object visitBranch(Branch branch);

	/**
	 * Called when a FilterAction is visited.
	 * 
	 * @param filterAction
	 * @return
	 */
	Object visitFilterAction(FilterActionInstruction filterAction);

	/**
	 * Called when a jump is visited.
	 * 
	 * @param jump
	 * @return
	 */
	Object visitJump(Jump jump);
}
