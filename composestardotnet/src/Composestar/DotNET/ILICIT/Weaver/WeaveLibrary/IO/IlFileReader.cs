using System;
using System.Collections;
using System.IO;

using Weavers.IlStructures;

namespace Weavers.IO
{
	/// <summary>
	/// Summary description for IlFileInterpreter.
	/// </summary>
	public class IlFileReader
	{
		private String mIlFile = "";
		private bool mQuiet = false;
		private bool mDebug = false;

		public IlFileReader(bool quiet, bool debug)
		{
			this.mQuiet = quiet;
			this.mDebug = debug;
		}

		private ArrayList ReadBlock(StreamReader srIn)
		{
			ArrayList result = new ArrayList();
			int brackets = 0;
			string line = "";

			while ((line = srIn.ReadLine()) != null)
			{
				if (line.StartsWith("{")) brackets += 1;
				if (line.StartsWith("}")) brackets -= 1;

				result.Add(line);

				if (brackets == 0 && line.StartsWith("}")) break;
			}

			return result;
		}

		private ExternalAssemblyBlock GetExternalAssemblyBlock(string header, StreamReader srIn)
		{
			string assemblyName = header.Remove(0, ".assembly extern ".Length);
			ExternalAssemblyBlock result = new ExternalAssemblyBlock(assemblyName);

			ArrayList block = ReadBlock(srIn);
			block.RemoveAt(0); // Remove { at beginning of block
			block.RemoveAt(block.Count-1); // Remove } at end of block

			for (int i=0; i < block.Count; i++)
			{
				string line = block[i].ToString().Trim();

				if (line.StartsWith(".publickeytoken "))
				{
					result.PublicKeyToken = line.Remove(0, ".publickeytoken = ".Length);
				}
				else if (line.StartsWith(".ver "))
				{
					result.Version = line.Remove(0, ".ver ".Length);
				}
				else
				{
					result.AddAdditionalCodeLine(line);
				}
			}

			return result;
		}

		private IlOpcode GetIlOpcodeStatement(string line, StreamReader srIn)
		{
			if (line.StartsWith(".line "))
			{
				char[] sep = {':'};
				string[] two = line.Split(sep,2);
				return new IlOpcode(two[0], two[1]);
			}
			if (line.IndexOf(": ") > 0) 
			{
				string label = line.Substring(0, line.IndexOf(": "));
				string opcode = line.Remove(0, line.IndexOf(": ")+1).TrimStart();
				if (opcode.IndexOf(" ") > 0) opcode = opcode.Remove(opcode.IndexOf(" "), opcode.Length - opcode.IndexOf(" "));

				IlOpcode result = new IlOpcode(label, opcode);

				// If this opcode has an argument, parse the argument
				if (line.IndexOf(opcode)+opcode.Length < line.Length) 
				{
					string argument = line.Substring(line.IndexOf(opcode)+opcode.Length, line.Length-line.IndexOf(opcode)-opcode.Length).Trim();
					if (line.IndexOf("(") > 0 && line.IndexOf(")") < 0 && !(argument.IndexOf("\"") == 0) && (argument.LastIndexOf("\"") != argument.Length-1) )
					{
						while (line.LastIndexOf(")") != line.Length-1)
						{
							// argument has been split into multiple lines
							line = srIn.ReadLine();
							argument += line.Trim();
						}
					}
					
					result.Argument = argument;
				}
				
				// niet vergeten om argument GOED op te zoeken, dubbele regel

				return result;
			}
			else if (line.Trim().StartsWith("+")) 
			{
				// String values are sometimes split in multiple lines, additional lines start with '+ sign
				IlOpcode result = new IlOpcode("NONE", "NONE", line);
				return result;

			}

			return null;
		}

		private MethodBlock GetMethodBlock(string header, StreamReader srIn)
		{
			string line = "";
			while ((line = srIn.ReadLine().Trim()) != "{")
			{
				header += " " + line;
			}
			string methodName = header.Remove(header.IndexOf("("), header.Length-header.IndexOf("("));  // Remove part after method name
			methodName = methodName.Remove(0, methodName.LastIndexOf(" ")+1);  // Remove part before method name

			MethodBlock result = new MethodBlock(methodName, header.Remove(0, ".method ".Length));

			if (this.mDebug && !this.mQuiet) Console.WriteLine("Reading method '" + result.Name + "'.");

			int brackets = 1;
			while ((line = srIn.ReadLine().Trim()) != null)
			{
				if (line.StartsWith("{")) brackets += 1;
				if (line.StartsWith("}")) brackets -= 1;

				if (line.StartsWith(".entrypoint"))
				{
					result.EntryPoint = true;
				}
				else if (line.StartsWith(".maxstack "))
				{
					result.MaxStack = int.Parse(line.Remove(0, ".maxstack ".Length));
				}
				else if (line.StartsWith(".locals"))
				{
					if (line.IndexOf("init") > 0) result.InitLocals = true;
					string locals = "";
					line = line.Remove(0, line.IndexOf("(")+1);
					while (line.LastIndexOf(")") == -1)
					{
						if (line.Length > 0) { locals += line.Trim(); }
						line = srIn.ReadLine();
					}
					locals += line.Trim();
					locals = locals.Remove(locals.Length-1, 1);
					if (this.mDebug && !this.mQuiet) Console.WriteLine("Found locals: " + locals);
					result.AddLocals(locals.Split(','));
				}
				else if (line.StartsWith(".custom"))
				{
					// Attributes
					string attribute = "";

					// Removing comment at end of line
					if (line.IndexOf("//") > 0) 
					{
						attribute = line.Substring(0, line.IndexOf("//")).Trim();
					}
					else
					{
						attribute = line;
					}
					//result.AddAdditionalCodeLine(line);
					int bracketCount = BracketCount(line);
					while (bracketCount > 0) 
					{
						// Read an additional line
						line = srIn.ReadLine().Trim();

						// Removing comment at end of line
						if (line.IndexOf("//") > 0) 
						{
							line = line.Substring(0, line.IndexOf("//"));
						}

						attribute = String.Concat(attribute, " ", line.Trim());
						//result.AddAdditionalCodeLine(line);
						bracketCount += BracketCount(line);
					}
					result.AddAttribute(attribute);
				}
				else if (line.StartsWith(".try"))
				{
					result.AddTryCodeLine(line);
				}
				else
				{
					IlOpcode opcode = this.GetIlOpcodeStatement(line, srIn);
					if (opcode != null) result.AddIlCode(opcode);
				}

				if (brackets == 0 && line.StartsWith("}")) break;
			}

			return result;
		}

		private int BracketCount(String line)
		{
			int result = 0;

			for (int i=0; i < line.Length; i++)
			{
				if (line[i] == '(') result++;
				else if (line[i] == ')') result--;
			}

			return result;
		}

		private ClassBlock GetClassBlock(string namespaceName, string header, StreamReader srIn)
		{
			string className = header.Substring(header.LastIndexOf(" ")+1, header.Length - header.LastIndexOf(" ") - 1);
            ClassBlock result = new ClassBlock(className);
			result.Namespace = namespaceName;
			result.Attributes = header.Substring(".class ".Length, header.LastIndexOf(" ")-".class ".Length);

			int brackets = 0;
			string line = "";

			if (this.mDebug && !this.mQuiet) Console.WriteLine("Reading class '" + className + "'.");

			while ((line = srIn.ReadLine().Trim()) != null)
			{
				if (line.StartsWith("{")) brackets += 1;
				if (line.StartsWith("}")) brackets -= 1;

				if (line.StartsWith("extends "))
				{
					result.Extends = line.Remove(0, "extends ".Length);
				}
				else if (line.StartsWith(".class "))
				{
					result.AddChild(GetClassBlock("", line, srIn));
				}
				else if (line.StartsWith(".method "))
				{
					result.AddChild(GetMethodBlock(line, srIn));
				}
				else 
				{
					result.AddChild(line);
				}

				if (brackets == 0 && line.StartsWith("}")) break;
			}

			return result;
		}

		private NamespaceBlock GetNamespaceBlock(string header, StreamReader srIn)
		{
			string namespaceName = header.Remove(0, ".namespace ".Length);
			NamespaceBlock result = new NamespaceBlock(namespaceName);

			int brackets = 0;
			string line = "";

			if (this.mDebug && !this.mQuiet) Console.WriteLine("Reading namespace '" + namespaceName + "'.");

			while ((line = srIn.ReadLine().Trim()) != null)
			{
				if (line.StartsWith("{")) brackets += 1;
				if (line.StartsWith("}")) brackets -= 1;

				if (line.StartsWith(".class "))
				{
					result.AddClass(GetClassBlock(namespaceName, line, srIn));
				}

				if (brackets == 0 && line.StartsWith("}")) break;
			}

			return result;
		}

		public ArrayList Read(String file)
		{
			this.mIlFile = file;

			// Create arraylist to store il code
			ArrayList codeList = new ArrayList();

			if (!this.mQuiet)
			{
				Console.WriteLine("Loading disassembled IL code from file '"+file+"'...");
			}

			// Open streamreader on il file
			StreamReader sr = new StreamReader(file, System.Text.Encoding.UTF8);

			string line = "";
			while ((line = sr.ReadLine()) != null)
			{
				if (line.ToLower().StartsWith(".assembly extern "))
				{
					// External assembly reference
					codeList.Add(GetExternalAssemblyBlock(line, sr));
				}
				else if (line.ToLower().StartsWith(".namespace "))
				{
					// 
					codeList.Add(GetNamespaceBlock(line, sr));
				}
				else if (line.ToLower().StartsWith(".class "))
				{
					// 
					codeList.Add(GetClassBlock("", line, sr));
				}
				else 
				{
					codeList.Add(line);
				}
			}

			sr.Close();

			return codeList;
		}
	}
}
