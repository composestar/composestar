package Composestar.Repository.LanguageModel.Inlining;

import Composestar.Repository.LanguageModel.Inlining.Visitor.IVisitable;
import Composestar.Repository.LanguageModel.Inlining.Visitor.IVisitor;
import Composestar.Repository.LanguageModel.LinkedList;

public class Block extends InlineInstruction implements IVisitable
{
	private LinkedList instructions;

	public Block()
	{
        instructions = new LinkedList();
	}

	public void addInstruction(InlineInstruction instruction)
	{
		instructions.add( instruction );
	}

    public void set_Instructions( InlineInstruction[] instructions ){
        this.instructions = new LinkedList();
        for (int i=0; i<instructions.length; i++){
            addInstruction( instructions[i] );
        }
    }
    
	/** @property */
	public InlineInstruction[] get_Instructions()
	{
		Object[] objs = this.instructions.toArray();
        InlineInstruction[] instructions = new InlineInstruction[ objs.length ];
        for (int i=0; i<objs.length; i++){
            instructions[i] = (InlineInstruction) objs[i];
        }
        
        return instructions;
	}

	public void Accept(IVisitor visitor)
	{
		super.Accept(visitor);

        InlineInstruction[] instructions = get_Instructions();
		for (int i = 0; i < instructions.length; i++)
		{
			InlineInstruction instr = instructions[i];
			instr.Accept(visitor);
		}
		//Iterator instr = instructions.iterator();

		//while (instr.hasNext())
		//{
		//    java.lang.Object o= instr.next();
		//    ((IVisitable)o).Accept(visitor);			
		//}
	}
    
    public String toString(){
        String s = super.toString();
        Object[] objs = instructions.toArray();
        for (int i=0; i<objs.length; i++){
            s += objs[i].toString();
        }
        
        s += "\n\n";
        
        return s;
    }
}
