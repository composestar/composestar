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
using Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions;
using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using System.Diagnostics.CodeAnalysis;
#endregion

namespace Composestar.StarLight.Entities.WeaveSpec.Instructions
{
	/// <summary>
	/// A jump instruction to a specific label specified by the target.
	/// </summary>
	/// <returns>Inline instruction</returns>
	[Serializable]
	[XmlType("FilterCode", Namespace = Constants.NS)]
	public class FilterCode : IVisitable
	{
		/// <summary>
		/// The conditions to check before executing the filtercode
		/// </summary>
		private ConditionExpression _checkCondition;

		/// <summary>
		/// The instructions
		/// </summary>
		private InlineInstruction _instructions;

        /// <summary>
        /// If true resource operation book keeping should be included.
        /// </summary>
        private bool _bookkeeping;

		/// <summary>
		/// Initializes a new instance of the <see cref="T:FilterCode"/> class.
		/// </summary>
		public FilterCode()
		{

		}

		/// <summary>
		/// Gets or sets the condition expression to check before executing the filtercode.
		/// </summary>
		/// <value>The condition expression to check.</value>
		public ConditionExpression CheckCondition
		{
			get { return _checkCondition; }
			set { _checkCondition = value; }
		}

		/// <summary>
		/// Gets or sets the instructions.
		/// </summary>
		/// <value>The instructions.</value>
		/// <returns>The instructions.</returns>
		public InlineInstruction Instructions
		{
			get { return _instructions; } // get
			set { _instructions = value; } // set
		}

        /// <summary>
        /// Sets the bookkeeping property.
        /// </summary>
        public bool BookKeeping
        {
            get { return _bookkeeping; }
            set { _bookkeeping = value; }
        }

		/// <summary>
		/// Accepts the specified visitor.
		/// </summary>
		/// <param name="visitor">The visitor.</param>
		public void Accept(IVisitor visitor)
		{
			if (visitor == null)
				throw new ArgumentNullException("visitor");

			visitor.VisitFilterCode(this);
		}
	}
}
