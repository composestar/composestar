using System;
using System.IO;
using System.Text;
using System.Collections;
using System.CodeDom;
using System.Globalization;

using antlr;	
using antlr.collections;
using Dom = DDW.CSharp.Dom;
using DDW.CSharp.SymbolTable;

namespace DDW.CSharp.Parse
{
	#region CSharpAST 
	/// <summary>
	/// The base class for all custom ASTs.
	/// </summary>
	public class CSharpAST : antlr.BaseAST // was abstract
	{	
		private int ttype = Token.INVALID_TYPE;
		protected string p_text;
		protected bool isDirty = true;
		protected Dom.TypeDecl curType;
		//public static int CurLine = 0;
		//public static int CurCol = 0;
		#region Constructor 
		public						CSharpAST()			
		{
			//Console.WriteLine("tok: "+this.GetType()+" ln# " + LinePragma.Line);
		}
		/// <summary>
		/// Construct blank AST node using a token
		/// </summary>
		public						CSharpAST(Token tok)
		{
			Console.WriteLine("has tok: "+this.GetType());
			initialize(tok);
		}
 		#endregion 
		#region UserData 
		/// <summary>
		/// Gets or sets the user-definable data for the current object.
		/// </summary>
		public virtual IDictionary 	UserData			
		{
			get{ return null; }
			set{ }
		}
		#endregion 
		#region FileName
		private string p_filename = "";
		public string			FileName				
		{
			get{return p_filename;}	
			set{p_filename = value;}	
		}
		#endregion
 		#region Text 
		/// <summary>
		/// Gets or sets the text of the node.
		/// </summary>
		//		public override string		Text				
		//		{
		//			get{return p_text;}	
		//			set{p_text = value;}	
		//		}
		#endregion 
 		#region Type 
		/// <summary>
		/// Gets or sets the Type of the node (TODO: make this enum)
		/// </summary>
		public override int			Type				
		{
			get{return ttype;}	
			set{ttype = value;}	
		}
		#endregion 
		#region  LinePragma
		private Dom.LinePragma p_linePragma = new Dom.LinePragma();
		public Dom.LinePragma				LinePragma		
		{
			get
			{	
				return p_linePragma;
			}
			set
			{
					p_linePragma = value;
			}
		}
		#endregion
		// was abstract
		public virtual Dom.IGraph  GetGraph(){ return null;}
		public virtual Dom.IGraph  GetGraph(Dom.IGraph graph)
		{ 
			//graph.LinePragma = this.LinePragma;
			return graph;
		}

		#region ToString
		public override String		ToString()			
		{
			return p_text +" (" + this.GetType().Name+")";
		}
		#endregion
		#region Various Java methods 
		public override void		initialize(int t, String txt)	
		{
			setType(t);
			setText(txt);
			//LinePragma.Line = CurLine;
			//LinePragma.Column = CurCol;
		}

		public override void		initialize(AST t)				
		{
			p_text = t.getText();
			Type = t.Type;
//			if(t is CSharpAST)
//			{
//				LinePragma.Line = CurLine;
//				LinePragma.Column = CurCol;
//			}
		}

		public override void		initialize(IToken tok)			
		{
			p_text = tok.getText();
			Type = tok.Type;
//			LinePragma.Line = tok.getLine();
//			LinePragma.Column = CurCol;
		}
		public override String		getText()						
		{
			return p_text;
		}
		public			int			getType()						
		{
			return ttype;
		}
		public override void		setText(String text_)			
		{
			p_text = text_;
		}

		public override void		setType(int ttype_)				
		{
			ttype = ttype_;
		}
		#endregion 

		#region ScopeTable
		private static Dom.CompileUnit currentCompileUnit;
		public static ScopeCollection ScopeTable
		{
			get
			{
				return  currentCompileUnit.ScopeTable;
			}
		}
		#endregion
		#region CurrentScope
		private static Scope p_currentScope;
		public static Scope CurrentScope
		{
			get
			{
				return  p_currentScope;
			}
			set
			{
				p_currentScope = value;
			}
		}
		#endregion		
		#region GlobalScope
		private static AssemblyScope p_globalScope;
		public static AssemblyScope GlobalScope
		{
			get
			{
				return  p_globalScope;
			}
			set
			{
				p_globalScope = value;
			}
		}
		#endregion

		#region SymbolTable
		private static DefinitionCollection  p_symtab = new DefinitionCollection();
		/// <summary>
		/// Stores un-attributed symbols until all types/vars are declared.
		/// </summary>
		public static DefinitionCollection SymbolTable
		{
			get
			{
				return  p_symtab;
			}
		}
		#endregion		
		#region AddSymbol(Dom.ISymbol)
		/// <summary>
		/// Symbols are variables that refer to types, or type refs themselves.
		/// </summary>
		public void AddSymbol(Dom.ISymbolRef sgraph, string name)
		{
			IDefinition d = sgraph.Definition;
			if(d == null)
			{
				d = new Definition();
				sgraph.Definition = d;
				d.Id = defCount++;
			}
			if(d.Scope == null) d.Scope = CSharpAST.CurrentScope;
			d.SourceGraph = sgraph;
			d.Name = name;

			CSharpAST.SymbolTable.Add(d);
		}
		#endregion	
		#region ResolveSymbols()
		/// <summary>
		/// Attributes all the stored symbols - only call once all definitions are parsed. This will clear the symbol table once its contents are attributed.
		/// </summary>
		public bool ResolveSymbols()
		{
			bool isValid = true;
			foreach(Definition d in SymbolTable)
			{
				Scope defScope = d.Scope;
				if(d.SourceGraph is Dom.IScope) 
					defScope = d.Scope.EnclosingScope;

				if(d.SourceGraph is Dom.UnknownReference)
				{
					Definition resolved = defScope.Lookup(d.Name);

					if(resolved != null)
					{
						//d.Scope = resolved.Scope;
						Dom.UnknownReference ur = (Dom.UnknownReference)d.SourceGraph;
						ur.Definition = resolved;
						Console.WriteLine(resolved.FullName);
					}
					else
					{
						isValid = false;
						Console.WriteLine(
							d.SourceGraph.GraphType.ToString() + 
							" def **not** found: " + d.Name);
					}
				}
				else if(d.SourceGraph is Dom.TypeRef)
				{	
					Definition resolved = defScope.Lookup(d.Name);
					if(resolved != null)
					{
						// rewrite the def to be the looked up def
						Dom.TypeRef tr = (Dom.TypeRef)d.SourceGraph;
						tr.Definition = resolved;
						Console.WriteLine("TypeRef: " + resolved.FullName);
					}
					else
					{
						isValid = false;
						Console.WriteLine(
							d.SourceGraph.GraphType.ToString() + 
							" not found: " + d.Name);
					}				
				}
				else
				{
					isValid = false;
					Console.WriteLine("*** " + d.Name +" - not resolved: " + d.SourceGraph.GraphType.ToString());
				}
			}
			SymbolTable.Clear();
			return isValid;
		}
		#endregion
		
		#region AddRootExpression(Dom.Expression, Type, string)
		private static ExpressionRootCollection  rootExprs = new ExpressionRootCollection();
		/// <summary>
		/// Stores root expressions (right sides, args, etc) to make expr exprAttr quicker.
		/// </summary>
		public void AddRootExpression(Dom.Expression expr, Type t, string propName)
		{
			if(expr == null) return;
			ExpressionRoot er = new ExpressionRoot();
			er.Expression = expr;
			er.Instance = t;
			er.PropertyName = propName;
			CSharpAST.rootExprs.Add(er);
		}
		#endregion	
		#region ResolveExpressions()
		/// <summary>
		/// Determines all the return types of expressions, needed for resolving overloads.
		/// </summary>
		public void ResolveExpressions()
		{
			Attribution.AttributeExpressions(CSharpAST.rootExprs);
		}
		#endregion

		#region OpenBlockScope()
		public Scope OpenBlockScope()
		{
			Scope sc = new BlockScope();
			sc.EnclosingScope =  CSharpAST.CurrentScope;
			CSharpAST.ScopeTable.Add(sc);
			CSharpAST.CurrentScope = sc;
			return sc;
		}
		#endregion
		#region OpenScope(IScope)
		/// <summary>
		/// Creates a new scope and sets it as the current scope. This will also add a definition to the parent scope if needed (in case of eg Class or Namespace, but not eg block or compile unit).
		/// </summary>
		/// <param name="graph">The current graph element.</param>
		/// <returns>Returns a reference to the newly created scope.</returns>
		public Scope OpenScope(Dom.IScope graph)
		{
			switch(graph.GraphType)
			{			
				case Dom.GraphTypes.CompileUnit		:
				{
					// set scope table to current assembly's scope
					currentCompileUnit = (Dom.CompileUnit)graph;
					// add a new Assembly scope to it
					GlobalScope = new AssemblyScope();
					graph.Scope = GlobalScope;
					break;
				}
				case Dom.GraphTypes.NamespaceDecl	:
				{
					graph.Scope = new NamespaceScope();
					break;
				}
				case Dom.GraphTypes.ClassDecl		:
				case Dom.GraphTypes.InterfaceDecl	:
				case Dom.GraphTypes.StructDecl		:
				case Dom.GraphTypes.EnumDecl		:
				{
					graph.Scope = new TypeScope();
					break;
				}
				case Dom.GraphTypes.MethodDecl		:
				case Dom.GraphTypes.IndexerDecl		:
				case Dom.GraphTypes.OperatorDecl	:	
				case Dom.GraphTypes.ConstructorDecl	:
				case Dom.GraphTypes.DestructorDecl	:
				case Dom.GraphTypes.AccessorDecl	:
				{
					graph.Scope = new MemberScope();
					break;
				}
				default	:
				{
					throw(new Exception("uncaught scope type"));
					break;
				}
			}
			// set parent and (parent's) child refs
			graph.Scope.EnclosingScope = CSharpAST.CurrentScope;
			if(CSharpAST.CurrentScope != null)
			{
				CSharpAST.CurrentScope.ChildScopes.Add(graph.Scope);
			}
			// add defintion and link scope to its name, if needed
			if(graph is Dom.IDeclaration)
			{
				((INamedScope)graph.Scope).Definition = 
					((Dom.IDeclaration)graph).Definition;
				// only add def after name is known		
				// AddDefinition((Dom.IDeclaration)graph, );
			}
			// add to the main scope table storage area too
			CSharpAST.ScopeTable.Add(graph.Scope);
			// set this to be the current scope
			CSharpAST.CurrentScope = graph.Scope;
			return graph.Scope;
		}
		#endregion
		public static int defCount = 1;
		#region AddDefinition(IDeclaration)
		public IDefinition AddDefinition(Dom.IDeclaration dgraph, string name, Dom.IDeclaration type)
		{
			dgraph.Definition.Scope = CSharpAST.CurrentScope;
			dgraph.Definition.SourceGraph = dgraph;
			dgraph.Definition.Name = name;
			dgraph.Definition.Type = type;
			dgraph.Definition.Id = defCount++;

			// overloadable types are stored as a collection in one OverloadableDefinition
			// that is always created here
			if(dgraph is Dom.IOverloadable)
			{
				// enclosing scopes are never null for defs..
				Definition d = CSharpAST.CurrentScope.Lookup(name);
				// overloads are stored in a main 'overloadable' definition
				// that contains a 'Definitions' collection of each overloaded def.

				// FIX: it is possible that a variable and a method have the same name
				// so we must make sure the looked up def is actaully for a variable
				// * thank you Niklas Pettersson
				bool dov = d is OverloadableDefinition;
				if(d == null || !dov)
				{
					d = new OverloadableDefinition();
					d.Name = name;
					d.Scope = CSharpAST.CurrentScope;
					d.SourceGraph = dgraph;
					d.Id = defCount++;
					d.Type = type;
					CSharpAST.CurrentScope.EnclosingScope.Add(d, name);
				}
				// must always be overloadable def
                ((OverloadableDefinition)d).Definitions.Add(dgraph.Definition); 
			}
			else if(dgraph is Dom.IScope)
			{
				// all other scopes should also be registered in the enclosing scope
				CSharpAST.CurrentScope.EnclosingScope.Add(dgraph.Definition, name);
			}
			else
			{
				// and locals, agrs should be registered in the scope they are found in
				CSharpAST.CurrentScope.Add(dgraph.Definition, name);
			}
			return dgraph.Definition;
		}
		#endregion

		#region AddBuiltInDefinition(string)
		public IDefinition AddBuiltInDefinition(string name)
		{
			BuiltInDefinition bid = 
				(BuiltInDefinition)GlobalScope.DefinitionTable[name];
			if(bid == null)
			{
				// set the def now for built in types as it is a subclass of def
				bid = new BuiltInDefinition();
				bid.Name = name;
				// set type now as that is what is different
				// the rest will get attributed once added to symbol table
				bid.Scope = GlobalScope;
				bid.Id = CSharpAST.defCount++;
				// must add this to defs if it isn't there already
				// this allows us to keep track of which built in types were used
				// always add built in to global scope
				GlobalScope.DefinitionTable.Add(name, bid);
			}
			return bid;
		}
		#endregion

		#region CloseScope()
		public void CloseScope()
		{			
			CSharpAST.CurrentScope = CSharpAST.CurrentScope.EnclosingScope;
		}
		#endregion

	}
	#endregion 

	#region CompileUnit 
	public class				CompileUnit : CSharpAST
	{	
		public override Dom.IGraph		GetGraph()				
		{	
			Dom.CompileUnit graph = 
				new Dom.CompileUnit();
			return this.GetGraph(graph);
		}	
		public Dom.CompileUnit	
			GetGraph(Dom.CompileUnit graph)
		{	
			base.GetGraph(graph);
			this.OpenScope(graph);
			Dom.NamespaceDecl dns = null;

			graph.FileName = this.FileName;

			AST tok = this.getFirstChild();
			while(tok != null)
			{			
				if(tok is NamespaceNode) 
				{
					Dom.NamespaceDecl ns = 
						(Dom.NamespaceDecl)((NamespaceNode)tok).GetGraph();
					graph.Namespaces.Add(ns);
				}
				else if(tok is CustomAttributes) 
					((CustomAttributes)tok).GetGraph(graph.AssemblyCustomAttributes);
				else if(tok is UsingNode) 
					graph.Imports.Add( (Dom.Import)((UsingNode)tok).GetGraph() );
				else //if(tok is Types || tok is QualIdent) 
				{					
					if(dns == null)
					{
						dns = new Dom.NamespaceDecl();
						dns.Name = "$DefaultNamespace";
						graph.Namespaces.Add(dns);
					}
					Types.ParseTypesIntoNamespace(tok, dns);
				}

				tok = tok.getNextSibling();
			}
			this.CloseScope();
			// do type attribution for declarations, localdecls and args
			//graph.IsAttributed = ResolveSymbols();
			// do type attribution for expressions
			//if(graph.IsAttributed) 
			//{
			//	ResolveExpressions();
			//}
			//else
			//{
			//	Console.WriteLine("Not resolving expressions due to some unresolved symbols.");
			//}
			return graph;
		}
	}
	#endregion 
	#region NamespaceNode 
	public class				NamespaceNode : CSharpAST
	{
		public override Dom.IGraph		GetGraph()				
		{	
			Dom.NamespaceDecl graph = 
				new Dom.NamespaceDecl();
			return this.GetGraph(graph);
		}	
		public Dom.NamespaceDecl	
			GetGraph(Dom.NamespaceDecl graph)
		{	
			//graph.Clear();
			base.GetGraph(graph);
			this.OpenScope(graph);

			AST tok = this.getFirstChild();			
			while(tok != null)
			{
				if(tok is QualIdent)
				{
					graph.Name = QualIdent.GetFullName((QualIdent)tok);
				}				
				else if(tok is Types) 
					graph.Types = ((Types)tok).GetGraph(graph.Types);
				else if (tok is UsingNode)
					graph.Imports.Add( (Dom.Import)((UsingNode)tok).GetGraph() );

				tok = tok.getNextSibling();
			}
			this.AddDefinition(graph, graph.Name, graph);
			this.CloseScope();
			return graph;
		}
	}
	#endregion 
	#region UsingNode 
	public class				UsingNode : CSharpAST
	{
		public override Dom.IGraph		GetGraph()				
		{	
			Dom.Import graph = 
				new Dom.Import();
			return this.GetGraph(graph);
		}	
		public Dom.Import	
			GetGraph(Dom.Import graph)
		{	
			base.GetGraph(graph);
			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is Ident) 
				{
					AST ctok = tok.getFirstChild();
					if(ctok != null) graph.Alias = ctok.getText();
				}
				if(tok is QualIdent) 
				{		
					graph.Namespace = QualIdent.GetFullName((QualIdent)tok);
				}
				tok = tok.getNextSibling();
			}
			return graph;
		}
	}
	#endregion 

	#region Types 
	public class				Types : CSharpAST
	{
		public override Dom.IGraph		GetGraph()				
		{	
			Dom.TypeDeclCollection graph = 
				new Dom.TypeDeclCollection();
			return this.GetGraph(graph);
		}	
		public Dom.TypeDeclCollection	
			GetGraph(Dom.TypeDeclCollection graph)
		{	
			base.GetGraph(graph);
			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is InterfaceNode) 
					graph.Add((Dom.InterfaceDecl)((InterfaceNode)tok).GetGraph());
				if(tok is ClassNode) 
					graph.Add((Dom.ClassDecl)((ClassNode)tok).GetGraph());
				if(tok is EnumNode) 
					graph.Add((Dom.EnumDecl)((EnumNode)tok).GetGraph());
				if(tok is StructNode) 
					graph.Add((Dom.StructDecl)((StructNode)tok).GetGraph());
				if(tok is DelegateNode) 
					graph.Add((Dom.DelegateDecl)((DelegateNode)tok).GetGraph());

				tok = tok.getNextSibling();
			}

			return graph;
		}
		public static void	ParseTypesIntoNamespace(AST tok, Dom.NamespaceDecl graph)
		{	
			if(tok == null || graph == null) return;

			if(tok is InterfaceNode) 
				graph.Types.Add((Dom.InterfaceDecl)((InterfaceNode)tok).GetGraph());
			if(tok is ClassNode) 
				graph.Types.Add((Dom.ClassDecl)((ClassNode)tok).GetGraph());
			if(tok is EnumNode) 
				graph.Types.Add((Dom.EnumDecl)((EnumNode)tok).GetGraph());
			if(tok is StructNode) 
				graph.Types.Add((Dom.StructDecl)((StructNode)tok).GetGraph());
			if(tok is DelegateNode) 
				graph.Types.Add((Dom.DelegateDecl)((DelegateNode)tok).GetGraph());

		}
	}
	#endregion 
    	#region InterfaceNode *
	public class						InterfaceNode : TypeNode
	{
		public override Dom.IGraph		GetGraph()				
		{	
			Dom.InterfaceDecl graph = 
				new Dom.InterfaceDecl();
			return this.GetGraph(graph);
		}	
		public Dom.InterfaceDecl	
			GetGraph(Dom.InterfaceDecl graph)
		{	
			//graph.Clear();
			this.OpenScope(graph);
			base.GetGraph(graph);
			this.CloseScope();
			this.AddDefinition(graph, graph.Name, graph);
			return graph;
		}
	}
		#endregion 
		#region ClassNode *
	public class						ClassNode : TypeNode
	{
		public override Dom.IGraph		GetGraph()				
		{	
			Dom.ClassDecl graph = 
				new Dom.ClassDecl();
			return this.GetGraph(graph);
		}	
		public Dom.ClassDecl	
			GetGraph(Dom.ClassDecl graph)
		{	
			//graph.Clear();
			this.OpenScope(graph);
			base.GetGraph(graph);
			this.AddDefinition(graph, graph.Name, graph);
			this.CloseScope();
			return graph;
		}
	}
	#endregion 
		#region EnumNode *
	public class						EnumNode : TypeNode
	{
		public override Dom.IGraph		GetGraph()				
		{	
			Dom.EnumDecl graph = 
				new Dom.EnumDecl();
			return this.GetGraph(graph);
		}	
		public Dom.EnumDecl	
			GetGraph(Dom.EnumDecl graph)
		{	
			//graph.Clear();
			this.OpenScope(graph);
			base.GetGraph(graph);
			this.AddDefinition(graph, graph.Name, graph);
			this.CloseScope();
			return graph;
		}
	}
	#endregion 
		#region StructNode *
	public class						StructNode : TypeNode
	{
		public override Dom.IGraph		GetGraph()				
		{	
			Dom.StructDecl graph = 
				new Dom.StructDecl();
			return this.GetGraph(graph);
		}	
		public Dom.StructDecl	
			GetGraph(Dom.StructDecl graph)
		{
			//graph.Clear();
			this.OpenScope(graph);
			base.GetGraph(graph);
			this.AddDefinition(graph, graph.Name, graph);
			this.CloseScope();
			return graph;
		}
	}
	#endregion 
		#region DelegateNode *
	public class						DelegateNode : TypeNode
	{
		public override Dom.IGraph		GetGraph()				
		{	
			Dom.DelegateDecl graph = 
				new Dom.DelegateDecl();
			return this.GetGraph(graph);
		}	
		public Dom.DelegateDecl	
			GetGraph(Dom.DelegateDecl graph)
		{	
			//graph.Clear();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is TypeRef) 
				{
					graph.ReturnType = (Dom.TypeRef)((TypeRef)tok).GetGraph();
				}
				else if(tok is DeclArgs) 
					((DeclArgs)tok).GetGraph(graph.Parameters);
				tok = tok.getNextSibling();
			}
			AddDefinition(graph, graph.Name, graph);
			return graph;
		}
	}
	#endregion 

	#region Members 
	public class				Members : CSharpAST
	{
		public override Dom.IGraph		GetGraph()				
		{	
			Dom.MemberDeclCollection graph = 
				new Dom.MemberDeclCollection();
			return this.GetGraph(graph);
		}	
		public Dom.MemberDeclCollection	
			GetGraph(Dom.MemberDeclCollection graph)
		{	
			base.GetGraph(graph);
			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is ConstantNode) 
					graph.Add( (Dom.ConstantDecl)((ConstantNode)tok).GetGraph() );

				else if(tok is	FieldNode) 
					graph.Add( (Dom.FieldDecl)((FieldNode)tok).GetGraph() );

				else if(tok is	MethodNode) 
					graph.Add( (Dom.MethodDecl)((MethodNode)tok).GetGraph() );

				else if(tok is	PropertyNode) 
					graph.Add( (Dom.PropertyDecl)((PropertyNode)tok).GetGraph() );

				else if(tok is EventNode) 
					graph.Add( (Dom.EventDecl)((EventNode)tok).GetGraph() );

				else if(tok is IndexerNode) 
					graph.Add( (Dom.IndexerDecl)((IndexerNode)tok).GetGraph() );

				else if(tok is OperatorNode)
					graph.Add( (Dom.OperatorDecl)((OperatorNode)tok).GetGraph() );

				else if(tok is ConstructorNode)
						graph.Add( (Dom.ConstructorDecl)((ConstructorNode)tok).GetGraph() );

				else if(tok is DestructorNode) 
					graph.Add( (Dom.DestructorDecl)((DestructorNode)tok).GetGraph() );

				else if(tok is AccessorNode) 
					graph.Add( (Dom.AccessorDecl)((AccessorNode)tok).GetGraph() );

				else if(tok is EnumMemberNode) 
					graph.Add( (Dom.EnumMemberDecl)((EnumMemberNode)tok).GetGraph() );

				// WH: Fix - support nested classes as well!
				else if(tok is ClassNode) 
					graph.Add((Dom.ClassDecl)((ClassNode)tok).GetGraph());

				tok = tok.getNextSibling();
			}

			return graph;
		}

	}
	#endregion 
	#region TypeMemberNode *
	/// <summary>
	/// Abstract base class for member types (Class, Method, Prop..)
	/// Name Attributes CustomAttributes Comments
	/// </summary>
	public abstract class TypeMemberNode : CSharpAST		
	{
//		public override Dom.IGraph			GetGraph()				
//		{	
//			Dom.MemberDecl graph = new Dom.MemberDecl();
//			return this.GetGraph(graph);
//		}	
		public Dom.MemberDecl	GetGraph(Dom.MemberDecl graph)
		{	
			// Name, Modifiers, CustomAttributes, Comments
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is ModifierAttributes) 
					graph.Attributes = ParseModifiers((ModifierAttributes)tok);
				else if(tok is CustomAttributes)
					((CustomAttributes)tok).GetGraph(graph.CustomAttributes);
				else if(tok is CommentNode)
					((CommentNode)tok).GetGraph(graph.Comments);

				tok = tok.getNextSibling();
			}
			return graph;
		}
		protected Dom.Modifiers ParseModifiers(ModifierAttributes tok)	
		{
			if(tok.getNumberOfChildren() == 0) return Dom.Modifiers.Empty;
			AST curtok = tok.getFirstChild();
			Dom.Modifiers mods = Dom.Modifiers.Empty;
			while(curtok != null)
			{
				switch (curtok.getText())
				{
					case "new":
						mods |= Dom.Modifiers.New;
						break;
					case "public":
						mods |= Dom.Modifiers.Public;
						break;
					case "protected":
						mods |= Dom.Modifiers.Protected;
						break;
					case "internal":
						mods |= Dom.Modifiers.Internal;
						break;
					case "private":
						mods |= Dom.Modifiers.Private;
						break;
					case "static":
						mods |= Dom.Modifiers.Static;
						break;
					case "virtual":
						mods |= Dom.Modifiers.Virtual;
						break;
					case "sealed":
						mods |= Dom.Modifiers.Sealed;
						break;
					case "override":
						mods |= Dom.Modifiers.Override;
						break;
					case "abstract":
						mods |= Dom.Modifiers.Abstract;
						break;
					case "extern":
						mods |= Dom.Modifiers.Extern;
						break;
					case "readonly":
						mods |= Dom.Modifiers.Readonly;
						break;
					case "volatile":
						mods |= Dom.Modifiers.Volatile;
						break;
				}
				curtok = curtok.getNextSibling();
			}
			return mods;
		}
	}
	#endregion 
		#region TypeNode *
	public abstract class				TypeNode : TypeMemberNode
	{
		public Dom.TypeDecl	GetGraph(Dom.TypeDecl graph)
		{	
			// required for constructors and enum members, so they can know their return type
			this.curType = graph;
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is ModifierAttributes) 
					graph.TypeAttributes = ParseTypeModifiers((ModifierAttributes)tok);
				else if(tok is Ident) 
					graph.Name = tok.getText();
				else if(tok is Members) 
					((Members)tok).GetGraph(graph.Members);
				else if(tok is BaseTypes) 
					((BaseTypes)tok).GetGraph(graph.BaseTypes);
				else if(tok is CommentNode)
					((CommentNode)tok).GetGraph(graph.Comments);

				tok = tok.getNextSibling();
			}
			return graph;
		}
		protected Dom.TypeModifiers ParseTypeModifiers(ModifierAttributes tok)	
		{
			if(tok.getNumberOfChildren() == 0) return Dom.TypeModifiers.Empty;
			AST curtok = tok.getFirstChild();
			Dom.TypeModifiers mods = Dom.TypeModifiers.Empty;
			while(curtok != null)
			{
				switch (curtok.getText())
				{
					case "new":
						mods |= Dom.TypeModifiers.Abstract;
						break;
					case "public":
						mods |= Dom.TypeModifiers.Class;
						break;
					case "protected":
						mods |= Dom.TypeModifiers.Interface;
						break;
					case "internal":
						mods |= Dom.TypeModifiers.NestedPrivate;
						break;
					case "private":
						mods |= Dom.TypeModifiers.NestedPublic;
						break;
					case "static":
						mods |= Dom.TypeModifiers.NotPublic;
						break;
					case "virtual":
						mods |= Dom.TypeModifiers.Public;
						break;
					case "sealed":
						mods |= Dom.TypeModifiers.Sealed;
						break;
				}
				curtok = curtok.getNextSibling();
			}
			return mods;
		}

	}
		#endregion 
		#region ConstantNode *
	public class						ConstantNode : TypeMemberNode
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.ConstantDecl graph = 
				new Dom.ConstantDecl();
			return this.GetGraph(graph);
		}	
		public Dom.ConstantDecl	
			GetGraph(Dom.ConstantDecl graph)
		{		
			base.GetGraph(graph);
			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is TypeRef)
				{
					graph.Type = (Dom.TypeRef)((TypeRef)tok).GetGraph();
				}
				else if(tok is Declarator) 
				{
					((Declarator)tok).GetGraph(graph.Delcarators);
				}				
				tok = tok.getNextSibling();
			}
			foreach(Dom.Declarator d in graph.Delcarators)
			{
				d.Definition.Type = graph.Type;
			}
			return graph;
		}


	}
		#endregion 
		#region FieldNode *
	public class						FieldNode : TypeMemberNode
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.FieldDecl graph = new Dom.FieldDecl();
			return this.GetGraph(graph);
		}	
		public Dom.FieldDecl	GetGraph(Dom.FieldDecl graph)
		{		
			base.GetGraph(graph);
			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is TypeRef)
				{
					graph.Type = (Dom.TypeRef)((TypeRef)tok).GetGraph();
				}
				else if(tok is Declarator) 
				{
					((Declarator)tok).GetGraph(graph.Delcarators);
				}	
				
				tok = tok.getNextSibling();
			}
			foreach(Dom.Declarator d in graph.Delcarators)
			{
				d.Definition.Type = graph.Type;
			}
			return graph;
		}

	}
		#endregion 
		#region MethodNode *
	public class						MethodNode : TypeMemberNode
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.MethodDecl graph = new Dom.MethodDecl();
			return this.GetGraph(graph);
		}	
		public Dom.MethodDecl	
			GetGraph(Dom.MethodDecl graph)
		{		
			// Parameters, ReturnType, Statements, CustomAttributes
			//graph.Clear();
			this.OpenScope(graph);
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is DeclArgs) 
					((DeclArgs)tok).GetGraph(graph.Parameters);
				else if(tok is TypeRef)
				{
					graph.ReturnType = (Dom.TypeRef)((TypeRef)tok).GetGraph();
				}
				else if(tok is QualIdent) 					
					graph.Name = QualIdent.GetFullName((QualIdent)tok);
				// in interfaces the name is an Ident
				else if(tok is Ident) 
					graph.Name = tok.getText();
				else if(tok is Statements)
					((Statements)tok).GetGraph(graph.Statements);

				tok = tok.getNextSibling();
			}
			this.AddDefinition(graph, graph.Name, graph.ReturnType);
			this.CloseScope();
			return graph;
		}
	}
    	#endregion 
		#region PropertyNode *
	public class						PropertyNode : TypeMemberNode
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.PropertyDecl graph = new Dom.PropertyDecl();
			return this.GetGraph(graph);
		}	
		public Dom.PropertyDecl	
			GetGraph(Dom.PropertyDecl graph)
		{		
			// GetAccessor, SetAccessor, Type
			//graph.Clear();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is TypeRef)
				{
					graph.Type = (Dom.TypeRef)((TypeRef)tok).GetGraph();
				}
				else if(tok is QualIdent) 
					graph.Name = QualIdent.GetFullName((QualIdent)tok);
				// in interfaces the name is an Ident
				else if(tok is Ident) 
					graph.Name = tok.getText();
				else if(tok is AccessorNode)
				{
					if(tok.getText() == "get")
					{
						//if(hasGet) // error;
						graph.HasGet = true;
						 ((AccessorNode)tok).GetGraph(graph.GetAccessor);
					}
					else if(tok.getText() == "set")
					{
						//if(hasSet) // error;
						graph.HasSet = true;
						((AccessorNode)tok).GetGraph(graph.SetAccessor);
					}
				}
				tok = tok.getNextSibling();
			}
			AddDefinition(graph, graph.Name, graph.Type);
			return graph;
		}

	}
		#endregion 
		#region EventNode *
	public class						EventNode : TypeMemberNode
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.EventDecl graph = new Dom.EventDecl();
			return this.GetGraph(graph);
		}	
		public Dom.EventDecl	GetGraph(Dom.EventDecl graph)
		{			
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is TypeRef)
				{
					graph.Type = (Dom.TypeRef)((TypeRef)tok).GetGraph();
				}
				else if(tok is Declarator) 
				{
					((Declarator)tok).GetGraph(graph.Delcarators);
				}
				// note: name may also come from qualIdent in case of accessor
				// because the event name may be prepended with an interface name. 
				else if(tok is QualIdent)
				{
					graph.UsesAccessors = true;
					Dom.Declarator dd = new Dom.Declarator();
					dd.Name = QualIdent.GetFullName((QualIdent)tok); 
					graph.Delcarators.Add(dd);
				}
				else if(tok is AccessorNode)
				{
					if(tok.getText() == "add")
					{
						//if(hasAdd) // error;
						graph.UsesAccessors = true;
						graph.AddAccessor = (Dom.AccessorDecl)((AccessorNode)tok).GetGraph();
					}
					else if(tok.getText() == "remove")
					{
						//if(hasRemove) // error;
						graph.UsesAccessors = true;
						graph.RemoveAccessor = (Dom.AccessorDecl)((AccessorNode)tok).GetGraph();
					}
				}
				else if(tok is Expression)
				{
					// temp: taking out InitExpression, just in case it wasn't an error..
					throw new Exception("csharpAst event - cant have expr");
				}
				tok = tok.getNextSibling();
			}
			foreach(Dom.Declarator d in graph.Delcarators)
			{
				d.Definition.Type = graph.Type;
			}
			return graph;
		}	
	}
		#endregion 
		#region IndexerNode *
	public class						IndexerNode : TypeMemberNode
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.IndexerDecl graph = new Dom.IndexerDecl();
			return this.GetGraph(graph);
		}	
		public Dom.IndexerDecl	
			GetGraph(Dom.IndexerDecl graph)
		{		
			// GetAccessor, SetAccessor, Type, Name, Params
			//graph.Clear();
			this.OpenScope(graph);
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is TypeRef)
				{
					graph.Type = (Dom.TypeRef)((TypeRef)tok).GetGraph();
				}
				if(tok is Ident)
				{
					// can't be array
					graph.InterfaceType = new Dom.TypeRef();
					graph.InterfaceType.TypeName = tok.getText();
				}
				else if(tok is DeclArgs) 
					 ((DeclArgs)tok).GetGraph(graph.Parameters);
				else if(tok is AccessorNode)
				{
					if(tok.getText() == "get")
					{
						//if(hasGet) // error;
						graph.HasGet = true;
						graph.GetAccessor = (Dom.AccessorDecl)((AccessorNode)tok).GetGraph();
					}
					else if(tok.getText() == "set")
					{
						//if(hasSet) // error;
						graph.HasSet = true;
						graph.SetAccessor = (Dom.AccessorDecl)((AccessorNode)tok).GetGraph();
					}
				}
				tok = tok.getNextSibling();
			}
			this.AddDefinition(graph, graph.Name, graph.Type);
			this.CloseScope();
			return graph;
		}

	}
		#endregion 
		#region OperatorNode *
	public class						OperatorNode : TypeMemberNode
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.OperatorDecl graph = 
				new Dom.OperatorDecl();
			return this.GetGraph(graph);
		}	
		public Dom.OperatorDecl	
			GetGraph(Dom.OperatorDecl graph)
		{		
			// FirstParameter, SecondParameter, Type, Statements
			//graph.Clear();
			this.OpenScope(graph);
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is DeclArgs)
				{
					DeclArgs atok = ((DeclArgs)tok);
					// must have one arg
					DeclArg arg1 = (DeclArg)(atok.getFirstChild());
					// may have max of two - might be null? (todo: catch null exception)
					AST arg2 = arg1.getNextSibling();
					graph.FirstParameter = (Dom.ParamDecl)arg1.GetGraph();
					if(arg2 != null) graph.SecondParameter = 
										 (Dom.ParamDecl)((DeclArg)arg2).GetGraph();
				}
				else if(tok is TypeRef)
				{
					graph.Type = (Dom.TypeRef)((TypeRef)tok).GetGraph();
				}
				else if(tok is Op)
				{
					string opType = ((Op)tok).getText();
					string op = tok.getFirstChild().getText();
					// test for unary minus and plus
					if(opType == "unary" && (op == "-" || op == "+") )
					{
						op += "u";
					}
					graph.Operator = Dom.OperatorDecl.OperatorFromString(op);
				}
				else if(tok is Statements)
					((Statements)tok).GetGraph(graph.Statements);

				tok = tok.getNextSibling();
			}
			this.AddDefinition(graph, graph.Name, graph.Type);
			this.CloseScope();
			return graph;
		}
	}
		#endregion 
		#region ConstructorNode *
	public class						ConstructorNode : TypeMemberNode
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.ConstructorDecl graph = 
				new Dom.ConstructorDecl();
			return this.GetGraph(graph);
		}	
		public Dom.ConstructorDecl	
			GetGraph(Dom.ConstructorDecl graph)
		{		
			// Parameters, InitExpression, Statements
			//graph.Clear();
			this.OpenScope(graph);
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is DeclArgs) 
					((DeclArgs)tok).GetGraph(graph.Parameters);
				else if(tok is Ident) 
					graph.Name = tok.getText();
				else if(tok is BaseRefExpr)
				{
					graph.InvokeBase = true;
					AST ctok = tok.getFirstChild();
					if(ctok != null)
						((Args)(ctok)).GetGraph(graph.BaseParameters);
				}
				else if(tok is ThisRefExpr)
				{
					graph.InvokeChain = true;
					AST ctok = tok.getFirstChild();
					if(ctok != null)
						((Args)(ctok)).GetGraph(graph.ChainParameters);
				}
				else if(tok is Statements)
					((Statements)tok).GetGraph(graph.Statements);

				tok = tok.getNextSibling();
			}
			this.AddDefinition(graph, graph.Name, this.curType); 
			this.CloseScope();
			return graph;
		}

	}
		#endregion 
		#region DestructorNode 
	public class						DestructorNode : TypeMemberNode
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.DestructorDecl graph = 
				new Dom.DestructorDecl();
			return this.GetGraph(graph);
		}	
		public Dom.DestructorDecl	
			GetGraph(Dom.DestructorDecl graph)
		{		
			// Statements
			//graph.Clear();
			this.OpenScope(graph);
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is Statements)
					((Statements)tok).GetGraph(graph.Statements);
				else if(tok is Ident) 
					graph.Name = tok.getText();

				tok = tok.getNextSibling();
			}
			this.AddDefinition(graph, graph.Name, null);
			this.CloseScope();
			return graph;
		}
	}
		#endregion 
		#region AccessorNode *
	public class						AccessorNode : TypeMemberNode
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.AccessorDecl graph = new Dom.AccessorDecl();
			return this.GetGraph(graph);
		}	
		public Dom.AccessorDecl	GetGraph(Dom.AccessorDecl graph)
		{		
			// Attributes, CustomAttributes, Statements
			//graph.Clear();
			this.OpenScope(graph);
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is Statements)
				{
					((Statements)tok).GetGraph(graph.Statements);
				}
				tok = tok.getNextSibling();
			}
			string tp = this.getText();
			if(tp != "")
			{
				if(tp == "get") 
					graph.AccessorModifier = Dom.AccessorModifiers.Get;
				else if(tp == "set") 
					graph.AccessorModifier = Dom.AccessorModifiers.Set;
				else if(tp == "add") 
					graph.AccessorModifier = Dom.AccessorModifiers.Add;
				else if(tp == "remove") 
					graph.AccessorModifier = Dom.AccessorModifiers.Remove;
				else return graph; // don't change if no match (shouldn't be...)
			}
			this.CloseScope();
			return graph;
		}
	}
	#endregion 
		#region EnumMemberNode 
	public class		EnumMemberNode : TypeMemberNode
	{	
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.EnumMemberDecl graph = new Dom.EnumMemberDecl();
			return this.GetGraph(graph);
		}	
		public Dom.EnumMemberDecl	GetGraph(Dom.EnumMemberDecl graph)
		{		
			// Attributes, CustomAttributes
			//graph.Clear();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is Ident) 
					graph.Name = tok.getText();
				else if(tok is Expression)
				{
					graph.Value = Expression.Parse(((Expression)tok));
				}		

				tok = tok.getNextSibling();
			}
			AddDefinition(graph, graph.Name, this.curType); // no new scope so just add def
			return graph;
		}
	}
	#endregion 

	#region BaseTypes 
	public class				BaseTypes : CSharpAST
	{
		// holder class
		public override Dom.IGraph		GetGraph()				
		{	
			Dom.TypeRefCollection graph = 
				new Dom.TypeRefCollection();
			return this.GetGraph(graph);
		}	
		public Dom.TypeRefCollection	
			GetGraph(Dom.TypeRefCollection graph)
		{	
			base.GetGraph(graph);
			AST tok = this.getFirstChild();
			while(tok != null)
			{			
				if(tok is TypeRef) 
				{
					graph.Add( (Dom.TypeRef)((UsingNode)tok).GetGraph() );
					tok = tok.getNextSibling();
				}
				else if (tok is Ident)
				{
					Dom.TypeRef tr = new Dom.TypeRef();
					tr.TypeName = tok.getText();
					tok = tok.getNextSibling();
					while (tok != null && tok is Ident)
					{
						tr.TypeName = tr.TypeName + "." + tok.getText();
						tok = tok.getNextSibling();
					}
					graph.Add(tr);
					this.AddSymbol(tr, tr.TypeName);
				}
				else
					tok = tok.getNextSibling();
					
					//if(tok.getText() != "") 
				/*{
					// can't be array...
					Dom.TypeRef tr = new Dom.TypeRef();
					tr.TypeName = tok.getText();
					graph.Add(tr);
					this.AddSymbol(tr, tr.TypeName);
				}*/
			}
			return graph;

		}

	}
	#endregion 

	#region	Statements *
	public class				Statements : CSharpAST
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.StatementCollection graph = 
				new Dom.StatementCollection();
			return this.GetGraph(graph);
		}	
		public Dom.StatementCollection	
			GetGraph(Dom.StatementCollection graph)
		{		
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				Dom.Statement stmt = Statement.ParseStatement( ((Statement)tok) );
				if(stmt != null)graph.Add(stmt);

				tok = tok.getNextSibling();
			}
			return graph;
		}
	}
	#endregion 
	#region	Statement 
	public class Statement : CSharpAST
	{
		public override Dom.IGraph	GetGraph()
		{
			throw new MethodAccessException("Code should never call GetGraph() on Statement");
		}
		/*
		protected override Dom.IGraph GetGraph(Dom.IGraph graph)
		{
			return base.GetGraph(graph);
		}
		*/
		public static Dom.Statement ParseStatement(Statement tok)
		{
			if(tok == null) return null;
			Dom.Statement stmt = null;

			if(tok is LabeledStmt)
				stmt = (Dom.LabeledStmt)((LabeledStmt)tok).GetGraph() ;
			else if(tok is ExprStmt)
				stmt = (Dom.ExprStmt)((ExprStmt)tok).GetGraph() ;
			else if(tok is VariableDeclStmt)
				stmt = (Dom.VariableDeclStmt)((VariableDeclStmt)tok).GetGraph() ;
			else if(tok is ConstantDeclStmt)
				stmt = (Dom.ConstantDeclStmt)((ConstantDeclStmt)tok).GetGraph() ;
			else if(tok is IfStmt)
				stmt = (Dom.IfStmt)((IfStmt)tok).GetGraph() ;
			else if(tok is SwitchStmt)
				stmt = (Dom.SwitchStmt)((SwitchStmt)tok).GetGraph() ;
			else if(tok is IterationStmt)
				stmt = (Dom.IterationStmt)((IterationStmt)tok).GetGraph() ;
			else if(tok is ForEachStmt)
				stmt = (Dom.ForEachStmt)((ForEachStmt)tok).GetGraph() ;
			else if(tok is GotoStmt)
				stmt = (Dom.GotoStmt)((GotoStmt)tok).GetGraph() ;
			else if(tok is ReturnStmt)
				stmt = (Dom.ReturnStmt)((ReturnStmt)tok).GetGraph() ;
			else if(tok is BreakStmt)
				stmt = (Dom.BreakStmt)((BreakStmt)tok).GetGraph() ;
			else if(tok is ContinueStmt)
				stmt = (Dom.ContinueStmt)((ContinueStmt)tok).GetGraph() ;
			else if(tok is CheckedStmt)
				stmt = (Dom.CheckedStmt)((CheckedStmt)tok).GetGraph() ;
			else if(tok is UncheckedStmt)
				stmt = (Dom.UncheckedStmt)((UncheckedStmt)tok).GetGraph() ;
			else if(tok is LockStmt)
				stmt = (Dom.LockStmt)((LockStmt)tok).GetGraph() ;
			else if(tok is UsingStmt)
				stmt = (Dom.UsingStmt)((UsingStmt)tok).GetGraph() ;
			else if(tok is TryCatchFinallyStmt)
				stmt = (Dom.TryCatchFinallyStmt)((TryCatchFinallyStmt)tok).GetGraph() ;
			else if(tok is ThrowStmt)
				stmt = (Dom.ThrowStmt)((ThrowStmt)tok).GetGraph() ;
			else if(tok is CommentNode)
				stmt = (Dom.CommentStmt)((CommentNode)tok).GetGraph() ;

			return stmt;

		}
	}
	#endregion 
		#region	LabeledStmt *
	public class				LabeledStmt : Statement
	{
		// Label, Statement
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.LabeledStmt graph = 
				new Dom.LabeledStmt();
			return this.GetGraph(graph);
		}	
		public Dom.LabeledStmt	
			GetGraph(Dom.LabeledStmt graph)
		{		
			//graph.Clear();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is Ident)
				{
					graph.Label = tok.getText();
				}	
				else if(tok is Statement)
					graph.Statement = Statement.ParseStatement( ((Statement)tok) );

				tok = tok.getNextSibling();
			}
			return graph;
		}
	}
	#endregion 
		#region	ExprStmt *
	public class				ExprStmt : Statement
	{
		// Expression
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.ExprStmt graph = 
				new Dom.ExprStmt();
			return this.GetGraph(graph);
		}	
		public Dom.ExprStmt	
			GetGraph(Dom.ExprStmt graph)
		{
			//graph.Clear();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			if(tok is Expression)
			{
				graph.Expression = Expression.Parse(((Expression)tok));
			}
			AddRootExpression(graph.Expression, graph.GetType(), "Expression");
			return graph;
		}

	}
	#endregion 
		#region	VariableDeclStmt *
	public class				VariableDeclStmt : Statement
	{
		// Init	// Name	// Type
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.VariableDeclStmt graph = 
				new Dom.VariableDeclStmt();
			return this.GetGraph(graph);
		}	
		public Dom.VariableDeclStmt	
			GetGraph(Dom.VariableDeclStmt graph)
		{		
			//graph.Clear();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is TypeRef)
				{
					graph.Type = (Dom.TypeRef)((TypeRef)tok).GetGraph();
				}
				else if(tok is Declarator) 
				{
					((Declarator)tok).GetGraph(graph.Delcarators);
				}
				tok = tok.getNextSibling();
			}
			foreach(Dom.Declarator d in graph.Delcarators)
			{
				d.Definition.Type = graph.Type;
			}
			return graph;
		}
	}
	#endregion 
		#region	ConstantDeclStmt *
	public class				ConstantDeclStmt : Statement
	{
		// Init	// Name	// Type
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.ConstantDeclStmt graph = 
				new Dom.ConstantDeclStmt();
			return this.GetGraph(graph);
		}	
		public Dom.ConstantDeclStmt	
			GetGraph(Dom.ConstantDeclStmt graph)
		{		
			//graph.Clear();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				 if(tok is TypeRef)
				{
					graph.Type = (Dom.TypeRef)((TypeRef)tok).GetGraph();
				}
				else if(tok is Declarator) 
				{
					((Declarator)tok).GetGraph(graph.Delcarators);
				}
				
				tok = tok.getNextSibling();
			}
			foreach(Dom.Declarator d in graph.Delcarators)
			{
				d.Definition.Type = graph.Type;
			}
			return graph;
		}
	}
	#endregion 
		#region	IfStmt *
	public class				IfStmt : Statement
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.IfStmt graph = 
				new Dom.IfStmt();
			return this.GetGraph(graph);
		}	
		public Dom.IfStmt	
			GetGraph(Dom.IfStmt graph)
		{		
			// Condition, FalseStatements, TrueStatements
			//graph.Clear();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is Statements)
				{
					string tf = tok.getText();
					if(tf == "true")
					{
						this.OpenBlockScope();
						((Statements)tok).GetGraph(graph.TrueStatements);
						this.CloseScope();
					}
					else if(tf == "false")
					{
						this.OpenBlockScope();
						((Statements)tok).GetGraph(graph.FalseStatements);
						this.CloseScope();
					}
				}
				else if(tok is Expression)
				{
					graph.Condition = Expression.Parse(((Expression)tok));
				}
				tok = tok.getNextSibling();
			}
			AddRootExpression(graph.Condition, graph.GetType(), "Condition");
			return graph;
		}

	}
	#endregion 
		#region	SwitchStmt *
	public class				SwitchStmt : Statement
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.SwitchStmt graph = 
				new Dom.SwitchStmt();
			return this.GetGraph(graph);
		}	
		public Dom.SwitchStmt	
			GetGraph(Dom.SwitchStmt graph)
		{		
			// Condition, Cases
			//graph.Clear();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is SwitchSection)
				{
					graph.Cases.Add( (Dom.Case)((SwitchSection)tok).GetGraph() );
				}
				else if(tok is Expression)
				{
					graph.Condition = Expression.Parse(((Expression)tok));
				}
				tok = tok.getNextSibling();
			}
			AddRootExpression(graph.Condition, graph.GetType(), "Condition");
			return graph;
		}

	}
	#endregion 
			#region	SwitchSection *
	public class				SwitchSection : CSharpAST
	{
		// IsDefault, Condition, Statements
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.Case graph = 
				new Dom.Case();
			return this.GetGraph(graph);
		}	
		public Dom.Case	
			GetGraph(Dom.Case graph)
		{		
			// Condition, FalseStatements, TrueStatements
			base.GetGraph(graph);
			this.OpenBlockScope();

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is Statements)
				{
					((Statements)tok).GetGraph(graph.Statements);
				}
				else if(tok is Expression)
				{
					if(tok.getText() == "default")
						graph.IsDefault = true;
					else
						graph.Condition = Expression.Parse(((Expression)tok));
				}
				tok = tok.getNextSibling();
			}
			this.CloseScope();
			AddRootExpression(graph.Condition, graph.GetType(), "Condition");
			return graph;
		}

	}
	#endregion 
		#region	IterationStmt *
	public class				IterationStmt : Statement
	{
		// InitStatement, IncrementStatement, TestExpression, Statements, TestFirst (bool)
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.IterationStmt graph = 
				new Dom.IterationStmt();
			return this.GetGraph(graph);
		}	
		public Dom.IterationStmt	
			GetGraph(Dom.IterationStmt graph)
		{		
			//graph.Clear();
			this.OpenBlockScope();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is InitStmt)
				{
					((Statements)tok).GetGraph(graph.Init);
				}
				else if(tok is IncStmt)
				{
					((Statements)tok).GetGraph(graph.Increment);
				}
				else if(tok is Statements)
				{
					((Statements)tok).GetGraph(graph.Statements);
				}
				else if(tok is Expression) // test expr
				{
					if(tok.getText() == "do") 
						graph.IterationType = Dom.IterationType.Do;
					else if(tok.getText() == "while") 
						graph.IterationType = Dom.IterationType.While;
					else if(tok.getText() == "for") 
						graph.IterationType = Dom.IterationType.For;					

					graph.Test = Expression.Parse(((Expression)tok));
				}


				tok = tok.getNextSibling();
			}
			this.CloseScope();
			AddRootExpression(graph.Test, graph.GetType(), "Test");
			return graph;
		}

	}
	#endregion 
			#region	InitStmt *
	public class				InitStmt : Statements
	{
		// Holder class for Init Statements in loop
	}
	#endregion 
			#region	IncStmt *
	public class				IncStmt : Statements
	{
		// Holder class for Inc Statements in loop
	}
	#endregion 
		#region	ForEachStmt *
	public class				ForEachStmt : Statement
	{
		// InitStatement, IncrementStatement, TestExpression, Statements, TestFirst (bool)
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.ForEachStmt graph = 
				new Dom.ForEachStmt();
			return this.GetGraph(graph);
		}	
		public Dom.ForEachStmt	
			GetGraph(Dom.ForEachStmt graph)
		{		
			//graph.Clear();
			this.OpenBlockScope();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{				
				if(tok is Ident)
					graph.Name = tok.getText();
				else if(tok is TypeRef)
				{
					graph.Type = (Dom.TypeRef)((TypeRef)tok).GetGraph();
				}
				else if(tok is Statements)
				{
					((Statements)tok).GetGraph(graph.Statements);
				}
				else if(tok is Expression)
				{
					graph.Collection = Expression.Parse(((Expression)tok));
				}

				tok = tok.getNextSibling();
			}
			this.CloseScope();
			AddRootExpression(graph.Collection, graph.GetType(), "Collection");
			return graph;
		}

	}
	#endregion 
		#region	GotoStmt *
	public class				GotoStmt : Statement
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.GotoStmt graph = 
				new Dom.GotoStmt();
			return this.GetGraph(graph);
		}	
		public Dom.GotoStmt	
			GetGraph(Dom.GotoStmt graph)
		{		
			//graph.Clear();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is Ident)
				{
					graph.Label = tok.getText();
				}	
				if(tok is ModifierAttributes)
				{
					graph.Label = tok.getFirstChild().getText();
				}	
				else if(tok is Expression)
				{
					graph.CaseLabel = Expression.Parse(((Expression)tok));
				}
				tok = tok.getNextSibling();
			}
			AddRootExpression(graph.CaseLabel, graph.GetType(), "CaseLabel");
			return graph;
		}
	}
	#endregion 
		#region	ReturnStmt *
	public class				ReturnStmt : Statement
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.ReturnStmt graph = 
				new Dom.ReturnStmt();
			return this.GetGraph(graph);
		}	
		public Dom.ReturnStmt	
			GetGraph(Dom.ReturnStmt graph)
		{		
			//graph.Clear();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is Expression)
				{
					graph.Expression = Expression.Parse(((Expression)tok));
				}	
				tok = tok.getNextSibling();
			}
			AddRootExpression(graph.Expression, graph.GetType(), "Expression");
			return graph;
		}

	}
	#endregion 
		#region	BreakStmt *
	public class				BreakStmt : Statement
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.BreakStmt graph = 
				new Dom.BreakStmt();
			return this.GetGraph(graph);
		}	
		public Dom.BreakStmt	
			GetGraph(Dom.BreakStmt graph)
		{		
			//graph.Clear();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{	
				tok = tok.getNextSibling();
			}
			return graph;
		}
	}
	#endregion 
		#region	ContinueStmt *
	public class				ContinueStmt : Statement
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.ContinueStmt graph = 
				new Dom.ContinueStmt();
			return this.GetGraph(graph);
		}	
		public Dom.ContinueStmt	
			GetGraph(Dom.ContinueStmt graph)
		{		
			//graph.Clear();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{	
				tok = tok.getNextSibling();
			}
			return graph;
		}
	}
	#endregion 
		#region	CheckedStmt *
	public class				CheckedStmt : Statement
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.CheckedStmt graph = 
				new Dom.CheckedStmt();
			return this.GetGraph(graph);
		}	
		public Dom.CheckedStmt	
			GetGraph(Dom.CheckedStmt graph)
		{		
			//graph.Clear();
			this.OpenBlockScope();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is Statements)
				{
					((Statements)tok).GetGraph(graph.Statements);
				}
				tok = tok.getNextSibling();
			}
			this.CloseScope();
			return graph;
		}
	}
	#endregion 
		#region	UncheckedStmt *
	public class				UncheckedStmt : Statement
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.UncheckedStmt graph = 
				new Dom.UncheckedStmt();
			return this.GetGraph(graph);
		}	
		public Dom.UncheckedStmt	
			GetGraph(Dom.UncheckedStmt graph)
		{		
			//graph.Clear();
			this.OpenBlockScope();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is Statements)
				{
					((Statements)tok).GetGraph(graph.Statements);
				}
				tok = tok.getNextSibling();
			}
			this.CloseScope();
			return graph;
		}
	}
	#endregion 
		#region	LockStmt *
	public class				LockStmt : Statement
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.LockStmt graph = 
				new Dom.LockStmt();
			return this.GetGraph(graph);
		}	
		public Dom.LockStmt	
			GetGraph(Dom.LockStmt graph)
		{		
			//graph.Clear();
			this.OpenBlockScope();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is Statements)
				{
					((Statements)tok).GetGraph(graph.Statements);
				}
				else if(tok is Expression)
				{
					graph.Target = Expression.Parse(((Expression)tok));
				}
				tok = tok.getNextSibling();
			}
			this.CloseScope();
			AddRootExpression(graph.Target, graph.GetType(), "Target");
			return graph;
		}
	}
	#endregion 
		#region	UsingStmt *
	public class				UsingStmt : Statement
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.UsingStmt graph = 
				new Dom.UsingStmt();
			return this.GetGraph(graph);
		}	
		public Dom.UsingStmt
			GetGraph(Dom.UsingStmt graph)
		{		
			//graph.Clear();
			this.OpenBlockScope();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is Statements)
				{
					((Statements)tok).GetGraph(graph.Statements);
				}
				else if(tok is Expression)
				{
					AST ctok = tok.getFirstChild();
					while(ctok != null)
					{
						if(ctok is Expression)
						{
							graph.Target = Expression.Parse(((Expression)ctok));
						}
						else if(ctok is VariableDeclStmt)
						{
							graph.Declaration = 
								(Dom.VariableDeclStmt)((VariableDeclStmt)ctok).GetGraph();
						}
						ctok = ctok.getNextSibling();
					}
					
				}
				tok = tok.getNextSibling();
			}
			this.CloseScope();
			AddRootExpression(graph.Target, graph.GetType(), "Target");
			return graph;
		}
	}
	#endregion 
		#region	ThrowStmt 
	public class				ThrowStmt : Statement
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.ThrowStmt graph = 
				new Dom.ThrowStmt();
			return this.GetGraph(graph);
		}	
		public Dom.ThrowStmt	
			GetGraph(Dom.ThrowStmt graph)
		{		
			//graph.Clear();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is Expression)
				{
					graph.ToThrow = Expression.Parse(((Expression)tok));
				}
				tok = tok.getNextSibling();
			}
			AddRootExpression(graph.ToThrow, graph.GetType(), "ToThrow");
			return graph;
		}


	}
	#endregion 
		#region	TryCatchFinallyStmt 
	public class				TryCatchFinallyStmt : Statement
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.TryCatchFinallyStmt graph = 
				new Dom.TryCatchFinallyStmt();
			return this.GetGraph(graph);
		}	
		public Dom.TryCatchFinallyStmt	
			GetGraph(Dom.TryCatchFinallyStmt graph)
		{		
			//graph.Clear();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is CatchClause)
				{
					this.OpenBlockScope();
					graph.CatchClauses.Add( 
						(Dom.Catch)((CatchClause)tok).GetGraph() );
					this.CloseScope();
				}
				else if(tok is TryStmt)
				{
					this.OpenBlockScope();
					Statements stmtsTok = (Statements)tok.getFirstChild();
					stmtsTok.GetGraph(graph.Try);
					this.CloseScope();
				}
				else if(tok is FinallyStmt)
				{
					this.OpenBlockScope();
					Statements stmtsTok = (Statements)tok.getFirstChild();
					stmtsTok.GetGraph(graph.Finally);
					this.CloseScope();
				}

				tok = tok.getNextSibling();
			}
			return graph;
		}

	}
	#endregion 
			#region	TryStmt *
	public class				TryStmt : CSharpAST
	{
		public override Dom.IGraph	GetGraph()
		{
			throw new MethodAccessException("Code should never call GetGraph() on TryStmt");
		}
		// Holder class
	}
	#endregion 
			#region	CatchClause *
	public class				CatchClause : Statement
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.Catch graph = 
				new Dom.Catch();
			return this.GetGraph(graph);
		}	
		public Dom.Catch	
			GetGraph(Dom.Catch graph)
		{		
			//graph.Clear();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{				
				if(tok is Ident)
					graph.LocalName = tok.getText();
				else if(tok is TypeRef)
				{
					graph.CatchExceptionType = (Dom.TypeRef)((TypeRef)tok).GetGraph();
				}
				else if(tok is Statements)
				{
					((Statements)tok).GetGraph(graph.Statements);
				}

				tok = tok.getNextSibling();
			}
			return graph;
		}
	}
	#endregion 
			#region	FinallyStmt *
	public class				FinallyStmt : CSharpAST
	{
		public override Dom.IGraph	GetGraph()
		{
			throw new MethodAccessException("Code should never call GetGraph() on FinallyStmt");
		}
		// Holder class
	}
	#endregion 
		#region CommentNode *
	public class						CommentNode : Statement
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.CommentStmt graph = 
				new Dom.CommentStmt();
			return this.GetGraph(graph);
		}	
		public Dom.CommentStmt	
			GetGraph(Dom.CommentStmt graph)
		{		
			//graph.Clear();
			//base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is StringLiteral)
				{
					graph.Comment.CommentText = tok.getText();
				}	

				tok = tok.getNextSibling();
			}
			return graph;
		}

		public Dom.CommentStmtCollection	
			GetGraph(Dom.CommentStmtCollection graph)
		{		
			//graph.Clear();
			//base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is CommentNode)
				{
					Dom.CommentStmt cs = new Dom.CommentStmt();
					graph.Add( this.GetGraph(cs) );
				}	
				tok = tok.getNextSibling();
			}
			return graph;
		}

	}
	#endregion 
	
	#region	Expressions 
	public class				Expressions : CSharpAST
	{
		public override Dom.IGraph	GetGraph()
		{
			throw new MethodAccessException("Code should never call GetGraph() on Expressions");
		}
	}
	#endregion 
	#region	Expression 
	public class				Expression : CSharpAST
	{
		public override Dom.IGraph	GetGraph()
		{
			throw new MethodAccessException("Code should never call GetGraph() on Expression");
		}
		// maybe get rid of this too...
		public override Dom.IGraph GetGraph(Dom.IGraph graph)
		{
			return base.GetGraph(graph);
		}
		// from here on use a static method to avoid automatic creation 
		// of generic expression in Dom classes, and then discarding it 
		// for a more specific type.
		// Probably a better way to avoid this...
		public static Dom.Expression Parse(Expression tok)
		{		
			if(tok == null) return null;
			Dom.Expression de = null;

			if(tok is AssignExpr)
				de = (Dom.Expression)((AssignExpr)tok).GetGraph();
			else if(tok is UnaryExpr)
				de = (Dom.UnaryExpr)((UnaryExpr)tok).GetGraph();
			else if(tok is BinaryExpr)
				de = (Dom.BinaryExpr)((BinaryExpr)tok).GetGraph();
			else if(tok is TernaryExpr)
				de = (Dom.TernaryExpr)((TernaryExpr)tok).GetGraph();
			else if(tok is CastExpr)
				de = (Dom.CastExpr)((CastExpr)tok).GetGraph();
			else if(tok is SubExpr)
				de = (Dom.SubExpr)((SubExpr)tok).GetGraph();
				// TODO: ternary expressions
			else if(tok is PrimaryExpression)
				de = PrimaryExpression.ParsePrimaryExpr(tok);

			// if this is 'just' and expression indicator, the meat is below.
			else if(!(tok is PrimaryFollowExpression))
			{
				AST ctok = tok.getFirstChild();
				try
				{
					Expression etok = (Expression)ctok;
					return Expression.Parse(etok);
				}
				catch(Exception e)
				{
					Console.WriteLine("Missed Expression type: " + ctok.Type + " - " + e);
				}

			}
			AST ntok = tok.getNextSibling();
			if(ntok != null && ntok is PrimaryFollowExpression)
			{
				de = PrimaryFollowExpression.ParseFollow(
					(PrimaryFollowExpression)ntok, de);
			}
			return de;
		}

	}
	#endregion 
		#region	AssignExpr *
	public class				AssignExpr : Expression
	{
		// Left Expression, Right Expression
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.AssignExpr graph = 
				new Dom.AssignExpr();
			return this.GetGraph(graph);
		}	
		public Dom.AssignExpr	
			GetGraph(Dom.AssignExpr graph)
		{		
			//graph.Clear();
			base.GetGraph(graph);
			graph.Operator = 
				Dom.AssignExpr.OperatorFromString(this.getText());

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is Expression)
				{
					string side = tok.getText(); // left or right
					if(side == "left")
					{
						AST ltok = tok.getFirstChild();
						if(ltok is Expression)
							graph.Left = Expression.Parse(((Expression)ltok));
					}
					else if (side == "right")
					{
						AST rtok = tok.getFirstChild();
						if(rtok is Expression)
							graph.Right = Expression.Parse(((Expression)rtok));
					}
				}
				tok = tok.getNextSibling();
			}
			return graph;
		}
			 
					 			
	}				 
	#endregion		 
		#region	UnaryExpr *
	public class				UnaryExpr : Expression
	{
		// Left Expression, Right Expression
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.UnaryExpr graph = 
				new Dom.UnaryExpr();
			return this.GetGraph(graph);
		}	
		public Dom.UnaryExpr	
			GetGraph(Dom.UnaryExpr graph)
		{		
			//graph.Clear();
			base.GetGraph(graph);
			graph.Operator = 
				Dom.UnaryExpr.OperatorFromString(this.getText());

			AST tok = this.getNextSibling();
			if(tok is Expression)
			{
				graph.Right = Expression.Parse(((Expression)tok));
			}

			return graph;
		}	
	}
	#endregion 
		#region	BinaryExpr *
	public class				BinaryExpr : Expression
	{
		// Left Expression, Right Expression
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.BinaryExpr graph = 
				new Dom.BinaryExpr();
			return this.GetGraph(graph);
		}	
		public Dom.BinaryExpr	
			GetGraph(Dom.BinaryExpr graph)
		{		
			//graph.Clear();
			base.GetGraph(graph);
			graph.Operator = 
				Dom.BinaryExpr.OperatorFromString(this.getText());

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is Expression)
				{
					string side = tok.getText(); // left or right
					if(side == "left")
					{
						AST ltok = tok.getFirstChild();
						if(ltok is Expression)
							graph.Left = Expression.Parse(((Expression)ltok));
					}
					else if (side == "right")
					{
						AST rtok = tok.getFirstChild();
						if(rtok is Expression)
							graph.Right = Expression.Parse(((Expression)rtok));
					}
				}

				tok = tok.getNextSibling();
			}
			return graph;
		}
	}
	#endregion 
		#region	TernaryExpr 
	public class				TernaryExpr : Expression
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.TernaryExpr graph = 
				new Dom.TernaryExpr();
			return this.GetGraph(graph);
		}	
		public Dom.TernaryExpr	
			GetGraph(Dom.TernaryExpr graph)
		{		
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is Expression)
				{
					string side = tok.getText(); // left or right
					if(side == "test")
					{
						AST tstok = tok.getFirstChild();
						if(tstok is Expression)
							graph.Test = Expression.Parse(((Expression)tstok));
					}
					else if(side == "true")
					{
						AST ttok = tok.getFirstChild();
						if(ttok is Expression)
							graph.True = Expression.Parse(((Expression)ttok));
					}
					else if (side == "false")
					{
						AST ftok = tok.getFirstChild();
						if(ftok is Expression)
							graph.False = Expression.Parse(((Expression)ftok));
					}
				}
				tok = tok.getNextSibling();
			}
			return graph;
		}
	}
	#endregion
		#region	CastExpr 
	public class				CastExpr : Expression
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.CastExpr graph = 
				new Dom.CastExpr();
			return this.GetGraph(graph);
		}	
		public Dom.CastExpr	
			GetGraph(Dom.CastExpr graph)
		{		
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is TypeRef)
				{
					graph.TargetType = (Dom.TypeRef)((TypeRef)tok).GetGraph();
				}
				else if(tok is Expression)
				{
					graph.Expression = Expression.Parse(((Expression)tok));
				}
				tok = tok.getNextSibling();
			}
			return graph;
		}
	}
	#endregion 
		#region	SubExpr 
	public class				SubExpr : Expression
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.SubExpr graph = new Dom.SubExpr();
			return this.GetGraph(graph);
		}	
		public Dom.SubExpr GetGraph(Dom.SubExpr graph)
		{		
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is Expression)
				{
					graph.Expression = Expression.Parse(((Expression)tok));
				}
				tok = tok.getNextSibling();
			}
			return graph;
		}
	}
	#endregion 

	#region TypeRef 
	public class						TypeRef : Expression
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.TypeRef graph = new Dom.TypeRef();
			return this.GetGraph(graph);
		}	
		public Dom.TypeRef	GetGraph(Dom.TypeRef graph)
		{
			base.GetGraph(graph);
			ArrayList dimensions = new ArrayList();

			AST tok = this.getFirstChild();
			while(tok != null)
			{	
				if(tok is Ident)
				{
					graph.TypeName = tok.getText();
				}
				if(tok is BuiltInType)
				{
					graph.TypeName = tok.getText();
					graph.Definition = AddBuiltInDefinition(graph.TypeName);
				}
				else if(tok is ArrayRankExpr)
				{
					Dom.RankSpecifier rank = new Dom.RankSpecifier();
					if(tok.getText() != "")
						rank.Dimensions = Int32.Parse( tok.getText());
					graph.ArrayRanks.Add(rank);
				}
				tok = tok.getNextSibling();
			}
			// add to symbolTable
			this.AddSymbol(graph, graph.TypeName);

			return graph;
		}
	}
	#endregion 
		#region QualIdent 
	public class						QualIdent : Expression
	{
		public static string GetFullName(QualIdent qiTok)
		{		
			string name = "";
			string dot = "";
			AST tok = qiTok.getFirstChild();
			while(tok != null)
			{
				if(tok is Ident)
				{
					name += dot + tok.getText();
					dot = ".";
				}
				tok = tok.getNextSibling();
			}
			return name;
		}
		


	}
	#endregion 
		#region DeclArg 
	public class						DeclArg : Expression
	{
		// TODO: temp - fill in
		public override Dom.IGraph		GetGraph()				
		{	
			Dom.ParamDecl graph = 
				new Dom.ParamDecl();
			return this.GetGraph(graph);
		}	
		public Dom.ParamDecl	
			GetGraph(Dom.ParamDecl graph)
		{	
			//graph.Clear();
			base.GetGraph(graph);
			
			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is TypeRef)
				{
					graph.Type = (Dom.TypeRef)((TypeRef)tok).GetGraph();
				}
				else if(tok is Ident) 
					graph.Name = tok.getText();
				else if(tok is ModifierAttributes)
				{
					AST ntok = tok.getFirstChild();
					if(ntok != null && ntok.getText() == "params")
						graph.IsParams = true;
				}
				else if(tok is CustomAttributes)
					((CustomAttributes)tok).GetGraph(graph.CustomAttributes);
				else if(tok is ArgDirection)
				{
					if(tok.getText() == "ref")
						graph.Direction = Dom.ParamDirection.Ref;
					else if(tok.getText() == "out")
						graph.Direction = Dom.ParamDirection.Out;
				}
				tok = tok.getNextSibling();
			}
			this.AddDefinition(graph, graph.Name, graph.Type);
			return graph;
		}
	}
	#endregion 
		#region Arg 
	public class						Arg : Expression
	{
		// TODO: temp - fill in
		public override Dom.IGraph		GetGraph()				
		{	
			Dom.Param graph = 
				new Dom.Param();
			return this.GetGraph(graph);
		}	
		public Dom.Param	
			GetGraph(Dom.Param graph)
		{	
			//graph.Clear();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is ArgDirection)
				{
					if(tok.getText() == "ref")
						graph.Direction = Dom.ParamDirection.Ref;
					else if(tok.getText() == "out")
						graph.Direction = Dom.ParamDirection.Out;
				}
				else if(tok is Expression)
				{
					graph.Value = Expression.Parse(((Expression)tok));
				}
				tok = tok.getNextSibling();
			}
			return graph;
		}
	}
	#endregion 
 
	#region	PrimaryExpression
	public class				PrimaryExpression : Expression
	{		
		// the sibling tokens get nested in reverse on the target methods -
		// eg: x.y() becomes methodInvoke(target=(memberAccess y(target = x)))
		public static Dom.Expression ParsePrimaryExpr(Expression tok)
		{		
			Dom.Expression ret = null;
			// literals
			if(tok is BooleanLiteral)
				ret = (Dom.BooleanLiteral)((BooleanLiteral)tok).GetGraph();
			else if(tok is IntegerLiteral)
				ret = (Dom.IntegerLiteral)((IntegerLiteral)tok).GetGraph();
			else if(tok is RealLiteral)
				ret = (Dom.RealLiteral)((RealLiteral)tok).GetGraph();
			else if(tok is CharLiteral)
				ret = (Dom.CharLiteral)((CharLiteral)tok).GetGraph();
			else if(tok is StringLiteral)
				ret = (Dom.StringLiteral)((StringLiteral)tok).GetGraph();
			else if(tok is NullLiteral)
				ret = (Dom.NullLiteral)((NullLiteral)tok).GetGraph();
			
			// primary
			else if(tok is Ident)
				ret = (Dom.UnknownReference)((Ident)tok).GetGraph();
			else if(tok is BuiltInType)
				ret = (Dom.BuiltInType)((BuiltInType)tok).GetGraph();
			else if(tok is ThisRefExpr)
				ret = (Dom.ThisRef)((ThisRefExpr)tok).GetGraph();
			else if(tok is BaseRefExpr)
				ret = (Dom.BaseRef)((BaseRefExpr)tok).GetGraph();
			else if(tok is ObjectCreateExpr)
				ret = (Dom.ObjectCreateExpr)((ObjectCreateExpr)tok).GetGraph();
			else if(tok is ArrayCreateExpr)
				ret = (Dom.ArrayCreateExpr)((ArrayCreateExpr)tok).GetGraph();
			else if(tok is ArrayInitExpr)
				ret = (Dom.ArrayInitializer)((ArrayInitExpr)tok).GetGraph();
			else if(tok is TypeOfExpr)
				ret = (Dom.TypeOfExpr)((TypeOfExpr)tok).GetGraph();

			else
				Console.WriteLine("Unmatched Primary Expr - Type: "+
					tok.Type.ToString() + " Text: " + tok.getText());

//			AST ntok = tok.getNextSibling();
//			if(ntok != null && ntok is PrimaryFollowExpression) 
//				return PrimaryFollowExpression.ParseFollow(
//								(PrimaryFollowExpression)ntok, ret);
//			else
				return ret;
		}
	
	}				 
	#endregion		 
		#region Ident *
	public class						Ident : PrimaryExpression
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.UnknownReference v = new Dom.UnknownReference();
			v.VariableName = this.getText();
			this.AddSymbol(v, v.VariableName);
			return v;
		}
	}
	#endregion 
		#region BuiltInType 
	public class					BuiltInType : PrimaryExpression
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.BuiltInType graph = new Dom.BuiltInType();
			return this.GetGraph(graph);
		}	
		public Dom.BuiltInType	GetGraph(Dom.BuiltInType graph)
		{
			base.GetGraph(graph);

			graph.TypeName = this.getText();
			return graph;
		}
	}
	#endregion 

		#region	ThisRefExpr *
	public class				ThisRefExpr : PrimaryExpression
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.ThisRef graph = 
				new Dom.ThisRef();
			return this.GetGraph(graph);
		}	
		public Dom.ThisRef	
			GetGraph(Dom.ThisRef graph)
		{		
			//graph.Clear();
			base.GetGraph(graph);
			return graph;
		}	
	}
	#endregion 
		#region	BaseRefExpr *
	public class				BaseRefExpr : PrimaryExpression
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.BaseRef graph = 
				new Dom.BaseRef();
			return this.GetGraph(graph);
		}	
		public Dom.BaseRef	
			GetGraph(Dom.BaseRef graph)
		{		
			//graph.Clear();
			base.GetGraph(graph);
			return graph;
		}	
	}
	#endregion 
		#region	ArrayCreateExpr 
	public class				ArrayCreateExpr : PrimaryExpression
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.ArrayCreateExpr graph = 
				new Dom.ArrayCreateExpr();
			return this.GetGraph(graph);
		}	
		public Dom.ArrayCreateExpr	
			GetGraph(Dom.ArrayCreateExpr graph)
		{		
			// Parameters, CreateType
			//graph.Clear();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is TypeRef)
				{
					graph.CreateType = (Dom.TypeRef)((TypeRef)tok).GetGraph();
				}
				else if(tok is IndexerExpr)
				{
					((IndexerExpr)tok).ParseIndexerSizes(graph.Sizes);
				}
				else if(tok is ArrayInitExpr)
				{
					graph.Initializer = 
						(Dom.ArrayInitializer)((ArrayInitExpr)tok).GetGraph();
				}
				else if(tok is ArrayRankExpr)
				{
					// ranks must come after first set, and can't have init values
					Dom.RankSpecifier rnk = new Dom.RankSpecifier();
					rnk.Dimensions = Int32.Parse(tok.getText());
					graph.RankSpecifiers.Add(rnk);
				}
				tok = tok.getNextSibling();
			}
			// find which was bigger, the defined number of dimensions, or the init'ed
			graph.DimensionCount = graph.Sizes.Count;
			if(	graph.Initializer != null &&
				graph.Initializer.InitialValues.Count > 0 &&  
				(graph.Initializer.InitialValues[0] is Dom.ArrayInitializer ||
				graph.Initializer.InitialValues[0] is Dom.ArrayCreateExpr) )
			{
				if(graph.Initializer.InitialValues.Count > graph.DimensionCount)
					graph.DimensionCount = graph.Initializer.InitialValues.Count;
			}
			return graph;
		}

	}
	#endregion 
		#region	ObjectCreateExpr *
	public class				ObjectCreateExpr : PrimaryExpression
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.ObjectCreateExpr graph = 
				new Dom.ObjectCreateExpr();
			return this.GetGraph(graph);
		}	
		public Dom.ObjectCreateExpr	
			GetGraph(Dom.ObjectCreateExpr graph)
		{		
			// Note: Delegate and ObjectCreate are the same until attribution
			// Parameters, CreateType
			//graph.Clear();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is Args) 
					((Args)tok).GetGraph(graph.Parameters);
				else if(tok is TypeRef)
				{
					graph.CreateType = (Dom.TypeRef)((TypeRef)tok).GetGraph();
				}
				tok = tok.getNextSibling();
			}
			return graph;
		}

	}
	#endregion 
		#region	TypeOfExpr *
	public class				TypeOfExpr : PrimaryExpression
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.TypeOfExpr graph = 
				new Dom.TypeOfExpr();
			return this.GetGraph(graph);
		}	
		public Dom.TypeOfExpr	
			GetGraph(Dom.TypeOfExpr graph)
		{		
			//graph.Clear();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is TypeRef)
				{
					graph.Type = (Dom.TypeRef)((TypeRef)tok).GetGraph();
				}
				tok = tok.getNextSibling();
			}
			return graph;
		}
	}
	#endregion 
		#region	CheckedExpr 
	public class				CheckedExpr : PrimaryExpression
	{
	}
	#endregion 
		#region	UncheckedExpr 
	public class				UncheckedExpr : PrimaryExpression
	{
	}
	#endregion 
		//	sizeof_expression
		//	predefined_type_access
		#region	PrimitiveExpr 
	public class				PrimitiveExpr : PrimaryExpression
	{
	}

	#endregion 
			#region  BooleanLiteral  *
	public class BooleanLiteral : PrimitiveExpr  
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.BooleanLiteral graph = 
				new Dom.BooleanLiteral();
			return this.GetGraph(graph);
		}	
		public Dom.BooleanLiteral	
			GetGraph(Dom.BooleanLiteral graph)
		{		
			//graph.Clear();
			base.GetGraph(graph);
			string txt = this.getText();
			if(txt.ToUpper()== "TRUE") graph.Value = true;
			else graph.Value = false;

			graph.ResultType = AddBuiltInDefinition("bool");
			return graph;
		}	
	}
	#endregion
			#region  IntegerLiteral  *
	public class IntegerLiteral : PrimitiveExpr  
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.IntegerLiteral graph = 
				new Dom.IntegerLiteral();
			return this.GetGraph(graph);
		}	
		public Dom.IntegerLiteral	
			GetGraph(Dom.IntegerLiteral graph)
		{		
			//graph.Clear();
			base.GetGraph(graph);
			string nm = getText();
			if(nm.StartsWith("0x"))
			{
				string nmp = nm.Substring(2, nm.Length-2);

				graph.Value = Int32.Parse(nmp, NumberStyles.HexNumber);
			}
			else
				graph.Value = Int32.Parse(this.getText(),NumberStyles.Integer);

			graph.ResultType = AddBuiltInDefinition("int");
			return graph;
		}	
	}
	#endregion
			#region  RealLiteral  *
	public class RealLiteral : PrimitiveExpr  
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.RealLiteral graph = 
				new Dom.RealLiteral();
			return this.GetGraph(graph);
		}	
		public Dom.RealLiteral	
			GetGraph(Dom.RealLiteral graph)
		{		
			//graph.Clear();
			base.GetGraph(graph);
			NumberFormatInfo nfi = CultureInfo.CreateSpecificCulture( "en" ).NumberFormat;
			graph.Value = Double.Parse(this.getText(), NumberStyles.Float, nfi);
			if(graph.Value <= float.MaxValue)
				graph.ResultType = AddBuiltInDefinition("float");
			else
				graph.ResultType = AddBuiltInDefinition("double");
			return graph;
		}	
	}
	#endregion
			#region  CharLiteral  *
	public class CharLiteral : PrimitiveExpr  
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.CharLiteral graph = 
				new Dom.CharLiteral();
			return this.GetGraph(graph);
		}	
		public Dom.CharLiteral	
			GetGraph(Dom.CharLiteral graph)
		{		
			//graph.Clear();
			base.GetGraph(graph);
			graph.Value = Char.Parse(this.getText());
			graph.ResultType = AddBuiltInDefinition("char");
			return graph;
		}	
	}
	#endregion
			#region  StringLiteral  *
	public class StringLiteral : PrimitiveExpr  
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.StringLiteral graph = 
				new Dom.StringLiteral();
			return this.GetGraph(graph);
		}	
		public Dom.StringLiteral	
			GetGraph(Dom.StringLiteral graph)
		{		
			//graph.Clear();
			base.GetGraph(graph);
			graph.Value = this.getText();
			graph.ResultType = AddBuiltInDefinition("string");
			return graph;
		}	
	}
	#endregion
			#region  NullLiteral  *
	public class NullLiteral : PrimitiveExpr  
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.NullLiteral graph = 
				new Dom.NullLiteral();
			return this.GetGraph(graph);
		}	
		public Dom.NullLiteral	
			GetGraph(Dom.NullLiteral graph)
		{		
			//graph.Clear();
			base.GetGraph(graph);
			graph.Value = null;
			graph.ResultType = AddBuiltInDefinition("null");
			return graph;
		}	
	}
	#endregion

	#region	PrimaryFollowExpression
	public class				PrimaryFollowExpression : Expression
	{		 			
		public static Dom.Expression ParseFollow(Expression tok, Dom.Expression target)
		{	
			Dom.Expression ret = null;
			if(tok is MemberAccessExpr)
				ret = ((MemberAccessExpr)tok).ParseMemberAccess(target);
			else if(tok is IndexerExpr)
				ret = ((IndexerExpr)tok).ParseIndexerExpr(target);
			else if(tok is InvokeExpr)
				ret = ((InvokeExpr)tok).ParseInvoke(target);
			else if(tok is PostfixExpr)
				ret = ((PostfixExpr)tok).ParsePostfixExpr(target);			

			AST ntok = tok.getNextSibling();
			if(ntok != null && ntok is PrimaryFollowExpression) 
				return PrimaryFollowExpression.ParseFollow(
							(PrimaryFollowExpression)ntok, ret);
			else
				return ret;
		}
	}
				 
	#endregion	
		#region	PostfixExpr 
	public class				PostfixExpr : PrimaryFollowExpression
	{
		public Dom.PostfixExpr ParsePostfixExpr(Dom.Expression targ)
		{
			Dom.PostfixExpr pfx = new Dom.PostfixExpr();
			pfx.Left = targ;
			string ps = this.getText();
			if(ps == "++")
				pfx.Operator = Dom.PostfixOperator.Increment;
			else if(ps == "--")
				pfx.Operator = Dom.PostfixOperator.Decrement;
			return pfx;
		}
	}
	#endregion 
		#region	IndexerExpr 
	public class				IndexerExpr : PrimaryFollowExpression
	{
		public Dom.IndexerRef ParseIndexerExpr(Dom.Expression targ)
		{
			Dom.IndexerRef ix = new Dom.IndexerRef();
			ix.Target = targ;
			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is Expression)
				{
					ix.Indices.Add( Expression.Parse((Expression)tok) );
				}
				tok = tok.getNextSibling();
			}
			return ix;
		}
		public void ParseIndexerSizes(Dom.ExpressionCollection col)
		{
			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is Expression)
				{
					col.Add( Expression.Parse((Expression)tok) );
				}
				tok = tok.getNextSibling();
			}
		}
	}
	#endregion 
		#region	InvokeExpr *
	public class				InvokeExpr : PrimaryFollowExpression
	{	
		public Dom.MethodInvokeExpr ParseInvoke(Dom.Expression targ)
		{
			// may be delegate invoke in the end...
			Dom.MethodInvokeExpr ex = new Dom.MethodInvokeExpr();
			// todo: sort out delegate vs method invoke
			Dom.MethodRef mr = new Dom.MethodRef();
			mr.Target = targ; // name unknown at this point
			ex.Target = mr;

			AST argsTok = this.getFirstChild();
			if(argsTok is Args)
				((Args)argsTok).GetGraph(ex.Parameters);
			return ex;
		}
	}
	#endregion 
		#region	MemberAccessExpr *
	public class				MemberAccessExpr : PrimaryFollowExpression
	{
		public Dom.MemberAccess ParseMemberAccess(Dom.Expression targ)
		{
			Dom.MemberAccess ex = new Dom.MemberAccess();
			ex.Target = targ;
			ex.MemberName = this.getText();
			return ex;
		}
	}
	#endregion 


	#region	ArrayRankExpr 
	public class				ArrayRankExpr : Expression
	{
	}
	#endregion 
	#region	ArrayInitExpr 
	public class				ArrayInitExpr : PrimaryExpression
	{
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.ArrayInitializer graph = 
				new Dom.ArrayInitializer();
			return this.GetGraph(graph);
		}	
		public Dom.ArrayInitializer	
			GetGraph(Dom.ArrayInitializer graph)
		{		
			// Expression list
			//graph.Clear();
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is ArrayCreateExpr)
				{
					graph.InitialValues.Add(Expression.Parse((ArrayCreateExpr)tok));
				}
				else if(tok is ArrayInitExpr)
				{
					graph.InitialValues.Add( 
						(Dom.ArrayInitializer)((ArrayInitExpr)tok).GetGraph() );
				}
				else if(tok is Expression)
				{
					//AST ctok = tok.getFirstChild();
				//	Dom.ArrayInitializer ai = new Dom.ArrayInitializer();
//					while(ctok != null)
//					{
//						if(ctok is Expression)
//						{
							graph.InitialValues.Add(Expression.Parse((Expression)tok));
//						}
//						ctok = ctok.getNextSibling();
//					}
				//	graph.InitialValues.Add(ai);
				}
				tok = tok.getNextSibling();
			}
			return graph;
		}

	}
	#endregion 

	#region CustomAttributes 
	public class CustomAttributes : CSharpAST
	{
		public override Dom.IGraph	GetGraph()
			{
				throw new MethodAccessException("Code should never call GetGraph() on CustomAttributes");
			}

		public Dom.CustomAttributeCollection	
			GetGraph(Dom.CustomAttributeCollection graph)
		{		
			base.GetGraph(graph);
			Dom.AttributeTarget at = Dom.AttributeTarget.Empty;

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is ModifierAttributes)
				{
					AST ntok = tok.getFirstChild();
					if(ntok != null) 
						at = CustomAttribute.ParseTarget(ntok.getText());
				}
				else if(tok is CustomAttribute)
				{
					Dom.CustomAttribute da = 
						(Dom.CustomAttribute)((CustomAttribute)tok).GetGraph();
					da.AttributeTarget = at;
					graph.Add(da);
				}

				tok = tok.getNextSibling();
			}
			return graph;
		}
	}
	#endregion 
	#region CustomAttribute 
	public class CustomAttribute : CSharpAST
	{	
		public override Dom.IGraph			GetGraph()				
		{	
			Dom.CustomAttribute graph = 
				new Dom.CustomAttribute();
			return this.GetGraph(graph);
		}	
		public Dom.CustomAttribute	
			GetGraph(Dom.CustomAttribute graph)
		{	
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is TypeRef)
				{
					// can't be array
					AST trName = tok.getFirstChild();
					if (trName == null)
						graph.Name = "";
					else
					{
						string name = trName.getText();
						while ((trName = trName.getNextSibling()) != null)
							name+= "." + trName.getText();
						graph.Name = name;
					}
				}
				else if(tok is Args)
				{
					//((Args)tok).GetGraph(graph.Parameters);
					AST ctok = tok.getFirstChild();
					while(ctok != null)
					{
						if(ctok is Expression)
						{
							Dom.Expression ex = Expression.Parse(((Expression)ctok));
							graph.Parameters.Add(ex);
							// TODO:! Attributes calls must be resolved differently 
							// AddRootExpression(ex, dl.GetType(), index...);
						}
						ctok = ctok.getNextSibling();
					}
				}
				tok = tok.getNextSibling();
			}
			return graph;

		}
		public static Dom.AttributeTarget ParseTarget(string name)	
		{
			Dom.AttributeTarget targ = Dom.AttributeTarget.Empty;
			switch (name.ToLower())
			{							
				case "assembly":							
					targ = Dom.AttributeTarget.Assembly;	
					break;								
				case "module":						
					targ = Dom.AttributeTarget.Module;	
					break;								
				case "class":						
					targ = Dom.AttributeTarget.Class;	
					break;								
				case "struct":							
					targ = Dom.AttributeTarget.Struct;	
					break;									
				case "interface":								
					targ = Dom.AttributeTarget.Interface;	
					break;									
				case "enum":								
					targ = Dom.AttributeTarget.Enum;		
					break;									
				case "delegate":								
					targ = Dom.AttributeTarget.Delegate;	
					break;
				case "method":
					targ = Dom.AttributeTarget.Method;
					break;
				case "parameter":
				case "param":
					targ = Dom.AttributeTarget.Parameter;
					break;
				case "field":
					targ = Dom.AttributeTarget.Field;
					break;
				case "propertyIndexer":
					targ = Dom.AttributeTarget.PropertyIndexer;
					break;
				case "propertyGetAccessor":
					targ = Dom.AttributeTarget.PropertyGetAccessor;
					break;
				case "propertySetAccessor":
					targ = Dom.AttributeTarget.PropertySetAccessor;
					break;
				case "eventField":
					targ = Dom.AttributeTarget.EventField;	
					break;														
				case "eventProperty":											
					targ = Dom.AttributeTarget.EventProperty;				
					break;														
				case "eventAdd":													
					targ = Dom.AttributeTarget.EventAdd;				
					break;															
				case "eventRemove":											
					targ = Dom.AttributeTarget.EventRemove;
					break;
				case "event":
					targ = Dom.AttributeTarget.Event;
					break;
				case "property":
					targ = Dom.AttributeTarget.Property;
					break;
				case "return":
					targ = Dom.AttributeTarget.Return;
					break;
				case "type":
					targ = Dom.AttributeTarget.Type;
					break;
			}
			return targ;
		}
	}
	#endregion 
	#region ModifierAttributes 
	public class ModifierAttributes : CSharpAST
	{
			public override Dom.IGraph	GetGraph()
				{
					throw new MethodAccessException("Code should never call GetGraph() on ModifierAttributes");
				}

	}
	#endregion 

	#region DeclArgs 
	public class						DeclArgs : CSharpAST
	{
		public override Dom.IGraph	GetGraph()
		{
			throw new MethodAccessException("Code should never call GetGraph() on DeclArgs");
		}

		public Dom.ParamDeclCollection	
			GetGraph(Dom.ParamDeclCollection graph)
		{	
			base.GetGraph(graph);

			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is DeclArg)
				{
					graph.Add( (Dom.ParamDecl)((DeclArg)tok).GetGraph() );
				}
				tok = tok.getNextSibling();
			}
			return graph;
		}
	}
	#endregion 
	#region Args 
	public class						Args : CSharpAST
	{
		public override Dom.IGraph	GetGraph()
		{
			throw new MethodAccessException("Code should never call GetGraph() on Args");
		}

		public Dom.ParamCollection	
			GetGraph(Dom.ParamCollection graph)
		{	
			base.GetGraph(graph);
			AST tok = this.getFirstChild();
			while(tok != null)
			{
				if(tok is Arg)
				{
					graph.Add( (Dom.Param)((Arg)tok).GetGraph() );
				}
				tok = tok.getNextSibling();
			}
			return graph;
		}
	}
	#endregion 
	#region ArgDirection 
	public class						ArgDirection : CSharpAST
	{
		public override Dom.IGraph	GetGraph()
		{
			throw new MethodAccessException("Code should never call GetGraph() on ArgDirection");
		}
	}
	#endregion 
	#region Op 
	public class						Op : CSharpAST
	{	
		public override Dom.IGraph	GetGraph()
		{
			throw new MethodAccessException("Code should never call GetGraph() on Op");
		}
	}
	#endregion 
	#region Declarator 
	public class						Declarator : CSharpAST
	{
		public override Dom.IGraph	GetGraph()
		{
			throw new MethodAccessException("Code should never call GetGraph() on Declarator");
		}
		public Dom.DeclaratorCollection	
			GetGraph(Dom.DeclaratorCollection graph)
		{		
			base.GetGraph(graph);
			Dom.Declarator dl = new Dom.Declarator();
			AST tok = this.getFirstChild();
			while(tok != null)
			{
				
				if(tok is Ident) 
					dl.Name = tok.getText();
				else if(tok is Expression) 
				{
					AST ctok = tok.getFirstChild();
					if(ctok != null)
						dl.InitExpression = Expression.Parse(((Expression)ctok));
					AddRootExpression(dl.InitExpression, dl.GetType(), "InitExpression");
				}				
				tok = tok.getNextSibling();
			}
			// the types for each declarator is set afterwards, by the member
			AddDefinition(dl, dl.Name, null);
			graph.Add(dl);
			return graph;
		}
	}
	#endregion 
	#region AssignOp 
	public class						AssignOp : CSharpAST
	{	
		public override Dom.IGraph	GetGraph()
		{
			throw new MethodAccessException("Code should never call GetGraph() on AssignOp");
		}
	}
	#endregion 
	#region UnaryOp 
	public class						UnaryOp : CSharpAST
	{	
		public override Dom.IGraph	GetGraph()
		{
			throw new MethodAccessException("Code should never call GetGraph() on UnaryOp");
		}
	}
	#endregion 
	#region BinaryOp 
	public class						BinaryOp : CSharpAST
	{	
		public override Dom.IGraph	GetGraph()
		{
			throw new MethodAccessException("Code should never call GetGraph() on BinaryOp");
		}
	}
	#endregion 
	#region TernaryOp 
	public class						TernaryOp : CSharpAST
	{	
		public override Dom.IGraph	GetGraph()
		{
			throw new MethodAccessException("Code should never call GetGraph() on TernaryOp");
		}
	}
	#endregion 


}	


