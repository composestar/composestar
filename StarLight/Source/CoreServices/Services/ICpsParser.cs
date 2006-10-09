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

        /// <summary>
        /// Parses the file.
        /// </summary>
        /// <param name="fileName">Name of the file.</param>
        void ParseFile(String fileName);

        /// <summary>
        /// Parses the file for referenced types.
        /// </summary>
        /// <param name="fileName">Name of the file.</param>
        /// <returns></returns>
        List<String> ParseFileForReferencedTypes(String fileName);

    }
}
