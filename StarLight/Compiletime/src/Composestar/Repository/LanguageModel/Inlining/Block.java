package Composestar.Repository.LanguageModel.Inlining;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Iterator;

import Composestar.Repository.LanguageModel.Inlining.Visitor.*;   

public class Block extends Instruction implements IVisitable  {
	private Vector instructions;

	public Block()
	{
		instructions = new Vector();
	}

	public void addInstruction(Instruction instruction)
	{
		instructions.addElement(instruction);
	}

	/** @property */
	public Object[] get_Instructions()
	{
		return instructions.toArray();
	}

	public void Accept(IVisitor visitor)
	{
		Iterator instr = instructions.iterator();

		while (instr.hasNext())
		{
			java.lang.Object o= instr.next();
			((IVisitable)o).Accept(visitor);			
		}
	}
}
