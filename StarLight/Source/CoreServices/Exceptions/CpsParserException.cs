using System;
using System.Runtime.Serialization;
using System.Security.Permissions;  

namespace Composestar.StarLight.CoreServices.Exceptions
{

    /// <summary>
    /// Exception throw by the CpsParser.
    /// </summary>
    [Serializable()]
    public sealed class CpsParserException : StarLightException, ISerializable
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
        private CpsParserException(SerializationInfo serInfo, StreamingContext streamContext)
            : base(serInfo, streamContext)
        {
            if (serInfo == null)
                throw new ArgumentNullException("serInfo");

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
        /// <summary>
        /// When overridden in a derived class, sets the <see cref="T:System.Runtime.Serialization.SerializationInfo"></see> with information about the exception.
        /// </summary>
        /// <param name="info">The <see cref="T:System.Runtime.Serialization.SerializationInfo"></see> that holds the serialized object data about the exception being thrown.</param>
        /// <param name="context">The <see cref="T:System.Runtime.Serialization.StreamingContext"></see> that contains contextual information about the source or destination.</param>
        /// <exception cref="T:System.ArgumentNullException">The info parameter is a null reference (Nothing in Visual Basic). </exception>
        /// <PermissionSet><IPermission class="System.Security.Permissions.FileIOPermission, mscorlib, Version=2.0.3600.0, Culture=neutral, PublicKeyToken=b77a5c561934e089" version="1" Read="*AllFiles*" PathDiscovery="*AllFiles*"/><IPermission class="System.Security.Permissions.SecurityPermission, mscorlib, Version=2.0.3600.0, Culture=neutral, PublicKeyToken=b77a5c561934e089" version="1" Flags="SerializationFormatter"/></PermissionSet>
        [SecurityPermission(SecurityAction.LinkDemand, Flags = SecurityPermissionFlag.SerializationFormatter)]
        public override void GetObjectData(SerializationInfo info, StreamingContext context)
        {
            if (info == null)
                throw new ArgumentNullException("info");
 
            info.AddValue("ILAnalyzerException._filename", _filename);
            base.GetObjectData(info, context);
        }

        #endregion

    }
}
