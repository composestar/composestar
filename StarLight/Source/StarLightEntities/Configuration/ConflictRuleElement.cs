using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;

namespace Composestar.StarLight.Entities.Configuration
{
    /// <summary>
    /// A conflict rule extracted from assemblies containing filter specifications.
    /// </summary>
    [Serializable]
    [XmlType("ConflictRuleElement", Namespace = Constants.NS)]
    public class ConflictRuleElement
    {
        private string _resource;
        private string _pattern;
        private bool _constraint;
        private string _message;

        /// <summary>
        /// 
        /// </summary>
        [XmlAttribute]
        public string Resource
        {
            get { return _resource; }
            set { _resource = value; }
        }

        /// <summary>
        /// 
        /// </summary>
        [XmlAttribute]
        public string Pattern
        {
            get { return _pattern; }
            set { _pattern = value; }
        }

        /// <summary>
        /// 
        /// </summary>
        [XmlAttribute]
        public bool Constraint
        {
            get { return _constraint; }
            set { _constraint = value; }
        }

        /// <summary>
        /// 
        /// </summary>
        [XmlAttribute]
        public string Message
        {
            get { return _message; }
            set { _message = value; }
        }
    }
}
