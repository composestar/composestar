using System;
using System.Collections.Generic;
using System.Collections.Specialized;   
using System.Text;

using Composestar.Repository;

namespace Composestar.StarLight.CoreServices
{
    /// <summary>
    /// Interface for the IL weaver
    /// </summary>
    public interface IILWeaver 
    {
        /// <summary>
        /// Does the actual weaving.
        /// </summary>
        void DoWeave();
        
        /// <summary>
        /// Gets the duration of the last executed method.
        /// </summary>
        /// <value>The last duration.</value>
        TimeSpan LastDuration{ get; }

    }
}
