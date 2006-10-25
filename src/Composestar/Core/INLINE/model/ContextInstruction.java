/*
 * Created on 12-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

/**
 * Indicates an instruction that manipulates the filtercontext or the joinpointcontext.
 *
 * @author Arjan
 */
public class ContextInstruction extends Instruction{
	/**
	 * The type of the instruction
	 */
    private int type;
    
    /**
     * An extra code
     */
    private int code;
    
    private Instruction instruction;

    public final static int REMOVED = 0;
    public final static int SET_INNER_CALL = 10;
    public final static int CHECK_INNER_CALL = 11;
    public final static int RESET_INNER_CALL = 12;
    public final static int CREATE_ACTION_STORE = 20;
    public final static int STORE_ACTION = 21;
    public final static int CREATE_JOIN_POINT_CONTEXT = 30;
    public final static int RESTORE_JOIN_POINT_CONTEXT = 31;
    public final static int RETURN_ACTION = 100;


    public ContextInstruction( int type ){
        this.type = type;
    }
    
    public ContextInstruction( int type, int code ){
        this.type = type;
        this.code = code;
    }
    
    
    public ContextInstruction( int type, int code, Instruction instruction ){
        this( type, code );
        this.instruction = instruction;
    }

    

    /**
     * @return the type
     */
    public int getType(){
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type){
        this.type = type;
    }
    
    

    /**
     * @return the code
     */
    public int getCode(){
        return code;
    }

    /**
     * @return the instruction
     */
    public Instruction getInstruction(){
        return instruction;
    }

    /**
     * @see Composestar.Core.INLINE.model.Visitable#accept(Composestar.Core.INLINE.model.Visitor)
     */
    public Object accept(Visitor visitor){
        return visitor.visitContextInstruction( this );
    }

    
    
}
