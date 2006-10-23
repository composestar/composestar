using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;

namespace Composestar.StarLight.WeaveSpec
{
    /// <summary>
    /// 
    /// </summary>
    [Serializable]
    [XmlRoot("WeaveType", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class WeaveType
    {


        /// <summary>
        /// Gets a value indicating whether this instance has internals.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance has internals; otherwise, <c>false</c>.
        /// </value>
        [XmlIgnore]
        public bool HasInternals
        {
            get { return _internals.Count > 0; } // get
        }

        private List<Internal> _internals = new List<Internal>();

        /// <summary>
        /// Gets or sets the internals.
        /// </summary>
        /// <value>The internals.</value>
        [XmlArray("Internals")]
        [XmlArrayItem("Internal")]
        public List<Internal> Internals
        {
            get { return _internals; }
            set { _internals = value; }
        }

        /// <summary>
        /// Gets a value indicating whether this instance has exterals.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance has exterals; otherwise, <c>false</c>.
        /// </value>
        [XmlIgnore]
        public bool HasExterals
        {
            get { return Externals.Count > 0; }
        }
        
        private List<External> _externals = new List<External>();

        /// <summary>
        /// Gets or sets the externals.
        /// </summary>
        /// <value>The externals.</value>
        [XmlArray("Externals")]
        [XmlArrayItem("External")]
        public List<External> Externals
        {
            get { return _externals; }
            set { _externals = value; }
        }

        private string _name;

        /// <summary>
        /// Gets or sets the name of the type. Needed for the lookup.
        /// </summary>
        /// <value>The name.</value>
        [XmlAttribute]
        public string Name
        {
            get { return _name; }
            set { _name = value; }
        }

        private List<WeaveMethod> _methods = new List<WeaveMethod>();

        /// <summary>
        /// Gets or sets the methods with instructions to weave.
        /// </summary>
        /// <value>The methods.</value>
        public List<WeaveMethod> Methods
        {
            get { return _methods; }
            set { _methods = value; }
        }

        /// <summary>
        /// Gets a value indicating whether this instance has methods with input filters.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance has input filters; otherwise, <c>false</c>.
        /// </value>
        [XmlIgnore]
        public bool HasInputFilters
        {
            get
            {
                foreach (WeaveMethod  method in _methods)
                {
                    if (method.InputFilter != null)
                        return true;
                }

                return false;
            }
        }

        /// <summary>
        /// Gets a value indicating whether this instance has methods with output filters.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance has output filters; otherwise, <c>false</c>.
        /// </value>
        [XmlIgnore]
        public bool HasOutputFilters
        {
            get
            {
                foreach (WeaveMethod method in _methods)
                {
                    if (method.OutputFilters.Count > 0)
                        return true;
                }

                return false;
            }
        }
    }
}
