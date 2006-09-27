/*
 * Created on 27-sep-2006
 *
 */
package Composestar.Repository.LanguageModel.Inlining;

import Composestar.Repository.LanguageModel.Inlining.Visitor.IVisitor;

public class Case extends InlineInstruction{
    private int checkConstant;
    private Block instructions;
    
    public Case( int checkConstant, Block instructions ){
        this.checkConstant = checkConstant;
        this.instructions = instructions;
    }
    
    
    
    
    /**
     * @return the checkConstant
     * @property
     */
    public int get_CheckConstant(){
        return checkConstant;
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
        visitor.VisitCase(this);
    }
    
    public String toString(){
        String s = "Case " + checkConstant + ":\n";
        s += instructions.toString();
        return s;
    }
}
