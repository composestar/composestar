using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;

namespace Composestar.StarLight.Entities.WeaveSpec
{
    /// <summary>
    /// 
    /// </summary>
    [Serializable]
    [XmlType("ConflictRule", Namespace = Constants.NS)]
    public class ConflictRule
    {
        private string _resource;
        private string _expression;
        private bool _constraint;

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
        public string Expression
        {
            get { return _expression; }
            set { _expression = value; }
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
    }
}
