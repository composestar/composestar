using System;
using System.Collections;
using DDW.CSharp.Dom;
using System.Reflection;

namespace DDW.CSharp.SymbolTable
{
	#region  Scope 
		public abstract class Scope
		{
		#region  EnclosingScope 
		private Scope p_enclosingScope; 
		/// <summary>
		/// Gets or sets the enclosing scope of the scope.
		/// </summary>
		public Scope				EnclosingScope					
		{
			get
			{
				return p_enclosingScope;
			}
			set
			{
				p_enclosingScope = value;
			}
		}	
		#endregion	
		#region  ChildScopes  
		private ScopeCollection p_childScopes = new ScopeCollection();
		public ScopeCollection				ChildScopes					
		{
			get
			{
				return p_childScopes;
			}
			set
			{
				p_childScopes = value;
			}
		}	
	#endregion	
			
		#region  DefinitionTable 
		private Hashtable p_defs = new Hashtable(); 
		/// <summary>
		/// Gets the hashtable that stored the declared symbol definitions.
		/// </summary>
		public Hashtable	DefinitionTable					
		{
			get
			{
				return p_defs;
			}
		}	
		#endregion			
		#region  Add 
		public void Add(IDefinition d, string name)
		{
			DefinitionTable.Add(name, d);
		}
		#endregion
		#region  AddRange 
		public void AddRange(DefinitionCollection dc)
		{
			foreach(Definition d in dc)
			{
				Add(d, d.Name);
			}
		}
		#endregion			

		#region  Lookup 
		public Definition Lookup(string s)
		{
			Definition d = (Definition)DefinitionTable[s];
			if(d != null) 
				return d;
			else if(EnclosingScope != null) 
				return EnclosingScope.Lookup(s);
			else return null;
		}
		#endregion	
		#region  Insert
		public Definition Insert(string s)
		{
			// todo..
			return null;
		}
		#endregion	
		#region  Validate
		public /*abstract*/ bool Validate()
		{
			return true;
		}
		#endregion	
	}
	#endregion	

	#region  AssemblyScope 
	public class AssemblyScope	: Scope
	{
		#region  Version  
		private string p_version;
		public string				Version					
		{
			get
			{
				return p_version;
			}
			set
			{
				p_version = value;
			}
		}	
	#endregion
	}
	#endregion	
	#region  NamespaceScope 
	public class NamespaceScope : Scope, INamedScope
	{
		#region  Imports  
		private ScopeCollection p_imports = new ScopeCollection();
		public ScopeCollection				Imports					
		{
			get
			{
				return p_imports;
			}
		}	
		#endregion
		#region Definition
		private IDefinition p_definition;
		/// <summary>
		/// Gets or sets the definition of this (named) scope.
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
	}
	#endregion	
	#region  TypeScope 
	public class TypeScope		: Scope, INamedScope
	{
		#region  Modifiers  
		private Modifiers p_modifiers = Modifiers.Empty;
		public Modifiers				Modifiers					
		{
			get
			{
				return p_modifiers;
			}
			set
			{
				p_modifiers = value;
			}
		}	
	#endregion
		#region  Imports  
		private ScopeCollection p_imports = new ScopeCollection();
		public ScopeCollection				Imports					
		{
			get
			{
				return p_imports;
			}
		}	
	#endregion
		#region  TypeKind  
		private TypeKind p_typeKind = TypeKind.Empty;
		public TypeKind				TypeKind					
		{
			get
			{
				return p_typeKind;
			}
			set
			{
				p_typeKind = value;
			}
		}	
		#endregion
		#region Definition
		private IDefinition p_definition;
		/// <summary>
		/// Gets or sets the definition of this (named) scope.
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
	}
	#endregion	
	#region  MemberScope 
	public class MemberScope	: Scope, INamedScope
	{	
		#region  Modifiers  
		private Modifiers p_modifiers = new Modifiers();
		public Modifiers				Modifiers					
		{
			get
			{
				return p_modifiers;
			}
			set
			{
				p_modifiers = value;
			}
		}	
	#endregion
		#region  Parameters  
		private DefinitionCollection p_parameters = new DefinitionCollection();
		public DefinitionCollection				Parameters					
		{
			get
			{
				return p_parameters;
			}
			set
			{
				p_parameters = value;
			}
		}	
		#endregion
		#region  MemberKind  
		private MemberKind p_memberKind = MemberKind.Empty;
		public MemberKind				MemberKind					
		{
			get
			{
				return p_memberKind;
			}
			set
			{
				p_memberKind = value;
			}
		}	
		#endregion
		#region Definition
		private IDefinition p_definition;
		/// <summary>
		/// Gets or sets the definition of this (named) scope.
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
	}
	#endregion	
	#region  BlockScope 
	public class BlockScope		: Scope
	{
	}
	#endregion	

	#region  INamedScope
	/// <summary>
	/// Interface used to differentiate between named scopes (ns, types, members) and unnamed (asm, block).
	/// </summary>
	public interface INamedScope
	{
		IDefinition Definition{get;set;}	
	}
	#endregion	


	#region  IDefinition
	/// <summary>
	/// Interface for Defintions.  Definitions may be variable declarations, or scope declarations that have names (like types or methods, but not blocks or accessors). Note: With members that can declare multiple types at once, like fields or events, the declarator is the IDefinition.
	/// </summary>
	public interface IDefinition
	{
		int Id{get;set;}
		string Name{get;set;}
		string FullName{get;}
		Scope Scope{get;set;}	
		IDeclaration SourceGraph{get;set;}	
		IDeclaration Type{get; set;}
	}
	#endregion	
	#region  Definition 
	/// <summary>
	/// Base class for Definitions. Definitions may be variables, or scopes that have names.
	/// </summary>
	public class Definition : IDefinition
	{
		private int p_id;
		public int Id{get{return p_id;}set{p_id = value;}}
		#region  Name 
		private string p_name = ""; 
		/// <summary>
		/// Gets or sets the given name of the Definition.
		/// </summary>
		public virtual string				Name					
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
		#region  FullName 
		/// <summary>
		/// Gets the full name of the scope (namespace.class.member...).
		/// </summary>
		public string				FullName					
		{
			get
			{
				if(Scope == null) return "";
				// todo: deal with ref'd assemblies etc
				string s = "";
				//TODO: must use path so block scopes can see up
				if(Scope.EnclosingScope is INamedScope)
					s += ((INamedScope)Scope.EnclosingScope).Definition.FullName + ".";
				if(!(SourceGraph is IScope) && Scope is INamedScope)
					s += ((INamedScope)Scope).Definition.Name + ".";
				s += Name;
				return s;
			}
		}	
		#endregion	
		#region  Scope 
		private Scope p_scope; 
		/// <summary>
		/// Gets or sets the scope of the definition.
		/// </summary>
		public Scope				Scope					
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
		#region  Type 
		private IDeclaration p_type;
		/// <summary>
		/// Link to the defining type. Actual types just return a ref to themselves.
		/// </summary>
		public IDeclaration				Type					
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
		#region SourceGraph  
		private IDeclaration p_graph;
		/// <summary>
		/// Link to the defining element.
		/// </summary>
		public IDeclaration				SourceGraph					
		{
			get
			{
				return p_graph;
			}
			set
			{
				p_graph = value;
			}
		}	
		#endregion	
		#region  GetHashCode 
		/// <summary>
		/// Gets the hash of this defintion. The hash is made from the full path.
		/// </summary>
		public override int	GetHashCode()					
		{
			return this.Name.GetHashCode();
		}	
		#endregion	

		#region  ToString 
		public override string	ToString()					
		{
			if(Scope == null) return "";
			string s = "";
			// Some defs may not have been attrib'ed at this point 
			// (eg. we don't dig into mscorlib etc), so 'try'
			try
			{
			if(Scope.EnclosingScope is INamedScope)
				s += ((INamedScope)Scope.EnclosingScope).Definition.FullName + ".";
			}
			catch(NullReferenceException){}
			try
			{
				if(!(SourceGraph is IScope) && Scope is INamedScope)
					s += ((INamedScope)Scope).Definition.Name + ".";
			}
			catch(NullReferenceException){}
			s += Name + " #" + Id;
			return s;
		}	
		#endregion	

	}
	#endregion	

	#region  ISymbol
	public interface ISymbol : IDefinition
	{
		IDefinition DeclaredType{get;set;}	
		IDefinition ActualType{get;set;}	
	}
	#endregion	
	#region  Symbol
	public class Symbol : Definition, ISymbol
	{
		#region  DeclaredType 
		private IDefinition p_declType; 
		/// <summary>
		/// Gets or sets the declared type of the symbol.
		/// </summary>
		public IDefinition				DeclaredType					
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
		#region  ActualType 
		private IDefinition p_actualType; 
		/// <summary>
		/// Gets or sets the declared type of the symbol.
		/// </summary>
		public IDefinition				ActualType					
		{
			get
			{
				return p_actualType;
			}
			set
			{
				p_actualType = value;
			}
		}	
		#endregion		
	}
	#endregion	

	#region  OverloadableDefinition 
	/// <summary>
	/// Base class for Definitions. Definitions may be variables, or scopes that have names.
	/// </summary>
	public class OverloadableDefinition : Definition
	{
		#region  Definitions  
		private DefinitionCollection p_defs = new DefinitionCollection();
		/// <summary>
		/// Gets the collection of Definitions that share the same name but use different arguments in the same scope (eg that are overloaded).
		/// </summary>
		public DefinitionCollection			Definitions					
		{
			get
			{
				return p_defs;
			}
		}	
		#endregion	
	}
	#endregion	
	#region  BuiltInDefinition 
	/// <summary>
	/// Base class for Definitions. Definitions may be variables, or scopes that have names.
	/// </summary>
	public class BuiltInDefinition : Definition
	{
		#region  Name  
		/// <summary>
		/// Gets or sets the name of the built in type.
		/// </summary>
		public override string				Name					
		{
			get
			{
				return p_literalType.ToString().ToLower();
			}
			set
			{
				p_literalType = (LiteralType)Enum.Parse(LiteralType.GetType(), value, true);
			}
		}	
		#endregion
		#region  LiteralType  
		private LiteralType p_literalType;
		/// <summary>
		/// Gets or sets the literal type of this built in type.
		/// </summary>
		public LiteralType				LiteralType					
		{
			get
			{
				return p_literalType;
			}
			set
			{
				p_literalType = value;
			}
		}	
		#endregion		
		#region Built in IDeclarations
		public static StructDecl SbyteDecl = new StructDecl();
		#endregion
		#region widening auto conversion arrays
		private LiteralType[] widenSbyte = new LiteralType[]
			{
				LiteralType.Sbyte,
				LiteralType.Short,
				LiteralType.Int, 
				LiteralType.Long,
				LiteralType.Float,
				LiteralType.Double,
				LiteralType.Decimal
			};
		private LiteralType[] widenByte = new LiteralType[]
			{
				LiteralType.Byte,
				LiteralType.Short,
				LiteralType.Ushort,
				LiteralType.Int, 
				LiteralType.Uint, 
				LiteralType.Long,
				LiteralType.Ulong,
				LiteralType.Float,
				LiteralType.Double,
				LiteralType.Decimal
			};
		private LiteralType[] widenShort = new LiteralType[]
			{
				LiteralType.Short,
				LiteralType.Int, 
				LiteralType.Long,
				LiteralType.Float,
				LiteralType.Double,
				LiteralType.Decimal
			};
		private LiteralType[] widenUshort = new LiteralType[]
			{
				LiteralType.Ushort,
				LiteralType.Int, 
				LiteralType.Uint, 
				LiteralType.Long,
				LiteralType.Ulong,
				LiteralType.Float,
				LiteralType.Double,
				LiteralType.Decimal
			};
		private LiteralType[] widenInt = new LiteralType[]
			{
				LiteralType.Int, 
				LiteralType.Long,
				LiteralType.Float,
				LiteralType.Double,
				LiteralType.Decimal
			};
		private LiteralType[] widenUint = new LiteralType[]
			{
				LiteralType.Uint, 
				LiteralType.Long,
				LiteralType.Ulong,
				LiteralType.Float,
				LiteralType.Double,
				LiteralType.Decimal
			};
		private LiteralType[] widenLong = new LiteralType[]
			{
				LiteralType.Long,
				LiteralType.Float,
				LiteralType.Double,
				LiteralType.Decimal
			};
		private LiteralType[] widenUlong = new LiteralType[]
			{
				LiteralType.Ulong,
				LiteralType.Float,
				LiteralType.Double,
				LiteralType.Decimal
			};
		private LiteralType[] widenFloat = new LiteralType[]
			{
				LiteralType.Float,
				LiteralType.Double
			};
		private LiteralType[] widenChar = new LiteralType[]
			{
				LiteralType.Char,
				LiteralType.Ushort,
				LiteralType.Int, 
				LiteralType.Uint, 
				LiteralType.Long,
				LiteralType.Ulong,
				LiteralType.Float,
				LiteralType.Double,
				LiteralType.Decimal
			};
		#endregion
		#region ConvertDistance
		public int ConvertDistance(LiteralType destinationType)
		{
			LiteralType[] convArray;
			switch(LiteralType)
			{
				case LiteralType.Sbyte :
				{
					convArray = widenSbyte;
					break;
				}
				case LiteralType.Byte :
				{
					convArray = widenByte;
					break;
				}
				case LiteralType.Short :
				{
					convArray = widenShort;
					break;
				}	
				case LiteralType.Ushort :
				{
					convArray = widenUshort;
					break;
				}	
				case LiteralType.Int :
				{
					convArray = widenInt;
					break;
				}	
				case LiteralType.Uint :
				{
					convArray = widenUint;
					break;
				}	
				case LiteralType.Long :
				{
					convArray = widenLong;
					break;
				}	
				case LiteralType.Ulong :
				{
					convArray = widenUlong;
					break;
				}	
				case LiteralType.Float :
				{
					convArray = widenFloat;
					break;
				}	
				case LiteralType.Char :
				{
					convArray = widenChar;
					break;
				}	
				default :
				{
					convArray = new LiteralType[0];
					break;
				}
			}
			int dist = Int32.MaxValue;
			for(int i = 0; i < convArray.Length; i++)
			{
				if(convArray[i] == destinationType)
				{
					dist = i;
					break;
				}
			}
			return dist;
		}
		#endregion
	}
	#endregion	

	#region ExpressionRoot
	public class ExpressionRoot
	{
		public Expression Expression;
		public Type Instance;
		public string PropertyName;

		public void ReplaceExpression(Expression ex)
		{
			if(Instance == null) return;       
			Type instType = Instance.GetType();
			PropertyInfo pi = instType.GetProperty(PropertyName);

			pi.SetValue(Instance, ex, null);
		}

	}
	#endregion
	
	#region LiteralType
	/// <summary>
	/// An enumeration of the built in types, from 'lowest' to 'highest'.
	/// </summary>
	public enum LiteralType
	{
		Void,
		Null,
		Bool,
		Sbyte,
		Byte,
		Short,
		Ushort,
		Char,
		Int,
		Uint,
		Long,
		Ulong,
		Float,
		Double,
		Decimal,
		String,	
		Object,
	}
	#endregion
}
