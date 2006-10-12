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
	public final static int CREATE_JOIN_POINT_CONTEXT = 30;
	public final static int RESTORE_JOIN_POINT_CONTEXT = 31;
    public final static int RETURN_ACTION = 100;

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
		switch (type){
			case SET_INNER_CALL:
				visitor.VisitSetInnerCall(this);
				break;
			case CHECK_INNER_CALL:
				visitor.VisitCheckInnerCall(this);
				break;
			case RESET_INNER_CALL:
				visitor.VisitResetInnerCall(this);
				break;
			case CREATE_ACTION_STORE:
				visitor.VisitCreateActionStore(this);
				break;
			case STORE_ACTION:
				visitor.VisitStoreAction(this);
				break;
			case CREATE_JOIN_POINT_CONTEXT:
				visitor.VisitCreateJoinPointContext(this);
				break;
			case RESTORE_JOIN_POINT_CONTEXT:
				visitor.VisitRestoreJoinPointContext(this);
				break;
			case RETURN_ACTION:
				visitor.VisitReturnAction(this);
				break;
			case REMOVED:
				return;
			default:
				break;
		}

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
		case CREATE_JOIN_POINT_CONTEXT:
			s += "Create JoinPointContext";
			return s;
		case RESTORE_JOIN_POINT_CONTEXT:
			s += "Restore JoinPointContext";
			return s;
		case RETURN_ACTION:
			s += "Return action";
			return s;
        default:
            return "UNKNOWN CONTEXT INSTRUCTION";
        }
    }
}
