using System;
using System.Collections.Generic;
using System.Text;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities.Configuration;
  
namespace Composestar.StarLight.CoreServices
{

    /// <summary>
    /// Interface for the IL analyzer
    /// </summary>
    public interface IILAnalyzer
    {

        /// <summary>
        /// Gets the unresolved assemblies.
        /// </summary>
        /// <value>The unresolved assemblies.</value>
        List<string> UnresolvedAssemblies { get; }

        /// <summary>
        /// Gets the resolved assemblies.
        /// </summary>
        /// <value>The resolved assemblies.</value>
        List<string> ResolvedAssemblies { get; }
        
        /// <summary>
        /// Gets or sets the unresolved types.
        /// </summary>
        /// <value>The unresolved types.</value>
        List<string> UnresolvedTypes { get; set; }
        
        /// <summary>
        /// Gets the resolved types.
        /// </summary>
        /// <value>The resolved types.</value>
        List<string> ResolvedTypes { get; }

        /// <summary>
        /// Resolves the assembly locations.
        /// </summary>
        /// <returns></returns>
        List<String> ResolveAssemblyLocations();

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
        /// Closes this instance. Cleanup any used resources.
        /// </summary>
        void Close();
    }
}
