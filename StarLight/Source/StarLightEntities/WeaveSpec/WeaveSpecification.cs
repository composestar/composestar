using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;

namespace Composestar.StarLight.Entities.WeaveSpec
{
    /// <summary>
    /// A weave specification file.
    /// </summary>
    [Serializable]
    [XmlRoot("WeaveSpecification", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class WeaveSpecification
    {

        private string _assemblyName;
        
        /// <summary>
        /// Gets or sets the name of the assembly this weave file applies to.
        /// </summary>
        /// <value>The name of the assembly.</value>
        [XmlAttribute]
        public string AssemblyName
        {
            get { return _assemblyName; }
            set { _assemblyName = value; }
        }

        private List<WeaveType> _weaveTypes;

        /// <summary>
        /// Gets or sets the types to weave on.
        /// </summary>
        /// <value>The weave types.</value>
        [XmlArray("WeaveTypes") ]
        [XmlArrayItem("WeaveType") ]
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
        public List<WeaveType> WeaveTypes
        {
            get { return _weaveTypes; }
            set { _weaveTypes = value; }
        }
       
    }
}
