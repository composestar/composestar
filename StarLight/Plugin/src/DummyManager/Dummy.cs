using System;
using System.CodeDom;
using System.Reflection;
using System.Text.RegularExpressions;

namespace ComposestarVSAddin
{
	/// <summary>
	/// Summary description for Dummy.
	/// </summary>
	public class Dummy
	{
		// the actual dummy code dom
		private CodeCompileUnit compileUnit;
		
		private string name;

		public string getName() 
		{
			return this.name;
		}

		public Dummy()
		{
			compileUnit = new CodeCompileUnit();
		}

		/// <summary>
		/// Add a comment at the top of the class with information how to override 
		/// this dummy with an user created dummy.
		/// </summary>
		/// <param name="cns"></param>
		private void AddTopComments(CodeNamespace cns)
		{
			CodeCommentStatementCollection cc = new CodeCommentStatementCollection();
			cc.Add(new CodeCommentStatement("------------------------------------------------------------------------------")); 
			cc.Add(new CodeCommentStatement("  This is an automatic generated dummy file created by ")); 
			cc.Add(new CodeCommentStatement("  the Composestar Dummy Generator. ")); 
			cc.Add(new CodeCommentStatement("   ")); 
			cc.Add(new CodeCommentStatement("  If this file returns errors you can override this file by changing "));
			cc.Add(new CodeCommentStatement(String.Format("  this file and save it as '{0}.user.cs' or '{0}.user.js'. ", this.getName())));
			cc.Add(new CodeCommentStatement("  Rerun the Composestar build and this file will be used instead of  ")); 
			cc.Add(new CodeCommentStatement("  a newly generated dummy. ")); 
			cc.Add(new CodeCommentStatement("------------------------------------------------------------------------------")); 
			
			cns.Comments.AddRange(cc);  
		}

		/// <summary>
		/// Construct a new class.
		/// </summary>
		/// <param name="dummyClass"></param>
		public void constructClass(EnvDTE.CodeClass dummyClass) 
		{
			this.name = dummyClass.FullName;
			EnvDTE.CodeNamespace dummyNamespace = null;
			CodeNamespace codeNamespace = null;

			try 
			{
				dummyNamespace = dummyClass.Namespace;
			}			
			catch (System.Exception) 
			{
				// If no namespace is set for the class, accessing the Namespace value results in a unspecified error. Even the != null check throws this error
			}
			if (dummyNamespace != null) 
			{
				codeNamespace = new CodeNamespace(dummyNamespace.FullName);
			}
			else 
			{
				codeNamespace = new CodeNamespace("");
			}
					
			compileUnit.Namespaces.Add(codeNamespace);
			
			CodeTypeDeclaration typeDeclaration = new CodeTypeDeclaration(dummyClass.Name);
					
			fillTypeDecl(dummyClass, typeDeclaration);
			codeNamespace.Types.Add(typeDeclaration);
			this.AddTopComments(codeNamespace); 	
		}

		/// <summary>
		/// Construct a new enumeration class.
		/// </summary>
		/// <param name="dummyEnum"></param>
		public void constructEnum(EnvDTE.CodeEnum dummyEnum) 
		{
			this.name = dummyEnum.FullName;
			EnvDTE.CodeNamespace dummyNamespace = null;
			CodeNamespace codeNamespace = null;

			try 
			{
				dummyNamespace = dummyEnum.Namespace;
			}			
			catch (System.Exception) 
			{
				// If no namespace is set for the class, accessing the Namespace value results in a unspecified error. Even the != null check throws this error
			}
			if (dummyNamespace != null) 
			{
				codeNamespace = new CodeNamespace(dummyNamespace.FullName);
			}
			else 
			{
				codeNamespace = new CodeNamespace("");
			}
					
			compileUnit.Namespaces.Add(codeNamespace);
			
			CodeTypeDeclaration typeDeclaration = new CodeTypeDeclaration(dummyEnum.Name);
			typeDeclaration.IsEnum = true;
			fillEnumDecl(dummyEnum, typeDeclaration);
			
			codeNamespace.Types.Add(typeDeclaration);
			this.AddTopComments(codeNamespace); 
		}

		/// <summary>
		/// Set the direction of the parameter. This information is based on the FileCodeModel which returns a type.
		/// Using this type as a string we can find out if it is a output or reference parameter.
		/// </summary>
		/// <param name="method">CodeMemberMethod which can be extended with extra set assignments.</param>
		/// <param name="param">The parameter from the filecodemodel.</param>
		/// <param name="paramDecl">The new parameter declaration to be added.</param>
		private void SetDirectionOfParam(CodeMemberMethod method, EnvDTE.CodeParameter param, CodeParameterDeclarationExpression paramDecl)
		{
			// set direction of param
			string typeAsString = param.Type.AsString.Trim().ToLower() ;
			if (typeAsString.StartsWith("out "))
			{
				paramDecl.Direction = FieldDirection.Out;
       
				// When we have an output, we have to set it to a value before we may return.
				System.Object defaultValue = GetDefaultValue(param.Type.AsFullName);
				CodeAssignStatement setValue = new CodeAssignStatement(new CodeVariableReferenceExpression(param.FullName), new CodePrimitiveExpression(defaultValue));
				method.Statements.Add(setValue);
			}
			else if (typeAsString.StartsWith("ref "))
			{
				paramDecl.Direction = FieldDirection.Ref;
			}
			else
			{
				paramDecl.Direction = FieldDirection.In;
			}
				
		}

		/// <summary>
		/// Get the default value of a given type. For a value type it will return a new instance, 
		/// for another type it will return nothing.
		/// </summary>
		/// <param name="typeName">Fully specified type name.</param>
		/// <returns>Object containing a default value.</returns>
		private System.Object GetDefaultValue(string typeName)
		{
			System.Object defaultValue = null;
			System.Type t = System.Type.GetType(typeName,false, true);
			if ((t != null) && (t.IsValueType ))
			{
				defaultValue = Activator.CreateInstance(t);
			}
			return defaultValue;
		}

		public void fillTypeDecl(EnvDTE.CodeClass dummyClass, CodeTypeDeclaration typeDeclaration)
		{
			//System.Windows.Forms.MessageBox.Show("Fill type decl for "+dummyClass.FullName);
			foreach(EnvDTE.CodeElement codeElement in dummyClass.Bases) 
			{
				typeDeclaration.BaseTypes.Add(codeElement.FullName);
			}
			
			if( dummyClass.IsAbstract )
				typeDeclaration.TypeAttributes |= TypeAttributes.Abstract;

			if( dummyClass.Access == EnvDTE.vsCMAccess.vsCMAccessPrivate )
				typeDeclaration.Attributes = MemberAttributes.Private;
			else if( dummyClass.Access == EnvDTE.vsCMAccess.vsCMAccessPublic )
				typeDeclaration.Attributes = MemberAttributes.Public;
					
					
			foreach( EnvDTE.CodeInterface codeInterface in dummyClass.ImplementedInterfaces )
			{
				CodeTypeReference typeRef = new CodeTypeReference(codeInterface.FullName);
				typeDeclaration.BaseTypes.Add(typeRef);
				//typeDeclaration.BaseTypes.Add(codeInterface.FullName);
			}

			foreach( EnvDTE.CodeElement member in dummyClass.Members ) 
			{	
				if( member.Kind == EnvDTE.vsCMElement.vsCMElementVariable )
				{
					EnvDTE.CodeVariable variable = (EnvDTE.CodeVariable) member;
					CodeTypeReference t = this.convertTypeRef(variable.Type);

					CodeMemberField field = new CodeMemberField(t, variable.Name);
					
					if( variable.InitExpression != null && !variable.InitExpression.ToString().Equals("") )
					{
						//TODO: doesn't always produce working code, in the case of:
						//	import Some.Package.AClass
						//  class SomeClass {
						//		public AClass ac = new AClass();
						//	}
						// Source Forge Bug ID: 1156104 
						string initExpr = variable.InitExpression.ToString();
					
						field.InitExpression = new System.CodeDom.CodeSnippetExpression(initExpr);
					}
					
					field.Attributes = MemberAttributes.Public | MemberAttributes.Final;
					//if( variable.IsConstant)
					//	field.Attributes |= MemberAttributes.Const;
					if( variable.IsShared )
						field.Attributes |= MemberAttributes.Static;
					
					typeDeclaration.Members.Add(field);

				}
				else if( member.Kind == EnvDTE.vsCMElement.vsCMElementProperty)
				{
					this.constructCodeMemberProperty(member,typeDeclaration,dummyClass.Bases);
				}
					//add support for Java inner-classes
				else if( member.Kind == EnvDTE.vsCMElement.vsCMElementClass )
				{
					// simply add a new CodeTypeDeclaration to the current one and call
					// this function again to process the inner class
					EnvDTE.CodeClass innerClass = (EnvDTE.CodeClass) member;
					CodeTypeDeclaration innerTypeDecl = new CodeTypeDeclaration(member.Name);
					typeDeclaration.Members.Add( innerTypeDecl );
					fillTypeDecl(innerClass, innerTypeDecl);
				}		
				else if( member.Kind == EnvDTE.vsCMElement.vsCMElementEvent )
				{
					this.constructCodeMemberEvent(member,typeDeclaration);
				}
				else if( member.Kind == EnvDTE.vsCMElement.vsCMElementFunction )
				{
					EnvDTE.CodeFunction function = (EnvDTE.CodeFunction) member;
					
					if( function.FunctionKind == EnvDTE.vsCMFunction.vsCMFunctionConstructor )
					{
						CodeConstructor constructor = new CodeConstructor();
						constructor.Name = function.Name;
						foreach(EnvDTE.CodeParameter param in function.Parameters) 
						{
							CodeParameterDeclarationExpression paramDecl = new CodeParameterDeclarationExpression();
							paramDecl.Name = param.Name;

							// Parameter direction
							SetDirectionOfParam(constructor, param, paramDecl);

							//System.Windows.Forms.MessageBox.Show("Searching param type : " + param.Type.AsFullName + " not found");
							CodeTypeReference typeRef = this.convertTypeRef(param.Type);
							if( typeRef != null )
								paramDecl.Type = typeRef;

							constructor.Parameters.Add(paramDecl);
						}
						if( function.Access != EnvDTE.vsCMAccess.vsCMAccessPrivate )
							constructor.Attributes = MemberAttributes.Public;
						typeDeclaration.Members.Add(constructor);
					}
					else if( function.FunctionKind == EnvDTE.vsCMFunction.vsCMFunctionDestructor  )
					{
						// A destructor is a member that implements the actions required to destruct 
						// an instance of a class. Destructors cannot have parameters, they cannot 
						// have accessibility modifiers, and they cannot be called explicitly. 
						// The destructor for an instance is called automatically during garbage 
						// collection.

						// We are using a CodeSnippetTypeMember because the destructor is not supported
						// in the CodeDom model. Not all the languages are supporting this concept.
						// For example in VB.NET you must override the Finilize method .
						CodeSnippetTypeMember destructor = new CodeSnippetTypeMember(string.Concat(function.Name, " () { /* destructor */ }"));
						destructor.Name = function.Name; 	
						typeDeclaration.Members.Add(destructor);
					}
					else 
					{
						CodeMemberMethod method = new CodeMemberMethod();
						method.Name = function.Name;
						
						if( function.Access == EnvDTE.vsCMAccess.vsCMAccessPrivate )
						{
							method.Attributes = MemberAttributes.Private;
							if(mustOverride(dummyClass.Bases,member))	
								method.Attributes = method.Attributes | MemberAttributes.Override;
						}
						else if( function.Access == EnvDTE.vsCMAccess.vsCMAccessPublic )
						{
							method.Attributes = MemberAttributes.Public;
							if(mustOverride(dummyClass.Bases,member))	
								method.Attributes = method.Attributes | MemberAttributes.Override;
						}
						else if(function.Access == EnvDTE.vsCMAccess.vsCMAccessProtected)
						{
							method.Attributes = MemberAttributes.Overloaded ;
						}
						else
						{
							method.Attributes = MemberAttributes.Public;
						}

						if( function.IsShared )
							method.Attributes = method.Attributes | MemberAttributes.Static;
						
						foreach(EnvDTE.CodeParameter param in function.Parameters) 
						{
							CodeParameterDeclarationExpression paramDecl = new CodeParameterDeclarationExpression();
							paramDecl.Name = param.Name;
						
							CodeTypeReference typeRef = this.convertTypeRef(param.Type);
							
							if( typeRef != null )
								paramDecl.Type = typeRef;
						
							SetDirectionOfParam(method, param, paramDecl);
							
							method.Parameters.Add(paramDecl);
						}
					
						if( function.Type.TypeKind == EnvDTE.vsCMTypeRef.vsCMTypeRefArray )
						{
							// for arrays return null
							method.Statements.Add(new CodeMethodReturnStatement(new CodeSnippetExpression("null")));
							
							// now the hard part, set the return type
							string type = function.Type.AsString;
							//System.Windows.Forms.MessageBox.Show("Array of type: "  + type);
								
							int rank = (type.Length - type.IndexOf("["))/2;

							type = type.Substring(0, type.Length-(rank*2));
							//System.Windows.Forms.MessageBox.Show("rank: " + rank);

							string newType = this.getFulltypeForValuetype(type);
							if( newType == null )
								newType = type;
							
							Type t = Type.GetType(newType);
							if( t != null )
							{
								CodeTypeReference returnRef = new CodeTypeReference(t.FullName, rank);
								method.ReturnType = returnRef;
							} 
							else
							{
								CodeTypeReference returnRef = new CodeTypeReference(newType, rank);
								method.ReturnType = returnRef;
							}
						}
						else if( function.Type.TypeKind == EnvDTE.vsCMTypeRef.vsCMTypeRefVoid )
						{
							// void method, no returntype or returnstatement
						}
						else
						{
							Type type = Type.GetType(function.Type.AsFullName);
							if( type == null )
							{
								CodeTypeReference returnRef = new CodeTypeReference(function.Type.AsFullName);
								method.Statements.Add(new CodeMethodReturnStatement(new CodeSnippetExpression("null")));
								method.ReturnType = returnRef;
							}
							else
							{
								CodeTypeReference returnRef = new CodeTypeReference(type);
								// set the returntype
								method.ReturnType = returnRef;
								// select the right return statement
								if( type.IsPrimitive && type.FullName.Equals("System.Boolean") )
									method.Statements.Add(new CodeMethodReturnStatement(new CodeSnippetExpression("false")));
								else if( type.IsPrimitive )
									method.Statements.Add(new CodeMethodReturnStatement(new CodeSnippetExpression("0")));
								else if( type.IsClass )
									method.Statements.Add(new CodeMethodReturnStatement(new CodeSnippetExpression("null")));	
								else if( type.IsInterface )
									method.Statements.Add(new CodeMethodReturnStatement(new CodeSnippetExpression("null")));	
								else 
									method.Statements.Add(new CodeMethodReturnStatement(new CodeSnippetExpression()));
							}
						}
						typeDeclaration.Members.Add(method);
					}
				}
				else 
				{
					string m = member.FullName  + "   " + member.Kind.ToString();  
					// what are we missing?
				}		
			}

		}

		public void fillEnumDecl(EnvDTE.CodeEnum dummyEnum, CodeTypeDeclaration typeDeclaration)
		{
		
			if( dummyEnum.Access == EnvDTE.vsCMAccess.vsCMAccessPrivate )
				typeDeclaration.Attributes = MemberAttributes.Private;
			else if( dummyEnum.Access == EnvDTE.vsCMAccess.vsCMAccessPublic )
				typeDeclaration.Attributes = MemberAttributes.Public;
			
			foreach( EnvDTE.CodeElement member in dummyEnum.Members ) 
			{	
				if( member.Kind == EnvDTE.vsCMElement.vsCMElementVariable )
				{
					EnvDTE.CodeVariable variable = (EnvDTE.CodeVariable) member;
					CodeTypeReference t = this.convertTypeRef(variable.Type);

					CodeMemberField field = new CodeMemberField(t, variable.Name);
					
					if( variable.InitExpression != null && !variable.InitExpression.ToString().Equals("") )
					{
						
						string initExpr = variable.InitExpression.ToString();
					
						field.InitExpression = new System.CodeDom.CodeSnippetExpression(initExpr);
					}
					
					field.Attributes = MemberAttributes.Public | MemberAttributes.Final;
					//if( variable.IsConstant)
					//	field.Attributes |= MemberAttributes.Const;
					if( variable.IsShared )
						field.Attributes |= MemberAttributes.Static;
					
					typeDeclaration.Members.Add(field);
				}
				else 
				{
					string m = member.FullName  + "   " + member.Kind.ToString();  
					// what are we missing?
				}		
			}

		}

		public bool mustOverride(EnvDTE.CodeElements bases,EnvDTE.CodeElement element)
		{
			
			try 
			{
				foreach(EnvDTE.CodeElement bas in bases)
				{	
					if(bas.Kind == EnvDTE.vsCMElement.vsCMElementClass) 
					{
						EnvDTE.CodeClass baseClass = (EnvDTE.CodeClass)bas;
						foreach(EnvDTE.CodeElement elem in baseClass.Members)
						{
							if(elem.Kind.Equals(element.Kind))
							{
								if(element.Name.Equals(elem.Name))
								{	
									if(elem.Kind == EnvDTE.vsCMElement.vsCMElementFunction)
									{
										EnvDTE.CodeFunction c1 = (EnvDTE.CodeFunction)elem;
										EnvDTE.CodeFunction c2 = (EnvDTE.CodeFunction)element;
										
										// You cannot override a non-virtual or static method. 
										// The overridden base method must be virtual, abstract, or override
										if(c1.CanOverride)
										{
											// check parameters
											if(c1.Parameters.Count == c2.Parameters.Count)
											{
												bool found = true;
												for(int i=1;i<=c1.Parameters.Count;i++)
												{
													EnvDTE.CodeParameter cp1 = (EnvDTE.CodeParameter)c1.Parameters.Item(i);
													EnvDTE.CodeParameter cp2 = (EnvDTE.CodeParameter)c2.Parameters.Item(i);
													if(cp1.Type.AsFullName != cp2.Type.AsFullName)
														found = false;
												}
											
												if(found)
													return found;
											}
										}
										
									}
									else if(elem.Kind == EnvDTE.vsCMElement.vsCMElementProperty) 
									{
										return true;
										//TODO: check parameters when property is indexer
									}
									else
										return false;
								}
							}
						}
					}
				}
			}
			catch(Exception){}
			return false;
			
		}

		public CodeTypeReference convertTypeRef(EnvDTE.CodeTypeRef origType)
		{
			CodeTypeReference typeRef = null;
			if( origType.TypeKind == EnvDTE.vsCMTypeRef.vsCMTypeRefArray )
			{
				string type = origType.AsString;
				int rank = (type.Length - type.IndexOf("["))/2;
				type = type.Substring(0, type.Length-(rank*2));
				
				string newType = this.getFulltypeForValuetype(type);
				if( newType == null )
					newType = type;
				
				if(newType.EndsWith("["))
					newType = newType + "]";	
				
				try 
				{
					Type t = Type.GetType(newType);
								
					if( t != null )
						typeRef = new CodeTypeReference(t.FullName, rank);
					else
						typeRef = new CodeTypeReference(newType, rank);
				}
				catch(Exception){System.Windows.Forms.MessageBox.Show("Type: " + newType);}
			}
			else if( origType.TypeKind == EnvDTE.vsCMTypeRef.vsCMTypeRefVoid )
			{
				// nothing
			}
			else
			{
				
				Type type = Type.GetType( origType.AsFullName);
				if( type == null )
					typeRef = new CodeTypeReference( origType.AsFullName);
				else
					typeRef = new CodeTypeReference(type);
			}
			return typeRef;
		}

		public void constructInterface(EnvDTE.CodeInterface dummyClass) 
		{
			this.name = dummyClass.FullName;
			EnvDTE.CodeNamespace dummyNamespace = dummyClass.Namespace;

			CodeNamespace codeNamespace = new CodeNamespace(dummyNamespace.FullName);
			CodeTypeDeclaration typeDeclaration = new CodeTypeDeclaration(dummyClass.Name);
			codeNamespace.Types.Add(typeDeclaration);
			compileUnit.Namespaces.Add(codeNamespace);

			typeDeclaration.IsInterface = true;
			
			foreach(EnvDTE.CodeElement codeElement in dummyClass.Bases) 
			{
				typeDeclaration.BaseTypes.Add(codeElement.FullName);
			}

			foreach( EnvDTE.CodeElement member in dummyClass.Members ) 
			{	
				try 
				{
					if(member.Kind == EnvDTE.vsCMElement.vsCMElementProperty)
						this.constructCodeMemberProperty(member,typeDeclaration,dummyClass.Bases);
					else if(member.Kind == EnvDTE.vsCMElement.vsCMElementFunction)
						this.constructCodeMemberFunction(member,typeDeclaration);
					else if(member.Kind == EnvDTE.vsCMElement.vsCMElementEvent)
						this.constructCodeMemberEvent(member,typeDeclaration);
				}
				catch(Exception){System.Windows.Forms.MessageBox.Show("Error constructInterface "+member.FullName);}
			}

		}

		/// <summary>
		/// Construct an event dummy
		/// </summary>
		/// <remarks>
		/// 
		/// This is not working correctly. The type of the event cannot be found.
		/// So this will simply generate an incorrent dummy. Manually overriding of 
		/// this dummy is for now the only solution.
		/// 
		/// </remarks> 
		/// <param name="element"></param>
		/// <param name="typeDeclaration"></param>
		public void constructCodeMemberEvent(EnvDTE.CodeElement element,CodeTypeDeclaration typeDeclaration)
		{	
			CodeMemberEvent event1 = new CodeMemberEvent();
			
			// Sets a name for the event.
			event1.Name = element.Name;
			
			// Sets the type of event.
			Type type = Type.GetType(element.FullName);
		  
			CodeTypeReference typeref;
			if( type == null )
				typeref = new CodeTypeReference(element.FullName);
			else
				typeref = new CodeTypeReference(type);
			
			event1.Type = typeref;
			event1.Attributes = MemberAttributes.Public; 
			typeDeclaration.Members.Add(event1);
		}

		public void constructCodeMemberProperty(EnvDTE.CodeElement element,CodeTypeDeclaration typeDeclaration,EnvDTE.CodeElements bases)
		{
			// construct a CodeMemberProperty and adds it to the members of a CodeTypeDeclaration
			EnvDTE.CodeProperty cp = (EnvDTE.CodeProperty)element;
			Type type = null;
			string fulltype = cp.Type.AsFullName;
			CodeTypeReference typeRef;
			bool IsIndexer = false;

			// set name of property
			CodeMemberProperty prop = new CodeMemberProperty();
			prop.Name = cp.Name;

			// set access of property
			if( cp.Access == EnvDTE.vsCMAccess.vsCMAccessPrivate )
				prop.Attributes = MemberAttributes.Private;
			else if( cp.Access == EnvDTE.vsCMAccess.vsCMAccessPublic )
			{
				prop.Attributes = MemberAttributes.Public;
				if(mustOverride(bases,element))	
					prop.Attributes = prop.Attributes | MemberAttributes.Override;
			}

			if(prop.Name.Equals("this"))
			{
				// The This is a special indexer property. We have to create a Item property to handle This.
				IsIndexer = true;
				prop.Name = "Item";
				String proptype = ((EnvDTE.CodeParameter)cp.Getter.Parameters.Item(1)).Type.AsFullName; 
				CodeParameterDeclarationExpression intIndex = new CodeParameterDeclarationExpression(Type.GetType(proptype)   , "index"); 
				prop.Parameters.Add( intIndex);
			}
					
			// set return type...
			if( cp.Type.TypeKind == EnvDTE.vsCMTypeRef.vsCMTypeRefArray )
			{
				// now the hard part, set the return type
				string arrtype = cp.Type.AsString;
				int rank = (arrtype.Length - arrtype.IndexOf("["))/2;
				
				string cptype = arrtype.Substring(0, arrtype.Length-(rank*2));
				string arraydim = arrtype.Substring(cptype.Length);
				
				fulltype = this.getFulltypeForValuetype(cptype);
				
				// add array part to type
				if(fulltype!=null)
					fulltype += arraydim;
				else
					fulltype = cptype + arraydim;
					
			}
			
			type = Type.GetType(fulltype);
			if( type == null )
				typeRef = new CodeTypeReference(fulltype);
			else
				typeRef = new CodeTypeReference(type);
				
			prop.Type = typeRef;
			
			// set accessors of property
			try 
			{				
				if (IsIndexer) 
				{
					prop.GetStatements.Add(new CodeMethodReturnStatement( new CodePrimitiveExpression(GetDefaultValue(fulltype))));
				}
				else
				{
					prop.GetStatements.Add(new CodeMethodReturnStatement(new CodeFieldReferenceExpression(new CodeThisReferenceExpression(),cp.Getter.Name)));
				}
			}
			catch(Exception){/*ignore*/}
			
			try 
			{
				if (IsIndexer && (cp.Setter!= null)) 
				{
					prop.SetStatements.Add(new CodeCommentStatement("empty setter", false));
				}
				else
				{
					prop.SetStatements.Add(new CodeAssignStatement(new CodeFieldReferenceExpression(new CodeThisReferenceExpression(),cp.Setter.Name),new CodePropertySetValueReferenceExpression()));
				}
			}
			catch(Exception){/*ignore*/ }
			

			// add property to typeDeclaration
			typeDeclaration.Members.Add(prop);
		}

		public void constructCodeMemberFunction(EnvDTE.CodeElement member,CodeTypeDeclaration typeDeclaration)
		{
			// construct a CodeFunction and adds it to the members of a CodeTypeDeclaration
			EnvDTE.CodeFunction function = (EnvDTE.CodeFunction) member;
			if( function.FunctionKind == EnvDTE.vsCMFunction.vsCMFunctionFunction )
			{
				CodeMemberMethod method = new CodeMemberMethod();
				method.Name = function.Name;

				// set the access of the method
				if( function.Access == EnvDTE.vsCMAccess.vsCMAccessPrivate )
					method.Attributes = MemberAttributes.Private;
				else if( function.Access == EnvDTE.vsCMAccess.vsCMAccessPublic )
					method.Attributes = MemberAttributes.Public;

				if( function.IsShared )
					method.Attributes = method.Attributes | MemberAttributes.Static;

				// set the parameters
				foreach(EnvDTE.CodeParameter param in function.Parameters) 
				{
					CodeParameterDeclarationExpression paramDecl = new CodeParameterDeclarationExpression();
					paramDecl.Name = param.Name;
 
					// Parameter direction
					SetDirectionOfParam(method, param, paramDecl);
					
					//System.Windows.Forms.MessageBox.Show("Adding parameter: " + param.Name + " | " + param.Type.ToString());
					CodeTypeReference typeRef = this.convertTypeRef(param.Type);
					if( typeRef != null )
						paramDecl.Type = typeRef;
					method.Parameters.Add(paramDecl);
				}
						
				// set the return type...
				if( function.Type.TypeKind == EnvDTE.vsCMTypeRef.vsCMTypeRefArray )
				{
					// now the hard part, set the return type
					string type = function.Type.AsString;

					int rank = (type.Length - type.IndexOf("["))/2;
					type = type.Substring(0, type.Length-(rank*2));
					
					string newType = this.getFulltypeForValuetype(type);
					if( newType == null )
						newType = type;
					Type t = Type.GetType(newType);
					if( t != null )
					{
						CodeTypeReference returnRef = new CodeTypeReference(t.FullName, rank);
						method.ReturnType = returnRef;
					} 
					else
					{
						CodeTypeReference returnRef = new CodeTypeReference(newType, rank);
						method.ReturnType = returnRef;
					}
				}
				else if( function.Type.TypeKind == EnvDTE.vsCMTypeRef.vsCMTypeRefVoid )
				{
					// void method, no returntype or returnstatement
				}
				else
				{
					//System.Windows.Forms.MessageBox.Show("Non-array type: " + function.Type.AsFullName);
					Type type = Type.GetType(function.Type.AsFullName);
					if( type == null )
					{
						CodeTypeReference returnRef = new CodeTypeReference(function.Type.AsFullName);
						method.ReturnType = returnRef;
					}
					else
					{
						CodeTypeReference returnRef = new CodeTypeReference(type);
						//System.Windows.Forms.MessageBox.Show("Non-array type: " + type.FullName);
						
						// set the returntype
						method.ReturnType = returnRef;
						// select the right return statement
					}
				}
				typeDeclaration.Members.Add(method);
			}
		}

		public CodeCompileUnit getCompileUnit() 
		{
			return compileUnit;
		}
	
		private string getFulltypeForValuetype(string type)
		{
			string fullType = "";
			
			/**
			 * Special case: remove [ character
			 * Occurs with multi-even-dimensional arrays 
			 */
			string reg = @"[\[]";
			type  = Regex.Replace(type,reg,"");
			
			switch (type) 
			{
				case "boolean":		// J# uses boolean instead of bool
					fullType = "System.Boolean";
					break;
				case "bool":
					fullType = "System.Boolean";
					break;
				case "char":
					fullType = "System.Char";					
					break;
				case "float":
					fullType = "System.Single";
					break;
				case "double":
					fullType = "System.Double";
					break;
				case "long":
					fullType = "System.Int64";
					break;
				case "int":
					fullType = "System.Int32";
					break;
				case "short":
					fullType = "System.Int16";
					break;
				case "single":
					fullType = "System.Single";
					break;
				case "string":
					fullType = "System.String";
					break;
				case "String":
					fullType = "System.String";
					break;
				case "byte":
					fullType = "System.Byte";
					break;
				case "ubyte":
					fullType = "System.UByte";
					break;
				case "uint":
					fullType = "System.UInt32";
					break;
				case "ushort":
					fullType = "System.UInt16";
					break;
				case "ulong":
					fullType = "System.UInt64";
					break;
				case "object":
					fullType = "System.Object";
					break;
				default:
					return null;
			}

			return fullType;
		}



	}

}
