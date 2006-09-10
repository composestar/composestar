using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Diagnostics;
using System.IO;
using System.Reflection;
using System.Reflection.Emit;
using System.Text;

using Composestar.StarLight.ILAnalyzer.ILInstructions;

namespace Composestar.StarLight.ILAnalyzer
{

    /// <summary>
    /// An implementation of the IILAnalyzer working with the default dotNET reflection.
    /// </summary>
    public class ReflectionILAnalyzer : IILAnalyzer
    {

        private string _fileName;
        private bool _isInitialized = false;
        private TimeSpan _lastDuration = TimeSpan.MinValue;

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

            Assembly assembly;
            try
            {
                assembly = Assembly.LoadFile(_fileName);
            }
            catch (BadImageFormatException ex)
            {
                throw;
            }
            catch (FileLoadException ex)
            {
                throw;
            }
            catch (FileNotFoundException ex)
            {
                throw new ArgumentException(String.Format(Properties.Resources.FileNotFound, _fileName), ex);
            }
            catch (ArgumentNullException ex)
            {
                throw new ArgumentNullException(Properties.Resources.FileNameNullOrEmpty, ex);
            }

            //Gets all types of the MainModule of the assembly
            foreach (Type type in assembly.GetTypes())
            {
                foreach (MethodInfo method in type.GetMethods())
                {
                    // Create a new method element
                    MethodElement me = new MethodElement();
                    me.MethodName = String.Format("{0}.{1}", type.FullName, method.Name);
                    me.ReturnType = method.ReturnType.FullName;

                    // Add the parameters
                    foreach (ParameterInfo param in method.GetParameters())
                    {
                        // Create a new parameter element
                        ParameterElement pe = new ParameterElement();
                        pe.Name = param.Name;
                        pe.Ordinal = (short)(param.Position);
                        pe.ParameterType = param.ParameterType.FullName;

                        // Add to the method
                        me.Parameters.Add(pe);
                    }

                    foreach (ILInstruction il in new ILReader(method))
                    {
                        /* do something with il */
                        if (il.OpCode == OpCodes.Call)
                        {
                          //  Console.WriteLine("Call");
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
            return "Reflection IL Analyzer";
        }
    }
}
