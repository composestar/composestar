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

using Mono.Cecil;
using Mono.Cecil.Binary;
using Mono.Cecil.Cil;
using Mono.Cecil.Metadata;
using Mono.Cecil.Signatures;

using Composestar.Repository;
using Composestar.StarLight.LanguageModel;
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions;
using Composestar.StarLight.Configuration;   

using Composestar.StarLight.ContextInfo.FilterTypes;
using Composestar.StarLight.Utilities.Cecil;

namespace Composestar.StarLight.ILAnalyzer
{
    /// <summary>
    /// An implementation of the IILAnalyzer working with Cecil.
    /// </summary>
    public class CecilILAnalyzer : IILAnalyzer
    {

        #region Private Variables
      
        private TimeSpan _lastDuration = TimeSpan.Zero;
        private List<String> _resolvedTypes = new List<String>();
        private List<String> _unresolvedTypes = new List<String>();
        private List<String> _cachedTypes = new List<String>();
        private bool _saveType = false;
        private bool _saveInnerType = false;
        private bool _processAttributes = false;
        private string _binFolder = "";

        private CecilAnalyzerConfiguration _configuration;
        private IEntitiesAccessor _entitiesAccessor;

        private StarLightAssemblyResolver _dar;
 
        private List<FilterTypeElement> _filterTypes = new List<FilterTypeElement>();
        private List<FilterActionElement> _filterActions = new List<FilterActionElement>();


        #endregion

        #region ctor

        /// <summary>
        /// Initializes a new instance of the <see cref="T:CecilILAnalyzer"/> class.
        /// </summary>
        /// <param name="configuration">The configuration.</param>
        /// <param name="languageModelAccessor">The language model accessor.</param>
        public CecilILAnalyzer(CecilAnalyzerConfiguration configuration, IEntitiesAccessor entitiesAccessor)
        {
            #region Check for null values

            if (configuration == null) throw new ArgumentNullException("configuration");
     
            #endregion

            _configuration = configuration;
            _entitiesAccessor = entitiesAccessor;

        }

        #endregion

        #region Properties

        /// <summary>
        /// Gets the assembly resolver.
        /// </summary>
        /// <value>The assembly resolver.</value>
        private StarLightAssemblyResolver AssemblyResolver
        {
            get
            {
                if (_dar == null)
                {
                    lock (this)
                    {
                        if (_dar == null)
                        {
                            _dar = new StarLightAssemblyResolver(_configuration.BinFolder); 
                        } // if
                    } // lock

                } // if
                return _dar;
            } 
        }

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

        /// <summary>
        /// Gets all encountered FilterTypes
        /// </summary>
        /// <value></value>
        public List<FilterTypeElement> FilterTypes
        {
            get
            {
                return _filterTypes;
            }
        }

        /// <summary>
        /// Gets all encountered FilterActions
        /// </summary>
        /// <value></value>
        public List<FilterActionElement> FilterActions
        {
            get
            {
                return _filterActions;
            }
        }

        #endregion

        #region IILAnalyzer Implementation

        /// <summary>
        /// Extracts all types.
        /// </summary>
        /// <param name="fileName">Name of the file.</param>
        /// <returns></returns>
        /// <exception cref="ArgumentException">If the filename is not specified this exception is thrown.</exception>
        /// <exception cref="FileNotFoundException">If the source file cannot be found, this exception will be thrown.</exception>
        public AssemblyElement ExtractAllTypes(String fileName)
        {

            #region Checks for null and file exists

            if (String.IsNullOrEmpty(fileName))
                throw new ArgumentException(string.Format(CultureInfo.CurrentCulture, Properties.Resources.FileNameNullOrEmpty));

            if (!File.Exists(fileName))
            {
                throw new FileNotFoundException(string.Format(CultureInfo.CurrentCulture, Properties.Resources.FileNotFound, fileName));
            }
            
            #endregion
           
            AssemblyElement result = null;

            // Create a stopwatch for timing
            Stopwatch sw = new Stopwatch();

            // Create the visitor
            CecilAssemblyVisitor visitor = new CecilAssemblyVisitor();
            
            // Set visitor properties
            visitor.ProcessMethodBody = _configuration.DoMethodCallAnalysis;
            visitor.IncludeFields = _configuration.DoFieldAnalysis;
            visitor.SaveInnerType = true;
            visitor.SaveType = true;
            visitor.ResolvedTypes = _resolvedTypes;
            visitor.UnresolvedTypes = _unresolvedTypes; 

            // Start the visitor
            result = visitor.Analyze(fileName);

            // Update the unresolved types
            _unresolvedTypes = visitor.UnresolvedTypes;
            _resolvedTypes = visitor.ResolvedTypes;  

            // Update the filtertypes
            _filterTypes.AddRange(visitor.FilterTypes);
            _filterActions.AddRange(visitor.FilterActions);
                     
            // Stop the timer
            sw.Stop();
            _lastDuration = sw.Elapsed;

            // Return the result
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
            
            if (UnresolvedTypes.Count > 0)
            {
                // Add all assemblies we can resolve from the AFQN type names
                foreach (String type in UnresolvedTypes)
                {
                    String assemblyName = null;
                    if (type.Contains(", "))
                        assemblyName = type.Substring(type.IndexOf(", ") + 2);
                    if (assemblyName != null && !assemblyNames.ContainsKey(assemblyName))
                        assemblyNames.Add(assemblyName, String.Empty);
                }

                if (assemblyNames.Count == 0) 
                    return new List<AssemblyElement>();
               
                // Go through each assembly name
                foreach (String assemblyName in assemblyNames.Keys)
                {
                    // TODO add to resource or remove the CW
                    Console.WriteLine(String.Format("Analyzing '{0}', please wait...", assemblyName));

                    try
                    {
                        AssemblyDefinition ad = AssemblyResolver.Resolve(assemblyName);

                        if (ad != null)
                        {
                            // Create the visitor
                            CecilAssemblyVisitor visitor = new CecilAssemblyVisitor();

                            // Set visitor properties
                            visitor.ProcessMethodBody = false;
                            visitor.IncludeFields = _configuration.DoFieldAnalysis;
                            visitor.SaveType = false;
                            visitor.ResolvedTypes = _resolvedTypes;
                            visitor.UnresolvedTypes = _unresolvedTypes;

                            // Start the visitor
                            AssemblyElement result = visitor.Analyze(assemblyNames[assemblyName]);

                            if (result.Types.Count > 0)
                            {
                                assemblies.Add(result);
                            } // if

                            // Add the unresolved types
                            _unresolvedTypes = visitor.UnresolvedTypes;
                            _resolvedTypes = visitor.ResolvedTypes;

                            // Update the filtertypes
                            _filterTypes.AddRange(visitor.FilterTypes);
                            _filterActions.AddRange(visitor.FilterActions);

                        } // if
                        else
                        {                            
                            throw new ILAnalyzerException(String.Format(Properties.Resources.UnableToResolveAssembly, assemblyName), assemblyName);
                        } // else
                    } // try
                    catch (Exception ex)
                    {                        
                        throw new ILAnalyzerException(String.Format(Properties.Resources.UnableToResolveAssembly, assemblyName), assemblyName, ex);
                    } // catch
                } // foreach  (assemblyName)

                if (UnresolvedTypes.Count > 0)
                {
                    ProcessUnresolvedTypes(new Dictionary<string, string>());
                } // if
            }

            sw.Stop();
            _lastDuration = sw.Elapsed;

            return assemblies;
        }

        /// <summary>
        /// Closes this instance. Cleanup any used resources.
        /// </summary>
        public void Close()
        {
            if (_dar != null)
                _dar = null;
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

        #endregion

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
