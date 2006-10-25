/*
 * Created on 27-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

/**
 * A while instruction to iterate over all stored actions.
 * 
 * @author Arjan
 */
public class While extends Instruction
{
	/**
	 * The while expression
	 */
	private ContextExpression expression;

	/**
	 * An instructionblock containing the instructions to execute while the
	 * while-expression is true.
	 */
	private Block instructions;

	/**
	 * The constructor
	 * 
	 * @param expression The while-expression
	 * @param instructions The instructions within the while.
	 */
	public While(ContextExpression expression, Block instructions)
	{
		this.expression = expression;
		this.instructions = instructions;
	}

	/**
	 * @return the expression
	 */
	public ContextExpression getExpression()
	{
		return expression;
	}

	/**
	 * @return the instructions
	 */
	public Block getInstructions()
	{
		return instructions;
	}

	/**
	 * @see Composestar.Core.INLINE.model.Visitable#accept(Composestar.Core.INLINE.model.Visitor)
	 */
	public Object accept(Visitor visitor)
	{
		return visitor.visitWhile(this);
	}

}
