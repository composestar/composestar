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

namespace Composestar.StarLight.Entities.WeaveSpec.Instructions
{
	/// <summary>
	/// A switch statement.
	/// </summary>
	/// <returns>Inline instruction</returns>
	[Serializable]
	[XmlRoot("Switch", Namespace = "Entities.TYM.DotNET.Composestar")]
	public class SwitchInstruction : InlineInstruction, IVisitable
	{

		/// <summary>
		/// _expression
		/// </summary>
		private ContextExpression _expression;
		/// <summary>
		/// _cases
		/// </summary>
		private List<CaseInstruction> _cases = new List<CaseInstruction>();

		/// <summary>
		/// Initializes a new instance of the <see cref="T:Switch"/> class.
		/// </summary>
		public SwitchInstruction()
		{

		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:Switch"/> class.
		/// </summary>
		/// <param name="expression">The expression.</param>
		public SwitchInstruction(ContextExpression expression)
		{
			_expression = expression;
		}


		/// <summary>
		/// Gets or sets the expression.
		/// </summary>
		/// <value>The expression.</value>
		public ContextExpression Expression
		{
			get { return _expression; } // get
			set { _expression = value; } // set
		}

		/// <summary>
		/// Gets or sets the cases.
		/// </summary>
		/// <value>The cases.</value>
		/// <returns>List</returns>
		[System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
		public List<CaseInstruction> Cases
		{
			get { return _cases; } // get    
		}

		/// <summary>
		/// Accepts the specified visitor.
		/// </summary>
		/// <param name="visitor">The visitor.</param>
		public new void Accept(IVisitor visitor)
		{
			if (visitor == null)
				throw new ArgumentNullException("visitor");

			base.Accept(visitor);
			visitor.VisitSwitch(this);

			foreach (CaseInstruction caseItem in _cases)
			{
				caseItem.Accept(visitor);
				visitor.VisitCaseEnd(this);
			}

			visitor.VisitSwitchEnd(this);

		}

	}

}
