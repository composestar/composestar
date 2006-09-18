using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ILWeaver
{

    /// <summary>
    /// Exception throw by the weaver.
    /// </summary>
    public class ILWeaverException : Exception
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
        /// Initializes a new instance of the <see cref="T:ILWeaverException"/> class.
        /// </summary>
        public ILWeaverException()
        {
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

    }
}
