using System;
using System.Collections.Generic;
using System.Globalization;
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
        private const int BranchLabelOffSet = 8000;
        private Type[] JPCTypes = new Type[1] { typeof(JoinPointContext) };
        private const string VoidType = "System.Void";

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

            switch (expr.Type)
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
        private VariableDefinition _returnValueLocal;
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

        private VariableDefinition CreateReturnValueLocal(TypeReference reference)
        {
            if (_returnValueLocal == null)
            {
                _returnValueLocal = CreateLocalVar(reference);
            }

            return _returnValueLocal;
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
            // Load returnvalue on the stack
            if (!Method.ReturnType.ReturnType.FullName.Equals(VoidType))
            {
                VariableDefinition returnValueVar = CreateReturnValueLocal(Method.ReturnType.ReturnType);
                Instructions.Add(Worker.Create(OpCodes.Ldloc, returnValueVar));
            }

            // Generate a return instruction
            Instructions.Add(Worker.Create(OpCodes.Ret));
        }

        public void VisitAfterAction(FilterAction filterAction)
        {
            MethodReference methodToCall;

            // Get the methodReference
            MethodReference methodReference = (MethodReference)Method;
            TypeDefinition parentType = (TypeDefinition)methodReference.DeclaringType;

            // Get method to call
            methodToCall = GetMethodToCall(filterAction, parentType);
            if (methodToCall == null)
            {
                throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
                        Properties.Resources.AdviceMethodNotFound, filterAction.Selector, filterAction.Target));
            }


            // Create JoinPointContext
            VariableDefinition jpcVar = CreateJoinPointContext(methodReference, true);

            // Do the advice-call
            CallAdvice(filterAction, parentType, methodToCall, jpcVar);

            // Restore the JoinPointContext:
            RestoreJoinPointContext(methodReference, jpcVar, true);


            // Add nop to enable debugging
            Instructions.Add(Worker.Create(OpCodes.Nop));
        }

        /// <summary>
        /// BeforeFilter implementation
        /// </summary>
        /// <param name="filterAction"></param>
        public void VisitBeforeAction(FilterAction filterAction)
        {
            MethodReference methodToCall;

            // Get the methodReference
            MethodReference methodReference = (MethodReference)Method;
            TypeDefinition parentType = (TypeDefinition)methodReference.DeclaringType;

            // Get method to call
            methodToCall = GetMethodToCall(filterAction, parentType);
            if (methodToCall == null)
            {
                throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
                        Properties.Resources.AdviceMethodNotFound, filterAction.Selector, filterAction.Target));
            }


            // Create JoinPointContext
            VariableDefinition jpcVar = CreateJoinPointContext(methodReference, false);

            // Do the advice-call
            CallAdvice(filterAction, parentType, methodToCall, jpcVar);

            // Restore the JoinPointContext
            RestoreJoinPointContext(methodReference, jpcVar, true);

            // Add nop to enable debugging
            Instructions.Add(Worker.Create(OpCodes.Nop));
        }


        /// <summary>
        /// Sets back the arguments and the returnvalue (for filteractions on return).
        /// </summary>
        /// <param name="originalMethod">MethodReference to the original method</param>
        /// <param name="jpcVar">VariableDefinition containing the JoinPointContext object</param>
        private void RestoreJoinPointContext(MethodReference originalMethod, VariableDefinition jpcVar, bool storeReturnValue)
        {
            int numberOfArguments = originalMethod.Parameters.Count;

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

                // Check if parameter is value type, then unbox
                if (originalMethod.Parameters[i].ParameterType.IsValueType)
                {
                    Instructions.Add(Worker.Create(OpCodes.Unbox_Any, originalMethod.Parameters[i].ParameterType));
                }

                // Store argument
                Instructions.Add(Worker.Create(OpCodes.Starg_S, originalMethod.Parameters[i]));
            }

            // Retrieve returnvalue
            if (storeReturnValue && !Method.ReturnType.ReturnType.FullName.Equals(VoidType))
            {
                Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

                Instructions.Add(Worker.Create(OpCodes.Callvirt, CreateMethodReference(
                    typeof(JoinPointContext).GetMethod("get_ReturnValue", new Type[] { }))));

                // Check if returnvalue is value type, then unbox, else cast
                if (originalMethod.ReturnType.ReturnType.IsValueType)
                {
                    Instructions.Add(Worker.Create(OpCodes.Unbox_Any, originalMethod.ReturnType.ReturnType));
                }
                else
                {
                    Instructions.Add(Worker.Create(OpCodes.Castclass, Method.ReturnType.ReturnType));
                }

                // Store the returnvalue
                VariableDefinition returnValueVar = CreateReturnValueLocal(originalMethod.ReturnType.ReturnType);
                Instructions.Add(Worker.Create(OpCodes.Stloc, returnValueVar));
            }
        }


        /// <summary>
        /// Weaves the call to the advice
        /// </summary>
        /// <param name="filterAction">The filteraction</param>
        /// <param name="parentType">The type containing the original method</param>
        /// <param name="methodToCall">The advice method</param>
        /// <param name="jpcVar">The local variable containing the JoinPointContext</param>
        private void CallAdvice(FilterAction filterAction, TypeDefinition parentType, MethodReference methodToCall,
            VariableDefinition jpcVar)
        {
            // Place target on the stack:
            if (methodToCall.HasThis)
            {
                if (filterAction.Target.Equals(FilterAction.INNER_TARGET) ||
                    filterAction.Target.Equals(FilterAction.SELF_TARGET))
                {
                    Instructions.Add(Worker.Create(OpCodes.Ldarg, Method.This));
                }
                else
                {
                    FieldDefinition target = parentType.Fields.GetField(filterAction.Target);
                    if (target == null)
                    {
                        throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
                            Properties.Resources.FieldNotFound, filterAction.Target));
                    }

                    Instructions.Add(Worker.Create(OpCodes.Ldarg, Method.This));
                    Instructions.Add(Worker.Create(OpCodes.Ldfld, target));
                }
            }

            // Load the JoinPointObject as the parameter
            Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

            // Call the Target
            Instructions.Add(Worker.Create(OpCodes.Call, methodToCall));
        }

        /// <summary>
        /// Weaves the creation and initialization of the JoinPointContext object.
        /// </summary>
        /// <param name="originalCall">The originaly called method</param>
        /// <returns>The VariableDefinition of the JoinPointContext object</returns>
        private VariableDefinition CreateJoinPointContext(MethodReference originalCall, bool storeReturnValue)
        {
            // Create a new or use an existing local variable for the JoinPointContext
            VariableDefinition jpcVar = CreateJoinPointContextLocal();
            Method.Body.InitLocals = true;

            //
            // Create new joinpointcontext object
            //
            Instructions.Add(Worker.Create(OpCodes.Newobj, CreateMethodReference(typeof(JoinPointContext).GetConstructors()[0])));

            // Store the just created joinpointcontext object
            Instructions.Add(Worker.Create(OpCodes.Stloc, jpcVar));

            // Store returnvalue
            if (storeReturnValue && !originalCall.ReturnType.ReturnType.FullName.Equals(VoidType))
            {
                // Get returnvalue field
                VariableDefinition returnValueVar = CreateReturnValueLocal(originalCall.ReturnType.ReturnType);

                // Load joinpointcontext object
                Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

                // Load returnvalue
                Instructions.Add(Worker.Create(OpCodes.Ldloc, returnValueVar));

                // Check if returnvalue is value type, then box
                if (originalCall.ReturnType.ReturnType.IsValueType)
                {
                    Instructions.Add(Worker.Create(OpCodes.Box, originalCall.ReturnType.ReturnType));
                }

                // Determine type
                Instructions.Add(Worker.Create(OpCodes.Callvirt, CreateMethodReference(typeof(System.Object).GetMethod("GetType", new Type[] { }))));

                // Call set_ReturnType in JoinPointContext
                Instructions.Add(Worker.Create(OpCodes.Callvirt, CreateMethodReference(
                    typeof(JoinPointContext).GetMethod("set_ReturnType", new Type[] { typeof(System.Type) }))));

                // Load joinpointcontext object
                Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

                // Again load the returnvalue
                Instructions.Add(Worker.Create(OpCodes.Ldloc, returnValueVar));

                // Check if returnvalue is value type, then box
                if (originalCall.ReturnType.ReturnType.IsValueType)
                {
                    Instructions.Add(Worker.Create(OpCodes.Box, originalCall.ReturnType.ReturnType));
                }
                
                // Call set_ReturnValue in JoinPointContext
                Instructions.Add(Worker.Create(OpCodes.Callvirt, CreateMethodReference(
                    typeof(JoinPointContext).GetMethod("set_ReturnValue", new Type[] { typeof(object) }))));
            }


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

            int numberOfArguments = 0;
            
            switch (FilterType)
            {
                case FilterTypes.InputFilter:
                    numberOfArguments = Method.Parameters.Count;
                    break;
                case FilterTypes.OutputFilter:
                    //numberOfArguments = methodBaseDef.GetParameters().Length;
                    //// Also set the sender
                    //// Load the joinpointcontext object
                    //Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

                    //// Load the this pointer
                    //if (Method.HasThis)
                    //    Instructions.Add(Worker.Create(OpCodes.Ldarg, Method.This));
                    //else
                    //    Instructions.Add(Worker.Create(OpCodes.Ldnull));

                    //// Assign to the Target property
                    //Instructions.Add(Worker.Create(OpCodes.Callvirt, CreateMethodReference(typeof(JoinPointContext).GetMethod("set_Sender", new Type[] { typeof(object) }))));

                    break;
            }

            //
            // Add the arguments, these are stored at the top of the stack
            //
            if (numberOfArguments > 0)
            {
                for (int i = 0; i < numberOfArguments; i++)
                {
                    //methodReference.Parameters[ i ].Attributes = Mono.Cecil.ParamAttributes.Out;                    

                    // Load jpc
                    Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

                    // Load the ordinal
                    Instructions.Add(Worker.Create(OpCodes.Ldc_I4, i));

                    // load the argument 
                    Instructions.Add(Worker.Create(OpCodes.Ldarg, originalCall.Parameters[i]));

                    // Check if parameter is value type, then box
                    if (originalCall.Parameters[i].ParameterType.IsValueType)
                    {
                        Instructions.Add(Worker.Create(OpCodes.Box, originalCall.Parameters[i].ParameterType));
                    }

                    // Determine type
                    Instructions.Add(Worker.Create(OpCodes.Callvirt, CreateMethodReference(typeof(System.Object).GetMethod("GetType", new Type[] { }))));

                    // Again load the argument
                    Instructions.Add(Worker.Create(OpCodes.Ldarg, originalCall.Parameters[i]));

                    // Check if parameter is value type, then box
                    if (originalCall.Parameters[i].ParameterType.IsValueType)
                    {
                        Instructions.Add(Worker.Create(OpCodes.Box, originalCall.Parameters[i].ParameterType));
                    }

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
            Instructions.Add(Worker.Create(OpCodes.Ldstr, originalCall.Name));

            // Assign name to MethodName
            Instructions.Add(Worker.Create(OpCodes.Callvirt, CreateMethodReference(typeof(JoinPointContext).GetMethod("set_MethodName", new Type[] { typeof(string) }))));

            return jpcVar;
        }

        /// <summary>
        /// Returns the MethodReference to the advice method
        /// </summary>
        /// <param name="filterAction">The filteraction</param>
        /// <param name="parentType">The type containing the original method</param>
        /// <returns>The MethodReference to the advice method</returns>
        private MethodReference GetMethodToCall(FilterAction filterAction, TypeDefinition parentType)
        {
            if (filterAction.Target.Equals(FilterAction.INNER_TARGET) ||
                filterAction.Target.Equals(FilterAction.SELF_TARGET))
            {
                return CecilUtilities.ResolveMethod(parentType, filterAction.Selector, JPCTypes);
            }
            else
            {
                FieldDefinition target = parentType.Fields.GetField(filterAction.Target);
                if (target == null)
                {
                    throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
                        Properties.Resources.FieldNotFound, filterAction.Target));
                }

                
                MethodDefinition md = CecilUtilities.ResolveMethod(target.FieldType, filterAction.Selector, JPCTypes);

                return TargetAssemblyDefinition.MainModule.Import(md);
            }
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

        #endregion

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
                    FieldDefinition target = null;
                    // Self call

                    // Get the methodReference
                    MethodReference methodReference = (MethodReference)Method;

                    TypeDefinition parentType = (TypeDefinition)methodReference.DeclaringType;

                    //Get the called method:
                    if (filterAction.Target.Equals(FilterAction.INNER_TARGET) ||
                        filterAction.Target.Equals(FilterAction.SELF_TARGET))
                    {
                        if (filterAction.Selector.Equals(Method.Name))
                        {
                            methodReference = Method;
                        }
                        else
                        {
                            methodReference = CecilUtilities.ResolveMethod(parentType, filterAction.Selector, Method);
                        }
                    }
                    else
                    {
                        target = parentType.Fields.GetField(filterAction.Target);
                        if (target == null)
                        {
                            throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
                                Properties.Resources.FieldNotFound, filterAction.Target));
                        }

                        TypeDefinition fieldType = (TypeDefinition)target.FieldType;
                        MethodDefinition md = CecilUtilities.ResolveMethod(fieldType, filterAction.Selector, Method);

                        methodReference = TargetAssemblyDefinition.MainModule.Import(md);
                    }

                    if (methodReference == null)
                    {
                        return;
                    }


                    // Place the arguments on the stack first

                    //Place target on the stack:
                    if (methodReference.HasThis)
                    {
                        if (filterAction.Target.Equals(FilterAction.INNER_TARGET) ||
                            filterAction.Target.Equals(FilterAction.SELF_TARGET))
                        {
                            Instructions.Add(Worker.Create(OpCodes.Ldarg, Method.This));
                        }
                        else
                        {
                            Instructions.Add(Worker.Create(OpCodes.Ldarg, Method.This));
                            Instructions.Add(Worker.Create(OpCodes.Ldfld, target));
                        }
                    }


                    int numberOfArguments = Method.Parameters.Count;
                    for (int i = 0; i < numberOfArguments; i++)
                    {
                        Instructions.Add(Worker.Create(OpCodes.Ldarg, Method.Parameters[i]));
                    }

                    // Call the method
                    Instructions.Add(Worker.Create(OpCodes.Call, methodReference));

                    //Store the return value:
                    if (!methodReference.ReturnType.ReturnType.FullName.Equals(VoidType))
                    {
                        VariableDefinition returnValueVar = CreateReturnValueLocal(Method.ReturnType.ReturnType);
                        Instructions.Add(Worker.Create(OpCodes.Stloc, returnValueVar));
                    }

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
            conditionsVisitor.RepositoryAccess = _languageModelAccessor;
            ((Composestar.Repository.LanguageModel.ConditionExpressions.Visitor.IVisitable)branch.ConditionExpression).Accept(conditionsVisitor);

            // Add the instructions containing the conditions to the IL instruction list
            AddInstructionList(conditionsVisitor.Instructions);

            // Add branch code
            branch.Label = BranchLabelOffSet + numberOfBranches;   // TODO check the correctness of this constructions (Michiel)
            numberOfBranches = numberOfBranches + 2;
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
            whileInstr.Label = BranchLabelOffSet + numberOfBranches;
            numberOfBranches = numberOfBranches + 2;
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
            foreach (Case c in switchInstr.Cases)
            {
                caseLabels.Add(GetJumpLabel(c.CheckConstant + 10000));
            }

            // The switch statement
            Instructions.Add(Worker.Create(OpCodes.Switch, caseLabels.ToArray()));

            // Jump to the end
            switchInstr.Label = BranchLabelOffSet + numberOfBranches;   // TODO check the correctness of this constructions (Michiel)
            numberOfBranches = numberOfBranches + 1;
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

            // Load the local
            Instructions.Add(Worker.Create(OpCodes.Ldloc, asVar));

            // Load the id onto the stack
            Instructions.Add(Worker.Create(OpCodes.Ldc_I4, contextInstruction.Code));

            // Call the StoreAction method
            Instructions.Add(Worker.Create(OpCodes.Callvirt, CreateMethodReference(typeof(FilterContext).GetMethod("StoreAction", new Type[] { typeof(int) }))));

        }



        #endregion

        #endregion

    }
}
