using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using System.Diagnostics.CodeAnalysis;
    
namespace Composestar.StarLight.Entities.WeaveSpec
{
    /// <summary>
    /// An internal.
    /// </summary>
    [Serializable]
    [XmlRoot("Internal", Namespace = "Entities.TYM.DotNET.Composestar")]
    [SuppressMessage("Microsoft.Naming", "CA1724:TypeNamesShouldNotMatchNamespaces", Justification = "It is an internal, use the full namespace to eliminate conflicts with System.EnterrpiseServices.Internal.")]
    public class Internal
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

        private string _nameSpace;

        /// <summary>
        /// Gets or sets the name space.
        /// </summary>
        /// <value>The name space.</value>
        [XmlAttribute]
        public string NameSpace
        {
            get { return _nameSpace; }
            set { _nameSpace = value; }
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

        private String _assembly;

        /// <summary>
        /// Gets or sets the assembly containing the type.
        /// </summary>
        [XmlAttribute]
        public String Assembly
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

        private string _concern;

        /// <summary>
        /// Gets or sets the concern.
        /// </summary>
        /// <value>The concern.</value>
        [XmlAttribute]
        public string Concern
        {
            get { return _concern; }
            set { _concern = value; }
        }
    }
}
