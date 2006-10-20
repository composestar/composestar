using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;

namespace Composestar.StarLight.Configuration
{
    [Serializable ]
    [XmlRoot("FilterType", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class FilterTypeElement
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

        private string _acceptCallAction;

        /// <summary>
        /// Gets or sets the accept call action.
        /// </summary>
        /// <value>The accept call action.</value>
        [XmlAttribute]
        public string AcceptCallAction
        {
            get { return _acceptCallAction; }
            set { _acceptCallAction = value; }
        }

        private string _rejectCallAction;

        /// <summary>
        /// Gets or sets the reject call action.
        /// </summary>
        /// <value>The reject call action.</value>
        [XmlAttribute]
        public string RejectCallAction
        {
            get { return _rejectCallAction; }
            set { _rejectCallAction = value; }
        }

        private string _acceptReturnAction;

        /// <summary>
        /// Gets or sets the accept return action.
        /// </summary>
        /// <value>The accept return action.</value>
        [XmlAttribute]
        public string AcceptReturnAction
        {
            get { return _acceptReturnAction; }
            set { _acceptReturnAction = value; }
        }

        private string _rejectReturnAction;

        /// <summary>
        /// Gets or sets the reject return action.
        /// </summary>
        /// <value>The reject return action.</value>
        [XmlAttribute]
        public string RejectReturnAction
        {
            get { return _rejectReturnAction; }
            set { _rejectReturnAction = value; }
        }
    }
}
