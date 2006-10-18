using System;
using System.Collections.Generic;
using System.Globalization;
using System.Reflection;
using System.Text;

using Composestar.Repository.LanguageModel.Inlining;
using Composestar.Repository.LanguageModel.Inlining.Visitor;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions;

using Mono.Cecil;
using Mono.Cecil.Cil;

namespace Composestar.StarLight.ILWeaver
{

    /// <summary>
    /// Visits all the InliningInstructions and generate IL code. 
    /// The caller has to add this code (in the Instructions property) to the method. 
    /// </summary>
    /// <remarks>
    /// This visitor can also add types and local variables to the method.
    /// </remarks> 
    public class CecilInliningInstructionVisitor : IVisitor
    {

        #region Constant values

        private const int FilterContextJumpId = 9999;
        private const int BranchLabelOffSet = 100000;
        
        #endregion

        #region Private variables
        private IList<Instruction> m_Instructions = new List<Instruction>();
        private CilWorker m_Worker;
        private int m_NumberOfBranches = 0;
        private MethodDefinition m_Method;
        private MethodDefinition called_Method;
        private AssemblyDefinition m_TargetAssemblyDefinition;
        private FilterTypes m_FilterType;
        private Dictionary<int, Instruction> m_JumpInstructions = new Dictionary<int, Instruction>();
        private ILanguageModelAccessor m_LanguageModelAccessor;
        #endregion

        #region FilterType Enumeration

        /// <summary>
        /// Possible filter types this visitor can generate code for.
        /// </summary>
        public enum FilterTypes
        {
            /// <summary>
            /// Default none.
            /// </summary>
            None = 0,
            /// <summary>
            /// Input filter.
            /// </summary>
            InputFilter = 1,
            /// <summary>
            /// Output filter.
            /// </summary>
            OutputFilter = 2,
        }

        #endregion

        #region Properties
        /// <summary>
        /// Gets or sets the type of the filter.
        /// </summary>
        /// <value>The type of the filter.</value>
        public FilterTypes FilterType
        {
            get
            {
                return m_FilterType;
            }
            set
            {
                m_FilterType = value;
            }
        }

        /// <summary>
        /// Gets or sets the repository access.
        /// </summary>
        /// <value>The repository access.</value>
        public ILanguageModelAccessor RepositoryAccess
        {
            get
            {
                return m_LanguageModelAccessor;
            }
            set
            {
                m_LanguageModelAccessor = value;
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
                return m_TargetAssemblyDefinition;
            }
            set
            {
                m_TargetAssemblyDefinition = value;
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
                return m_Method;
            }
            set
            {
                m_Method = value;
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
                return called_Method;
            }
            set
            {
                called_Method = value;
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
                return m_Worker;
            }
            set
            {
                m_Worker = value;
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
                return m_Instructions;
            }
            set
            {
                m_Instructions = value;
            }
        }
        #endregion

        #region Helper functions

        /// <summary>
        /// Creates the context expression.
        /// </summary>
        /// <remarks>
        /// The ActionStore object has to be used before (as in created by the CreateActionStore visitor).
        /// </remarks> 
        /// <param name="expr">The expr.</param>
        private void CreateContextExpression(ContextExpression expr)
        {
            if (expr == null)
                return;

            // Get an actionstore local
            VariableDefinition asVar = CreateActionStoreLocal();

            // Load the local
            Instructions.Add(Worker.Create(OpCodes.Ldloc, asVar));

            switch (expr.Type)
            {
                case ContextExpression.HAS_MORE_ACTIONS:
                    // Call the HasMoreStoredActions method
                    Instructions.Add(Worker.Create(OpCodes.Callvirt, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.HasMoreStoredActions)));
                    break;
                case ContextExpression.RETRIEVE_ACTION:
                    // Call the NextStoredAction method
                    Instructions.Add(Worker.Create(OpCodes.Callvirt, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.NextStoredAction)));
                    break;

            }

        }

        /// <summary>
        /// Converts the mono attributes to JPC attributes.
        /// </summary>
        /// <param name="attrMono">The attr mono.</param>
        /// <returns></returns>
        public ArgumentAttributes ConvertAttributes(Mono.Cecil.ParamAttributes attrMono)
        {
            ArgumentAttributes attr = ArgumentAttributes.In;

            if ((attrMono & ParamAttributes.In) == ParamAttributes.In) 
                attr = attr | ArgumentAttributes.In;
            else
                attr &= ~ArgumentAttributes.In;
            if ((attrMono & ParamAttributes.Out) == ParamAttributes.Out) attr = attr | ArgumentAttributes.Out;
            if ((attrMono & ParamAttributes.Optional) == ParamAttributes.Optional) attr = attr | ArgumentAttributes.Optional;
            if ((attrMono & ParamAttributes.HasDefault) == ParamAttributes.HasDefault) attr = attr | ArgumentAttributes.HasDefault;
            if ((attrMono & ParamAttributes.HasFieldMarshal) == ParamAttributes.HasFieldMarshal) attr = attr | ArgumentAttributes.HasFieldMarshal;
 
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
                return null;

            Instruction jumpNopInstruction;
            if (!m_JumpInstructions.TryGetValue(labelId, out jumpNopInstruction))
            {
                jumpNopInstruction = Worker.Create(OpCodes.Nop);
                m_JumpInstructions.Add(labelId, jumpNopInstruction);
            }

            return jumpNopInstruction;
        }

        /// <summary>
        /// Creates the local var.
        /// </summary>
        /// <param name="type">The type.</param>
        /// <returns>Returns the variable</returns>
        private VariableDefinition CreateLocalVar(Type type)
        {
            TypeReference typeRef = CecilUtilities.CreateTypeReference(TargetAssemblyDefinition, type);
            return CreateLocalVar(typeRef);
        }

        /// <summary>
        /// Creates the local var.
        /// </summary>
        /// <param name="type">The type.</param>
        /// <returns>Returns the variable</returns>
        private VariableDefinition CreateLocalVar(TypeReference type)
        {
            VariableDefinition var = new VariableDefinition(type);
            var.Name = type.ToString();

            Method.Body.Variables.Add(var);

            return var;
        }

       

        // Because we need local vars to store the object and type of arguments in, we have to add these local vars.
        // But only once, so these functions make sure we only have one of this variables
        private VariableDefinition m_ObjectLocal;
        private VariableDefinition m_TypeLocal;
        private VariableDefinition m_JpcLocal;  
        private VariableDefinition m_ActionStoreLocal;

        /// <summary>
        /// Creates the object ordinal.
        /// </summary>
        /// <returns></returns>
        private VariableDefinition CreateObjectLocal()
        {
            if (m_ObjectLocal == null)
            {
                m_ObjectLocal = CreateLocalVar(typeof(Object));
            }

            return m_ObjectLocal;
        }

        /// <summary>
        /// Creates the action store local.
        /// </summary>
        /// <returns></returns>
        private VariableDefinition CreateActionStoreLocal()
        {
            if (m_ActionStoreLocal == null)
            {
                m_ActionStoreLocal = CreateLocalVar(typeof(FilterContext));
            }

            return m_ActionStoreLocal;
        }

        /// <summary>
        /// Creates the type ordinal.
        /// </summary>
        /// <returns></returns>
        private VariableDefinition CreateTypeLocal()
        {
            if (m_TypeLocal == null)
            {
                m_TypeLocal = CreateLocalVar(typeof(System.Type));
            }

            return m_TypeLocal;
        }

        /// <summary>
        /// Creates the join point context variable.
        /// </summary>
        internal VariableDefinition CreateJoinPointContextLocal()
        {
            if (m_JpcLocal == null)
            {
                m_JpcLocal = CreateLocalVar(typeof(JoinPointContext));
                Method.Body.InitLocals = true;
            }

            return m_JpcLocal;
        }

        

        /// <summary>
        /// Adds an instruction list to the Instructions list.
        /// </summary>
        /// <param name="instructions">The instructions.</param>
        private void AddInstructionList(IList<Instruction> instructions)
        {
            foreach (Instruction instruction in instructions)
            {
                Instructions.Add(instruction);
            }
        }
        #endregion

        #region Inlining Instructions Visitor Handlers

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
        /// Visits the return action.
        /// </summary>
        /// <param name="contextInstruction">The context instruction.</param>
        public void VisitReturnAction(ContextInstruction contextInstruction)
        {
            // Generate a return instruction
            Instructions.Add(Worker.Create(OpCodes.Ret));
        }

        

        #region Inner Call handlers

        /// <summary>
        /// Generates the filter context is inner call check.
        /// </summary>
        /// <param name="contextInstruction">The context instruction.</param>
        /// <example>
        /// Generate the following code:
        /// <code>
        /// if (!FilterContext.IsInnerCall(this, methodName))
        /// {
        /// <b>filtercode</b>
        /// }
        /// </code>
        /// The <b>filtercode</b> are the inputfilters added to the method.
        /// </example>
        /// <remarks>
        /// A call to a Label instruction is needed to place the branchToInstruction at the correct place.
        /// </remarks>
        public void VisitCheckInnerCall(ContextInstruction contextInstruction)
        {
            // Load the this parameter
            if (!Method.HasThis)
                Instructions.Add(Worker.Create(OpCodes.Ldnull));
            else
                Instructions.Add(Worker.Create(OpCodes.Ldarg, Method.This));

            // Load the methodId
            Instructions.Add(Worker.Create(OpCodes.Ldc_I4, contextInstruction.Code));

            // Call the IsInnerCall
            Instructions.Add(Worker.Create(OpCodes.Call, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.IsInnerCall)));

            // Create the call instruction
            Instruction branchToInstruction = GetJumpLabel(FilterContextJumpId);

            // Result is placed on the stack, so use it to branch to the skipFiltersInstruction
            Instructions.Add(Worker.Create(OpCodes.Brtrue, branchToInstruction));
        }

        /// <summary>
        /// Generate the SetInnerCall code.
        /// </summary>
        /// <param name="contextInstruction"></param>
        /// <example>
        /// Generate the following code:
        /// <code>        
        /// FilterContext.SetInnerCall(this, methodId);
        /// this.calledMethod();
        /// </code>       
        /// </example> 
        public void VisitSetInnerCall(ContextInstruction contextInstruction)
        {
            // Load the this parameter
            if (!Method.HasThis)
                Instructions.Add(Worker.Create(OpCodes.Ldnull));
            else
                Instructions.Add(Worker.Create(OpCodes.Ldarg, Method.This));

            // Load the methodId
            Instructions.Add(Worker.Create(OpCodes.Ldc_I4, contextInstruction.Code));

            // Call the SetInnerCall
            Instructions.Add(Worker.Create(OpCodes.Call, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.SetInnerCall)));

        }

        /// <summary>
        /// Generates the ResetInnerCall code.
        /// </summary>
        /// <param name="contextInstruction">The context instruction.</param>
        /// <example>
        /// Generate the following code:
        /// <code>        
        /// FilterContext.ResetInnerCall();        
        /// </code>
        /// A LABEL instruction has to proceed this call.
        /// </example> 
        /// <remarks>
        /// There must be a call the the IsInnerCall to generate the jump instruction.
        /// </remarks> 
        public void VisitResetInnerCall(ContextInstruction contextInstruction)
        {
            // Call the reset inner call function
            Instructions.Add(Worker.Create(OpCodes.Call, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.ResetInnerCall)));
        }

        #endregion

        

        /// <summary>
        /// Visits the filter action. Uses Strategypattern to weave a specific instruction.
        /// </summary>
        /// <param name="filterAction">The filter action.</param>
        public void VisitFilterAction(FilterAction filterAction)
        {
            FilterActionWeaveStrategy strategy = FilterActionWeaveStrategy.GetFilterActionWeaveStrategy(
                filterAction.Type);

            strategy.Weave(this, filterAction, CalledMethod);
        }

        /// <summary>
        /// Add a jump to another block
        /// </summary>
        /// <param name="jump"></param>
        public void VisitJumpInstruction(Jump jump)
        {
            Instruction jumpToInstruction = GetJumpLabel(jump.Target);
            if (jumpToInstruction == null)
                throw new ILWeaverException(Properties.Resources.FilterJumpLabelIsNotSet);

            // Add an unconditional branch instruction
            Instructions.Add(Worker.Create(OpCodes.Br, jumpToInstruction));
        }

        /// <summary>
        /// Visits the skip action. No code is needed for this action.
        /// </summary>
        /// <param name="filterAction">The filter action.</param>
        public void VisitSkipAction(FilterAction filterAction)
        {
            // No code needed
        }

        /// <summary>
        /// Visits the substitution action. No code is needed for this action.
        /// </summary>
        /// <param name="filterAction">The filter action.</param>
        public void VisitSubstitutionAction(FilterAction filterAction)
        {
            // No code needed
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
            CecilConditionsVisitor conditionsVisitor = new CecilConditionsVisitor();
            conditionsVisitor.Method = Method;
            conditionsVisitor.Worker = Worker;
            conditionsVisitor.TargetAssemblyDefinition = TargetAssemblyDefinition;
            conditionsVisitor.RepositoryAccess = m_LanguageModelAccessor;
            ((Composestar.Repository.LanguageModel.ConditionExpressions.Visitor.IVisitable)branch.ConditionExpression).Accept(conditionsVisitor);

            // Add the instructions containing the conditions to the IL instruction list
            AddInstructionList(conditionsVisitor.Instructions);

            // Add branch code
            branch.Label = BranchLabelOffSet + m_NumberOfBranches;   // TODO check the correctness of this constructions (Michiel)
            m_NumberOfBranches = m_NumberOfBranches + 2;
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

        #region While construction

        /// <summary>
        /// While loop visitor.
        /// </summary>
        /// <remarks>
        /// Should generate the following code:
        /// <code>
        /// l2:
        /// condition
        /// brfalse l1:
        /// // code
        /// br l2:
        /// l1:
        /// </code>
        /// </remarks> 
        /// <param name="whileInstr"></param>
        public void VisitWhile(While whileInstr)
        {
            // Create a start label
            whileInstr.Label = BranchLabelOffSet + m_NumberOfBranches;
            m_NumberOfBranches = m_NumberOfBranches + 2;
            Instructions.Add(GetJumpLabel(whileInstr.Label));

            // Context instruction
            CreateContextExpression(whileInstr.Expression);

            // Add branch code
            Instructions.Add(Worker.Create(OpCodes.Brfalse, GetJumpLabel(whileInstr.Label + 1)));
        }

        /// <summary>
        /// Visits the while end.
        /// </summary>
        /// <remarks>
        /// <code>
        /// while(condition)
        /// {
        ///   // Block
        /// }
        /// </code>
        /// In IL we need:
        /// <code>
        /// l2: condition
        /// brfalse l1
        /// block
        /// br l2
        /// l1: nop
        /// </code>
        /// </remarks> 
        /// <param name="whileInstr">The while instruction.</param>
        public void VisitWhileEnd(While whileInstr)
        {
            // Add the branch back to condition part
            Instructions.Add(Worker.Create(OpCodes.Br, GetJumpLabel(whileInstr.Label)));

            // Place the end label
            Instructions.Add(GetJumpLabel(whileInstr.Label + 1));

            // Reset label
            whileInstr.Label = -1;
        }

        #endregion

        #region Switch construction

        /// <summary>
        /// 
        /// </summary>
        /// <param name="switchInstr"></param>
        public void VisitSwitch(Switch switchInstr)
        {

            // Context instruction
            CreateContextExpression(switchInstr.Expression);

            // The labels to jump to
            List<Instruction> caseLabels = new List<Instruction>();
            foreach (Case caseElement in switchInstr.Cases)
            {
                caseLabels.Add(GetJumpLabel(caseElement.CheckConstant + 10000));
            }

            // The switch statement
            Instructions.Add(Worker.Create(OpCodes.Switch, caseLabels.ToArray()));

            // Jump to the end
            switchInstr.Label = BranchLabelOffSet + m_NumberOfBranches;   // TODO check the correctness of this constructions (Michiel)
            m_NumberOfBranches = m_NumberOfBranches + 1;
            Instructions.Add(Worker.Create(OpCodes.Br, GetJumpLabel(switchInstr.Label)));
        }

        /// <summary>
        /// Visits the switch end.
        /// </summary>
        /// <param name="switchInstr">The switch instr.</param>
        public void VisitSwitchEnd(Switch switchInstr)
        {
            // Emit a label to jump to.
            Instructions.Add(GetJumpLabel(switchInstr.Label));

            // Reset label
            switchInstr.Label = -1;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="caseInstr"></param>
        public void VisitCase(Case caseInstr)
        {
            // Add the label
            Instructions.Add(GetJumpLabel(caseInstr.CheckConstant + 10000));
        }

        /// <summary>
        /// Visits the case end.
        /// </summary>
        /// <param name="switchInstr">The switch instr.</param>
        public void VisitCaseEnd(Switch switchInstr)
        {
            // Add a jump to the end of the switch
            Instructions.Add(Worker.Create(OpCodes.Br, GetJumpLabel(switchInstr.Label)));
        }

        #endregion

        #region Action store

        /// <summary>
        /// This ContextInstruction indicates that an actionstore needs to be created. 
        /// We use the FilterContext object as the actionstore, so when this contextinstruction is encountered, 
        /// a new instance of the FilterContext object needs to be created:
        /// <code>
        /// FilterContext actionStore = new FilterContext();
        /// </code> 
        /// </summary>
        /// <remarks>
        /// This ContextInstruction only occurs when there might actually be an action that needs to be stored. 
        /// When no action will ever be stored in the filtercode, this ContextInstruction is not present.
        /// </remarks> 
        /// <param name="contextInstruction">The context instruction.</param>
        public void VisitCreateActionStore(ContextInstruction contextInstruction)
        {
            // Get an actionstore local
            VariableDefinition asVar = CreateActionStoreLocal();

            //
            // Create new FilterContext object
            //

            // Call the constructor
            Instructions.Add(Worker.Create(OpCodes.Newobj, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.FilterContextConstructor)));

            // Store the local
            Instructions.Add(Worker.Create(OpCodes.Stloc, asVar));
        }

        /// <summary>
        /// This ContextInstruction indicates that an action needs to be stored. 
        /// The action that needs to be stored is represented by an integer id, 
        /// so actually only this id needs to be stored. 
        /// This id is present in the code-field of ContextInstruction. 
        /// So the code that needs to be created for this instructions is:
        /// <code>
        /// actionStore.storeAction( contextinstruction.Code );
        /// </code>
        /// </summary>
        /// <param name="contextInstruction">The context instruction.</param>
        public void VisitStoreAction(ContextInstruction contextInstruction)
        {
            // Get an actionstore local
            VariableDefinition asVar = CreateActionStoreLocal();

            // Load the local
            Instructions.Add(Worker.Create(OpCodes.Ldloc, asVar));

            // Load the id onto the stack
            Instructions.Add(Worker.Create(OpCodes.Ldc_I4, contextInstruction.Code));

            // Call the StoreAction method
            Instructions.Add(Worker.Create(OpCodes.Callvirt, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.StoreAction)));

        }


        #endregion

        #region JoinPointContext

        /// <summary>
        /// This method creates and initializes the JoinPointContext. This always happens at the beginning
        /// of the filtercode. All FilterActions should use the JoinPointContext to access parameter values and
        /// return-value, and not use for example the LdParam opcode, because this value might not be up to date
        /// or might be entirely wrong, because the action is in an outputfilter.
        /// </summary>
        /// <param name="contextInstruction"></param>
        public void VisitCreateJoinPointContext(ContextInstruction contextInstruction)
        {
            // Create a new or use an existing local variable for the JoinPointContext
            VariableDefinition jpcVar = CreateJoinPointContextLocal();

            //
            // Create new joinpointcontext object
            //
            Instructions.Add(Worker.Create(OpCodes.Newobj, 
                CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.JoinPointContextConstructor )));

            // Store the just created joinpointcontext object
            Instructions.Add(Worker.Create(OpCodes.Stloc, jpcVar));


            //
            // Store sender (only for outputfilters)
            //
            if(FilterType == FilterTypes.OutputFilter  &&  Method.HasThis)
            {
                // Load joinpointcontext object
                Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

                // Load this
                Instructions.Add(Worker.Create(OpCodes.Ldarg, Method.This));

                // Call set_Sender
                Instructions.Add(Worker.Create(OpCodes.Callvirt, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.JoinPointContextSetSender)));
            }

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
                        
            //
            // Add the arguments, these are stored at the top of the stack
            //
            switch (FilterType)
            {
                case FilterTypes.None:
                    break;
                case FilterTypes.InputFilter:
                    foreach (ParameterDefinition param in CalledMethod.Parameters)
                    {
                        // Load the argument
                        Instructions.Add(Worker.Create(OpCodes.Ldarg, param));

                        // Check if parameter is value type, then box
                        if (param.ParameterType.IsValueType)
                        {
                            Instructions.Add(Worker.Create(OpCodes.Box, param.ParameterType));
                        }
                       
                        // Load the ordinal
                        Instructions.Add(Worker.Create(OpCodes.Ldc_I4, param.Sequence));

                        // Determine type
                        Instructions.Add(Worker.Create(OpCodes.Ldtoken, param.ParameterType));
                        Instructions.Add(Worker.Create(OpCodes.Call,CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.GetTypeFromHandle )));                       

                        // Determine the parameter direction
                        ArgumentAttributes attr = ConvertAttributes(param.Attributes);
                    
                        Instructions.Add(Worker.Create(OpCodes.Ldc_I4, (int)attr));
               
                        // Load jpc
                        Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

                        // Call the AddArgument function statically
                        Instructions.Add(Worker.Create(OpCodes.Call, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.JoinPointContextAddArgument)));                            

                    }
                    break;
                case FilterTypes.OutputFilter:
                    ParameterDefinitionCollection parameters = CalledMethod.Parameters;
                    int count = parameters.Count;

                    // iterate backward over parameters, because last parameter is on top of the stack
                    for (int i = count - 1; i >= 0; i--)
                    {
                        ParameterDefinition param = parameters[i];

                        // Check if parameter is value type, then box
                        if (param.ParameterType.IsValueType)
                        {
                            Instructions.Add(Worker.Create(OpCodes.Box, param.ParameterType));
                        }

                        // Load the ordinal
                        Instructions.Add(Worker.Create(OpCodes.Ldc_I4, param.Sequence));

                        // Determine type
                        Instructions.Add(Worker.Create(OpCodes.Ldtoken, param.ParameterType));
                        Instructions.Add(Worker.Create(OpCodes.Call,CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.GetTypeFromHandle )));

                        // Determine the parameter direction
                        ArgumentAttributes attr = ConvertAttributes(param.Attributes); 

                        Instructions.Add(Worker.Create(OpCodes.Ldc_I4, (int)attr));
             
                        // Load jpc
                        Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

                        // Call the AddArgument function statically
                        Instructions.Add(Worker.Create(OpCodes.Call,CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.JoinPointContextAddArgument)));
                    }
                    break;
                default:
                    break;
            }
           
            //
            // Set the target
            //

            if(FilterType == FilterTypes.InputFilter)
            {
                // Load the joinpointcontext object
                Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

                // Load the this pointer
                if(Method.HasThis)
                    Instructions.Add(Worker.Create(OpCodes.Ldarg, Method.This));
                else
                    Instructions.Add(Worker.Create(OpCodes.Ldnull));

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
            // Set the selector
            //

            // Load joinpointcontext first
            Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

            // Load the name onto the stack
            Instructions.Add(Worker.Create(OpCodes.Ldstr, CalledMethod.Name));

            // Assign name to MethodName
            Instructions.Add(Worker.Create(OpCodes.Callvirt, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.JoinPointContextSetStartSelector)));
                
        }

        /// <summary>
        /// Restores the JoinPointContext at the end of the filtercode. It puts for example the returnvalue on the
        /// stack.
        /// </summary>
        /// <param name="contextInstruction"></param>
        public void VisitRestoreJoinPointContext(ContextInstruction contextInstruction)
        {
            // Retrieve returnvalue
            if(!CalledMethod.ReturnType.ReturnType.FullName.Equals(CecilUtilities.VoidType))
            {
                // Get JoinPointContext
                VariableDefinition jpcVar = CreateJoinPointContextLocal();

                // Load JoinPointContext
                Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

                // Get returnvalue
                Instructions.Add(Worker.Create(OpCodes.Callvirt, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.JoinPointContextGetReturnValue)));
                           

                // Check if returnvalue is value type, then unbox, else cast
                if(CalledMethod.ReturnType.ReturnType.IsValueType)
                {
                    Instructions.Add(Worker.Create(OpCodes.Unbox_Any, CalledMethod.ReturnType.ReturnType));
                }
                else
                {
                    Instructions.Add(Worker.Create(OpCodes.Castclass, CalledMethod.ReturnType.ReturnType));
                }
            }
        }

        #endregion

        #endregion

    }
}
