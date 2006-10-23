using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization; 

namespace Composestar.StarLight.Entities.LanguageModel
{
    [Flags()]
    [Serializable]
    [XmlRoot("ParameterOptions", Namespace = "Entities.TYM.DotNET.Composestar")]
    public enum ParameterOptions
    {
        /// <summary>
        /// Input parameter.
        /// </summary>
        In = 1,
        /// <summary>
        /// Output parameter.
        /// </summary>
        Out = 2,
        /// <summary>
        /// An optional parameter.
        /// </summary>
        Optional = 4,
        /// <summary>
        /// A return value.
        /// </summary>
        RetVal = 8,
        /// <summary>
        /// None option.
        /// </summary>
        None = 0,
    }
}
