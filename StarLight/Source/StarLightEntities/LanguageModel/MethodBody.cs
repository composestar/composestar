using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
 
namespace Composestar.StarLight.Entities.LanguageModel
{
    /// <summary>
    /// Method body of a method.
    /// </summary>
    [Serializable]
    [XmlRoot("MethodBody", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class MethodBody
    {
        private List<CallElement> _calls = new List<CallElement>();

        [XmlArray("Calls")]
        [XmlArrayItem("Call")]
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
        public List<CallElement> Calls
        {
            get { return _calls; }
            set { _calls = value; }
        }
    }
}
