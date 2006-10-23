#region Using directives
using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Xml;
using System.Xml.Serialization;

using Composestar.StarLight.Entities.LanguageModel;  
#endregion

namespace Composestar.StarLight.Entities.Configuration
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
        public String NameSpace
        {
            get
            {
                return _nameSpace;
            } // get
            set
            {
                _nameSpace = value;
            } // set
        } // NameSpace

        private String _nameSpace;


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