using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ILAnalyzer
{
    /// <summary>
    /// Contains a single parameter of a method.
    /// </summary>
    public class ParameterElement
    {

        private string _name;
        private Int16 _ordinal;
        private string _parameterType;
        
        /// <summary>
        /// Gets or sets the name.
        /// </summary>
        /// <value>The name.</value>
        public string Name
        {
            get
            {
                return _name;
            }
            set
            {
                _name = value;
            }
        }

        /// <summary>
        /// Gets or sets the ordinal.
        /// </summary>
        /// <value>The ordinal.</value>
        public Int16 Ordinal
        {
            get
            {
                return _ordinal;
            }
            set
            {
                _ordinal = value;
            }
        }

        /// <summary>
        /// Gets or sets the type of the parameter.
        /// </summary>
        /// <value>The type of the parameter.</value>
        public string ParameterType
        {
            get
            {
                return _parameterType;
            }
            set
            {
                _parameterType = value;
            }
        }


    }
}
