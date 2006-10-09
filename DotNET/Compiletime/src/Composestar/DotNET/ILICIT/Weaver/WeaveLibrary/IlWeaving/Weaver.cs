using System;
using System.Collections;
using System.Text;

using Weavers.ExitCodes;
using Weavers.IlStructures;
using Weavers.WeaveSpecifications; 

namespace Weavers.IlWeaving
{
	/// <summary>
	/// 
	/// </summary>
	enum CallContext : int
	{
		ApplicationStart = 0,
		BeforeInstantiation = 1,
		AfterInstantiation = 2,
		Call = 3,
		BeforeFieldAccess = 4,
		AfterFieldAccess = 5
	};

	/// <summary>
	/// This class performs all transformations (weaving) of CIL code.
	/// </summary>
	public class Weaver
	{
		private bool mQuiet = false;
		private bool mDebug = false;

		// Hold the value for the next CIL label, to ensure every opcode inside a method has a unique label.
		long NextLabel = 0;

		int indCallArguments = -1;
		int indTemporaryObject = -1;
		int indCreatedObject = -1;

		IlOpcode tempIlOpcode = null;

		Hashtable addedExternalAssemblies = null;

		// Constructor
		public Weaver(bool quiet, bool debug)
		{
			this.mQuiet = quiet;
			this.mDebug = debug;

			addedExternalAssemblies = new Hashtable();
		}

		/// <summary>
		/// Creates a list of opcodes pushing the selected argument onto the stack.
		/// </summary>
		/// <param name="argument"></param>
		/// <param name="calledClass"></param>
		/// <param name="calledMethod"></param>
		/// <param name="callConventions"></param>
		/// <param name="containingMethod"></param>
		/// <param name="argumentTypes"></param>
		/// <returns>ArrayList of IlOpcode objects</returns>
		private ArrayList PushArgument(ArgumentInformation argument, string calledClass, string calledMethod, string callConventions, int callContext, string containingClass, ref MethodBlock containingMethod, ref ArrayList argumentTypes, bool debug)
		{
			ArrayList result = new ArrayList();

			if (debug) Console.WriteLine("Creating CIL instruction for method parameter '" + argument.Value + "'.");

			switch (argument.Value)
			{
				case "%calledtype":
					argumentTypes.Add("string");
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "ldstr", "\"" + calledClass + "\""));
					break;
				case "%createdobject":
					argumentTypes.Add("object");
					if (indCreatedObject >= 0)
					{
						result.Add(CreateLoadOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), indCreatedObject));
					}
					else 
					{
						result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "ldnull"));
					}
					break;
				case "%senderobject":
					if (containingMethod.IsStatic()) 
					{
						argumentTypes.Add("string");
						result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "ldstr", "\"" + containingClass + "\""));
					}
					else 
					{
						argumentTypes.Add("object");
						result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "ldarg.0"));
					}
					break;
				case "%targetname":
					argumentTypes.Add("string");
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "ldstr", "\"" + calledClass + "::" + calledMethod + "\""));
					break;
				case "%targetobject":
					if (callConventions.IndexOf("instance") >= 0) 
					{
						// Call is made to an instance, we already stored the target object
						argumentTypes.Add("object");
						result.Add(CreateLoadOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), indTemporaryObject));
					}
					else 
					{
						// Call is not made to an instance, argument will be the name of the target object
						argumentTypes.Add("string");
						result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "ldstr", "\"" + calledClass + "\""));
					}
					break;
				case "%targetmethod":
					argumentTypes.Add("string");
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "ldstr", "\"" + calledMethod + "\""));
					break;
				case "%originalparameters":
					argumentTypes.Add("object[]");
					result.Add(CreateLoadOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), indCallArguments));
					break;
				case "%casttarget":
					argumentTypes.Add("string");
					break;
				case "%fieldvalue":
					argumentTypes.Add("object");
					if (this.tempIlOpcode.Opcode.Equals("ldfld")) 
					{
						result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "ldarg.0"));
					}
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), this.tempIlOpcode.Opcode, this.tempIlOpcode.Argument));
					break;
				default:
					argumentTypes.Add(argument.Type);
					if (!argument.Value.Equals("")) 
					{
						if (argument.Type.Equals("string"))
						{
							result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "ldstr", "\"" + argument.Value + "\""));
						}
						else if (argument.Type.Equals("int32")) 
						{
							result.Add(CreateLiteralOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), int.Parse(argument.Value)));
						}
					}
					// TODO: add more types here
					break;
			}

			return result;
		}

		private IlOpcode CreateSimpleCallStatement(MethodInformation mi, WeaveSpecification ws, ref MethodBlock callerMethod)
		{
			string call = mi.ReturnType + " ";
				
			if ( !mi.AssemblyName.Equals("") ) 
			{
				call += "[" + mi.AssemblyName + "]";
			}

			ArrayList argumentTypes = new ArrayList();
			IEnumerator enumArguments = mi.GetArgumentEnumerator();
			while (enumArguments.MoveNext()) 
			{
				PushArgument(((ArgumentInformation)enumArguments.Current), "", "", "instance", 0, "", ref callerMethod, ref argumentTypes, false);
			}
			string args = "(" + String.Join(",", (string[])argumentTypes.ToArray(typeof(string))) + ")";

			call += mi.FullClassName + "::" + mi.Name + args;

			Console.WriteLine(call);

			// Add the referenced assembly to the list of added assemblies.
			if ( !this.addedExternalAssemblies.ContainsKey(mi.AssemblyName)) 
			{
				AssemblyInformation ai = ws.GetAssemblyInformation(mi.AssemblyName);
				
				if (ai != null) 
				{
					this.addedExternalAssemblies.Add(mi.AssemblyName, ai);
				}
			}

			return new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "call", call);
		}

		/// <summary>
		/// Creates the opcodes for a method call
		/// </summary>
		/// <param name="ws"></param>
		/// <param name="mi"></param>
		/// <param name="calledAssembly"></param>
		/// <param name="calledClass"></param>
		/// <param name="calledMethod"></param>
		/// <param name="methodArgs"></param>
		/// <param name="callConventions"></param>
		/// <param name="callerMethod"></param>
		/// <param name="quiet"></param>
		/// <param name="debug"></param>
		/// <returns>ArrayList of IlOpcode objects</returns>
		private ArrayList CreateCallStatement(WeaveSpecification ws, MethodInformation mi, string calledAssembly, string calledClass, string calledMethod, string methodArgs, string callConventions, int callContext, string callerClass, ref MethodBlock callerMethod, bool quiet, bool debug)
		{
			ArrayList result = new ArrayList();
			ArrayList argumentTypes = new ArrayList();

			IEnumerator enumArguments = mi.GetArgumentEnumerator();
			while (enumArguments.MoveNext()) 
			{
				result.AddRange(PushArgument(((ArgumentInformation)enumArguments.Current), calledClass, calledMethod, callConventions, callContext, callerClass, ref callerMethod, ref argumentTypes, debug));
			}

			string methodCall = mi.FullClassName + "::" + mi.Name;
			if (!mi.AssemblyName.Equals("")) 
			{
				methodCall = methodCall.Insert(0, "[" + mi.AssemblyName + "]");
			}
			if (mi.GetArgumentCount() == 0) 
			{
				methodCall += "()";
			}
			else 
			{
				methodCall += "(" + String.Join(",", (string[])argumentTypes.ToArray(typeof(string))) + ")";
			}

			string returntype = "";
			bool castReturntype = false;
			if (!calledMethod.Equals("_ctor")) 
			{
				if (callConventions.IndexOf("instance") >= 0) 
				{
					returntype = callConventions.Remove(callConventions.IndexOf("instance"), 8).Trim();
				}
				else 
				{
					returntype = callConventions.Trim();
				}

				if (returntype.Equals("void") || returntype.Equals("")) 
				{
					methodCall = methodCall.Insert(0, "void ");
				}
				else if (mi.ReturnType.Equals(""))
				{
					// Keep the original returntype
					methodCall = methodCall.Insert(0, returntype + " ");
				}
				else 
				{
					// Insert the returntype from the weavespec file
					if (this.IsValueType(mi.ReturnType)) 
					{
						methodCall = methodCall.Insert(0, mi.ReturnType + " ");
					}
					else if (mi.ReturnType.Equals("object"))
					{
						methodCall = methodCall.Insert(0, "object ");
						castReturntype = true;
					}
					else if (mi.ReturnType.Equals("string"))
					{
						methodCall = methodCall.Insert(0, "string ");
					}
					else 
					{
						methodCall = methodCall.Insert(0, "class " + mi.ReturnType + " ");
						
						if (callConventions.IndexOf("instance") >= 0) 
						{
							methodCall = methodCall.Insert(0, "instance ");
						}
					}
				}
			}
			else 
			{
				methodCall = methodCall.Insert(0, "void ");
			}

			if (debug) Console.WriteLine("Creating CIL instruction for call '" + methodCall + "'.");
			result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "call", methodCall));

			// Add the referenced assembly to the list of added assemblies.
			if ( !this.addedExternalAssemblies.ContainsKey(mi.AssemblyName)) 
			{
				AssemblyInformation ai = ws.GetAssemblyInformation(mi.AssemblyName);
				
				if (ai != null) 
				{
					this.addedExternalAssemblies.Add(mi.AssemblyName, ai);
				}
			}

			// If the return type has to be casted back create the IL code to do so
			if (castReturntype) 
			{
				result.AddRange(CreateCastOpcode(returntype));
			}

			return result;
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="returntype"></param>
		/// <returns></returns>
		private ArrayList CreateCastOpcode(string returntype)
		{
			ArrayList result = new ArrayList();
				
			if (IsValueType(returntype)) 
			{
				// Unbox to value type
				if (returntype.Equals("bool")) 
				{
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "unbox", GetFulltypeForValuetype(returntype)));
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "ldind.u1"));
				}
				else if (returntype.Equals("wchar")) 
				{
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "unbox", GetFulltypeForValuetype(returntype)));
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "ldind.u2"));
				}
				else if (returntype.Equals("string")) 
				{
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "castclass", returntype));
				}
				else if (returntype.Equals("int8"))
				{
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "unbox", GetFulltypeForValuetype(returntype)));
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "ldind.i1"));
				}
				else if (returntype.Equals("int16"))
				{
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "unbox", GetFulltypeForValuetype(returntype)));
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "ldind.i2"));
				}
				else if (returntype.Equals("int32"))
				{
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "unbox", GetFulltypeForValuetype(returntype)));
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "ldind.i4"));
				}
				else if (returntype.Equals("int64"))
				{
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "unbox", GetFulltypeForValuetype(returntype)));
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "ldind.i8"));
				}
				else if (returntype.Equals("float32"))
				{
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "unbox", GetFulltypeForValuetype(returntype)));
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "ldind.r4"));
				}
				else if (returntype.Equals("float64"))
				{
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "unbox", GetFulltypeForValuetype(returntype)));
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "ldind.r8"));
				}
				else if (returntype.Equals("unsigned int8"))
				{
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "unbox", GetFulltypeForValuetype(returntype)));
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "ldind.u1"));
				}
				else if (returntype.Equals("unsigned int16"))
				{
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "unbox", GetFulltypeForValuetype(returntype)));
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "ldind.u2"));
				}
				else if (returntype.Equals("unsigned int32"))
				{
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "unbox", GetFulltypeForValuetype(returntype)));
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "ldind.u4"));
				}
				else if (returntype.Equals("unsigned int64"))
				{
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "unbox", GetFulltypeForValuetype(returntype)));
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "ldind.u8"));
				}
			}
			else 
			{
				result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "castclass", returntype));
			}

			return result;
		}

		/// <summary>
		/// Creates a load opcode for the requested local variable. Short forms are used for locals 0 to 3.
		/// </summary>
		/// <param name="label"></param>
		/// <param name="indLocal"></param>
		/// <returns>IlOpcode with the requested load instruction.</returns>
		private IlOpcode CreateLoadOpcode(string label, int indLocal)
		{
			string loadOpcode = "";
			string loadArgument = "";
			switch (indLocal) 
			{
				case 0:
					loadOpcode = "ldloc.0";
					break;
				case 1:
					loadOpcode = "ldloc.1";
					break;
				case 2:
					loadOpcode = "ldloc.2";
					break;
				case 3:
					loadOpcode = "ldloc.3";
					break;
				default:
					loadOpcode = "ldloc";
					loadArgument = indLocal.ToString();
					break;
			}

			return new IlOpcode(label, loadOpcode, loadArgument);
		}

		/// <summary>
		/// Creates a store opcode for the requested local variable. Short forms are used for locals 0 to 3.
		/// </summary>
		/// <param name="label"></param>
		/// <param name="indLocal"></param>
		/// <returns>IlOpcode with the requested store instruction.</returns>
		private IlOpcode CreateStoreOpcode(string label, int indLocal)
		{
			string storeOpcode = "";
			string storeArgument = "";
			switch (indLocal) 
			{
				case 0:
					storeOpcode = "stloc.0";
					break;
				case 1:
					storeOpcode = "stloc.1";
					break;
				case 2:
					storeOpcode = "stloc.2";
					break;
				case 3:
					storeOpcode = "stloc.3";
					break;
				default:
					storeOpcode = "stloc";
					storeArgument = indLocal.ToString();
					break;
			}

			return new IlOpcode(label, storeOpcode, storeArgument);
		}

		/// <summary>
		/// Determines if the specified type is a valuetype.
		/// </summary>
		/// <param name="type"></param>
		/// <returns>True, if 'type' is a value type.</returns>
		private bool IsValueType(string type)
		{
			if (GetFulltypeForValuetype(type) != null) 
			{
				return true;
			}

			return false;
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="type"></param>
		/// <returns></returns>
		private string GetFulltypeForValuetype(string type)
		{
			string fullType = "";

			switch (type) 
			{
				case "bool":
					fullType = "[mscorlib]System.Boolean";
					break;
				case "char":
					fullType = "[mscorlib]System.Char";					
					break;
				case "float32":
					fullType = "[mscorlib]System.Single";
					break;
				case "float64":
					fullType = "[mscorlib]System.Double";
					break;
				case "int8":
					fullType = "[mscorlib]System.SByte";
					break;
				case "int16":
					fullType = "[mscorlib]System.Short";
					break;
				case "int32":
					fullType = "[mscorlib]System.Int32";
					break;
				case "int64":
					fullType = "[mscorlib]System.Int64";
					break;
				case "unsigned int8":
					fullType = "[mscorlib]System.Byte";
					break;
				case "unsigned int16":
					fullType = "[mscorlib]System.UInt16";
					break;
				case "unsigned int32":
					fullType = "[mscorlib]System.UInt32";
					break;
				case "unsigned int64":
					fullType = "[mscorlib]System.UInt64";
					break;
				default:
					return null;
			}

			return fullType;
		}

		/// <summary>
		/// Creates a box instruction for the specified type.
		/// </summary>
		/// <param name="label"></param>
		/// <param name="type"></param>
		/// <returns>IlOpcode with the boxing instruction.</returns>
		private IlOpcode CreateBoxOpcode(string label, string type)
		{
			return new IlOpcode(label, "box", GetFulltypeForValuetype(type));
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="label"></param>
		/// <param name="type"></param>
		/// <returns></returns>
		private IlOpcode CreateUnboxOpcode(string label, string type)
		{
			return new IlOpcode(label, "unbox", GetFulltypeForValuetype(type));
		}

		/// <summary>
		/// Creates a load literal of type i4 instruction for the specified literal.
		/// </summary>
		/// <param name="label"></param>
		/// <param name="literal"></param>
		/// <returns>IlOpcode with the load instruction.</returns>
		private IlOpcode CreateLiteralOpcode(string label, int literal)
		{
			string literalOpcode = "";
			string literalArgument = "";
			switch (literal) 
			{
				case 0:
					literalOpcode = "ldc.i4.0";
					break;
				case 1:
					literalOpcode = "ldc.i4.1";
					break;
				case 2:
					literalOpcode = "ldc.i4.2";
					break;
				case 3:
					literalOpcode = "ldc.i4.3";
					break;
				case 4:
					literalOpcode = "ldc.i4.4";
					break;
				case 5:
					literalOpcode = "ldc.i4.5";
					break;
				case 6:
					literalOpcode = "ldc.i4.6";
					break;
				case 7:
					literalOpcode = "ldc.i4.7";
					break;
				case 8:
					literalOpcode = "ldc.i4.8";
					break;
				default:
					literalOpcode = "ldc.i4";
					literalArgument = literal.ToString();
					break;
			}

			return new IlOpcode(label, literalOpcode, literalArgument);
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="method"></param>
		private void CreateLocalVariables(ref MethodBlock method)
		{
			if (!method.HasParameterLocals)
			{
				// Create local for object array
				method.AddLocal("object[] parameterstore");
				indCallArguments = method.CountLocals()-1;

				// Create local for temporary object
				method.AddLocal("object tempobject");
				indTemporaryObject = method.CountLocals()-1;

				method.InitLocals = true;
				method.HasParameterLocals = true;
			}
		}

		/// <summary>
		/// Creates a list of opcodes to store the arguments of a call.
		/// </summary>
		/// <param name="ws"></param>
		/// <param name="calledAssembly"></param>
		/// <param name="calledClass"></param>
		/// <param name="calledMethod"></param>
		/// <param name="methodArgs"></param>
		/// <param name="callerMethod"></param>
		/// <param name="quiet"></param>
		/// <param name="debug"></param>
		/// <returns>ArrayList of IlOpcode objects</returns>
		private ArrayList ProcessCallArguments(WeaveSpecification ws, string calledAssembly, string calledClass, string calledMethod, string methodArgs, ref MethodBlock callerMethod, bool quiet, bool debug)
		{
			ArrayList result = new ArrayList();

			if (debug) Console.WriteLine("Store original parameters for method '" + calledClass + "::" + calledMethod + "'.");

			CreateLocalVariables(ref callerMethod);

			string[] methodArgsArray = methodArgs.Split(',');

			if (!methodArgs.Equals("")) 
			{
				// Opcodes to create the object array 
				result.Add(CreateLiteralOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), methodArgsArray.Length));
				result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "newarr", "[mscorlib]System.Object"));
				result.Add(CreateStoreOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), indCallArguments));
			
				// Process all the arguments
				for (int i=methodArgsArray.Length-1; i >=0; i--) 
				{
					if (IsValueType(methodArgsArray[i]))
					{
						// This argument is a valuetype, it has to be boxed!
						result.Add(CreateBoxOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), methodArgsArray[i]));
					}
					
					// Store argument value to temporary object local
					result.Add(CreateStoreOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), indTemporaryObject));
					
					// Load array
					result.Add(CreateLoadOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), indCallArguments));
					
					// Push array index onto stack
					result.Add(CreateLiteralOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), i));
				
					// Load argument value from temporary object local
					result.Add(CreateLoadOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), indTemporaryObject));

					// Store argument in array
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "stelem.ref"));
				
				}
			}
			else 
			{
				// Create an empty array
				result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "ldc.i4.0"));
				result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "newarr", "[mscorlib]System.Object"));
				result.Add(CreateStoreOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), indCallArguments));
			}

			return result;
		}

		/// <summary>
		/// Updates branch statements, it can happen that short branch statement are no longer valid. Fortunately
		/// the Microsoft .NET Framework IL Assembler will insert short forms if possible.
		/// </summary>
		/// <param name="code"></param>
		/// <returns></returns>
		private ArrayList VerifyBranchStatements(ArrayList code, bool debug)
		{
			if (debug) Console.WriteLine("Modifying branch statements (i.e. removing all the short forms)");
		
			ArrayList branchInstructions = new ArrayList(14);
			branchInstructions.AddRange	(new string[] {"beq.s","bge.s","bge.un.s","bgt.s","bgt.un.s",
														  "ble.s","ble.un.s","blt.s","blt.un.s","bne.un.s","br.s","brfalse.s","brtrue.s","leave.s"});

			for (int i=0; i < code.Count; i++) 
			{
				if (branchInstructions.Contains(((IlOpcode)code[i]).Opcode)) 
				{
					((IlOpcode)code[i]).Opcode = ((IlOpcode)code[i]).Opcode.Substring(0, ((IlOpcode)code[i]).Opcode.Length-2);
				}
			}

			return code;
		}

		private ArrayList ProcessClassInstantiation(WeaveSpecification ws, IlOpcode code, string callConventions, string calledAssembly, string calledClass, string calledMethod, string methodArgs, ref ClassBlock callerClass, ref MethodBlock callerMethod, ref int stackIncrease, bool quiet, bool debug)
		{
			ArrayList result = new ArrayList();

			if (ws.NeedsClassInstantiation(calledClass))
			{
				if (debug) Console.WriteLine("Processing instantiation of class [" + calledAssembly + "]" + calledClass + "...");

				string methodBefore = ws.GetClassInstantiation(calledClass).MethodBefore;
				if (!methodBefore.Equals(""))
				{
					MethodInformation mi = ws.GetMethodInformation(methodBefore);
					if (mi != null) 
					{
						result.AddRange(CreateCallStatement(ws, mi, calledAssembly, calledClass, calledMethod, methodArgs, "before", (int)CallContext.BeforeInstantiation, callerClass.Name, ref callerMethod, quiet, debug));
					}
				}
				string codeBefore = ws.GetClassInstantiation(calledClass).CodeBlockBefore;
				if (!codeBefore.Equals(""))
				{
					// TODO: Insert codeblock before class instantiation
				}


				// Load after invocation information and store constructor args if necesarry.
				string methodAfter = ws.GetClassInstantiation(calledClass).MethodAfter;
				if (!methodAfter.Equals(""))
				{
					MethodInformation mi = ws.GetMethodInformation(methodAfter);
					if (mi != null) 
					{
						if (mi.HasArgument("%originalparameters")) 
						{
							result.AddRange(ProcessCallArguments(ws, calledAssembly, calledClass, calledMethod, methodArgs, ref callerMethod, quiet, debug));
							if (!methodArgs.Equals("")) result.AddRange(PushArgumentsFromArray(methodArgs));
						}
					}
				}

				// Add call to constructor
				result.Add(code);

				if (!methodAfter.Equals(""))
				{
					MethodInformation mi = ws.GetMethodInformation(methodAfter);
					if (mi != null) 
					{
						// Store newly created object
						if (indCreatedObject < 0) 
						{
							// Create local for created object
							callerMethod.AddLocal("object createdobject");
							indCreatedObject = callerMethod.CountLocals()-1;
						}
						result.Add(CreateStoreOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), indCreatedObject));
						
						result.AddRange(CreateCallStatement(ws, mi, calledAssembly, calledClass, calledMethod, methodArgs, callConventions, (int)CallContext.AfterInstantiation, callerClass.Name, ref callerMethod, quiet, debug));
						if (mi.HasArgument("%originalparameters") || mi.HasArgument("%createdobject"))
						{
							// Load newly created object and cast it back to the right type (we stored it in an System.Object)
							result.Add(CreateLoadOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), indCreatedObject));
							string newobj = calledClass;
							if (!calledAssembly.Equals("")) 
							{
								newobj = "[" + calledAssembly + "]" + newobj;
							}
							result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "castclass", newobj));
						}
						//result = VerifyBranchStatements(result, debug);
						if (mi.GetArgumentCount() > stackIncrease ) 
						{
							stackIncrease = mi.GetArgumentCount();
						}

					}
				}
			}
			else 
			{
				result.Add(code);
			}

			return result;
		}

		private void RemoveCallArguments(WeaveSpecification ws, string calledAssembly, string calledClass, string calledMethod, string methodArgs, ref MethodBlock callerMethod, bool quiet, bool debug)
		{
			// TODO: implement this method, not necessary for Compose*
		}

		private ArrayList ProcessMethodInvocation(WeaveSpecification ws, IlOpcode code, string callConventions, string calledAssembly, string calledClass, string calledMethod, string methodArgs, ref ClassBlock callerClass, ref MethodBlock callerMethod, ref int stackIncrease, bool quiet, bool debug)
		{
			ArrayList result = new ArrayList();
			MethodInformation invocation = ws.GetMethodInvocation(callerClass.FullName, calledAssembly, calledClass, calledMethod);
			if (invocation != null)
			{
				// A method invocation is defined for this call
				if (debug) Console.WriteLine("Processing invocation of method [" + calledAssembly + "]" + calledClass + "::" + calledMethod + "(" + methodArgs + ")...");

				MethodInformation redirectTo = null;
				if (callConventions.IndexOf("void") >= 0) 
				{
					redirectTo = ws.GetMethodInformation(invocation.VoidRedirectTo);
				}
				else 
				{
					redirectTo = ws.GetMethodInformation(invocation.ReturnValueRedirectTo);
				}

				if (redirectTo != null) 
				{
					// Process original call arguments
					if (redirectTo.HasArgument("%originalparameters"))
					{
						// Save the arguments
						result.AddRange(ProcessCallArguments(ws, calledAssembly, calledClass, calledMethod, methodArgs, ref callerMethod, quiet, debug));
					}
					else 
					{
						// Remove the arguments
						RemoveCallArguments(ws, calledAssembly, calledClass, calledMethod, methodArgs, ref callerMethod, quiet, debug);
					}

					// If the call is made to an instance, save this instance
					if (callConventions.IndexOf("instance") >= 0 && redirectTo.GetArgumentCount() > 0) 
					{
						if (!callerMethod.HasParameterLocals)
						{
							CreateLocalVariables(ref callerMethod);
						}
						result.Add(CreateStoreOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), indTemporaryObject));
					}
					else if (callConventions.IndexOf("instance") >= 0 && redirectTo.GetArgumentCount() == 0)
					{
						// Pop the loaded instance object from the stack
						result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "pop"));
					}

					ArrayList newCallStatement = CreateCallStatement(ws, redirectTo, calledAssembly, calledClass, calledMethod, methodArgs, callConventions, (int)CallContext.Call, callerClass.Name, ref callerMethod, quiet, debug);
					if (newCallStatement.Count == 0) 
					{
						result.Add(code);
					}
					else 
					{
						// Replace first label with the original label if replacing something (keep jumps valid)
						((IlOpcode)newCallStatement[0]).Label = code.Label;
					
						result.AddRange(newCallStatement);
						//result = VerifyBranchStatements(result, debug);
						if ( redirectTo.GetArgumentCount() > stackIncrease ) 
						{
							stackIncrease = redirectTo.GetArgumentCount();
						}
					}
				}
				else 
				{
					result.Add(code);
				}
			}
			else 
			{
				// Keep the original method call, no modifications needed
				// TODO: in case of instance call, push instance back on stack
				result.Add(code);
			}

			return result;
		}

		private ArrayList ProcessInheritedMethodInvocation(WeaveSpecification ws, IlOpcode code, string callConventions, string calledAssembly, string calledClass, string calledMethod, string methodArgs, ref ClassBlock callerClass, ref MethodBlock callerMethod, ref int stackIncrease, bool quiet, bool debug)
		{
			ArrayList result = new ArrayList();
			MethodInformation invocation = ws.GetMethodInvocation(callerClass.FullName, "*", "*parent*", calledMethod);
			if (invocation != null)
			{
				// A method invocation is defined for this call
				if (debug) Console.WriteLine("Processing invocation of method [" + calledAssembly + "]" + calledClass + "::" + calledMethod + "(" + methodArgs + ")...");

				MethodInformation redirectTo = null;
				if (callConventions.IndexOf("void") >= 0) 
				{
					redirectTo = ws.GetMethodInformation(invocation.VoidRedirectTo);
				}
				else 
				{
					redirectTo = ws.GetMethodInformation(invocation.ReturnValueRedirectTo);
				}

				if (redirectTo != null) 
				{
					// Process original call arguments
					if (redirectTo.HasArgument("%originalparameters"))
					{
						// Save the arguments
						result.AddRange(ProcessCallArguments(ws, calledAssembly, calledClass, calledMethod, methodArgs, ref callerMethod, quiet, debug));
					}
					else 
					{
						// Remove the arguments
						RemoveCallArguments(ws, calledAssembly, calledClass, calledMethod, methodArgs, ref callerMethod, quiet, debug);
					}

					// If the call is made to an instance, save this instance
					if (callConventions.IndexOf("instance") >= 0 && redirectTo.GetArgumentCount() > 0) 
					{
						if (!callerMethod.HasParameterLocals)
						{
							CreateLocalVariables(ref callerMethod);
						}
						result.Add(CreateStoreOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), indTemporaryObject));
					}
					else if (callConventions.IndexOf("instance") >= 0 && redirectTo.GetArgumentCount() == 0)
					{
						// Pop the loaded instance object from the stack
						result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "pop"));
					}

					ArrayList newCallStatement = CreateCallStatement(ws, redirectTo, calledAssembly, calledClass, calledMethod, methodArgs, callConventions, (int)CallContext.Call, callerClass.Name, ref callerMethod, quiet, debug);
					if (newCallStatement.Count == 0) 
					{
						result.Add(code);
					}
					else 
					{
						// Replace first label with the original label if replacing something (keep jumps valid)
						((IlOpcode)newCallStatement[0]).Label = code.Label;

						result.AddRange(newCallStatement);
						//result = VerifyBranchStatements(result, debug);
						if ( redirectTo.GetArgumentCount() > stackIncrease ) 
						{
							stackIncrease = redirectTo.GetArgumentCount();
						}
					}
				}
				else 
				{
					result.Add(code);
				}
			}
			else 
			{
				// Keep the original method call, no modifications needed
				// TODO: in case of instance call, push instance back on stack
				result.Add(code);
			}

			return result;
		}

		private ArrayList ProcessCast(WeaveSpecification ws, IlOpcode code, ref ClassBlock callerClass, ref MethodBlock callerMethod, ref int stackIncrease, bool quiet, bool debug)
		{
			ArrayList result = new ArrayList();
	
			String targetAssembly = "";
			String targetClass = "";

			if (code.Argument.IndexOf("]") >= 0) 
			{
				targetAssembly = code.Argument.Substring(code.Argument.IndexOf("[") + 1, code.Argument.IndexOf("]") - code.Argument.IndexOf("[") - 1);
				targetClass = code.Argument.Substring(code.Argument.IndexOf("]") + 1);
			}
			else 
			{
				targetClass = code.Argument;
			}

			CastInterceptionInformation cii = ws.GetCastInterception(callerClass.FullName, targetAssembly, targetClass);

			if ( cii != null )
			{
				result.Add( new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "ldstr", "\""+code.Argument+"\"") );
				result.Add( CreateSimpleCallStatement(ws.GetMethodInformation(cii.ExecuteMethodBefore), ws, ref callerMethod) );
				result.Add( code );

				stackIncrease = 1;
			}

			return result;
		}

		private ArrayList ProcessFieldAccess(WeaveSpecification ws, IlOpcode code, ref ClassBlock callerClass, ref MethodBlock callerMethod, ref int stackIncrease, bool quiet, bool debug)
		{
			ArrayList result = new ArrayList();
	
			string fieldName = code.Argument.Substring(code.Argument.LastIndexOf("::") + 2, code.Argument.Length - code.Argument.LastIndexOf("::") - 2);
			string className = code.Argument.Substring(code.Argument.LastIndexOf(" ") + 1, code.Argument.IndexOf("::") - code.Argument.LastIndexOf(" ") - 1);
			
			FieldInformation fi = ws.GetFieldAccessDefinition(callerClass.Name, className, fieldName);
			if (fi != null) 
			{
				if (!fi.ExecuteMethodBefore.Equals("")) 
				{
					this.tempIlOpcode = code;
					result.AddRange(this.CreateCallStatement(ws, ws.GetMethodInformation(fi.ExecuteMethodBefore), "", "", "", "", "void", (int)CallContext.AfterFieldAccess, callerClass.Name, ref callerMethod, quiet, debug));
					callerMethod.MaxStack += 1;
					if ( fi.ExecuteMethodAfter.Equals("") && fi.ExecuteMethodReplace.Equals("") ) result.Add(code);
					this.tempIlOpcode = null;
				}

				if (!fi.ExecuteMethodReplace.Equals("")) 
				{
					result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "pop"));
					this.tempIlOpcode = code;
					MethodInformation mi = ws.GetMethodInformation(fi.ExecuteMethodReplace);
					result.AddRange(this.CreateCallStatement(ws, mi, "", "", "", "", mi.ReturnType, (int)CallContext.AfterFieldAccess, callerClass.Name, ref callerMethod, quiet, debug));
					callerMethod.MaxStack += 1;
					this.tempIlOpcode = null;
				}

				if (!fi.ExecuteMethodAfter.Equals("")) 
				{
					if ( fi.ExecuteMethodReplace.Equals("") ) result.Add(code);
					this.tempIlOpcode = code;
					result.AddRange(this.CreateCallStatement(ws, ws.GetMethodInformation(fi.ExecuteMethodAfter), "", "", "", "", "void", (int)CallContext.AfterFieldAccess, callerClass.Name, ref callerMethod, quiet, debug));
					callerMethod.MaxStack += 1;	
					this.tempIlOpcode = null;
				}
			}
			
			if (result.Count == 0)
			{
				result.Add(code);
			}

			return result;
		}

		private ArrayList ProcessStaticFieldAccess(WeaveSpecification ws, IlOpcode code, ref ClassBlock callerClass, ref MethodBlock callerMethod, ref int stackIncrease, bool quiet, bool debug)
		{
			ArrayList result = new ArrayList();
	
			// TODO: implement this function!
			result.Add(code);

			return result;
		}

		private string GetAttributeArgumentsDefinitionString(AttributeModificationInformation ami)
		{
			string result = "";

			IEnumerator enumArguments = ami.GetArgumentEnumerator();
			while (enumArguments.MoveNext())
			{
				if (!result.Equals("")) 
				{
					result += ", ";
				}
                result += ((DictionaryEntry)enumArguments.Current).Key;
			}

            return result;
		}

		private string GetAttributeArgumentsString(AttributeModificationInformation ami)
		{
			string result = "01 00 ";  // prolog

			UTF8Encoding utf8 = new UTF8Encoding();

			IEnumerator enumArguments = ami.GetArgumentEnumerator();
			while (enumArguments.MoveNext())
			{
				DictionaryEntry argument = (DictionaryEntry)enumArguments.Current;
				
				if ( argument.Key.Equals("string") )
				{
					// Argument is of type string, creating the packed utf8 string
					Byte[] encodedBytes = utf8.GetBytes((string)argument.Value);

					if (encodedBytes.Length < 128) 
					{
						result += String.Format("{0:x2} ", BitConverter.GetBytes(encodedBytes.Length)[0]);
					}
					else if (encodedBytes.Length >= 128 && encodedBytes.Length < 16383)
					{
						result += String.Format("{0:x2} {1:x2} ", BitConverter.GetBytes(encodedBytes.Length+32768)[1], BitConverter.GetBytes(encodedBytes.Length+32768)[0]);
					}

					for (int i=0; i < encodedBytes.Length; i++) 
					{
						result += String.Format("{0:x2} ", encodedBytes[i]);
					}			
				}
				else if ( argument.Key.Equals("int32") )
				{
					// Argument is of type integer
					Byte[] bytes = BitConverter.GetBytes(int.Parse(argument.Value.ToString()));

					for (int i=0; i < bytes.Length; i++)
					{
						result += String.Format("{0:x2} ", bytes[i]);
					}
				}
				else if ( argument.Key.Equals("int64") )
				{
					// Argument is of type integer
					Byte[] bytes = BitConverter.GetBytes(Int64.Parse(argument.Value.ToString()));

					for (int i=0; i < bytes.Length; i++)
					{
						result += String.Format("{0:x2} ", bytes[i]);
					}
				}

			}
			
			
			return result.ToUpper() + "00 00";
		}

		/// <summary>
		/// Relabels the current opcode and insert a nop. This will make sure the jump targets 
		/// will not be messed up.
		/// </summary>
		/// <param name="code"></param>
		/// <param name="newCodes"></param>
		private void relabelOpcode(IlOpcode code, ArrayList newCodes)
		{
			newCodes.Add(new IlOpcode(code.Label, ""));
			code.Label = code.Label+"_";
		}

		/// <summary>
		/// Processes a CIL class block.
		/// </summary>
		/// <param name="ws"></param>
		/// <param name="namespaceName"></param>
		/// <param name="block"></param>
		/// <param name="quiet"></param>
		/// <param name="debug"></param>
		/// <returns>Modified ClassBlock.</returns>
		private ClassBlock ProcessClassBlock(WeaveSpecification ws, string namespaceName, ClassBlock block, bool quiet, bool debug)
		{
			ArrayList methods = block.GetMethodDeclarations();
			int stackIncrease = 0;

			if (debug) Console.WriteLine("Processing class '" + block.FullName + "'.");
            
			// Search for replacement definitions for this class
			ArrayList replacements = new ArrayList();
			if ( ws.HasReplacementDefined(block.Name) ) 
			{
				replacements = ws.GetReplacementDefinitions(block.Name);
				block.Replace(replacements, ref this.addedExternalAssemblies, ws);
			}	

			IEnumerator enumMethods = methods.GetEnumerator();
			while (enumMethods.MoveNext())
			{
				MethodBlock method = (MethodBlock)enumMethods.Current;

				// Add attributes
//block.FullName 
//				method.AddAttribute("// added attribute goes here!");
				ArrayList attributes = ws.GetAttributeDefinitions(block.FullName, method.Name);
				if ( attributes.Count > 0 ) 
				{
					IEnumerator enumAttributes = attributes.GetEnumerator();
					while (enumAttributes.MoveNext())
					{
						AttributeModificationInformation attribute = (AttributeModificationInformation)enumAttributes.Current;
						
						AttributeInformation attributedef = ws.GetAttributeDefinition(attribute.AttibuteNamae);
						if (attributedef != null) 
						{
							method.AddAttribute(".custom instance void [" + attributedef.AssemblyName + "]" + attributedef.FullClassName + "::.ctor(" + GetAttributeArgumentsDefinitionString(attribute) + ") = ( " + GetAttributeArgumentsString(attribute) + " )");
						}
					}
				}

				NextLabel = 0;
				indCreatedObject = -1;
				indTemporaryObject = -1;

				ArrayList newCodes = new ArrayList();

				if (method.EntryPoint && !ws.ApplicationInfo.EntrypointMethod.Equals("")) 
				{
					// If necessary make a call to the defined application start method
					newCodes.AddRange(CreateCallStatement(ws, ws.GetMethodInformation(ws.ApplicationInfo.EntrypointMethod), "", "", "", "", "", (int)CallContext.ApplicationStart, block.Name, ref method, quiet, debug));
					stackIncrease = ws.GetMethodInformation(ws.ApplicationInfo.EntrypointMethod).GetArgumentCount();
				}

				IEnumerator enumCodes = method.GetIlCodeEnumerator();
				while (enumCodes.MoveNext())
				{
					string callConventions = "";
					string calledAssembly = "";
					string calledClass = "";
					string calledMethod = "";
					string methodArgs = "";

					IlOpcode code = (IlOpcode)enumCodes.Current;

					// Parse opcode argument
					bool success = true;
					if (code.Opcode.Equals("newobj") || code.Opcode.Equals("callvirt") || code.Opcode.Equals("call")) 
					{
						methodArgs = code.Argument.Substring(code.Argument.LastIndexOf("(")+1, code.Argument.LastIndexOf(")")-code.Argument.LastIndexOf("(")-1);

						string argumentToSplit = code.Argument.Substring(0, code.Argument.IndexOf("("));
						
						string[] splitArgument = argumentToSplit.Split(' ');
						string fullCalledMethod = splitArgument[splitArgument.Length-1];

						if (fullCalledMethod.IndexOf("[") >= 0 ) 
						{
							calledAssembly = fullCalledMethod.Substring(fullCalledMethod.IndexOf("[")+1, fullCalledMethod.IndexOf("]")-fullCalledMethod.IndexOf("[")-1);
							calledClass = fullCalledMethod.Substring(fullCalledMethod.IndexOf("]")+1, fullCalledMethod.IndexOf("::")-fullCalledMethod.IndexOf("]")-1);
						}
						else if (fullCalledMethod.IndexOf("::") >= 0)
						{
							calledClass = fullCalledMethod.Substring(0, fullCalledMethod.IndexOf("::"));
							calledClass = calledClass.Remove(0, calledClass.LastIndexOf(" ")+1);
						}
						else 
						{
							if (debug) Console.WriteLine("Unable to process argument '" + code.Argument + "' for opcode '" + code.Opcode + "'!");
							success = false;
						}

						if (calledAssembly.Equals("") && !calledClass.Equals("")) 
						{
							callConventions = code.Argument.Substring(0, code.Argument.IndexOf(calledClass)).Trim();
						}
						else 
						{
							callConventions = code.Argument.Substring(0, code.Argument.IndexOf("[" + calledAssembly + "]" + calledClass + "::"));
						}

						calledMethod = code.Argument.Substring(code.Argument.IndexOf("::")+2, code.Argument.LastIndexOf("(")-code.Argument.IndexOf("::")-2);
					}

					if (success) 
					{
						switch (code.Opcode)
						{
							// Detect class instantiations
							case "newobj":
								relabelOpcode(code, newCodes);
								newCodes.AddRange(ProcessClassInstantiation(ws, code, callConventions, calledAssembly, calledClass, calledMethod, methodArgs, ref block, ref method, ref stackIncrease, quiet, debug));
								break;

							// Detect method invocation
							case "call":
								relabelOpcode(code, newCodes);
								newCodes.AddRange(ProcessMethodInvocation(ws, code, callConventions, calledAssembly, calledClass, calledMethod, methodArgs, ref block, ref method, ref stackIncrease, quiet, debug));
								break;
							
							case "callvirt":
								relabelOpcode(code, newCodes);
								ArrayList insert = ProcessMethodInvocation(ws, code, callConventions, calledAssembly, calledClass, calledMethod, methodArgs, ref block, ref method, ref stackIncrease, quiet, debug);
								if (insert.Count == 1) 
								{
									// Search for possible inherited method calls
									newCodes.AddRange(ProcessInheritedMethodInvocation(ws, code, callConventions, calledAssembly, calledClass, calledMethod, methodArgs, ref block, ref method, ref stackIncrease, quiet, debug));
								}
								else 
								{
									newCodes.AddRange(insert);
								}
								
								break;

							// Detect load of field of an object
							case "ldfld":
								relabelOpcode(code, newCodes);
								insert = ProcessFieldAccess(ws, code, ref block, ref method, ref stackIncrease, quiet, debug);
								if (insert.Count > 0)
								{
									if ( ((IlOpcode)insert[0]).Opcode.Equals("pop") )
									{
										newCodes.RemoveAt(newCodes.Count - 1);
										insert.RemoveAt(0);
									}
								}
								newCodes.AddRange(insert);
								break;

							// Detect static load of field of an object
							case "ldsfld":
								relabelOpcode(code, newCodes);
								newCodes.AddRange(ProcessStaticFieldAccess(ws, code, ref block, ref method, ref stackIncrease, quiet, debug));
								break;

							// Detect casting
							case "castclass":
								relabelOpcode(code, newCodes);
								newCodes.AddRange(ProcessCast(ws, code, ref block, ref method, ref stackIncrease, quiet, debug));
								break;

							// Add original opcode by default
							default:
								newCodes.Add(code);
								break;
						}
					}
					else 
					{
						newCodes.Add(code);
					}
				}

				// MaxStack is increased with the number of arguments for the inserted call, worst case approach
				method.MaxStack += stackIncrease; // TODO: we need a final stack scan to calculate the exact stacksize
				if (stackIncrease > 0) 
				{
					newCodes = this.VerifyBranchStatements(newCodes, debug);
				}
				method.ReplaceIlCode(newCodes);
			}

			return block;
		}

		private ArrayList PushArgumentsFromArray(string args)
		{
			ArrayList result = new ArrayList();
			string[] arglist = args.Split(',');
			int i = 0;
			foreach (string arg in arglist)
			{
				result.Add(CreateLoadOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), indCallArguments));
				
				result.Add(CreateLiteralOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), i));
			
				result.Add(new IlOpcode("PW_" + Convert.ToString(NextLabel++, 16).PadLeft(4, '0'), "ldelem.ref"));
				
				string goodarg = "";
				if (arg.IndexOf("class") >= 0)
				{
					goodarg = arg.Substring(arg.IndexOf("class")+6, arg.Length - arg.IndexOf("class") - 6);
				}
				else 
				{
					goodarg = arg;
				}
				
				result.AddRange(CreateCastOpcode(goodarg));

				i++;
			}

			return result;
		}

		/// <summary>
		/// Processes a CIL namespace block.
		/// </summary>
		/// <param name="ws"></param>
		/// <param name="block"></param>
		/// <param name="quiet"></param>
		/// <param name="debug"></param>
		/// <returns>Modified NamespaceBlock.</returns>
		private NamespaceBlock ProcessNamespaceBlock(WeaveSpecification ws, NamespaceBlock block, bool quiet, bool debug)
		{
			IEnumerator enumClasses = block.GetClassEnumarator();
			ArrayList updatedClasses = new ArrayList();

			if (debug) Console.WriteLine("Processing namespace '" + block.Namespace + "'.");
			
			while (enumClasses.MoveNext()) 
			{
				ClassBlock theClass = (ClassBlock)enumClasses.Current;

				if (theClass.HasMethodDeclarations())
				{
					theClass = ProcessClassBlock(ws, block.Namespace, theClass, quiet, debug);
					updatedClasses.Add(theClass);
				}
				else 
				{
					ArrayList replacements = new ArrayList();
					if ( ws.HasReplacementDefined(theClass.Name) ) 
					{
						replacements = ws.GetReplacementDefinitions(theClass.Name);
						theClass.Replace(replacements, ref this.addedExternalAssemblies, ws);
					}
				}
			}
			
			// Store updated classes
			enumClasses = updatedClasses.GetEnumerator();
			while (enumClasses.MoveNext())
			{
				block.UpdateClass((ClassBlock)enumClasses.Current);	
			}

			return block;
		}

		/// <summary>
		/// Processes an abstract CIL representation.
		/// </summary>
		/// <param name="ws"></param>
		/// <param name="IlCode"></param>
		/// <param name="quiet"></param>
		/// <param name="debug"></param>
		/// <returns>Modified ArrayList with abstract CIL block.</returns>
		public ArrayList Process(WeaveSpecification ws, ArrayList IlCode, bool quiet, bool debug)
		{
			if (!quiet) Console.WriteLine("Updating IL code conform the weave specifications...");
			
			string currentAssembly = "";
			this.addedExternalAssemblies = new Hashtable();
			ArrayList usedExternalAssemblies = new ArrayList();
			ArrayList usedExternalAssemblyBlocks = new ArrayList();
			ArrayList newExternalAssemblies = new ArrayList();
			int firstExternalAssembly = -1;

			for (int i=0; i < IlCode.Count; i++)
			{
				if (!IlCode[i].GetType().ToString().Equals("System.String"))
				{
					// Object in IlCode is of type IlStructure
					IlStructure structure = (IlStructure)IlCode[i];
					if (structure.GetType().ToString().Equals("Weavers.IlStructures.ExternalAssemblyBlock")) 
					{
						// Add the assembly name to the list of used assemblies, if it's not defined for removal!
						AssemblyInformation ai = ws.GetAssemblyInformation(((ExternalAssemblyBlock)IlCode[i]).Name);
						if ( ai != null ) 
						{
							if ( !ai.Remove ) 
							{
								usedExternalAssemblies.Add(((ExternalAssemblyBlock)IlCode[i]).Name);
								usedExternalAssemblyBlocks.Add(structure);
							}
						}
						else 
						{
							usedExternalAssemblies.Add(((ExternalAssemblyBlock)IlCode[i]).Name);
							usedExternalAssemblyBlocks.Add(structure);
						}

						// See if this is the last external assembly reference
						if (firstExternalAssembly == -1)
						{
							firstExternalAssembly = i;
						}
						structure = null;
					}
					else if (structure.GetType().ToString().Equals("Weavers.IlStructures.NamespaceBlock"))
					{
						structure = ProcessNamespaceBlock(ws, (NamespaceBlock)structure, quiet, debug);
					}
					else if (structure.GetType().ToString().Equals("Weavers.IlStructures.ClassBlock"))
					{
						//Console.WriteLine("?????");
						structure = ProcessClassBlock(ws, "", (ClassBlock)structure, quiet, debug);
					}

					IlCode[i] = structure;
				}
				else 
				{
					// Search for assembly name
					// TODO: can't this be done some where else in a nicer way, eg. a special structure for it
					if ( ((String)IlCode[i]).StartsWith(".assembly") )
					{
						currentAssembly = ((String)IlCode[i]).Substring(".assembly ".Length);
					}
				}
			}

			// Remove 'null' structures
			while ( IlCode.Contains(null) )
			{
				IlCode.Remove(null);
			}

			// Add external assembly references
			IEnumerator enumAssemblies = ws.GetAssemblyEnumerator();
			while (enumAssemblies.MoveNext()) 
			{
				AssemblyInformation ai = ((AssemblyInformation)((DictionaryEntry)enumAssemblies.Current).Value);

				// Check for a forced reference to this assembly
				bool forceReference = false;
				if ( !ai.ForceReferenceIn.Equals("") ) 
				{
					string[] forceReferencesIn = ai.ForceReferenceIn.Split(',');
					for (int i=0; i < forceReferencesIn.Length; i++) 
					{
						if (forceReferencesIn[i].Equals(currentAssembly)) 
						{
							forceReference = true;
							break;
						}
					}
				}

				if ( (!usedExternalAssemblies.Contains(ai.Name) && addedExternalAssemblies.Contains(ai.Name) && !ai.Remove) || forceReference ) 
				{
					ExternalAssemblyBlock newAssemblyReference = new ExternalAssemblyBlock(ai.Name);
					newAssemblyReference.Version = ai.Version.Replace(".", ":");
					newExternalAssemblies.Add(newAssemblyReference);
				}
			}

			if (firstExternalAssembly == -1) firstExternalAssembly = 0; 
			IlCode.InsertRange(firstExternalAssembly, usedExternalAssemblyBlocks);
			IlCode.InsertRange(firstExternalAssembly + usedExternalAssemblies.Count, newExternalAssemblies);

			return IlCode;
		}
	}
}
