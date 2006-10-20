using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;

namespace Composestar.StarLight.LanguageModel
{
    /// <summary>
    /// A parameter of a method.
    /// </summary>
    [Serializable]
    [XmlRoot("ParameterElement", Namespace = "Entities.TYM.DotNET.Composestar")]
    public sealed class ParameterElement
    {

        private ParameterOptions _parameterOption = ParameterOptions.None;
        private short _ordinal;
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
        private string _type;

        /// <summary>
        /// Gets or sets the type.
        /// </summary>
        /// <value>The type.</value>
        [XmlAttribute]
        public string Type
        {
            get { return _type; }
            set { _type = value; }
        }

        /// <summary>
        /// Gets or sets the ordinal.
        /// </summary>
        /// <value>The ordinal.</value>
        [XmlAttribute]
        public short Ordinal
        {
            get { return _ordinal; }
            set { _ordinal = value; }
        }

        /// <summary>
        /// Gets or sets the parameter option.
        /// </summary>
        /// <value>The parameter option.</value>
        [XmlAttribute]
        public ParameterOptions ParameterOption
        {
            get { return _parameterOption; }
            set { _parameterOption = value; }
        }
    }
}
