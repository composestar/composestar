/*
 * Created on 27-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

import java.util.Vector;


public class Switch extends Instruction{
    private ContextExpression expression;
    private Vector cases;
    
    public Switch( ContextExpression expression ){
        this.expression = expression;
        this.cases = new Vector();
    }
    
    
    
    
    /**
     * @return the expression
     */
    public ContextExpression getExpression(){
        return expression;
    }


    public void addCase( Case caseInstruction ){
        this.cases.add( caseInstruction );
    }
    
    public Case[] getCases(){
        return (Case[]) cases.toArray( new Case[cases.size()] );
    }
    
    
    public boolean hasCases(){
        return !cases.isEmpty();
    }


    public Object accept(Visitor visitor){
        return visitor.visitSwitch( this );
    }

}
