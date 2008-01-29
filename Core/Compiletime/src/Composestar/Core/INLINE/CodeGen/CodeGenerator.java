/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2008 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */

package Composestar.Core.INLINE.CodeGen;

import java.util.List;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.BinaryOperator;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.CondLiteral;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.UnaryOperator;
import Composestar.Core.INLINE.lowlevel.ModelBuilder;
import Composestar.Core.INLINE.model.FilterAction;
import Composestar.Core.INLINE.model.FilterCode;
import Composestar.Core.INLINE.model.Visitor;
import Composestar.Core.LAMA.MethodInfo;

/**
 * The generic interface for an INLINE code generator
 * 
 * @author Michiel Hendriks
 */
public interface CodeGenerator<T> extends Visitor
{
	public static final String MODULE_NAME = ModelBuilder.MODULE_NAME + ".CodeGen";

	/**
	 * Generate code for the given filter code and method.
	 * 
	 * @param code
	 * @param currentMethod the method to produce filter code for
	 * @param currentMethodId the ID of the current method
	 * @return the generated code
	 */
	T generate(FilterCode code, MethodInfo currentMethod, int currentMethodId);

	/**
	 * Retrieve the method info of the method currently processed
	 * 
	 * @return
	 */
	MethodInfo getCurrentMethod();

	/**
	 * Register a filter action code generator with this code generator.
	 * 
	 * @param facg
	 */
	void register(FilterActionCodeGenerator<T> facg);

	/**
	 * Emit an instruction to jump to the label with the given id
	 * 
	 * @param labelId the id of the label, if -1 then jump to the return flow
	 * @return
	 */
	T emitJump(int labelId);

	/**
	 * Emit a label.
	 * 
	 * @param labelId the id of the label
	 * @return
	 */
	T emitLabel(int labelId);

	/**
	 * Emit the processing of the return filter actions
	 * 
	 * @return
	 */
	T emitReturnActions();

	/**
	 * The default filter action code generator. Will be called when there is
	 * not specific code generator for the filter action registered.
	 * 
	 * @param filterAction
	 * @return
	 */
	T emitDefaultFilterAction(FilterAction filterAction);

	/**
	 * Emit the execution of the filter action. This can be either an on-call or
	 * on-return filter action.
	 * 
	 * @param filterAction the filteraction instruction to emit the code for
	 * @return
	 */
	T emitFilterAction(FilterAction filterAction);

	/**
	 * Emit the code to enqueue a 'return filter action' to the list of filter
	 * actions to be executed in the return flow
	 * 
	 * @param idx the index of the filter action
	 * @param filterAction the filter action
	 * @return
	 */
	T emitReturnFilterAction(int idx, FilterAction filterAction);

	/**
	 * Emit a boolean literal
	 * 
	 * @param literal
	 * @return
	 */
	T emitCondLiteral(CondLiteral literal);

	/**
	 * Emit a unary boolean operation expression
	 * 
	 * @param op the unary operator instance
	 * @param expr the expression
	 * @return
	 */
	T emitUnaryOperator(UnaryOperator op, T expr);

	/**
	 * Emit a binary boolean operator expression
	 * 
	 * @param op the operator instance
	 * @param lhs the left hand side of the operation
	 * @param rhs the right hand side of the operation
	 * @return
	 */
	T emitBinaryOperator(BinaryOperator op, T lhs, T rhs);

	/**
	 * Emit a code block
	 * 
	 * @param code the block of code to encapsulate as a block
	 * @return
	 */
	T emitBlock(T code);

	/**
	 * Emit a branching construction (if-statement)
	 * 
	 * @param condition the condition part of the if-statement
	 * @param trueBranch the statement to execute when the condition evaluates
	 *            to true, can be null or empty
	 * @param falseBranch the statment to execute when the condition evaluates
	 *            to false, can be null or empty
	 * @return
	 */
	T emitBranch(T condition, T trueBranch, T falseBranch);

	/**
	 * Emit a short boolean "or" expression, this is used for the filter module
	 * condition check
	 * 
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	T emitShortOr(T lhs, T rhs);

	/**
	 * Produce a method call to the given method using the args list as the
	 * arguments
	 * 
	 * @param method
	 * @param args the list of arguments (as string) to include in the method
	 *            call. This list can be null or empty.
	 * @param context the context of the method call (internal/external), will
	 *            be null when the context is this or static.
	 * @return
	 */
	T emitMethodCall(MethodInfo method, List<T> args, Object context);

	/**
	 * Emit the code to set the inner call flag for the method with the given id
	 * 
	 * @param methodId
	 * @return
	 */
	T emitSetInnerCall(int methodId);

	/**
	 * Emit the string representation of the call to the condition
	 * 
	 * @param cond
	 * @return
	 */
	T emitCondition(Condition cond);

	/**
	 * Emit the filter code block
	 * 
	 * @param fmConditions the filter module condition expression, will be null
	 *            when there are no filter module conditions that must match
	 * @param onCall the filter code for the on call filter action processing
	 * @param onReturn the filter code of the return filter action processing,
	 *            can be null when there are not return action
	 * @return
	 */
	T emitFilterCode(T fmConditions, T onCall, T onReturn);
}