using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Text;

using Composestar.Repository.LanguageModel;
using Composestar.Repository;

namespace Composestar.StarLight.CoreServices
{
    public enum IlAnalyzerResults
    {
        FromAssembly = 0,
        FromCache = 1
    }


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
        /// <exception cref="ArgumentException"></exception>
        /// <exception cref="FileNotFoundException"></exception>
        AssemblyElement ExtractAllTypes(String fileName);

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
