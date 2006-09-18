using System;
using System.Collections.Generic;
using System.Collections.Specialized;   
using System.Text;

using Composestar.Repository.LanguageModel;
using Composestar.Repository;

namespace Composestar.StarLight.ILAnalyzer
{
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
        void Initialize(string fileName, NameValueCollection config );
       
        /// <summary>
        /// Extracts the types.
        /// </summary>
        /// <returns></returns>
        IList<TypeElement> ExtractTypeElements();

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
