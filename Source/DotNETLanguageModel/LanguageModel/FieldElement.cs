using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;

namespace Composestar.StarLight.LanguageModel
{
    /// <summary>
    /// Contains a single field.
    /// </summary>
    [Serializable]
    [XmlRoot("Field", Namespace = "Entities.TYM.DotNET.Composestar")]
    public sealed class FieldElement
    {
        private string _name;

        /// <summary>
        /// Gets or sets the name.
        /// </summary>
        /// <value>The name.</value>
        [XmlAttribute]
        public string Name
        {
            get { return _name; }
            set { _name = value; }
        }

        private string _type;

        /// <summary>
        /// Gets or sets the type.
        /// </summary>
        /// <value>The type.</value>
        [XmlAttribute]
        public string Type
        {
            get { return _type; }
            set { _type = value; }
        }

        private bool _isPrivate;

        /// <summary>
        /// Gets or sets a value indicating whether this instance is private.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance is private; otherwise, <c>false</c>.
        /// </value>
        [XmlAttribute]
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
        [XmlAttribute]
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
        [XmlAttribute]
        public bool IsStatic
        {
            get { return _isStatic; }
            set { _isStatic = value; }
        }
    }
}
