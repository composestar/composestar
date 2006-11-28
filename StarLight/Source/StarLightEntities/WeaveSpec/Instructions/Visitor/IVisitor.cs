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
		/// Visits the check inner call.
		/// </summary>
		/// <param name="contextInstruction">The context instruction.</param>
		void VisitCheckInnerCall(ContextInstruction contextInstruction);
		/// <summary>
		/// Visits the set inner call.
		/// </summary>
		/// <param name="contextInstruction">The context instruction.</param>
		void VisitSetInnerCall(ContextInstruction contextInstruction);
		/// <summary>
		/// Visits the reset inner call.
		/// </summary>
		/// <param name="contextInstruction">The context instruction.</param>
		void VisitResetInnerCall(ContextInstruction contextInstruction);
		/// <summary>
		/// Visits the return action.
		/// </summary>
		/// <param name="contextInstruction">The context instruction.</param>
		void VisitReturnAction(ContextInstruction contextInstruction);

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

		/// <summary>
		/// Visits the while.
		/// </summary>
		/// <param name="whileInstruction">The while instruction.</param>
		void VisitWhile(WhileInstruction whileInstruction);
		/// <summary>
		/// Visits the while end.
		/// </summary>
		/// <param name="whileInstruction">The while instruction.</param>
		void VisitWhileEnd(WhileInstruction whileInstruction);

		/// <summary>
		/// Visits the switch.
		/// </summary>
		/// <param name="switchInstruction">The switch instruction.</param>
		void VisitSwitch(SwitchInstruction switchInstruction);
		/// <summary>
		/// Visits the case.
		/// </summary>
		/// <param name="caseInstruction">The case instruction.</param>
		void VisitCase(CaseInstruction caseInstruction);
		/// <summary>
		/// Visits the case end.
		/// </summary>
		/// <param name="switchInstruction">The switch instruction.</param>
		void VisitCaseEnd(SwitchInstruction switchInstruction);
		/// <summary>
		/// Visits the switch end.
		/// </summary>
		/// <param name="switchInstruction">The switch instruction.</param>
		void VisitSwitchEnd(SwitchInstruction switchInstruction);

		/// <summary>
		/// Visits the create action store.
		/// </summary>
		/// <param name="contextInstruction">The context instruction.</param>
		void VisitCreateActionStore(ContextInstruction contextInstruction);
		/// <summary>
		/// Visits the store action.
		/// </summary>
		/// <param name="contextInstruction">The context instruction.</param>
		void VisitStoreAction(ContextInstruction contextInstruction);

		/// <summary>
		/// Visits the create join point context.
		/// </summary>
		/// <param name="contextInstruction">The context instruction.</param>
		void VisitCreateJoinPointContext(ContextInstruction contextInstruction);
		/// <summary>
		/// Visits the restore join point context.
		/// </summary>
		/// <param name="contextInstruction">The context instruction.</param>
		void VisitRestoreJoinPointContext(ContextInstruction contextInstruction);
	}
}
