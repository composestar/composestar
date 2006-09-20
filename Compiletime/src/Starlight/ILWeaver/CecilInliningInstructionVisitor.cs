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
        /// Creates the local var.
        /// </summary>
        /// <param name="type">The type.</param>
        /// <returns>Returns the ordinal number given to this variable</returns>
        private int CreateLocalVar(Type type)
        {
            TypeReference typeRef = _targetAssemblyDefinition.MainModule.Import(type);
            VariableDefinition var = new VariableDefinition(typeRef);

            Method.Body.Variables.Add(var);

            return Method.Body.Variables.Count - 1 + (Method.HasThis ? 1 : 0);
        }

        /// <summary>
        /// Creates the method reference.
        /// </summary>
        /// <param name="methodInfo">The method info.</param>
        /// <returns></returns>
        private MethodReference CreateMethodReference(MethodInfo methodInfo)
        {
            return TargetAssemblyDefinition.MainModule.Import(methodInfo);
        }

        // Because we need local vars to store the object and type of arguments in, we have to add these local vars.
        // But only once, so these functions make sure we only have one of this variables
        private int _objectOrdinal = -1;
        private int _typeOrdinal = -1;
        private int _jpcOrdinal = -1;

        /// <summary>
        /// Creates the object ordinal.
        /// </summary>
        /// <returns></returns>
        private int CreateObjectOrdinal()
        {
            if (_objectOrdinal == -1)
            {
                _objectOrdinal = CreateLocalVar(typeof(Object));
            }

            return _objectOrdinal;
        }

        /// <summary>
        /// Creates the type ordinal.
        /// </summary>
        /// <returns></returns>
        private int CreateTypeOrdinal()
        {
            if (_typeOrdinal == -1)
            {
                _typeOrdinal = CreateLocalVar(typeof(System.Type));
            }

            return _typeOrdinal;
        }

        /// <summary>
        /// Creates the join point context ordinal.
        /// </summary>
        /// <returns></returns>
        private int CreateJoinPointContextOrdinal()
        {
            if (_jpcOrdinal == -1)
            {
                _jpcOrdinal = CreateLocalVar(typeof(JoinPointContext));
            }

            return _jpcOrdinal;
        }
        #endregion

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
            int jpcOrdinal = CreateJoinPointContextOrdinal();

            //
            // Create new joinpointcontext object
            //
            Instructions.Add(Worker.Create(OpCodes.Newobj, CreateMethodReference(typeof(JoinPointContext).GetMethod(".ctor", new Type[] { }))));
            
            // Store the just created joinpointcontext object
            Instructions.Add(Worker.Create(OpCodes.Stloc, jpcOrdinal));

            //
            // Set the target
            //
            // Load the joinpointcontext object
            Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcOrdinal));
            
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
                int objectOrdinal = CreateObjectOrdinal();
                int typeOrdinal = CreateTypeOrdinal();
                
                for (int i = numberOfArguments; i >= 0; i--) // We start at the top, because the last element is at to top of the stack
                {
                    // Duplicate the value
                    Instructions.Add(Worker.Create(OpCodes.Dup));

                    // Determine type
                    Instructions.Add(Worker.Create(OpCodes.Callvirt, CreateMethodReference(typeof(System.Type).GetMethod("GetType", new Type[] { }))));

                    // Save the type
                    Instructions.Add(Worker.Create(OpCodes.Stloc, typeOrdinal));
                     
                    // Save the object
                    Instructions.Add(Worker.Create(OpCodes.Stloc, objectOrdinal));

                    // Perpare to call AddArgument by loading the parameters onto the stack
                    // Load jpc
                    Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcOrdinal));
                     
                    // Load the ordinal
                    Instructions.Add(Worker.Create(OpCodes.Ldc_I4, i));

                    // Load the type
                    Instructions.Add(Worker.Create(OpCodes.Ldloc, typeOrdinal));
                     
                    // Load the object
                    Instructions.Add(Worker.Create(OpCodes.Ldloc, objectOrdinal));

                    // Call the AddArgument function
                    Instructions.Add(Worker.Create(OpCodes.Callvirt, CreateMethodReference(typeof(JoinPointContext).GetMethod("AddArgument", new Type[] { typeof(Int16), typeof(System.Type), typeof(object) }))));
                }
            }

            //
            // Set the selector
            //
            // Load joinpointcontext first
            Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcOrdinal));
             
            // Load the name onto the stack
            Instructions.Add(Worker.Create(OpCodes.Ldstr, filterAction.Selector));
                       
            // Assign name to MethodName
            Instructions.Add(Worker.Create(OpCodes.Callvirt, CreateMethodReference(typeof(JoinPointContext).GetMethod("set_MethodName", new Type[] { typeof(string) }))));

            //
            // Call the target
            //
            // Load the JoinPointObject first
            Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcOrdinal));

            // Call the Target
            Instructions.Add(Worker.Create(OpCodes.Call, filterAction.Target)); 
            // TODO This will most likely not work, difference between call and callvirt etc 

            //
            // Retrieve the arguments
            //               
            for (int i = numberOfArguments; i >= 0; i--) // We start at the top, because the last element is at to top of the stack
            {
                // Load jpc
                Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcOrdinal));

                // Load the ordinal
                Instructions.Add(Worker.Create(OpCodes.Ldc_I4, i));

                // Call the GetArgumentValue(int16) function
                Instructions.Add(Worker.Create(OpCodes.Callvirt, CreateMethodReference(typeof(JoinPointContext).GetMethod("GetArgumentValue", new Type[] { typeof(Int16) }))));
            }            

            // Add nop to enable debugging
            Instructions.Add(Worker.Create(OpCodes.Nop)); 
        }

        public void VisitBranch(Branch branch)
        {

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
            Instructions.Add(Worker.Create(OpCodes.Newobj, CreateMethodReference(typeof(Exception).GetMethod(".ctor", new Type[] { }))));

            // Throw the exception
            Instructions.Add(Worker.Create(OpCodes.Throw));

            // Add the NOP to enable debugging
            Instructions.Add(Worker.Create(OpCodes.Nop)); 
            
        }

        public void VisitFilterAction(FilterAction filterAction)
        {

        }

        public void VisitJumpInstruction(Jump jump)
        {

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
