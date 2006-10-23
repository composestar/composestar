using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;

using Composestar.StarLight.Entities.WeaveSpec.Instructions;

namespace Composestar.StarLight.Entities.WeaveSpec
{
    /// <summary>
    /// A call with outputfilters.
    /// </summary>
    [Serializable]
    [XmlRoot("WeaveCall", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class WeaveCall
    {
        private string _methodName;

        /// <summary>
        /// Gets or sets the methodname of the call.
        /// </summary>
        /// <value>The selector.</value>
        [XmlAttribute]
        public string MethodName
        {
            get { return _methodName; }
            set { _methodName = value; }
        }

        private InlineInstruction _outputFilter;

        /// <summary>
        /// Gets or sets the output filter.
        /// </summary>
        /// <value>The output filter.</value>
        public InlineInstruction OutputFilter
        {
            get { return _outputFilter; }
            set { _outputFilter = value; }
        }
    }
}
