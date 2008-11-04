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
	public Object visitFilterCode(FilterCode filterCode);

	/**
	 * Called when a block is visited.
	 * 
	 * @param block
	 * @return
	 */
	public Object visitBlock(Block block);

	/**
	 * Called when a branch is visited.
	 * 
	 * @param branch
	 * @return
	 */
	public Object visitBranch(Branch branch);

	/**
	 * Called when a FilterAction is visited.
	 * 
	 * @param filterAction
	 * @return
	 */
	public Object visitFilterAction(FilterActionInstruction filterAction);

	/**
	 * Called when a jump is visited.
	 * 
	 * @param jump
	 * @return
	 */
	public Object visitJump(Jump jump);
}
