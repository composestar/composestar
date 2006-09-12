using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;


namespace Composestar.Repository.LanguageModel
{
    /// <summary>
    /// Contains information about the types.
    /// </summary>
    public class TypeInfo
    {
        private string _name;
        private string _fullname;
        private string _basetype;
        private AssemblyInfo _assembly;
        private bool _isabstract;
        private bool _isinterface;
        private bool _issealed;
        private bool _isvaluetype;
        private bool _isenum;
        private IList<MethodInfo> _methods;  

        // TODO: Add the following properties
        // assemblyname/assemblyinfo type is contained in
        //type.Attributes
        //type.Fields
        //type.CustomAttributes
        //type.Properties

        /// <summary>
        /// Initializes a new instance of the <see cref="T:TypeInfo"/> class.
        /// </summary>
        public TypeInfo()
        {
            _methods = new List<MethodInfo>();
        }

        /// <summary>
        /// Gets or sets the name.
        /// </summary>
        /// <value>The name.</value>
        public string Name
        {
            get { return _name; }
            set { _name = value; }
        }

        /// <summary>
        /// Gets or sets the full name.
        /// </summary>
        /// <value>The full name.</value>
        public string FullName
        {
            get { return _fullname; }
            set { _fullname = value; }
        }

        /// <summary>
        /// Gets or sets the type of the base class.
        /// </summary>
        /// <value>The type of the base class.</value>
        public string BaseType
        {
            get { return _basetype; }
            set { _basetype = value; }
        }

        /// <summary>
        /// Gets or sets the assembly information for the type.
        /// </summary>
        /// <value>The assembly information.</value>
        public AssemblyInfo Assembly
        {
            get { return _assembly; }
            set { _assembly = value; }
        }

        /// <summary>
        /// Gets or sets a value indicating whether this instance is abstract.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance is abstract; otherwise, <c>false</c>.
        /// </value>
        public bool IsAbstract
        {
            get { return _isabstract; }
            set { _isabstract = value; }
        }

        /// <summary>
        /// Gets or sets a value indicating whether this instance is enum.
        /// </summary>
        /// <value><c>true</c> if this instance is enum; otherwise, <c>false</c>.</value>
        public bool IsEnum
        {
            get { return _isenum; }
            set { _isenum = value; }
        }

        /// <summary>
        /// Gets or sets a value indicating whether this instance is interface.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance is interface; otherwise, <c>false</c>.
        /// </value>
        public bool IsInterface
        {
            get { return _isinterface; }
            set { _isinterface = value; }
        }

        /// <summary>
        /// Gets or sets a value indicating whether this instance is sealed.
        /// </summary>
        /// <value><c>true</c> if this instance is sealed; otherwise, <c>false</c>.</value>
        public bool IsSealed
        {
            get { return _issealed; }
            set { _issealed = value; }
        }

        /// <summary>
        /// Gets or sets a value indicating whether this instance is value type.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance is value type; otherwise, <c>false</c>.
        /// </value>
        public bool IsValueType
        {
            get { return _isvaluetype; }
            set { _isvaluetype = value; }
        }

        /// <summary>
        /// Adds the method.
        /// </summary>
        /// <param name="methodinfo">The methodinfo.</param>
        public IList<MethodInfo> Methods
        {
            get
            {
                return _methods;
            }
            set
            {
                _methods = value;
            }
        }
              

    }
}
