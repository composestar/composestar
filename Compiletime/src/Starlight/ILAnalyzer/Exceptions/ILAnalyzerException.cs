using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.Serialization;
  
namespace Composestar.StarLight.ILAnalyzer
{

    /// <summary>
    /// Exception throw by the analyzer.
    /// </summary>
    [Serializable()]
    public class ILAnalyzerException : Exception
    {

        private string _filename;

        /// <summary>
        /// Gets or sets the filename.
        /// </summary>
        /// <value>The filename.</value>
        public string Filename
        {
            get { return _filename; }
            set { _filename = value; }
        }


        /// <summary>
        /// Initializes a new instance of the <see cref="T:ILAnalyzerException"/> class.
        /// </summary>
        public ILAnalyzerException()
        {
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ILAnalyzerException"/> class.
        /// </summary>
        /// <param name="serInfo">The ser info.</param>
        /// <param name="streamContext">The stream context.</param>
        protected ILAnalyzerException(SerializationInfo serInfo, StreamingContext streamContext) : base(serInfo, streamContext)
        {

        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ILAnalyzerException"/> class.
        /// </summary>
        /// <param name="message">The message.</param>
        public ILAnalyzerException(string message)
            : base(message)
        {
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ILAnalyzerException"/> class.
        /// </summary>
        /// <param name="message">The message.</param>
        /// <param name="inner">The inner.</param>
        public ILAnalyzerException(string message, Exception inner)
            : base(message, inner)
        {
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ILAnalyzerException"/> class.
        /// </summary>
        /// <param name="message">The message.</param>
        /// <param name="filename">The filename.</param>
        public ILAnalyzerException(string message, string filename)
            : base(message)
        {
            _filename = filename;
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ILAnalyzerException"/> class.
        /// </summary>
        /// <param name="message">The message.</param>
        /// <param name="filename">The filename.</param>
        /// <param name="inner">The inner.</param>
        public ILAnalyzerException(string message, string filename, Exception inner)
            : base(message, inner)
        {
            _filename = filename;
        }

    }
}
