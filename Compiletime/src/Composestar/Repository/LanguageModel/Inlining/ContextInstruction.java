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
		if (type == SET_INNER_CALL)
			visitor.VisitSetInnerCall(this);
		else if (type == CHECK_INNER_CALL)
			visitor.VisitCheckInnerCall(this);
		else if (type == RESET_INNER_CALL)
			visitor.VisitResetInnerCall(this);

		if (innerBlock != null)
			innerBlock.Accept(visitor); 		
	}
    
    public String toString(){
        String s = super.toString();
        switch( type ){
        case REMOVED:
            s += "ContextInstruction Removed\n;";
            return s;
        case SET_INNER_CALL:
            s += "SET innercall context " + methodId;
            s += "\n";
            return s;
        case CHECK_INNER_CALL:
            s += "CHECK innercall context " + methodId;
            s += "\nBlock:\n";
            s += innerBlock.toString();
            return s;
        case RESET_INNER_CALL:
            s += "RESET innercall context " + methodId;
            s += "\n";
            return s;
        default:
            return "UNKNOWN CONTEXT INSTRUCTION";
        }
    }
}
