using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using Composestar.StarLight.Configuration;  
 
namespace Composestar.StarLight.WeaveSpec
{
    /// <summary>
    /// An external.
    /// </summary>
    [Serializable]
    [XmlRoot("External", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class External
    {
        private string _name;

        /// <summary>
        /// Gets or sets the name.
        /// </summary>
        /// <value>The name.</value>
        [XmlAttribute ]
        public string Name
        {
            get { return _name; }
            set { _name = value; }
        }

        private Reference _reference;


        /// <summary>
        /// Gets or sets the reference.
        /// </summary>
        /// <value>The reference.</value>
        [XmlAttribute]
        public Reference Reference
        {
            get { return _reference; }
            set { _reference = value; }
        }

        private String _type;

        /// <summary>
        /// Gets or sets the type.
        /// </summary>
        /// <value>The type.</value>
        [XmlAttribute]
        public String Type
        {
            get { return _type; }
            set { _type = value; }
        }
      
    }
}
