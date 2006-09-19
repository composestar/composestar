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
    public class CecilInliningInstructionVisitor : IVisitor
    {

        #region Private variables
        IList<Instruction> _instructions = new List<Instruction>();
        CilWorker _worker;
        MethodReference _method;
        AssemblyDefinition _targetAssemblyDefinition;
        #endregion

        #region Properties
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
        public MethodReference Method
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

        public void VisitAfterAction(FilterAction filterAction)
        {
            
        }

        public void VisitBeforeAction(FilterAction filterAction)
        {
  //          JoinPointContext context = new JoinPointContext();
  //          context.MethodName = Method.Name;
  //          if (Method.HasThis)
  //context.Target(Method.)
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
            // Declare the needed information
            MethodInfo exceptionConstructor = typeof(Exception).GetMethod(".ctor", new Type[] { });
            MethodReference exceptionReference = TargetAssemblyDefinition.MainModule.Import(exceptionConstructor);

            // The actual instructions
            Instruction newException = Worker.Create(OpCodes.Newobj, exceptionReference); 
            Instruction throwInstruction = Worker.Create(OpCodes.Throw);
            
            // Add to the list
            Instructions.Add(newException); 
            Instructions.Add(throwInstruction);
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
