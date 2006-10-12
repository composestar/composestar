/*
 * Created on 27-sep-2006
 *
 */
package Composestar.Repository.LanguageModel.Inlining;

import Composestar.Repository.LanguageModel.Inlining.Visitor.IVisitor;

public class While extends InlineInstruction{
    private ContextExpression expression;
    private Block instructions;
    
    public While( ContextExpression expression, Block instructions ){
        this.expression = expression;
        this.instructions = instructions;
    }
        
    /**
     * @return the expression
     * @property
     */
    public ContextExpression get_Expression(){
        return expression;
    }

    /**
     * @return the instructions
     * @property
     */
    public Block get_Instructions(){
        return instructions;
    }

    public void Accept(IVisitor visitor)
    {
		super.Accept(visitor);
		int label = get_Label();//FIXME nice way to restore label after visitor has changed it
		visitor.VisitWhile(this);
		if (instructions != null)
			instructions.Accept(visitor);
		visitor.VisitWhileEnd(this);
		set_Label(label);//FIXME
    }
    
    public String toString(){
        String s = super.toString() + "While(" + expression.toString() + "){\n";
        s += instructions.toString();
        s += "}\n";
        
        return s;
    }
}