using System;
using System.Collections.Generic;
using System.Collections.Specialized;   
using System.Text;

using Composestar.Repository;   

namespace Composestar.StarLight.ILWeaver
{
    /// <summary>
    /// Interface for the IL weaver
    /// </summary>
    public interface IILWeaver
    {

        /// <summary>
        /// Initializes the weaver with the specified assembly name.
        /// </summary>
        /// <param name="fileName">Name of the file.</param>
        /// <param name="config">The config settings.</param>
        void Initialize(string inputImage, NameValueCollection config );

        /// <summary>
        /// Does the actual weaving.
        /// </summary>
        void DoWeave();
        
        /// <summary>
        /// Gets the duration of the last executed method.
        /// </summary>
        /// <value>The last duration.</value>
        TimeSpan LastDuration{ get; }

        /// <summary>
        /// Gets access to the repository.
        /// </summary>
        /// <value>The repository access.</value>
        RepositoryAccess RepositoryAccess { get; }

        /// <summary>
        /// Closes this instance. Cleanup any used resources.
        /// </summary>
        void Close();
    }
}
