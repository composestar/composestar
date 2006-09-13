package Composestar.Repository.LanguageModel.Inlining;

import java.util.Enumeration;
import java.util.Vector;

public class Block extends Instruction {
	private Vector instructions;

	public Block()
	{
		instructions = new Vector();
	}

	public void addInstruction(Instruction instruction)
	{
		instructions.addElement(instruction);
	}

	public Enumeration getInstructions()
	{
		return instructions.elements();
	}
}
