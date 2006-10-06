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

using Composestar.StarLight.ContextInfo.FilterTypes;

namespace Composestar.StarLight.ILAnalyzer
{
    /// <summary>
    /// An implementation of the IILAnalyzer working with Cecil.
    /// </summary>
    public class CecilILAnalyzer : IILAnalyzer
    {
        private bool _isInitialized = false;
        private TimeSpan _lastDuration=TimeSpan.Zero;
        //private RepositoryAccess _repositoryAccess;
        //private CacheAccess _cacheAccess;
        private List<String> _resolvedTypes = new List<String>();
        private List<String> _unresolvedTypes = new List<String>();
        private List<String> _cachedTypes = new List<String>();
        private bool _saveType = false;
        private bool _saveInnerType = false;
        private bool _processMethodBody = true;
        private bool _processAttributes = false;
        //private string _cacheFolder = String.Empty;

        private string _filterTypeName = 
            typeof(Composestar.StarLight.ContextInfo.FilterTypes.FilterType).FullName;
        private string _filterTypeAnnotationName =
            typeof(Composestar.StarLight.ContextInfo.FilterTypes.FilterTypeAttribute).FullName;
        private string _filterActionName =
            typeof(Composestar.StarLight.ContextInfo.FilterTypes.FilterAction).FullName;
        private string _filterActionAnnotationName =
            typeof(Composestar.StarLight.ContextInfo.FilterTypes.FilterActionAttribute).FullName;
        //private const string Filter

        /// <summary>
        /// Initializes the analyzer with the specified assembly name.
        /// </summary>
        /// <param name="fileName">Name of the file.</param>
        /// <param name="config">The config.</param>
        public void Initialize(NameValueCollection config)
        {
            #region Config
            string repositoryFilename = string.Empty;
            if (config != null)
            {
                repositoryFilename = config.Get("RepositoryFilename"); 
            }

            if (string.IsNullOrEmpty(repositoryFilename))
                throw new ArgumentException(Properties.Resources.RepositoryFilenameNotSpecified, "RepositoryFilename");

            //if (_repositoryAccess == null)
            //{
                //Repository.Db4oContainers.Db4oDataStoreContainer.Instance.RepositoryFileName = repositoryFilename;
            //    _repositoryAccess = new RepositoryAccess(Repository.Db4oContainers.Db4oRepositoryContainer.Instance, repositoryFilename);
            //}
            //_cacheFolder = config.Get("CacheFolder");

            if (config.Get("ProcessMethodBody") == "false") this._processMethodBody = false;

            #endregion

            _lastDuration = TimeSpan.Zero;

            _isInitialized = true;

        }

        //public RepositoryAccess RepositoryAccess
        //{
        //    get { return _repositoryAccess; }
        //}

        //public CacheAccess CacheAccess
        //{
        //    get { return _cacheAccess; }
        //}

        public List<String> UnresolvedTypes
        {
            get { return _unresolvedTypes; }
        }

        public List<String> ResolvedTypes
        {
            get { return _resolvedTypes; }
        }

        public List<String> CachedTypes
        {
            get { return _cachedTypes; }
        }

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
                //RepositoryAccess.AddAttribute(parent, ae);
            }
        }

        private String CreateAFQN(AssemblyDefinition _targetAssemblyDefinition, TypeReference type)
        {
            if (_targetAssemblyDefinition == null)
                throw new ArgumentNullException("_targetAssemblyDefinition");

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
                foreach (AssemblyNameReference assembly in _targetAssemblyDefinition.MainModule.AssemblyReferences)
                {
                    if (type.Scope.Name == assembly.Name)
                    {
                        return assembly.FullName;
                    }
                }
            }
           
            return "NULL";
        }

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

        private String CreateTypeAFQN(AssemblyDefinition _targetAssemblyDefinition, TypeReference type)
        {
            return String.Format("{0}, {1}", CreateTypeName(type), CreateAFQN(_targetAssemblyDefinition, type));
        }

        public IlAnalyzerResults ExtractTypeElements(String fileName)
        {
            CheckForInit();

            Stopwatch sw = new Stopwatch();
            sw.Start();

            AssemblyDefinition _targetAssemblyDefinition = null;

            #region Filename
            if (String.IsNullOrEmpty(fileName))
                throw new ArgumentNullException("fileName", Properties.Resources.FileNameNullOrEmpty);

            if (!File.Exists(fileName))
                throw new ArgumentException(String.Format(Properties.Resources.FileNotFound, fileName), "fileName");

            // Only extract types if assembly does not exist or has been updated
            AssemblyElement assembly = null;// RepositoryAccess.GetAssemblyElementByFileName(System.IO.Path.GetFullPath(fileName));
            if (assembly != null)
            {
                // Clean up datastore
                //RepositoryAccess.DeleteWeavingInstructions(); // TODO: have to work on assembly lvl only, now EVERYTHING gets cleared!

                if (assembly.Timestamp != File.GetLastWriteTimeUtc(fileName).Ticks)
                {
                    // Assembly has to be re-analysed (remove all previous types from datastore)
                    //RepositoryAccess.DeleteTypeElements(assembly.Name);
                }
                else
                {
                    sw.Stop();
                    _lastDuration = sw.Elapsed; 

                    return IlAnalyzerResults.FROM_CACHE;
                }
            }

            try
            {
                _targetAssemblyDefinition = AssemblyFactory.GetAssembly(fileName);

                if (assembly == null)
                {
                    assembly = new AssemblyElement();
                    assembly.Name = _targetAssemblyDefinition.Name.FullName;
                    assembly.FileName = System.IO.Path.GetFullPath(fileName);
                }
                assembly.Timestamp = File.GetLastWriteTimeUtc(fileName).Ticks;
                //RepositoryAccess.AddAssembly(assembly);

                this.ExtractTypeElements(fileName, _targetAssemblyDefinition, false);
            }
            catch (EndOfStreamException)
            {
                throw new BadImageFormatException(String.Format(Properties.Resources.ImageIsBad, fileName));
            }
            #endregion
                      

            sw.Stop();
            _lastDuration = sw.Elapsed;

            return IlAnalyzerResults.FROM_ASSEMBLY;
        }

        public AssemblyElement ExtractAllTypes(String fileName)
        {
            CheckForInit();

            Stopwatch sw = new Stopwatch();
            sw.Start();

            this._saveType = true;
            this._saveInnerType = true;

            AssemblyDefinition _targetAssemblyDefinition = null;

            // Error checks

            _targetAssemblyDefinition = AssemblyFactory.GetAssembly(fileName);

            AssemblyElement result = ExtractAllTypes(_targetAssemblyDefinition, fileName);

            sw.Stop();
            _lastDuration = sw.Elapsed;

            return result;
        }

        private AssemblyElement ExtractAllTypes(AssemblyDefinition _targetAssemblyDefinition, String fileName)
        {
            AssemblyElement ae = new AssemblyElement();

            ae.Name = _targetAssemblyDefinition.Name.FullName;
            ae.FileName = fileName;
            ae.Timestamp = File.GetLastWriteTime(fileName).Ticks;

            ae.TypeElements = ExtractTypes(_targetAssemblyDefinition, ae, _targetAssemblyDefinition.MainModule.Types);

            return ae;
        }

        private TypeElement[] ExtractTypes(AssemblyDefinition _targetAssemblyDefinition, AssemblyElement assembly, StringCollection types)
        {
            TypeDefinitionCollection tdc = new TypeDefinitionCollection(_targetAssemblyDefinition.MainModule);

            foreach (String type in types)
            {
                if (_targetAssemblyDefinition.MainModule.Types.Contains(type))
                {
                    tdc.Add(_targetAssemblyDefinition.MainModule.Types[type]);
                    //extractedTypes.Add(ExtractType(_targetAssemblyDefinition, _targetAssemblyDefinition.MainModule.Types[type]));
                 
                }
            }



            return ExtractTypes(_targetAssemblyDefinition, assembly, tdc);
        }

        private TypeElement[] ExtractTypes(AssemblyDefinition _targetAssemblyDefinition, AssemblyElement assembly, TypeDefinitionCollection types)
        {
            TypeElement[] result = null;
            if (types.Contains("<Module>"))
            {
                result = new TypeElement[types.Count - 1];
            }
            else
            {
                result = new TypeElement[types.Count];
            }
            
            int i = 0;
            foreach (TypeDefinition type in types)
            {
                if (type.Name != "<Module>")
                {
                    result[i] = ExtractType(_targetAssemblyDefinition, assembly, type);
                    i = i + 1;
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

            return result;
        }

        private TypeElement ExtractType(AssemblyDefinition _targetAssemblyDefinition, AssemblyElement parentAssembly, TypeDefinition type)
        {
            TypeElement te = new TypeElement(System.Guid.NewGuid().ToString());

            // Name
            te.Name = type.Name;
            te.Namespace = type.Namespace;
            te.FullName = CreateTypeName(type);

            String assembly = CreateAFQN(_targetAssemblyDefinition, type);
            te.Assembly = assembly;

            // TODO Zou weg kunnen? Info komt nu uit assemblyElement
            te.FromDLL = parentAssembly.FileName;

            String typeAFQN = CreateTypeAFQN(_targetAssemblyDefinition, type);
            this._saveInnerType = this._saveType || UnresolvedTypes.Contains(typeAFQN);

            // Properties
            te.IsAbstract = type.IsAbstract;
            te.IsEnum = type.IsEnum;
            te.IsInterface = type.IsInterface;
            te.IsSealed = type.IsSealed;
            te.IsValueType = type.IsValueType;
            te.FromDLL = "";//TODO: _targetAssemblyDefinition.;
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
                String baseTypeAFQN = CreateTypeAFQN(_targetAssemblyDefinition, type.BaseType);
                if (this._saveInnerType && !CachedTypes.Contains(baseTypeAFQN) && !UnresolvedTypes.Contains(baseTypeAFQN))
                {
                    UnresolvedTypes.Add(baseTypeAFQN);
                }


                // Check whether type is a FilterType:
                if(type.BaseType.FullName.Equals(_filterTypeName))
                {
                    ExtractFilterType(type);
                }

                // Check whether type is a FilterAction:
                if(type.BaseType.FullName.Equals(_filterActionName))
                {
                    ExtractFilterAction(type);
                }
            }

            te.FieldElements = ExtractFields(_targetAssemblyDefinition, type.Fields);
            te.MethodElements = ExtractMethods(_targetAssemblyDefinition, type.Methods);

            CachedTypes.Add(typeAFQN);
            if (this._saveType || UnresolvedTypes.Contains(typeAFQN)) ResolvedTypes.Add(typeAFQN);

            // Remove this type from the list of unresolved types
            if (UnresolvedTypes.Contains(typeAFQN))
            {
                UnresolvedTypes.Remove(typeAFQN);
            }

            return te;
        }


        /// <summary>
        /// Extracts the type of the filter.
        /// </summary>
        /// <param name="type">The type.</param>
        private void ExtractFilterType(TypeDefinition type)
        {
            foreach(CustomAttribute attr in type.CustomAttributes)
            {
                // FIXME Beter om fullname te gebruiken?
                if(attr.Constructor.DeclaringType.Name.Equals("FilterTypeAnnotation"))
                {
                    FilterTypeElement ftEl = new FilterTypeElement();

                    ftEl.Name = (String) attr.ConstructorParameters[0];
                    ftEl.AcceptCallAction = (String) attr.ConstructorParameters[1];
                    ftEl.RejectCallAction = (String) attr.ConstructorParameters[2];
                    ftEl.AcceptReturnAction = (String) attr.ConstructorParameters[3];
                    ftEl.RejectReturnAction = (String) attr.ConstructorParameters[4];

                    return;
                }
            }
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


        private FieldElement[] ExtractFields(AssemblyDefinition _targetAssemblyDefinition, FieldDefinitionCollection fields)
        {
            FieldElement[] result = new FieldElement[fields.Count];

            int i = 0;
            foreach (FieldDefinition field in fields)
            {
                result[i] = ExtractField(_targetAssemblyDefinition, field);
                i = i + 1;
            }
        
            return result;
        }

        private FieldElement ExtractField(AssemblyDefinition _targetAssemblyDefinition, FieldDefinition field)
        {
            FieldElement fe = new FieldElement(System.Guid.NewGuid().ToString());

            fe.Name = field.Name;
            fe.Type = field.FieldType.FullName;

            fe.IsPrivate = field.Attributes == Mono.Cecil.FieldAttributes.Private;
            fe.IsPublic = field.Attributes == Mono.Cecil.FieldAttributes.Public;
            fe.IsStatic = field.IsStatic;

            // If the field type has not yet been resolved, add it to the list of unresolved types
            String fieldTypeAFQN = CreateTypeAFQN(_targetAssemblyDefinition, field.FieldType);
            if (this._saveInnerType && !CachedTypes.Contains(fieldTypeAFQN) && !UnresolvedTypes.Contains(fieldTypeAFQN))
            {
                UnresolvedTypes.Add(fieldTypeAFQN);
            }
            
            return fe;
        }

        private MethodElement[] ExtractMethods(AssemblyDefinition _targetAssemblyDefinition, MethodDefinitionCollection methods)
        {
            MethodElement[] result = new MethodElement[methods.Count];

            int i = 0;
            foreach (MethodDefinition method in methods)
            {
                result[i] = ExtractMethod(_targetAssemblyDefinition, method);
                i = i + 1;
            }

            return result;
        }

        private MethodElement ExtractMethod(AssemblyDefinition _targetAssemblyDefinition, MethodDefinition method)
        {
            // Create a new method element
            MethodElement me = new MethodElement(System.Guid.NewGuid().ToString());
            me.Signature = method.ToString();
            me.Name = method.Name;
            me.ReturnType = method.ReturnType.ReturnType.FullName;

            // If the return type has not yet been resolved, add it to the list of unresolved types
            String returnTypeAFQN = CreateTypeAFQN(_targetAssemblyDefinition, method.ReturnType.ReturnType);
            if (this._saveInnerType && !CachedTypes.Contains(returnTypeAFQN) && !UnresolvedTypes.Contains(returnTypeAFQN))
            {
                UnresolvedTypes.Add(returnTypeAFQN);
            }

            me.IsAbstract = method.IsAbstract;
            me.IsConstructor = method.IsConstructor;
            me.IsPrivate = method.Attributes == Mono.Cecil.MethodAttributes.Private;
            me.IsPublic = method.Attributes == Mono.Cecil.MethodAttributes.Public;
            me.IsStatic = method.IsStatic;
            me.IsVirtual = method.IsVirtual;

            // Add the parameters
            me.ParameterElements = ExtractParameters(_targetAssemblyDefinition, method.Parameters);

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
                
                CallElement[] callElementsArray = new CallElement[callElements.Count];
                int i = 0;
                foreach (CallElement ce in callElements)
                {
                    callElementsArray[i] = ce;
                    i = i + 1;
                }

                me.MethodBody.CallElements = callElementsArray;
            }

            return me;
        }

        private ParameterElement[] ExtractParameters(AssemblyDefinition _targetAssemblyDefinition, ParameterDefinitionCollection parameters)
        {
            ParameterElement[] result = new ParameterElement[parameters.Count];

            int i = 0;
            foreach (ParameterDefinition parameter in parameters)
            {
                result[i] = ExtractParameter(_targetAssemblyDefinition, parameter);
                i = i + 1;
            }

            return result;
        }

        private ParameterElement ExtractParameter(AssemblyDefinition _targetAssemblyDefinition, ParameterDefinition parameter)
        {
            ParameterElement pe = new ParameterElement();

            pe.Name = parameter.Name;
            pe.Ordinal = (short)(parameter.Sequence);
            pe.ParameterType = parameter.ParameterType.FullName;

            // If the parameter type has not yet been resolved, add it to the list of unresolved types
            String parameterTypeAFQN = CreateTypeAFQN(_targetAssemblyDefinition, parameter.ParameterType);
            if (this._saveInnerType && !CachedTypes.Contains(parameterTypeAFQN) && !UnresolvedTypes.Contains(parameterTypeAFQN))
            {
                UnresolvedTypes.Add(parameterTypeAFQN);
            }

            pe.IsIn = parameter.Attributes == ParamAttributes.In;
            pe.IsOptional = parameter.Attributes == ParamAttributes.Optional;
            pe.IsOut = parameter.Attributes == ParamAttributes.Out;
            pe.IsRetVal = !parameter.ParameterType.FullName.Equals("System.Void", StringComparison.CurrentCultureIgnoreCase);

            return pe;
        }

        /// <summary>
        /// Extracts the types.
        /// </summary>
        /// <returns></returns>
        private void ExtractTypeElements(String fileName, AssemblyDefinition _targetAssemblyDefinition, bool cache)
        {
            int i = 0;
            
            //Gets all types of the MainModule of the assembly
            foreach (TypeDefinition type in _targetAssemblyDefinition.MainModule.Types)
            {

                i = i + 1;
                if ((i<40 || i%50==0) && !cache) Console.WriteLine(String.Format("Processing type ({0}/{1}): {2}", i, _targetAssemblyDefinition.MainModule.Types.Count, type.FullName));
                TypeElement ti = new TypeElement(System.Guid.NewGuid().ToString());
                

                // Name
                ti.Name = type.Name;
                ti.FullName = CreateTypeName(type);

                String assembly = CreateAFQN(_targetAssemblyDefinition, type);
                ti.Assembly = assembly;

                String typeAFQN = CreateTypeAFQN(_targetAssemblyDefinition, type);

                // Properties
                ti.IsAbstract = type.IsAbstract;
                ti.IsEnum = type.IsEnum;
                ti.IsInterface = type.IsInterface;
                ti.IsSealed = type.IsSealed;
                ti.IsValueType = type.IsValueType;
                ti.FromDLL = fileName;
                ti.IsClass = !type.IsValueType & !type.IsInterface;
                ti.IsNotPublic = type.Attributes == Mono.Cecil.TypeAttributes.NotPublic; 
                ti.IsPrimitive = false;
                ti.IsPublic = type.Attributes == Mono.Cecil.TypeAttributes.Public; 
                ti.IsSerializable = type.Attributes == Mono.Cecil.TypeAttributes.Serializable; 
                ti.Namespace = type.Namespace;
                
                // Interface
                foreach (TypeReference interfaceDef in type.Interfaces)
                {
                    ti.ImplementedInterface = String.Format("{0};{1}", ti.ImplementedInterface, interfaceDef.FullName);
                }
              
                // Basetype
                if (type.BaseType != null) { 
                    ti.BaseType = type.BaseType.FullName;

                    // If the base type has not yet been resolved, add it to the list of unresolved types
                    String baseTypeAFQN = CreateTypeAFQN(_targetAssemblyDefinition, type.BaseType); 
                    if (!UnresolvedTypes.Contains(baseTypeAFQN) && !ResolvedTypes.Contains(baseTypeAFQN))
                    {
                        //Console.WriteLine("  basetype: {0}", type.BaseType.FullName);
                        UnresolvedTypes.Add(baseTypeAFQN);
                    }
                }

                // Add to the repository
                //if (cache) { 
                //    CacheAccess.AddType(ti);
                //    if (UnresolvedTypes.Contains(typeAFQN))
                //    {
                //        RepositoryAccess.AddType(ti);
                //    }
                //}
                //else { RepositoryAccess.AddType(ti); }
                //ResolvedTypes.Add(typeAFQN);

                // Remove this type from the list of unresolved types
                if (UnresolvedTypes.Contains(typeAFQN))
                {
                    UnresolvedTypes.Remove(typeAFQN);
                }

                // Add attributes for type
                ExtractAttributeElements(ti, type.CustomAttributes);


                foreach (FieldDefinition field in type.Fields)
                {
                    // Create a new field element
                    FieldElement fe = new FieldElement(System.Guid.NewGuid().ToString(), ti.Id);
                    fe.Name = field.Name;
                    fe.Type = field.FieldType.FullName;

                    // If the field type has not yet been resolved, add it to the list of unresolved types
                    String fieldTypeAFQN = CreateTypeAFQN(_targetAssemblyDefinition, field.FieldType); 
                    if (!UnresolvedTypes.Contains(fieldTypeAFQN) && !ResolvedTypes.Contains(fieldTypeAFQN))
                    {
                        //Console.WriteLine("  fieldtype: {0}", field.FieldType.FullName);
                        UnresolvedTypes.Add(fieldTypeAFQN);
                    }

                    fe.IsPrivate = field.Attributes == Mono.Cecil.FieldAttributes.Private;
                    fe.IsPublic = field.Attributes == Mono.Cecil.FieldAttributes.Public;
                    fe.IsStatic = field.IsStatic;

                    // Add to the repository
                    //if (cache) { 
                    //    CacheAccess.AddField(ti, fe);
                    //    if (UnresolvedTypes.Contains(typeAFQN))
                    //    {
                    //        RepositoryAccess.AddField(ti, fe);
                    //    }
                    //}
                    //else { RepositoryAccess.AddField(ti, fe); }

                    // Add attributes for field
                    ExtractAttributeElements(fe, field.CustomAttributes);
                }
                
                foreach (MethodDefinition method in type.Methods)
                {
                    // Create a new method element
                    MethodElement mi = new MethodElement(System.Guid.NewGuid().ToString());    
                    mi.Signature = method.ToString(); 
                    mi.Name = method.Name;                    
                    mi.ReturnType = method.ReturnType.ReturnType.FullName;

                    // If the return type has not yet been resolved, add it to the list of unresolved types
                    String returnTypeAFQN = CreateTypeAFQN(_targetAssemblyDefinition, method.ReturnType.ReturnType); 
                    if (!UnresolvedTypes.Contains(returnTypeAFQN) && !ResolvedTypes.Contains(returnTypeAFQN))
                    {
                        //Console.WriteLine("  returntype: {0}", method.ReturnType.ReturnType.FullName);
                        UnresolvedTypes.Add(returnTypeAFQN);
                    }

                    mi.IsAbstract = method.IsAbstract;
                    mi.IsConstructor = method.IsConstructor;
                    mi.IsPrivate = method.Attributes == Mono.Cecil.MethodAttributes.Private;
                    mi.IsPublic = method.Attributes == Mono.Cecil.MethodAttributes.Public;
                    mi.IsStatic = method.IsStatic;
                    mi.IsVirtual = method.IsVirtual;
                    
                    // Add to the repositorys
                    if (method.Body != null && this._processMethodBody) mi.MethodBody = new Composestar.Repository.LanguageModel.MethodBody(System.Guid.NewGuid().ToString(), mi.Id);
                    //if (cache) { 
                    //    CacheAccess.AddMethod(ti, mi);
                    //    if (UnresolvedTypes.Contains(typeAFQN))
                    //    {
                    //        RepositoryAccess.AddMethod(ti, mi);
                    //    }
                    //}
                    //else { RepositoryAccess.AddMethod(ti, mi); }

                    ExtractAttributeElements(mi, method.CustomAttributes);
                    
                    // Add the parameters
                    foreach (ParameterDefinition param in method.Parameters)
                    {
                        // Create a new parameter element
                        ParameterElement pe = new ParameterElement();
                        pe.ParentMethodId = mi.Id;
                        pe.Name = param.Name;
                        pe.Ordinal = (short)(param.Sequence);
                        pe.ParameterType = param.ParameterType.FullName;

                        // If the parameter type has not yet been resolved, add it to the list of unresolved types
                        String parameterTypeAFQN = CreateTypeAFQN(_targetAssemblyDefinition, param.ParameterType);
                        if (!UnresolvedTypes.Contains(parameterTypeAFQN) && !ResolvedTypes.Contains(parameterTypeAFQN))
                        {
                            //Console.WriteLine("  paramtype: {0}", param.ParameterType.FullName);
                            UnresolvedTypes.Add(parameterTypeAFQN);
                        }

                        pe.IsIn = param.Attributes == ParamAttributes.In;
                        pe.IsOptional = param.Attributes == ParamAttributes.Optional;
                        pe.IsOut = param.Attributes == ParamAttributes.Out;                          
                        pe.IsRetVal = !param.ParameterType.FullName.Equals("System.Void", StringComparison.CurrentCultureIgnoreCase);

                        // Add to the method
                        //if (cache) { 
                        //    CacheAccess.AddParameter(ti, mi, pe);
                        //    if (UnresolvedTypes.Contains(typeAFQN))
                        //    {
                        //        RepositoryAccess.AddParameter(mi, pe);
                        //    }
                        //}
                        //else { RepositoryAccess.AddParameter(mi, pe); }
                    }

                    if (method.Body != null && this._processMethodBody)
                    {
                        foreach (Instruction instr in method.Body.Instructions)
                        {
                           if (instr.OpCode.Value == OpCodes.Call.Value ||
                               instr.OpCode.Value == OpCodes.Calli.Value ||
                               instr.OpCode.Value == OpCodes.Callvirt.Value
                               )
                           {
                               CallElement ce = new CallElement();
                               MethodReference mr = (MethodReference)(instr.Operand);
                               ce.MethodReference = mr.ToString() ; 

                               // Add to the list of the method
                               //RepositoryAccess.AddCallToMethod(mi, ce); 
                           }
                        }
                    }
                }
            }

            
            //Console.WriteLine("Resolving types from datastore...");
            IList<String> types = new List<String>(UnresolvedTypes);
            foreach (String type in types)
            {
                if (type.EndsWith(", NULL"))
                {
                    UnresolvedTypes.Remove(type);
                    continue;
                }

                // Perhaps unresolved types are already in the local datastore from a previous run
                //TypeElement te = null;// RepositoryAccess.GetTypeElementByAFQN(type.Substring(0, type.IndexOf(", ")), type.Substring(type.IndexOf(", ") + 2));
                //if (te != null)
                //{
                //    UnresolvedTypes.Remove(type);
                //}
            }



            //IList<TypeElement> ret = RepositoryAccess.GetTypeElements();
            
            //_repositoryAccess.CloseDatabase();
        }



        public List<AssemblyElement> ProcessUnresolvedTypes()
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
                Dictionary<string, StringCollection> unresolvedAssemblies = new Dictionary<string, StringCollection>();

                foreach (String type in UnresolvedTypes)
                {
                    if (type.IndexOf(",") > -1)
                    {
                        String assemblyName = type.Substring(type.IndexOf(", ") + 2);

                        if (assemblyName.StartsWith("NULL")) continue;

                        if (unresolvedAssemblies.ContainsKey(assemblyName))
                        {
                            unresolvedAssemblies[assemblyName].Add(type.Substring(0, type.IndexOf(",")));

                        }
                        else {
                            StringCollection sc = new StringCollection();
                            sc.Add(type.Substring(0, type.IndexOf(",")));
                            unresolvedAssemblies.Add(assemblyName, sc);
                        }
                        
                        //if (!unresolvedAssemblies.Contains(assemblyName)) unresolvedAssemblies.Add(assemblyName);
                    }
                }

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
                foreach (String assemblyName in unresolvedAssemblies.Keys)
                {
                    AssemblyDefinition ad = dar.Resolve(assemblyName);
                    if (ad != null)
                    {
                        Console.WriteLine(String.Format("Analyzing '{0}', please wait...", assemblyName));
                        _processMethodBody = false;

                        //AssemblyElement ae = new AssemblyElement();

                        //ae.Name = ad.Name.FullName;
                        //ae.FileName = ad.Name.Name;
                        //ae.TypeElements = ;
                        
                        assemblies.Add(ExtractAllTypes(ad, String.Empty));
                        
                        _processMethodBody = true;
                    }
                }

                if (UnresolvedTypes.Count > 0)
                {
                    //foreach (String s in UnresolvedTypes)
                    //{
                    //    Console.WriteLine("Missing type {0}.", s);
                    //}
                    
                    ProcessUnresolvedTypes();
                }

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

        /// <summary>
        /// Closes this instance. Cleanup any used resources.
        /// </summary>
        public void Close()
        {
            //if (_repositoryAccess != null)
            //    _repositoryAccess.CloseContainer();
        }

        /// <summary>
        /// Checks for initialization. Throw exception when not inited.
        /// </summary>
        private void CheckForInit()
        {
            if (!_isInitialized)
                 throw new ILAnalyzerException(Properties.Resources.NotYetInitialized);
            
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
