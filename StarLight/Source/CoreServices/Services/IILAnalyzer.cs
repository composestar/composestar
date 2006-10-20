using System;
using System.Collections.Generic;
using System.Text;
using Composestar.StarLight.LanguageModel;
using Composestar.StarLight.Configuration;
  
namespace Composestar.StarLight.CoreServices
{

    /// <summary>
    /// Interface for the IL analyzer
    /// </summary>
    public interface IILAnalyzer
    {

        /// <summary>
        /// Gets the unresolved types.
        /// </summary>
        /// <value>The unresolved types.</value>
        List<string> UnresolvedTypes { get; }

        /// <summary>
        /// Gets the resolved types.
        /// </summary>
        /// <value>The resolved types.</value>
        List<string> ResolvedTypes { get; }

        /// <summary>
        /// Processes the unresolved types.
        /// </summary>
        /// <param name="assemblyNames">The assembly names.</param>
        /// <returns>List of AssemblyElements</returns>
        List<AssemblyElement> ProcessUnresolvedTypes(Dictionary<String, String> assemblyNames);

        /// <summary>
        /// Extracts all types.
        /// </summary>
        /// <param name="fileName">Name of the file.</param>
        /// <returns></returns>
        AssemblyElement ExtractAllTypes(String fileName);

        /// <summary>
        /// Gets all encountered FilterTypes
        /// </summary>
        List<FilterTypeElement> FilterTypes { get; }
        
        /// <summary>
        /// Gets all encountered FilterActions
        /// </summary>
        List<FilterActionElement> FilterActions { get; }
        
        /// <summary>
        /// Gets the duration of the last executed method.
        /// </summary>
        /// <value>The last duration.</value>
        TimeSpan LastDuration { get; }

        /// <summary>
        /// Closes this instance. Cleanup any used resources.
        /// </summary>
        void Close();
    }
}
