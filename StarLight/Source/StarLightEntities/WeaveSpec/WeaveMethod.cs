using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using Composestar.StarLight;
using Composestar.StarLight.Entities.WeaveSpec.Instructions;
   
namespace Composestar.StarLight.Entities.WeaveSpec
{
    /// <summary>
    /// The method in a type with certain input filters and output filters.
    /// </summary>
    [Serializable]
    [XmlRoot("WeaveMethod", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class WeaveMethod
    {

        private string _signature;

        /// <summary>
        /// Gets or sets the signature of the method.
        /// </summary>
        /// <value>The signature.</value>
        [XmlAttribute]
        public string Signature
        {
            get { return _signature; }
            set { _signature = value; }
        }

        private InlineInstruction _inputFilter;

        /// <summary>
        /// Gets or sets the input filter.
        /// </summary>
        /// <value>The input filter.</value>
        public InlineInstruction InputFilter
        {
            get { return _inputFilter; }
            set { _inputFilter = value; }
        }

        private SerializableDictionary<string, InlineInstruction> _outputFilters = new SerializableDictionary<string,InlineInstruction>();

        /// <summary>
        /// Gets or sets the outputfilters. 
        /// Specified as a dictionary with the methodreference as the key and the instruction as the value.
        /// </summary>
        /// <value>The output filter.</value>
        public SerializableDictionary<string, InlineInstruction> OutputFilters
        {
            get { return _outputFilters; }
            set { _outputFilters = value; }
        }

        /// <summary>
        /// Gets a value indicating whether this instance has input filters.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance has input filters; otherwise, <c>false</c>.
        /// </value>
        [XmlIgnore]
        public bool HasInputFilters
        {
            get
            {
                if (InputFilter != null)
                {
                    return true;
                }

                return false;
            }
        }

        /// <summary>
        /// Gets a value indicating whether this instance has output filters.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance has output filters; otherwise, <c>false</c>.
        /// </value>
        [XmlIgnore]
        public bool HasOutputFilters
        {
            get
            {

                if (OutputFilters.Count > 0)
                    return true;

                return false;
            }
        }
         
    }
}