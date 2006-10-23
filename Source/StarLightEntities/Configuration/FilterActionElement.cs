using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization; 

namespace Composestar.StarLight.Entities.Configuration
{
    [Serializable]
    [XmlRoot("FilterAction", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class FilterActionElement
    {
        public  const int FlowContinue = 1;
        public  const int FlowExit = 2;
        public  const int FlowReturn = 3;
        public  const int MessageOriginal = 1;
        public  const int MessageSubstituted = 2;
        public  const int MessageAny = 3;

        private int _flowBehavior;

        /// <summary>
        /// Gets or sets the flow behavior.
        /// </summary>
        /// <value>The flow behavior.</value>
        [XmlAttribute]
        public int FlowBehavior
        {
            get { return _flowBehavior; }
            set { _flowBehavior = value; }
        }
        private int _messageChangeBehavior;

        /// <summary>
        /// Gets or sets the message change behavior.
        /// </summary>
        /// <value>The message change behavior.</value>
        [XmlAttribute]
        public int MessageChangeBehavior
        {
            get { return _messageChangeBehavior; }
            set { _messageChangeBehavior = value; }
        }
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
        private string _fullName;

        /// <summary>
        /// Gets or sets the full name.
        /// </summary>
        /// <value>The full name.</value>
        [XmlAttribute]
        public string FullName
        {
            get { return _fullName; }
            set { _fullName = value; }
        }

        private String _assembly;

        /// <summary>
        /// Gets or sets the assembly.
        /// </summary>
        /// <value>The assembly.</value>
        [XmlAttribute]
        public String Assembly
        {
            get { return _assembly; }
            set { _assembly = value; }
        }
	

    }
}
