#region license
//
// (C) db4objects Inc. http://www.db4o.com
//
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
#endregion

using System;
using System.IO;
using System.Text;
using Mono.Cecil;
using Mono.Cecil.Cil;
using System.Globalization;
 
namespace Composestar.StarLight.Utilities
{
	/// <summary>
    /// Utility to format Cecil IL constructions to a textual representation.
	/// </summary>
	public sealed class CecilFormatter 
    {

        /// <summary>
        /// Formats the instruction.
        /// </summary>
        /// <param name="instruction">The instruction.</param>
        /// <returns>A formatted instruction.</returns>
		public static string FormatInstruction (Instruction instruction)
		{
            using (StringWriter writer = new StringWriter(CultureInfo.CurrentCulture))
            {
                WriteInstruction(writer, instruction);
                return writer.ToString();
            }
		}

        /// <summary>
        /// Formats the method body.
        /// </summary>
        /// <param name="method">The method.</param>
        /// <returns>A formatted method body.</returns>
		public static string FormatMethodBody (MethodDefinition method)
		{
            if (method == null)
                throw new ArgumentNullException("method");

            using (StringWriter writer = new StringWriter(CultureInfo.CurrentCulture))
            {
                WriteMethodBody(writer, method);
                return writer.ToString();
            }
		}

        /// <summary>
        /// Writes the method body of the <paramref name="method"/> to the <paramref name="writer"/>.
        /// </summary>
        /// <param name="writer">The writer.</param>
        /// <param name="method">The method.</param>
		public static void WriteMethodBody (TextWriter writer, MethodDefinition method)
		{
            if (writer == null)
                throw new ArgumentNullException("writer");

            if (method == null)
                throw new ArgumentNullException("method");


			writer.WriteLine (method.ToString ());
			foreach (Instruction instruction in method.Body.Instructions) {
				writer.Write ('\t');
				WriteInstruction (writer, instruction);
				writer.WriteLine ();
			}
		}

        /// <summary>
        /// Writes the instruction to the <paramref name="writer"/>.
        /// </summary>
        /// <param name="writer">The writer.</param>
        /// <param name="instruction">The instruction.</param>
		public static void WriteInstruction (TextWriter writer, Instruction instruction)
		{
            if (writer == null)
                throw new ArgumentNullException("writer");

            if (instruction == null)
                throw new ArgumentNullException("instruction");

			writer.Write (FormatLabel (instruction.Offset));
			writer.Write (": ");
			writer.Write (instruction.OpCode.Name);
			if (null != instruction.Operand) {
				writer.Write (' ');
				WriteOperand (writer, instruction.Operand);
			}
		}

        /// <summary>
        /// Formats the label.
        /// </summary>
        /// <param name="offset">The offset.</param>
        /// <returns>A formatted IL label.</returns>
		private static string FormatLabel (int offset)
		{
			string label = "000" + offset.ToString ("x", CultureInfo.CurrentCulture);
			return "IL_" + label.Substring (label.Length - 4);
		}

        /// <summary>
        /// Writes the operand to the <paramref name="writer"/>.
        /// </summary>
        /// <param name="writer">The writer.</param>
        /// <param name="operand">The operand.</param>
        /// <exception cref="ArgumentNullException">Thrown when the <paramref name="operand"/> is <see langword="null"/>.</exception>
		private static void WriteOperand (TextWriter writer, object operand)
		{
			if (null == operand) throw new ArgumentNullException ("operand");

			Instruction targetInstruction = operand as Instruction;
			if (null != targetInstruction) {
				writer.Write (FormatLabel (targetInstruction.Offset));
				return;
			}

            VariableReference variableRef = operand as VariableReference;
            if (null != variableRef)
            {
                writer.Write(variableRef.Index.ToString(CultureInfo.CurrentCulture));
                return;
            }

            MethodReference methodRef = operand as MethodReference;
			if (null != methodRef) {
				WriteMethodReference (writer, methodRef);
				return;
			}

			string s = operand as string;
			if (null != s) {
				writer.Write ("\"" + s + "\"");
				return;
			}

			s = ToInvariantCultureString (operand);
			writer.Write (s);
		}

        /// <summary>
        /// Convert a value to an invariant culture string.
        /// </summary>
        /// <param name="value">The value.</param>
        /// <returns></returns>
		public static string ToInvariantCultureString (object value)
		{
            if (value == null)
                throw new ArgumentNullException("value"); 

			IConvertible convertible = value as IConvertible;
			return (null != convertible)
				? convertible.ToString (System.Globalization.CultureInfo.InvariantCulture)
				: value.ToString ();
		}

        /// <summary>
        /// Writes the method reference specified by the <paramref name="method"/> parameter to the <paramref name="writer"/>.
        /// </summary>
        /// <param name="writer">The writer.</param>
        /// <param name="method">The method.</param>        
		private static void WriteMethodReference (TextWriter writer, MethodReference method)
		{
			writer.Write (FormatTypeReference (method.ReturnType.ReturnType));
			writer.Write (' ');
			writer.Write (FormatTypeReference (method.DeclaringType));
			writer.Write ("::");
			writer.Write (method.Name);
			writer.Write ("(");
			ParameterDefinitionCollection parameters = method.Parameters;
			for (int i=0; i < parameters.Count; ++i) {
				if (i > 0) writer.Write (", ");
				writer.Write (FormatTypeReference (parameters [i].ParameterType));
			}
			writer.Write (")");
		}

        /// <summary>
        /// Formats the type reference.
        /// </summary>
        /// <param name="type">The type.</param>
        /// <returns>A formatted type reference.</returns>
		public static string FormatTypeReference (TypeReference type)
		{
			string typeName = type.FullName;
			switch (typeName) {
			case "System.Void": return "void";
			case "System.String": return "string";
			case "System.Int32": return "int32";
			case "System.Long": return "int64";
			case "System.Boolean": return "bool";
			case "System.Single": return "float32";
			case "System.Double": return "float64";
			}
			return typeName;
		}

        /// <summary>
        /// Initializes a new instance of the <see cref="T:CecilFormatter"/> class.
        /// </summary>
		private CecilFormatter ()
		{
		}
	}
}
