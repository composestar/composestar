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
using Composestar.StarLight.Entities.WeaveSpec.Instructions;
#endregion

namespace Composestar.StarLight.Entities.WeaveSpec.Instructions.Visitor
{
	/// <summary>
	/// Visitor for the instructions.
	/// </summary>    
	public interface IVisitor
	{
		/// <summary>
		/// Visits the filter code.
		/// </summary>
		/// <param name="code">The filter code</param>
		void VisitFilterCode(FilterCode code);

		/// <summary>
		/// Visits the branch.
		/// </summary>
		/// <param name="branch">The branch.</param>
		void VisitBranch(Branch branch);
		
		/// <summary>
		/// Visits the branch false.
		/// </summary>
		/// <param name="branch">The branch.</param>
		void VisitBranchFalse(Branch branch);
		
		/// <summary>
		/// Visits the branch end.
		/// </summary>
		/// <param name="branch">The branch.</param>
		void VisitBranchEnd(Branch branch);

		/// <summary>
		/// Visits the filter action.
		/// </summary>
		/// <param name="filterAction">The filter action.</param>
		void VisitFilterAction(FilterAction filterAction);
		/// <summary>
		/// Visits the jump instruction.
		/// </summary>
		/// <param name="jump">The jump.</param>
		void VisitJumpInstruction(JumpInstruction jump);

		/// <summary>
		/// Visits the inline instruction.
		/// </summary>
		/// <param name="inlineInstruction">The inline instruction.</param>
		void VisitInlineInstruction(InlineInstruction inlineInstruction);
	}
}
