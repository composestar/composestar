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
  
namespace Composestar.StarLight.ILAnalyzer
{
    /// <summary>
    /// An implementation of the IILAnalyzer working with Cecil.
    /// </summary>
    public class CecilILAnalyzer : IILAnalyzer
    {
        private bool _isInitialized = false;
        private TimeSpan _lastDuration=TimeSpan.MinValue;
        private RepositoryAccess _repositoryAccess;
        private List<String> _resolvedTypes = new List<String>();
        private List<String> _unresolvedTypes = new List<String>();
        private bool _processMethodBody = true;
        private bool _processAttributes = false;
        private string _cacheFolder = String.Empty;

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

            if (_repositoryAccess == null)
            {
                //Repository.Db4oContainers.Db4oDataStoreContainer.Instance.RepositoryFileName = repositoryFilename;
                _repositoryAccess = new RepositoryAccess(Repository.Db4oContainers.Db4oRepositoryContainer.Instance, repositoryFilename);
            }
            _cacheFolder = config.Get("CacheFolder");

            if (config.Get("ProcessMethodBody") == "false") this._processMethodBody = false;

            #endregion

            _isInitialized = true;

        }

        public RepositoryAccess RepositoryAccess
        {
            get { return _repositoryAccess; }
        }

        public List<String> UnresolvedTypes
        {
            get { return _unresolvedTypes; }
        }

        public List<String> ResolvedTypes
        {
            get { return _resolvedTypes; }
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
                RepositoryAccess.AddAttribute(parent, ae);
            }
        }

        private String CreateAFQN(AssemblyDefinition _targetAssemblyDefinition, TypeReference type)
        {
            if (_targetAssemblyDefinition == null)
                throw new ArgumentNullException("_targetAssemblyDefinition");

            if (type == null)
                throw new ArgumentNullException("type");
            
            // Locally declared type
            if (type.Scope != null)
            {
                if (type.Scope is ModuleDefinition)
                {
                    if (((ModuleDefinition)type.Scope).Assembly != null)
                    {
                        return ((ModuleDefinition)type.Scope).Assembly.Name.FullName;
                    }
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

            return "";
            //throw new Exception(String.Format("Unable to resolve assembly name for type '{0}'.", type.FullName));
        }

        private String CreateTypeAFQN(AssemblyDefinition _targetAssemblyDefinition, TypeReference type)
        {
            String typeName = type.FullName;
            if (type.FullName.EndsWith("[]")) typeName = type.FullName.Substring(0, type.FullName.Length - 2);

            return String.Format("{0}, {1}", typeName, CreateAFQN(_targetAssemblyDefinition, type));
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
            AssemblyElement assembly = RepositoryAccess.GetAssemblyElementByFileName(System.IO.Path.GetFullPath(fileName));
            if (assembly != null)
            {
                // Clean up datastore
                RepositoryAccess.DeleteWeavingInstructions(); // TODO: have to work on assembly lvl only, now EVERYTHING gets cleared!

                if (assembly.Timestamp != File.GetLastWriteTimeUtc(fileName).Ticks)
                {
                    // Assembly has to be re-analysed (remove all previous types from datastore)
                    RepositoryAccess.DeleteTypeElements(assembly.Name);
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
                RepositoryAccess.AddAssembly(assembly);

                this.ExtractTypeElements(fileName, _targetAssemblyDefinition);
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
      
        /// <summary>
        /// Extracts the types.
        /// </summary>
        /// <returns></returns>
        private void ExtractTypeElements(String fileName, AssemblyDefinition _targetAssemblyDefinition)
        {
            int i = 0;
            
            //Gets all types of the MainModule of the assembly
            foreach (TypeDefinition type in _targetAssemblyDefinition.MainModule.Types)
            {
                i = i + 1;
                if (i%40==0 || i<40) Console.WriteLine(String.Format("Processing type ({0}/{1}): {2}", i, _targetAssemblyDefinition.MainModule.Types.Count, type.FullName));
                TypeElement ti = new TypeElement(System.Guid.NewGuid().ToString());
                
                // Name
                ti.Name = type.Name;
                ti.FullName = type.FullName;

                String assembly = CreateAFQN(_targetAssemblyDefinition, type);
                ti.Assembly = assembly;

                String typeAFQN = String.Format("{0}, {1}", type.FullName, assembly);

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
                        UnresolvedTypes.Add(baseTypeAFQN);
                    }
                }

                // Add to the repository
                RepositoryAccess.AddType(ti);
                ResolvedTypes.Add(typeAFQN);

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
                        UnresolvedTypes.Add(fieldTypeAFQN);
                    }

                    fe.IsPrivate = field.Attributes == Mono.Cecil.FieldAttributes.Private;
                    fe.IsPublic = field.Attributes == Mono.Cecil.FieldAttributes.Public;
                    fe.IsStatic = field.IsStatic;

                    // Add to the repository
                    RepositoryAccess.AddField(ti, fe);

                    // Add attributes for field
                    ExtractAttributeElements(fe, field.CustomAttributes);
                }
                
                foreach (MethodDefinition method in type.Methods)
                {
                    // Create a new method element
                    MethodElement mi = new MethodElement(System.Guid.NewGuid().ToString());    
                    mi.Signature = method.ToString(); 
                    mi.Name = method.Name;                    
                    mi.ReturnType = method.ReturnType.ReturnType.ToString();

                    // If the return type has not yet been resolved, add it to the list of unresolved types
                    String returnTypeAFQN = CreateTypeAFQN(_targetAssemblyDefinition, method.ReturnType.ReturnType); 
                    if (!UnresolvedTypes.Contains(returnTypeAFQN) && !ResolvedTypes.Contains(returnTypeAFQN))
                    {
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
                    RepositoryAccess.AddMethod(ti, mi);

                    ExtractAttributeElements(mi, method.CustomAttributes);
                    
                    // Add the parameters
                    foreach (ParameterDefinition param in method.Parameters)
                    {
                        // Create a new parameter element
                        ParameterElement pe = new ParameterElement();
                        pe.ParentMethodId = mi.Id;
                        pe.Name = param.Name;
                        pe.Ordinal = (short)(param.Sequence);
                        pe.ParameterType = param.ParameterType.ToString();

                        // If the parameter type has not yet been resolved, add it to the list of unresolved types
                        String parameterTypeAFQN = CreateTypeAFQN(_targetAssemblyDefinition, param.ParameterType);
                        if (!UnresolvedTypes.Contains(parameterTypeAFQN) && !ResolvedTypes.Contains(parameterTypeAFQN))
                        {
                            UnresolvedTypes.Add(parameterTypeAFQN);
                        }

                        pe.IsIn = param.Attributes == ParamAttributes.In;
                        pe.IsOptional = param.Attributes == ParamAttributes.Optional;
                        pe.IsOut = param.Attributes == ParamAttributes.Out;                          
                        pe.IsRetVal = !param.ParameterType.FullName.Equals("System.Void", StringComparison.CurrentCultureIgnoreCase);

                        // Add to the method
                        RepositoryAccess.AddParameter(mi, pe);                         
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
                               RepositoryAccess.AddCallToMethod(mi, ce); 
                           }
                        }
                    }
                }
            }

            // Perhaps unresolved types are already in the local datastore from a previous run
            //Console.WriteLine("Resolving types from datastore...");
            IList<String> types = new List<String>(UnresolvedTypes);
            foreach (String type in types)
            {
                TypeElement te = RepositoryAccess.GetTypeElementByAFQN(type.Substring(0, type.IndexOf(", ")), type.Substring(type.IndexOf(", ")+2));
                if (te != null)
                {
                    UnresolvedTypes.Remove(type);
                }
            }



            //IList<TypeElement> ret = RepositoryAccess.GetTypeElements();
            
            //_repositoryAccess.CloseDatabase();
        }

        public void ProcessUnresolvedTypes()
        {
            Stopwatch sw = new Stopwatch();
            sw.Start();

            CacheAccess cache = new CacheAccess(Repository.Db4oContainers.Db4oCacheContainer.Instance, _cacheFolder);
            
            // Retrieve type elements from cache
            IList<String> types = new List<String>(UnresolvedTypes);
            foreach (String type in types)
            {
                TypeElement te = cache.GetTypeElementByAFQN(type.Substring(0, type.IndexOf(", ")), type.Substring(type.IndexOf(", ") + 2));

                // Type was found in the caches
                if (te != null)
                {
                    // Copy type from cache to local datastore
                    Console.WriteLine("Copying: "+te.FullName);
                    RepositoryAccess.AddType(te);

                    // Copy the type fields
                    foreach (FieldElement fe in cache.GetFieldElements(te))
                    {
                        RepositoryAccess.AddField(te, fe);
                    }

                    /// Copy the type methods
                    foreach (MethodElement me in cache.GetMethodElements(te))
                    {
                        RepositoryAccess.AddMethod(te, me);

                        // Copy the method parameters
                        foreach (ParameterElement pe in cache.GetParameterElements(te, me))
                        {
                            RepositoryAccess.AddParameter(me, pe);
                        }
                    }

                    UnresolvedTypes.Remove(type);
                }
            }

            //// ???
            //List<String> unresolvedAssemblies = new List<string>();

            //// Collect the assembly names of the unresolved types
            //types = new List<String>(UnresolvedTypes);
            //foreach (String type in types)
            //{
            //    if (type.IndexOf(",") > -1)
            //    {
            //        String assemblyName = type.Substring(type.IndexOf(", ") + 1);
            //        if (!unresolvedAssemblies.Contains(assemblyName)) unresolvedAssemblies.Add(assemblyName);
            //    }
            //}

            //// 
            //DefaultAssemblyResolver dar = new DefaultAssemblyResolver();
            //foreach (String assemblyName in unresolvedAssemblies)
            //{
            //    AssemblyDefinition ad = dar.Resolve(assemblyName);
            //    if (ad != null)
            //    {
            //        //Console.WriteLine(String.Format("Analyzing '{0}'...", ad.Name.FullName));
            //    }
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
            


            cache.CloseContainer();
            ////RepositoryAccess.CloseDatabase();
            //Console.WriteLine("WARNING: Explicitly clearing the unresolved types list, types have NOT been resolved!");
            //UnresolvedTypes.Clear();

            sw.Stop();
            _lastDuration = sw.Elapsed; 
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
            if (_repositoryAccess != null)
                _repositoryAccess.CloseContainer();
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
