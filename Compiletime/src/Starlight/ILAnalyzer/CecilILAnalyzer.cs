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

        private string _fileName;
        private bool _isInitialized = false;
        private TimeSpan _lastDuration=TimeSpan.MinValue;
        private AssemblyDefinition _targetAssemblyDefinition;
        private RepositoryAccess _repositoryAccess ;

        /// <summary>
        /// Initializes the analyzer with the specified assembly name.
        /// </summary>
        /// <param name="fileName">Name of the file.</param>
        /// <param name="config">The config.</param>
        public void Initialize(string fileName, NameValueCollection config)
        {
            #region Filename
            if (String.IsNullOrEmpty(fileName))
                throw new ArgumentNullException("fileName", Properties.Resources.FileNameNullOrEmpty);

            if (!File.Exists(fileName))
                throw new ArgumentException(String.Format(Properties.Resources.FileNotFound, fileName), "fileName");

            try
            {
                _targetAssemblyDefinition = AssemblyFactory.GetAssembly(fileName);
            }
            catch (EndOfStreamException)
            {
                throw new BadImageFormatException(String.Format(Properties.Resources.ImageIsBad, fileName));
            }
            #endregion

            #region Config
            string repositoryFilename = string.Empty;
            if (config != null)
            {
                repositoryFilename = config.Get("RepositoryFilename"); 
            }

            if (string.IsNullOrEmpty(repositoryFilename))
                throw new ArgumentException(Properties.Resources.RepositoryFilenameNotSpecified, "RepositoryFilename");
 
            _repositoryAccess = new RepositoryAccess(repositoryFilename);

            #endregion

            _fileName = fileName;
                        
            _isInitialized = true;

        }

        public RepositoryAccess RepositoryAccess
        {
            get { return _repositoryAccess; }
        }

        private void ExtractAttributeElements(IRepositoryElement parent, CustomAttributeCollection attributes)
        {
            foreach (CustomAttribute attr in attributes)
            {
                AttributeElement ae = new AttributeElement();
                ae.Type = attr.Constructor.Name;
                ae.Value = attr.ConstructorParameters[0].ToString();

                RepositoryAccess.AddAttribute(parent, ae);
            }
        }
      
        /// <summary>
        /// Extracts the types.
        /// </summary>
        /// <returns></returns>
        public IList<TypeElement> ExtractTypeElements()
        {
            CheckForInit();
                        
            Stopwatch sw = new Stopwatch();
            sw.Start();            
           

            //Gets all types of the MainModule of the assembly
            foreach (TypeDefinition type in _targetAssemblyDefinition.MainModule.Types)
            {
                TypeElement ti = new TypeElement();
                
                // Name
                ti.Name = type.Name;
                ti.FullName = type.FullName;

                // Properties
                ti.IsAbstract = type.IsAbstract;
                ti.IsEnum = type.IsEnum;
                ti.IsInterface = type.IsInterface;
                ti.IsSealed = type.IsSealed;
                ti.IsValueType = type.IsValueType;
                ti.FromDLL = _fileName;
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
                if (type.BaseType != null) { ti.BaseType = type.BaseType.Name; }

                // Add to the repository
                RepositoryAccess.AddType(ti);

                // Add attributes for type
                ExtractAttributeElements(ti, type.CustomAttributes);

                foreach (FieldDefinition field in type.Fields)
                {
                    // Create a new field element
                    FieldElement fe = new FieldElement();
                    fe.Name = field.Name;
                    fe.Type = field.FieldType.FullName;
                    fe.IsPrivate = field.Attributes == Mono.Cecil.FieldAttributes.Private;
                    fe.IsPublic = field.Attributes == Mono.Cecil.FieldAttributes.Public;
                    fe.IsStatic = field.IsStatic;

                    // Add to the repository
                    RepositoryAccess.AddField(ti, fe);

                    // Add attributes for field
                    //sExtractAttributeElements(fe, field.CustomAttributes);
                }
                
                foreach (MethodDefinition method in type.Methods)
                {
                    // Create a new method element
                    MethodElement mi = new MethodElement();    
                    mi.Signature = SignatureBuilder.MethodSignature(method.Name, method.ReturnType.ReturnType.ToString(), GetParameterTypesList(method)); 
                    mi.Name = method.Name;                    
                    mi.ReturnType = method.ReturnType.ReturnType.ToString();
                    mi.IsAbstract = method.IsAbstract;
                    mi.IsConstructor = method.IsConstructor;
                    mi.IsPrivate = method.Attributes == Mono.Cecil.MethodAttributes.Private;
                    mi.IsPublic = method.Attributes == Mono.Cecil.MethodAttributes.Public;
                    mi.IsStatic = method.IsStatic;
                    mi.IsVirtual = method.IsVirtual;
                    
                    // Add to the repositorys
                    RepositoryAccess.AddMethod(ti, mi);

                    //ExtractAttributeElements(mi, method.CustomAttributes);
                    
                    // Add the parameters
                    foreach (ParameterDefinition param in method.Parameters)
                    {
                        // Create a new parameter element
                        ParameterElement pe = new ParameterElement();
                        pe.ParentMethodId = mi.Id;
                        pe.Name = param.Name;
                        pe.Ordinal = (short)(param.Sequence);
                        pe.ParameterType = param.ParameterType.ToString();
                        pe.IsIn = param.Attributes == ParamAttributes.In;
                        pe.IsOptional = param.Attributes == ParamAttributes.Optional;
                        pe.IsOut = param.Attributes == ParamAttributes.Out;                          
                        pe.IsRetVal = !param.ParameterType.FullName.Equals("System.Void", StringComparison.CurrentCultureIgnoreCase);

                        // Add to the method
                        RepositoryAccess.AddParameter(mi, pe);                         
                    }

                    if (method.Body != null)
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

            sw.Stop();
            _lastDuration = sw.Elapsed; 

            IList<TypeElement> ret = RepositoryAccess.GetTypeElements();
            
            _repositoryAccess.CloseDatabase(); 

            return ret;
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
                _repositoryAccess.CloseDatabase();
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
