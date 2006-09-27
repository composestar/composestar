/*
 * Created on 27-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

public class While extends Instruction{
    private ContextExpression expression;
    private Block instructions;
    
    public While( ContextExpression expression, Block instructions ){
        this.expression = expression;
        this.instructions = instructions;
    }
    
    
    

    /**
     * @return the expression
     */
    public ContextExpression getExpression(){
        return expression;
    }




    /**
     * @return the instructions
     */
    public Block getInstructions(){
        return instructions;
    }




    public Object accept(Visitor visitor){
        return visitor.visitWhile( this );
    }

}
