package Composestar.Repository.LanguageModel.Inlining;

import Composestar.Repository.LanguageModel.Inlining.Visitor.*;

public class ContextInstruction extends InlineInstruction implements IVisitable
{
	private int type;
	private int code;
	private Block innerBlock;

	public final static int REMOVED = 0; //do nothing for this context instruction
    public final static int SET_INNER_CALL = 10;
    public final static int CHECK_INNER_CALL = 11;
    public final static int RESET_INNER_CALL = 12;
    public final static int CREATE_ACTION_STORE = 20;
    public final static int STORE_ACTION = 21;

	public ContextInstruction(int type, int methodId, Block innerBlock)
	{
		this.type = type;
		this.code = methodId;
		this.innerBlock = innerBlock;
	}

	/**
     * @return the code
	 * @property 
	 */
	public int get_Code()
	{
		return code;
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
            s += "SET innercall context " + code;
            s += "\n";
            return s;
        case CHECK_INNER_CALL:
            s += "CHECK innercall context " + code;
            s += "\nBlock:\n";
            s += innerBlock.toString();
            return s;
        case RESET_INNER_CALL:
            s += "RESET innercall context " + code;
            s += "\n";
            return s;
        case CREATE_ACTION_STORE:
            s += "Create action store";
            return s;
        case STORE_ACTION:
            s += "Store action " + code;
            return s;
        default:
            return "UNKNOWN CONTEXT INSTRUCTION";
        }
    }
}
