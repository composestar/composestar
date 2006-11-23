using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Xml;
using System.Xml.Serialization;

namespace Composestar.StarLight.Entities.Concerns
{
    /// <summary>
    /// Represents the code embedded inside a concern
    /// </summary>
    [Serializable]
    [XmlRoot("EmbeddedCode", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class EmbeddedCode
    {

        private string _language;
        private string _code;
        private string _fileName;

        /// <summary>
        /// Gets or sets the language.
        /// </summary>
        /// <value>The language.</value>
        [XmlAttribute]
        public string Language
        {
            get
            {
                return _language;
            }
            set
            {
                _language = value;
            }
        }

        /// <summary>
        /// Gets or sets the name of the file.
        /// </summary>
        /// <value>The name of the file.</value>
        [XmlAttribute]
        public string FileName
        {
            get
            {
                return _fileName;
            }
            set
            {
                _fileName = value;
            }
        }

        /// <summary>
        /// Gets or sets the code.
        /// </summary>
        /// <value>The code.</value>
        [XmlElement]
        public string Code
        {
            get
            {
                return _code;
            }
            set
            {
                _code = value;
            }
        }        

    }
}
