using System;
using System.Collections.Generic;
using System.Reflection;
using System.Text;

using Composestar.Repository.LanguageModel.Inlining;
using Composestar.Repository.LanguageModel.Inlining.Visitor;

using Composestar.StarLight.ContextInfo;

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

        #region Private variables
        IList<Instruction> _instructions = new List<Instruction>();
        CilWorker _worker;
        MethodDefinition _method;
        AssemblyDefinition _targetAssemblyDefinition;
        FilterTypes _filterType;
        Dictionary<int, Instruction> _jumpInstructions = new Dictionary<int, Instruction>();
        #endregion

        public enum FilterTypes
        {
            InputFilter = 1,
	        OutputFilter = 2,
        }
        
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
        private VariableDefinition _objectLocal = null;
        private VariableDefinition _typeLocal = null;
        private VariableDefinition _jpcLocal = null;

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

        public void VisitInlineInstruction(InlineInstruction inlineInstruction)
        {
            if (inlineInstruction.Label != null & inlineInstruction.Label.Id != -1 )
                Instructions.Add(GetJumpLabel(inlineInstruction.Label.Id)); 
        }

        public void VisitAfterAction(FilterAction filterAction)
        {
           
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

            //
            // Add the arguments, these are stored at the top of the stack
            //
            int numberOfArguments;
            if (FilterType == FilterTypes.InputFilter)
                numberOfArguments = Method.Parameters.Count;
            else
                numberOfArguments = 0; // TODO aanpassen

            if (numberOfArguments > 0)
            {
                // We have to use temporary local varibales, because we cannot swap the elements on the stack
                // and we have to place the pointer to the jpc on the stack also
                // Create the local vars, but only once in this method.
                VariableDefinition objectVar = CreateObjectLocal();
                VariableDefinition typeVar = CreateTypeLocal();
                
                for (int i = numberOfArguments; i >= 0; i--) // We start at the top, because the last element is at to top of the stack
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
            // Load the JoinPointObject first
            //Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

            // Call the Target
            //Instructions.Add(Worker.Create(OpCodes.Call, filterAction.Target)); 
            // TODO This will most likely not work, difference between call and callvirt etc 
            // Have to resolve this to a valid type

            //
            // Retrieve the arguments
            //               
            for (int i = numberOfArguments; i > 0; i--) // We start at the top, because the last element is at to top of the stack
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
        /// Branching for conditions
        /// </summary>
        /// <param name="branch"></param>
        public void VisitBranch(Branch branch)
        {
           
            // Add condition code
            CecilConditionsVisitor conditionsVisitor = new CecilConditionsVisitor();
            conditionsVisitor.Method = Method;
            conditionsVisitor.Worker = Worker;
            conditionsVisitor.TargetAssemblyDefinition = TargetAssemblyDefinition;
            ((Composestar.Repository.LanguageModel.ConditionExpressions.Visitor.IVisitable)branch.ConditionExpression).Accept(conditionsVisitor);  

            AddInstructionList(conditionsVisitor.Instructions);
 
            // TODO condition
            Instructions.Add(Worker.Create(OpCodes.Ldc_I4_1));  // Load a constant value of 1 for now
 
            // Add branch code
            Instructions.Add(Worker.Create(OpCodes.Brtrue, GetJumpLabel(branch.TrueBlock.Label.Id)));
            Instructions.Add(Worker.Create(OpCodes.Br, GetJumpLabel(branch.FalseBlock.Label.Id)));
        }

        public void VisitContextInstruction(ContextInstruction contextInstruction)
        {
            
        }

        public void VisitContinueAction(FilterAction filterAction)
        {
            // No code needed
        }

        public void VisitDispatchAction(FilterAction filterAction)
        {
            

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
        ///   L_0000: nop 
        ///   L_0001: newobj instance void [mscorlib]System.Exception::.ctor()
        ///   L_0006: throw 
        /// </code>
        /// </example> 
        public void VisitErrorAction(FilterAction filterAction)
        {           
            // Create an exception
            Instructions.Add(Worker.Create(OpCodes.Newobj, CreateMethodReference(typeof(Exception).GetConstructors()[0] )));

            // Throw the exception
            Instructions.Add(Worker.Create(OpCodes.Throw));

            // Add the Nop to enable debugging
            Instructions.Add(Worker.Create(OpCodes.Nop)); 
            
        }

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
            Instruction jumpToInstruction = GetJumpLabel(jump.Target.Id);
            if (jumpToInstruction == null)
                throw new ILWeaverException(Properties.Resources.FilterJumpLabelIsNotSet);

            // Add a branch instruction
            Instructions.Add(Worker.Create(OpCodes.Br, jumpToInstruction)); 
        }

        public void VisitSkipAction(FilterAction filterAction)
        {
            // No code needed
        }

        public void VisitSubstitutionAction(FilterAction filterAction)
        {
            // No code needed
        }
    }
}
