using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
 
namespace Composestar.DataStore.LanguageModel
{
    /// <summary>
    /// Contains a single parameter of a method.
    /// </summary>
    public class ParameterInfo
    {
        private string _name;
        private Int16 _ordinal;
        private string _parameterType;
        private ParameterAttributes _parameterAttributes;


        /// <summary>
        /// Gets or sets the parameter attributes.
        /// </summary>
        /// <value>The parameter attributes.</value>
        public ParameterAttributes ParameterAttributes
        {
            get
            {
                return _parameterAttributes;
            }
            set
            {
                _parameterAttributes = value;
            }
        }

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
