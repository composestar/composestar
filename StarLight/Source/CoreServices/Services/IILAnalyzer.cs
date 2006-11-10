using System;
using System.Collections.Generic;
using System.Text;
using System.Collections.ObjectModel;
  
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities.Configuration;
  
namespace Composestar.StarLight.CoreServices
{

    /// <summary>
    /// Interface for the IL analyzer
    /// </summary>
    public interface IILAnalyzer :  IDisposable 
    {

        /// <summary>
        /// Gets the unresolved assemblies.
        /// </summary>
        /// <value>The unresolved assemblies.</value>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1002")]
        IList<string> UnresolvedAssemblies { get; }

        /// <summary>
        /// Gets the resolved assemblies.
        /// </summary>
        /// <value>The resolved assemblies.</value>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1002")]
        IList<string> ResolvedAssemblies { get; }
        
        /// <summary>
        /// Gets or sets the unresolved types.
        /// </summary>
        /// <value>The unresolved types.</value>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2227")]
        List<string> UnresolvedTypes { get; set; }
        
        /// <summary>
        /// Gets the resolved types.
        /// </summary>
        /// <value>The resolved types.</value>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1002")]
        IList<string> ResolvedTypes { get; }

        /// <summary>
        /// Resolves the assembly locations.
        /// </summary>
        /// <returns></returns>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1002")]
        IList<String> ResolveAssemblyLocations();

        /// <summary>
        /// Extracts all types.
        /// </summary>
        /// <param name="fileName">Name of the file.</param>
        /// <returns></returns>
        AssemblyElement ExtractAllTypes(String fileName);

        /// <summary>
        /// Gets all encountered FilterTypes
        /// </summary>
        ReadOnlyCollection<FilterTypeElement> FilterTypes { get; }
        
        /// <summary>
        /// Gets all encountered FilterActions
        /// </summary>
        ReadOnlyCollection<FilterActionElement> FilterActions { get; }
              
    }
}
