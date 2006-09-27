/*
 * Created on 27-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

public class Case extends Instruction{
    private int checkConstant;
    private Block instructions;
    
    public Case( int checkConstant, Block instructions ){
        this.checkConstant = checkConstant;
        this.instructions = instructions;
    }
    
    
    
    /**
     * @return the checkConstant
     */
    public int getCheckConstant(){
        return checkConstant;
    }


    /**
     * @return the instructions
     */
    public Block getInstructions(){
        return instructions;
    }



    /**
     * @see Composestar.Core.INLINE.model.Visitable#accept(Composestar.Core.INLINE.model.Visitor)
     */
    public Object accept(Visitor visitor){
        return visitor.visitCase( this );
    }
    
    
    
}
