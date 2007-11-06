#region License
/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * ComposeStar StarLight [http://janus.cs.utwente.nl:8000/twiki/bin/view/StarLight/WebHome]
 * Copyright (C) 2003, University of Twente.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification,are permitted provided that the following conditions
 * are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Twente nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
*/
#endregion

#region Using directives
using Composestar.StarLight.Entities.WeaveSpec.Instructions.Visitor;
using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
#endregion

/// <summary>
/// Composestar. star light. weave spec. instructions
/// </summary>
namespace Composestar.StarLight.Entities.WeaveSpec.Instructions
{
	/// <summary>
	/// The filter action to perform.
	/// </summary>
	/// <returns>Inline instruction</returns>
	[Serializable]
	[XmlType("FilterAction", Namespace = Constants.NS)]
	public class FilterAction : InlineInstruction, IVisitable
	{
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

		private const String GeneralizeSelector = "_";

		#endregion

		#region Private Variables

		private string _type;
		private string _fullName;
		private string _selector;
		private string _target;
		private string _substitutionSelector;
		private string _substitutionTarget;

        private bool _bookkeeping;
        private string _resourceOperations;

		/// <summary>
		/// Indicates whether this filter action instruction should be executed on call or on return.
		/// </summary>
		private bool _onCall;

		/// <summary>
		/// Indicates whether this filter action instruction returns the flow (only when it is on call).
		/// </summary>
		private bool _returning;

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
			get { return _type; }
			set { _type = value; }
		}
		/// <summary>
		/// Indicates whether this filter action instruction should be executed on call or on return.
		/// </summary>
		[XmlAttribute]
		public bool OnCall
		{
			get { return _onCall; }
			set { _onCall = value; }
		}

		/// <summary>
		/// Indicates whether this filter action instruction returns the flow (only when it is on call).
		/// </summary>
		[XmlAttribute]
		public bool Returning
		{
			get { return _returning; }
			set { _returning = value; }
		}


        /// <summary>
        /// Sets the bookkeeping property.
        /// </summary>
        [XmlAttribute]
        public bool BookKeeping
        {
            get { return _bookkeeping; }
            set { _bookkeeping = value; }
        }

        /// <summary>
        /// A list of resource operations before the execution of this filter action.
        /// These are implied resource operations as extracted from the execution model.
        /// </summary>
        [XmlAttribute]
        public string ResourceOperations
        {
            get { return _resourceOperations; }
            set { _resourceOperations = value; }
        }
		#endregion

		#region Constructors

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
		}

		#endregion

		#region IVisitable

		/// <summary>
		/// Accepts the specified visitor.
		/// </summary>
		/// <param name="visitor">The visitor.</param>
		public override void Accept(IVisitor visitor)
		{
			if (visitor == null)
				throw new ArgumentNullException("visitor");

			base.Accept(visitor);

			visitor.VisitFilterAction(this);
		}

		#endregion

		/// <summary>
		/// Gets the clone.
		/// </summary>
		/// <param name="selector">The selector.</param>
		/// <returns>A cloned FilterAction.</returns>
		public FilterAction GetClone(String selector)
		{
			FilterAction fa = new FilterAction();
			fa.Type = this.Type;
			fa.FullName = this.FullName;

			fa.Target = this.Target;
			if (this.Selector.Equals(GeneralizeSelector))
			{
				fa.Selector = selector;
			}
			else
			{
				fa.Selector = this.Selector;
			}

			fa.SubstitutionTarget = this.SubstitutionTarget;
			if (this.SubstitutionSelector.Equals(GeneralizeSelector))
			{
				fa.SubstitutionSelector = selector;
			}
			else
			{
				fa.SubstitutionSelector = this.SubstitutionSelector;
			}

			fa.OnCall = this.OnCall;
			fa.Returning = this.Returning;
            fa.BookKeeping = this.BookKeeping;
            fa.ResourceOperations = this.ResourceOperations;

			return fa;
		}
	}
}
