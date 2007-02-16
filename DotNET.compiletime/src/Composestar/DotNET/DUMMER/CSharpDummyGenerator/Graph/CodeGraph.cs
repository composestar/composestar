using System;
using System.Text;
using System.Collections;
using System.Collections.Specialized;

using DDW.CSharp.SymbolTable;

namespace DDW.CSharp.Dom
{
	#region  IGraph 
	/// <summary>
	/// An interface that all CSharpDom elements must implement.
	/// </summary>
	public interface IGraph 
	{
		/// <exclude/>
		string Text {get;}
		GraphTypes GraphType{get;}
		LinePragma LinePragma{get;}
	}
	#endregion

	#region  CSharpGraph
	/// <summary>
	/// An abstract base class for CSharpDom elements.
	/// </summary>
	public abstract class CSharpGraph : IGraph
	{
		public virtual GraphTypes GraphType{get{return GraphTypes.CSharpGraph;}}
		#region  UserData  
		private IDictionary p_userData = null;
		/// <summary>
		/// Gets or sets the user-definable data for the current object.
		/// </summary>
		public IDictionary				UserData					
		{
			get
			{
				return p_userData;
			}
			set
			{
				p_userData = value;
			}
		}	
		#endregion
		#region  LinePragma
		private LinePragma p_linePragma = new LinePragma();
		public LinePragma				LinePragma		
		{
			get
			{	
				return p_linePragma;
			}
		}
		#endregion

		#region Text
		/// <exclude/>
		public abstract string Text{get;}
		#endregion
		#region GetTypeFromString
		// different assemblies can use this to get dom types.
		public static Type GetTypeFromString(string s)
		{
			Type t = Type.GetType(s);
			if(t == null)
				throw(new Exception("Type not found: " + s) );
			return t;
		}
		#endregion
	}
	#endregion
	#region  CompileUnit  -s
	/// <summary>
	/// A C# file level unit of code. This must be a complete C# file, snippets are not valid.
	/// </summary>
	public class CompileUnit : CSharpGraph, IScope  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.CompileUnit;}}
		#region  Imports  
		private ImportCollection p_imports = new ImportCollection();
		/// <summary>
		/// Gets the collection of imports ('using' declarations) for this Compile unit.
		/// </summary>
		[CodeCollection]
		public ImportCollection				Imports					
		{
			get
			{
				return p_imports;
			}
		}	
	#endregion	
		#region  Namespaces  
		private NamespaceDeclCollection p_namespaces = new NamespaceDeclCollection();
		/// <summary>
		/// Gets the collection of Namespace Declarations for this Compile unit.
		/// </summary>
		[CodeCollection]
		public NamespaceDeclCollection				Namespaces					
		{
			get
			{
				return p_namespaces;
			}
		}	
	#endregion	
		#region  AssemblyCustomAttributes  
		private CustomAttributeCollection p_assemblyCustomAttributes = new CustomAttributeCollection();
		/// <summary>
		/// Gets the collection of assembly level custom attributes - eg. [assembly:CLSCompliant(true)] for this Compile unit.
		/// </summary>
		[CodeCollection]
		public CustomAttributeCollection				AssemblyCustomAttributes					
		{
			get
			{
				return p_assemblyCustomAttributes;
			}
		}	
	#endregion	
		#region  ReferencedAssemblies  
		private AssemblyReferenceCollection p_referencedAssemblies = 
			new AssemblyReferenceCollection();
		/// <summary>
		/// Gets the collection of referenced assemblies - this does not come from the source code, but rather would be set when invoking the parser.
		/// </summary>
		[CodeCollection]
		public AssemblyReferenceCollection		ReferencedAssemblies	
		{
			get
			{
				return p_referencedAssemblies;
			}
		}	
	#endregion	


		#region ScopeTable
		private ScopeCollection  p_scopeTable = new ScopeCollection();
		/// </exclude>
		public ScopeCollection ScopeTable
		{
			get
			{
				return  p_scopeTable;
			}
		}
		#endregion
		#region  Scope 
		private Scope p_scope; 
		public Scope			Scope					
		{
			get
			{
				return p_scope;
			}
			set
			{
				p_scope = value;
			}
		}	
	#endregion		
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				return FileName + " - Compile Unit";
			}
		}
		#endregion
		#region FileName
		private string p_fileName = "";
		/// <summary>
		/// Gets or sets the name of the file being parsed. This is not a Code Element.
		/// </summary>
		public string FileName
		{
			get
			{
				return p_fileName;
			}
			set
			{
				p_fileName = value;
			}
		}
		#endregion

	}
	#endregion
	#region  Import 
	/// <summary>
	/// An import declaration. This maps to the 'using' statement, so it can also be used to set aliases.
	/// </summary>
	public class Import : CSharpGraph  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.Import;}}
		#region  Namespace 
		private string p_namespace;
		/// <summary>
		/// Gets or sets the namespace to be imported or aliased.
		/// </summary>
		[CodeElement]
		public string				Namespace					
		{
			get
			{
				return p_namespace;
			}
			set
			{
				p_namespace = value;
			}
		}	
	#endregion	
		#region  Alias 
		private string p_alias;
		/// <summary>
		/// Gets or sets the alias of the imported namespace, if present.
		/// </summary>
		[CodeElement]
		public string				Alias					
		{
			get
			{
				return p_alias;
			}
			set
			{
				p_alias = value;
			}
		}	
	#endregion	

		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				return "Import";
			}
		}
		#endregion
	}
	#endregion
	#region  NamespaceDecl  -ds
	/// <summary>
	/// A namespace declaration.
	/// </summary>
	public class NamespaceDecl : CSharpGraph, IScope, IDeclaration  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.NamespaceDecl;}}
		#region  Imports  
		private ImportCollection p_imports = new ImportCollection();
		/// <summary>
		/// Gets the collection of imports ('using' declarations) for this namespace.
		/// </summary>
		[CodeCollection]
		public ImportCollection				Imports					
		{
			get
			{
				return p_imports;
			}
		}	
	#endregion	
		#region  Name  
		/// <summary>
		/// Gets or sets the name of the namespace. This may use dot syntax.
		/// </summary>
		[CodeElement]
		public string				Name					
		{
			get
			{
				return p_definition.Name;
			}
			set
			{
				p_definition.Name = value;
			}
		}	
	#endregion	
		#region  Types  
		private TypeDeclCollection p_types = new TypeDeclCollection();
		/// <summary>
		/// A collection of types declared in this namespace (classes, interfaces, structs, etc).
		/// </summary>
		[CodeCollection]
		public TypeDeclCollection				Types					
		{
			get
			{
				return p_types;
			}
			set
			{
				p_types = value;
			}
		}	
		#endregion	

		#region Definition
		private IDefinition p_definition = new Definition();
		/// <summary>
		/// Gets the (scoped) definition.
		/// </summary>
		public IDefinition Definition
		{
			get
			{
				return p_definition;
			}
		}
		#endregion
		#region  Scope 
		private Scope p_scope; 
		public Scope			Scope					
		{
			get
			{
				return p_scope;
			}
			set
			{
				p_scope = value;
			}
		}	
		#endregion	
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				return p_definition.ToString();
			}
		}
		#endregion
	}
	#endregion

	#region  MemberDecl  
	/// <summary>
	/// An abstract class for all types (class, struct...) and type members (method, field...).
	/// </summary>
	public abstract class MemberDecl : CSharpGraph 
	{
		public abstract MemberKind MemberKind{get;}
		#region  Attributes  
		private Modifiers p_attributes = new Modifiers(); 
		/// <summary>
		/// Gets or sets the modifier flags for this member (public, private etc).
		/// </summary>
		[CodeElement]
		public Modifiers				Attributes					
		{
			get
			{
				return p_attributes;
			}
			set
			{
				p_attributes = value;
			}
		}	
	#endregion	
		#region  CustomAttributes  
		private CustomAttributeCollection p_customAttributes = new CustomAttributeCollection();
		/// <summary>
		/// Gets the collection of Custom Attributes used for this member (custom attributes are the ones declared between brackets, like [Flags]).
		/// </summary>
		[CodeCollection]
		public CustomAttributeCollection				CustomAttributes					
		{
			get
			{
				return p_customAttributes;
			}
		}	
	#endregion	
		#region  Comments  
		private CommentStmtCollection p_comments = new CommentStmtCollection();
		/// <summary>
		/// Gets the collection of comments for this member.
		/// </summary>
		[CodeCollection]
		public CommentStmtCollection				Comments					
		{
			get
			{
				return p_comments;
			}
		}	
	#endregion
		protected virtual bool				VerifyModifiers()		
		{
			return true;
		}
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				string s = this.GetType().Name;
				s = s.Substring(0, s.LastIndexOf("Decl"));
				return s; //+ ": " + Name;
			}
		}
		#endregion
	}
	#endregion
		#region  MethodDecl  -ds
	/// <summary>
	/// A method declaration.
	/// </summary>
	public class MethodDecl : MemberDecl, IDeclaration, IScope, IOverloadable   
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.MethodDecl;}}
		public override MemberKind MemberKind{get{return MemberKind.Method;}}
			#region  Name 
		/// <summary>
		/// Gets or sets the name of the method. This may use dot syntax.
		/// </summary>
		[CodeElement]
		public string				Name					
		{
			get
			{
				return p_definition.Name;
			}
			set
			{
				p_definition.Name = value;
			}
		}	
	#endregion	
			#region  Parameters  
		private ParamDeclCollection p_parameters = new ParamDeclCollection();
		/// <summary>
		/// Gets the collection of parameters for the method. Each of these hold information about the type, direction etc.
		/// </summary>
		[CodeCollection]
		public ParamDeclCollection			Parameters					
		{
			get
			{
				return p_parameters;
			}
		}	
	#endregion	
			#region  ReturnType  
		private TypeRef p_returnType;
		/// <summary>
		/// Gets or sets the return type of the method.
		/// </summary>
		[CodeElement]
		public TypeRef				ReturnType					
		{
			get
			{
				return p_returnType;
			}
			set
			{
				p_returnType = value;
			}
		}	
			#endregion			
			#region  ReturnTypeCustomAttributes  
		private CustomAttributeCollection p_returnTypeCustomAttributes = 
			new CustomAttributeCollection();
		/// <summary>
		/// Gets the collection of custom attributes that apply to the return type of the method.
		/// </summary>
		[CodeCollection]
		public CustomAttributeCollection	ReturnTypeCustomAttributes		
		{
			get
			{
				return p_returnTypeCustomAttributes;
			}
		}	
			#endregion			
			#region  Statements  
		private StatementCollection p_statements = new StatementCollection();
		/// <summary>
		/// Gets the collection of statements in the method.
		/// </summary>
		[CodeCollection]
		public StatementCollection				Statements					
		{
			get
			{
				return p_statements;
			}
		}	
		#endregion		
		#region Definition
		private IDefinition p_definition = new OverloadableDefinition();
		/// <summary>
		/// Gets the (scoped) definition.
		/// </summary>
		public IDefinition Definition
		{
			get
			{
				return p_definition;
			}
		}
		#endregion
		#region  Scope 
		private Scope p_scope; 
		public Scope			Scope					
		{
			get
			{
				return p_scope;
			}
			set
			{
				p_scope = value;
			}
		}	
		#endregion	

		#region HashText
		/// <exclude/>
		/// <summary>
		/// Gets the hash string of this defintion. This is of the form Name@Out:param1@Ref:param2...
		/// </summary>
		public string HashText
		{
			get
			{
				string s = this.Name;
				foreach(ParamDecl pd in Parameters)
				{
					s += "@" + pd.Direction.ToString();
					s += ":" + pd.Type.TypeName;
				}
				return s;
			}
		}
		#endregion
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				string s = this.GetType().Name;
				s = s.Substring(0, s.LastIndexOf("Decl"));
				s += " - ";
				if(Definition == null) s += this.Name;
				else s += Definition.ToString();
				return s; //+ ": " + Name;
			}
		}
		#endregion

	}
		#endregion
		#region  FieldDecl 
	/// <summary>
	/// A field declaration.
	/// </summary>
	public class FieldDecl : MemberDecl // isn't IDeclaration - declarators are
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.FieldDecl;}}
		public override MemberKind MemberKind{get{return MemberKind.Field;}}
			#region  Delcarators 
		private DeclaratorCollection p_declarators = new DeclaratorCollection(); 
		/// <summary>
		/// Gets the collection of declarators created in the field. Fields can declare multiple variables at once, so this must be a collection.
		/// </summary>
		[CodeCollection]
		public DeclaratorCollection			Delcarators					
		{
			get
			{
				return p_declarators;
			}
		}	
	#endregion	
			#region  Type  
		private TypeRef p_type;
		/// <summary>
		/// Gets or sets the type of the field declaration. While a field can declare multiple variables, they must be of the same type, so this is not a collection.
		/// </summary>
		[CodeElement]
		public TypeRef			Type					
		{
			get
			{
				return p_type;
			}
			set
			{
				p_type = value;
			}
		}	
	#endregion	
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				string s = this.GetType().Name;
				s = s.Substring(0, s.LastIndexOf("Decl"));
				return s;;
			}
		}
		#endregion
	}
		#endregion
		#region  PropertyDecl -d
	/// <summary>
	/// A property declaration.
	/// </summary>
	public class PropertyDecl : MemberDecl, IDeclaration  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.PropertyDecl;}}
		public override MemberKind MemberKind{get{return MemberKind.Property;}}
			#region  Name 
		/// <summary>
		/// Gets or sets the name of the property. This may use dot syntax.
		/// </summary>
		[CodeElement]
		public string				Name					
		{
			get
			{
				return p_definition.Name;
			}
			set
			{
				p_definition.Name = value;
			}
		}	
	#endregion	
			#region  GetAccessor  
		private AccessorDecl p_getAccessor = new AccessorDecl();
		/// <summary>
		/// Gets or sets the 'get' accessor for this property. This will not have an associated block if the property is declared as abstract or in an interface.
		/// </summary>
		[CodeElement]
		public AccessorDecl				GetAccessor					
		{
			get
			{
				return p_getAccessor;
			}
			set
			{
				p_getAccessor = value;
			}
		}	
	#endregion
			#region  SetAccessor  
		private AccessorDecl p_setAccessor = new AccessorDecl();
		/// <summary>
		/// Gets or sets the 'set' accessor for this property, if present. This will not have an associated block if the property is declared as abstract or in an interface.
		/// </summary>
		[CodeElement]
		public AccessorDecl				SetAccessor					
		{
			get
			{
				return p_setAccessor;
			}
			set
			{
				p_setAccessor = value;
			}
		}	
	#endregion
			#region  Type  
		private TypeRef p_type;
		/// <summary>
		/// Gets or sets the type of the property.
		/// </summary>
		[CodeElement]
		public TypeRef				Type					
		{
			get
			{
				return p_type;
			}
			set
			{
				p_type = value;
			}
		}	
			#endregion
			#region  HasGet  
		private bool p_hasGet = false;
		/// <summary>
		/// Gets or sets the boolean value that indicates whether a get accessor is  to be used.
		/// </summary>
		[CodeElement]
		public bool				HasGet					
		{
			get
			{
				return p_hasGet;
			}
			set
			{
				p_hasGet = value;
			}
		}	
	#endregion
			#region  HasSet  
		private bool p_hasSet = false;
		/// <summary>
		/// Gets or sets the boolean value that indicates whether a set accessor is to be used. Setting this to false will not destroy the SetAccessor value.
		/// </summary>
		[CodeElement]
		public bool				HasSet					
		{
			get
			{
				return p_hasSet;
			}
			set
			{
				p_hasSet = value;
			}
		}	
		#endregion
		#region Definition
		private IDefinition p_definition = new Definition();
		/// <summary>
		/// Gets the (scoped) definition.
		/// </summary>
		public IDefinition Definition
		{
			get
			{
				return p_definition;
			}
		}
		#endregion
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				string s = this.GetType().Name;
				s = s.Substring(0, s.LastIndexOf("Decl"));
				s += " - ";
				if(Definition == null) s += this.Name;
				else s += Definition.ToString();
				return s; //+ ": " + Name;
			}
		}
		#endregion
	}
		#endregion
		#region  EventDecl 
	/// <summary>
	/// An event declaration.
	/// </summary>
	public class EventDecl : MemberDecl  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.EventDecl;}}
		public override MemberKind MemberKind{get{return MemberKind.Event;}}
			#region  Type  
		private TypeRef p_type;
		/// <summary>
		/// Gets or sets the type of the event. This must be a delegate type.
		/// </summary>
		[CodeElement]
		public TypeRef				Type					
		{
			get
			{
				return p_type;
			}
			set
			{
				p_type = value;
			}
		}	
			#endregion
			#region  Delcarators 
		private DeclaratorCollection p_declarators = new DeclaratorCollection(); 
		/// <summary>
		/// Gets a declarator collection. Events can declare multiple variables (like fields) if no accessors are present, so this must be a collection. If accessors are present, the name will be stored in the first declarator, however in this case it may have an interface name prepended - thus it may be in dot syntax. The declarator expression is always ignored.
		/// </summary>
		[CodeCollection]
		public DeclaratorCollection			Delcarators					
		{
			get
			{
				return p_declarators;
			}
		}	
	#endregion	
			#region  AddAccessor  
		private AccessorDecl p_addAccessor = new AccessorDecl();
		/// <summary>
		/// Gets or sets the 'add' accessor for this property, if present. It is an error to have an add accessor without a remove accessor. This will not have an associated block if the property is declared as abstract or in an interface.
		/// </summary>
		[CodeElement]
		public AccessorDecl				AddAccessor					
		{
			get
			{
				return p_addAccessor;
			}
			set
			{
				p_addAccessor = value;
			}
		}	
	#endregion
			#region  RemoveAccessor  
		private AccessorDecl p_removeAccessor = new AccessorDecl();
		/// <summary>
		/// Gets or sets the 'remove' accessor for this property, if present. It is an error to have an add accessor without a remove accessor. This will not have an associated block if the property is declared as abstract or in an interface.
		/// </summary>
		[CodeElement]
		public AccessorDecl				RemoveAccessor					
		{
			get
			{
				return p_removeAccessor;
			}
			set
			{
				p_removeAccessor = value;
			}
		}	
	#endregion
			#region  UsesAccessors  
		private bool p_usesAccessors = false;
		/// <summary>
		/// Gets or sets the boolean value that indicates whether accessors are to be used. Setting this to false will not destroy the accessor values.
		/// </summary>
		[CodeElement]
		public bool				UsesAccessors					
		{
			get
			{
				return p_usesAccessors;
			}
			set
			{
				p_usesAccessors = value;
			}
		}	
	#endregion
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				string s = this.GetType().Name;
				s = s.Substring(0, s.LastIndexOf("Decl"));

				if(Delcarators.Count > 0)
					foreach(Declarator d in Delcarators)
						s += " - " + d.Name;
				return s; //+ ": " + Name;
			}
		}
		#endregion
	}
		#endregion
		#region  ConstantDecl 
	/// <summary>
	/// A constant declartation.
	/// </summary>
	public class ConstantDecl : MemberDecl  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.ConstantDecl;}}

		public override MemberKind MemberKind{get{return MemberKind.Constant;}}
			#region  Delcarators 
		private DeclaratorCollection p_declarators = new DeclaratorCollection(); 
		/// <summary>
		/// Gets the collection of declarators created in the field. Constants declarations can declare multiple variables at once, so this must be a collection.
		/// </summary>
		[CodeCollection]
		public DeclaratorCollection			Delcarators					
		{
			get
			{
				return p_declarators;
			}
		}	
	#endregion	
			#region  Type  
		private TypeRef p_type;
		/// <summary>
		/// Gets or sets the type of the constant. There can only be one type per declaration.
		/// </summary>
		[CodeElement]
		public TypeRef			Type					
		{
			get
			{
				return p_type;
			}
			set
			{
				p_type = value;
			}
		}	
	#endregion	
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				string s = this.GetType().Name;
				s = s.Substring(0, s.LastIndexOf("Decl"));
				return s; //+ ": " + Name;
			}
		}
		#endregion
	}
			#endregion
		#region  IndexerDecl -ds
		/// <summary>
	/// An indexer declartation.
	/// </summary> 
		public class IndexerDecl : MemberDecl, IDeclaration, IScope, IOverloadable   
		{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.IndexerDecl;}}

		public override MemberKind MemberKind{get{return MemberKind.Indexer;}}
			#region  Parameters  
		private ParamDeclCollection p_parameters = new ParamDeclCollection();
		/// <summary>
		/// Gets the collection of parameters for the indexer. Each of these hold information about the type, direction etc.
		/// </summary>
		[CodeCollection]
		public ParamDeclCollection				Parameters					
		{
			get
			{
				return p_parameters;
			}
		}	
	#endregion	
			#region  GetAccessor  
		private AccessorDecl p_getAccessor = new AccessorDecl();
		/// <summary>
		/// Gets or sets the 'get' accessor for the indexer. This will not have an associated block if the indexer is declared as abstract or in an interface.
		/// </summary>
		[CodeElement]
		public AccessorDecl				GetAccessor					
		{
			get
			{
				return p_getAccessor;
			}
			set
			{
				p_getAccessor = value;
			}
		}	
	#endregion
			#region  SetAccessor  
		private AccessorDecl p_setAccessor = new AccessorDecl();
		/// <summary>
		/// Gets or sets the 'set' accessor for the indexer. This will not have an associated block if the indexer is declared as abstract or in an interface.
		/// </summary>
		[CodeElement]
		public AccessorDecl				SetAccessor					
		{
			get
			{
				return p_setAccessor;
			}
			set
			{
				p_setAccessor = value;
			}
		}	
	#endregion
			#region  Type  
		private TypeRef p_type;
		/// <summary>
		/// Gets or sets the type of the indexer.
		/// </summary>
		[CodeElement]
		public TypeRef				Type					
		{
			get
			{
				return p_type;
			}
			set
			{
				p_type = value;
			}
		}	
			#endregion
			#region  InterfaceType  
		private TypeRef p_itype;
		/// <summary>
		/// Gets or sets the interface type, if present. In the case of a class implementing multiple interfaces that require indexers, the interface type is prepended to 'this' (with the syntax IList.this).
		/// </summary>
		[CodeElement]
		public TypeRef				InterfaceType					
		{
			get
			{
				return p_itype;
			}
			set
			{
				p_itype = value;
			}
		}	
			#endregion
			#region  HasGet  
		private bool p_hasGet = false;
		/// <summary>
		/// Gets or sets the boolean value that indicates whether a get accessor is to be used. Setting this to false will not destroy the GetAccessor value.
		/// </summary>
		[CodeElement]
		public bool				HasGet					
		{
			get
			{
				return p_hasGet;
			}
			set
			{
				p_hasGet = value;
			}
		}	
	#endregion
			#region  HasSet  
		private bool p_hasSet = false;
		/// <summary>
		/// Gets or sets the boolean value that indicates whether a set accessor is to be used. Setting this to false will not destroy the SetAccessor value.
		/// </summary>
		[CodeElement]
		public bool				HasSet					
		{
			get
			{
				return p_hasSet;
			}
			set
			{
				p_hasSet = value;
			}
		}	
		#endregion
		#region Definition
		private IDefinition p_definition = new OverloadableDefinition();
		/// <summary>
		/// Gets the (scoped) definition.
		/// </summary>
		public IDefinition Definition
		{
			get
			{
				return p_definition;
			}
		}
		#endregion
		#region  Scope 
		private Scope p_scope; 
		public Scope			Scope					
		{
			get
			{
				return p_scope;
			}
			set
			{
				p_scope = value;
			}
		}	
		#endregion	
		#region  Name 
		/// <summary>
		/// Gets the name of the indexer (which is always 'Item').
		/// </summary>
		public string				Name					
		{
			get
			{
				return "Item";
			}
			set
			{
			}
		}	
			#endregion	
		#region HashText
		/// <exclude/>
		/// <summary>
		/// Gets the hash string of this defintion. This is of the form Name@Out:param1@Ref:param2...
		/// </summary>
		public string HashText
		{
			get
			{
				string s = this.Name;
				foreach(ParamDecl pd in Parameters)
				{
					s += "@" + pd.Direction.ToString();
					s += ":" + pd.Type.TypeName;
				}
				return s;
			}
		}
		#endregion
	}
			#endregion
		#region  OperatorDecl  -s
	/// <summary>
	/// An operator declartation.
	/// </summary> 
	public class OperatorDecl : MemberDecl, IDeclaration, IScope, IOverloadable  
	{
		// TODO: Operator must be overloadable. Set some custom methods that can get resolved.
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.OperatorDecl;}}

		public override MemberKind MemberKind{get{return MemberKind.Operator;}}
			#region  Type  
		private TypeRef p_type;
		/// <summary>
		/// Gets or sets the type that the operator will affect.
		/// </summary>
		[CodeElement]
		public TypeRef			Type					
		{
			get
			{
				return p_type;
			}
			set
			{
				p_type = value;
			}
		}	
	#endregion	
			#region  FirstParameter  
		/// <summary>
		/// Gets or sets the first object in a unary, binary or conversion overload.
		/// </summary>
		[CodeElement]
		public ParamDecl				FirstParameter					
		{
			get
			{
				return Parameters[0];
			}
			set
			{
				Parameters[0] = value;
			}
		}	
	#endregion
			#region  SecondParameter  
		/// <summary>
		/// Gets or sets the second object in a binary overload (not set for unary and conversion overloads).
		/// </summary>
		[CodeElement]
		public ParamDecl				SecondParameter					
		{
			get
			{
				if(Parameters.Count < 2) return null;
				return Parameters[1];
			}
			set
			{
				if(Parameters.Count == 0) Parameters.Add(null);
				Parameters[1] = value;
			}
		}	
	#endregion
			#region  Operator  
		private OverloadableOperator p_op = OverloadableOperator.Empty;
		/// <summary>
		/// Gets or sets the operator to overload. This can be 'explicit' or 'implicit' in the case of conversion. 
		/// </summary>
		[CodeElement]
		public OverloadableOperator				Operator					
		{
			get
			{
				return p_op;
			}
			set
			{
				p_op = value;
			}
		}	
	#endregion
			#region  Statements  
		private StatementCollection p_statements = new StatementCollection();
		/// <summary>
		/// Gets the collection of statements for the operator declaration.
		/// </summary>
		[CodeCollection]
		public StatementCollection				Statements					
		{
			get
			{
				return p_statements;
			}
		}	
		#endregion
		#region  Parameters  
		private ParamDeclCollection p_parameters = new ParamDeclCollection();
		/// <summary>
		/// Gets the collection of parameters - this is actually just putting the first and second (if present) parameter into a collection.
		/// </summary>
		public ParamDeclCollection				Parameters					
		{
			get
			{
				return p_parameters;
			}
		}	
	#endregion	
		#region Definition
		private IDefinition p_definition = new OverloadableDefinition();
		/// <summary>
		/// Gets the (scoped) definition.
		/// </summary>
		public IDefinition Definition
		{
			get
			{
				return p_definition;
			}
		}
		#endregion
		#region  Scope 
		private Scope p_scope; 
		public Scope			Scope					
		{
			get
			{
				return p_scope;
			}
			set
			{
				p_scope = value;
			}
		}	
		#endregion	
		#region HashText
		/// <exclude/>
		/// <summary>
		/// Gets the hash string of this defintion. This is of the form Name@Out:param1@Ref:param2...
		/// </summary>
		public string HashText
		{
			get
			{
				string s = this.Name;
				s += "@" + FirstParameter.Direction.ToString();
				s += ":" + FirstParameter.Type.TypeName;
				s += "@" + SecondParameter.Direction.ToString();
				s += ":" + SecondParameter.Type.TypeName;
				return s;
			}
		}
		#endregion
		#region  Name 
		/// <summary>
		/// Gets the (hashed) name of the operator. This is in the form "@op:Plus" so as to avoid potential conflicts with a method or field named 'Plus'. This is not a code element, just used for type attribution.
		/// </summary>
		/// <exclude/>
		public string				Name					
		{
			get
			{
				return "@op:" + Operator.ToString();
			}
		}	
		#endregion	
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				string s = this.GetType().Name;
				s = s.Substring(0, s.LastIndexOf("Decl"));
				return s; 
			}
		}
		#endregion
		/// <summary>
		/// A helper method that converts strings to operators. Note: the unary plus and negation strings should have a 'u' appened to differentiate them from addition and subtraction strings.
		/// </summary>
		/// <param name="sop">String to convert to an OverloadableOperator.</param>
		/// <returns>An OverloadableOperator.</returns>
		public static OverloadableOperator OperatorFromString(string sop)
		{
			OverloadableOperator oop = OverloadableOperator.Empty;
			switch(sop)
			{
				case "implicit" :
					oop = OverloadableOperator.Implicit;
					break;
				case "explicit" :
					oop = OverloadableOperator.Explicit;
					break;
				case "+u" :
					oop = OverloadableOperator.UnaryPlus;
					break;
				case "-u" :
					oop = OverloadableOperator.UnaryNegation;
					break;
				case "!" :
					oop = OverloadableOperator.Not;
					break;
				case "~" :
					oop = OverloadableOperator.OnesComplement;
					break;
				case "++" :
					oop = OverloadableOperator.Increment;
					break;
				case "--" :
					oop = OverloadableOperator.Decrement;
					break;
				case "true" :
					oop = OverloadableOperator.True;
					break;
				case "false" :
					oop = OverloadableOperator.False;
					break;
				case "+" :
					oop = OverloadableOperator.Addition;
					break;
				case "-" :
					oop = OverloadableOperator.Subtraction;
					break;
				case "*" :
					oop = OverloadableOperator.Multiply;
					break;
				case "/" :
					oop = OverloadableOperator.Division;
					break;
				case "%" :
					oop = OverloadableOperator.Modulus;
					break;
				case "&" :
					oop = OverloadableOperator.BitwiseAnd;
					break;
				case "|" :
					oop = OverloadableOperator.BitwiseOr;
					break;
				case "^" :
					oop = OverloadableOperator.ExclusiveOr;
					break;
				case "<<" :
					oop = OverloadableOperator.LeftShift;
					break;
				case ">>" :
					oop = OverloadableOperator.RightShift;
					break;
				case "==" :
					oop = OverloadableOperator.Equality;
					break;
				case "!=" :
					oop = OverloadableOperator.Inequality;
					break;
				case ">" :
					oop = OverloadableOperator.GreaterThan;
					break;
				case "<" :
					oop = OverloadableOperator.LessThan;
					break;
				case ">=" :
					oop = OverloadableOperator.GreaterThanOrEqual;
					break;
				case "<=" :
					oop = OverloadableOperator.LessThanOrEqual;
					break;
			}
			return oop;
		}
		/// <summary>
		/// A helper method that converts operators to strings. Note: the unary plus and negation strings returned will NOT have a 'u' appened as that usually isn't desired. 
		/// </summary>
		/// <param name="sop">OverloadableOperator to be converted to text.</param>
		/// <returns>A string representation of the input operator.</returns>
		public static string StringFromOperator(OverloadableOperator sop)
		{
			string oop = "";
			switch(sop)
			{
				case OverloadableOperator.Implicit :
					oop = "implicit";
					break;
				case OverloadableOperator.Explicit :
					oop = "explicit";
					break;
				case OverloadableOperator.UnaryPlus :
					oop = "+";
					break;
				case OverloadableOperator.UnaryNegation :
					oop = "-";
					break;
				case OverloadableOperator.Not :
					oop = "!";
					break;
				case OverloadableOperator.OnesComplement :
					oop = "~";
					break;
				case OverloadableOperator.Increment :
					oop = "++";
					break;
				case OverloadableOperator.Decrement :
					oop = "--";
					break;
				case OverloadableOperator.True :
					oop = "true";
					break;
				case OverloadableOperator.False :
					oop = "false";
					break;
				case OverloadableOperator.Addition :
					oop = "+";
					break;
				case OverloadableOperator.Subtraction :
					oop = "-";
					break;
				case OverloadableOperator.Multiply :
					oop = "*";
					break;
				case OverloadableOperator.Division :
					oop = "/";
					break;
				case OverloadableOperator.Modulus :
					oop = "%";
					break;
				case OverloadableOperator.BitwiseAnd :
					oop = "&";
					break;
				case OverloadableOperator.BitwiseOr :
					oop = "|";
					break;
				case OverloadableOperator.ExclusiveOr :
					oop = "^";
					break;
				case OverloadableOperator.LeftShift :
					oop = "<<";
					break;
				case OverloadableOperator.RightShift :
					oop = ">>";
					break;
				case OverloadableOperator.Equality :
					oop = "==";
					break;
				case OverloadableOperator.Inequality :
					oop = "!=";
					break;
				case OverloadableOperator.GreaterThan :
					oop = ">";
					break;
				case OverloadableOperator.LessThan :
					oop = "<";
					break;
				case OverloadableOperator.GreaterThanOrEqual :
					oop = ">=";
					break;
				case OverloadableOperator.LessThanOrEqual :
					oop = "<=";
					break;
			}
			return oop;
		}
	}
			#endregion
		#region  ConstructorDecl  -ds
	/// <summary>
	/// A constructor declartation.
	/// </summary> 
	public class ConstructorDecl : MemberDecl, IDeclaration, IScope, IOverloadable   
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.ConstructorDecl;}}
		public override MemberKind MemberKind{get{return MemberKind.Constructor;}}
			#region  Name 
		/// <summary>
		/// Gets or sets the name of the constructor (which matches the name of the class).
		/// </summary>
		[CodeElement]
		public string				Name					
		{
			get
			{
				return p_definition.Name;
			}
			set
			{
				p_definition.Name = value;
			}
		}	
	#endregion	
			#region  InvokeBase  
		private bool p_invokeBase = false;
		/// <summary>
		/// Gets or sets a flag that indicates the constructor specifies which base class should be called and thus requires a base call - eg MyDerived():base(i).
		/// </summary>
		[CodeElement]
		public bool				InvokeBase					
		{
			get
			{
				return p_invokeBase;
			}
			set
			{
				p_invokeBase = value;
			}
		}	
	#endregion
			#region  InvokeChain  
		private bool p_invokeChain = false;
		/// <summary>
		/// Gets or sets a flag that indicates the constructor calls a separate overloaded constructor before proceeding - eg MyDerived():this(i).
		/// </summary>
		[CodeElement]
		public bool				InvokeChain					
		{
			get
			{
				return p_invokeChain;
			}
			set
			{
				p_invokeChain = value;
			}
		}	
	#endregion
			#region  BaseParameters  
		private ParamCollection p_baseParameters = new ParamCollection();
		/// <summary>
		/// Gets a collection of parameters to be passed to the base class as the constructor is initialized (the parameters for a ':base()' call).
		/// </summary>
		[CodeCollection]
		public ParamCollection				BaseParameters					
		{
			get
			{
				return p_baseParameters;
			}
		}	
	#endregion	
			#region  ChainParameters  
		private ParamCollection p_chainParameters = new ParamCollection();
		/// <summary>
		/// Gets a collection of parameters to be passed to a separate overloaded constructor as this constructor is initialized (the parameters for a ':this()' call).
		/// </summary>
		[CodeCollection]
		public ParamCollection				ChainParameters					
		{
			get
			{
				return p_chainParameters;
			}
		}	
	#endregion
			#region  Parameters  
		private ParamDeclCollection p_parameters = new ParamDeclCollection();
		/// <summary>
		/// Gets the collection of parameters for the constructor. Each of these hold information about the type, direction etc.
		/// </summary>
		[CodeCollection]
		public ParamDeclCollection				Parameters					
		{
			get
			{
				return p_parameters;
			}
		}	
	#endregion	
			#region  Statements  
		private StatementCollection p_statements = new StatementCollection();
		/// <summary>
		/// Gets the collection of statements in the constructor.
		/// </summary>
		[CodeCollection]
		public StatementCollection				Statements					
		{
			get
			{
				return p_statements;
			}
		}	
		#endregion
		#region Definition
		private IDefinition p_definition = new OverloadableDefinition();
		/// <summary>
		/// Gets the (scoped) definition.
		/// </summary>
		public IDefinition Definition
		{
			get
			{
				return p_definition;
			}
		}
		#endregion
		#region  Scope 
		private Scope p_scope; 
		public Scope			Scope					
		{
			get
			{
				return p_scope;
			}
			set
			{
				p_scope = value;
			}
		}	
		#endregion	
		#region HashText
		/// <exclude/>
		/// <summary>
		/// Gets the hash string of this defintion. This is of the form Name@Out:param1@Ref:param2...
		/// </summary>
		public string HashText
		{
			get
			{
				string s = this.Name;
				foreach(ParamDecl pd in Parameters)
				{
					s += "@" + pd.Direction.ToString();
					s += ":" + pd.Type.TypeName;
				}
				return s;
			}
		}
		#endregion
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				string s = this.GetType().Name;
				s = s.Substring(0, s.LastIndexOf("Decl"));
				s += " - ";
				if(Definition == null) s += this.Name;
				else s += Definition.ToString();
				return s; //+ ": " + Name;
			}
		}
		#endregion
	}
			#endregion
		#region  DestructorDecl  -ds
	/// <summary>
	/// A destructor declartation.
	/// </summary> 
	public class DestructorDecl : MemberDecl, IDeclaration, IScope   
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.DestructorDecl;}}

		public override MemberKind MemberKind{get{return MemberKind.Destructor;}}
			#region  Name 
		/// <summary>
		/// Gets or sets the name of the destructor. This does not include the tilde ('~') character, so it matches the class name.
		/// </summary>
		[CodeElement]
		public string				Name					
		{
			get
			{
				return p_definition.Name;
			}
			set
			{
				p_definition.Name = value;
			}
		}	
	#endregion	
			#region  Statements  
		private StatementCollection p_statements = new StatementCollection();
		/// <summary>
		/// Gets the collection of statements in the destructor.
		/// </summary>
		[CodeCollection]
		public StatementCollection				Statements					
		{
			get
			{
				return p_statements;
			}
		}	
		#endregion
		#region Definition
		private IDefinition p_definition = new Definition();
		/// <summary>
		/// Gets the (scoped) definition.
		/// </summary>
		public IDefinition Definition
		{
			get
			{
				return p_definition;
			}
		}
		#endregion
		#region  Scope 
		private Scope p_scope; 
		public Scope			Scope					
		{
			get
			{
				return p_scope;
			}
			set
			{
				p_scope = value;
			}
		}	
		#endregion	
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				string s = this.GetType().Name;
				s = s.Substring(0, s.LastIndexOf("Decl"));
				s += " - ";
				if(Definition == null) s += this.Name;
				else s += Definition.ToString();
				return s; //+ ": " + Name;
			}
		}
		#endregion
	}
			#endregion
		#region  AccessorDecl  -s
	/// <summary>
	/// An accessor declartation.
	/// </summary> 
	public class AccessorDecl : MemberDecl, IScope   
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.AccessorDecl;}}

		public override MemberKind MemberKind{get{return MemberKind.Accessor;}}
			#region  AccessorModifier  
		private AccessorModifiers p_accessorModifier = AccessorModifiers.Empty;
		/// <summary>
		/// Gets or sets the type of accessor (get, set, add, or remove).
		/// </summary>
		[CodeElement]
		public AccessorModifiers	AccessorModifier	
		{
			get
			{
				return p_accessorModifier;
			}
			set
			{
				p_accessorModifier = value;
			}
		}	
			#endregion
			#region  Statements  
		private StatementCollection p_statements = new StatementCollection();
		/// <summary>
		/// Gets the collection of statements in the accessor.
		/// </summary>
		[CodeCollection]
		public StatementCollection				Statements					
		{
			get
			{
				return p_statements;
			}
		}	
		#endregion
		#region  Scope 
		private Scope p_scope; 
		public Scope			Scope					
		{
			get
			{
				return p_scope;
			}
			set
			{
				p_scope = value;
			}
		}	
		#endregion	
	}
	#endregion
		#region  EnumMemberDecl  -d
	/// <summary>
	/// An enum declartation.
	/// </summary> 
	public class EnumMemberDecl : MemberDecl, IDeclaration  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.EnumMemberDecl;}}
		public override MemberKind MemberKind{get{return MemberKind.EnumMember;}}
			#region  Name 
		/// <summary>
		/// Gets or sets the name of the enum.
		/// </summary> 
		[CodeElement]
		public string				Name					
		{
			get
			{
				return p_definition.Name;
			}
			set
			{
				p_definition.Name = value;
			}
		}	
	#endregion	
			#region  Value  
		private Expression p_value;
		/// <summary>
		/// Gets or sets the value of the enum member, if specified.
		/// </summary>
		[CodeElement]
		public Expression				Value					
		{
			get
			{
				return p_value;
			}
			set
			{
				p_value = value;
			}
		}	
		#endregion
		#region Definition
		private IDefinition p_definition = new Definition();
		/// <summary>
		/// Gets the (scoped) definition.
		/// </summary>
		public IDefinition Definition
		{
			get
			{
				return p_definition;
			}
		}
		#endregion
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				string s = this.GetType().Name;
				s = s.Substring(0, s.LastIndexOf("Decl"));
				s += " - " + this.Name;
				return s; //+ ": " + Name;
			}
		}
		#endregion
	}
			#endregion

		#region  TypeDecl  
	/// <summary>
	/// An abstract base class for types (classes, structs, interfaces etc). 
	/// </summary> 
	public abstract class TypeDecl : MemberDecl, IDeclaration  
	{
			public abstract TypeKind TypeKind{get;}
			public override MemberKind MemberKind{get{return MemberKind.Type;}}
			#region  TypeAttributes 
		// does this have to go..? From codeDom
		private TypeModifiers p_typeAttributes;
		/// <summary>
		/// Gets or sets the modifiers for the type (public, sealed etc). These values can be or'ed with each other. This was derived from the CodeDom, it may be changed for something more suitable.
		/// </summary>
		[CodeElement]
		public TypeModifiers		TypeAttributes	
		{
			get
			{
				return p_typeAttributes;
			}
			set
			{
				p_typeAttributes = value;
			}
		}	
			#endregion
			#region  BaseTypes  
		private TypeRefCollection p_baseTypes = new TypeRefCollection();
		/// <summary>
		/// Gets or sets the base types that are inherited by this type.
		/// </summary>
		[CodeCollection]
		public TypeRefCollection				BaseTypes					
		{
			get
			{
				return p_baseTypes;
			}
			set
			{
				p_baseTypes = value;
			}
		}	
			#endregion
			#region  Members  
		private MemberDeclCollection p_members = new MemberDeclCollection();
		/// <summary>
		/// Gets a collection of type members in the current type (methods, fields etc.).
		/// </summary>
		[CodeCollection]
		public MemberDeclCollection				Members					
		{
			get
			{
				return p_members;
			}
		}	
			#endregion
			#region  Name 
		[CodeElement]
		public string				Name					
		{
			get
			{
				return p_definition.Name;
			}
			set
			{
				p_definition.Name = value;
			}
		}	
		#endregion	
		#region Definition
		private IDefinition p_definition = new Definition();
		/// <summary>
		/// Gets the (scoped) definition.
		/// </summary>
		public IDefinition Definition
		{
			get
			{
				return p_definition;
			}
		}
		#endregion
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
//				string s = this.GetType().Name;
//				s = s.Substring(0, s.LastIndexOf("Decl"));
//				s += " - " + this.Name;
//				return s; //+ ": " + Name;
				return Definition.ToString();
			}
		}
		#endregion
		#region DistanceToType
		/// <summary>
		/// Determines if this type is a subtype of the passed type.
		/// </summary>
		/// <param name="baseType">Base type to check.</param>
		/// <returns>True if this is a subtype.</returns>
		public abstract int DistanceToType(IDefinition baseType, int distance);
		#endregion

	}
		#endregion
			#region  ClassDecl  -ds
	/// <summary>
	/// A class declartation.
	/// </summary> 
	public class ClassDecl : TypeDecl, IScope
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.ClassDecl;}}
		public override TypeKind TypeKind{get{return TypeKind.Class;}}
		#region  Scope 
		private Scope p_scope; 
		public Scope			Scope					
		{
			get
			{
				return p_scope;
			}
			set
			{
				p_scope = value;
			}
		}	
		#endregion	
		#region  Constructors 
		public OverloadableDefinition	Constructors					
		{
			get
			{
				if(Scope == null) return null;
				return (OverloadableDefinition)Scope.DefinitionTable[this.Name];
			}
		}	
		#endregion	
		#region DistanceToType
		public override int DistanceToType(IDefinition baseType, int distance)
		{
			if(baseType == Definition) return distance;
			distance++;
			foreach(TypeRef tr in BaseTypes)
			{
				if(tr == baseType) return distance;
			}
			foreach(TypeRef tr in BaseTypes)
			{
				int gpDist = 
					((TypeDecl)tr.Definition.SourceGraph).DistanceToType(baseType, distance);
				if(gpDist < Int32.MaxValue) return gpDist;
			}
			if(baseType.Name.ToLower() == "object" && 
				(BaseTypes.Count == 0 || BaseTypes[0].GraphType == GraphTypes.InterfaceDecl))
				return distance;
			return Int32.MaxValue;
		}
    	#endregion

	}
			#endregion
			#region  InterfaceDecl  -ds 
	/// <summary>
	/// An interface declartation.
	/// </summary> 
	public class InterfaceDecl : TypeDecl, IScope
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.InterfaceDecl;}}
		public override TypeKind TypeKind{get{return TypeKind.Interface;}}
		#region  Scope 
		private Scope p_scope; 
		public Scope			Scope					
		{
			get
			{
				return p_scope;
			}
			set
			{
				p_scope = value;
			}
		}	
		#endregion	
		#region DistanceToType
		public override int DistanceToType(IDefinition baseType, int distance)
		{
			if(baseType == Definition) return distance;
			distance++;
			foreach(TypeRef tr in BaseTypes)
			{
				if(tr == baseType) return distance;
			}
			foreach(TypeRef tr in BaseTypes)
			{
				int gpDist = 
					((TypeDecl)tr.Definition.SourceGraph).DistanceToType(baseType, distance);
				if(gpDist < Int32.MaxValue) return gpDist;
			}
			if(baseType.Name.ToLower() == "object" && 
				(BaseTypes.Count == 0 || BaseTypes[0].GraphType == GraphTypes.InterfaceDecl))
				return distance;
			return Int32.MaxValue;
		}
    	#endregion
	}
			#endregion
			#region  StructDecl  -ds 
	/// <summary>
	/// A struct declartation.
	/// </summary> 
	public class StructDecl : TypeDecl, IScope
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.StructDecl;}}
		public override TypeKind TypeKind{get{return TypeKind.Struct;}}
		#region  Scope 
		private Scope p_scope; 
		public Scope			Scope					
		{
			get
			{
				return p_scope;
			}
			set
			{
				p_scope = value;
			}
		}	
		#endregion	
		#region  Constructors 
		public OverloadableDefinition	Constructors					
		{
			get
			{
				if(Scope == null) return null;
				return (OverloadableDefinition)Scope.DefinitionTable[this.Name];
			}
		}	
		#endregion	
		#region DistanceToType
		public override int DistanceToType(IDefinition baseType, int distance)
		{
			if(baseType == Definition) return distance;
			distance++;
			foreach(TypeRef tr in BaseTypes)
			{
				if(tr == baseType) return distance;
			}
			foreach(TypeRef tr in BaseTypes)
			{
				int gpDist = 
					((TypeDecl)tr.Definition.SourceGraph).DistanceToType(baseType, distance);
				if(gpDist < Int32.MaxValue) return gpDist;
			}
			if(baseType.Name.ToLower() == "object" && 
				(BaseTypes.Count == 0 || BaseTypes[0].GraphType == GraphTypes.InterfaceDecl))
				return distance;
			return Int32.MaxValue;
		}
    	#endregion
	}
			#endregion
			#region  EnumDecl  -ds 
	/// <summary>
	/// An enum declartation.
	/// </summary> 
	public class EnumDecl : TypeDecl, IScope 
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.EnumDecl;}}
		public override TypeKind TypeKind{get{return TypeKind.Enum;}}
		#region  Scope 
		private Scope p_scope; 
		public Scope			Scope					
		{
			get
			{
				return p_scope;
			}
			set
			{
				p_scope = value;
			}
		}	
		#endregion	
		#region DistanceToType
		public override int DistanceToType(IDefinition baseType, int distance)
		{
			if(baseType == Definition) return distance;
			if(baseType.Name.ToLower() == "object") return 3; // enum ValueType object
			return Int32.MaxValue;
		}
    	#endregion
	}
	#endregion
			#region  DelegateDecl  -d 
	/// <summary>
	/// A delegate declartation.
	/// </summary> 
	public class DelegateDecl : TypeDecl
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.DelegateDecl;}}
		public override TypeKind TypeKind{get{return TypeKind.Delegate;}}

				#region  Parameters  
		private ParamDeclCollection p_parameters = new ParamDeclCollection();
		/// <summary>
		/// Gets the collection of parameters for the delegate. Each of these hold information about the type, direction etc.
		/// </summary>
		[CodeCollection]
		public ParamDeclCollection				Parameters					
		{
			get
			{
				return p_parameters;
			}
		}	
	#endregion	
				#region  ReturnType  
		private TypeRef p_returnType;
		/// <summary>
		/// Gets or sets the return type of the delegate.
		/// </summary>
		[CodeElement]
		public TypeRef				ReturnType					
		{
			get
			{
				return p_returnType;
			}
			set
			{
				p_returnType = value;
			}
		}	
		#endregion
		#region DistanceToType
		public override int DistanceToType(IDefinition baseType, int distance)
		{
			if(baseType == Definition) return distance;
			if(baseType.Name.ToLower() == "object") return 1; // delegate object
			return Int32.MaxValue;
		}
    	#endregion
	}
    		#endregion

	#region  Statement  
	/// <summary>
	/// An abstract base class for statements.
	/// </summary>
	public abstract class Statement : CSharpGraph  
	{
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				return this.GetType().Name.ToString();
			}
		}
		#endregion
	}
	#endregion
		#region  ExprStmt  
	/// <summary>
	/// A code expression that can be treated as a code statement.
	/// </summary>
	public class ExprStmt : Statement  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.ExprStmt;}}
			#region  Expression  
		private Expression p_expression;
		/// <summary>
		/// Gets or sets the expression this statement encapsulates. 
		/// </summary>
		[CodeElement]
		public Expression				Expression					
		{
			get
			{
				return p_expression;
			}
			set
			{
				p_expression = value;
			}
		}	
	#endregion		
	}
		#endregion
		#region  CommentStmt 
	/// <summary>
	/// A normal comment.
	/// </summary>
	public class CommentStmt : Statement  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.CommentStmt;}}

			#region  Comment  
		private Comment p_comment = new Comment();
		/// <summary>
		/// Gets or sets the comment string.
		/// </summary>
		[CodeElement]
		public Comment				Comment					
		{
			get
			{
				return p_comment;
			}
			set
			{
				p_comment = value;
			}
		}	
	#endregion
	}
		#endregion
		#region  VariableDeclStmt  
	/// <summary>
	/// A variable declaration statement.
	/// </summary>
	public class VariableDeclStmt : Statement  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.VariableDeclStmt;}}

		#region  Delcarators 
		private DeclaratorCollection p_declarators = new DeclaratorCollection(); 
		/// <summary>
		/// Gets the collection of declarators created. Variable declarations can declare multiple variables at once, so this must be a collection.
		/// </summary>
		[CodeCollection]
		public DeclaratorCollection			Delcarators					
		{
			get
			{
				return p_declarators;
			}
		}	
	#endregion	
		#region  Type  
		private TypeRef p_type;
		/// <summary>
		/// Gets or sets the type of the variable. While one can declare multiple variables they must be of the same type - therefore this is not a collection.
		/// </summary>
		[CodeElement]
		public TypeRef				Type					
		{
			get
			{
				return p_type;
			}
			set
			{
				p_type = value;
			}
		}	
	#endregion
		#region  DimensionCount  
		private int p_dimensions = 0;
		/// <summary>
		/// Gets or sets the number of dimensions if this is an array. Zero dimensions indicates this is not an array. The dimensions are represented by commas in C#, like [,,].
		/// </summary>
		[CodeElement]
		public int				DimensionCount				
		{
			get
			{
				return p_dimensions;
			}
			set
			{
				p_dimensions = value;
			}
		}	
	#endregion

	}
		#endregion
		#region  ConstantDeclStmt  
	/// <summary>
	/// A constant declaration statement.
	/// </summary>
	public class ConstantDeclStmt : Statement  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.ConstantDeclStmt;}}

		#region  Delcarators 
		private DeclaratorCollection p_declarators = new DeclaratorCollection(); 
		/// <summary>
		/// Gets the collection of declarators created. Constant declarations can declare multiple variables at once, so this must be a collection.
		/// </summary>
		[CodeCollection]
		public DeclaratorCollection			Delcarators					
		{
			get
			{
				return p_declarators;
			}
		}	
	#endregion	
		#region  Type  
		private TypeRef p_type;
		/// <summary>
		/// Gets or sets the type of the constant. While one can declare multiple constant variables they must be of the same type - therefore this is not a collection.
		/// </summary>
		[CodeElement]
		public TypeRef				Type					
		{
			get
			{
				return p_type;
			}
			set
			{
				p_type = value;
			}
		}	
	#endregion
	}
		#endregion
		#region  IfStmt  
	/// <summary>
	/// An if (conditional) statement.
	/// </summary>
	public class IfStmt : Statement  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.IfStmt;}}
		#region  Condition  
		private Expression p_condition; 
		/// <summary>
		/// Gets or sets the condition to be tested.
		/// </summary>
		[CodeElement]
		public Expression				Condition					
		{
			get
			{
				return p_condition;
			}
			set
			{
				p_condition = value;
			}
		}	
	#endregion	
		#region  TrueStatements  
		private StatementCollection p_trueStatements = new StatementCollection();
		/// <summary>
		/// Gets or sets the statements to be executed if the condition is true.
		/// </summary>
		[CodeCollection]
		public StatementCollection				TrueStatements					
		{
			get
			{
				return p_trueStatements;
			}
		}	
	#endregion	
		#region  FalseStatements  
		private StatementCollection p_falseStatements = new StatementCollection();
		/// <summary>
		/// Gets or sets the statements to be executed if the condition is false.
		/// </summary>
		[CodeCollection]
		public StatementCollection				FalseStatements					
		{
			get
			{
				return p_falseStatements;
			}
		}	
	#endregion
	}
		#endregion
		#region  SwitchStmt
	/// <summary>
	/// A switch statement.
	/// </summary>
	public class SwitchStmt : Statement  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.SwitchStmt;}}
		#region  Condition  
		private Expression p_condition; 
		/// <summary>
		/// Gets or sets the switch condition to be tested.
		/// </summary>
		[CodeElement]
		public Expression				Condition					
		{
			get
			{
				return p_condition;
			}
			set
			{
				p_condition = value;
			}
		}	
	#endregion	
		#region  Cases  
		private CaseCollection p_cases = new CaseCollection();
		/// <summary>
		/// Gets a collection of cases that will be called based on the condition.
		/// </summary>
		[CodeCollection]
		public CaseCollection				Cases					
		{
			get
			{
				return p_cases;
			}
		}	
	#endregion	
	}
		#endregion
		  #region  Case  
	/// <summary>
	/// A case clause for a switch statement.
	/// </summary>
	public class Case : CSharpGraph  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.Case;}}
		#region  IsDefault  
		private bool p_default = false;
		/// <summary>
		/// Gets or sets the flag that indicates whether or not this is a 'default' case.
		/// </summary>
		[CodeElement]
		public bool				IsDefault					
		{
			get
			{
				return p_default;
			}
			set
			{
				p_default = value;
			}
		}	
	#endregion	
		#region  Condition  
		private Expression p_condition;
		/// <summary>
		/// Gets or sets the expression that the switch condition will be compared to.
		/// </summary>
		[CodeElement]
		public Expression				Condition					
		{
			get
			{
				return p_condition;
			}
			set
			{
				p_condition = value;
			}
		}	
	#endregion	
		#region  Statements  
		private StatementCollection p_statements = new StatementCollection();
		/// <summary>
		/// Gets the collection of statements in the case clause. This must include a break statement.
		/// </summary>
		[CodeCollection]
		public StatementCollection				Statements					
		{
			get
			{
				return p_statements;
			}
		}	
		#endregion
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				return this.GetType().Name;
			}
		}
		#endregion
	}
		#endregion
		#region  IterationStmt 
	/// <summary>
	/// A loop construct. This can be a for, do or while loop, however foreach loops are treated separately (as a ForEachStmt).
	/// </summary>
	public class IterationStmt : Statement  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.IterationStmt;}}

			#region  IterationType  
		private IterationType p_iType = IterationType.While; 
		/// <summary>
		/// Gets or sets the type of iteration (for, while, do). This is mostly useful when rebuilding code.
		/// </summary>
		[CodeElement]
		public IterationType			IterationType					
		{
			get
			{
				return p_iType;
			}
			set
			{
				p_iType = value;
			}
		}	
	#endregion	
			#region  TestFirst 
		/// <summary>
		/// Gets or sets a flag that specifies the test happens at the start of a loop, like in a 'while' loop. 'For' and 'do' loops test at the end of the loop.
		/// </summary>
		[CodeElement]
		public bool				TestFirst					
		{
			get
			{
				if(p_iType == IterationType.Do) return false;
				return true;
			}
		}	
	#endregion	
			#region  Init  
		private StatementCollection p_init = new StatementCollection();
		/// <summary>
		/// Gets a collection of statements that initalize the loop. 
		/// </summary>
		[CodeCollection]
		public StatementCollection			Init					
		{
			get
			{
				return p_init;
			}
		}	
	#endregion	
			#region  Test  
		private Expression p_test;
		/// <summary>
		/// Gets or sets the test condition that determines if the loop will be terminated.
		/// </summary>
		[CodeElement]
		public Expression				Test					
		{
			get
			{
				return p_test;
			}
			set
			{
				p_test = value;
			}
		}	
	#endregion	
			#region  Increment  
		private StatementCollection p_increment = new StatementCollection();
		/// <summary>
		/// Gets a collection of statements that increments the loop. 
		/// </summary>
		[CodeCollection]
		public StatementCollection				Increment					
		{
			get
			{
				return p_increment;
			}
		}	
	#endregion	
			#region  Statements  
		private StatementCollection p_statements = new StatementCollection();
		/// <summary>
		/// Gets the collection of statements that will be looped over.
		/// </summary>
		[CodeCollection]
		public StatementCollection				Statements					
		{
			get
			{
				return p_statements;
			}
		}	
	#endregion
	}
		#endregion
		#region  ForEachStmt
	/// <summary>
	/// A foreach statement that enumerates over a collection.
	/// </summary>
	public class ForEachStmt : Statement  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.ForEachStmt;}}

		#region  IterationType  
		/// <summary>
		/// Gets the type of iteration statement. This will always be 'IterationType.ForEach'.
		/// </summary>
		[CodeElement]
		public IterationType			IterationType					
		{
			get
			{
				return IterationType.ForEach;
			}
		}	
	#endregion	
		#region  Name  
		private string p_Name = "";
		/// <summary>
		/// Gets or sets the name of the variable that holds the current enumeration value.
		/// </summary>
		[CodeElement]
		public string				Name					
		{
			get
			{
				return p_Name;
			}
			set
			{
				p_Name = value;
			}
		}	
	#endregion	
		#region  Type  
		private TypeRef p_type;
		/// <summary>
		/// Gets or sets the type of the object to be found in the collection.
		/// </summary>
		[CodeElement]
		public TypeRef				Type					
		{
			get
			{
				return p_type;
			}
			set
			{
				p_type = value;
			}
		}	
	#endregion
		#region  Collection  
		private Expression p_collection;
		/// <summary>
		/// Gets the collection that will be searched.
		/// </summary>
		[CodeElement]
		public Expression				Collection					
		{
			get
			{
				return p_collection;
			}
			set
			{
				p_collection = value;
			}
		}	
	#endregion	
		#region  Statements  
		private StatementCollection p_statements = new StatementCollection();
		/// <summary>
		/// Gets the collection of statements in the loop.
		/// </summary>
		[CodeCollection]
		public StatementCollection				Statements					
		{
			get
			{
				return p_statements;
			}
		}	
	#endregion
	}
		#endregion
		#region  GotoStmt  
	/// <summary>
	/// A goto statement, jumps to a label or a case clause.
	/// </summary>
	public class GotoStmt : Statement  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.GotoStmt;}}
			#region  Label  
		private string p_label = "";
		/// <summary>
		/// Gets or sets the label to jump to. If this jumps between cases, the label will be 'case' or 'default'.
		/// </summary>
		[CodeElement]
		public string				Label					
		{
			get
			{
				return p_label;
			}
			set
			{
				p_label = value;
			}
		}	
	#endregion
			#region  CaseLabel  
		private Expression p_caseLabel;
		/// <summary>
		/// Gets or sets the case statement to jump to if this is inside a switch statement. The label holds the word 'case', so this is just the expression part.
		/// </summary>
		[CodeElement]
		public Expression				CaseLabel					
		{
			get
			{
				return p_caseLabel;
			}
			set
			{
				p_caseLabel = value;
			}
		}	
	#endregion
	}
		#endregion
		#region  LabeledStmt  
	/// <summary>
	/// A labeled statement that can be jumped to with a goto statement.
	/// </summary>
	public class LabeledStmt : Statement  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.LabeledStmt;}}
			#region  Label  
		private string p_label = "";
		/// <summary>
		/// Gets or sets the label text.
		/// </summary>
		[CodeElement]
		public string				Label					
		{
			get
			{
				return p_label;
			}
			set
			{
				p_label = value;
			}
		}	
	#endregion	
			#region  Statement  
		private Statement p_statement = null;
		/// <summary>
		/// Gets or sets the statement the label is attached to.
		/// </summary>
		[CodeElement]
		public Statement				Statement					
		{
			get
			{
				return p_statement;
			}
			set
			{
				p_statement = value;
			}
		}	
	#endregion
	}
		#endregion
		#region  ReturnStmt  
	/// <summary>
	/// A return statement.
	/// </summary>
	public class ReturnStmt : Statement  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.ReturnStmt;}}
			#region  Expression  
		private Expression p_expression;
		/// <summary>
		/// Gets or sets the expression to be returned. Can be left empty for void returns.
		/// </summary>
		[CodeElement]
		public Expression				Expression					
		{
			get
			{
				return p_expression;
			}
			set
			{
				p_expression = value;
			}
		}	
	#endregion
	}
		#endregion

		#region  BreakStmt 
	/// <summary>
	/// A break statement.
	/// </summary>
	public class BreakStmt : Statement  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.BreakStmt;}}
	}
		#endregion
		#region  ContinueStmt  
	/// <summary>
	/// A continue statement.
	/// </summary>
	public class ContinueStmt : Statement  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.ContinueStmt;}}
	}
		#endregion
		#region  CheckedStmt  
	/// <summary>
	/// A checked statement - explicitly checks for overflow. 
	/// </summary>
	public class CheckedStmt : Statement  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.CheckedStmt;}}
		#region  Statements  
		private StatementCollection p_statements = new StatementCollection();
		/// <summary>
		/// Gets the collection of statements in the checked statement.
		/// </summary>
		[CodeCollection]
		public StatementCollection				Statements					
		{
			get
			{
				return p_statements;
			}
		}	
	#endregion

	}
		#endregion
		#region  UncheckedStmt  
	/// <summary>
	/// An unchecked statement - explicitly ignores overflow. 
	/// </summary>
	public class UncheckedStmt : Statement  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.UncheckedStmt;}}

		#region  Statements  
		private StatementCollection p_statements = new StatementCollection();
		/// <summary>
		/// Gets the collection of statements in the unchecked statement.
		/// </summary>
		[CodeCollection]
		public StatementCollection				Statements					
		{
			get
			{
				return p_statements;
			}
		}	
	#endregion

	}
		#endregion
		#region  LockStmt
	/// <summary>
	/// A lock statement.
	/// </summary>
	public class LockStmt : Statement  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.LockStmt;}}

		#region  Target  
		private Expression p_target;
		/// <summary>
		/// Gets or sets the target to be locked for the duration of the statement block.
		/// </summary>
		[CodeElement]
		public Expression				Target					
		{
			get
			{
				return p_target;
			}
			set
			{
				p_target = value;
			}
		}	
	#endregion
		#region  Statements  
		private StatementCollection p_statements = new StatementCollection();
		/// <summary>
		/// Gets the collection of statements in the lock statement.
		/// </summary>
		[CodeCollection]
		public StatementCollection				Statements					
		{
			get
			{
				return p_statements;
			}
		}	
	#endregion
	}
		#endregion
		#region  UsingStmt
	/// <summary>
	/// A using statement, aquires a resource that will be disposed at the end of the scope.
	/// </summary>
	public class UsingStmt : Statement  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.UsingStmt;}}
		#region  Target  
		private Expression p_target;
		/// <summary>
		/// Gets or sets the target resource to be used - use the target expression if the using statement doesn't declare it's own target(s). In the normal case, where the using statement declares it's resources, use the Declaration property.
		/// </summary>
		[CodeElement]
		public Expression				Target					
		{
			get
			{
				return p_target;
			}
			set
			{
				p_target = value;
				p_dr = false;
			}
		}	
	#endregion
		#region  Declaration  
		private VariableDeclStmt p_vds;
		/// <summary>
		/// Gets or sets the resources declared, which will be disposed at the end of the statements block.
		/// </summary>
		[CodeElement]
		public VariableDeclStmt		Declaration					
		{
			get
			{
				return p_vds;
			}
			set
			{
				p_vds = value;
				p_dr = true;
			}
		}	
	#endregion
		#region  DeclaresResource  
		private bool p_dr;
		/// <summary>
		/// Gets a flag that determines if the using statement declares it's own resources.
		/// </summary>
		[CodeElement]
		public bool		DeclaresResource					
		{
			get
			{
				return p_dr;
			}
		}	
	#endregion
		#region  Statements  
		private StatementCollection p_statements = new StatementCollection();
		/// <summary>
		/// Gets the collection of statements in the block of the using statement.
		/// </summary>
		[CodeCollection]
		public StatementCollection				Statements					
		{
			get
			{
				return p_statements;
			}
		}	
	#endregion
	}
		#endregion

		#region  ThrowStmt 
	/// <summary>
	/// A throw statement.
	/// </summary>
	public class ThrowStmt : Statement  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.ThrowStmt;}}
			#region  ToThrow  
		private Expression p_toThrow;
		/// <summary>
		/// Gets or sets the exception that is thrown.
		/// </summary>
		[CodeElement]
		public Expression				ToThrow					
		{
			get
			{
				return p_toThrow;
			}
			set
			{
				p_toThrow = value;
			}
		}	
	#endregion
	}
		#endregion
		#region  TryCatchFinallyStmt  
	/// <summary>
	/// A try, catch, finally construct.
	/// </summary>
	public class TryCatchFinallyStmt : Statement  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.TryCatchFinallyStmt;}}
			#region  Try  
		private StatementCollection p_try = new StatementCollection();
		/// <summary>
		/// Gets a collection of statements to 'try'.
		/// </summary>
		[CodeCollection]
		public StatementCollection				Try					
		{
			get
			{
				return p_try;
			}
		}	
	#endregion	
			#region  CatchClauses  
		private CatchCollection p_catchClauses = new CatchCollection();
		/// <summary>
		/// Gets a collection of Catch clauses that will try to catch any exceptions thrown.
		/// </summary>
		[CodeCollection]
		public CatchCollection				CatchClauses					
		{
			get
			{
				return p_catchClauses;
			}
			set
			{
				p_catchClauses = value;
			}
		}	
	#endregion	
			#region  Finally  
		private StatementCollection p_finally = new StatementCollection();
		/// <summary>
		/// Gets a collection of statements that will always be executed (ermm, well, pretty well always).
		/// </summary>
		[CodeCollection]
		public StatementCollection				Finally					
		{
			get
			{
				return p_finally;
			}
		}	
	#endregion
	}
		#endregion
			#region  Catch  
	/// <summary>
	/// A catch clause.
	/// </summary>
	public class Catch : CSharpGraph  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.Catch;}}
			#region  CatchExceptionType  
		private TypeRef p_catchExceptionType;
		/// <summary>
		/// Gets or sets the type of exception this catches.
		/// </summary>
		[CodeElement]
		public TypeRef				CatchExceptionType					
		{
			get
			{
				return p_catchExceptionType;
			}
			set
			{
				p_catchExceptionType = value;
			}
		}	
	#endregion	
			#region  LocalName  
		private string p_localName = "";
		/// <summary>
		/// Gets or sets the variable name given to this exception.
		/// </summary>
		[CodeElement]
		public string				LocalName					
		{
			get
			{
				return p_localName;
			}
			set
			{
				p_localName = value;
			}
		}	
	#endregion	
			#region  Statements  
		private StatementCollection p_statements = new StatementCollection();
		/// <summary>
		/// Gets the collection of statements in the catch clause.
		/// </summary>
		[CodeCollection]
		public StatementCollection				Statements					
		{
			get
			{
				return p_statements;
			}
		}	
			#endregion
			#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				return this.GetType().Name;
			}
		}
		#endregion
	}
		#endregion
		#region  AttachDelegateStmt 
	/// <summary>
	/// Attaches a delegate to an event.
	/// </summary>
	public class AttachDelegateStmt : Statement  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.AttachDelegateStmt;}}
			#region  Event  
		private TypeRef p_event;
		/// <summary>
		/// Gets or sets the event to add the delegate to.
		/// </summary>
		[CodeElement]
		public TypeRef				Event					
		{
			get
			{
				return p_event;
			}
			set
			{
				p_event = value;
			}
		}	
	#endregion	
			#region  Listener  
		private Expression p_listener;
		/// <summary>
		/// Gets or sets the listener method to be attached.
		/// </summary>
		[CodeElement]
		public Expression				Listener					
		{
			get
			{
				return p_listener;
			}
			set
			{
				p_listener = value;
			}
		}	
	#endregion
	
	}
		#endregion
		#region  RemoveDelegateStmt  
	/// <summary>
	/// Removes a delegate from an event.
	/// </summary>
	public class RemoveDelegateStmt : Statement  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.RemoveDelegateStmt;}}
			#region  Event  
		private TypeRef p_event;
		/// <summary>
		/// Gets or sets the event to remove the delegate from.
		/// </summary>
		[CodeElement]
		public TypeRef				Event					
		{
			get
			{
				return p_event;
			}
			set
			{
				p_event = value;
			}
		}	
	#endregion	
			#region  Listener  
		private Expression p_listener;
		/// <summary>
		/// Gets or sets the listener method to be detached.
		/// </summary>
		[CodeElement]
		public Expression				Listener					
		{
			get
			{
				return p_listener;
			}
			set
			{
				p_listener = value;
			}
		}	
	#endregion	
	}
		#endregion


	#region  Expression 
	/// <summary>
	/// An abstract base class for expressions.
	/// </summary>
	public abstract class Expression : CSharpGraph  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.Expression;}}
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				return this.GetType().Name;
			}
		}
		#endregion	
	
		#region ResultType
		private IDefinition p_resultType;
		/// <exclude/>
		public  IDefinition ResultType
		{
			get
			{
				return p_resultType;
			}
			set
			{
				p_resultType = value;
			}
		}
		#endregion
	}
	#endregion
		#region  AssignExpr  
	/// <summary>
	/// An assignment expression.
	/// </summary>
	public class AssignExpr : Expression  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.AssignExpr;}}
			#region  Left  
		private Expression p_left;
		/// <summary>
		/// Gets or sets the left side of the assignment.
		/// </summary>
		[CodeElement]
		public Expression				Left					
		{
			get
			{
				return p_left;
			}
			set
			{
				p_left = value;
			}
		}	
	#endregion	
			#region  Right  
		private Expression p_right;
		/// <summary>
		/// Gets or sets the right side of the assignment.
		/// </summary>;
		[CodeElement]
		public Expression				Right					
		{
			get
			{
				return p_right;
			}
			set
			{
				p_right = value;
			}
		}	
	#endregion
			#region  Operator  
		private AssignOperator p_op = AssignOperator.Empty;
		/// <summary>
		/// Gets or sets the assignment operator.
		/// </summary>
		[CodeElement]
		public AssignOperator			Operator					
		{
			get
			{
				return p_op;
			}
			set
			{
				p_op = value;
			}
		}	
	#endregion			

			#region OperatorFromString
		/// <summary>
		/// A helper method that returns the AssignOperator from string input.
		/// </summary>
		/// <param name="op">The string to be converted.</param>
		/// <returns>The resulting AssignOperator.</returns>
		public static Dom.AssignOperator OperatorFromString(string op)	
		{
			AssignOperator retOp = AssignOperator.Empty; 
			switch (op)
			{
				case "=":
					retOp = AssignOperator.Assign;
					break;
				case "+=":
					retOp = AssignOperator.AdditionAssign;
					break;
				case "-=":
					retOp = AssignOperator.SubtractionAssign;
					break;
				case "*=":
					retOp = AssignOperator.MultiplyAssign;
					break;
				case "/=":
					retOp = AssignOperator.DivisionAssign;
					break;
				case "%=":
					retOp = AssignOperator.ModulusAssign;
					break;
				case "&=":
					retOp = AssignOperator.BitwiseAndAssign;
					break;
				case "|=":
					retOp = AssignOperator.BitwiseOrAssign;
					break;
				case  "~=":
					retOp = AssignOperator.ExclusiveOrAssign;
					break;
				case "<<=":
					retOp = AssignOperator.LeftShiftAssign;
					break;
				case ">>=":
					retOp = AssignOperator.RightShiftAssign;
					break;
			}	
			return retOp;	
		}		
		#endregion
			#region StringFromOperator
		/// <summary>
		/// A helper method that returns a string (that would be used in C# code) from an AssignOperator.
		/// </summary>
		/// <param name="op">The AssignOperator to be parsed.</param>
		/// <returns>The string representation.</returns>
		public static string StringFromOperator(AssignOperator op)	
		{
			string retOp = ""; 
			switch (op)
			{
				case AssignOperator.Assign:
					retOp = "=";
					break;
				case AssignOperator.AdditionAssign:
					retOp = "+=";
					break;
				case AssignOperator.SubtractionAssign:
					retOp = "-=";
					break;
				case AssignOperator.MultiplyAssign:
					retOp = "*=";
					break;
				case AssignOperator.DivisionAssign:
					retOp = "/=";
					break;
				case AssignOperator.ModulusAssign:
					retOp = "%=";
					break;
				case AssignOperator.BitwiseAndAssign:
					retOp = "&=";
					break;
				case AssignOperator.BitwiseOrAssign:
					retOp = "|=";
					break;
				case  AssignOperator.ExclusiveOrAssign:
					retOp = "~=";
					break;
				case AssignOperator.LeftShiftAssign:
					retOp = "<<=";
					break;
				case AssignOperator.RightShiftAssign:
					retOp = ">>=";
					break;
			}	
			return retOp;	
		}		
		#endregion		
	}
		#endregion
		#region  UnaryExpr
	/// <summary>
	///  A unary expression.
	/// </summary>
	public class UnaryExpr : Expression  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.UnaryExpr;}}
		#region  Right  
		private Expression p_right;
		/// <summary>
		/// Gets or sets the right (and only) side of the unary expression.
		/// </summary>
		[CodeElement]
		public Expression				Right					
		{
			get
			{
				return p_right;
			}
			set
			{
				p_right = value;
			}
		}	
	#endregion	
		#region  Operator  
		private UnaryOperator p_operator = UnaryOperator.Empty;
		/// <summary>
		/// Gets or sets the unary operator.
		/// </summary>
		[CodeElement]
		public UnaryOperator				Operator					
		{
			get
			{
				return p_operator;
			}
			set
			{
				p_operator = value;
			}
		}	
		#endregion
		/// <summary>
		/// A helper method that returns the UnaryOperator from string input. Note: Plus and negation have a 'u' appended to differentiate them from add and subtract.
		/// </summary>
		/// <param name="op">The string to be converted.</param>
		/// <returns>The resulting UnaryOperator.</returns>
		public static UnaryOperator OperatorFromString(string txt)
		{
			UnaryOperator uop = UnaryOperator.Empty;				
			switch (txt)											
			{														
				case "+u" :											
					uop =  UnaryOperator.UnaryPlus;				
					break;											
				case "-u" :											
					uop =  UnaryOperator.UnaryNegation;
					break;
				case "!" :
					uop =  UnaryOperator.Not;
					break;
				case "~" :
					uop =  UnaryOperator.OnesComplement;
					break;
				case "*" :
					uop =  UnaryOperator.Pointer;
					break;
				case "++" :
					uop =  UnaryOperator.Increment;
					break;
				case "--" :
					uop =  UnaryOperator.Decrement;
					break;
			}
			return uop;
		}
		/// <summary>
		/// A helper method that returns a string (that would be used in C# code) from a UnaryOperator. Note: there is no 'u' appended for return plus and negation operators.
		/// </summary>
		/// <param name="op">The UnaryOperator to be parsed.</param>
		/// <returns>The string representation.</returns>
		public static string StringFromOperator(UnaryOperator uOpEnum)
		{
			string uop = "";				
			switch (uOpEnum)											
			{														
				case UnaryOperator.UnaryPlus  :											
					uop = "+";				
					break;											
				case UnaryOperator.UnaryNegation  :										
					uop = "-";
					break;
				case UnaryOperator.Not  :
					uop = "!";
					break;
				case UnaryOperator.OnesComplement  :
					uop = "~";
					break;
				case  UnaryOperator.Pointer :
					uop = "*";
					break;
				case UnaryOperator.Increment  :
					uop = "++";
					break;
				case UnaryOperator.Decrement  :
					uop = "--";
					break;
			}
			return uop;
		}
	}
	#endregion
		#region  BinaryExpr  
	/// <summary>
	/// A binary expression.
	/// </summary>
	public class BinaryExpr : Expression  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.BinaryExpr;}}

		#region  Left  
		private Expression p_left;
		/// <summary>
		/// Gets or sets the left side of the binary expression.
		/// </summary>
		[CodeElement]
		public Expression				Left					
		{
			get
			{
				return p_left;
			}
			set
			{
				p_left = value;
			}
		}	
	#endregion	
		#region  Operator  
		private BinaryOperator p_operator = BinaryOperator.Empty;
		/// <summary>
		/// Gets or sets the binary operator.
		/// </summary>
		[CodeElement]
		public BinaryOperator				Operator					
		{
			get
			{
				return p_operator;
			}
			set
			{
				p_operator = value;
			}
		}	
	#endregion	
		#region  Right  
		private Expression p_right; 
		/// <summary>
		/// Gets or sets the right side of the binary expression.
		/// </summary>
		[CodeElement]
		public Expression				Right					
		{
			get
			{
				return p_right;
			}
			set
			{
				p_right = value;
			}
		}	
		#endregion

		/// <summary>
		/// A helper method that returns the BinaryOperator from string input. 
		/// </summary>
		/// <param name="op">The string to be converted.</param>
		/// <returns>The resulting BinaryOperator.</returns>
		public static BinaryOperator OperatorFromString(string txt)
		{
			BinaryOperator bop = BinaryOperator.Empty;
			switch (txt)
			{
				case "||" :
					bop =  BinaryOperator.BooleanOr;
					break;
				case "&&" :
					bop =  BinaryOperator.BooleanAnd;
					break;
				case "|" :
					bop =  BinaryOperator.BitwiseOr;
					break;
				case "&" :
					bop =  BinaryOperator.BitwiseAnd;
					break;
				case "^" :
					bop =  BinaryOperator.BitwiseXor;
					break;
				case "==" :
					bop =  BinaryOperator.IdentityEquality;
					break;
				case "!=" :
					bop =  BinaryOperator.IdentityInequality;
					break;
				case ">=" :
					bop =  BinaryOperator.GreaterThanOrEqual;
					break;
				case "<=" :
					bop =  BinaryOperator.LessThanOrEqual;
					break;
				case ">" :
					bop =  BinaryOperator.GreaterThan;
					break;
				case "<" :
					bop =  BinaryOperator.LessThan;
					break;
				case "-" :
					bop =  BinaryOperator.Subtract;
					break;
				case "+" :
					bop =  BinaryOperator.Add;
					break;
				case "%" :
					bop =  BinaryOperator.Modulus;
					break;
				case "/" :
					bop =  BinaryOperator.Divide;
					break;
				case "*" :
					bop =  BinaryOperator.Multiply;
					break;
				case "as" :
					bop =  BinaryOperator.As;
					break;
				case "is" :
					bop =  BinaryOperator.Is;
					break;
				case ">>" :
					bop =  BinaryOperator.ShiftRight;
					break;
				case "<<" :
					bop =  BinaryOperator.ShiftLeft;
					break;			
			}
			return bop;
		}
		/// <summary>
		/// A helper method that returns a string (that would be used in C# code) from a BinaryOperator. 
		/// </summary>
		/// <param name="op">The BinaryOperator to be parsed.</param>
		/// <returns>The string representation.</returns>
		public static string StringFromOperator(BinaryOperator bopEnum)
		{
			string bop = "";
			switch (bopEnum)
			{
				case BinaryOperator.BooleanOr  :
					bop = "||";
					break;
				case BinaryOperator.BooleanAnd  :
					bop = "&&";
					break;
				case  BinaryOperator.BitwiseOr :
					bop = "|";
					break;
				case  BinaryOperator.BitwiseAnd :
					bop = "&";
					break;
				case BinaryOperator.BitwiseXor  :
					bop = "^";
					break;
				case BinaryOperator.IdentityEquality  :
					bop = "==";
					break;
				case BinaryOperator.IdentityInequality  :
					bop = "!=";
					break;
				case BinaryOperator.GreaterThanOrEqual  :
					bop = ">=";
					break;
				case BinaryOperator.LessThanOrEqual  :
					bop = "<=";
					break;
				case BinaryOperator.GreaterThan  :
					bop = ">";
					break;
				case BinaryOperator.LessThan  :
					bop = "<";
					break;
				case BinaryOperator.Subtract  :
					bop = "-";
					break;
				case BinaryOperator.Add  :
					bop = "+";
					break;
				case BinaryOperator.Modulus  :
					bop = "%";
					break;
				case BinaryOperator.Divide  :
					bop = "/";
					break;
				case BinaryOperator.Multiply  :
					bop = "*";
					break;
				case BinaryOperator.As  :
					bop = "as";
					break;
				case BinaryOperator.Is  :
					bop = "is";
					break;
				case BinaryOperator.ShiftRight  :
					bop = ">>";
					break;
				case BinaryOperator.ShiftLeft  :
					bop = "<<";
					break;			
			}
			return bop;
		}
	}
	#endregion
		#region  TernaryExpr  
	/// <summary>
	/// A ternary (conditional) expression (bool ? expr : expr).
	/// </summary>
	public class TernaryExpr : Expression  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.TernaryExpr;}}
		#region  Test  
		private Expression p_test;
		/// <summary>
		/// Gets or sets the expression to test.
		/// </summary>
		[CodeElement]
		public Expression				Test					
		{
			get
			{
				return p_test;
			}
			set
			{
				p_test = value;
			}
		}	
		#endregion	
		#region  True  
		private Expression p_true;
		/// <summary>
		/// Gets or sets the expression assign if the test resolves to 'true'.
		/// </summary>
		[CodeElement]
		public Expression				True					
		{
			get
			{
				return p_true;
			}
			set
			{
				p_true = value;
			}
		}	
	#endregion	
		#region  False  
		private Expression p_false;
		/// <summary>
		/// Gets or sets the expression assign if the test resolves to 'false'.
		/// </summary>
		[CodeElement]
		public Expression				False					
		{
			get
			{
				return p_false;
			}
			set
			{
				p_false = value;
			}
		}	
	#endregion
	}
	#endregion
		#region  CastExpr  
	/// <summary>
	/// A cast expression.
	/// </summary>
	public class CastExpr : Expression  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.CastExpr;}}

			#region  Expression  
		private Expression p_expression;
		/// <summary>
		/// Gets or sets the expression to cast to a new type.
		/// </summary>
		[CodeElement]
		public Expression				Expression					
		{
			get
			{
				return p_expression;
			}
			set
			{
				p_expression = value;
			}
		}	
	#endregion	
			#region  TargetType  
		private TypeRef p_targetType;
		/// <summary>
		/// Gets or sets the type to cast the expression to.
		/// </summary>
		[CodeElement]
		public TypeRef				TargetType					
		{
			get
			{
				return p_targetType;
			}
			set
			{
				p_targetType = value;
			}
		}	
		#endregion

		#region ResultType
		/// <exclude/>
		public virtual IDeclaration ResultType
		{
			get
			{
				return TargetType;
			}
		}
		#endregion
	}
		#endregion
		#region  SubExpr  
	/// <summary>
	/// A sub (parenthesized) expression. This overrides regular operator precedence eg: (2+4)*6.
	/// </summary>
	public class SubExpr : Expression  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.SubExpr;}}
			#region  Expression  
		private Expression p_expression;
		/// <summary>
		/// The expression to be grouped.
		/// </summary>
		[CodeElement]
		public Expression				Expression					
		{
			get
			{
				return p_expression;
			}
			set
			{
				p_expression = value;
			}
		}	
	#endregion	
	}
		#endregion

		#region  Reference  
	/// <summary>
	/// A variable.
	/// </summary>
	public abstract class Reference : Expression, ISymbolRef  
	{
		#region  Definition 
		private IDefinition p_definition; 
		/// <summary>
		/// Gets or sets the definition for the reference symbol (long x = (int)5; --> x).
		/// </summary>
		/// <exclude/>
		public IDefinition				Definition					
		{
			get
			{
				return p_definition;
			}
			set
			{
				p_definition = value;
			}
		}	
		#endregion	
		#region  DeclaredType 
		private IDeclaration p_declType; 
		/// <summary>
		/// Gets or sets the declared type of the symbol (long x = (int)5; --> long).
		/// </summary>
		/// <exclude/>
		public IDeclaration				DeclaredType					
		{
			get
			{
				return p_declType;
			}
			set
			{
				p_declType = value;
			}
		}	
		#endregion	
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				return this.GetType().Name;
			}
		}
		#endregion
	}
		#endregion
			#region  UnknownReference  
	/// <summary>
	/// A variable who's type has not yet been determined.
	/// </summary>
	public class UnknownReference : Reference  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.UnknownReference;}}
		#region  VariableName  
		private string p_variableName = "";
		/// <summary>
		/// Gets or sets the name of the variable.
		/// </summary>
		[CodeElement]
		public string				VariableName					
		{
			get
			{
				if(Definition == null || Definition.Name == "")
					return p_variableName;
				else return Definition.Name;
			}
			set
			{
				p_variableName = value;
			}
		}	
		#endregion
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				string s = "var: ";				
				if(Definition != null) s += Definition.ToString();
				else s += VariableName;
				return s;
			}
		}
		#endregion
	}
	#endregion
			#region  ThisRef  
	/// <summary>
	/// A 'this' reference.
	/// </summary>
	public class ThisRef : Reference  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.ThisRef;}}
		// scope
			#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				return "this";
			}
		}
		#endregion
	}
		#endregion
			#region  BaseRef  
	/// <summary>
	/// A 'base' reference.
	/// </summary>
	public class BaseRef : Reference  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.BaseRef;}}
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				return "base";
			}
		}
		#endregion
	}
		#endregion
			#region  PropertySetValueRef 
	/// <summary>
	/// A 'value' reference, used inside a set accessor.
	/// </summary>
	public class PropertySetValueRef : Reference  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.PropertySetValueRef;}}
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				return "value";
			}
		}
		#endregion
	}
	#endregion
			#region  ArgumentRef 
	/// <summary>
	/// An argument reference.
	/// </summary>
	public class ArgumentRef : Reference  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.ArgumentRef;}}
		#region  ParameterName  
		private string p_parameterName = "";
		/// <summary>
		/// Gets or sets the name of the argument.
		/// </summary>
		[CodeElement]
		public string				ParameterName					
		{
			get
			{
				if(Definition == null || Definition.Name == "")
					return p_parameterName;
				else return Definition.Name;
			}
			set
			{
				p_parameterName = value;
			}
		}	
		#endregion
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				string s = "Arg: ";				
				if(Definition != null) s += Definition.ToString();
				else s += ParameterName;
				return s;
			}
		}
		#endregion
	}
	#endregion
			#region  LocalRef  
	/// <summary>
	/// A local reference.
	/// </summary>
	public class LocalRef : Reference  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.LocalRef;}}
		#region  VariableName  
		private string p_variableName = "";
		/// <summary>
		/// Gets or sets the name of the variable.
		/// </summary>
		[CodeElement]
		public string				VariableName					
		{
			get
			{
				if(Definition == null || Definition.Name == "")
					return p_variableName;
				else return Definition.Name;
			}
			set
			{
				p_variableName = value;
			}
		}	
		#endregion
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				string s = "local: ";				
				if(Definition != null) s += Definition.ToString();
				else s += VariableName;
				return s;
			}
		}
		#endregion
	}
	#endregion
	#region  BuiltInType  
	/// <summary>
	/// A C# built in type.
	/// </summary>
	public class BuiltInType : Reference  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.BuiltInType;}}
		#region  TypeName  
		private string p_name;
		/// <summary>
		/// Gets or sets the name of the built in type.
		/// </summary>
		[CodeElement]
		public string				TypeName					
		{
			get
			{
				if(Definition == null || Definition.Name == "")
					return p_name;
				else return Definition.Name;
			}
			set
			{
				if(this.Definition != null) 
					this.Definition.Name = value;
				else
					p_name = value;
			}
		}	
		#endregion
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				if(Definition != null) return Definition.ToString();
				return TypeName;
			}
		}
		#endregion
	}
	#endregion

			#region  TypeOfExpr  
	/// <summary>
	/// A 'typeof' expression.
	/// </summary>
	public class TypeOfExpr : Expression  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.TypeOfExpr;}}
			#region  Type  
		private TypeRef p_type;
		/// <summary>
		/// Gets or sets the object who's type will be looked up.
		/// </summary>
		[CodeElement]
		public TypeRef				Type					
		{
			get
			{
				return p_type;
			}
			set
			{
				p_type = value;
			}
		}	
	#endregion
	}
		#endregion
	// checkedExpr
	// uncheckedExpr

	#region  TypeRef  
	/// <summary>
	/// A reference to a type (interface, class, struct, enum, delegate - this includes built in types).
	/// </summary>
	public class TypeRef : CSharpGraph, ISymbolRef  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.TypeRef;}}
		#region  TypeName  
		private string p_typeName = "";
		/// <summary>
		/// Gets or sets the name of the reference. If the Definition is not null and its Name is set it will get the name from there, otherwise it will store it internally as a string.
		/// </summary>
		[CodeElement]
		public string				TypeName					
		{
			get
			{
				if(p_definition == null || p_definition.Name == "") 
					return p_typeName;
				else 
					return p_definition.Name;
			}
			set
			{
				//if(p_definition != null && p_typeName != value)
				//	p_definition = p_definition.Scope.Lookup(value);
				p_typeName = value;
			}
		}	
	#endregion
		#region  ArrayElementType  
		private TypeRef p_arrayElementType =null;
		/// <summary>
		/// Gets or sets the base element type if this is an array reference.
		/// </summary>
		[CodeElement]
		public TypeRef				ArrayElementType					
		{
			get
			{
				return p_arrayElementType;
			}
			set
			{
				p_arrayElementType = value;
			}
		}	
	#endregion	
		#region  ArrayRanks  
		private  RankSpecifierCollection p_arrayRanks = new RankSpecifierCollection();
		/// <summary>
		/// Gets or sets the rank specifiers if this is an array reference. Rank specifiers are the [][] blocks in jagged arrays.
		/// </summary>
		[CodeElement]
		public RankSpecifierCollection		ArrayRanks					
		{
			get
			{
				return p_arrayRanks;
			}
			set
			{
				p_arrayRanks = value;
			}
		}	
		#endregion	
		#region Definition
		private IDefinition p_definition = new Definition();
		/// <summary>
		/// Gets or sets the definition of this type from the scope table.
		/// </summary>
		public IDefinition Definition
		{
			get
			{
				return p_definition;
			}
			set
			{
				p_definition = value;
			}
		}
		#endregion
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				string s = TypeName;
				if(Definition != null)
					s = Definition.ToString();
				if(ArrayRanks == null) return s;
				for(int i = 0; i < ArrayRanks.Count; i++)
				{
					s += "[";
					for(int j = 1; j < ArrayRanks[i].Dimensions; j++)
					{
						s += ",";
					}
					s += "]";
				}
				return s;
			}
		}
		#endregion
	}
	#endregion

	// has target expressions	
			#region  FieldRef  
	/// <summary>
	/// A reference to a field.
	/// </summary>
	public class FieldRef : Reference  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.FieldRef;}}
		#region  FieldName  
		private string p_fieldName = "";
		/// <summary>
		/// Gets or sets the name of the field.
		/// </summary>
		[CodeElement]
		public string				FieldName					
		{
			get
			{
				if(Definition == null || Definition.Name == "")
					return p_fieldName;
				else return Definition.Name;
			}
			set
			{
				p_fieldName = value;
			}
		}	
	#endregion	
		#region  Target  
		private Expression p_target;
		/// <summary>
		/// Gets or sets the target object to which the field belongs.
		/// </summary>
		[CodeElement]
		public Expression				Target					
		{
			get
			{
				return p_target;
			}
			set
			{
				p_target = value;
			}
		}	
	#endregion
	}
	#endregion
			#region  ArrayElementRef 
	/// <summary>
	/// A reference to a specific element of an array.
	/// </summary>
	public class ArrayElementRef : Reference  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.ArrayElementRef;}}
			#region  Target  
		private Expression p_target;
		/// <summary>
		/// Gets or sets the target array object to which the element belongs.
		/// </summary>
		[CodeElement]
		public Expression				Target					
		{
			get
			{
				return p_target;
			}
			set
			{
				p_target = value;
			}
		}	
	#endregion	
			#region  Indices  
		private ExpressionCollection p_indices = new ExpressionCollection();
		/// <summary>
		/// Gets a collection of indexes that navigate to the element. An array may be multi-dimensional ([,,]) so this must be a collection. Index zero represents the first dimension.
		/// </summary>
		[CodeCollection]
		public ExpressionCollection				Indices					
		{
			get
			{
				return p_indices;
			}
		}	
	#endregion
	}
	#endregion 
			#region  MethodRef
	/// <summary>
	/// A reference to a method.
	/// </summary>
	public class MethodRef : Reference  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.MethodRef;}}
		
		#region  MethodName  
		private string p_methodName = "";
		/// <summary>
		/// Gets or sets the name of the method.
		/// </summary>
		[CodeElement]
		public string				MethodName					
		{
			get
			{
				if(Definition == null || Definition.Name == "")
					return p_methodName;
				else return Definition.Name;
			}
			set
			{
				p_methodName = value;
			}
		}	
	#endregion	
		#region  Target  
		private Expression p_target;
		/// <summary>
		/// Gets or sets the target object to which the method belongs.
		/// </summary>
		[CodeElement]
		public Expression				Target					
		{
			get
			{
				return p_target;
			}
			set
			{
				p_target = value;
			}
		}	
		#endregion	
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				string s = this.GetType().Name;
				if(Definition != null) s += " - " + Definition.ToString();
				return s; 
			}
		}
		#endregion
	}
	#endregion
			#region  PropertyRef  
	/// <summary>
	/// A reference to a property.
	/// </summary>
	public class PropertyRef : Reference  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.PropertyRef;}}
		#region  PropertyName  
		private string p_propertyName = "";
		/// <summary>
		/// Gets or sets the name of the property.
		/// </summary>
		[CodeElement]
		public string				PropertyName					
		{
			get
			{
				if(Definition == null || Definition.Name == "")
					return p_propertyName;
				else return Definition.Name;
			}
			set
			{
				p_propertyName = value;
			}
		}	
	#endregion	
		#region  Target  
		private Expression p_target; 
		/// <summary>
		/// Gets or sets the target object to which the property belongs.
		/// </summary>
		[CodeElement]
		public Expression				Target					
		{
			get
			{
				return p_target;
			}
			set
			{
				p_target = value;
			}
		}	
	#endregion	
	}
	#endregion
			#region  EventRef  
	/// <summary>
	/// A reference to an event.
	/// </summary>
	public class EventRef : Reference  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.EventRef;}}
		#region  EventName  
		private string p_eventName = "";
		/// <summary>
		/// Gets or sets the name of the event.
		/// </summary>
		[CodeElement]
		public string				EventName					
		{
			get
			{
				if(Definition == null || Definition.Name == "")
					return p_eventName;
				else return Definition.Name;
			}
			set
			{
				p_eventName = value;
			}
		}	
	#endregion	
		#region  Target  
		private Expression p_target; 
		/// <summary>
		/// Gets or sets the target object to which the event belongs.
		/// </summary>
		[CodeElement]
		public Expression				Target					
		{
			get
			{
				return p_target;
			}
			set
			{
				p_target = value;
			}
		}	
	#endregion
	}
	#endregion		
			#region  IndexerRef  
	/// <summary>
	/// A reference to a class indexer.
	/// </summary>
	public class IndexerRef : Reference  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.IndexerRef;}}

		#region  Target  
		private Expression p_target;
		/// <summary>
		/// Gets or sets a reference to the type of the indexer.
		/// </summary>
		[CodeElement]
		public Expression				Target					
		{
			get
			{
				return p_target;
			}
			set
			{
				p_target = value;
			}
		}	
	#endregion	
		#region  Indices  
		private ExpressionCollection p_indices = new ExpressionCollection();
		/// <summary>
		/// Gets a collection of indexes that navigate to the indexer element. An indexer may be multi-dimensional ([,,]) so this must be a collection. Index zero represents the first dimension.
		/// </summary>
		[CodeCollection]
		public ExpressionCollection				Indices					
		{
			get
			{
				return p_indices;
			}
		}	
	#endregion

	}
	#endregion
	// follow (has target) expresions
			#region  MethodInvokeExpr 
	/// <summary>
	/// Invokes a method with the given parameters.
	/// </summary>
	public class MethodInvokeExpr : Expression  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.MethodInvokeExpr;}}
		// Note: may need to be delegate invoke after type attribution
			#region  Target  
		private MethodRef p_target;
		/// <summary>
		/// Gets or sets a reference to the method being invoked.
		/// </summary>
		[CodeElement]
		public MethodRef				Target					
		{
			get
			{
				return p_target;
			}
			set
			{
				p_target = value;
			}
		}	
	#endregion	
			#region  Parameters  
		private ParamCollection p_parameters = new ParamCollection();
		/// <summary>
		/// Gets the collection of parameters to be passed to the invoked method.
		/// </summary>
		[CodeCollection]
		public ParamCollection				Parameters					
		{
			get
			{
				return p_parameters;
			}
		}	
		#endregion
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				string s = this.GetType().Name;
				if(Target.Definition != null) s += " - " + Target.Definition.ToString();
				return s; 
			}
		}
		#endregion
	}
		#endregion
			#region  PostfixExpr  
	/// <summary>
	/// A postfix expression (x++, y--).
	/// </summary>
	public class PostfixExpr : Expression  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.PostfixExpr;}}
			#region  Left  
		private Expression p_left; 
		/// <summary>
		/// Gets or sets the left side of the expression that the postfix affects.
		/// </summary>
		[CodeElement]
		public Expression				Left					
		{
			get
			{
				return p_left;
			}
			set
			{
				p_left = value;
			}
		}	
	#endregion	
			#region  Operator  
		private PostfixOperator p_operator = PostfixOperator.Empty;
		/// <summary>
		/// Gets or sets the postfix operator (which can be either ++ or --).
		/// </summary>
		[CodeElement]
		public PostfixOperator				Operator					
		{
			get
			{
				return p_operator;
			}
			set
			{
				p_operator = value;
			}
		}	
	#endregion


	}
		#endregion
			#region  DelegateInvokeExpr  
	/// <summary>
	/// Invokes a delegate.
	/// </summary>
	public class DelegateInvokeExpr : Expression  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.DelegateInvokeExpr;}}
			#region  Target  
		private Expression p_target;
		/// <summary>
		/// Gets or sets a reference to the delegate being invoked.
		/// </summary>
		[CodeElement]
		public Expression				Target					
		{
			get
			{
				return p_target;
			}
			set
			{
				p_target = value;
			}
		}	
	#endregion
			#region  Parameters  
		private ParamCollection p_parameters = new ParamCollection();
		/// <summary>
		/// Gets the collection of parameters to be passed to the invoked method.
		/// </summary>
		[CodeCollection]
		public ParamCollection				Parameters					
		{
			get
			{
				return p_parameters;
			}
		}	
	#endregion	

	}
		#endregion
	// postfix
			#region  MemberAccess 
	/// <summary>
	/// An expression that accesses a member of a variable (x.y).
	/// </summary>
	public class MemberAccess : Reference  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.MemberAccess;}}
		#region  MemberName  
		private string p_memberName = "";
		/// <summary>
		/// Gets or sets the name of the member to access.
		/// </summary>
		[CodeElement]
		public string				MemberName					
		{
			get
			{
				if(Definition == null || Definition.Name == "")
					return p_memberName;
				else return Definition.Name;
			}
			set
			{
				p_memberName = value;
			}
		}	
	#endregion	
		#region  Target  
		private Expression p_target;
		/// <summary>
		/// Gets or sets the target object to which the member being accessed belongs.
		/// </summary>
		[CodeElement]
		public Expression				Target					
		{
			get
			{
				return p_target;
			}
			set
			{
				p_target = value;
			}
		}	
	#endregion
	}
	#endregion

	// ident 
		#region  ArrayCreateExpr  
	/// <summary>
	/// An expression that creates a new array.
	/// </summary>
	public class ArrayCreateExpr : Expression  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.ArrayCreateExpr;}}
			#region  CreateType  
		private TypeRef p_createType;
		/// <summary>
		/// Gets or sets the type of the elements that the array being created stores.
		/// </summary>
		[CodeElement]
		public TypeRef				CreateType					
		{
			get
			{
				return p_createType;
			}
			set
			{
				p_createType = value;
			}
		}	
	#endregion	
			#region  Initializer  
		private ArrayInitializer p_initializer;
		/// <summary>
		/// Gets or sets the objects the array is initialized with (the {1,2,3} part).
		/// </summary>
		[CodeElement]
		public ArrayInitializer			Initializer		
		{
			get
			{
				return p_initializer;
			}
			set
			{
				p_initializer = value;
			}
		}	
	#endregion	
			#region  Sizes  
		private ExpressionCollection p_sizes = new ExpressionCollection();
		/// <summary>
		/// Gets a collection of sizes of each dimension in the array (eg. int[2,3]).
		/// </summary>
		[CodeCollection]
		public ExpressionCollection			Sizes					
		{
			get
			{
				return p_sizes;
			}
		}	
	#endregion
			#region  DimensionCount  
		private int p_dimensions = 1;
		/// <summary>
		/// Gets or sets the number of dimensions in the array (where [,,] has three dimensions). The default value is 1 which represents 'normal' arrays (int[]).
		/// </summary>
		[CodeElement]
		public int				DimensionCount				
		{
			get
			{
				return p_dimensions;
			}
			set
			{
				p_dimensions = value;
			}
		}	
	#endregion
			#region  RankSpecifiers  
		private RankSpecifierCollection p_ranks = new RankSpecifierCollection();
		/// <summary>
		/// Gets a collection of rank specifiers for the array. Rank specifiers represent each bracketed section of an array ([,,][,] indicates two rank specifiers). Only the first rank specifier can hold size information, so there is no need for a collection of rank specifier sizes. 
		/// Note: the .net documentation can be confusing on these terms, as it uses 'rank' and 'dimensions' interchangeably, and 'jagged arrays' for these types of arrays of arrays. This is fine, but leaves us with 'a jag' to refer to a rank specifier. The term 'RankSpecifiers' is thus derived from the C# spec instead.
		/// </summary>
		[CodeCollection]
		public RankSpecifierCollection			RankSpecifiers					
		{
			get
			{
				return p_ranks;
			}
		}	
	#endregion
	}
		#endregion
		#region  ObjectCreateExpr
	/// <summary>
	/// An expression that creates a new object.
	/// </summary>
	public class ObjectCreateExpr : Expression  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.ObjectCreateExpr;}}
		// Note: Delegate and Object Create are the same until attribution
			#region  CreateType  
		private TypeRef p_createType;
		/// <summary>
		/// Gets or sets the type of object to be created.
		/// </summary>
		[CodeElement]
		public TypeRef				CreateType					
		{
			get
			{
				return p_createType;
			}
			set
			{
				p_createType = value;
			}
		}	
		#endregion	
			#region  Parameters  
		private ParamCollection p_parameters = new ParamCollection();
		/// <summary>
		/// Gets the collection of parameters to be passed to the object being created.
		/// </summary>
		[CodeCollection]
		public ParamCollection				Parameters					
		{
			get
			{
				return p_parameters;
			}
		}	
		#endregion
			#region  ConstructorDefinition  
		private IDefinition p_constructorDef = null;
		/// <summary>
		/// Gets or sets the matching constructor (in case of overload).
		/// </summary>
		[CodeElement]
		public IDefinition				ConstructorDefinition					
		{
			get
			{
				return p_constructorDef;
			}
			set
			{
				p_constructorDef = value;
			}
		}	
		#endregion
	}
		#endregion
		#region  CreateDelegateExpr 
	/// <summary>
	/// An expression that creates a new delegate.
	/// </summary>
	public class CreateDelegateExpr : Expression  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.CreateDelegateExpr;}}
			#region  MethodName  
		private string p_methodName = "";
		/// <summary>
		/// Gets or sets the name of the method to 'delegate' to.
		/// </summary>
		[CodeElement]
		public string				MethodName					
		{
			get
			{
				return p_methodName;
			}
			set
			{
				p_methodName = value;
			}
		}	
	#endregion	
			#region  Target  
		private Expression p_target; 
		/// <summary>
		/// Gets or sets the target object to which the delegate method belongs.
		/// </summary>
		[CodeElement]
		public Expression				Target					
		{
			get
			{
				return p_target;
			}
			set
			{
				p_target = value;
			}
		}	
	#endregion	
			#region  Type  
		private TypeRef p_type;
		/// <summary>
		/// Gets or sets the return type of the delegate. This may be 'void'.
		/// </summary>
		[CodeElement]
		public TypeRef				Type					
		{
			get
			{
				return p_type;
			}
			set
			{
				p_type = value;
			}
		}	
		#endregion
	}
		#endregion

	#region  PrimitiveExpr  
	/// <summary>
	/// An abstract base class for primative types.
	/// </summary>
	public abstract class PrimitiveExpr : Expression  
	{
	}
		#endregion
		#region	BooleanLiteral 
	/// <summary>
	/// A boolean literal.
	/// </summary>
	public class				BooleanLiteral : PrimitiveExpr
	{	
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.BooleanLiteral;}}
		private bool p_value;
		/// <summary>
		/// Gets or sets the value of this literal. Must be 'true' or 'false'.
		/// </summary>
		[CodeElement]
		public bool Value
		{
			get
			{
				return p_value;
			}
			set
			{
				p_value = value;
			}
		}
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				return "Bool: " + Value.ToString();
			}
		}
		#endregion
	}
	#endregion 
		#region	IntegerLiteral 
	/// <summary>
	/// An interger literal.
	/// </summary>
	public class				IntegerLiteral : PrimitiveExpr
	{	
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.IntegerLiteral;}}
		private int p_value;
		/// <summary>
		/// Gets or sets the numeric value of this integer literal. 
		/// </summary>
		[CodeElement]
		public int Value
		{
			get
			{
				return p_value;
			}
			set
			{
				p_value = value;
			}
		}
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				return Value.ToString();
			}
		}
		#endregion
	}
	#endregion 
		#region	RealLiteral 
	/// <summary>
	/// An real literal. Will be a floating point number.
	/// </summary>
	public class				RealLiteral : PrimitiveExpr
	{	
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.RealLiteral;}}
		private double p_value;
		/// <summary>
		/// Gets or sets the floating point value of this real literal.
		/// </summary>
		[CodeElement]
		public double Value
		{
			get
			{
				return p_value;
			}
			set
			{
				p_value = value;
			}
		}
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				return Value.ToString();
			}
		}
		#endregion
	}
	#endregion 
		#region	CharLiteral 
	/// <summary>
	/// A character literal.
	/// </summary>
	public class				CharLiteral : PrimitiveExpr
	{	
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.CharLiteral;}}
		private char p_value;
		/// <summary>
		/// Gets or sets the 'char' value of this char literal.
		/// </summary>
		[CodeElement]
		public char Value
		{
			get
			{
				return p_value;
			}
			set
			{
				p_value = value;
			}
		}
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				return Value.ToString();
			}
		}
		#endregion
	}
	#endregion 
		#region	StringLiteral 
	/// <summary>
	/// A string literal.
	/// </summary>
	public class				StringLiteral : PrimitiveExpr
	{	
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.StringLiteral;}}
		private string p_value;
		/// <summary>
		/// Gets or sets the string value of this string literal.
		/// </summary>
		[CodeElement]
		public string Value
		{
			get
			{
				return p_value;
			}
			set
			{
				p_value = value;
			}
		}
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				return Value.ToString();
			}
		}
		#endregion
	}
	#endregion 	
		#region	NullLiteral 
	/// <summary>
	/// A null literal.
	/// </summary>
	public class				NullLiteral : PrimitiveExpr
	{	
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.NullLiteral;}}
		/// <summary>
		/// Gets or sets a null value. Always will be null.
		/// </summary>
		[CodeElement]
		public string Value
		{
			get
			{
				return null;
			}
			set
			{
			}
		}
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				return "null";
			}
		}
		#endregion
	}
	#endregion 

	#region  ArrayInitializer  
	/// <summary>
	/// An expression that initializes a new array being created (the {1,2,3} part).
	/// </summary>
	public class ArrayInitializer : Expression  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.ArrayInitializer;}}
		#region  InitialValues  
		private ExpressionCollection p_initialvalues = new ExpressionCollection();
		/// <summary>
		/// Gets a collection of inital values to give teh array being created.
		/// </summary>
		[CodeCollection]
		public ExpressionCollection			InitialValues					
		{
			get
			{
				return p_initialvalues;
			}
		}	
		#endregion	
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				return this.GetType().Name;
			}
		}
		#endregion
	}
		#endregion


	#region  CustomAttribute  
	/// <summary>
	/// An single attribute applied to a code element (eg. [Flags]). Note: multiple comma separated attributes declared in a single attribute element are separated into multiple attributes. 
	/// </summary>
	public class CustomAttribute : CSharpGraph  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.CustomAttribute;}}

		#region  Name 
		private string p_name = ""; 
		/// <summary>
		/// Gets or sets the name of the attribute.
		/// </summary>
		[CodeElement]
		public string				Name					
		{
			get
			{
				return p_name;
			}
			set
			{
				p_name = value;
			}
		}	
	#endregion	
		#region  AttributeTarget 
		private AttributeTarget p_target = AttributeTarget.Empty;
		/// <summary>
		/// Gets or sets the target element that the attribute applies to.
		/// </summary>
		[CodeElement]
		public AttributeTarget			AttributeTarget
		{
			get
			{
				return p_target;
			}
			set
			{
				p_target = value;
			}
		}	
	#endregion	
		#region  Parameters  
		private ExpressionCollection p_parameters = new ExpressionCollection();
		/// <summary>
		/// Gets the collection of parameters to be passed to the custom attribute.
		/// </summary>
		[CodeCollection]
		public ExpressionCollection				Parameters					
		{
			get
			{
				return p_parameters;
			}
		}	
	#endregion	

		public static string StringFromAttributeTarget(AttributeTarget at)
		{
			string s = "";
			switch(at)
			{
				case AttributeTarget.Assembly :
				{
					s = "assembly";
					break;
				}
				case AttributeTarget.Field :
				{
					s = "field";
					break;
				}
				case AttributeTarget.Event :
				{
					s = "event";
					break;
				}
				case AttributeTarget.Method :
				{
					s = "method";
					break;
				}
				case AttributeTarget.Module :
				{
					s = "module";
					break;
				}
				case AttributeTarget.Parameter :
				{
					s = "param";
					break;
				}
				case AttributeTarget.Property :
				{
					s = "property";
					break;
				}
				case AttributeTarget.Return :
				{
					s = "return";
					break;
				}
				case AttributeTarget.Type :
				{
					s = "type";
					break;
				}
			}
			return s;
		}

		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				return "Custom Attribute: "+Name;
			}
		}
		#endregion
	}
	#endregion
	#region  ParamDecl  
	/// <summary>
	/// A parameter delaration.
	/// </summary>
	public class ParamDecl : CSharpGraph, IDeclaration
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.ParamDecl;}}
		#region  CustomAttributes  
		private CustomAttributeCollection p_customAttributes = 
			new CustomAttributeCollection();
		/// <summary>
		/// Gets the collection of Custom Attributes used for the parameter (custom attributes are the ones declared between brackets, like [Flags]).
		/// </summary>
		[CodeCollection]
		public CustomAttributeCollection				CustomAttributes					
		{
			get
			{
				return p_customAttributes;
			}
			set
			{
				p_customAttributes = value;
			}
		}	
	#endregion	
		#region  Name  
		private string p_name = "";
		/// <summary>
		/// Gets or sets the name of the parameter.
		/// </summary>
		[CodeElement]
		public string				Name					
		{
			get
			{
				if(Definition == null || Definition.Name == "")
					return p_name;
				else return Definition.Name;
			}
			set
			{
				p_name = value;
			}
		}	
	#endregion	
		#region  Direction  
		private ParamDirection p_direction = ParamDirection.Value; // default
		/// <summary>
		/// Gets or sets the direction of the parameter (ref or out).
		/// </summary>
		[CodeElement]
		public ParamDirection				Direction					
		{
			get
			{
				return p_direction;
			}
			set
			{
				p_direction = value;
			}
		}	
	#endregion	
		#region  Type  
		private TypeRef p_type;
		/// <summary>
		/// Gets or sets the type of the parameter.
		/// </summary>
		[CodeElement]
		public TypeRef				Type					
		{
			get
			{
				return p_type;
			}
			set
			{
				p_type = value;
			}
		}	
	#endregion	
		#region  IsParams  
		private bool p_isParams = false;
		/// <summary>
		/// Gets or sets a flag that indicates this parameter is a special 'params' array type.
		/// </summary>
		[CodeElement]
		public bool				IsParams					
		{
			get
			{
				return p_isParams;
			}
			set
			{
				p_isParams = value;
			}
		}	
	#endregion	

		#region Definition
		private IDefinition p_definition = new Definition();
		/// <summary>
		/// Gets the (scoped) definition.
		/// </summary>
		public IDefinition Definition
		{
			get
			{
				return p_definition;
			}
		}
		#endregion
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				if(Definition == null || Definition.Name == "")
					return Name;
				else return Definition.ToString();
			}
		}
		#endregion
	}
	#endregion
	#region  Param 
	/// <summary>
	/// A parameter that will be passed to another code element.
	/// </summary>
	public class Param : CSharpGraph
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.Param;}}
		#region  Value  
		private Expression p_expression;
		/// <summary>
		/// Gets or sets the value of the parameter.
		/// </summary>
		[CodeElement]
		public Expression				Value					
		{
			get
			{
				return p_expression;
			}
			set
			{
				p_expression = value;
			}
		}	
	#endregion	
		#region  Direction  
		private ParamDirection p_direction = ParamDirection.Value; // default
		/// <summary>
		/// Gets or sets the direction of the parameter (ref or out).
		/// </summary>
		[CodeElement]
		public ParamDirection				Direction					
		{
			get
			{
				return p_direction;
			}
			set
			{
				p_direction = value;
			}
		}	
	#endregion	
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				return "Param";
			}
		}
		#endregion
	}
	#endregion
	#region  VariableDecl 
	/// <summary>
	/// A variable declaration.
	/// </summary>
	public class VariableDecl : CSharpGraph  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.VariableDecl;}}
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				return "VarDecl: TODO";
			}
		}
		#endregion
	}
	#endregion
	#region  Comment  
	/// <summary>
	/// A comment that may be mixed in with other elements.
	/// </summary>
	public class Comment : CSharpGraph  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.Comment;}}

		#region  DocComment  
		private bool p_docComment;
		/// <summary>
		/// Set to true if this is a Documentation comment.
		/// </summary>
		[CodeElement]
		public bool				DocComment					
		{
			get
			{
				return p_docComment;
			}
			set
			{
				p_docComment = value;
			}
		}	
	#endregion	
		#region  CommentText  
		private string p_commentText = "";
		/// <summary>
		/// Gets or set the text of the comment.
		/// </summary>
		[CodeElement]
		public string				CommentText					
		{
			get
			{
				return p_commentText;
			}
			set
			{
				p_commentText = value;
			}
		}	
		#endregion
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				return this.GetType().Name;
			}
		}
		#endregion
	}
	#endregion
	#region  AssemblyReference 
	/// <summary>
	/// A reference to an assembly. This is normally something on the command line.
	/// </summary>
	public class AssemblyReference : CSharpGraph  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.AssemblyReference;}}
		#region  Name   
		private string p_name  = "";
		/// <summary>
		/// Gets or set the name of the assembly.
		/// </summary>
		[CodeElement]
		public string				Name 					
		{
			get
			{
				return p_name ;
			}
			set
			{
				p_name  = value;
			}
		}	
	#endregion	
		#region  HintPath  
		private string p_hintPath = "";
		/// <summary>
		/// Gets or set the path used to help find the assembly (works like /lib). 
		/// </summary>
		[CodeElement]
		public string				HintPath					
		{
			get
			{
				return p_hintPath;
			}
			set
			{
				p_hintPath = value;
			}
		}	
	#endregion
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				return this.Name;
			}
		}
		#endregion

	}
	#endregion
	#region  RankSpecifier  
	/// <summary>
	/// A rank specifier for an array. There can be more than one rank specifier in the case of a jagged array (x[][]).
	/// </summary>
	public class RankSpecifier : CSharpGraph  
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.RankSpecifier;}}
		#region  Dimensions  
		private int p_dimensions = 1;
		/// <summary>
		/// Gets or set the number of dimensions in this rank specifier (eg. [,,] has three dimensions).
		/// </summary>
		[CodeElement]
		public int				Dimensions					
		{
			get
			{
				return p_dimensions;
			}
			set
			{
				p_dimensions = value;
			}
		}	
	#endregion	
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				string s = "[";
				for(int i = 1; i < Dimensions; i++)
					s+= ",";
				s += "]";
				return s;
			}
		}
		#endregion
	}
	#endregion
	#region  Declarator  
	/// <summary>
	/// Code elements such as fields or events can declare multiple variables of the same type in one statement (int x=5, y=6, z=7;). Each of these are declarators.
	/// </summary>
	public class Declarator : CSharpGraph, IDeclaration 
	{
		/// <exclude/>
		public override GraphTypes GraphType{get{return GraphTypes.Declarator;}}
		#region Name
		/// <summary>
		/// Gets or sets the name of the declarator.
		/// </summary>
		[CodeElement]
		public string Name
		{
			get
			{
				return Definition.Name;
			}
			set
			{
				p_definition.Name = value;
			}
		}
		#endregion
		#region  InitExpression  
		private Expression p_init;
		/// <summary>
		/// Gets or sets the inital value of the declarator.
		/// </summary>
		[CodeElement]
		public Expression		InitExpression					
		{
			get
			{
				return p_init;
			}
			set
			{
				p_init = value;
			}
		}	
		#endregion	
		#region Definition
		private IDefinition p_definition = new Definition();
		/// <summary>
		/// Gets the (scoped) definition.
		/// </summary>
		public IDefinition Definition
		{
			get
			{
				return p_definition;
			}
		}
		#endregion
		#region Text
		/// <exclude/>
		public override string Text
		{
			get
			{
				return "Declarator - " + Definition.ToString();
			}
		}
		#endregion
	}
	#endregion

	#region LinePragma
	public class						LinePragma
	{
		#region  Line  
		private int p_line = 0;
		public int				Line					
		{
			get
			{
				return p_line;
			}
			set
			{
				p_line = value;
			}
		}	
		#endregion	
		#region  EndLine  
		private int p_endLine = 0;
		public int				EndLine					
		{
			get
			{
				return p_endLine;
			}
			set
			{
				p_endLine = value;
			}
		}	
	#endregion	
		#region  Column  
		private int p_column = 0;
		public int				Column					
		{
			get
			{
				return p_column;
			}
			set
			{
				p_column = value;
			}
		}	
	#endregion	
		#region  EndColumn  
		private int p_endColumn = 0;
		public int				EndColumn					
		{
			get
			{
				return p_endColumn;
			}
			set
			{
				p_endColumn = value;
			}
		}	
	#endregion			
		#region  FileName 
		private string p_fileName = "";
		public string				FileName					
		{
			get
			{
				return p_fileName;
			}
			set
			{
				p_fileName = value;
			}
		}	
		#endregion
		#region ToString
		public override string ToString()
		{
			return "File: " + FileName + "\tLine: " + Line + "\tColumn: " + Column;
		}
		#endregion
	}
	#endregion 

	
	#region Modifiers Enum
	/// <summary>
	/// A bit field enumeration of the modifier attributes that can be applied to type members (eg. public, static, override etc).
	/// </summary>
	[Flags]
	public enum Modifiers : uint
	{
		Empty		= 0,
		New			= 1,
		Public		= 2,
		Protected	= 4,
		Internal	= 8,
		Private		= 16,
		Static		= 32,
		Virtual		= 64,
		Sealed		= 128,
		Override	= 256,
		Abstract	= 512,
		Extern		= 1024,
		Readonly	= 2048,
		Volatile	= 4096
	}
	#endregion
	#region TypeModifiers Enum
	/// <summary>
	/// A bit field enumeration of the modifier attributes that can be applied to types.
	/// </summary>
	[Flags]
	public enum TypeModifiers : int
	{
		Empty			= 0,
		Class			= 1,
		Interface		= 2,
		Abstract		= 4,
		Sealed			= 8,
		Public			= 16,
		NotPublic		= 32,
		NestedPublic	= 64,
		NestedPrivate	= 128
	}
	#endregion
	#region AccessorModifiers Enum
	/// <summary>
	/// An enumeration of possible accessor types (get, set, add, or remove).
	/// </summary>
	public enum AccessorModifiers : int
	{
		Empty			= 0,
		Get				= 1,
		Set				= 2,
		Add				= 3,
		Remove			= 4
	}
	#endregion
	#region AttributeTarget Enum
	/// <summary>
	/// An enumeration of possible targets a custom attribute can apply to.
	/// </summary>
	public enum AttributeTarget : int		
	{
		Empty				,
		Assembly			,
		Module				,
		Class				,
		Struct				,
		Interface			,
		Enum				,
		Delegate			,
		Method				,
		Parameter			,
		Field				,
		PropertyIndexer		,
		PropertyGetAccessor	,
		PropertySetAccessor	,
		EventField			,
		EventProperty		,
		EventAdd			,
		EventRemove			,		
		Event				,
		Property			,
		Return				,
		Type		
	}
	#endregion
	#region OverloadableOperator Enum
	/// <summary>
	/// An enumeration of possible overloadable operators (operators that can be redefined).
	/// </summary>
	public enum OverloadableOperator
	{
		Empty					, // "",
		Implicit				, // "implicit",
		Explicit				, // "explicit",

		UnaryPlus				, // "+u", 
		UnaryNegation			, // "-u", 
		Not						, // "!", 
		OnesComplement			, // "~"
		Increment				, // "++", 
		Decrement				, // "--", 
		True					, // "true",
		False					, // "false",

		Addition				, // "+", 
		Subtraction				, // "-", 
		Multiply				, // "*", 
		Division				, // "/", 
		Modulus					, // "%", 
		BitwiseAnd				, // "&", 
		BitwiseOr				, // "|",  
		ExclusiveOr				, // "^",
		LeftShift				, // "<<", 
		RightShift				, // ">>", 
		Equality				, // "==",
		Inequality				, // "!=",  
		GreaterThan				, // ">", 
		LessThan				, // "<", 
		GreaterThanOrEqual		, // ">=", 
		LessThanOrEqual			 // "<=", 
	}
	#endregion
	#region BinaryOperator Enum
	/// <summary>
	/// An enumeration of possible binary expression operators.
	/// </summary>
	public enum BinaryOperator
	{
		Empty					, // ""		
		BooleanOr				, // "||"	LOR
		BooleanAnd				, // "&&"	LAND
		BitwiseOr				, // "|"	BOR
		BitwiseAnd				, // "&"	BXOR
		BitwiseXor				, // "^"	BAND
		IdentityInequality		, // "=="	NOT_EQUAL
		IdentityEquality		, // "!="	EQUAL
		GreaterThanOrEqual		, // ">="	GE
		LessThanOrEqual			, // "<="	LE
		GreaterThan				, // ">"	GTHAN
		LessThan				, // "<"	LTHAN
		Subtract				, // "-"	MINUS
		Add						, // "+"	PLUS
		Modulus					, // "%"	MOD
		Divide					, // "/"	DIV
		Multiply				, // "*"	STAR

		As						, // "as"	AS
		Is						, // "is"	IS
		ShiftRight				, // ">>"	SR
		ShiftLeft				  // "<<"	SL
	}
	#endregion
	#region UnaryOperator Enum
	/// <summary>
	/// An enumeration of possible unary expression operators.
	/// </summary>
	public enum UnaryOperator
	{
		Empty					, // "",

		UnaryPlus				, // "+u"	PLUS
		UnaryNegation			, // "-u"	MINUS
		Not						, // "!"	LNOT
		OnesComplement			, // "~"	BNOT
		Pointer					, // "*"	STAR
		Increment				, // "++"	INC
		Decrement				  // "--"	DEC
	}
	#endregion
	#region PostfixOperator Enum
	/// <summary>
	/// An enumeration of possible postfix expression operators (++ and --).
	/// </summary>
	public enum PostfixOperator
	{
		Empty					, // "",
		Increment				, // "++"	INC
		Decrement				  // "--"	DEC
	}
	#endregion
	#region AssignOperator Enum
	/// <summary>
	/// An enumeration of possible assign expression operators (=, +=, *=...).
	/// </summary>
	public enum AssignOperator
	{
		Empty					, // "",

		Assign					, // "="	ASSIGN
		AdditionAssign			, // "+="	PLUS_ASN
		SubtractionAssign		, // "-=	MINUS_ASN
		MultiplyAssign			, // "*="	STAR_ASN
		DivisionAssign			, // "/="	DIV_ASN
		ModulusAssign			, // "%="	MOD_ASN
		BitwiseAndAssign		, // "&="	BAND_ASN
		BitwiseOrAssign			, // "|="	BOR_ASN
		ExclusiveOrAssign		, // "~="	BXOR_ASN
		LeftShiftAssign			, // "<<="	SL_ASN
		RightShiftAssign		  // ">>="	SR_ASN
	}
	#endregion
	#region IterationType Enum
	/// <summary>
	/// An enumeration of possible types of iterations (for, do, while, and foreach).
	/// </summary>
	public enum IterationType
	{
		For			,
		Do			,
		While		, 
		ForEach		
	}
	#endregion
	#region ParamDirection Enum
	/// <summary>
	/// An enumeration of possible parameter directions (ref or out).
	/// </summary>
	public enum ParamDirection
	{
		Value		,
		Ref			,
		Out			
	}
	#endregion

	#region TypeKind Enum
	/// <summary>
	/// An enumeration of possible type kinds (class, interface, enum etc).
	/// </summary>
	public enum TypeKind
	{
		Empty		,
		Class		,
		Interface	,
		Struct		,
		Enum		,
		Delegate
	}
	#endregion
	#region MemberKind Enum
	/// <summary>
	/// An enumeration of possible type kinds (class, interface, enum etc).
	/// </summary>
	public enum MemberKind
	{
		Empty		,
		Method		,
		Field		,
		Property	,
		Event		,
		Constant	,
		Indexer		,
		Operator	,
		Constructor	,
		Destructor	,
		Accessor	,
		EnumMember	,
		Type	
	}
	#endregion

	// attributes
	#region  CodeElement Attribute
	/// <summary>
	/// An attribute that specifies that a CSharp Graph property is a code element as opposed to a helper property.
	/// </summary>
	[AttributeUsage(AttributeTargets.Property)]
	public class CodeElementAttribute : System.Attribute 
	{
	}
	#endregion
	#region  CodeCollection Attribute
	/// <summary>
	/// An attribute that specifies that a CSharp Graph property is a code element (as opposed to a helper property) as well as a collection. 
	/// </summary>
	[AttributeUsage(AttributeTargets.Property)]
	public class CodeCollectionAttribute : CodeElementAttribute 
	{
	}
	#endregion

	// scope interfaces
	#region IScope
	public interface IScope
	{
		Scope Scope{get;set;}
		GraphTypes GraphType{get;}
	}
	#endregion
	#region IDeclaration
	public interface IDeclaration
	{
		IDefinition Definition{get;}
		GraphTypes GraphType{get;}
	}
	#endregion

	#region ISymbolRef
	public interface ISymbolRef : IDeclaration
	{
		new IDefinition Definition{get;set;}
	}
	#endregion

	#region IOverloadable
	public interface IOverloadable
	{
		string HashText{get;} 
		ParamDeclCollection	Parameters{get;}
	}
	#endregion

	#region GraphTypes
	/// <summary>
	/// An enumeration of all CSharpDom types. Used to speed up and simplify reflection (each CSharp type has a 'GraphType' property).
	/// </summary>
	public enum GraphTypes
	{
		CSharpGraph,
		CompileUnit,
		Import,
		NamespaceDecl,
		MemberDecl,
		MethodDecl,
		FieldDecl,
		PropertyDecl,
		EventDecl,
		ConstantDecl,
		IndexerDecl,
		OperatorDecl,
		ConstructorDecl,
		DestructorDecl,
		AccessorDecl,
		EnumMemberDecl,
		TypeDecl,
		ClassDecl,
		InterfaceDecl,
		StructDecl,
		EnumDecl,
		DelegateDecl,
		Statement,
		ExprStmt,
		CommentStmt,
		VariableDeclStmt,
		ConstantDeclStmt,
		IfStmt,
		SwitchStmt,
		Case,
		IterationStmt,
		ForEachStmt,
		GotoStmt,
		LabeledStmt,
		ReturnStmt,
		BreakStmt,
		ContinueStmt,
		CheckedStmt,
		UncheckedStmt,
		LockStmt,
		UsingStmt,
		ThrowStmt,
		TryCatchFinallyStmt,
		Catch,
		AttachDelegateStmt,
		RemoveDelegateStmt,
		Expression,
		AssignExpr,
		UnaryExpr,
		BinaryExpr,
		TernaryExpr,
		CastExpr,
		SubExpr,
		Reference,
		UnknownReference,
		ThisRef,
		BaseRef,
		PropertySetValueRef,
		ArgumentRef,
		LocalRef,
		TypeOfExpr,
		StaticRef,
		FieldRef,
		ArrayElementRef,
		MethodRef,
		PropertyRef,
		EventRef,
		MethodInvokeExpr,
		PostfixExpr,
		DelegateInvokeExpr,
		IndexerRef,
		MemberAccess,
		ArrayCreateExpr,
		ObjectCreateExpr,
		CreateDelegateExpr,
		PrimitiveExpr,
		BooleanLiteral,
		CharLiteral,
		IntegerLiteral,
		NullLiteral,
		RealLiteral,
		StringLiteral,
		ArrayInitializer,
		CustomAttribute,
		TypeRef,
		BuiltInType,
		ParamDecl,
		Param,
		VariableDecl,
		LinePragma,
		Comment,
		AssemblyReference,
		RankSpecifier,
		Declarator,
		CompileUnitCollection,
		ImportCollection,
		NamespaceDeclCollection,
		TypeDeclCollection,
		MemberDeclCollection,
		TypeRefCollection,
		ParamCollection,
		ParamDeclCollection,
		StatementCollection,
		CommentStmtCollection,
		CatchCollection,
		ExpressionCollection,
		CustomAttributeCollection,
		CaseCollection,
		AssemblyReferenceCollection,
		RankSpecifierCollection,
		DeclaratorCollection
	}
		#endregion

}