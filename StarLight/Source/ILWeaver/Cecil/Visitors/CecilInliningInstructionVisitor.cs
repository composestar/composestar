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
using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions;
using Composestar.StarLight.Entities.Concerns;
using Composestar.StarLight.Entities.Configuration;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities.WeaveSpec;
using Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions;
using Composestar.StarLight.Entities.WeaveSpec.Instructions;
using Composestar.StarLight.Entities.WeaveSpec.Instructions.Visitor;
using Composestar.StarLight.ILWeaver.WeaveStrategy;
using Composestar.StarLight.Utilities;
using Composestar.StarLight.Utilities.Interfaces;
using Composestar.StarLight.Weaving.Strategies;
using Mono.Cecil;
using Mono.Cecil.Cil;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Reflection;
using System.Text;
#endregion

namespace Composestar.StarLight.ILWeaver
{

    /// <summary>
    /// Visits all the InliningInstructions and generate IL code. 
    /// The caller has to add this code (in the Instructions property) to the method. 
    /// </summary>
    /// <remarks>
    /// This visitor can also add types and local variables to the method.
    /// </remarks> 
    [CLSCompliant(false)]
    public class CecilInliningInstructionVisitor : IVisitor, ICecilInliningInstructionVisitor
    {

        #region Constant values

        private const int BranchLabelOffSet = 100000;

        private const String GeneralizationSelector = "+";

        #endregion

        #region Private variables

        /// <summary>
        /// _instructions
        /// </summary>
        private IList<Instruction> _instructions = new List<Instruction>();
        /// <summary>
        /// _worker
        /// </summary>
        private CilWorker _worker;
        /// <summary>
        /// _number of branches
        /// </summary>
        private int _numberOfBranches;
        /// <summary>
        /// _method
        /// </summary>
        private MethodDefinition _method;
        /// <summary>
        /// _called method
        /// </summary>
        private MethodDefinition _calledMethod;
        /// <summary>
        /// _target assembly definition
        /// </summary>
        private AssemblyDefinition _targetAssemblyDefinition;
        /// <summary>
        /// _filter type
        /// </summary>
        private FilterType _filterType;
        /// <summary>
        /// _jump instructions
        /// </summary>
        private Dictionary<int, Instruction> _jumpInstructions = new Dictionary<int, Instruction>();
        /// <summary>
        /// _weave configuration
        /// </summary>
        private ConfigurationContainer _weaveConfiguration;

        /// <summary>
        /// _weave type
        /// </summary>
        private WeaveType _weaveType;

        /// <summary>
        /// _return actions
        /// </summary>
        private List<FilterAction> _returnActions = new List<FilterAction>();

        /// <summary>
        /// _return instruction
        /// </summary>
        private Instruction _returnInstruction;

        /// <summary>
        /// ExceptionHandler used during bookkeeping
        /// </summary>
        private ExceptionHandler bkExceptionHandler;

        /// <summary>
        /// Exception handler for the execution of a filter action, Used in case of post action operations.
        /// </summary>
        private ExceptionHandler faBkExceptionHandler;

        #endregion

        #region Properties

        /// <summary>
        /// Gets or sets the WeaveType object.
        /// </summary>
        /// <value>The type of the weave.</value>
        public WeaveType WeaveType
        {
            get
            {
                return _weaveType;
            }
            set
            {
                _weaveType = value;
            }
        }

        /// <summary>
        /// Gets or sets the type of the filter.
        /// </summary>
        /// <value>The type of the filter.</value>
        public FilterType FilterType
        {
            get
            {
                return _filterType;
            }
            set
            {
                _filterType = value;
            }
        }

        /// <summary>
        /// Weave configuration
        /// </summary>
        /// <returns>Configuration container</returns>
        public ConfigurationContainer WeaveConfiguration
        {
            get
            {
                return _weaveConfiguration;
            }
            set
            {
                _weaveConfiguration = value;
            }
        }

        /// <summary>
        /// Gets or sets the target assembly definition.
        /// </summary>
        /// <value>The target assembly definition.</value>
        public AssemblyDefinition TargetAssemblyDefinition
        {
            get
            {
                return _targetAssemblyDefinition;
            }
            set
            {
                _targetAssemblyDefinition = value;
            }
        }

        /// <summary>
        /// Gets or sets the containing method.
        /// </summary>
        /// <value>The method.</value>
        public MethodDefinition Method
        {
            get
            {
                return _method;
            }
            set
            {
                _method = value;
            }
        }

        /// <summary>
        /// Gets or sets the called method. For inputfilters this is the containing method.
        /// For outputfilters this is the method to which the outgoing call is targeted
        /// </summary>
        /// <value>The method.</value>
        public MethodDefinition CalledMethod
        {
            get
            {
                return _calledMethod;
            }
            set
            {
                _calledMethod = value;
            }
        }

        /// <summary>
        /// Gets or sets the worker.
        /// </summary>
        /// <value>The worker.</value>
        public CilWorker Worker
        {
            get
            {
                return _worker;
            }
            set
            {
                _worker = value;
            }
        }

        /// <summary>
        /// Gets or sets the instructions.
        /// </summary>
        /// <value>The instructions.</value>
        public IList<Instruction> Instructions
        {
            get
            {
                return _instructions;
            }
            set
            {
                _instructions = value;
            }
        }
        #endregion

        #region Helper functions

        /// <summary>
        /// Converts the mono attributes to JPC attributes.
        /// </summary>
        /// <param name="attrMono">The attr mono.</param>
        /// <returns></returns>
        private static ArgumentAttributes ConvertAttributes(Mono.Cecil.ParameterAttributes attrMono)
        {
            ArgumentAttributes attr = ArgumentAttributes.In;

            if ((attrMono & Mono.Cecil.ParameterAttributes.Out) != Mono.Cecil.ParameterAttributes.Out)
                attr = attr | ArgumentAttributes.In;
            else
                attr &= ~ArgumentAttributes.In;
            if ((attrMono & Mono.Cecil.ParameterAttributes.Out) == Mono.Cecil.ParameterAttributes.Out) attr = attr | ArgumentAttributes.Out;
            if ((attrMono & Mono.Cecil.ParameterAttributes.Optional) == Mono.Cecil.ParameterAttributes.Optional) attr = attr | ArgumentAttributes.Optional;
            if ((attrMono & Mono.Cecil.ParameterAttributes.HasDefault) == Mono.Cecil.ParameterAttributes.HasDefault) attr = attr | ArgumentAttributes.HasDefault;
            if ((attrMono & Mono.Cecil.ParameterAttributes.HasFieldMarshal) == Mono.Cecil.ParameterAttributes.HasFieldMarshal) attr = attr | ArgumentAttributes.HasFieldMarshal;

            return attr;
        }

        /// <summary>
        /// Gets the jump label.
        /// </summary>
        /// <param name="labelId">The label id.</param>
        /// <returns></returns>
        private Instruction GetJumpLabel(int labelId)
        {
            if (labelId < 0)
                return _returnInstruction;

            Instruction jumpNopInstruction;
            if (!_jumpInstructions.TryGetValue(labelId, out jumpNopInstruction))
            {
                jumpNopInstruction = Worker.Create(OpCodes.Nop);
                _jumpInstructions.Add(labelId, jumpNopInstruction);
            }

            return jumpNopInstruction;
        }

        /// <summary>
        /// Creates the local variable.
        /// </summary>
        /// <param name="type">The type of the variable to create.</param>
        /// <returns>Returns the variable as a <see cref="T:Mono.Cecil.VariableDefinition"></see>.</returns>
        public VariableDefinition CreateLocalVariable(Type type)
        {
            TypeReference typeRef = CecilUtilities.CreateTypeReference(TargetAssemblyDefinition, type);
            return CreateLocalVariable(typeRef);
        }

        /// <summary>
        /// Creates the local variable.
        /// </summary>
        /// <param name="type">The typereference to create.</param>
        /// <returns>Returns the variable as a <see cref="T:Mono.Cecil.VariableDefinition"></see>.</returns>
        public VariableDefinition CreateLocalVariable(TypeReference type)
        {
            if (type == null)
                throw new ArgumentNullException("type");

            VariableDefinition var = new VariableDefinition(type);
            var.Name = type.ToString();
            Method.Body.InitLocals = true;
            Method.Body.Variables.Add(var);

            return var;
        }

        /// <summary>
        /// Because we need local vars to store the object and type of arguments in, we have to add these local vars.
        /// But only once, so these functions make sure we only have one of this variables
        /// </summary>
        private VariableDefinition m_JpcLocal;
        /// <summary>
        /// The action store local used to store all the after actions.
        /// </summary>
        private VariableDefinition m_ActionStoreLocal;

        /// <summary>
        /// Creates the action store local.
        /// </summary>
        /// <returns></returns>
        private VariableDefinition CreateActionStoreLocal()
        {
            if (m_ActionStoreLocal == null)
            {
                m_ActionStoreLocal = CreateLocalVariable(typeof(FilterContext));
            }

            return m_ActionStoreLocal;
        }

        #region JoinPointContext

        /// <summary>
        /// Creates the join point context variable.
        /// </summary>
        public VariableDefinition CreateJoinPointContextLocal()
        {
            if (m_JpcLocal == null)
            {
                m_JpcLocal = CreateLocalVariable(typeof(JoinPointContext));
            }

            return m_JpcLocal;
        }

        #endregion

        #endregion

        /// <summary>
        /// Performs the weaving of the current instruction set.
        /// </summary>
        /// <param name="instructionSet">The instruction set.</param>
        public void DoWeave(FilterCode instructionSet)
        {
            // Store the current index in the Instructions to use it later to insert the create action store.
            int currentIndex = Instructions.Count;

            // Create returnInstruction, to be used to jump to when returning.
            _returnInstruction = Worker.Create(OpCodes.Nop);

            // Create the join point context
            CreateJoinPointContext(instructionSet.BookKeeping);

            // Generate the filterset instructions
            instructionSet.Accept(this);

            // Add return jumpto instruction
            Instructions.Add(_returnInstruction);

            // Weave the create action store command and the return actions
            if (_returnActions.Count > 0)
            {
                // Insert the create action store command:
                CreateActionStore(currentIndex);

                // Weave the return actions:
                WeaveReturnActions();
            }

            // Restore the join point context
            RestoreJoinPointContext(instructionSet.BookKeeping);

            // If inputfilters are weaved, do a return
            if (FilterType == FilterType.InputFilter)
            {
                // Generate a return instruction
                Instructions.Add(Worker.Create(OpCodes.Ret));
            }
        }

        /// <summary>
        /// Weave return actions
        /// </summary>
        private void WeaveReturnActions()
        {
            if (_returnActions.Count == 0)
            {
                return;
            }

            //
            // While Expression:
            //

            // Get an actionstore local
            VariableDefinition asVar = CreateActionStoreLocal();

            // Load the local
            Instruction whileStart = Worker.Create(OpCodes.Ldloc, asVar);
            Instructions.Add(whileStart);

            // Call the HasMoreStoredActions method
            Instructions.Add(Worker.Create(OpCodes.Callvirt,
                CecilUtilities.CreateMethodReference(TargetAssemblyDefinition,
                CachedMethodDefinition.HasMoreStoredActions)));

            // Add branch code
            Instruction whileEnd = Worker.Create(OpCodes.Nop);
            Instructions.Add(Worker.Create(OpCodes.Brfalse, whileEnd));

            //
            // Switch expression
            //

            // Load the actionstore local
            Instructions.Add(Worker.Create(OpCodes.Ldloc, asVar));

            // Call the NextStoredAction method
            Instructions.Add(Worker.Create(OpCodes.Callvirt,
                CecilUtilities.CreateMethodReference(TargetAssemblyDefinition,
                CachedMethodDefinition.NextStoredAction)));

            // The switch statement
            Instruction[] caseStarts = new Instruction[_returnActions.Count];
            Instructions.Add(Worker.Create(OpCodes.Switch, caseStarts));

            // Default: jump to the end
            Instruction switchEnd = Worker.Create(OpCodes.Nop);
            Instructions.Add(Worker.Create(OpCodes.Br, switchEnd));

            //
            // Cases
            //
            for (int i = 0; i < _returnActions.Count; i++)
            {
                int lastInstructionIndex = Instructions.Count - 1;

                // weave filteraction
                FilterAction returnAction = _returnActions[i];
                WeaveFilterAction(returnAction);

                // Add a jump to the end of the switch
                Instructions.Add(Worker.Create(OpCodes.Br, switchEnd));

                // Set switch jumptable entry for this case to the first instruction of the case
                caseStarts[i] = Instructions[lastInstructionIndex + 1];
            }

            //
            // Switch end
            //

            // Emit the end instruction
            Instructions.Add(switchEnd);

            //
            // While Loop
            //

            // Add the branch back to condition part
            Instructions.Add(Worker.Create(OpCodes.Br, whileStart));

            // Place the end label
            Instructions.Add(whileEnd);
        }

        #region Inlining Instructions Visitor Handlers

        /// <summary>
        /// Visits the filter code
        /// </summary>
        /// <param name="filterCode"></param>
        public void VisitFilterCode(FilterCode filterCode)
        {
            filterCode.Instructions.Accept(this);
        }

        /// <summary>
        /// Visits the inline instruction.
        /// </summary>
        /// <remarks>
        /// Place a label Id if an Id is available. Labels are Nop instructions and are used by the jump and branch action.
        /// They are cached so we can reference ahead of time to labels not yet introduced. 
        /// If the inlining tool did not generate the correct labels (it forgot one), then the branch will jump to the end of the method.
        /// </remarks> 
        /// <param name="inlineInstruction">The inline instruction.</param>
        public void VisitInlineInstruction(InlineInstruction inlineInstruction)
        {
            if (inlineInstruction.Label != -1)
            {
                Instructions.Add(GetJumpLabel(inlineInstruction.Label));
            }
        }

        /// <summary>
        /// Visits the filter action. Uses Strategypattern to weave a specific instruction.
        /// </summary>
        /// <param name="filterAction">The filter action.</param>
        public void VisitFilterAction(FilterAction filterAction)
        {
            FilterAction degeneralizedAction = Degeneralize(filterAction);

            if (degeneralizedAction.OnCall)
            {
                WeaveFilterAction(degeneralizedAction);
                if (degeneralizedAction.Returning)
                {
                    Instructions.Add(Worker.Create(OpCodes.Br, _returnInstruction));
                }
            }
            else
            {
                StoreAction(degeneralizedAction);
            }
        }

        /// <summary>
        /// Degeneralizes the filter action. This means that the generalization selector
        /// is replaced with the called methods name.
        /// </summary>
        /// <param name="filterAction">The filter action to be degeneralized</param>
        private FilterAction Degeneralize(FilterAction filterAction)
        {
            if (filterAction.Selector.Equals(GeneralizationSelector) ||
                filterAction.SubstitutionSelector.Equals(GeneralizationSelector))
            {
                FilterAction degenFilterAction = new FilterAction();

                degenFilterAction.FullName = filterAction.FullName;
                degenFilterAction.Label = filterAction.Label;
                degenFilterAction.OnCall = filterAction.OnCall;
                degenFilterAction.Returning = filterAction.Returning;

                if (filterAction.Selector.Equals(GeneralizationSelector))
                {
                    degenFilterAction.Selector = CalledMethod.Name;
                }
                else
                {
                    degenFilterAction.Selector = filterAction.Selector;
                }

                if (filterAction.SubstitutionSelector.Equals(GeneralizationSelector))
                {
                    degenFilterAction.SubstitutionSelector = CalledMethod.Name;
                }
                else
                {
                    degenFilterAction.SubstitutionSelector = filterAction.SubstitutionSelector;
                }

                degenFilterAction.SubstitutionTarget = filterAction.SubstitutionTarget;
                degenFilterAction.Target = filterAction.Target;
                degenFilterAction.Type = filterAction.Type;
                degenFilterAction.BookKeeping = filterAction.BookKeeping;
                degenFilterAction.ResourceOperations = filterAction.ResourceOperations;

                return degenFilterAction;
            }
            else
            {
                return filterAction;
            }
        }

        /// <summary>
        /// Weaves the filter action. Uses strategy pattern.
        /// </summary>
        /// <param name="filterAction"></param>
        private void WeaveFilterAction(FilterAction filterAction)
        {
            if (filterAction == null)
                throw new ArgumentNullException("filterAction");

            // Create clone with current selector applied to the possible generalized selector in the filteraction
            filterAction = filterAction.GetClone(CalledMethod.Name);

            if (filterAction.BookKeeping)
            {
                WeaveBookKeepingPre(filterAction);
            }

            FilterActionWeaveStrategy strategy = FilterActionStrategyDispatcher.GetFilterActionWeaveStrategy(filterAction.Type);

            strategy.Weave(this, filterAction, CalledMethod);

            if (filterAction.BookKeeping)
            {
                WeaveBookKeepingPost(filterAction);
            }
        }

        #region Filter Action Resource Operation Book Keeping

        private static string[] FILTER_ACTION_SEPARATOR = new string[1] { "<FilterAction>" };

        /// <summary>
        /// Adds bookkeeping information
        /// </summary>
        /// <param name="filterAction"></param>
        private void WeaveBookKeepingPre(FilterAction filterAction)
        {
            VariableDefinition jpcVar = CreateJoinPointContextLocal();

            if (!String.IsNullOrEmpty(filterAction.ResourceOperations))
            {
                string[] postops = filterAction.ResourceOperations.Split(FILTER_ACTION_SEPARATOR, 2, StringSplitOptions.None);
                if (postops.Length >= 1)
                {
                    postops[0] = postops[0].Trim(';');
                }
                if (postops.Length >= 1 && !String.IsNullOrEmpty(postops[0]))
                {

                    Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));
                    // resourceoperations list
                    Instructions.Add(Worker.Create(OpCodes.Ldstr, postops[0].Trim()));
                    Instructions.Add(Worker.Create(OpCodes.Callvirt,
                        CecilUtilities.CreateMethodReference(TargetAssemblyDefinition,
                        CachedMethodDefinition.JoinPointContextAddResourceOperationList)));
                }

                if (postops.Length >= 2)
                {
                    postops[1] = postops[1].Trim(';');
                }
                if (postops.Length >= 2 && !String.IsNullOrEmpty(postops[1]))
                {
                    // start Try block
                    faBkExceptionHandler = new ExceptionHandler(ExceptionHandlerType.Finally);
                    faBkExceptionHandler.TryStart = Worker.Create(OpCodes.Nop);
                    Instructions.Add(faBkExceptionHandler.TryStart);
                }
            }

            // Create FilterAction object:
            FilterActionElement filterActionElement = null;
            foreach (FilterActionElement fae in WeaveConfiguration.FilterActions)
            {
                if (fae.FullName.Equals(filterAction.FullName))
                {
                    filterActionElement = fae;
                    break;
                }
            }

            if (filterActionElement == null)
                throw new ILWeaverException(string.Format(CultureInfo.CurrentCulture, Properties.Resources.CouldNotResolveFilterAction, filterAction.FullName));

            TypeReference typeRef = CecilUtilities.ResolveType(filterAction.FullName, filterActionElement.Assembly, null);
            TypeDefinition typeDef = CecilUtilities.ResolveTypeDefinition(typeRef);

            string _ResourceOperationAttribute = typeof(Composestar.StarLight.Filters.FilterTypes.ResourceOperationAttribute).FullName;

            foreach (CustomAttribute ca in typeRef.CustomAttributes)
            {
                if (ca.Constructor.DeclaringType.FullName.Equals(_ResourceOperationAttribute))
                {
                    string seq = "";
                    bool manualBk = false;
                    bool autoBk = true;
                    if (ca.ConstructorParameters.Count >= 1)
                    {
                        seq = Convert.ToString(ca.ConstructorParameters[0]);
                    }
                    if (ca.ConstructorParameters.Count >= 2)
                    {
                        manualBk = (Convert.ToBoolean(ca.ConstructorParameters[1], CultureInfo.InvariantCulture));
                    }
                    if (ca.ConstructorParameters.Count >= 3)
                    {
                        autoBk = (Convert.ToBoolean(ca.ConstructorParameters[2], CultureInfo.InvariantCulture));
                    }

                    if (manualBk)
                    {
                        Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));
                        if (autoBk)
                        {
                            Instructions.Add(Worker.Create(OpCodes.Ldc_I4_1));
                        }
                        else
                        {
                            Instructions.Add(Worker.Create(OpCodes.Ldc_I4_0));
                        }
                        Instructions.Add(Worker.Create(OpCodes.Callvirt,
                            CecilUtilities.CreateMethodReference(TargetAssemblyDefinition,
                            CachedMethodDefinition.JoinPointContextSetAutoBookKeeping)));
                    }
                    else
                    {
                        // never perform auto bookkeeping when the whole list is emitted
                        Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));
                        Instructions.Add(Worker.Create(OpCodes.Ldc_I4_0));
                        Instructions.Add(Worker.Create(OpCodes.Callvirt,
                            CecilUtilities.CreateMethodReference(TargetAssemblyDefinition,
                            CachedMethodDefinition.JoinPointContextSetAutoBookKeeping)));

                        Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));
                        // resourceoperations list
                        Instructions.Add(Worker.Create(OpCodes.Ldstr, seq));
                        Instructions.Add(Worker.Create(OpCodes.Callvirt,
                            CecilUtilities.CreateMethodReference(TargetAssemblyDefinition,
                            CachedMethodDefinition.JoinPointContextAddResourceOperationList)));
                    }
                    break;
                }
            }
        }

        /// <summary>
        /// Adds bookkeeping information after the filter action. This only hadd the operations after the filter action execution (which could be none).
        /// </summary>
        /// <param name="filterAction"></param>
        private void WeaveBookKeepingPost(FilterAction filterAction)
        {
            if (String.IsNullOrEmpty(filterAction.ResourceOperations))
            {
                return;
            }

            string[] postops = filterAction.ResourceOperations.Split(FILTER_ACTION_SEPARATOR, 2, StringSplitOptions.None);
            if (postops.Length >= 2)
            {
                postops[1] = postops[1].Trim(';');
            }
            if (postops.Length >= 2 && !String.IsNullOrEmpty(postops[1]))
            {
                VariableDefinition jpcVar = CreateJoinPointContextLocal();

                // post operations are done in a try {} finally {} block so that these operations
                // are added even if the filter action raises an exception (e.g. error action)
                // post operations are very often empty, in that case no try finally will be added

                // end try block
                Instruction bkEHend = Worker.Create(OpCodes.Nop);
                Instructions.Add(Worker.Create(OpCodes.Leave, bkEHend));

                // start finally handler
                faBkExceptionHandler.TryEnd = Worker.Create(OpCodes.Nop);
                Instructions.Add(faBkExceptionHandler.TryEnd);
                faBkExceptionHandler.HandlerStart = faBkExceptionHandler.TryEnd;

                Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));
                // resourceoperations list
                Instructions.Add(Worker.Create(OpCodes.Ldstr, postops[1].Trim()));
                Instructions.Add(Worker.Create(OpCodes.Callvirt,
                    CecilUtilities.CreateMethodReference(TargetAssemblyDefinition,
                    CachedMethodDefinition.JoinPointContextAddResourceOperationList)));

                // end finally handler
                Instructions.Add(Worker.Create(OpCodes.Endfinally));

                faBkExceptionHandler.HandlerEnd = bkEHend;
                Instructions.Add(faBkExceptionHandler.HandlerEnd);
                Method.Body.ExceptionHandlers.Add(faBkExceptionHandler);
            }
        }

        #endregion

        /// <summary>
        /// Add a jump to another block
        /// </summary>
        /// <param name="jump"></param>
        public void VisitJumpInstruction(JumpInstruction jump)
        {
            Instruction jumpToInstruction = GetJumpLabel(jump.Target);
            if (jumpToInstruction == null)
                throw new ILWeaverException(Properties.Resources.FilterJumpLabelIsNotSet);

            // Add an unconditional branch instruction
            Instructions.Add(Worker.Create(OpCodes.Br, jumpToInstruction));
        }

        #region Branching

        /// <summary>
        /// Generate the branching code for the conditions.
        /// </summary>
        /// <example>
        /// <code>
        /// if (condition)
        /// {
        ///   // true
        /// }
        /// else
        /// {
        ///   // false
        /// }
        /// </code>
        /// In IL code:
        /// <code>
        /// condition
        /// brfalse l1:
        /// trueblock
        /// br l2
        /// l1:
        /// falseblock
        /// l2:
        /// </code>
        /// </example> 
        /// <param name="branch">The branch.</param>
        public void VisitBranch(Branch branch)
        {
            // Add condition code
            CecilConditionsVisitor conditionsVisitor = new CecilConditionsVisitor(this);
            ((Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions.Visitor.IVisitable)branch.ConditionExpression).Accept(conditionsVisitor);

            // Add branch code
            branch.Label = BranchLabelOffSet + _numberOfBranches;   // TODO check the correctness of this constructions (Michiel)
            _numberOfBranches = _numberOfBranches + 2;
            Instructions.Add(Worker.Create(OpCodes.Brfalse, GetJumpLabel(branch.Label)));
        }

        /// <summary>
        /// Generate the branching code for the conditions.
        /// </summary>
        /// <param name="branch">The branch.</param>
        public void VisitBranchFalse(Branch branch)
        {
            // Make sure we jump over the false block
            Instructions.Add(Worker.Create(OpCodes.Br, GetJumpLabel(branch.Label + 1)));

            // Place label for the false block
            Instructions.Add(GetJumpLabel(branch.Label));
        }

        /// <summary>
        /// Visits the branch end.
        /// </summary>
        /// <param name="branch">The branch.</param>
        public void VisitBranchEnd(Branch branch)
        {
            // Place label for the End branch block
            Instructions.Add(GetJumpLabel(branch.Label + 1));

            // Reset branch label:
            branch.Label = -1;
        }

        #endregion

        #region Action store



        /// <summary>
        /// Generates the instructions that create an action store.
        /// We use the FilterContext object as the actionstore, so when this contextinstruction is encountered, 
        /// a new instance of the FilterContext object needs to be created:
        /// <code>
        /// FilterContext actionStore = new FilterContext();
        /// </code> 
        /// <param name="index">The index in Instructions where these instructions need to be inserted</param>
        /// </summary>
        public void CreateActionStore(int index)
        {
            // Get an actionstore local
            VariableDefinition asVar = CreateActionStoreLocal();

            //
            // Create new FilterContext object
            //

            // Call the constructor
            Instructions.Insert(index, Worker.Create(OpCodes.Newobj, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.FilterContextConstructor)));

            // Store the local
            Instructions.Insert(index + 1, Worker.Create(OpCodes.Stloc, asVar));
        }

        /// <summary>
        /// Generates the instructions that store an action. 
        /// The action that needs to be stored is represented by an integer id, 
        /// so actually only this id needs to be stored. 
        /// </summary>
        /// <example>
        /// This id is on compiletime the index in the returnActions list of the action to be stored. 
        /// So the code that needs to be created for this instructions is:
        /// <code>
        /// actionStore.storeAction( index );
        /// </code>
        /// </example> 
        /// <param name="filterAction">The filter action to be stored.</param>
        public void StoreAction(FilterAction filterAction)
        {
            // Add the filteraction to the returnActions:
            _returnActions.Add(filterAction);
            int index = _returnActions.Count - 1;

            // Get an actionstore local
            VariableDefinition asVar = CreateActionStoreLocal();

            // Load the local
            Instructions.Add(Worker.Create(OpCodes.Ldloc, asVar));

            // Load the id onto the stack
            Instructions.Add(Worker.Create(OpCodes.Ldc_I4, index));

            // Call the StoreAction method
            Instructions.Add(Worker.Create(OpCodes.Callvirt, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.StoreAction)));

        }

        #endregion

        /// <summary>
        /// This method creates and initializes the JoinPointContext. This always happens at the beginning
        /// of the filtercode. All FilterActions should use the JoinPointContext to access parameter values and
        /// return-value, and not use for example the LdParam opcode, because this value might not be up to date
        /// or might be entirely wrong, because the action is in an outputfilter.
        /// </summary>
        public void CreateJoinPointContext(bool bookKeeping)
        {
            // Create a new or use an existing local variable for the JoinPointContext
            VariableDefinition jpcVar = CreateJoinPointContextLocal();

            //
            // Create new joinpointcontext object
            //
            Instructions.Add(Worker.Create(OpCodes.Newobj,
                CecilUtilities.CreateMethodReference(TargetAssemblyDefinition,
                CachedMethodDefinition.JoinPointContextConstructor)));

            // Store the just created joinpointcontext object
            Instructions.Add(Worker.Create(OpCodes.Stloc, jpcVar));

            //
            // Store sender (only for outputfilters)
            //
            if (FilterType == FilterType.OutputFilter && Method.HasThis)
            {
                // Load joinpointcontext object
                Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

                // Load this
                Instructions.Add(Worker.Create(OpCodes.Ldarg, Method.This));

                // Call set_Sender
                Instructions.Add(Worker.Create(OpCodes.Callvirt,
                    CecilUtilities.CreateMethodReference(TargetAssemblyDefinition,
                    CachedMethodDefinition.JoinPointContextSetSender)));
            }

            if (!CalledMethod.ReturnType.ReturnType.FullName.Equals(CecilUtilities.VoidType))
            {
                //
                // Store returnType                
                //

                // Load joinpointcontext object
                Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

                // Determine type
                Instructions.Add(Worker.Create(OpCodes.Ldtoken, CalledMethod.ReturnType.ReturnType));
                Instructions.Add(Worker.Create(OpCodes.Call, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.GetTypeFromHandle)));

                // Call set_ReturnType in JoinPointContext
                Instructions.Add(Worker.Create(OpCodes.Callvirt, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.JoinPointContextSetReturnType)));
            }

            //
            // Add the arguments, these are stored at the top of the stack
            //
            switch (FilterType)
            {
                case FilterType.None:
                    break;
                case FilterType.InputFilter:
                    foreach (ParameterDefinition param in CalledMethod.Parameters)
                    {
                        if ((param.Attributes & Mono.Cecil.ParameterAttributes.Out) != Mono.Cecil.ParameterAttributes.Out)
                        {
                            // Load the argument
                            Instructions.Add(Worker.Create(OpCodes.Ldarg, param));

                            // Check for reference parameter
                            if (param.ParameterType.FullName.EndsWith("&"))
                            {
                                Instructions.Add(Worker.Create(OpCodes.Ldobj, param.ParameterType));
                            }

                            // Check if parameter is value type, then box
                            if (param.ParameterType.IsValueType || param.ParameterType is GenericParameter)
                            {
                                Instructions.Add(Worker.Create(OpCodes.Box, param.ParameterType));
                            }
                        }
                        else
                        {
                            // Load null if parameter is out
                            Instructions.Add(Worker.Create(OpCodes.Ldnull));
                        }

                        int ordinal = param.Sequence - 1;// (CalledMethod.HasThis ? 1 : 0);

                        // Load the ordinal
                        Instructions.Add(Worker.Create(OpCodes.Ldc_I4, ordinal));

                        // Determine type
                        Instructions.Add(Worker.Create(OpCodes.Ldtoken, param.ParameterType));
                        Instructions.Add(Worker.Create(OpCodes.Call, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.GetTypeFromHandle)));

                        // Determine the parameter direction
                        ArgumentAttributes attr = ConvertAttributes(param.Attributes);

                        Instructions.Add(Worker.Create(OpCodes.Ldc_I4, (int)attr));

                        // Load jpc
                        Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

                        // Call the AddArgument function statically
                        Instructions.Add(Worker.Create(OpCodes.Call, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.JoinPointContextAddArgument)));


                    }
                    break;
                case FilterType.OutputFilter:
                    ParameterDefinitionCollection parameters = CalledMethod.Parameters;
                    int count = parameters.Count;

                    // iterate backward over parameters, because last parameter is on top of the stack
                    for (int i = count - 1; i >= 0; i--)
                    {
                        ParameterDefinition param = parameters[i];

                        int ordinal = param.Sequence - 1;// (CalledMethod.HasThis ? 1 : 0);

                        // Check for reference parameter
                        if (param.ParameterType.FullName.EndsWith("&"))
                        {
                            Instructions.Add(Worker.Create(OpCodes.Ldobj, param.ParameterType));
                        }

                        // Check if parameter is value type, then box
                        if (param.ParameterType.IsValueType || param.ParameterType is GenericParameter)
                        {
                            Instructions.Add(Worker.Create(OpCodes.Box, param.ParameterType));
                        }

                        // Load the ordinal
                        Instructions.Add(Worker.Create(OpCodes.Ldc_I4, ordinal));

                        // Determine type
                        Instructions.Add(Worker.Create(OpCodes.Ldtoken, param.ParameterType));
                        Instructions.Add(Worker.Create(OpCodes.Call, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.GetTypeFromHandle)));

                        // Determine the parameter direction
                        ArgumentAttributes attr = ConvertAttributes(param.Attributes);

                        Instructions.Add(Worker.Create(OpCodes.Ldc_I4, (int)attr));

                        // Load jpc
                        Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

                        // Call the AddArgument function statically
                        Instructions.Add(Worker.Create(OpCodes.Call, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.JoinPointContextAddArgument)));
                    }
                    break;
                default:
                    break;
            }

            //
            // Set the target
            //

            if (FilterType == FilterType.InputFilter)
            {
                // Load the joinpointcontext object
                Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

                // Load the this pointer
                if (Method.HasThis && !Method.DeclaringType.IsValueType)
                {
                    Instructions.Add(Worker.Create(OpCodes.Ldarg, Method.This));
                }
                else if (Method.DeclaringType.IsValueType)
                {
                    Instructions.Add(Worker.Create(OpCodes.Ldarg, Method.This));
                    Instructions.Add(Worker.Create(OpCodes.Ldobj, Method.DeclaringType));
                    Instructions.Add(Worker.Create(OpCodes.Box, Method.DeclaringType));
                }
                else
                {
                    Instructions.Add(Worker.Create(OpCodes.Ldnull));
                }

                // Assign to the Target property
                Instructions.Add(Worker.Create(OpCodes.Callvirt, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.JoinPointContextSetStartTarget)));

            }
            else
            {
                // Load the joinpointcontext object
                Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

                // Assign to the Target property through static method
                Instructions.Add(Worker.Create(OpCodes.Call, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.JoinPointContextSetTarget)));
            }

            //
            // Load the current method base. This is a trick in IL (.NET2) to get the MethodBase using lightweight reflection.
            // See for more information: http://www.interact-sw.co.uk/iangblog/2005/08/31/methodinfofromtoken
            // In IL code:
            // ldtoken method void SomeClass::SomeMethod()
            // call class [mscorlib]System.Reflection.MethodBase [mscorlib]System.Reflection.MethodBase::GetMethodFromHandle(valuetype [mscorlib]System.RuntimeMethodHandle)
            //

            // Load joinpointcontext first
            Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

            Instructions.Add(Worker.Create(OpCodes.Ldtoken, TargetAssemblyDefinition.MainModule.Import(CalledMethod)));

            if (CalledMethod.GenericParameters.Count > 0 || CalledMethod.DeclaringType.GenericParameters.Count > 0)
            {
                // If it is a generic we have to add the declaring type token. This is not directly supported in tools like Reflector.
                Instructions.Add(Worker.Create(OpCodes.Ldtoken, TargetAssemblyDefinition.MainModule.Import(CalledMethod.DeclaringType)));
                Instructions.Add(Worker.Create(OpCodes.Call, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.GetMethodFromHandleExt)));
            }
            else
                Instructions.Add(Worker.Create(OpCodes.Call, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.GetMethodFromHandle)));

            // Assign name to MethodInformation
            Instructions.Add(Worker.Create(OpCodes.Callvirt, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.JoinPointContextSetMethodInformation)));

            //
            // Set the selector
            //

            // Load joinpointcontext first
            Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

            // Load the name onto the stack
            Instructions.Add(Worker.Create(OpCodes.Ldstr, CalledMethod.Name));

            // Assign name to MethodName
            Instructions.Add(Worker.Create(OpCodes.Callvirt, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.JoinPointContextSetStartSelector)));

            if (bookKeeping)
            {
                JpcBookKeepingBegin();
            }
        }

        /// <summary>
        /// Restores the JoinPointContext at the end of the filtercode. It puts, for example, the returnvalue on the
        /// stack.
        /// </summary>
        public void RestoreJoinPointContext(bool bookKeeping)
        {
            // Get JoinPointContext
            VariableDefinition jpcVar = CreateJoinPointContextLocal();

            //
            // Restore out/ref parameters
            //
            switch (FilterType)
            {
                case FilterType.None:
                    break;
                case FilterType.InputFilter:
                    foreach (ParameterDefinition param in CalledMethod.Parameters)
                    {
                        if (param.ParameterType.FullName.EndsWith("&"))
                        {
                            // Load the argument
                            Instructions.Add(Worker.Create(OpCodes.Ldarg, param));

                            //
                            // Load the value
                            //

                            // Load jpc
                            Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

                            // Load the ordinal
                            int ordinal = param.Sequence - 1;// (CalledMethod.HasThis ? 1 : 0);
                            Instructions.Add(Worker.Create(OpCodes.Ldc_I4, ordinal));

                            // Call the GetArgumentValue function
                            Instructions.Add(Worker.Create(OpCodes.Call, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.JoinPointContextGetArgumentValue)));

                            // Check if returnvalue is value type, then unbox, else cast
                            if (param.ParameterType.IsValueType || param.ParameterType is GenericParameter)
                            {
                                Instructions.Add(Worker.Create(OpCodes.Unbox_Any, param.ParameterType));
                            }
                            else
                            {
                                Instructions.Add(Worker.Create(OpCodes.Castclass, param.ParameterType));
                            }

                            //
                            // Store the value
                            //
                            Instructions.Add(Worker.Create(OpCodes.Stobj, param.ParameterType));
                        }

                    }
                    break;
                case FilterType.OutputFilter:
                    //TODO no outputfilters when call has ref or out parameters
                    break;
                default:
                    break;
            }

            // Retrieve returnvalue
            if (!CalledMethod.ReturnType.ReturnType.FullName.Equals(CecilUtilities.VoidType))
            {
                if (bookKeeping)
                {
                    JpcBookKeepingEnd(true);
                }

                // Load JoinPointContext
                Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

                // Get returnvalue
                Instructions.Add(Worker.Create(OpCodes.Callvirt, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.JoinPointContextGetReturnValue)));

                // Check if returnvalue is value type, then unbox, else cast
                if (CalledMethod.ReturnType.ReturnType.IsValueType || CalledMethod.ReturnType.ReturnType is GenericParameter)
                {
                    Instructions.Add(Worker.Create(OpCodes.Unbox_Any, CalledMethod.ReturnType.ReturnType));
                }
                else
                {
                    Instructions.Add(Worker.Create(OpCodes.Castclass, CalledMethod.ReturnType.ReturnType));
                }
            }
            else
            {
                if (bookKeeping)
                {
                    JpcBookKeepingEnd(false);
                }
            }
        }

        /// <summary>
        /// Activate resource operation book keeping
        /// </summary>
        public void JpcBookKeepingBegin()
        {
            VariableDefinition jpcVar = CreateJoinPointContextLocal();

            // Load joinpointcontext first
            Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));
            // "true" constant
            Instructions.Add(Worker.Create(OpCodes.Ldc_I4_1));
            Instructions.Add(Worker.Create(OpCodes.Callvirt, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.JoinPointContextSetBookKeeping)));

            bkExceptionHandler = new ExceptionHandler(ExceptionHandlerType.Finally);
            bkExceptionHandler.TryStart = Worker.Create(OpCodes.Nop);
            Instructions.Add(bkExceptionHandler.TryStart);
        }

        /// <summary>
        /// Finalize resource operation book keeping
        /// </summary>
        public void JpcBookKeepingEnd(bool withReturn)
        {
            VariableDefinition jpcVar = CreateJoinPointContextLocal();

            if (withReturn)
            {
                // TODO: optimize
                Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));
                Instructions.Add(Worker.Create(OpCodes.Ldstr, "return.read"));
                Instructions.Add(Worker.Create(OpCodes.Callvirt,
                    CecilUtilities.CreateMethodReference(TargetAssemblyDefinition,
                    CachedMethodDefinition.JoinPointContextAddResourceOperationList)));
            }

            Instruction bkEHend = Worker.Create(OpCodes.Nop);

            Instructions.Add(Worker.Create(OpCodes.Leave, bkEHend));

            bkExceptionHandler.TryEnd = Worker.Create(OpCodes.Nop);
            Instructions.Add(bkExceptionHandler.TryEnd);

            bkExceptionHandler.HandlerStart = bkExceptionHandler.TryEnd;

            Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));
            Instructions.Add(Worker.Create(OpCodes.Callvirt, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.JoinPointContextFinalizeBookKeeping)));
            Instructions.Add(Worker.Create(OpCodes.Endfinally));

            bkExceptionHandler.HandlerEnd = bkEHend;
            Instructions.Add(bkExceptionHandler.HandlerEnd);

            Method.Body.ExceptionHandlers.Add(bkExceptionHandler);
        }

        #endregion

    }
}
