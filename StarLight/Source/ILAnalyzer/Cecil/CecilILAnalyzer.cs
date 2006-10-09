using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Diagnostics;
using System.IO;
using System.Reflection;
using System.Text;

using Mono.Cecil;
using Mono.Cecil.Binary;
using Mono.Cecil.Cil;
using Mono.Cecil.Metadata;
using Mono.Cecil.Signatures;

using Composestar.Repository;
using Composestar.Repository.LanguageModel;
using Composestar.StarLight.CoreServices;

using Composestar.StarLight.ContextInfo.FilterTypes;

namespace Composestar.StarLight.ILAnalyzer
{
    /// <summary>
    /// An implementation of the IILAnalyzer working with Cecil.
    /// </summary>
    public class CecilILAnalyzer : IILAnalyzer
    {

        #region Constants

        private const string ModuleName = "<Module>";

        #endregion

        #region Private Variables

        private bool _isInitialized = false;
        private TimeSpan _lastDuration = TimeSpan.Zero;
        private List<String> _resolvedTypes = new List<String>();
        private List<String> _unresolvedTypes = new List<String>();
        private List<String> _cachedTypes = new List<String>();
        private bool _saveType = false;
        private bool _saveInnerType = false;
        private bool _processMethodBody = true;
        private bool _processAttributes = false;

        private CecilAnalyzerConfiguration _configuration;
        private ILanguageModelAccessor _languageModelAccessor;

        private string _filterTypeName = typeof(Composestar.StarLight.ContextInfo.FilterTypes.FilterType).FullName;
        private string _filterTypeAnnotationName = typeof(Composestar.StarLight.ContextInfo.FilterTypes.FilterTypeAttribute).FullName;
        private string _filterActionName = typeof(Composestar.StarLight.ContextInfo.FilterTypes.FilterAction).FullName;
        private string _filterActionAnnotationName = typeof(Composestar.StarLight.ContextInfo.FilterTypes.FilterActionAttribute).FullName;

        #endregion

        #region ctor

        /// <summary>
        /// Initializes a new instance of the <see cref="T:CecilILAnalyzer"/> class.
        /// </summary>
        /// <param name="configuration">The configuration.</param>
        /// <param name="languageModelAccessor">The language model accessor.</param>
        public CecilILAnalyzer(CecilAnalyzerConfiguration configuration, ILanguageModelAccessor languageModelAccessor)
        {
            #region Check for null values

            if (configuration == null) throw new ArgumentNullException("configuration");
     
            #endregion

            _configuration = configuration;
            _languageModelAccessor = languageModelAccessor;
        }

        #endregion

        #region Properties

        /// <summary>
        /// Gets the unresolved types.
        /// </summary>
        /// <value>The unresolved types.</value>
        public List<String> UnresolvedTypes
        {
            get { return _unresolvedTypes; }
        }

        /// <summary>
        /// Gets the resolved types.
        /// </summary>
        /// <value>The resolved types.</value>
        public List<String> ResolvedTypes
        {
            get { return _resolvedTypes; }
        }

        /// <summary>
        /// Gets the cached types.
        /// </summary>
        /// <value>The cached types.</value>
        public List<String> CachedTypes
        {
            get { return _cachedTypes; }
        }

        #endregion

        #region Helper Functions

        /// <summary>
        /// Extracts the attribute elements.
        /// </summary>
        /// <param name="parent">The parent.</param>
        /// <param name="attributes">The attributes.</param>
        private void ExtractAttributeElements(IRepositoryElement parent, CustomAttributeCollection attributes)
        {
            if (!this._processAttributes) return;

            foreach (CustomAttribute attr in attributes)
            {
                AttributeElement ae = new AttributeElement();
                ae.Type = attr.Constructor.Name;
                if (attr.ConstructorParameters.Count > 0)
                {
                    ae.Value = attr.ConstructorParameters[0].ToString();
                }
            }
        }

        /// <summary>
        /// Creates the Assembly Fully Qualified Name.
        /// </summary>
        /// <param name="targetAssemblyDefinition">The target assembly definition.</param>
        /// <param name="type">The type.</param>
        /// <returns></returns>
        private String CreateAFQN(AssemblyDefinition targetAssemblyDefinition, TypeReference type)
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

        /// <summary>
        /// Creates the name of the type.
        /// </summary>
        /// <param name="type">The type.</param>
        /// <returns></returns>
        private string CreateTypeName(TypeReference type)
        {
            String typeName = type.FullName;

            if (typeName.Contains("`")) typeName = String.Format("{0}.{1}", type.Namespace, type.Name);
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
        private String CreateTypeAFQN(AssemblyDefinition targetAssemblyDefinition, TypeReference type)
        {
            return String.Format("{0}, {1}", CreateTypeName(type), CreateAFQN(targetAssemblyDefinition, type));
        }



        /// <summary>
        /// Gets the parameter types list.
        /// </summary>
        /// <param name="method">The method.</param>
        /// <returns></returns>
        private String[] GetParameterTypesList(MethodDefinition method)
        {
            List<String> ret = new List<String>();
            foreach (ParameterDefinition param in method.Parameters)
            {
                ret.Add(param.ParameterType.FullName);
            }

            return ret.ToArray();
        }
        #endregion

        #region Extract type information




        /// <summary>
        /// Extracts all types.
        /// </summary>
        /// <param name="targetAssemblyDefinition">The target assembly definition.</param>
        /// <param name="fileName">Name of the file.</param>
        /// <returns></returns>
        private AssemblyElement ExtractAllTypes(AssemblyDefinition targetAssemblyDefinition, String fileName)
        {
            AssemblyElement ae = new AssemblyElement();

            ae.Name = targetAssemblyDefinition.Name.FullName;
            ae.FileName = fileName;
            ae.Timestamp = File.GetLastWriteTime(fileName).Ticks;

            ae.TypeElements = ExtractTypes(targetAssemblyDefinition, ae, targetAssemblyDefinition.MainModule.Types);

            return ae;
        }





        /// <summary>
        /// Extracts the field.
        /// </summary>
        /// <param name="targetAssemblyDefinition">The target assembly definition.</param>
        /// <param name="field">The field.</param>
        /// <returns></returns>
        private FieldElement ExtractField(AssemblyDefinition targetAssemblyDefinition, FieldDefinition field)
        {
            FieldElement fe = new FieldElement(System.Guid.NewGuid().ToString());

            fe.Name = field.Name;
            fe.Type = field.FieldType.FullName;

            fe.IsPrivate = field.Attributes == Mono.Cecil.FieldAttributes.Private;
            fe.IsPublic = field.Attributes == Mono.Cecil.FieldAttributes.Public;
            fe.IsStatic = field.IsStatic;

            // If the field type has not yet been resolved, add it to the list of unresolved types
            //String fieldTypeAFQN = CreateTypeAFQN(_targetAssemblyDefinition, field.FieldType);
            //if (this._saveInnerType && !CachedTypes.Contains(fieldTypeAFQN) && !UnresolvedTypes.Contains(fieldTypeAFQN))
            //{
            //    UnresolvedTypes.Add(fieldTypeAFQN);
            //}

            return fe;
        }




        /// <summary>
        /// Extracts the fields.
        /// </summary>
        /// <param name="targetAssemblyDefinition">The target assembly definition.</param>
        /// <param name="fields">The fields.</param>
        /// <returns></returns>
        private FieldElement[] ExtractFields(AssemblyDefinition targetAssemblyDefinition, FieldDefinitionCollection fields)
        {
            List<FieldElement> result = new List<FieldElement>(fields.Count);

            foreach (FieldDefinition field in fields)
            {
                result.Add(ExtractField(targetAssemblyDefinition, field));
            }

            return result.ToArray();
        }
        /// <summary>
        /// Extracts the filter action.
        /// </summary>
        /// <param name="type">The type.</param>       
        private void ExtractFilterAction(TypeDefinition type)
        {
            Console.WriteLine("Extract filterAction1");
            foreach (CustomAttribute attr in type.CustomAttributes)
            {
                if (attr.Constructor.DeclaringType.FullName.Equals(_filterActionAnnotationName))
                {
                    // We use .NET reflection here, because Cecil can not read the enumerations
                    Assembly assembly = Assembly.LoadFrom(type.Module.Image.FileInformation.FullName);
                    if (assembly == null) continue;

                    Type refType = assembly.GetType(type.FullName);
                    if (refType == null) continue;

                    foreach (FilterActionAttribute faa in refType.GetCustomAttributes(typeof(FilterActionAttribute), false))
                    {
                        FilterActionElement faEl = new FilterActionElement();

                        faEl.FullName = type.FullName;
                        faEl.Name = faa.ActionName;
                        switch (faa.FlowBehaviour)
                        {
                            case FilterFlowBehaviour.Continue:
                                faEl.FlowBehaviour = FilterActionElement.FLOW_CONTINUE;
                                break;
                            case FilterFlowBehaviour.Exit:
                                faEl.FlowBehaviour = FilterActionElement.FLOW_EXIT;
                                break;
                            case FilterFlowBehaviour.Return:
                                faEl.FlowBehaviour = FilterActionElement.FLOW_RETURN;
                                break;
                            default:
                                faEl.FlowBehaviour = FilterActionElement.FLOW_CONTINUE;
                                break;
                        } // switch

                        switch (faa.SubstitutionBehaviour)
                        {
                            case MessageSubstitutionBehaviour.Original:
                                faEl.FlowBehaviour = FilterActionElement.MESSAGE_ORIGINAL;
                                break;
                            case MessageSubstitutionBehaviour.Substituted:
                                faEl.FlowBehaviour = FilterActionElement.MESSAGE_SUBSTITUTED;
                                break;
                            case MessageSubstitutionBehaviour.Any:
                                faEl.FlowBehaviour = FilterActionElement.MESSAGE_ANY;
                                break;
                            default:
                                faEl.FlowBehaviour = FilterActionElement.MESSAGE_ORIGINAL;
                                break;
                        } // switch
                    } // foreach  (faa)

                    return;
                }
            }
        }


        /// <summary>
        /// Extracts the type of the filter.
        /// </summary>
        /// <param name="type">The type.</param>
        private void ExtractFilterType(TypeDefinition type)
        {
            foreach (CustomAttribute attr in type.CustomAttributes)
            {
                // FIXME Beter om fullname te gebruiken?
                if (attr.Constructor.DeclaringType.Name.Equals("FilterTypeAnnotation"))
                {
                    FilterTypeElement ftEl = new FilterTypeElement();

                    ftEl.Name = (String)attr.ConstructorParameters[0];
                    ftEl.AcceptCallAction = (String)attr.ConstructorParameters[1];
                    ftEl.RejectCallAction = (String)attr.ConstructorParameters[2];
                    ftEl.AcceptReturnAction = (String)attr.ConstructorParameters[3];
                    ftEl.RejectReturnAction = (String)attr.ConstructorParameters[4];

                    return;
                }
            }
        }
        /// <summary>
        /// Extracts the method.
        /// </summary>
        /// <param name="targetAssemblyDefinition">The target assembly definition.</param>
        /// <param name="method">The method.</param>
        /// <returns></returns>
        private MethodElement ExtractMethod(AssemblyDefinition targetAssemblyDefinition, MethodDefinition method)
        {
            // Create a new method element
            MethodElement me = new MethodElement(System.Guid.NewGuid().ToString());
            me.Signature = method.ToString();
            me.Name = method.Name;
            me.ReturnType = method.ReturnType.ReturnType.FullName;

            // If the return type has not yet been resolved, add it to the list of unresolved types
            String returnTypeAFQN = CreateTypeAFQN(targetAssemblyDefinition, method.ReturnType.ReturnType);
            //if (this._saveInnerType && !CachedTypes.Contains(returnTypeAFQN) && !UnresolvedTypes.Contains(returnTypeAFQN))
            //{
            //    UnresolvedTypes.Add(returnTypeAFQN);
            //}

            me.IsAbstract = method.IsAbstract;
            me.IsConstructor = method.IsConstructor;
            me.IsPrivate = method.Attributes == Mono.Cecil.MethodAttributes.Private;
            me.IsPublic = method.Attributes == Mono.Cecil.MethodAttributes.Public;
            me.IsStatic = method.IsStatic;
            me.IsVirtual = method.IsVirtual;

            // Add the parameters
            me.ParameterElements = ExtractParameters(targetAssemblyDefinition, method.Parameters);

            // Add the method body
            if (method.Body != null && this._processMethodBody)
            {
                me.MethodBody = new Composestar.Repository.LanguageModel.MethodBody(System.Guid.NewGuid().ToString(), me.Id);

                List<CallElement> callElements = new List<CallElement>();
                foreach (Instruction instr in method.Body.Instructions)
                {
                    if (instr.OpCode.Value == OpCodes.Call.Value ||
                        instr.OpCode.Value == OpCodes.Calli.Value ||
                        instr.OpCode.Value == OpCodes.Callvirt.Value
                        )
                    {
                        CallElement ce = new CallElement();
                        MethodReference mr = (MethodReference)(instr.Operand);
                        ce.MethodReference = mr.ToString();

                        callElements.Add(ce);
                    }
                }

                List<CallElement> callElementsArray = new List<CallElement>(callElements.Count);
                foreach (CallElement ce in callElements)
                {
                    callElementsArray.Add(ce);
                }

                me.MethodBody.CallElements = callElementsArray.ToArray();
            }

            return me;
        }

        /// <summary>
        /// Extracts the methods.
        /// </summary>
        /// <param name="targetAssemblyDefinition">The target assembly definition.</param>
        /// <param name="methods">The methods.</param>
        /// <returns></returns>
        private MethodElement[] ExtractMethods(AssemblyDefinition targetAssemblyDefinition, MethodDefinitionCollection methods)
        {
            List<MethodElement> result = new List<MethodElement>(methods.Count);

            foreach (MethodDefinition method in methods)
            {
                result.Add(ExtractMethod(targetAssemblyDefinition, method));
            }

            return result.ToArray();
        }
        /// <summary>
        /// Extracts the parameter.
        /// </summary>
        /// <param name="targetAssemblyDefinition">The target assembly definition.</param>
        /// <param name="parameter">The parameter.</param>
        /// <returns></returns>
        private ParameterElement ExtractParameter(AssemblyDefinition targetAssemblyDefinition, ParameterDefinition parameter)
        {
            ParameterElement pe = new ParameterElement();

            pe.Name = parameter.Name;
            pe.Ordinal = (short)(parameter.Sequence);
            pe.ParameterType = parameter.ParameterType.FullName;

            // If the parameter type has not yet been resolved, add it to the list of unresolved types
            //String parameterTypeAFQN = CreateTypeAFQN(_targetAssemblyDefinition, parameter.ParameterType);
            //if (this._saveInnerType && !CachedTypes.Contains(parameterTypeAFQN) && !UnresolvedTypes.Contains(parameterTypeAFQN))
            //{
            //    UnresolvedTypes.Add(parameterTypeAFQN);
            //}

            pe.IsIn = parameter.Attributes == ParamAttributes.In;
            pe.IsOptional = parameter.Attributes == ParamAttributes.Optional;
            pe.IsOut = parameter.Attributes == ParamAttributes.Out;
            pe.IsRetVal = !parameter.ParameterType.FullName.Equals("System.Void", StringComparison.CurrentCultureIgnoreCase);

            return pe;
        }

        /// <summary>
        /// Extracts the parameters.
        /// </summary>
        /// <param name="targetAssemblyDefinition">The target assembly definition.</param>
        /// <param name="parameters">The parameters.</param>
        /// <returns></returns>
        private ParameterElement[] ExtractParameters(AssemblyDefinition targetAssemblyDefinition, ParameterDefinitionCollection parameters)
        {
            List<ParameterElement> result = new List<ParameterElement>(parameters.Count);

            foreach (ParameterDefinition parameter in parameters)
            {
                result.Add(ExtractParameter(targetAssemblyDefinition, parameter));
            }

            return result.ToArray();
        }
        /// <summary>
        /// Extracts the type.
        /// </summary>
        /// <param name="targetAssemblyDefinition">The target assembly definition.</param>
        /// <param name="parentAssembly">The parent assembly.</param>
        /// <param name="type">The type.</param>
        /// <returns></returns>
        private TypeElement ExtractType(AssemblyDefinition targetAssemblyDefinition, AssemblyElement parentAssembly, TypeDefinition type)
        {
            TypeElement te = new TypeElement(System.Guid.NewGuid().ToString());

            // Name
            te.Name = type.Name;
            te.Namespace = type.Namespace;
            te.FullName = CreateTypeName(type);

            String assembly = CreateAFQN(targetAssemblyDefinition, type);
            te.Assembly = assembly;

            String typeAFQN = CreateTypeAFQN(targetAssemblyDefinition, type);
            this._saveInnerType = this._saveType || UnresolvedTypes.Contains(typeAFQN);

            // Properties
            te.IsAbstract = type.IsAbstract;
            te.IsEnum = type.IsEnum;
            te.IsInterface = type.IsInterface;
            te.IsSealed = type.IsSealed;
            te.IsValueType = type.IsValueType;
            te.IsClass = !type.IsValueType & !type.IsInterface;
            te.IsNotPublic = type.Attributes == Mono.Cecil.TypeAttributes.NotPublic;
            te.IsPrimitive = false;
            te.IsPublic = type.Attributes == Mono.Cecil.TypeAttributes.Public;
            te.IsSerializable = type.Attributes == Mono.Cecil.TypeAttributes.Serializable;

            // Interface
            foreach (TypeReference interfaceDef in type.Interfaces)
            {
                te.ImplementedInterface = String.Format("{0};{1}", te.ImplementedInterface, interfaceDef.FullName);
            }

            // Basetype
            if (type.BaseType != null)
            {
                te.BaseType = type.BaseType.FullName;

                // If the base type has not yet been resolved, add it to the list of unresolved types
                //String baseTypeAFQN = CreateTypeAFQN(_targetAssemblyDefinition, type.BaseType);
                //if (this._saveInnerType && !CachedTypes.Contains(baseTypeAFQN) && !UnresolvedTypes.Contains(baseTypeAFQN))
                //{
                //    UnresolvedTypes.Add(baseTypeAFQN);
                //}


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
            }

            te.FieldElements = ExtractFields(targetAssemblyDefinition, type.Fields);
            te.MethodElements = ExtractMethods(targetAssemblyDefinition, type.Methods);

            CachedTypes.Add(typeAFQN);
            if (this._saveType || (UnresolvedTypes.Contains(typeAFQN) || UnresolvedTypes.Contains(type.FullName))) ResolvedTypes.Add(typeAFQN);

            // Remove this type from the list of unresolved types
            if (UnresolvedTypes.Contains(typeAFQN)) UnresolvedTypes.Remove(typeAFQN);
            if (UnresolvedTypes.Contains(type.FullName)) UnresolvedTypes.Remove(type.FullName); // Unresolved types from the cps files are not AFQN style

            return te;
        }
        /// <summary>
        /// Extracts the types.
        /// </summary>
        /// <param name="targetAssemblyDefinition">The target assembly definition.</param>
        /// <param name="assembly">The assembly.</param>
        /// <param name="types">The types.</param>
        /// <returns></returns>
        private TypeElement[] ExtractTypes(AssemblyDefinition targetAssemblyDefinition, AssemblyElement assembly, List<String> types)
        {
            TypeDefinitionCollection tdc = new TypeDefinitionCollection(targetAssemblyDefinition.MainModule);

            foreach (String type in types)
            {
                if (targetAssemblyDefinition.MainModule.Types.Contains(type))
                {
                    tdc.Add(targetAssemblyDefinition.MainModule.Types[type]);
                }
            }

            return ExtractTypes(targetAssemblyDefinition, assembly, tdc);
        }

        /// <summary>
        /// Extracts the types.
        /// </summary>
        /// <param name="targetAssemblyDefinition">The target assembly definition.</param>
        /// <param name="assembly">The assembly.</param>
        /// <param name="types">The types.</param>
        /// <returns></returns>
        private TypeElement[] ExtractTypes(AssemblyDefinition targetAssemblyDefinition, AssemblyElement assembly, TypeDefinitionCollection types)
        {
            List<TypeElement> result = new List<TypeElement>();

            foreach (TypeDefinition type in types)
            {
                if (type.Name != ModuleName)
                {
                    result.Add(ExtractType(targetAssemblyDefinition, assembly, type));
                }
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

            return result.ToArray();
        }
        #endregion
             
        /// <summary>
        /// Extracts all types.
        /// </summary>
        /// <param name="fileName">Name of the file.</param>
        /// <returns></returns>
        public AssemblyElement ExtractAllTypes(String fileName)
        {

            Stopwatch sw = new Stopwatch();
            sw.Start();

            this._saveType = true;
            this._saveInnerType = true;

            AssemblyDefinition targetAssemblyDefinition = null;

            // Error checks

            targetAssemblyDefinition = AssemblyFactory.GetAssembly(fileName);

            AssemblyElement result = ExtractAllTypes(targetAssemblyDefinition, fileName);

            sw.Stop();
            _lastDuration = sw.Elapsed;

            return result;
        }
              
        /// <summary>
        /// Processes the unresolved types.
        /// </summary>
        /// <param name="assemblyNames">The assembly names.</param>
        /// <returns></returns>
        public List<AssemblyElement> ProcessUnresolvedTypes(Dictionary<String, String> assemblyNames)
        {
            this._saveType = false;

            List<AssemblyElement> assemblies = new List<AssemblyElement>();

            Stopwatch sw = new Stopwatch();
            sw.Start();

            //_cacheAccess = new CacheAccess(Repository.Db4oContainers.Db4oCacheContainer.Instance, _cacheFolder);

            // Retrieve type elements from cache
            //IList<String> types = new List<String>(UnresolvedTypes);
            //foreach (String type in types)
            //{
            //    TypeElement te = null;// CacheAccess.GetTypeElementByAFQN(type.Substring(0, type.IndexOf(", ")), type.Substring(type.IndexOf(", ") + 2));

            //    // Type was found in the cache
            //    if (te != null)
            //    {
            //        // Copy type from cache to local datastore
            //        Console.WriteLine("Copying type: "+te.FullName);
            //        //RepositoryAccess.AddType(te);

            //        //// Copy the type fields
            //        //foreach (FieldElement fe in CacheAccess.GetFieldElements(te))
            //        //{
            //        //    RepositoryAccess.AddField(te, fe);
            //        //}

            //        ///// Copy the type methods
            //        //foreach (MethodElement me in CacheAccess.GetMethodElements(te))
            //        //{
            //        //    RepositoryAccess.AddMethod(te, me);

            //        //    // Copy the method parameters
            //        //    foreach (ParameterElement pe in CacheAccess.GetParameterElements(te, me))
            //        //    {
            //        //        RepositoryAccess.AddParameter(me, pe);
            //        //    }
            //        //}

            //        UnresolvedTypes.Remove(type);
            //    }
            //}

            // 
            if (UnresolvedTypes.Count > 0)
            {
                //Dictionary<string, StringCollection> unresolvedAssemblies = new Dictionary<string, StringCollection>();

                //foreach (String type in UnresolvedTypes)
                //{
                //    if (type.IndexOf(",") > -1)
                //    {
                //        String assemblyName = type.Substring(type.IndexOf(", ") + 2);

                //        if (assemblyName.StartsWith("NULL")) continue;

                //        if (unresolvedAssemblies.ContainsKey(assemblyName))
                //        {
                //            unresolvedAssemblies[assemblyName].Add(type.Substring(0, type.IndexOf(",")));

                //        }
                //        else {
                //            StringCollection sc = new StringCollection();
                //            sc.Add(type.Substring(0, type.IndexOf(",")));
                //            unresolvedAssemblies.Add(assemblyName, sc);
                //        }

                //        //if (!unresolvedAssemblies.Contains(assemblyName)) unresolvedAssemblies.Add(assemblyName);
                //    }
                //}

                //List<String> unresolvedAssemblies = new List<string>();

                // Collect the assembly names of the unresolved types
                //Console.WriteLine("Generating assembly list for unresolved types...");
                //types = new List<String>(UnresolvedTypes);
                //foreach (String type in types)
                //{
                //    //Console.WriteLine("  {0}", type);
                //    if (type.IndexOf(",") > -1)
                //    {
                //        String assemblyName = type.Substring(type.IndexOf(", ") + 1);
                //        if (!unresolvedAssemblies.Contains(assemblyName)) unresolvedAssemblies.Add(assemblyName);
                //    }
                //}


                // Use the Cecil assembly resolver to find the missing assemblies
                DefaultAssemblyResolver dar = new DefaultAssemblyResolver();
                foreach (String assemblyName in assemblyNames.Keys)
                {
                    //Console.WriteLine(String.Format("Analyzing '{0}', please wait...", assemblyName));
                    AssemblyDefinition ad = dar.Resolve(assemblyName);
                    if (ad != null)
                    {
                        _processMethodBody = false;

                        AssemblyElement ae = new AssemblyElement();
                        ae.Name = ad.Name.FullName;
                        ae.FileName = assemblyNames[assemblyName];

                        TypeElement[] types = ExtractTypes(ad, ae, this.UnresolvedTypes);

                        if (types.Length > 0)
                        {
                            ae.TypeElements = types;

                            assemblies.Add(ae);
                        }

                        _processMethodBody = true;
                    }
                }

                //if (UnresolvedTypes.Count > 0)
                //{
                //foreach (String s in UnresolvedTypes)
                //{
                //    Console.WriteLine("Missing type {0}.", s);
                //}

                //ProcessUnresolvedTypes();
                //}

            }


            //}


            //    //
            //    //if (type.IndexOf(",") > -1)
            //    //{
            //    //    String assemblyName = type.Substring(type.IndexOf(",") + 1);

            //    //    if (!resolvedAssemblies.Contains(assemblyName))
            //    //    {
            //    //        AssemblyDefinition ad = dar.Resolve(assemblyName);
            //    //        if (ad != null)
            //    //        {
            //    //            Console.WriteLine(String.Format("Analyzing {0}", ad.Name));
            //    //            this.ExtractTypeElements(String.Empty, ad);
            //    //            resolvedAssemblies.Add(assemblyName);
            //    //        }
            //    //    }
            //    //}



            //_cacheAccess.CloseContainer();
            ////RepositoryAccess.CloseDatabase();
            //Console.WriteLine("WARNING: Explicitly clearing the unresolved types list, types have NOT been resolved!");
            //UnresolvedTypes.Clear();

            sw.Stop();
            _lastDuration = sw.Elapsed;

            return assemblies;
        }

        /// <summary>
        /// Closes this instance. Cleanup any used resources.
        /// </summary>
        public void Close()
        {
            //if (_repositoryAccess != null)
            //    _repositoryAccess.CloseContainer();
        }


        /// <summary>
        /// Gets the duration of the last executed method.
        /// </summary>
        /// <value>The last duration.</value>
        public TimeSpan LastDuration
        {
            get
            {
                return _lastDuration;
            }
        }

        /// <summary>
        /// Returns a <see cref="T:System.String"></see> that represents the current <see cref="T:System.Object"></see>.
        /// </summary>
        /// <returns>
        /// A <see cref="T:System.String"></see> that represents the current <see cref="T:System.Object"></see>.
        /// </returns>
        public override string ToString()
        {
            return "Cecil IL Analyzer";
        }
    }
}
