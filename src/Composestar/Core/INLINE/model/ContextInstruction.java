/*
 * Created on 12-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

import Composestar.Core.LAMA.MethodInfo;

public class ContextInstruction extends Instruction{
    private int type;
    private MethodInfo method;
    private boolean enabled;

    public final static int REMOVED = 0;
    public final static int SET_INNER_CALL = 10;
    public final static int CHECK_INNER_CALL = 11;

    public ContextInstruction( int type, MethodInfo method ){
        this.type = type;
        this.method = method;
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
     * @return the enabled
     */
    public boolean isEnabled(){
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }
}
