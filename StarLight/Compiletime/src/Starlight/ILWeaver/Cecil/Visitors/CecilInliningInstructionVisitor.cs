using System;
using System.Collections.Generic;
using System.Reflection;
using System.Text;

using Composestar.Repository.LanguageModel.Inlining;
using Composestar.Repository.LanguageModel.Inlining.Visitor;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.CoreServices;

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

        #endregion

        #region Private variables
        IList<Instruction> _instructions = new List<Instruction>();
        CilWorker _worker; 
        int numberOfBranches = 0;
        MethodDefinition _method;
        AssemblyDefinition _targetAssemblyDefinition;
        FilterTypes _filterType;
        Dictionary<int, Instruction> _jumpInstructions = new Dictionary<int, Instruction>();
        ILanguageModelAccessor _languageModelAccessor;
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
                return _filterType;
            }
            set
            {
                _filterType = value;
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
                return _languageModelAccessor;
            }
            set
            {
                _languageModelAccessor = value;
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
        /// Gets or sets the method.
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
            if (expr == null)
                return;

            // Get an actionstore local
            VariableDefinition asVar = CreateActionStoreLocal();
       
            // Load the local
            Instructions.Add(Worker.Create(OpCodes.Ldloc, asVar));

            switch(expr.Type)
            {
                case ContextExpression.HAS_MORE_ACTIONS:
                    // Call the HasMoreStoredActions method
                    Instructions.Add(Worker.Create(OpCodes.Callvirt, CreateMethodReference(typeof(FilterContext).GetMethod("HasMoreStoredActions", new Type[] { }))));
                    break;
                case ContextExpression.RETRIEVE_ACTION:
                    // Call the NextStoredAction method
                    Instructions.Add(Worker.Create(OpCodes.Callvirt, CreateMethodReference(typeof(FilterContext).GetMethod("NextStoredAction", new Type[] { }))));
                    break;

            }
            
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
        /// Creates the local var.
        /// </summary>
        /// <param name="type">The type.</param>
        /// <returns>Returns the variable</returns>
        private VariableDefinition CreateLocalVar(Type type)
        {
            TypeReference typeRef = _targetAssemblyDefinition.MainModule.Import(type);
            VariableDefinition var = new VariableDefinition(typeRef);
            var.Name = type.ToString(); 
            
            Method.Body.Variables.Add(var);

            return var;
        }

        /// <summary>
        /// Creates the method reference.
        /// </summary>
        /// <param name="methodBase">The method info.</param>
        /// <returns></returns>
        private MethodReference CreateMethodReference(MethodBase methodBase)
        {
            return TargetAssemblyDefinition.MainModule.Import(methodBase);
        }

        // Because we need local vars to store the object and type of arguments in, we have to add these local vars.
        // But only once, so these functions make sure we only have one of this variables
        private VariableDefinition _objectLocal;
        private VariableDefinition _typeLocal;
        private VariableDefinition _jpcLocal;
        private VariableDefinition _actionStoreLocal;

        /// <summary>
        /// Creates the object ordinal.
        /// </summary>
        /// <returns></returns>
        private VariableDefinition CreateObjectLocal()
        {
            if (_objectLocal == null)
            {
                _objectLocal = CreateLocalVar(typeof(Object));
            }

            return _objectLocal;
        }

        /// <summary>
        /// Creates the action store local.
        /// </summary>
        /// <returns></returns>
        private VariableDefinition CreateActionStoreLocal()
        {
            if (_actionStoreLocal == null)
            {
                _actionStoreLocal = CreateLocalVar(typeof(FilterContext));
            }

            return _actionStoreLocal;
        }

        /// <summary>
        /// Creates the type ordinal.
        /// </summary>
        /// <returns></returns>
        private VariableDefinition CreateTypeLocal()
        {
            if (_typeLocal == null)
            {
                _typeLocal = CreateLocalVar(typeof(System.Type));
            }

            return _typeLocal;
        }

        /// <summary>
        /// Creates the join point context variable.
        /// </summary>
        private VariableDefinition CreateJoinPointContextLocal()
        {
            if (_jpcLocal == null)
            {
                _jpcLocal = CreateLocalVar(typeof(JoinPointContext));
            }

            return _jpcLocal;
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
                Instructions.Add(GetJumpLabel(inlineInstruction.Label));
        }

        public void VisitAfterAction(FilterAction filterAction)
        {
            // TODO Copy beforeAction and adapt
        }

        /// <summary>
        /// BeforeFilter implementation
        /// </summary>
        /// <param name="filterAction"></param>
        public void VisitBeforeAction(FilterAction filterAction)
        {

            // Create a new or use an existing local variable for the JoinPointContext
            VariableDefinition jpcVar = CreateJoinPointContextLocal();

            //
            // Create new joinpointcontext object
            //
            Instructions.Add(Worker.Create(OpCodes.Newobj, CreateMethodReference(typeof(JoinPointContext).GetConstructors()[0])));

            // Store the just created joinpointcontext object
            Instructions.Add(Worker.Create(OpCodes.Stloc, jpcVar));

            //
            // Set the target
            //
            // Load the joinpointcontext object
            Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));        
      
            // Load the this pointer
            if (Method.HasThis)
                Instructions.Add(Worker.Create(OpCodes.Ldarg, Method.This));
            else
                Instructions.Add(Worker.Create(OpCodes.Ldnull));

            // Assign to the Target property
            Instructions.Add(Worker.Create(OpCodes.Callvirt, CreateMethodReference(typeof(JoinPointContext).GetMethod("set_Target", new Type[] { typeof(object) }))));
  
            int numberOfArguments=0;
            
            // Get the method we have to call
            MethodBase methodBaseDef = CecilUtilities.ResolveMethod(filterAction.Target);
            if (methodBaseDef == null)
            {
                throw new ILWeaverException(String.Format(Properties.Resources.CouldNotResolveMethod, filterAction.Target));
            }
            MethodReference methodToCall = CreateMethodReference(methodBaseDef);

            switch (FilterType)
            {
                case FilterTypes.InputFilter:
                    numberOfArguments = Method.Parameters.Count;
                    break;
                case FilterTypes.OutputFilter:
                    numberOfArguments = methodBaseDef.GetParameters().Length;
                    // Also set the sender
                    // Load the joinpointcontext object
                    Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

                    // Load the this pointer
                    if (Method.HasThis)
                        Instructions.Add(Worker.Create(OpCodes.Ldarg, Method.This));
                    else
                        Instructions.Add(Worker.Create(OpCodes.Ldnull));

                    // Assign to the Target property
                    Instructions.Add(Worker.Create(OpCodes.Callvirt, CreateMethodReference(typeof(JoinPointContext).GetMethod("set_Sender", new Type[] { typeof(object) }))));

                    break;
            }

            //
            // Add the arguments, these are stored at the top of the stack
            //

            if (numberOfArguments > 0)
            {
                // We have to use temporary local varibales, because we cannot swap the elements on the stack
                // and we have to place the pointer to the jpc on the stack also
                // Create the local vars, but only once in this method.
                VariableDefinition objectVar = CreateObjectLocal();
                VariableDefinition typeVar = CreateTypeLocal();

                for (int i = 1; i < numberOfArguments; i++) 
                {
                    // Duplicate the value
                    Instructions.Add(Worker.Create(OpCodes.Dup));

                    // Determine type
                    Instructions.Add(Worker.Create(OpCodes.Callvirt, CreateMethodReference(typeof(System.Type).GetMethod("GetType", new Type[] { }))));

                    // Save the type
                    Instructions.Add(Worker.Create(OpCodes.Stloc, typeVar));

                    // Save the object
                    Instructions.Add(Worker.Create(OpCodes.Stloc, objectVar));

                    // Perpare to call AddArgument by loading the parameters onto the stack
                    // Load jpc
                    Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

                    // Load the ordinal
                    Instructions.Add(Worker.Create(OpCodes.Ldc_I4, i));

                    // Load the type
                    Instructions.Add(Worker.Create(OpCodes.Ldloc, typeVar));

                    // Load the object
                    Instructions.Add(Worker.Create(OpCodes.Ldloc, objectVar));

                    // Call the AddArgument function
                    Instructions.Add(Worker.Create(OpCodes.Callvirt, CreateMethodReference(typeof(JoinPointContext).GetMethod("AddArgument", new Type[] { typeof(Int16), typeof(System.Type), typeof(object) }))));
                }
            }

            //
            // Set the selector
            //

            // Load joinpointcontext first
            Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

            // Load the name onto the stack
            Instructions.Add(Worker.Create(OpCodes.Ldstr, filterAction.Selector));

            // Assign name to MethodName
            Instructions.Add(Worker.Create(OpCodes.Callvirt, CreateMethodReference(typeof(JoinPointContext).GetMethod("set_MethodName", new Type[] { typeof(string) }))));

            //
            // Call the target
            //

            // Load the JoinPointObject as the parameter
            Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

            // Call the Target
            Instructions.Add(Worker.Create(OpCodes.Call, methodToCall));

            //
            // Retrieve the arguments
            //               
            for (int i = 0; i < numberOfArguments; i++) 
            {
                // Load jpc
                Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

                // Load the ordinal
                Instructions.Add(Worker.Create(OpCodes.Ldc_I4, i));

                // Call the GetArgumentValue(int16) function
                Instructions.Add(Worker.Create(OpCodes.Callvirt, CreateMethodReference(typeof(JoinPointContext).GetMethod("GetArgumentValue", new Type[] { typeof(Int16) }))));
            }

            // Add nop to enable debugging
            Instructions.Add(Worker.Create(OpCodes.Nop));
        }

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
        /// l1:
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
            conditionsVisitor.RepositoryAccess = _languageModelAccessor;
            ((Composestar.Repository.LanguageModel.ConditionExpressions.Visitor.IVisitable)branch.ConditionExpression).Accept(conditionsVisitor);

            // Add the instructions containing the conditions to the IL instruction list
            AddInstructionList(conditionsVisitor.Instructions);

            // Add branch code
            branch.Label = 8000 + numberOfBranches;   // TODO check the correctness of this constructions (Michiel)
            numberOfBranches++;
            Instructions.Add(Worker.Create(OpCodes.Brfalse, GetJumpLabel(branch.Label)));
        }

        /// <summary>
        /// Generate the branching code for the conditions.
        /// </summary>
        /// <param name="branch">The branch.</param>
        public void VisitBranchFalse(Branch branch)
        {
            Instructions.Add(GetJumpLabel(branch.Label));
        }

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
            Instructions.Add(Worker.Create(OpCodes.Call, CreateMethodReference(typeof(FilterContext).GetMethod("IsInnerCall", new Type[] { typeof(object), typeof(int) }))));

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
            Instructions.Add(Worker.Create(OpCodes.Call, CreateMethodReference(typeof(FilterContext).GetMethod("SetInnerCall", new Type[] { typeof(object), typeof(int) }))));

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
            Instructions.Add(Worker.Create(OpCodes.Call, CreateMethodReference(typeof(FilterContext).GetMethod("ResetInnerCall", new Type[] { }))));
        }

        /// <summary>
        /// Visits the continue action. No code is generated for this action because further jumps are introduced.
        /// </summary>
        /// <param name="filterAction">The filter action.</param>
        public void VisitContinueAction(FilterAction filterAction)
        {
            // No code needed
        }

        /// <summary>
        /// Visits the dispatch action.
        /// </summary>
        /// <param name="filterAction">The filter action.</param>
        public void VisitDispatchAction(FilterAction filterAction)
        {
            //
            // Call the same method
            //
            switch (FilterType)
            {
                case FilterTypes.InputFilter:
                    // Self call
                    
                    // Get the methodReference
                    MethodReference methodReference = (MethodReference)Method;

                    // Place the arguments on the stack first
                    if (methodReference.HasThis)
                        Instructions.Add(Worker.Create(OpCodes.Ldarg, Method.This));

                    int numberOfArguments = Method.Parameters.Count;
                    for (int i = 1; i < numberOfArguments; i++)
                    {
                        Instructions.Add(Worker.Create(OpCodes.Ldarg, Method.Parameters[i].Sequence));
                    }

                    // Call the same method
                    Instructions.Add(Worker.Create(OpCodes.Call, methodReference));

                    break;
                case FilterTypes.OutputFilter:

                    break;
            }
        }

        /// <summary>
        /// Visits the error action.
        /// </summary>
        /// <param name="filterAction">The filter action.</param>
        /// <remarks>
        /// Generate exception throw
        /// </remarks> 
        /// <example>
        /// The following construction should be created:
        /// <code>
        /// throw new Exception();
        /// </code>
        /// or in IL code:
        /// <code>
        ///   newobj instance void [mscorlib]System.Exception::.ctor()
        ///   throw 
        /// </code>
        /// </example> 
        public void VisitErrorAction(FilterAction filterAction)
        {
            // Create an exception
            Instructions.Add(Worker.Create(OpCodes.Newobj, CreateMethodReference(typeof(Exception).GetConstructors()[0])));

            // Throw the exception
            Instructions.Add(Worker.Create(OpCodes.Throw));

            // Add the Nop to enable debugging
            Instructions.Add(Worker.Create(OpCodes.Nop));

        }

        /// <summary>
        /// Visits the filter action. At this moment not used. 
        /// </summary>
        /// <remarks>Can be removed since specialized actions are visited.</remarks> 
        /// <param name="filterAction">The filter action.</param>
        public void VisitFilterAction(FilterAction filterAction)
        {
            // Currently not used (not visited)
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
            foreach (Case c in switchInstr.Cases)
            {
                caseLabels.Add(GetJumpLabel(c.Label));
            }
              
            // The switch statement
            Instructions.Add(Worker.Create(OpCodes.Switch, caseLabels.ToArray()));
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="whileInstr"></param>
        public void VisitWhile(While whileInstr)
        {

            // TODO process contextexpressions

            // Add branch code
            whileInstr.Label = 8000 + numberOfBranches;
            numberOfBranches++;
            Instructions.Add(Worker.Create(OpCodes.Brfalse, GetJumpLabel(whileInstr.Label)));
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
        /// l1: condition
        /// brfalse l2
        /// block
        /// br l1
        /// l2: nop
        /// </code>
        /// </remarks> 
        /// <param name="whileInstr">The while instruction.</param>
        public void VisitWhileEnd(While whileInstr)
        {
            // TODO not yet correct. must add a branch back
            Instructions.Add(GetJumpLabel(whileInstr.Label));
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="caseInstr"></param>
        public void VisitCase(Case caseInstr)
        {
            // TODO visit case construction
        }

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
            Instructions.Add(Worker.Create(OpCodes.Newobj, CreateMethodReference(typeof(FilterContext).GetConstructors()[0])));
            
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

            // Load the id onto the stack
            Instructions.Add(Worker.Create(OpCodes.Ldc_I4, contextInstruction.Code));

            // Load the local
            Instructions.Add(Worker.Create(OpCodes.Ldloc, asVar));

            // Call the StoreAction method
            Instructions.Add(Worker.Create(OpCodes.Callvirt, CreateMethodReference(typeof(FilterContext).GetMethod("StoreAction", new Type[] { typeof(int) }))));

        }

        #endregion
    
    }
}
