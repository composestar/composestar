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

using Mono.Cecil;
using Mono.Cecil.Binary;
using Mono.Cecil.Cil;
using Mono.Cecil.Metadata;
using Mono.Cecil.Signatures;

using Composestar.Repository;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions;
using Composestar.StarLight.Entities.Configuration;   

using Composestar.StarLight.Filters.FilterTypes;
using Composestar.StarLight.Utilities.Cecil;
#endregion

namespace Composestar.StarLight.ILAnalyzer
{
    /// <summary>
    /// An implementation of the IILAnalyzer working with Cecil.
    /// </summary>
    public class CecilILAnalyzer : IILAnalyzer
    {

        #region Private Variables
      
        private TimeSpan _lastDuration = TimeSpan.Zero;
        private List<String> _resolvedAssemblies = new List<String>();
        private List<String> _unresolvedAssemblies = new List<String>();
        private List<String> _resolvedTypes = new List<String>();
        private List<String> _unresolvedTypes = new List<String>();
        private List<String> _cachedTypes = new List<String>();

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
        /// Gets or sets the resolved types.
        /// </summary>
        /// <value>The resolved types.</value>
        public List<String> ResolvedTypes
        {
            get
            {
                return _resolvedTypes;
            }
        }

        /// <summary>
        /// Gets or sets the unresolved types.
        /// </summary>
        /// <value>The unresolved types.</value>
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
        /// Gets the unresolved assemblies.
        /// </summary>
        /// <value>The unresolved assemblies.</value>
        public List<String> UnresolvedAssemblies
        {
            get { return _unresolvedAssemblies; }
        }

        /// <summary>
        /// Gets the resolved assemblies.
        /// </summary>
        /// <value>The resolved assemblies.</value>
        public List<String> ResolvedAssemblies
        {
            get { return _resolvedAssemblies; }
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
            visitor.ExtractUnresolvedOnly = _configuration.ExtractUnresolvedOnly;  
            visitor.SaveInnerType = true;
            visitor.SaveType = true;
            visitor.ResolvedAssemblies = _resolvedAssemblies;
            visitor.UnresolvedAssemblies = _unresolvedAssemblies;
            visitor.UnresolvedTypes = _unresolvedTypes;
            visitor.ResolvedTypes = _resolvedTypes; 

            // Start the visitor
            result = visitor.Analyze(fileName);

            // Update the unresolved types
            _unresolvedAssemblies = visitor.UnresolvedAssemblies;
            _resolvedAssemblies = visitor.ResolvedAssemblies;
            _unresolvedTypes = visitor.UnresolvedTypes;
            _resolvedTypes = visitor.ResolvedTypes;  

            // Update the filtertypes
            _filterTypes =visitor.FilterTypes;
            _filterActions =visitor.FilterActions;
                     
            // Stop the timer
            sw.Stop();
            _lastDuration = sw.Elapsed;

            // Return the result
            return result;
        }

        /// <summary>
        /// Resolve assembly locations
        /// </summary>
        /// <returns>List</returns>
        public List<String> ResolveAssemblyLocations()
        {
            List<string> ret = new List<string>();

            // Go through each assembly name
            foreach (String assemblyName in _unresolvedAssemblies)
            {           
                try
                {
                    AssemblyDefinition ad = AssemblyResolver.Resolve(assemblyName);

                    if (ad != null)
                    {
                        ret.Add(ad.MainModule.Image.FileInformation.FullName);
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


            return ret;
        } // ResolveAssemblyLocations()
          
        
        /// <summary>
        /// Closes this instance. Cleanup any used resources.
        /// </summary>
        public void Close()
        {
            if (_dar != null)
                _dar = null;
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
