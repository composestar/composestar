/*
 * Created on 12-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

import Composestar.Core.LAMA.MethodInfo;

public class ContextInstruction extends Instruction{
    private int type;
    private MethodInfo method;
    private Instruction instruction;

    public final static int REMOVED = 0;
    public final static int SET_INNER_CALL = 10;
    public final static int CHECK_INNER_CALL = 11;
    public final static int RESET_INNER_CALL = 12;
    public final static int STORE_ACTION = 20;
    public final static int EXECUTE_STORED_ACTIONS = 21;

    public ContextInstruction( int type, MethodInfo method ){
        this.type = type;
        this.method = method;
    }
    
    public ContextInstruction( int type, MethodInfo method, Instruction instruction ){
        this( type, method );
        this.instruction = instruction;
    }

    /**
     * @return the method
     */
    public MethodInfo getMethod(){
        return method;
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
