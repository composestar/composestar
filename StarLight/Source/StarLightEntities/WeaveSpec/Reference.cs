#region Using directives
using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Xml;
using System.Xml.Serialization;
using System.Globalization;
using System.Diagnostics.CodeAnalysis;
   
using Composestar.StarLight.Entities.LanguageModel;  
#endregion

namespace Composestar.StarLight.Entities.WeaveSpec
{
    /// <summary>
    /// Reference
    /// </summary>
    [Serializable]
    [XmlRoot("Reference", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class Reference
    {
        public const String InnerTarget = "inner";
        public const String SelfTarget = "self";

        /// <summary>
        /// Initializes a new instance of the <see cref="T:Reference"/> class.
        /// </summary>
        public Reference()
        {

        } // Reference()


        /// <summary>
        /// Gets or sets the name space.
        /// </summary>
        /// <value>The name space.</value>
        [XmlAttribute]
        public String Namespace
        {
            get
            {
                return _namespace;
            } // get
            set
            {
                _namespace = value;
            } // set
        } // NameSpace

        private String _namespace;


        /// <summary>
        /// Gets the fullname.
        /// </summary>
        /// <value>The fullname.</value>
        /// <returns>String</returns>
        [XmlIgnore]
        public string FullName
        {
            get
            {
                return string.Format(CultureInfo.CurrentCulture, "{0}.{1}", _namespace, _target);
            } // get
        } // Fullname

        /// <summary>
        /// Gets or sets the target.
        /// </summary>
        /// <value>The target.</value>
        [XmlAttribute]
        public String Target
        {
            get
            {
                return _target;
            } // get
            set
            {
                _target = value;
            } // set
        } // Target

        private String _target;


        private String _assembly;

        /// <summary>
        /// Gets or sets the assembly containing the type of the reference.
        /// </summary>
        [XmlAttribute]
        public String Assembly
        {
            get
            {
                return _assembly;
            }
            set
            {
                _assembly = value;
            }
        }


        /// <summary>
        /// Gets or sets the selector.
        /// </summary>
        /// <value>The selector.</value>
        [XmlAttribute]
        public String Selector
        {
            get
            {
                return _selector;
            } // get
            set
            {
                _selector = value;
            } // set
        } // Selector

        private String _selector;

        /// <summary>
        /// Gets or sets the inner call context.
        /// </summary>
        /// <value>The inner call context.</value>
        [XmlAttribute]
        public int InnerCallContext
        {
            get
            {
                return _innerCallContext;
            } // get
            set
            {
                _innerCallContext = value;
            } // set
        } // InnerCallContext

        private int _innerCallContext;

    } // class Reference
}
