using System;
using System.Collections.Generic;
using System.Globalization;
using System.Reflection;
using System.Text;

using Composestar.StarLight.Entities.Concerns;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities.Configuration; 
using Composestar.StarLight.Entities.WeaveSpec;
using Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions;
using Composestar.StarLight.Entities.WeaveSpec.Instructions;
using Composestar.StarLight.Entities.WeaveSpec.Instructions.Visitor;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions;

using Mono.Cecil;
using Mono.Cecil.Cil;

using Composestar.StarLight.Utilities;
using Composestar.StarLight.Utilities.Interfaces;
using Composestar.StarLight.Weaving.Strategies;
using Composestar.StarLight.ILWeaver.WeaveStrategy;

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

        private const int FilterContextJumpId = 9999;
        private const int BranchLabelOffSet = 100000;
        
        #endregion

        #region Private variables
        
        private IList<Instruction> _instructions = new List<Instruction>();
        private CilWorker _worker;
        private int _numberOfBranches;
        private MethodDefinition _method;
        private MethodDefinition _calledMethod;
        private AssemblyDefinition _targetAssemblyDefinition;
        private FilterType _filterType;
        private Dictionary<int, Instruction> _jumpInstructions = new Dictionary<int, Instruction>();
        private IEntitiesAccessor _entitiesAccessor;
        private ConfigurationContainer _weaveConfiguration;
        private WeaveType _weaveType;
        
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
        /// Gets or sets the entities accessor.
        /// </summary>
        /// <value>The entities accessor.</value>
        public IEntitiesAccessor EntitiesAccessor
        {
            get
            {
                return _entitiesAccessor;
            }
            set
            {
                _entitiesAccessor = value;
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
            } // get
            set
            {
                _weaveConfiguration = value;
            } // set
        } // WeaveConfiguration

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
        /// Creates the context expression.
        /// </summary>
        /// <remarks>
        /// The ActionStore object has to be used before (as in created by the CreateActionStore visitor).
        /// </remarks> 
        /// <param name="expr">The expr.</param>
        private void CreateContextExpression(ContextExpression expr)
        {           

            // Get an actionstore local
            VariableDefinition asVar = CreateActionStoreLocal();

            // Load the local
            Instructions.Add(Worker.Create(OpCodes.Ldloc, asVar));

            switch (expr)
            {
                case ContextExpression.HasMoreActions:
                    // Call the HasMoreStoredActions method
                    Instructions.Add(Worker.Create(OpCodes.Callvirt, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.HasMoreStoredActions)));
                    break;
                case ContextExpression.RetrieveAction:
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
                return null;

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

       

        // Because we need local vars to store the object and type of arguments in, we have to add these local vars.
        // But only once, so these functions make sure we only have one of this variables
        private VariableDefinition m_JpcLocal;  
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
            if (filterAction == null)
                throw new ArgumentNullException("filterAction"); 

            FilterActionWeaveStrategy strategy = FilterActionStrategyDispatcher.GetFilterActionWeaveStrategy(filterAction.Type);

            strategy.Weave(this, filterAction, CalledMethod);
        }

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
        public void VisitWhile(WhileInstruction whileInstruction)
        {
            // Create a start label
            whileInstruction.Label = BranchLabelOffSet + _numberOfBranches;
            _numberOfBranches = _numberOfBranches + 2;
            Instructions.Add(GetJumpLabel(whileInstruction.Label));

            // Context instruction
            CreateContextExpression(whileInstruction.Expression);

            // Add branch code
            Instructions.Add(Worker.Create(OpCodes.Brfalse, GetJumpLabel(whileInstruction.Label + 1)));
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
        public void VisitWhileEnd(WhileInstruction whileInstruction)
        {
            // Add the branch back to condition part
            Instructions.Add(Worker.Create(OpCodes.Br, GetJumpLabel(whileInstruction.Label)));

            // Place the end label
            Instructions.Add(GetJumpLabel(whileInstruction.Label + 1));

            // Reset label
            whileInstruction.Label = -1;
        }

        #endregion

        #region Switch construction

        /// <summary>
        /// 
        /// </summary>
        /// <param name="switchInstr"></param>
        public void VisitSwitch(SwitchInstruction switchInstruction)
        {

            // Context instruction
            CreateContextExpression(switchInstruction.Expression);

            // The labels to jump to
            List<Instruction> caseLabels = new List<Instruction>();
            foreach (CaseInstruction caseElement in switchInstruction.Cases)
            {
                caseLabels.Add(GetJumpLabel(caseElement.CheckConstant + 10000));
            }

            // The switch statement
            Instructions.Add(Worker.Create(OpCodes.Switch, caseLabels.ToArray()));

            // Jump to the end
            switchInstruction.Label = BranchLabelOffSet + _numberOfBranches;   // TODO check the correctness of this constructions (Michiel)
            _numberOfBranches = _numberOfBranches + 1;
            Instructions.Add(Worker.Create(OpCodes.Br, GetJumpLabel(switchInstruction.Label)));
        }

        /// <summary>
        /// Visits the switch end.
        /// </summary>
        /// <param name="switchInstr">The switch instr.</param>
        public void VisitSwitchEnd(SwitchInstruction switchInstruction)
        {
            // Emit a label to jump to.
            Instructions.Add(GetJumpLabel(switchInstruction.Label));

            // Reset label
            switchInstruction.Label = -1;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="caseInstr"></param>
        public void VisitCase(CaseInstruction caseInstruction)
        {
            // Add the label
            Instructions.Add(GetJumpLabel(caseInstruction.CheckConstant + 10000));
        }

        /// <summary>
        /// Visits the case end.
        /// </summary>
        /// <param name="switchInstr">The switch instr.</param>
        public void VisitCaseEnd(SwitchInstruction switchInstruction)
        {
            // Add a jump to the end of the switch
            Instructions.Add(Worker.Create(OpCodes.Br, GetJumpLabel(switchInstruction.Label)));
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
            if(FilterType == FilterType.OutputFilter  &&  Method.HasThis)
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
                case FilterType.None:
                    break;
                case FilterType.InputFilter:
                    foreach(ParameterDefinition param in CalledMethod.Parameters)
                    {
                        if((param.Attributes & Mono.Cecil.ParameterAttributes.Out) != Mono.Cecil.ParameterAttributes.Out)
                        {
                            // Load the argument
                            Instructions.Add(Worker.Create(OpCodes.Ldarg, param));

                            // Check for reference parameter
                            if(param.ParameterType.FullName.EndsWith("&"))
                            {
                                Instructions.Add(Worker.Create(OpCodes.Ldobj, param.ParameterType));
                            }

                            // Check if parameter is value type, then box
                            if(param.ParameterType.IsValueType)
                            {
                                Instructions.Add(Worker.Create(OpCodes.Box, param.ParameterType));
                            }
                        }
                        else
                        {
                            // Load null if parameter is out
                            Instructions.Add(Worker.Create(OpCodes.Ldnull));
                        }

                        int ordinal = param.Sequence - (CalledMethod.HasThis ? 1 : 0);

                        // Load the ordinal
                        Instructions.Add(Worker.Create(OpCodes.Ldc_I4, ordinal));

                        //// Determine type
                        //Instructions.Add(Worker.Create(OpCodes.Ldtoken, param.ParameterType));
                        //Instructions.Add(Worker.Create(OpCodes.Call, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.GetTypeFromHandle)));

                        // Determine the parameter direction
                        ArgumentAttributes attr = ConvertAttributes(param.Attributes);

                        Instructions.Add(Worker.Create(OpCodes.Ldc_I4, (int) attr));

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

                        int ordinal = param.Sequence - (CalledMethod.HasThis ? 1 : 0);

                        // Check for reference parameter
                        if(param.ParameterType.FullName.EndsWith("&"))
                        {
                            Instructions.Add(Worker.Create(OpCodes.Ldobj, param.ParameterType));
                        }

                        // Check if parameter is value type, then box
                        if (param.ParameterType.IsValueType)
                        {
                            Instructions.Add(Worker.Create(OpCodes.Box, param.ParameterType));
                        }

                        // Load the ordinal
                        Instructions.Add(Worker.Create(OpCodes.Ldc_I4, ordinal));

                        //// Determine type
                        //Instructions.Add(Worker.Create(OpCodes.Ldtoken, param.ParameterType));
                        //Instructions.Add(Worker.Create(OpCodes.Call,CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.GetTypeFromHandle )));

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

            if(FilterType == FilterType.InputFilter)
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
            // Get JoinPointContext
            VariableDefinition jpcVar = CreateJoinPointContextLocal();

            //
            // Restore out/ref parameters
            //
            switch(FilterType)
            {
                case FilterType.None:
                    break;
                case FilterType.InputFilter:
                    foreach(ParameterDefinition param in CalledMethod.Parameters)
                    {
                        if(param.ParameterType.FullName.EndsWith("&"))
                        {
                            // Load the argument
                            Instructions.Add(Worker.Create(OpCodes.Ldarg, param));

                            //
                            // Load the value
                            //

                            // Load jpc
                            Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

                            // Load the ordinal
                            Instructions.Add(Worker.Create(OpCodes.Ldc_I4, param.Sequence));

                            // Call the GetArgumentValue function
                            Instructions.Add(Worker.Create(OpCodes.Call, CecilUtilities.CreateMethodReference(TargetAssemblyDefinition, CachedMethodDefinition.JoinPointContextGetArgumentValue)));

                            // Check if returnvalue is value type, then unbox, else cast
                            if(param.ParameterType.IsValueType)
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
            if(!CalledMethod.ReturnType.ReturnType.FullName.Equals(CecilUtilities.VoidType))
            {
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
