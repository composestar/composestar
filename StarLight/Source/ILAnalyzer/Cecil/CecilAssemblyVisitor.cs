#region Using directives
using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Diagnostics;
using System.Globalization;
using System.IO;
using System.Reflection;
using System.Text;
using System.Security.Policy;  //for evidence object
using System.Diagnostics.CodeAnalysis;
 
using Mono.Cecil;
using Mono.Cecil.Binary;
using Mono.Cecil.Cil;
using Mono.Cecil.Metadata;
using Mono.Cecil.Signatures;

using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions;
using Composestar.StarLight.Entities.Configuration;

using Composestar.StarLight.Filters.FilterTypes;
#endregion

namespace Composestar.StarLight.ILAnalyzer
{
    /// <summary>
    /// Visitor to visit all the relevant assembly items and returns an <see cref="AssemblyElement"></see>.
    /// </summary>
    [CLSCompliant(false)]
    public class CecilAssemblyVisitor : BaseReflectionVisitor
    {

        #region Constants

        private const string ModuleName = "<Module>";

        #endregion

        #region Private variables

        private AssemblyElement _assemblyElement = new AssemblyElement();
        private AssemblyDefinition _assembly;

        private IList<String> _resolvedAssemblies = new List<String>();
        private IList<String> _unresolvedAssemblies = new List<String>();
        private IList<String> _cachedTypes = new List<String>();
        private IList<String> _resolvedTypes = new List<String>();
        private List<String> _unresolvedTypes = new List<String>();

        private bool _saveType;
        private bool _saveInnerType;
        private bool _processMethodBody = true;
        private bool _processAttributes;
        private bool _includeFields = true;
        private bool _extractUnresolvedOnly;

        private IList<FilterTypeElement> _filterTypes = new List<FilterTypeElement>();
        private IList<FilterActionElement> _filterActions = new List<FilterActionElement>();

        private TypeElement _currentType;
        #endregion

        #region Properties

        /// <summary>
        /// Gets or sets a value indicating whether [extract unresolved only].
        /// </summary>
        /// <value>
        /// 	<c>true</c> if [extract unresolved only]; otherwise, <c>false</c>.
        /// </value>
        public bool ExtractUnresolvedOnly
        {
            get
            {
                return _extractUnresolvedOnly;
            }
            set
            {
                _extractUnresolvedOnly = value;
            }
        }

        /// <summary>
        /// Gets or sets the resolved types.
        /// </summary>
        /// <value>The resolved types.</value>
        public IList<String> ResolvedTypes
        {
            get
            {
                return _resolvedTypes;
            }
            set
            {
                _resolvedTypes = value;
            } // set
        }

        /// <summary>
        /// Gets or sets the unresolved types.
        /// </summary>
        /// <value>The unresolved types.</value>
        [SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
        public List<String> UnresolvedTypes
        {
            get
            {
                return _unresolvedTypes;
            }
            set
            {
                _unresolvedTypes = value;
            }
        }

        /// <summary>
        /// Gets or sets the filter actions.
        /// </summary>
        /// <value>The filter actions.</value>
        public IList<FilterActionElement> FilterActions
        {
            get
            {
                return _filterActions;
            }         
        }

        /// <summary>
        /// Gets or sets the filter types.
        /// </summary>
        /// <value>The filter types.</value>
        public IList<FilterTypeElement> FilterTypes
        {
            get
            {
                return _filterTypes;
            }           
        }

        /// <summary>
        /// Gets or sets a value indicating whether [include fields].
        /// </summary>
        /// <value><c>true</c> if [include fields]; otherwise, <c>false</c>.</value>
        public bool IncludeFields
        {
            get
            {
                return _includeFields;
            }
            set
            {
                _includeFields = value;
            }
        }

        /// <summary>
        /// Gets or sets a value indicating whether [process attributes].
        /// </summary>
        /// <value><c>true</c> if [process attributes]; otherwise, <c>false</c>.</value>
        public bool ProcessAttributes
        {
            get
            {
                return _processAttributes;
            }
            set
            {
                _processAttributes = value;
            }
        }

        /// <summary>
        /// Gets or sets a value indicating whether [process method body].
        /// </summary>
        /// <value><c>true</c> if [process method body]; otherwise, <c>false</c>.</value>
        public bool ProcessMethodBody
        {
            get
            {
                return _processMethodBody;
            }
            set
            {
                _processMethodBody = value;
            }
        }

        /// <summary>
        /// Gets or sets a value indicating whether [save inner type].
        /// </summary>
        /// <value><c>true</c> if [save inner type]; otherwise, <c>false</c>.</value>
        public bool SaveInnerType
        {
            get
            {
                return _saveInnerType;
            }
            set
            {
                _saveInnerType = value;
            }
        }

        /// <summary>
        /// Gets or sets a value indicating whether [save type].
        /// </summary>
        /// <value><c>true</c> if [save type]; otherwise, <c>false</c>.</value>
        public bool SaveType
        {
            get
            {
                return _saveType;
            }
            set
            {
                _saveType = value;
            }
        }

        /// <summary>
        /// Gets or sets the cached types.
        /// </summary>
        /// <value>The cached types.</value>
        public IList<String> CachedTypes
        {
            get
            {
                return _cachedTypes;
            }
            set
            {
                _cachedTypes = value;
            }
        }

        /// <summary>
        /// Gets or sets the unresolved assemblies.
        /// </summary>
        /// <value>The unresolved assemblies.</value>
        public IList<String> UnresolvedAssemblies
        {
            get
            {
                return _unresolvedAssemblies;
            }
            set
            {
                _unresolvedAssemblies = value;
            }
        }

        /// <summary>
        /// Gets or sets the resolved assemblies.
        /// </summary>
        /// <value>The resolved assemblies.</value>
        public IList<String> ResolvedAssemblies
        {
            get
            {
                return _resolvedAssemblies;
            }
            set
            {
                _resolvedAssemblies = value;
            }
        }

        #endregion

        #region Filter Type Naming

        private string _filterTypeName = typeof(Composestar.StarLight.Filters.FilterTypes.FilterType).FullName;
        private string _filterActionName = typeof(Composestar.StarLight.Filters.FilterTypes.FilterAction).FullName;

        #endregion

        #region Analyze Function

        /// <summary>
        /// Analyze the specified assembly filename.
        /// </summary>
        /// <param name="assemblyFilename">The assembly filename.</param>
        /// <returns></returns>
        public AssemblyElement Analyze(string assemblyFileName)
        {

            try
            {
                // Load assembly for processing by Mono.Cecil
                _assembly = AssemblyFactory.GetAssembly(assemblyFileName);
            }
            catch (EndOfStreamException)
            {
                throw new BadImageFormatException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.ImageIsBad, assemblyFileName));
            }

            AddResolvedAssemblyList(_assembly.Name.ToString());
   
            // Fill the assemblyElement values
            _assemblyElement.Name = _assembly.Name.ToString();
            _assemblyElement.FileName = assemblyFileName;

            // Get all the types defined in the main module. Typically you won't need
            // to worry that your assembly contains more than one module
            foreach (TypeDefinition type in _assembly.MainModule.Types)
            {
                if (type.Name.Equals(ModuleName))
                    continue;
 
                type.Accept(this);
            }

            // Remove types without a proper assembly name, e.g. the generic identifiers T, V, K, TKey, TValue, TOutput, TItem
            IList<String> unresolvedtypes = new List<String>(UnresolvedTypes);
            foreach (String type in unresolvedtypes)
            {
                if (type.EndsWith(", NULL"))
                {
                    UnresolvedTypes.Remove(type);
                }
            }
                      
            // Return the assembly element
            return _assemblyElement;
        }

        #endregion

        #region Visitor Implementation

        /// <summary>
        /// Visits the type definition.
        /// </summary>
        /// <param name="type">The type.</param>
        [CLSCompliant(false)]
        public override void VisitTypeDefinition(TypeDefinition type)
        {
            // Create a new typeElement
            TypeElement typeElement = new TypeElement();

            //
            // Fill the properties
            //

            // Name
            typeElement.Name = type.Name;
            
            if (string.IsNullOrEmpty(type.Namespace))
            {
                if (type.DeclaringType != null)
                   typeElement.Namespace = string.Concat(type.DeclaringType.FullName, "/");
                
            } 
            else
                typeElement.Namespace = type.Namespace;
                        
            // Properties
            typeElement.IsAbstract = type.IsAbstract;
            typeElement.IsEnum = type.IsEnum;
            typeElement.IsInterface = type.IsInterface;
            typeElement.IsSealed = type.IsSealed;
            typeElement.IsValueType = type.IsValueType;
            typeElement.IsClass = !type.IsValueType & !type.IsInterface;
            typeElement.IsNotPublic = type.Attributes == Mono.Cecil.TypeAttributes.NotPublic;
            typeElement.IsPrimitive = false;
            typeElement.IsPublic = type.Attributes == Mono.Cecil.TypeAttributes.Public;
            typeElement.IsSerializable = type.Attributes == Mono.Cecil.TypeAttributes.Serializable;

            // Interface
            foreach (TypeReference interfaceDef in type.Interfaces)
            {
                if (string.IsNullOrEmpty(typeElement.ImplementedInterfaces))
                    typeElement.ImplementedInterfaces = interfaceDef.FullName;
                else
                    typeElement.ImplementedInterfaces = String.Format(CultureInfo.CurrentCulture,  "{0};{1}", typeElement.ImplementedInterfaces, interfaceDef.FullName);
            }

            // Basetype
            if (type.BaseType != null)
            {
                typeElement.BaseType = type.BaseType.FullName;

                AddUnresolvedAssemblyList(type.BaseType);
                
                // Check whether type is a FilterType:
                if (type.BaseType.FullName.Equals(_filterTypeName))
                {
                    ExtractFilterType(type);
                }

                // Check whether type is a FilterAction:
                if (type.BaseType.FullName.Equals(_filterActionName))
                {
                    ExtractFilterAction(type);
                }

                // We may need the base class
                AddUnresolvedType(type.BaseType); 

            }

            if (!ExtractUnresolvedOnly || (ExtractUnresolvedOnly && _unresolvedTypes.Contains(CreateTypeName(type))))
            {
                // Get custom attributes
                typeElement.Attributes.AddRange(ExtractCustomAttributes(type.CustomAttributes));
                
                _currentType = typeElement;
                // Visit methods
                foreach (MethodDefinition method in type.Methods)
                {
                    method.Accept(this);
                }

                // Visit fields
                foreach (FieldDefinition field in type.Fields)
                {
                    field.Accept(this);
                }

                _assemblyElement.Types.Add(_currentType);

                // Add this type to the resolved types
                AddResolvedType(type);

                // Remove from unresolved
                _unresolvedTypes.Remove(CreateTypeName(type));
            }
        }

       
        /// <summary>
        /// Visits the method definition.
        /// </summary>
        /// <param name="method">The method.</param>
        [CLSCompliant(false)]
        public override void VisitMethodDefinition(MethodDefinition method)
        {

            // If we only extract the unresolvedtypes then we most likely are only interested
            // in methods which can be overriden. So skip the rest.
            if (ExtractUnresolvedOnly && !method.IsVirtual)
                return;

            // Create a new method element
            MethodElement me = new MethodElement();
            me.Signature = method.ToString();
            me.Name = method.Name;
            me.ReturnType = method.ReturnType.ReturnType.FullName;

            // If the return type has not yet been resolved, add it to the list of unresolved types   
           // if (!method.ReturnType.ReturnType.FullName.Equals("System.Void")  )
           //     AddUnresolvedAssemblyList(method.ReturnType.ReturnType);

            me.IsAbstract = method.IsAbstract;
            me.IsConstructor = method.IsConstructor;
            me.IsPrivate = method.Attributes == Mono.Cecil.MethodAttributes.Private;
            me.IsPublic = method.Attributes == Mono.Cecil.MethodAttributes.Public;
            me.IsStatic = method.IsStatic;
            me.IsVirtual = method.IsVirtual;

            // Add the parameters
            foreach (ParameterDefinition param in method.Parameters)
            {
                ParameterElement pe = new ParameterElement();

                pe.Name = param.Name;
                pe.Ordinal = (short)(param.Sequence);
                pe.Type = param.ParameterType.FullName;

                if ((param.Attributes & Mono.Cecil.ParameterAttributes.Out) != Mono.Cecil.ParameterAttributes.Out)
                    pe.ParameterOption = pe.ParameterOption | ParameterOptions.In;
                else
                    pe.ParameterOption &= ~ParameterOptions.In;
                if ((param.Attributes & Mono.Cecil.ParameterAttributes.Out) == Mono.Cecil.ParameterAttributes.Out) 
                    pe.ParameterOption = pe.ParameterOption | ParameterOptions.Out;
                if ((param.Attributes & Mono.Cecil.ParameterAttributes.Optional) == Mono.Cecil.ParameterAttributes.Optional) 
                    pe.ParameterOption = pe.ParameterOption | ParameterOptions.Optional;
   
                me.Parameters.Add(pe);
            }

            // Add the method body
            if (ProcessMethodBody && method.HasBody)
            {
                me.Body = new Entities.LanguageModel.MethodBody();

                List<String> callList = new List<string>();

                foreach (Instruction instr in method.Body.Instructions)
                {
                    if (instr.OpCode.Value == OpCodes.Call.Value ||
                        instr.OpCode.Value == OpCodes.Calli.Value ||
                        instr.OpCode.Value == OpCodes.Callvirt.Value
                        )
                    {
                        CallElement ce = new CallElement();
                        
                        // instr.Operand can be a MethodReference or a CallSite
                        ce.MethodReference = instr.Operand.ToString();

                        if (!callList.Contains(ce.MethodReference))
                        {
                            me.Body.Calls.Add(ce);
                            callList.Add(ce.MethodReference);
                        }
                    }
                }

            }

            // Custom attributes
            me.Attributes.AddRange(ExtractCustomAttributes(method.CustomAttributes));

            // Add to the type
            _currentType.Methods.Add(me);
        }


      
        /// <summary>
        /// Visits the field definition.
        /// </summary>
        /// <param name="field">The field.</param>
        [CLSCompliant(false)]
        public override void VisitFieldDefinition(FieldDefinition field)
        {
            if (!_includeFields) return;

            FieldElement fe = new FieldElement();

            fe.Name = field.Name;
            fe.Type = field.FieldType.FullName;

            fe.IsPrivate = field.Attributes == Mono.Cecil.FieldAttributes.Private;
            fe.IsPublic = field.Attributes == Mono.Cecil.FieldAttributes.Public;
            fe.IsStatic = field.IsStatic;

            //AddUnresolvedAssemblyList(field.FieldType);

            _currentType.Fields.Add(fe);
        }
           
        #endregion

        #region Filter Actions

        /// <summary>
        /// Extracts the filter action.
        /// </summary>
        /// <param name="type">The type.</param>       
        private void ExtractFilterAction(TypeDefinition type)
        {
            // We use .NET reflection here, because Cecil can not read the enumerations

            // TODO Loading an assembly locks the assembly for the duration of the appdomain.
            // The weaver can no loner access the file to weave in it.
            // We now use ShadowCopy to overcome this. It is an obsolute method
            // Switch to a seperate appdomain.

            SetupReflectionAssembly(type.Module.Image.FileInformation.Directory.FullName);

            Assembly assembly = Assembly.LoadFrom(type.Module.Image.FileInformation.FullName);
            
            if (assembly == null)
            {
                throw new ILAnalyzerException(String.Format(CultureInfo.CurrentCulture,  Properties.Resources.CouldNotFindAssembly, type.Module.Image.FileInformation.FullName));
            } // if

            Type refType = assembly.GetType(type.FullName);
           
            if (refType == null)
            {
                throw new ILAnalyzerException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.CouldNotFindType, type.FullName));
            } // if

            FilterActionAttribute[] faas = (FilterActionAttribute[])refType.GetCustomAttributes(typeof(FilterActionAttribute), true);
            foreach (FilterActionAttribute faa in faas)
            {
                FilterActionElement faEl = new FilterActionElement();

                faEl.FullName = type.FullName;
                faEl.Name = faa.ActionName;
                faEl.Assembly = type.Module.Assembly.Name.ToString();

                switch (faa.FlowBehavior)
                {
                    case FilterActionAttribute.FilterFlowBehavior.Continue:
                        faEl.FlowBehavior = FilterActionElement.FlowContinue;
                        break;
                    case FilterActionAttribute.FilterFlowBehavior.Exit:
                        faEl.FlowBehavior = FilterActionElement.FlowExit;
                        break;
                    case FilterActionAttribute.FilterFlowBehavior.Return:
                        faEl.FlowBehavior = FilterActionElement.FlowReturn;
                        break;
                    default:
                        faEl.FlowBehavior = FilterActionElement.FlowContinue;
                        break;
                } // switch

                switch (faa.SubstitutionBehavior )
                {
                    case FilterActionAttribute.MessageSubstitutionBehavior.Original:
                        faEl.MessageChangeBehavior = FilterActionElement.MessageOriginal;
                        break;
                    case FilterActionAttribute.MessageSubstitutionBehavior.Substituted:
                        faEl.MessageChangeBehavior = FilterActionElement.MessageSubstituted;
                        break;
                    case FilterActionAttribute.MessageSubstitutionBehavior.Any:
                        faEl.MessageChangeBehavior = FilterActionElement.MessageAny;
                        break;
                    default:
                        faEl.MessageChangeBehavior = FilterActionElement.MessageOriginal;
                        break;
                } // switch

                faEl.CreateJPC = faa.CreateJoinPointContext; 

                _filterActions.Add(faEl);
            } // ExtractFilterAction(type)
                      
            type = null;
            assembly = null;

            return;
        }

        private String m_rootAssembly;

        /// <summary>
        /// Mies the reflection only resolve event handler.
        /// </summary>
        /// <param name="sender">The sender.</param>
        /// <param name="args">The <see cref="T:System.ResolveEventArgs"/> instance containing the event data.</param>
        /// <returns></returns>
        private Assembly MyReflectionOnlyResolveEventHandler(object sender, ResolveEventArgs args)
        {

            AssemblyName name = new AssemblyName(args.Name);

            String asmToCheck = Path.GetDirectoryName(m_rootAssembly) + "\\" + name.Name + ".dll";

            if (File.Exists(asmToCheck))
            {
                return Assembly.ReflectionOnlyLoadFrom(asmToCheck);
            }

            return Assembly.ReflectionOnlyLoad(args.Name);
        }

        private bool _reflectionAssemblySetup;

        /// <summary>
        /// Setups the reflection assembly.
        /// </summary>
        /// <param name="rootPath">The root path.</param>
        [SuppressMessage("Microsoft.Performance", "CA1804:RemoveUnusedLocals", Target="assmContext" )]
        [SuppressMessage("Microsoft.Performance", "CA1804:RemoveUnusedLocals", Target="assmFilters")]
        private void SetupReflectionAssembly(string rootPath)
        {
            if (_reflectionAssemblySetup) return;

            AppDomain.CurrentDomain.SetShadowCopyFiles();
            AppDomain.CurrentDomain.AppendPrivatePath(rootPath);
            m_rootAssembly = rootPath;
            AppDomain.CurrentDomain.ReflectionOnlyAssemblyResolve += new ResolveEventHandler(MyReflectionOnlyResolveEventHandler);

            // We have to inject the ContextInfo into the domain, or we cannot find the specific type.
            Assembly assmContext =
                Assembly.ReflectionOnlyLoadFrom(Path.Combine(rootPath, "Composestar.StarLight.ContextInfo.dll"));
            Assembly assmFilters =
              Assembly.ReflectionOnlyLoadFrom(Path.Combine(rootPath, "Composestar.StarLight.Filters.dll"));

            _reflectionAssemblySetup = true;

        } 

        /// <summary>
        /// Extracts the type of the filter.
        /// </summary>
        /// <param name="type">The type.</param>
        private void ExtractFilterType(TypeDefinition type)
        {
            // We use .NET reflection here, because Cecil can not read the enumerations

            // TODO Loading an assembly locks the assembly for the duration of the appdomain.
            // The weaver can no loner access the file to weave in it.
            // We now use ShadowCopy to overcome this. It is an obsolute method
            // Switch to a seperate appdomain.

            SetupReflectionAssembly(type.Module.Image.FileInformation.Directory.FullName);          

            Assembly assembly = Assembly.LoadFrom(type.Module.Image.FileInformation.FullName);
           

            if (assembly == null)
            {
                throw new ILAnalyzerException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.CouldNotFindAssembly, type.Module.Image.FileInformation.FullName));
            } // if

            Type refType = assembly.GetType(type.FullName);

            if (refType == null)
            {
                throw new ILAnalyzerException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.CouldNotFindType, type.FullName));
            } // if

            FilterTypeAttribute[] ftas = (FilterTypeAttribute[]) refType.GetCustomAttributes(typeof(FilterTypeAttribute), true);
            foreach (FilterTypeAttribute fta in ftas)
            {
                     FilterTypeElement ftEl = new FilterTypeElement();
                    _filterTypes.Add(ftEl);

                    ftEl.Name = fta.Name;
                    ftEl.AcceptCallAction = fta.AcceptCallAction;
                    ftEl.RejectCallAction = fta.RejectCallAction;
                    ftEl.AcceptReturnAction = fta.AcceptReturnAction;
                    ftEl.RejectReturnAction = fta.RejectReturnAction;

            } // foreach  (fta)

            // Using .NET reflection, because otherelse we cannot resolve the types used in the 
            // FilterTypeAttribute constructor.

            //foreach (CustomAttribute attr in type.CustomAttributes)
            //{
            //    if (attr.Constructor.DeclaringType.FullName.Equals(_filterTypeAnnotationName))
            //    {
            //        FilterTypeElement ftEl = new FilterTypeElement();
            //        _filterTypes.Add(ftEl);

            //        ftEl.Name = (String)attr.ConstructorParameters[0];
            //        ftEl.AcceptCallAction = (String)attr.ConstructorParameters[1];
            //        ftEl.RejectCallAction = (String)attr.ConstructorParameters[2];
            //        ftEl.AcceptReturnAction = (String)attr.ConstructorParameters[3];
            //        ftEl.RejectReturnAction = (String)attr.ConstructorParameters[4];

            //        return;
            //    }
            //}
        }

        #endregion

        #region Helper Functions

        #region Resolve and unresolved functions

        /// <summary>
        /// Adds the assembly to the resolved list.
        /// </summary>
        /// <param name="assemblyName">Assembly name</param>
        private void AddResolvedAssemblyList(string assemblyName)
        {
            // Add to resolved
            if (!_resolvedAssemblies.Contains(assemblyName))
                _resolvedAssemblies.Add(assemblyName);

            // remove from unresolved
            _unresolvedAssemblies.Remove(assemblyName);

        } // UpdateAssemblyList(assemblyName)

        /// <summary>
        /// Adds the assembly to the unresolved list.
        /// </summary>
        /// <param name="assemblyName">Name of the assembly.</param>
        private void AddUnresolvedAssemblyList(string assemblyName)
        {
            // Check if the assembly is not yet resolved.
            if (!_resolvedAssemblies.Contains(assemblyName))
            {
                if (!_unresolvedAssemblies.Contains(assemblyName))
                {
                    _unresolvedAssemblies.Add(assemblyName);
                } // if
            } // if

        }

        /// <summary>
        /// Adds the assembly to the unresolved list.
        /// </summary>
        /// <param name="typeReference">The type reference.</param>
        private void AddUnresolvedAssemblyList(TypeReference typeReference)
        {
            if (typeReference == null)
                return;

            if (typeReference.Scope != null)
            {
                // Locally declared type
                if (typeReference.Scope is ModuleDefinition)
                {
                    if (((ModuleDefinition)typeReference.Scope).Assembly != null)
                    {
                        AddUnresolvedAssemblyList(((ModuleDefinition)typeReference.Scope).Assembly.Name.FullName);
                    }
                }

                // Referenced type
                foreach (AssemblyNameReference assembly in _assembly.MainModule.AssemblyReferences)
                {
                    if (typeReference.Scope.Name == assembly.Name)
                    {
                        AddUnresolvedAssemblyList(assembly.FullName);
                    }
                }
            }
        }

        /// <summary>
        /// Adds the type of the resolved.
        /// </summary>
        /// <param name="typeName">Name of the type.</param>
        private void AddResolvedType(string typeName)
        {
            // Add to resolved
            if (!_resolvedTypes.Contains(typeName))
                _resolvedTypes.Add(typeName);

            // remove from unresolved
            _unresolvedTypes.Remove(typeName);

        }

        /// <summary>
        /// Adds the type of the unresolved.
        /// </summary>
        /// <param name="typeName">Name of the type.</param>
        private void AddUnresolvedType(string typeName)
        {
            // Check if the assembly is not yet resolved.
            if (!_resolvedTypes.Contains(typeName))
            {
                if (!_unresolvedTypes.Contains(typeName))
                {
                    _unresolvedTypes.Add(typeName);
                } // if
            } // if

        }

        /// <summary>
        /// Adds the type of the unresolved.
        /// </summary>
        /// <param name="type">The type.</param>
        private void AddUnresolvedType(TypeReference type)
        {
            AddUnresolvedType(CreateTypeName(type));
        } // AddUnresolvedType(type)

        /// <summary>
        /// Adds the type of the resolved.
        /// </summary>
        /// <param name="type">The type.</param>
        private void AddResolvedType(TypeReference type)
        {
            AddResolvedType(CreateTypeName(type));
        }

        #endregion

        /// <summary>
        /// Visits the custom attributes.
        /// </summary>
        /// <param name="customAttributes">The custom attributes.</param>
        /// <returns></returns>
        private static List<AttributeElement> ExtractCustomAttributes(CustomAttributeCollection customAttributes)
        {
            List<AttributeElement> ret = new List<AttributeElement>();

            if (customAttributes == null || customAttributes.Count == 0)
                return ret;

            foreach (CustomAttribute  ca in customAttributes)
            {
                AttributeElement ae = new AttributeElement();
                if (ca.Constructor.DeclaringType == null)
                    continue;
 
                ae.AttributeType = ca.Constructor.DeclaringType.ToString();

                for (int i = 0; i < ca.ConstructorParameters.Count; i++)
                {
                    AttributeValueElement ave = new AttributeValueElement();
                    ave.Name = ca.Constructor.Parameters[i].Name;
                    if (ca.ConstructorParameters[i] == null)
                        ave.Value = null;
                    else
                        ave.Value = ca.ConstructorParameters[i].ToString(); 
                    ae.Values.Add(ave);  
                }

                foreach (object propKey in ca.Properties.Keys)
                {
                    AttributeValueElement ave = new AttributeValueElement();
                    ave.Name = Convert.ToString(propKey, CultureInfo.InvariantCulture);
                    if (ca.Properties[propKey] == null)
                        ave.Value = null;
                    else
                        ave.Value = ca.Properties[propKey].ToString();
                    ae.Values.Add(ave);
                }

                ret.Add(ae);
            }

            return ret;
        }

        /// <summary>
        /// Creates the name of the type.
        /// </summary>
        /// <param name="type">The type.</param>
        /// <returns></returns>
        [SuppressMessage("Microsoft.Performance", "CA1811:AvoidUncalledPrivateCode")]
        private static string CreateTypeName(TypeReference type)
        {
            String typeName = type.FullName;

            if (typeName.Contains("`")) typeName = String.Format(CultureInfo.CurrentCulture, "{0}.{1}", type.Namespace, type.Name);
            if (typeName.EndsWith("&")) typeName = typeName.Substring(0, typeName.Length - 1);
            if (typeName.EndsWith("**")) typeName = typeName.Substring(0, typeName.Length - 2);
            if (typeName.EndsWith("*")) typeName = typeName.Substring(0, typeName.Length - 1);
            if (typeName.Contains("[")) typeName = typeName.Substring(0, typeName.IndexOf("[", 0));
            if (typeName.Contains(" modreq(")) typeName = typeName.Substring(0, typeName.IndexOf(" modreq(", 0));
            if (typeName.Contains(" modopt(")) typeName = typeName.Substring(0, typeName.IndexOf(" modopt(", 0));

            return typeName;
        }

        /// <summary>
        /// Creates the type Assembly Fully Qualified Name.
        /// </summary>
        /// <param name="targetAssemblyDefinition">The target assembly definition.</param>
        /// <param name="type">The type.</param>
        /// <returns></returns>
        [SuppressMessage("Microsoft.Performance", "CA1811:AvoidUncalledPrivateCode")]
        private static string CreateTypeAFQN(AssemblyDefinition targetAssemblyDefinition, TypeReference type)
        {
            return String.Format(CultureInfo.CurrentCulture, "{0}, {1}", CreateTypeName(type), CreateAFQN(targetAssemblyDefinition, type));
        }

        /// <summary>
        /// Creates the Assembly Fully Qualified Name.
        /// </summary>
        /// <param name="targetAssemblyDefinition">The target assembly definition.</param>
        /// <param name="type">The type.</param>
        /// <returns></returns>
        [SuppressMessage("Microsoft.Performance", "CA1811:AvoidUncalledPrivateCode")]
        private static String CreateAFQN(AssemblyDefinition targetAssemblyDefinition, TypeReference type)
        {
            if (targetAssemblyDefinition == null)
                throw new ArgumentNullException("targetAssemblyDefinition");

            if (type == null)
                throw new ArgumentNullException("type");


            if (type.Scope != null)
            {
                // Locally declared type
                if (type.Scope is ModuleDefinition)
                {
                    if (((ModuleDefinition)type.Scope).Assembly != null)
                    {
                        return ((ModuleDefinition)type.Scope).Assembly.Name.FullName;
                    }
                }

                // Referenced type
                foreach (AssemblyNameReference assembly in targetAssemblyDefinition.MainModule.AssemblyReferences)
                {
                    if (type.Scope.Name == assembly.Name)
                    {
                        return assembly.FullName;
                    }
                }
            }

            return "NULL";
        }

        #endregion

    }
}
