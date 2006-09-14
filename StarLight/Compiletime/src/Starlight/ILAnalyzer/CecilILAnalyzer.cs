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
                
                ti.Name = type.Name;
                ti.FullName = type.FullName;
                ti.IsAbstract = type.IsAbstract;
                ti.IsEnum = type.IsEnum;
                ti.IsInterface = type.IsInterface;
                ti.IsSealed = type.IsSealed;
                ti.IsValueType = type.IsValueType;
                
                if (type.BaseType != null) { ti.BaseType = type.BaseType.Name; }

                // Add to the repository
                RepositoryAccess.AddType(ti);
                
                foreach (MethodDefinition method in type.Methods)
                {
                    // Create a new method element
                    MethodElement mi = new MethodElement();                
                    mi.Name = method.Name;                    
                    mi.ReturnType = method.ReturnType.ReturnType.ToString();
                    
                    // Add to the repository
                    RepositoryAccess.AddMethod(ti, mi);
                    
                    // Add the parameters
                    foreach (ParameterDefinition param in method.Parameters)
                    {
                        // Create a new parameter element
                       ParameterElement pe = new ParameterElement();
                        pe.ParentMethodId = mi.Id;
                        pe.Name = param.Name;
                        pe.Ordinal = (short)(param.Sequence);
                        pe.ParameterType = param.ParameterType.ToString();
                          
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

            return RepositoryAccess.GetTypeElements();
        }

        /// <summary>
        /// Checks for initialization. Throw exception when not inited.
        /// </summary>
        private void CheckForInit()
        {
            if (!_isInitialized)
                throw new ApplicationException(Properties.Resources.NotYetInitialized);
            
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
