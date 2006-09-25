using System;
using System.Collections.Generic;
using System.Text;
using Mono.Cecil;
using Mono.Cecil.Cil;


namespace Testing.CecilILReader
{
    /// <summary>
    /// Class used to retrieve IL code from a method
    /// </summary>
    public class ILReader
    {
        AssemblyDefinition myLibrary;

        /// <summary>
        /// Opens the assembly.
        /// </summary>
        /// <param name="filename">The filename.</param>
        public void OpenAssembly(string filename)
        {
            myLibrary = AssemblyFactory.GetAssembly(filename);
        }

        /// <summary>
        /// Gets the IL instructions.
        /// </summary>
        /// <param name="className">Name of the class.</param>
        /// <param name="methodName">Name of the method.</param>
        /// <returns></returns>
        public List<ILInstruction> GetILInstructions(string className, string methodName)
        {
            TypeDefinition t = myLibrary.MainModule.Types[className];
            if (t == null)
                return null;

            MethodDefinition m = null;
            foreach (MethodDefinition method in t.Methods)
            {
                if (method.Name.Equals(methodName))
                    m = method;
            }

            if (m == null)
                return null;

            if (m.HasBody == false)
                return null;

            List<ILInstruction> ret = new List<ILInstruction>();

            foreach (Instruction i in m.Body.Instructions)
            {
                string opCode;
                string operand;
                opCode = i.OpCode.Name;
                operand = i.Operand == null ? string.Empty : i.Operand.ToString();
                ILInstruction ilinstr = new ILInstruction(i.Offset, opCode, operand);
                ret.Add(ilinstr);
            }

            return ret;
        }

        public bool ContainsILInstructions(List<ILInstruction> instructions, List<ILInstruction> instructionsToCheck)
        {
            bool containsInstructions = false;
            int instrCounter = 0;
            ILInstruction instructionToCheck = instructionsToCheck[instrCounter];

            foreach (ILInstruction instr in instructions)
            {
                if (instr.OpCode.Equals(instructionToCheck.OpCode) && instr.Operand.Equals(instructionToCheck.Operand))
                {
                    containsInstructions = true;
                    instrCounter++;
                    if (instrCounter < instructionsToCheck.Count)
                        instructionToCheck = instructionsToCheck[instrCounter];
                    else
                        return containsInstructions;
                }
                else
                    containsInstructions = false;
            }

            return containsInstructions;
        }
    }
}
