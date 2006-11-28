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
using Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions;
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
	/// A branch contains two blocks; a true and a false flow. The condition determines which path is taken at runtime.
	/// </summary>
	/// <returns>Inline instruction</returns>
	[Serializable]
	[XmlRoot("Branch", Namespace = "Entities.TYM.DotNET.Composestar")]
	public class Branch : InlineInstruction, IVisitable
	{
		/// <summary>
		/// _condition expression
		/// </summary>
		private ConditionExpression _conditionExpression;

		/// <summary>
		/// _true block
		/// </summary>
		private Block _trueBlock;
		/// <summary>
		/// _false block
		/// </summary>
		private Block _falseBlock;

		/// <summary>
		/// Initializes a new instance of the <see cref="T:Branch"/> class.
		/// </summary>
		public Branch()
		{

		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:Branch"/> class.
		/// </summary>
		/// <param name="conditionExpression">The condition expression.</param>
		public Branch(ConditionExpression conditionExpression)
		{
			_conditionExpression = conditionExpression;

		}

		/// <summary>
		/// Gets or sets the true block.
		/// </summary>
		/// <value>The true block.</value>
		public Block TrueBlock
		{
			get
			{
				return _trueBlock;
			}
			set
			{
				_trueBlock = value;
			}
		}

		/// <summary>
		/// Gets or sets the false block.
		/// </summary>
		/// <value>The false block.</value>
		public Block FalseBlock
		{
			get
			{
				return _falseBlock;
			}
			set
			{
				_falseBlock = value;
			}
		}

		/// <summary>
		/// Gets or sets the condition expression.
		/// </summary>
		/// <value>The condition expression.</value>
		/// <returns>Condition expression</returns>
		[XmlElement("Condition")]
		public ConditionExpression ConditionExpression
		{
			get
			{
				return _conditionExpression;
			}
			set
			{
				_conditionExpression = value;
			}
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

			visitor.VisitBranch(this);
			if (_trueBlock != null)
				((IVisitable)_trueBlock).Accept(visitor);
			visitor.VisitBranchFalse(this);
			if (_falseBlock != null)
				((IVisitable)_falseBlock).Accept(visitor);
			visitor.VisitBranchEnd(this);

		}


	}
}
