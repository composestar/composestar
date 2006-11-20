using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Xml;
using System.Xml.Serialization;

namespace Composestar.StarLight.Entities.Concerns
{
	/// <summary>
	/// A concern specification.
	/// </summary>
	[Serializable]
	[XmlRoot("Concern", Namespace = "Entities.TYM.DotNET.Composestar")]
	public class ConcernElement
	{
		private string _fileName;
		private string _concernName;
		private long _timestamp;
		private string _pathName;
		private bool _hasOutputFilters;
		private bool _hasEmbeddedCode;
        private List<String> _types = new List<string> ();

        /// <summary>
        /// Gets or sets the types used in the internals or externals.
        /// </summary>
        /// <value>The types.</value>
        [XmlArray("Types")]
        [XmlArrayItem("Type")]
        public List<String> ReferencedTypes
        {
            get
            {
                return _types;
            }
            set
            {
                _types = value;
            }
        }

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
		/// Gets or sets a value indicating whether this concern has output filters.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if this concern has output filters; otherwise, <c>false</c>.
		/// </value>
		[XmlAttribute]
		public bool HasOutputFilters
		{
			get { return _hasOutputFilters; }
			set { _hasOutputFilters = value; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether this concern has embedded code.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if this concern has embedded code; otherwise, <c>false</c>.
		/// </value>
		[XmlAttribute]
		public bool HasEmbeddedCode
		{
			get { return _hasEmbeddedCode; }
			set { _hasEmbeddedCode = value; }
		}

		/// <summary>
		/// Gets the full path of this concern.
		/// </summary>
		/// <value>The full path.</value>
		[XmlIgnore]
		public string FullPath
		{
			get { return Path.Combine(_pathName, _fileName); }
		}
	}
}
