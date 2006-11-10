using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;

namespace Composestar.StarLight.Entities.LanguageModel
{
    /// <summary>
    /// Contains a single annotation definition.
    /// </summary>
    [Serializable()]
    [XmlRoot("Attribute", Namespace = "Entities.TYM.DotNET.Composestar")]
    public sealed class AttributeElement
    {

        private String _attributeType;

        /// <summary>
        /// Gets or sets the type of the attribute.
        /// </summary>
        /// <value>The type of the attribute.</value>
        [XmlAttribute]
        public String AttributeType
        {
            get { return _attributeType; }
            set { _attributeType = value; }
        }

        private List<AttributeValueElement> _values = new List<AttributeValueElement>();

        /// <summary>
        /// Gets or sets the values.
        /// </summary>
        /// <value>The values.</value>
        [XmlArray("Values")]
        [XmlArrayItem("Value")]
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
        public List<AttributeValueElement> Values
        {
            get { return _values; }
            set { _values = value; }
        }

    }

    /// <summary>
    /// Contains a single attribute value.
    /// </summary>
    [Serializable()]
    [XmlRoot("AttributeValue", Namespace = "Entities.TYM.DotNET.Composestar")]
    public sealed class AttributeValueElement
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

        private string _value;

        /// <summary>
        /// Gets or sets the value.
        /// </summary>
        /// <value>The value.</value>
        [XmlAttribute]
        public string Value
        {
            get { return _value; }
            set { _value = value; }
        }
	
	

    }
}
