using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;  

namespace Composestar.StarLight.Entities.LanguageModel
{
    /// <summary>
    /// Contains information about a single method.
    /// </summary>
    [Serializable]
    [XmlRoot("Method", Namespace = "Entities.TYM.DotNET.Composestar")]
    public sealed class MethodElement
    {
        private string _name;

        /// <summary>
        /// Gets or sets the name.
        /// </summary>
        /// <value>The name.</value>
        [XmlAttribute()]
        public string Name
        {
            get { return _name; }
            set { _name = value; }
        }

        private string _returnType;

        /// <summary>
        /// Gets or sets the type of the return.
        /// </summary>
        /// <value>The type of the return.</value>
        [XmlAttribute()]
        public string ReturnType
        {
            get { return _returnType; }
            set { _returnType = value; }
        }
        private string _signature;

        /// <summary>
        /// Gets or sets the signature.
        /// </summary>
        /// <value>The signature.</value>
        [XmlAttribute()]
        public string Signature
        {
            get { return _signature; }
            set { _signature = value; }
        }
        private bool _isAbstract;

        /// <summary>
        /// Gets or sets a value indicating whether this instance is abstract.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance is abstract; otherwise, <c>false</c>.
        /// </value>
        [XmlAttribute()]
        public bool IsAbstract
        {
            get { return _isAbstract; }
            set { _isAbstract = value; }
        }
        private bool _isConstructor;

        /// <summary>
        /// Gets or sets a value indicating whether this instance is constructor.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance is constructor; otherwise, <c>false</c>.
        /// </value>
        [XmlAttribute()]
        public bool IsConstructor
        {
            get { return _isConstructor; }
            set { _isConstructor = value; }
        }
        private bool _isPrivate;

        /// <summary>
        /// Gets or sets a value indicating whether this instance is private.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance is private; otherwise, <c>false</c>.
        /// </value>
        [XmlAttribute()]
        public bool IsPrivate
        {
            get { return _isPrivate; }
            set { _isPrivate = value; }
        }
        private bool _isPublic;

        /// <summary>
        /// Gets or sets a value indicating whether this instance is public.
        /// </summary>
        /// <value><c>true</c> if this instance is public; otherwise, <c>false</c>.</value>
        [XmlAttribute()]
        public bool IsPublic
        {
            get { return _isPublic; }
            set { _isPublic = value; }
        }
        private bool _isStatic;

        /// <summary>
        /// Gets or sets a value indicating whether this instance is static.
        /// </summary>
        /// <value><c>true</c> if this instance is static; otherwise, <c>false</c>.</value>
        [XmlAttribute()]
        public bool IsStatic
        {
            get { return _isStatic; }
            set { _isStatic = value; }
        }

        private List<ParameterElement> _parameters = new List<ParameterElement> ();

        /// <summary>
        /// Gets or sets the parameters.
        /// </summary>
        /// <value>The parameters.</value>
        [XmlArray("Parameters") ]
        [XmlArrayItem("Parameter") ]
        public List<ParameterElement> Parameters
        {
            get { return _parameters; }
            set { _parameters = value; }
        }

        private MethodBody _methodBody;

        /// <summary>
        /// Gets or sets the body.
        /// </summary>
        /// <value>The body.</value>
        public MethodBody Body
        {
            get
            {
                return _methodBody;
            }
            set
            {
                _methodBody = value;
            }
        }

        /// <summary>
        /// Gets a value indicating whether this instance has a method body.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance has a method body; otherwise, <c>false</c>.
        /// </value>
        [XmlIgnore]
        public bool HasMethodBody
        {
            get
            {
                return (_methodBody != null);
            }            
        }

        private bool _isVirtual;

        /// <summary>
        /// Gets or sets a value indicating whether this instance is virtual.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance is virtual; otherwise, <c>false</c>.
        /// </value>
        [XmlAttribute]
        public bool IsVirtual
        {
            get { return _isVirtual; }
            set { _isVirtual = value; }
        }
    }
}
