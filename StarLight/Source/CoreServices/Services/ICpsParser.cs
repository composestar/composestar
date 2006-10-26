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
        /// Gets the referenced types.
        /// </summary>
        /// <value>The referenced types.</value>
        List<String> ReferencedTypes { get; }

        /// <summary>
        /// Gets a value indicating whether this instance has output filters.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance has output filters; otherwise, <c>false</c>.
        /// </value>
        bool HasOutputFilters { get; }

        /// <summary>
        /// Parses the file.
        /// </summary>
        void Parse();

    }
}
