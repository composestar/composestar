using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
 
namespace Composestar.StarLight.Entities.LanguageModel
{
    /// <summary>
    /// Represents a call to another method.
    /// </summary>
    [Serializable ]
    [XmlRoot("Call", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class CallElement
    {

        private string _methodReference;

        /// <summary>
        /// Gets or sets the method reference.
        /// </summary>
        /// <value>The method reference.</value>
        [XmlAttribute]
        public string MethodReference
        {
            get { return _methodReference; }
            set { _methodReference = value; }
        }
	

    }
}
