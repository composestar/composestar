using System;
using System.IO;
using System.Text;
using System.Reflection;
using System.Collections;
using System.CodeDom.Compiler;

using DDW.CSharp.Dom;
using DDW.CSharp.Walk;
using DDW.CSharp.SymbolTable;

namespace DDW.CSharp.Gen
{		
	public class CSharpGen : GraphParserBase
	{
		LineWriter sb;
		private bool termStmt = true;
		private bool addSpace = true;
		private bool isInterface = false;

		public CSharpGen(TextWriter tw)
		{
			sb = new LineWriter(tw); 
		}
		

		#region CompileUnit *
		public override void ParseElement(CompileUnit gr)
		{
			if(gr.Imports.Count != 0)
			{
				Parse(gr.Imports);
				sb.WriteLine("");
			}
			if(gr.AssemblyCustomAttributes.Count != 0)
			{
				Parse(gr.AssemblyCustomAttributes);
				sb.WriteLine("");
			}
			Parse(gr.Namespaces);
		}
		#endregion
		#region Import *
		public override void ParseElement(Import gr)
		{
			sb.Write("using "); 
			if(gr.Alias != null) 
				sb.Write(gr.Alias + " = ");
			sb.WriteLine(gr.Namespace + ";");
		}
		#endregion
		#region NamespaceDecl *
		public override void ParseElement(NamespaceDecl gr)
		{
			AttributeState.Namespace = gr.Name;

			if (gr.Name == "$DefaultNamespace")
			{
				Parse(gr.Imports);
				Parse(gr.Types);
			}
			else
			{
				sb.Write("namespace " + gr.Name);

				OpenBlock();
				Parse(gr.Imports);
				Parse(gr.Types);
				CloseBlock();
			}
		}
		#endregion

		#region MethodDecl *
		public override void ParseElement(MethodDecl gr)
		{
			AttributeState.Method = gr.Name;
			AttributeState.Type = AttributeState.TargetType.TargetMethod;
			Parse(gr.Comments);
			Parse(gr.CustomAttributes);
			Parse(gr.ReturnTypeCustomAttributes);

			ParseElement(gr.Attributes);
			Parse(gr.ReturnType);
			sb.Write(gr.Name);
			Parse(gr.Parameters); 
			if(	isInterface || 
				(int)(gr.Attributes & Modifiers.Abstract) > 0)
			{
				sb.WriteLine(";");
			}
			else
			{
				OpenBlock();
				IDefinition retDef = gr.ReturnType.Definition;
				/* Assign null to all out-parameters (because it is obligatory to assign *something*) */
				ParamDeclCollection parameterList = gr.Parameters;
				if (parameterList != null) // Does this method have parameters at all?
				{
					foreach (ParamDecl pd in parameterList)
					{
						// Only need to assign something if the parameter is an 'out'-parameter
						if (pd.Direction == ParamDirection.Out)
						{
							sb.Write(pd.Name + " = ");
							if (pd.Type.Definition is BuiltInDefinition)
							{ // If it's a builtin type, set it to something appropriate for that type
								switch (((BuiltInDefinition)pd.Type.Definition).LiteralType)
								{
									case LiteralType.Bool:
										sb.WriteLine("false;");
										break;
									case LiteralType.Byte:
									case LiteralType.Decimal:
									case LiteralType.Int:
									case LiteralType.Long:
									case LiteralType.Sbyte:
									case LiteralType.Short:
									case LiteralType.Uint:
									case LiteralType.Ulong:
									case LiteralType.Ushort:
										sb.WriteLine("0;");
										break;
									case LiteralType.Char:
										sb.WriteLine("'0';");
										break;
									case LiteralType.Double:
									case LiteralType.Float:
										sb.WriteLine("0.0;");
										break;
									case LiteralType.Null:
									case LiteralType.Object:
										sb.WriteLine("null;");
										break;
									case LiteralType.String:
										sb.WriteLine("\"\";");
										break;
									case LiteralType.Void:
									default:
										sb.WriteLine("null;"); // and hope this works...
										break;
								}
							}
							else
								sb.WriteLine("null;"); // Assign null to any normal type
						}
					}
				}

				if (gr.ReturnType.ArrayRanks.Count > 0) // It's an array def, so null is a valid return value
					sb.WriteLine("return null;");
				else if (retDef.GetType().Equals(typeof(BuiltInDefinition)))
				{ // Have to return a specific value for builtin types
					switch (((BuiltInDefinition)retDef).LiteralType)
					{
						case LiteralType.Bool:
							sb.WriteLine("return false;");
							break;
						case LiteralType.Byte:
						case LiteralType.Decimal:
						case LiteralType.Int:
						case LiteralType.Long:
						case LiteralType.Sbyte:
						case LiteralType.Short:
						case LiteralType.Uint:
						case LiteralType.Ulong:
						case LiteralType.Ushort:
							sb.WriteLine("return 0;");
							break;
						case LiteralType.Char:
							sb.WriteLine("return '0';");
							break;
						case LiteralType.Double:
						case LiteralType.Float:
							sb.WriteLine("return 0.0;");
							break;
						case LiteralType.Null:
						case LiteralType.Object:
							sb.WriteLine("return null;");
							break;
						case LiteralType.String:
							sb.WriteLine("return \"\";");
							break;
						case LiteralType.Void:
							break; // write nothing
						default:
							sb.WriteLine("return null;"); // and hope this works...
							break;
					}
				}
				else
				{ // For all 'normal' types, null should be a valid return value
					sb.WriteLine("return null;");
				}
				/* WH - Edit: remove contents of methods! */
				//Parse(gr.Statements);
				CloseBlock();
			}
		}
		#endregion
		#region FieldDecl *
		public override void ParseElement(FieldDecl gr)
		{
			Parse(gr.Comments);
			//AttributeCollector.Instance.Field = gr.Name;
			//AttributeCollector.Instance.Type = AttributeCollector.Instance.Field = AttributeCollector.TargetType.TargetField;
			Parse(gr.CustomAttributes);
			ParseElement(gr.Attributes);
			Parse(gr.Type);
			string comma = "";
			foreach(Declarator dl in gr.Delcarators)
			{
				sb.Write(comma);
				ParseElement(dl);
				comma = ", ";
			}
			sb.WriteLine(";");
		}
		#endregion
		#region PropertyDecl *
		public override void ParseElement(PropertyDecl gr)
		{
			Parse(gr.Comments);
			Parse(gr.CustomAttributes);
			ParseElement(gr.Attributes);
			Parse(gr.Type);
			sb.Write(gr.Name);
			OpenBlock();
			if(	isInterface ||
				(int)(gr.Attributes & Modifiers.Abstract) > 0)
			{
				if(gr.HasGet)sb.WriteLine("get;");
			}
			else
			{
				Parse(gr.GetAccessor);
			}
			if(	isInterface ||
				(int)(gr.Attributes & Modifiers.Abstract) > 0)
			{
				if(gr.HasSet)sb.WriteLine("set;");
			}
			else
			{
				Parse(gr.SetAccessor);
			}
			CloseBlock();
		}
		#endregion
		#region EventDecl *
		public override void ParseElement(EventDecl gr)
		{
			Parse(gr.Comments);
			Parse(gr.CustomAttributes);
			ParseElement(gr.Attributes);
			sb.Write("event ");
			Parse(gr.Type);
			string comma = "";
			foreach(Declarator dl in gr.Delcarators)
			{
				sb.Write(comma);
				ParseElement(dl); // will only be name part...
				comma = ", ";
			}
			if(gr.UsesAccessors)
			{
				// always uses both accessors
				OpenBlock();
				if(	isInterface ||
					(int)(gr.Attributes & Modifiers.Abstract) > 0)
				{
					sb.WriteLine("add;");
				}
				else
				{
					Parse(gr.AddAccessor);
				}
				if(	isInterface ||
					(int)(gr.Attributes & Modifiers.Abstract) > 0)
				{
					sb.WriteLine("remove;");
				}
				else
				{
					Parse(gr.RemoveAccessor);
				}
				CloseBlock();
			}
			else
			{
				sb.WriteLine(";");
			}
		}
		#endregion
		#region ConstantDecl *
		public override void ParseElement(ConstantDecl gr)
		{
			Parse(gr.Comments);
			Parse(gr.CustomAttributes);
			ParseElement(gr.Attributes);
			sb.Write("const ");
			Parse(gr.Type);
			string comma = "";
			foreach(Declarator dl in gr.Delcarators)
			{
				sb.Write(comma);
				ParseElement(dl);
				comma = ", ";
			}
			sb.WriteLine(";");
		}
		#endregion
		#region IndexerDecl *
		public override void ParseElement(IndexerDecl gr)
		{
			Parse(gr.Comments);
			Parse(gr.CustomAttributes);
			ParseElement(gr.Attributes);
			Parse(gr.Type);
			if(gr.InterfaceType != null)
				sb.Write(gr.InterfaceType.TypeName + ".");
			sb.Write("this[");
			Parse(gr.Parameters); 
			sb.Write("]");			
			OpenBlock();
			if(	isInterface ||
				(int)(gr.Attributes & Modifiers.Abstract) > 0)
			{
				if(gr.HasGet)sb.WriteLine("get;");
			}
			else
			{
				Parse(gr.GetAccessor);
			}
			if(	isInterface ||
				(int)(gr.Attributes & Modifiers.Abstract) > 0)
			{
				if(gr.HasSet)sb.WriteLine("set;");
			}
			else
			{
				Parse(gr.SetAccessor);
			}
			CloseBlock();
		}
		#endregion
		#region OperatorDecl *
		public override void ParseElement(OperatorDecl gr)
		{
			Parse(gr.Comments);
			Parse(gr.CustomAttributes);
			ParseElement(gr.Attributes);
			Parse(gr.Type);
			sb.Write("operator ");
			ParseElement(gr.Operator);
			// always one param
			sb.Write("(");
			Parse(gr.FirstParameter);
			if(gr.SecondParameter != null)
			{
				sb.Write(", ");
				Parse(gr.FirstParameter);
			}
			sb.Write(")");
			if(gr.Statements.Count > 0)
			{
				OpenBlock();
				Parse(gr.Statements);
				CloseBlock();
			}
			else
			{
				sb.WriteLine(";");
			}
		}
		#endregion
		#region ConstructorDecl *
		public override void ParseElement(ConstructorDecl gr)
		{
			Parse(gr.Comments);
			Parse(gr.CustomAttributes);

			ParseElement(gr.Attributes);
			sb.Write(gr.Name);
			Parse(gr.Parameters); 
			if(gr.InvokeBase)
			{
				sb.Write(": base(");
				Parse(gr.BaseParameters); 
				sb.Write(")");
			}
			else if(gr.InvokeChain)
			{
				sb.Write(": this(");
				Parse(gr.ChainParameters); 
				sb.Write(")");
			}
			OpenBlock();
			// WH: Edit - Remove actual contents of constructor code.
			//Parse(gr.Statements);
			CloseBlock();
		}
		#endregion
		#region DestructorDecl
		public override void ParseElement(DestructorDecl gr)
		{
			Parse(gr.Comments);
			Parse(gr.CustomAttributes);

			ParseElement(gr.Attributes);
			sb.Write(" ~ " + gr.Name + "( )");
			OpenBlock();
			Parse(gr.Statements);
			CloseBlock();
		}
		#endregion
		#region AccessorDecl *
		public override void ParseElement(AccessorDecl gr)
		{
			if(gr.AccessorModifier == AccessorModifiers.Empty) return;
			Parse(gr.Comments);
			Parse(gr.CustomAttributes);
            sb.Write(Enum.GetName(	gr.AccessorModifier.GetType(), 
									gr.AccessorModifier).ToLower() );
			OpenBlock();
			Parse(gr.Statements);
			CloseBlock();
		}
		#endregion
		#region EnumMemberDecl *
		public override void ParseElement(EnumMemberDecl gr)
		{
			Parse(gr.CustomAttributes);
			sb.Write(gr.Name);
			if(gr.Value != null)
			{
				sb.Write(" = ");
				Parse(gr.Value);
			}
		}
		#endregion

		#region ClassDecl *
		public override void ParseElement(ClassDecl gr)
		{
			Parse(gr.Comments);
			AttributeState.Class = gr.Name;
			AttributeState.Type = AttributeState.TargetType.TargetClass;
			Parse(gr.CustomAttributes);
			ParseElement(gr.Attributes);
			sb.Write("class " + gr.Name +" ");
			// Match class - store TypeLocation information
			// Store Current filename?
			Console.WriteLine("TypeLocation");
			Console.WriteLine(CSharpUI.CSharpDummyGenerator.Filename);
			Console.WriteLine(AttributeState.Target);

			Parse(gr.BaseTypes);
			OpenBlock();
			Parse(gr.Members);
			CloseBlock();
		}
		#endregion
		#region InterfaceDecl *
		public override void ParseElement(InterfaceDecl gr)
		{
			AttributeState.Class = gr.Name;
			AttributeState.Type = AttributeState.TargetType.TargetInterface;
			Parse(gr.CustomAttributes);
			ParseElement(gr.Attributes);
			sb.Write("interface " + gr.Name +" ");

			// Match interface - store TypeLocation information
			// Store Current filename?
			Console.WriteLine("TypeLocation");
			Console.WriteLine(CSharpUI.CSharpDummyGenerator.Filename);
			Console.WriteLine(AttributeState.Target);

			Parse(gr.BaseTypes);
			OpenBlock();

			isInterface = true;
			Parse(gr.Members);
			isInterface = false;

			CloseBlock();
		}
		#endregion
		#region StructDecl *
		public override void ParseElement(StructDecl gr)
		{
			Parse(gr.CustomAttributes);
			ParseElement(gr.Attributes);
			sb.Write("struct " + gr.Name +" ");
			Parse(gr.BaseTypes);
			OpenBlock();
			Parse(gr.Members);
			CloseBlock();
		}
		#endregion
		#region EnumDecl *
		public override void ParseElement(EnumDecl gr)
		{
			Parse(gr.CustomAttributes);
			ParseElement(gr.Attributes);
			sb.Write("enum " + gr.Name +" ");
			Parse(gr.BaseTypes);
			OpenBlock();
			string comma = "";
			foreach(EnumMemberDecl em in gr.Members)
			{
				if(comma != "") sb.WriteLine(comma);
				ParseElement(em);
				comma = ",";
			}
			sb.WriteLine("");
			CloseBlock();
		}
		#endregion
		#region DelegateDecl *
		public override void ParseElement(DelegateDecl gr)
		{
			Parse(gr.CustomAttributes);
			ParseElement(gr.Attributes);
			Parse(gr.ReturnType);
			sb.Write("delegate ");
			sb.Write(gr.Name);
			Parse(gr.Parameters); 
			sb.WriteLine(";");
			//Parse(gr.BaseTypes); //delegate has no base types
			//OpenBlock();
			//Parse(gr.Members); //delegate has no members
			//CloseBlock();

		}
		#endregion

		#region ExprStmt
		public override void ParseElement(ExprStmt gr)
		{
			Parse(gr.Expression);
			if(termStmt) sb.WriteLine(";");
		}
		#endregion
		#region CommentStmt
		public override void ParseElement(CommentStmt gr)
		{
			Parse(gr.Comment);			
			if(termStmt) sb.WriteLine("");
		}
		#endregion
		#region VariableDeclStmt *
		public override void ParseElement(VariableDeclStmt gr)
		{ 
			Parse(gr.Type);
			string comma = "";
			foreach(Declarator dl in gr.Delcarators)
			{
				sb.Write(comma);
				ParseElement(dl);
				comma = ", ";
			}
			if(termStmt) sb.WriteLine(";");
		}
		#endregion
		#region ConstantDeclStmt *
		public override void ParseElement(ConstantDeclStmt gr)
		{
			sb.Write("const ");
			Parse(gr.Type);
			string comma = "";
			foreach(Declarator dl in gr.Delcarators)
			{
				sb.Write(comma);
				ParseElement(dl);
				comma = ", ";
			}
			if(termStmt) sb.WriteLine(";");
		}
		#endregion
		#region IfStmt *
		public override void ParseElement(IfStmt gr)
		{
			// maybe check for one stmt, then don't use brackets...
			sb.Write("if(");
			Parse(gr.Condition);
			sb.Write(")");			
			if(gr.TrueStatements.Count > 0)
			{
				// dont use blocks for single statement
				if(gr.TrueStatements.Count > 1)
				{
					OpenBlock();
					Parse(gr.TrueStatements);
					CloseBlock();
				}
				else
				{
					sb.Indent++;
					sb.WriteLine("");
					Parse(gr.TrueStatements);
					sb.Indent--;
				}
				if(gr.FalseStatements.Count > 0)
				{
					sb.Write("else ");
					Parse(gr.FalseStatements);
				}
			}
			else
			{
				sb.Write(";");
			}
		}
		#endregion
		#region SwitchStmt *
		public override void ParseElement(SwitchStmt gr)
		{
			sb.Write("switch(");
			Parse(gr.Condition);
			sb.Write(")");
			OpenBlock();
            Parse(gr.Cases);
			CloseBlock();
		}
		#endregion
		#region Case *
		public override void ParseElement(Case gr)
		{
			if(gr.IsDefault)
			{
				sb.Write("default :");
			}
			else
			{
				sb.Write("case ");
				Parse(gr.Condition);
				sb.Write(" :");
			}
			OpenBlock();
            Parse(gr.Statements);
			CloseBlock();
		}
		#endregion
		#region IterationStmt *
		public override void ParseElement(IterationStmt gr)
		{
			if(gr.TestFirst)
			{
				termStmt = false;
				sb.Write("for(");
				if(gr.Init != null)
				{						
					string comma = "";
					foreach(Statement o in gr.Init)
					{
						sb.Write(comma);
						Parse(o);
						comma = ", ";
					}
				}
				sb.Write("; ");
				Parse(gr.Test);
				sb.Write("; ");
				if(gr.Increment != null)
				{						
					string comma = "";
					foreach(Statement o in gr.Increment)
					{
						sb.Write(comma);
						Parse(o);
						comma = ", ";
					}
				}
				sb.Write(")");
				termStmt = true;
				OpenBlock();
				Parse(gr.Statements);
				CloseBlock();					
			}
			else
			{
				sb.Write("do");
				OpenBlock();
				Parse(gr.Statements);
				CloseBlock();
				sb.Write("while(");
				Parse(gr.Test);
				sb.WriteLine(");");
			}
		}
		#endregion
		#region ForEachStmt
		public override void ParseElement(ForEachStmt gr)
		{
			sb.Write("foreach(");
			Parse(gr.Type);
			sb.Write(" " + gr.Name + " in ");
			Parse(gr.Collection);
			sb.Write(")");
			OpenBlock();
			Parse(gr.Statements);
			CloseBlock();			
		}
		#endregion
		#region GotoStmt
		public override void ParseElement(GotoStmt gr)
		{			
			sb.Write("goto " + gr.Label);
			if(gr.CaseLabel != null)
			{
				sb.Write(" " );
				Parse(gr.CaseLabel);
			}
			sb.WriteLine(";");
		}
		#endregion
		#region LabeledStmt
		public override void ParseElement(LabeledStmt gr)
		{
			int ind = sb.Indent;
			int tabSize = 4; // how to get this..?
			sb.Indent = 0;
			string lbl = gr.Label + ":";
			sb.Write(lbl.PadRight(ind * tabSize, ' ') );
			Parse(gr.Statement);
			sb.Indent = ind;
		}
		#endregion
		#region ReturnStmt
		public override void ParseElement(ReturnStmt gr)
		{
			sb.Write("return");
			if(gr.Expression != null)
			{
				sb.Write(" ");
				Parse(gr.Expression);
			}
			sb.WriteLine(";");
		}
		#endregion
		#region BreakStmt
		public override void ParseElement(BreakStmt gr)
		{
			sb.WriteLine("break;");
		}
		#endregion
		#region ContinueStmt
		public override void ParseElement(ContinueStmt gr)
		{
			sb.WriteLine("continue;");
		}
		#endregion
		#region CheckedStmt
		public override void ParseElement(CheckedStmt gr)
		{
			sb.Write("checked");
			OpenBlock();
			Parse(gr.Statements);
			CloseBlock();
		}
		#endregion
		#region UncheckedStmt
		public override void ParseElement(UncheckedStmt gr)
		{
			sb.Write("unchecked");
			OpenBlock();
			Parse(gr.Statements);
			CloseBlock();
		}
		#endregion
		#region LockStmt
		public override void ParseElement(LockStmt gr)
		{
			sb.Write("lock(");
			Parse(gr.Target);
			sb.Write(")");
			OpenBlock();
			Parse(gr.Statements);
			CloseBlock();
		}
		#endregion
		#region UsingStmt
		public override void ParseElement(UsingStmt gr)
		{
			sb.Write("using(");
			if(gr.DeclaresResource)
			{
				termStmt = false;
				Parse(gr.Declaration);
				termStmt = true;
			}
			else
			{
				Parse(gr.Target);
			}
			sb.Write(")");
			OpenBlock();
			Parse(gr.Statements);
			CloseBlock();
		}
		#endregion
		#region ThrowStmt
		public override void ParseElement(ThrowStmt gr)
		{
			sb.Write("throw(");
			Parse(gr.ToThrow);
			sb.WriteLine(");");
		}
		#endregion
		#region TryCatchFinallyStmt
		public override void ParseElement(TryCatchFinallyStmt gr)
		{
			sb.Write("try");
			OpenBlock();
			Parse(gr.Try);
			CloseBlock();
			Parse(gr.CatchClauses);
			Parse(gr.Finally);
		}
		#endregion
		#region Catch
		public override void ParseElement(Catch gr)
		{
			sb.Write("catch(");
			Parse(gr.CatchExceptionType);
			sb.Write(gr.LocalName);
			sb.Write(")");
			OpenBlock();
			Parse(gr.Statements);
			CloseBlock();
		}
		#endregion
		#region AttachDelegateStmt TODO: not parsing yet
		public override void ParseElement(AttachDelegateStmt gr)
		{
			//gr.Event;
			//gr.Listener;
		}
		#endregion
		#region RemoveDelegateStmt TODO: not parsing yet
		public override void ParseElement(RemoveDelegateStmt gr)
		{
		}
		#endregion

		#region AssignExpr
		public override void ParseElement(AssignExpr gr)
		{
			Parse(gr.Left);
			sb.Write(" " + AssignExpr.StringFromOperator(gr.Operator) + " ");
			Parse(gr.Right);
		}
		#endregion
		#region UnaryExpr
		public override void ParseElement(UnaryExpr gr)
		{
			sb.Write(UnaryExpr.StringFromOperator(gr.Operator));
			Parse(gr.Right);
		}
		#endregion
		#region BinaryExpr
		public override void ParseElement(BinaryExpr gr)
		{
			Parse(gr.Left);
			sb.Write(" " + BinaryExpr.StringFromOperator(gr.Operator) + " ");
			Parse(gr.Right);
		}
		#endregion
		#region Ternary
		public override void ParseElement(TernaryExpr gr)
		{
			Parse(gr.Test);
			sb.Write(" ? ");
			Parse(gr.True);
			sb.Write(" : ");
			Parse(gr.False);
		}
		#endregion
		#region CastExpr
		public override void ParseElement(CastExpr gr)
		{
			sb.Write("(");
			addSpace = false;
			Parse(gr.TargetType);
			addSpace = true;
			sb.Write(")");
			Parse(gr.Expression);
		}
		#endregion
		#region SubExpr
		public override void ParseElement(SubExpr gr)
		{
			sb.Write("(");
			addSpace = false;
			Parse(gr.Expression);
			addSpace = true;
			sb.Write(")");
		}
		#endregion
		#region UnknownReference
		public override void ParseElement(UnknownReference gr)
		{
			sb.Write(gr.VariableName);
		}
		#endregion
		#region ThisRef
		public override void ParseElement(ThisRef gr)
		{
			sb.Write("this");
		}
		#endregion
		#region BaseRef
		public override void ParseElement(BaseRef gr)
		{
			sb.Write("base");
		}
		#endregion
		#region PropertySetValueRef
		public override void ParseElement(PropertySetValueRef gr)
		{
			sb.Write("value");
		}
		#endregion
		#region ArgumentRef
		public override void ParseElement(ArgumentRef gr)
		{
			sb.Write(gr.ParameterName);
		}
		#endregion
		#region LocalRef
		public override void ParseElement(LocalRef gr)
		{
			sb.Write(gr.VariableName);
		}
		#endregion
		#region TypeOfExpr
		public override void ParseElement(TypeOfExpr gr)
		{
			sb.Write("typeof(");
			Parse(gr.Type);
			sb.Write(")");
		}
		#endregion
		#region FieldRef
		public override void ParseElement(FieldRef gr)
		{
			if(gr.Target != null)
			{
				Parse(gr.Target);
				sb.Write(".");
			}
			sb.Write(gr.FieldName);
		}
		#endregion
		#region ArrayElementRef
		public override void ParseElement(ArrayElementRef gr)
		{
			if(gr.Target != null)
			{
				Parse(gr.Target);
			}
			sb.Write("[");
			if(gr.Indices != null)
			{
				string comma = "";
				foreach(Expression o in gr.Indices)
				{
					sb.Write(comma);
					Parse(o);
					comma = ", ";
				}
			}
			sb.Write("]");

		}
		#endregion
		#region MethodRef
		public override void ParseElement(MethodRef gr)
		{
			if(gr.Target != null)
			{
				Parse(gr.Target);
			}
			else if(gr.MethodName != "")
				sb.Write(gr.MethodName);
		}
		#endregion
		#region PropertyRef
		public override void ParseElement(PropertyRef gr)
		{
			if(gr.Target != null)
			{
				Parse(gr.Target);
				sb.Write(".");
			}
			sb.Write(gr.PropertyName);
		}
		#endregion
		#region EventRef
		public override void ParseElement(EventRef gr)
		{
			if(gr.Target != null)
			{
				Parse(gr.Target);
				sb.Write(".");
			}
			sb.Write(gr.EventName);
		}
		#endregion
		#region MethodInvokeExpr
		public override void ParseElement(MethodInvokeExpr gr)
		{
			Parse(gr.Target);
			sb.Write("(");
			if(gr.Parameters != null)
			{
				string comma = "";
				foreach(Param o in gr.Parameters)
				{
					sb.Write(comma);
					Parse(o);
					comma = ", ";
				}
			}
			sb.Write(")");
		}
		#endregion
		#region PostfixExpr
		public override void ParseElement(PostfixExpr gr)
		{
			Parse(gr.Left);
			if(gr.Operator == PostfixOperator.Increment)
				sb.Write("++");
			else if(gr.Operator == PostfixOperator.Decrement)
				sb.Write("--");
		}
		#endregion
		#region DelegateInvokeExpr
		public override void ParseElement(DelegateInvokeExpr gr)
		{
			Parse(gr.Target);
			sb.Write("(");
			if(gr.Parameters != null)
			{
				string comma = "";
				foreach(Param o in gr.Parameters)
				{
					sb.Write(comma);
					Parse(o);
					comma = ", ";
				}
			}
			sb.Write(")");
		}
		#endregion
		#region IndexerRef
		public override void ParseElement(IndexerRef gr)
		{
			Parse(gr.Target);
			sb.Write("[");
			if(gr.Indices != null)
			{
				string comma = "";
				foreach(Expression o in gr.Indices)
				{
					sb.Write(comma);
					Parse(o);
					comma = ", ";
				}
			}
			sb.Write("]");
		}
		#endregion
		#region MemberAccess
		public override void ParseElement(MemberAccess gr)
		{
			addSpace = false;
			Parse(gr.Target);
			addSpace = true;
			sb.Write("." + gr.MemberName);
		}
		#endregion
		#region ArrayCreateExpr
		public override void ParseElement(ArrayCreateExpr gr)
		{
			string comma = "";
			sb.Write("new ");
			sb.Write(gr.CreateType.TypeName);
			sb.Write("[");
			for(int i = 0; i < gr.DimensionCount; i++)
			{
				if(gr.Sizes != null && gr.Sizes.Count > i && gr.Sizes[i] != null)
				{	
					sb.Write(comma);
					Parse(gr.Sizes[i]);
					comma = ", ";
				}
				else
				{
					sb.Write(comma);
					comma = ",";
				}
			}
			sb.Write("]");

			Parse(gr.RankSpecifiers);
			Parse(gr.Initializer);
		}
		#endregion
		#region ObjectCreateExpr
		public override void ParseElement(ObjectCreateExpr gr)
		{
			sb.Write("new ");
			sb.Write(gr.CreateType.TypeName);
			sb.Write("(");
			if(gr.Parameters != null)
			{
				string comma = "";
				foreach(Param o in gr.Parameters)
				{
					sb.Write(comma);
					Parse(o);
					comma = ", ";
				}
			}
			sb.Write(")");
		}
		#endregion
		#region CreateDelegateExpr
		public override void ParseElement(CreateDelegateExpr gr)
		{
			sb.Write("new ");
			if(gr.Target != null)
			{
				Parse(gr.Target);
				sb.Write(".");
			}
			sb.Write(gr.Type.TypeName);
			sb.Write("(");
			sb.Write(gr.MethodName);
			sb.Write(")");
		}
		#endregion

		#region BooleanLiteral
		public override void ParseElement(BooleanLiteral gr)
		{
			if(gr.Value == true)
				sb.Write("true");
			else
				sb.Write("false");
		}
		#endregion
		#region CharLiteral
		public override void ParseElement(CharLiteral gr)
		{
			sb.Write("'" + gr.Value + "'");
		}
		#endregion
		#region IntegerLiteral
		public override void ParseElement(IntegerLiteral gr)
		{
			sb.Write(gr.Value);
		}
		#endregion
		#region NullLiteral
		public override void ParseElement(NullLiteral gr)
		{
			sb.Write("null");
		}
		#endregion
		#region RealLiteral
		public override void ParseElement(RealLiteral gr)
		{
			sb.Write(gr.Value + "f");
		}
		#endregion
		#region StringLiteral
		public override void ParseElement(StringLiteral gr)
		{
			sb.Write("\"" + gr.Value + "\"");
		}
		#endregion

		#region ArrayInitializer
		public override void ParseElement(ArrayInitializer gr)
		{
			if(gr == null || gr.InitialValues.Count == 0) return;
			sb.Write("{");
			string comma = "";
			for(int i = 0; i < gr.InitialValues.Count; i++)
			{
				sb.Write(comma);
				Parse(gr.InitialValues[i]);
				comma = ", ";
			} 
			sb.Write("}");
		}
		#endregion
		#region CustomAttribute
		public override void ParseElement(CustomAttribute gr)
		{
			/** Added to gather custom attributes */
			AttributeRepresentation ar = new AttributeRepresentation();
			ar.TargetType = AttributeState.TypeAsString;
			ar.TargetLocation = AttributeState.Target;
			ar.TypeName = gr.Name; // Name may not be fully qualified!
			/** End */

			sb.Write("[");
			if(gr.AttributeTarget != AttributeTarget.Empty)
			{
				sb.Write(CustomAttribute.StringFromAttributeTarget(gr.AttributeTarget));
				sb.Write(": ");
			}
			sb.Write(gr.Name);
			if(gr.Parameters.Count != 0)
			{
				sb.Write("(");

				LineWriter oldWriter = sb;
				CapturingLineWriter capture = new CapturingLineWriter(oldWriter); // Capture input + write as normal
				sb = capture;

				string comma = "";
				foreach(Expression o in gr.Parameters)
				{
					sb.Write(comma);
					Parse(o);
					comma = ", ";
				}
				ar.ConstructorValues = capture.Text;
				sb = oldWriter; // Restore normal writer

				sb.Write(")");
			}			
			sb.WriteLine("]");

			/** Write the attribute to XML */
			AttributeWriter.Instance.addAttribute(ar);
			/** End added code */

		}
		#endregion
		#region TypeRef
		public override void ParseElement(TypeRef gr)
		{
			sb.Write(gr.TypeName);
			Parse(gr.ArrayRanks);
			if(addSpace)
				sb.Write(" ");
		}
		#endregion
		#region BuiltInType
		public override void ParseElement(BuiltInType gr)
		{
			sb.Write(gr.TypeName);
			if(addSpace)
				sb.Write(" ");
		}
		#endregion
		#region ParamDecl
		public override void ParseElement(ParamDecl gr)
		{
			if(gr.Direction == ParamDirection.Out) sb.Write("out ");
			else if(gr.Direction == ParamDirection.Ref) sb.Write("ref ");
			else if(gr.IsParams) sb.Write("params ");
			Parse(gr.Type);
			sb.Write(gr.Name);
		}
		#endregion
		#region Param
		public override void ParseElement(Param gr)
		{
			if(gr.Direction == ParamDirection.Out) sb.Write("out ");
			else if(gr.Direction == ParamDirection.Ref) sb.Write("ref ");
			Parse(gr.Value);
		}
		#endregion
		#region VariableDecl : TODO
		public override void ParseElement(VariableDecl gr)
		{
		}
		#endregion
		#region LinePragma
		public override void ParseElement(LinePragma gr)
		{
		}
		#endregion
		#region Comment
		public override void ParseElement(Comment gr)
		{
		}
		#endregion
		#region AssemblyReference
		public override void ParseElement(AssemblyReference gr)
		{
		}
		#endregion
		#region RankSpecifier
		public override void ParseElement(RankSpecifier gr)
		{
			sb.Write("[");
			for(int i = 1; i < gr.Dimensions; i++)
			{
				sb.Write(",");
			}
			sb.Write("]");
		}
		#endregion
		#region Declarator
		public override void ParseElement(Declarator gr)
		{
			sb.Write(gr.Name);
			if(gr.InitExpression != null)
			{
				sb.Write(" = ");
				Parse(gr.InitExpression);
			}
		}
		#endregion

		#region CompileUnitCollection
		public override void ParseElement(CompileUnitCollection gr)
		{
			if(gr == null) return;
		}
		#endregion
		#region ImportCollection
		public override void ParseElement(ImportCollection gr)
		{
			if(gr == null) return;
			foreach(Import o in gr)
			{
				ParseElement(o);
			}
		}
		#endregion
		#region NamespaceDeclCollection
		public override void ParseElement(NamespaceDeclCollection gr)
		{
			if(gr == null) return;
			foreach(NamespaceDecl o in gr)
			{
				ParseElement(o);
			}
		}
		#endregion
		#region TypeDeclCollection
		public override void ParseElement(TypeDeclCollection gr)
		{
			if(gr == null) return;
			foreach(TypeDecl o in gr)
			{
				Parse(o);
			}
		}
		#endregion
		#region MemberDeclCollection
		public override void ParseElement(MemberDeclCollection gr)
		{
			if(gr == null) return;
			foreach(MemberDecl o in gr)
			{
				Parse(o);
			}
		}
		#endregion
		#region TypeRefCollection
		public override void ParseElement(TypeRefCollection gr)
		{
			if(gr == null) return;
			string comma = ": ";
			foreach(TypeRef o in gr)
			{
				sb.Write(comma + o.TypeName);
				comma = ", ";
			}
		}
		#endregion
		#region ParamCollection
		public override void ParseElement(ParamCollection gr)
		{
			if(gr == null) return;
			string comma = "";
			sb.Write("(");
			foreach(Param o in gr)
			{
				sb.Write(comma);
				ParseElement(o);
				comma = ", ";
			}
			sb.Write(")");
		}
		#endregion
		#region ParamDeclCollection
		public override void ParseElement(ParamDeclCollection gr)
		{
			if(gr == null) return;
			string comma = "";
			sb.Write("(");
			foreach(ParamDecl o in gr)
			{
				sb.Write(comma);
				ParseElement(o);
				comma = ", ";
			}
			sb.Write(")");
		}
		#endregion
		#region StatementCollection
		public override void ParseElement(StatementCollection gr)
		{
			if(gr == null) return;
			foreach(Statement o in gr)
			{
				Parse(o);
			}
		}
		#endregion
		#region CommentStmtCollection
		public override void ParseElement(CommentStmtCollection gr)
		{
			if(gr == null) return;
			foreach(CommentStmt o in gr)
			{
				ParseElement(o);
			}
		}
		#endregion
		#region CatchCollection
		public override void ParseElement(CatchCollection gr)
		{
			if(gr == null) return;
			foreach(Catch o in gr)
			{
				ParseElement(o);
			}
		}
		#endregion
		#region ExpressionCollection
		public override void ParseElement(ExpressionCollection gr)
		{
			if(gr == null) return;
			// todo: this can be comma separated, as in ConstructorDecl
			foreach(Expression o in gr)
			{
				Parse(o);
			}
		}
		#endregion
		#region CustomAttributeCollection
		public override void ParseElement(CustomAttributeCollection gr)
		{
			if(gr.Count == 0) return;
			foreach(CustomAttribute o in gr)
			{
				ParseElement(o);
			}
		}
		#endregion
		#region CaseCollection
		public override void ParseElement(CaseCollection gr)
		{
			if(gr == null) return;
			foreach(Case o in gr)
			{
				ParseElement(o);
			}
		}
		#endregion
		#region AssemblyReferenceCollection
		public override void ParseElement(AssemblyReferenceCollection gr)
		{
			if(gr == null) return;
			foreach(AssemblyReference o in gr)
			{
				ParseElement(o);
			}
		}
		#endregion
		#region RankSpecifierCollection
		public override void ParseElement(RankSpecifierCollection gr)
		{
			if(gr == null) return;
			for(int i = 0; i < gr.Count; i++)
			{
				ParseElement(gr[i]);
			}
		}
		#endregion
		#region DeclaratorCollection
		public override void ParseElement(DeclaratorCollection gr)
		{
		}
		#endregion

		#region Modifiers Enum
		public override void ParseElement(Modifiers gr)
		{
			if(gr == Modifiers.Empty) return;
			if((gr & Modifiers.Public) != 0)		sb.Write("public ");
			if((gr & Modifiers.Protected) != 0)		sb.Write("protected ");
			if((gr & Modifiers.Private) != 0)		sb.Write("private ");
			if((gr & Modifiers.Static) != 0)		sb.Write("static ");
			if((gr & Modifiers.New) != 0)			sb.Write("new ");
			if((gr & Modifiers.Override) != 0)		sb.Write("override ");
			if((gr & Modifiers.Abstract) != 0)		sb.Write("abstract ");
			if((gr & Modifiers.Virtual) != 0)		sb.Write("virtual ");
			if((gr & Modifiers.Sealed) != 0)		sb.Write("sealed ");
			if((gr & Modifiers.Internal) != 0)		sb.Write("internal ");
			if((gr & Modifiers.Readonly) != 0)		sb.Write("readonly ");
			if((gr & Modifiers.Volatile) != 0)		sb.Write("volatile ");
			if((gr & Modifiers.Extern) != 0)		sb.Write("extern ");
		}
		#endregion
		#region TypeModifiers Enum
		public override void ParseElement(TypeModifiers gr)
		{
		}
		#endregion
		#region AccessorModifiers Enum
		public override void ParseElement(AccessorModifiers gr)
		{
		}
		#endregion
		#region AttributeTarget Enum
		public override void ParseElement(AttributeTarget gr)
		{
		}
		#endregion
		#region OverloadableOperator Enum
		public override void ParseElement(OverloadableOperator gr)
		{
			sb.Write(OperatorDecl.StringFromOperator(gr) + " ");
		}
		#endregion
		#region BinaryOperator Enum
		public override void ParseElement(BinaryOperator gr)
		{
			sb.Write(BinaryExpr.StringFromOperator(gr) + " ");
		}
		#endregion
		#region UnaryOperator Enum
		public override void ParseElement(UnaryOperator gr)
		{
			sb.Write(UnaryExpr.StringFromOperator(gr) + " ");
		}
		#endregion
		#region PostfixOperator Enum
		public override void ParseElement(PostfixOperator gr)
		{
		}
		#endregion
		#region AssignOperator Enum
		public override void ParseElement(AssignOperator gr)
		{
		}
		#endregion
		#region IterationType Enum
		public override void ParseElement(IterationType gr)
		{
		}
		#endregion
		#region ParamDirection Enum
		public override void ParseElement(ParamDirection gr)
		{
		}
		#endregion


		private void OpenBlock()
		{
			sb.WriteLine("");
			sb.WriteLine("{");
			sb.Indent++;
		}
		private void CloseBlock()
		{
			sb.Indent--;
			sb.WriteLine("}");
		}

		protected override void BeginParse(IGraph gr)
		{
			gr.LinePragma.Column = sb.Column;
			gr.LinePragma.Line = sb.Line;
		}
		protected override void EndParse(IGraph gr)
		{
			gr.LinePragma.EndColumn = sb.Column;
			gr.LinePragma.EndLine = sb.Line;
		}
		public override string ToString()
		{
			return sb.ToString();
		}
		public void Close()
		{
			sb.Close();
		}

	}
}