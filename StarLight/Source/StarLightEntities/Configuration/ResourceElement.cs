using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;

namespace Composestar.StarLight.Entities.Configuration
{
    /// <summary>
    /// A resource definition, extracted from the annotations on the used assemblies.
    /// </summary>
    [Serializable]
    [XmlType("ResourceElement", Namespace = Constants.NS)]
    public class ResourceElement
    {
        private string _name;
        private string _operations;

        /// <summary>
        /// The resource name
        /// </summary>
        [XmlAttribute]
        public string Name
        {
            get { return _name; }
            set { _name = value; }
        }

        /// <summary>
        /// Comma separated list of resource operations
        /// </summary>
        [XmlAttribute]
        public string Operations
        {
            get { return _operations; }
            set { _operations = value; }
        }
    }
}
