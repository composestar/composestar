using System;
using System.Runtime.Serialization;
  
namespace Composestar.StarLight.CoreServices.Exceptions
{

    /// <summary>
    /// Exception throw by the CpsParser.
    /// </summary>
    [Serializable()]
    public class CpsParserException : StarLightException, ISerializable
    {

        private string _filename;

        #region ctor

        /// <summary>
        /// Initializes a new instance of the <see cref="T:CpsParserException"/> class.
        /// </summary>
        public CpsParserException()
        {
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:CpsParserException"/> class.
        /// </summary>
        /// <param name="serInfo">The ser info.</param>
        /// <param name="streamContext">The stream context.</param>
        protected CpsParserException(SerializationInfo serInfo, StreamingContext streamContext)
            : base(serInfo, streamContext)
        {
            _filename = serInfo.GetString("CpsParserException._filename");
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:CpsParserException"/> class.
        /// </summary>
        /// <param name="message">The message.</param>
        public CpsParserException(string message)
            : base(message)
        {
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:CpsParserException"/> class.
        /// </summary>
        /// <param name="message">The message.</param>
        /// <param name="inner">The inner.</param>
        public CpsParserException(string message, Exception inner)
            : base(message, inner)
        {
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:CpsParserException"/> class.
        /// </summary>
        /// <param name="message">The message.</param>
        /// <param name="filename">The filename.</param>
        public CpsParserException(string message, string filename)
            : base(message)
        {
            _filename = filename;
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:CpsParserException"/> class.
        /// </summary>
        /// <param name="message">The message.</param>
        /// <param name="filename">The filename.</param>
        /// <param name="inner">The inner.</param>
        public CpsParserException(string message, string filename, Exception inner)
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
            info.AddValue("ILAnalyzerException._filename", _filename);
            base.GetObjectData(info, context);
        }

        #endregion

    }
}
