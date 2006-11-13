#region Using directives
using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Xml;
using System.Xml.Serialization;
using System.Globalization;
 
using Composestar.StarLight.Entities.LanguageModel;  
#endregion

namespace Composestar.StarLight.Entities.Configuration
{
    /// <summary>
    /// Contains configuration about the assembly.
    /// </summary>
    [Serializable]
    [XmlRoot("AssemblyConfig", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class AssemblyConfig
    {
        private string _name;

        /// <summary>
        /// Gets or sets the name.
        /// </summary>
        /// <value>The name.</value>
        [XmlAttribute]
        public string Name
        {
            get { return _name; }
            set { _name = value; }
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

        private string _fileName;

        /// <summary>
        /// Gets or sets the filename.
        /// </summary>
        /// <value>The filename.</value>
        [XmlAttribute("Filename")]        
        public string FileName
        {
            get { return _fileName; }
            set { _fileName = value; }
        }

        private string _serializedFileName = string.Empty;

        /// <summary>
        /// Gets or sets the serialized filename.
        /// </summary>
        /// <value>The serialized filename.</value>
        [XmlAttribute("SerializedFilename")]
        public string SerializedFileName
        {
            get
            {
                return _serializedFileName;
            }
            set
            {
                _serializedFileName = value;
            }
        }

        private AssemblyElement _assembly;

        /// <summary>
        /// Gets or sets the assembly.
        /// </summary>
        /// <value>The assembly.</value>
        [XmlIgnore]
        public AssemblyElement Assembly
        {
            get
            {
                return _assembly;
            }
            set
            {
                _assembly = value;
            }
        }

        /// <summary>
        /// Generate a serialized filename to be used to store the file.
        /// </summary>
        public void GenerateSerializedFileName(string objectFolder)
        {
            if (_assembly != null)
            {
                string name = _assembly.Name;
                name = name.Replace(", ", "_");
                name = name.Replace(".", "_");
                name = name.Replace("Version=", "");
                name = name.Replace("Culture=", "");
                name = name.Replace("PublicKeyToken=", ""); 
                _serializedFileName = Path.Combine(objectFolder, string.Format(CultureInfo.CurrentCulture, "{0}.xml.gzip", name)); 
            } 

        } // GenerateSerializedFilename()

        private String _weaveSpec;

        /// <summary>
        /// Gets or sets the weave specification file.
        /// </summary>
        /// <value>The weave specification file.</value>
        [XmlAttribute]
        public String WeaveSpecificationFile
        {
            get { return _weaveSpec; }
            set { _weaveSpec = value; }
        }

        private bool _isReference;

        /// <summary>
        /// Gets or sets a value indicating whether this assembly is a reference.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance is reference; otherwise, <c>false</c>.
        /// </value>
        [XmlAttribute]
        public bool IsReference
        {
            get { return _isReference; }
            set { _isReference = value; }
        }
	

    }
}
