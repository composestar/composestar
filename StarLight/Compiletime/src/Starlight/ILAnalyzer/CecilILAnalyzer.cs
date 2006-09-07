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

        /// <summary>
        /// Initializes the analyzer with the specified assembly name.
        /// </summary>
        /// <param name="fileName">Name of the file.</param>
        /// <param name="config">The config.</param>
        public void Initialize(string fileName, NameValueCollection config)
        {
            if (String.IsNullOrEmpty(fileName))
                throw new ArgumentNullException("fileName", Properties.Resources.FileNameNullOrEmpty);

            if (!File.Exists(fileName))
                throw new ArgumentException(String.Format(Properties.Resources.FileNotFound, fileName), "fileName");
             
            _fileName = fileName;

            _isInitialized = true;

        }

        /// <summary>
        /// Extracts the methods.
        /// </summary>
        /// <returns></returns>
        public List<MethodElement> ExtractMethods()
        {
            CheckForInit();

            List<MethodElement> retList = new List<MethodElement>();

            Stopwatch sw = new Stopwatch();
            sw.Start(); 

            //Gets the AssemblyDefinition of "_assemblyName"
            AssemblyDefinition assembly;
            try
            {
                assembly = AssemblyFactory.GetAssembly(_fileName);
            }
            catch (Exception ex) // TODO catch more specific exception
            {
                throw;
            }

            //Gets all types of the MainModule of the assembly
            foreach (TypeDefinition type in assembly.MainModule.Types)
            {
                foreach (MethodDefinition method in type.Methods)
                {
                    // Create a new method element
                    MethodElement me = new MethodElement();
                    me.MethodName = String.Format("{0}.{1}", type.Name, method.Name);
                    me.ReturnType = method.ReturnType.ReturnType.ToString();  

                    // Add the parameters
                    foreach (ParameterDefinition param in method.Parameters)
                    {
                        // Create a new parameter element
                        ParameterElement pe = new ParameterElement();
                        pe.Name = param.Name;
                        pe.Ordinal = (short)(param.Sequence);
                        pe.ParameterType = param.ParameterType.ToString();
                          
                        // Add to the method
                        me.Parameters.Add(pe);
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
                               CallConstruction cc = new CallConstruction();
                               MethodReference mr = (MethodReference)(instr.Operand);
                               cc.MethodReference = mr.ToString() ; 

                               // Add to the list of the method
                               me.Calls.Add(cc); 
                           }
                        }
                    }

                    // Add the method to the return list
                    retList.Add(me);
                }
            }            

            sw.Stop();
            _lastDuration = sw.Elapsed; 

            return retList;
        }

        /// <summary>
        /// Extracts the types.
        /// </summary>
        /// <returns></returns>
        public List<string> ExtractTypes()
        {
            CheckForInit();

            List<MethodElement> retList = new List<MethodElement>();

            Stopwatch sw = new Stopwatch();
            sw.Start(); 

            //Gets the AssemblyDefinition of "_assemblyName"
            AssemblyDefinition assembly;
            try
            {
                assembly = AssemblyFactory.GetAssembly(_fileName);
            }
            catch (Exception ex) // TODO catch more specific exception
            {
                throw;
            }
           

            //Gets all types of the MainModule of the assembly
            foreach (TypeDefinition type in assembly.MainModule.Types)
            {
                foreach (MethodDefinition method in type.Methods)
                {
                    // Create a new method element
                    MethodElement me = new MethodElement();
                    me.MethodName = String.Format("{0}.{1}", type.Name, method.Name);
                    me.ReturnType = method.ReturnType.ReturnType.ToString();  

                    // Add the parameters
                    foreach (ParameterDefinition param in method.Parameters)
                    {
                        // Create a new parameter element
                        ParameterElement pe = new ParameterElement();
                        pe.Name = param.Name;
                        pe.Ordinal = (short)(param.Sequence);
                        pe.ParameterType = param.ParameterType.ToString();
                          
                        // Add to the method
                        me.Parameters.Add(pe);
                    }

                  
                    // Add the method to the return list
                    retList.Add(me);
                }
            }            

            sw.Stop();
            _lastDuration = sw.Elapsed; 

            return null;
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
