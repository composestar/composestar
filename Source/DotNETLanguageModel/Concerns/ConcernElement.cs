using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Xml;
using System.Xml.Serialization;

namespace Composestar.StarLight.Concerns
{
    /// <summary>
    /// A concern specification.
    /// </summary>
    [Serializable]
    [XmlRoot("Concern", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class ConcernElement
    {
        private string _fileName;

        /// <summary>
        /// Gets or sets the name of the file.
        /// </summary>
        /// <value>The name of the file.</value>
        [XmlAttribute]
        public string FileName
        {
            get { return _fileName; }
            set { _fileName = value; }
        }
        private string _concernName;

        /// <summary>
        /// Gets or sets the name of the concern.
        /// </summary>
        /// <value>The name of the concern.</value>
        [XmlAttribute]
        public string ConcernName
        {
            get { return _concernName; }
            set { _concernName = value; }
        }
        private long _timestamp;

        /// <summary>
        /// Gets or sets the timestamp.
        /// </summary>
        /// <value>The timestamp.</value>
        [XmlAttribute]
        public long Timestamp
        {
            get { return _timestamp; }
            set { _timestamp = value; }
        }
        private string _pathName;

        /// <summary>
        /// Gets or sets the name of the path.
        /// </summary>
        /// <value>The name of the path.</value>
        [XmlAttribute]
        public string PathName
        {
            get { return _pathName; }
            set { _pathName = value; }
        }

        /// <summary>
        /// Gets the full path.
        /// </summary>
        /// <value>The full path.</value>
        [XmlIgnore]
        public string FullPath
        {
            get
            {
                return Path.Combine(_pathName, _fileName);
            }           
        }
    }
}
