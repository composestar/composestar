/*
 * Created on 27-sep-2006
 *
 */
package Composestar.Core.INLINE.model;


public class ContextExpression{
    private int type;
    
    public final static int HAS_MORE_ACTIONS = 22;
    public final static int RETRIEVE_ACTION = 23;
    
    public ContextExpression( int type ){
        this.type = type;
    }

    /**
     * @return the type
     */
    public int getType(){
        return type;
    }
    
    
    
}
