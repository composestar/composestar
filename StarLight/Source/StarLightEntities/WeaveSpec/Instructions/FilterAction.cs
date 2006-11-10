#region Using directives
using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using Composestar.StarLight.Entities.WeaveSpec.Instructions.Visitor;
#endregion

/// <summary>
/// Composestar. star light. weave spec. instructions
/// </summary>
namespace Composestar.StarLight.Entities.WeaveSpec.Instructions
{
    /// <summary>
    /// The filter action to perform.
    /// </summary>
    [Serializable]
    [XmlRoot("FilterActionInstruction", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class FilterAction : InlineInstruction, IVisitable
    {

        #region Private Variables

        private string _type;
        private string _fullName;
        private string _selector;
        private string _target;
        private string _substitutionSelector;
        private string _substitutionTarget;

        #endregion

        #region Constants

        public const String DispatchAction = "DispatchAction";
        public const String BeforeAction = "BeforeAction";
        public const String AfterAction = "AfterAction";
        public const String SkipAction = "SkipAction";
        public const String ErrorAction = "ErrorAction";
        public const String SubstitutionAction = "SubstitutionAction";
        public const String CustomAction = "CustomAction";
        public const String ContinueAction = "ContinueAction";

        public const String InnerTarget = "inner";
        public const String SelfTarget = "self";

        #endregion

        #region Public Properties
            
        /// <summary>
        /// Gets or sets the full name.
        /// </summary>
        /// <value>The full name.</value>
        [XmlAttribute]
        public string FullName
        {
            get { return _fullName; }
            set { _fullName = value; }
        }

        /// <summary>
        /// Gets or sets the selector.
        /// </summary>
        /// <value>The selector.</value>
        [XmlAttribute]
        public string Selector
        {
            get { return _selector; }
            set { _selector = value; }
        }

        /// <summary>
        /// Gets or sets the target.
        /// </summary>
        /// <value>The target.</value>
        [XmlAttribute]
        public string Target
        {
            get { return _target; }
            set { _target = value; }
        }

        /// <summary>
        /// Gets or sets the substitution selector.
        /// </summary>
        /// <value>The substitution selector.</value>
        [XmlAttribute]
        public string SubstitutionSelector
        {
            get { return _substitutionSelector; }
            set { _substitutionSelector = value; }
        }

        /// <summary>
        /// Gets or sets the substitution target.
        /// </summary>
        /// <value>The substitution target.</value>
        [XmlAttribute]
        public string SubstitutionTarget
        {
            get { return _substitutionTarget; }
            set { _substitutionTarget = value; }
        }

        /// <summary>
        /// Gets or sets the type.
        /// </summary>
        /// <value>The type.</value>
        [XmlAttribute]
        public String Type
        {
            get
            {
                return _type;
            } // get
            set
            {
                _type = value;
            } // set
        } // Type

        #endregion

        #region ctor

        /// <summary>
        /// Initializes a new instance of the <see cref="T:FilterAction"/> class.
        /// </summary>
        public FilterAction()
        {

        }

   
        /// <summary>
        /// Initializes a new instance of the <see cref="T:FilterAction"/> class.
        /// </summary>
        /// <param name="type">The type.</param>
        /// <param name="fullName">The full name.</param>
        /// <param name="selector">The selector.</param>
        /// <param name="target">The target.</param>
        /// <param name="substitutionSelector">The substitution selector.</param>
        /// <param name="substitutionTarget">The substitution target.</param>
        public FilterAction(String type, String fullName, String selector, String target,
            String substitutionSelector, String substitutionTarget)
        {
            _type = type;
            _fullName = fullName;
            _selector = selector;
            _target = target;
            _substitutionSelector = substitutionSelector;
            _substitutionTarget = substitutionTarget;
        } // FilterAction(type, fullName, selector)

        #endregion

        #region IVisitable

        /// <summary>
        /// Accepts the specified visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        public new void Accept(IVisitor visitor)
        {
             if (visitor == null)
                throw new ArgumentNullException("visitor");

            base.Accept(visitor);

            visitor.VisitFilterAction(this);
        } 

        #endregion

    } 
} 