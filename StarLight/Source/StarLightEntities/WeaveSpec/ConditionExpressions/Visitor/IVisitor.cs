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
#endregion

/// <summary>
/// Composestar. star light. weave spec. condition expressions. visitor
/// </summary>
namespace Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions.Visitor
{

	/// <summary>
	/// Interface for the Condition Expression visitor.
	/// </summary>
	public interface IVisitor
	{
		/// <summary>
		/// Visits the AND left part.
		/// </summary>
		/// <param name="and">The and.</param>
		void VisitAndLeft(AndCondition and);

		/// <summary>
		/// Visits the AND right part.
		/// </summary>
		/// <param name="and">The and.</param>
		void VisitAndRight(AndCondition and);

		/// <summary>
		/// Visits the condition literal.
		/// </summary>
		/// <param name="conditionLiteral">The condition literal.</param>
		void VisitConditionLiteral(ConditionLiteral conditionLiteral);

		/// <summary>
		/// Visits the false.
		/// </summary>
		/// <param name="falseObject">The false object.</param>
		void VisitFalse(FalseCondition falseObject);

		/// <summary>
		/// Visits the true.
		/// </summary>
		/// <param name="trueObject">The true object.</param>
		void VisitTrue(TrueCondition trueObject);

		/// <summary>
		/// Visits the not.
		/// </summary>
		/// <param name="not">The not.</param>
		void VisitNot(NotCondition not);

		/// <summary>
		/// Visits the OR left part.
		/// </summary>
		/// <param name="or">The or.</param>
		void VisitOrLeft(OrCondition or);

		/// <summary>
		/// Visits the OR right part.
		/// </summary>
		/// <param name="or">The or.</param>
		void VisitOrRight(OrCondition or);

	}
} 
