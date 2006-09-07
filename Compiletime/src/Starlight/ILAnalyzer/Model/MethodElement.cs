using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ILAnalyzer
{
    /// <summary>
    /// Contains a single method element
    /// </summary>
    public class MethodElement
    {

        private List<CallConstruction> _calls = new List<CallConstruction>();
        private string _methodName;
        private string _returnType;
        private List<ParameterElement> _parameters = new List<ParameterElement> ();

        /// <summary>
        /// Gets or sets the type of the return.
        /// </summary>
        /// <value>The type of the return.</value>
        public string ReturnType
        {
            get
            {
                return _returnType;
            }
            set
            {
                _returnType = value;
            }
        }

        /// <summary>
        /// Gets or sets the name of the method.
        /// </summary>
        /// <value>The name of the method.</value>
        public string MethodName
        {
            get
            {
                return _methodName;
            }
            set
            {
                _methodName = value;
            }
        }

        /// <summary>
        /// Gets or sets the calls.
        /// </summary>
        /// <value>The calls.</value>
        public List<CallConstruction> Calls
        {
            get
            {
                return _calls;
            }
            set
            {
                _calls = value;
            }
        }

        /// <summary>
        /// Gets or sets the parameters.
        /// </summary>
        /// <value>The parameters.</value>
        public List<ParameterElement> Parameters
        {
            get
            {
                return _parameters;
            }
            set
            {
                _parameters = value;
            }
        }
    }
}
