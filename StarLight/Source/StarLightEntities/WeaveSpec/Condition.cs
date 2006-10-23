#region Using directives
using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Xml;
using System.Xml.Serialization;

using Composestar.StarLight.Entities.LanguageModel;  
#endregion

namespace Composestar.StarLight.Entities.WeaveSpec
{
    /// <summary>
    /// Condition
    /// </summary>
    [Serializable]
    [XmlRoot("Condition", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class Condition
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="T:Condition"/> class.
        /// </summary>
        public Condition()
        {

        } // Condition()

        /// <summary>
        /// Gets or sets the name.
        /// </summary>
        /// <value>The name.</value>
        [XmlAttribute ]
        public String Name
        {
            get
            {
                return _name;
            } // get
            set
            {
                _name = value;
            } // set
        } // Name

        private String _name;
         
        /// <summary>
        /// Gets or sets the reference.
        /// </summary>
        /// <value>The reference.</value>
        public Reference Reference
        {
            get
            {
                return _reference;
            } // get
            set
            {
                _reference = value;
            } // set
        } // Reference

        private Reference _reference;

    } // class Condition
}
