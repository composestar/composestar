package Composestar.Repository.LanguageModel.Inlining;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Iterator;

import Composestar.Repository.LanguageModel.Inlining.Visitor.*;

public class Block extends InlineInstruction implements IVisitable
{
	private InlineInstruction[] instructions;
	private int instructionCount = 0;

	public Block()
	{
		instructions = new InlineInstruction[10];
	}

	public void addInstruction(InlineInstruction instruction)
	{
		instructions[instructionCount++] = instruction;
	}

	/** @property */
	public InlineInstruction[] get_Instructions()
	{
		return instructions;
	}

	public void Accept(IVisitor visitor)
	{
		super.Accept(visitor);

		for (int i = 0; i < instructionCount; i++)
		{
			Object o= instructions[i];
			((IVisitable)o).Accept(visitor);
		}
		//Iterator instr = instructions.iterator();

		//while (instr.hasNext())
		//{
		//    java.lang.Object o= instr.next();
		//    ((IVisitable)o).Accept(visitor);			
		//}
	}
}
