/*
 * Created on 16-aug-2006
 *
 */
package Composestar.Core.FIRE2.util.queryengine.predicates;

import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.util.queryengine.Predicate;

/**
 * 
 *
 * @author Arjan de Roo
 */
public class StateType extends Predicate {
    private String type;
    
    public StateType( String type ){
        this.type = type;
    }
    
    public boolean isTrue(ExecutionState state) {
        return state != null && state.getFlowNode().containsName(type);
    }
    
    public String toString(){
        return super.toString() + "(StateType)";
    }
}
