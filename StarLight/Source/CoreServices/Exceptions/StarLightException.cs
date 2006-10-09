using System;
using System.Runtime.Serialization;

namespace Composestar.StarLight.CoreServices.Exceptions
{
    /// <summary>
    /// Base type for the starlight exceptions.
    /// </summary>
    [Serializable]
    public class StarLightException : Exception, ISerializable
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="T:StarLightException"/> class.
        /// </summary>
        public StarLightException()
        {

        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:StarLightException"/> class.
        /// </summary>
        /// <param name="message">The message.</param>
        public StarLightException(string message) : base(message)
        {

        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:StarLightException"/> class.
        /// </summary>
        /// <param name="message">The message.</param>
        /// <param name="innerException">The inner exception.</param>
        public StarLightException(string message, Exception innerException) : base(message, innerException)
        {

        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:StarLightException"/> class.
        /// </summary>
        /// <param name="info">The info.</param>
        /// <param name="streamContext">The stream context.</param>
        public StarLightException(SerializationInfo info,StreamingContext streamContext) : base(info, streamContext ) 
        {

        }
    }
}
