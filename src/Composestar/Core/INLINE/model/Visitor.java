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
	 * Called when a contextInstruction is visited.
	 * 
	 * @param contextInstruction
	 * @return
	 */
	public Object visitContextInstruction(ContextInstruction contextInstruction);

	/**
	 * Called when a FilterAction is visited.
	 * 
	 * @param filterAction
	 * @return
	 */
	public Object visitFilterAction(FilterAction filterAction);

	/**
	 * Called when a jump is visited.
	 * 
	 * @param jump
	 * @return
	 */
	public Object visitJump(Jump jump);

	/**
	 * Called when a switch is visited.
	 * 
	 * @param switchInstruction
	 * @return
	 */
	public Object visitSwitch(Switch switchInstruction);

	/**
	 * Called when a case is visited.
	 * 
	 * @param caseInstruction
	 * @return
	 */
	public Object visitCase(Case caseInstruction);

	/**
	 * Called when a while is visited.
	 * 
	 * @param whileInstruction
	 * @return
	 */
	public Object visitWhile(While whileInstruction);
}
