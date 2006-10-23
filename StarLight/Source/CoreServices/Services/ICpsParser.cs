using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.CoreServices
{
    /// <summary>
    /// Interface for the CpsParser
    /// </summary>
    public interface ICpsParser
    {
        List<String> ReferencedTypes { get; }

        bool HasOutputFilters { get; }

        /// <summary>
        /// Parses the file.
        /// </summary>
        /// <param name="fileName">Name of the file.</param>
        void Parse();

    }
}
