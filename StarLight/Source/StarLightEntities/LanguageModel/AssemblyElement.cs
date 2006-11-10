using System;
using System.Collections.Generic;
using System.Text;
using System.Xml; 
using System.Xml.Serialization;

namespace Composestar.StarLight.Entities.LanguageModel
{
    /// <summary>
    /// Contains a single Assembly definition.
    /// </summary>
    [Serializable()]
    [XmlRoot("Assembly", Namespace="Entities.TYM.DotNET.Composestar")]
    public sealed class AssemblyElement
    {
        /// <summary>
        /// Name of the assembly. A fully qualified name.
        /// </summary>
        private string _name;
        private string _fileName;      
        private List<TypeElement> _types = new List<TypeElement>();

        /// <summary>
        /// Collection of TypeElements
        /// </summary>
        [XmlArray("Types")]
        [XmlArrayItem("Type")]
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
        public List<TypeElement> Types
        {
            get { return _types; }
            set { _types = value; }
        }

        /// <summary>
        /// Name of the file this assembly is in.
        /// </summary>
        [XmlAttribute()]
        public string FileName
        {
            get { return _fileName; }
            set { _fileName = value; }
        }

        /// <summary>
        /// Name of the assembly. A fully qualified name.
        /// </summary>
        [XmlAttribute()]
        public string Name
        {
            get { return _name; }
            set { _name = value; }
        }
    }
}
