using System;
using System.Collections.Generic;
using System.Collections.Specialized;   
using System.Text;

using Composestar.StarLight.CoreServices.ILWeaver;   

namespace Composestar.StarLight.CoreServices
{
    /// <summary>
    /// Interface for the IL weaver
    /// </summary>
    public interface IILWeaver : IDisposable
    {
        /// <summary>
        /// Does the actual weaving.
        /// </summary>
        WeaveStatistics DoWeave();
      
        /// <summary>
        /// Cleans up any resources associated with this instance.
        /// </summary>
        void Dispose();
    }
}
