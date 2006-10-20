using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
 
namespace Composestar.StarLight.LanguageModel
{
    /// <summary>
    /// Contains a single type with properties and fields/methods.
    /// </summary>
    [Serializable()]
    [XmlRoot("TypeElement", Namespace = "Entities.TYM.DotNET.Composestar")]
    public sealed class TypeElement
    {
        private string _name;

        /// <summary>
        /// Name of this type.
        /// </summary>
        [XmlAttribute()]
        public string Name
        {
            get { return _name; }
            set { _name = value; }
        }
        private string _baseType;

        /// <summary>
        /// Base type of this type.
        /// </summary>
        [XmlAttribute()]
        public string BaseType
        {
            get { return _baseType; }
            set { _baseType = value; }
        }

        private string _fullName;

        /// <summary>
        /// Gets or sets the fullname.
        /// </summary>
        /// <value>The fullname.</value>
        [XmlAttribute]
        public string FullName
        {
            get { return _fullName; }
            set { _fullName = value; }
        }
	

        private string _namespace;

        /// <summary>
        /// Full namespace of this type.
        /// </summary>
        /// <value>The namespace.</value>
        [XmlAttribute()]
        public string Namespace
        {
            get { return _namespace; }
            set { _namespace = value; }
        }


        private string _implementedInterfaces;

        /// <summary>
        /// Gets or sets the implemented interfaces.
        /// </summary>
        /// <value>The implemented interfaces.</value>
        public string ImplementedInterfaces
        {
            get { return _implementedInterfaces; }
            set { _implementedInterfaces = value; }
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
        private bool _isInterface;

        /// <summary>
        /// Gets or sets a value indicating whether this instance is interface.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance is interface; otherwise, <c>false</c>.
        /// </value>
        [XmlAttribute()]
        public bool IsInterface
        {
            get { return _isInterface; }
            set { _isInterface = value; }
        }
        private bool _isSealed;

        /// <summary>
        /// Gets or sets a value indicating whether this instance is sealed.
        /// </summary>
        /// <value><c>true</c> if this instance is sealed; otherwise, <c>false</c>.</value>
        [XmlAttribute()]
        public bool IsSealed
        {
            get { return _isSealed; }
            set { _isSealed = value; }
        }
        private bool _isValueType;

        /// <summary>
        /// Gets or sets a value indicating whether this instance is value type.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance is value type; otherwise, <c>false</c>.
        /// </value>
        [XmlAttribute()]
        public bool IsValueType
        {
            get { return _isValueType; }
            set { _isValueType = value; }
        }
        private bool _isEnum;

        /// <summary>
        /// Gets or sets a value indicating whether this instance is enum.
        /// </summary>
        /// <value><c>true</c> if this instance is enum; otherwise, <c>false</c>.</value>
        [XmlAttribute()]
        public bool IsEnum
        {
            get { return _isEnum; }
            set { _isEnum = value; }
        }
        private bool _isClass;

        /// <summary>
        /// Gets or sets a value indicating whether this instance is class.
        /// </summary>
        /// <value><c>true</c> if this instance is class; otherwise, <c>false</c>.</value>
        [XmlAttribute()]
        public bool IsClass
        {
            get { return _isClass; }
            set { _isClass = value; }
        }
        private bool _isNotPublic;

        /// <summary>
        /// Gets or sets a value indicating whether this instance is not public.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance is not public; otherwise, <c>false</c>.
        /// </value>
        [XmlAttribute()]
        public bool IsNotPublic
        {
            get { return _isNotPublic; }
            set { _isNotPublic = value; }
        }
        private bool _isPrimitive;

        /// <summary>
        /// Gets or sets a value indicating whether this instance is primitive.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance is primitive; otherwise, <c>false</c>.
        /// </value>
        [XmlAttribute()]
        public bool IsPrimitive
        {
            get { return _isPrimitive; }
            set { _isPrimitive = value; }
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
        private bool _isSerializable;

        /// <summary>
        /// Gets or sets a value indicating whether this instance is serializable.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance is serializable; otherwise, <c>false</c>.
        /// </value>
        [XmlAttribute()]
        public bool IsSerializable
        {
            get { return _isSerializable; }
            set { _isSerializable = value; }
        }

        private List<MethodElement> _methods = new List<MethodElement> ();

        /// <summary>
        /// Gets or sets the methods.
        /// </summary>
        /// <value>The methods.</value>
        public List<MethodElement> Methods
        {
            get { return _methods; }
            set { _methods = value; }
        }

        private List<FieldElement> _fields = new List<FieldElement> ();

        /// <summary>
        /// Gets or sets the fields.
        /// </summary>
        /// <value>The fields.</value>
        public List<FieldElement> Fields
        {
            get { return _fields; }
            set { _fields = value; }
        }
    }
}
