using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Diagnostics;
using System.IO;
using System.Reflection;
using System.Reflection.Emit;
using System.Text;

using Composestar.StarLight.ILAnalyzer.ILInstructions;
using Composestar.Repository.LanguageModel;
using Composestar.Repository;

namespace Composestar.StarLight.ILAnalyzer
{

    /// <summary>
    /// An implementation of the IILAnalyzer working with the default dotNET reflection.
    /// </summary>
    public class ReflectionILAnalyzer
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

        public int UnresolvedTypeCount
        {
            get { return 0; }
        }
         
        /// <summary>
        /// Extracts the types.
        /// </summary>
        /// <returns></returns>
        public IList<TypeElement> ExtractTypeElements()
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
        /// Closes this instance. Cleanup any used resources.
        /// </summary>
        public void Close()
        {
           
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
