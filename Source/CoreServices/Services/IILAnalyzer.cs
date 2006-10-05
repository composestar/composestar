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
        FROM_ASSEMBLY = 0,
        FROM_CACHE = 1
    }


    /// <summary>
    /// Interface for the IL analyzer
    /// </summary>
    public interface IILAnalyzer
    {

        /// <summary>
        /// Initializes the analyzer with the specified assembly name.
        /// </summary>
        /// <param name="fileName">Name of the file.</param>
        /// <param name="config">The config settings.</param>
        void Initialize(NameValueCollection config );

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
        /// Extracts the types.
        /// </summary>
        /// <returns></returns>
        IlAnalyzerResults ExtractTypeElements(String fileName);

        /// <summary>
        /// Processes the unresolved types.
        /// </summary>
        void ProcessUnresolvedTypes();

        /// <summary>
        /// Gets the duration of the last executed method.
        /// </summary>
        /// <value>The last duration.</value>
        TimeSpan LastDuration{ get; }

        /// <summary>
        /// Closes this instance. Cleanup any used resources.
        /// </summary>
        void Close();
    }
}
