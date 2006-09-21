package Composestar.Repository.LanguageModel.Inlining;

import Composestar.Repository.LanguageModel.Inlining.Visitor.*;

public class ContextInstruction extends InlineInstruction implements IVisitable
{
	private int type;
	private long methodId;
	private Block innerBlock;

	public final static int REMOVED = 0; //do nothing for this context instruction
	public final static int SET_INNER_CALL = 10;
	public final static int CHECK_INNER_CALL = 11;
	public final static int RESET_INNER_CALL = 12;

	public ContextInstruction(int type, long methodId, Block innerBlock)
	{
		this.type = type;
		this.methodId = methodId;
		this.innerBlock = innerBlock;
	}

	/**
     * @return the method
	 * @property 
	 */
	public long get_MethodId()
	{
		return methodId;
	}

	/**
     * @return the type
	 * @property 
     */
	public int get_Type()
	{
			return type;
	}

	/**
     * @return the innerBlock
	 * @property 
     */
	public Block get_InnerBlock()
	{
		return innerBlock;
	}

	public void Accept(IVisitor visitor)
	{
		super.Accept(visitor);
		visitor.VisitContextInstruction(this);
		//TODO visit innerblock, if not null
	}
}
