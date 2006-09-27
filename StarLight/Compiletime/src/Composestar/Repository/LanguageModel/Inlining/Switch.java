/*
 * Created on 27-sep-2006
 *
 */
package Composestar.Repository.LanguageModel.Inlining;

import Composestar.Repository.LanguageModel.LinkedList;
import Composestar.Repository.LanguageModel.Inlining.Visitor.IVisitor;

public class Switch extends InlineInstruction{
    private ContextExpression expression;
    private LinkedList cases;
    
    public Switch( ContextExpression expression ){
        this.expression = expression;
        cases = new LinkedList();
    }
    
    
    
    
    /**
     * @return the expression
     * @property
     */
    public ContextExpression get_Expression(){
        return expression;
    }
    
    
    public void addCase( Case caseInstruction ){
        cases.add( caseInstruction );
    }

    /**
     * 
     * @return the cases
     * @property
     */
    public Case[] get_Cases(){
        Object[] objs = cases.toArray();
        Case[] result = new Case[objs.length];
        
        for (int i=0; i<objs.length; i++){
            result[i] = (Case) objs[i];
        }
        
        return result;
    }



    public void Accept(IVisitor visitor)
    {
        super.Accept(visitor);
        visitor.VisitSwitch(this);
    }
    
    
    public String toString(){
        String s = super.toString() + "Switch(" + expression.toString() + "){\n";
        
        Case[] cases = get_Cases();
        for (int i=0; i<cases.length; i++){
            s += cases[i].toString();
        }
        
        s += "}\n";
        
        return s;
    }
}
