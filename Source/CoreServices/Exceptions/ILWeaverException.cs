using System;
using System.Runtime.Serialization;
 
namespace Composestar.StarLight.CoreServices.Exceptions
{

    /// <summary>
    /// Exception throw by the weaver.
    /// </summary>
    [Serializable()]
    public class ILWeaverException : StarLightException, ISerializable
    {
        private readonly string _filename;

        #region ctor

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ILWeaverException"/> class.
        /// </summary>
        public ILWeaverException()
        {
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ILWeaverException"/> class.
        /// </summary>
        /// <param name="serInfo">The ser info.</param>
        /// <param name="streamContext">The stream context.</param>
        protected ILWeaverException(SerializationInfo serializationInformation, StreamingContext streamContext) 
            : base(serializationInformation, streamContext)
        {
            _filename = serializationInformation.GetString("ILWeaverException._filename");
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ILWeaverException"/> class.
        /// </summary>
        /// <param name="message">The message.</param>
        public ILWeaverException(string message)
            : base(message)
        {
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ILWeaverException"/> class.
        /// </summary>
        /// <param name="message">The message.</param>
        /// <param name="inner">The inner.</param>
        public ILWeaverException(string message, Exception inner)
            : base(message, inner)
        {
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ILWeaverException"/> class.
        /// </summary>
        /// <param name="message">The message.</param>
        /// <param name="filename">The filename.</param>
        public ILWeaverException(string message, string filename)
            : base(message)
        {
            _filename = filename;
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ILWeaverException"/> class.
        /// </summary>
        /// <param name="message">The message.</param>
        /// <param name="filename">The filename.</param>
        /// <param name="inner">The inner.</param>
        public ILWeaverException(string message, string filename, Exception inner)
            : base(message, inner)
        {
            _filename = filename;
        }

        /// <summary>
        /// Gets the filename.
        /// </summary>
        /// <value>The filename.</value>
        public string Filename
        {
            get { return _filename; }
        }
        #endregion

        #region ISerializable Members

        void ISerializable.GetObjectData(SerializationInfo info, StreamingContext context)
        {
            info.AddValue("ILWeaverException._filename", _filename);
            base.GetObjectData(info, context);
        }

        #endregion
    }
}
