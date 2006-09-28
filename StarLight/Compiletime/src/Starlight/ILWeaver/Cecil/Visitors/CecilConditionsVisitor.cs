using System;
using System.Collections.Generic;
using System.Text;

using Composestar.Repository.LanguageModel.ConditionExpressions;
using Composestar.Repository.LanguageModel.ConditionExpressions.Visitor;
using Composestar.StarLight.CoreServices;
  
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace Composestar.StarLight.ILWeaver
{
    /// <summary>
    /// A visitor used to visit all the condition expressions and build an IL representation of the expressions.
    /// </summary>
    /// <remarks>
    /// The order of the ConditionExpressions is important. An And expression requires first the two values before 
    /// it can evaluate the And operator.
    /// </remarks> 
    public class CecilConditionsVisitor : IVisitor
    {

        #region Private variables

        IList<Instruction> _instructions = new List<Instruction>();
        CilWorker _worker;
        MethodDefinition _method;
        AssemblyDefinition _targetAssemblyDefinition;
        ILanguageModelAccessor _languageModelAccessor;  // FIXME we now pass this model through the visitors because we need the repository to get the conditions. Not a good seperation...
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
        #endregion

        #region Helper functions

        /// <summary>
        /// Creates the method reference.
        /// </summary>
        /// <param name="methodBase">The method base.</param>
        /// <returns></returns>
        private MethodReference CreateMethodReference(System.Reflection.MethodBase methodBase)
        {
            return TargetAssemblyDefinition.MainModule.Import(methodBase);
        }

        #endregion

        #region Condition Visitor Implementation

        /// <summary>
        /// Visits the and condition.
        /// </summary>
        /// <remarks>
        /// Generate the And instruction.
        /// </remarks> 
        /// <param name="and">The and.</param>
        public void VisitAnd(And and)
        {
            Instructions.Add(Worker.Create(OpCodes.And));
        }

        /// <summary>
        /// Visits the condition literal.
        /// </summary>
        /// <remarks>
        /// Generate a call to a condition. This is a call to a boolean method.
        /// </remarks> 
        /// <param name="conditionLiteral">The condition literal.</param>
        public void VisitConditionLiteral(ConditionLiteral conditionLiteral)
        {
            // Get the condition
            Composestar.Repository.LanguageModel.Condition con = RepositoryAccess.GetConditionByName(conditionLiteral.Name);
            if (con == null)
                throw new ILWeaverException(String.Format(Properties.Resources.ConditionNotFound, conditionLiteral.Name));

            // Get the type
            Composestar.Repository.LanguageModel.TypeElement te = RepositoryAccess.GetTypeElementById(con.ParentTypeId);

            // TODO Is the con.Reference.Selector unique enough?
            System.Reflection.MethodBase method = CecilUtilities.ResolveMethod(con.Reference.Selector, te.FullName, te.FromDLL);

            if (method == null)
                throw new ILWeaverException(String.Format(Properties.Resources.MethodNotFound, con.Reference.Selector, te.Name, te.AFQN));
            
            if ((method.CallingConvention&System.Reflection.CallingConventions.HasThis)==System.Reflection.CallingConventions.HasThis)
            {
                // Load the this pointer
                Instructions.Add(Worker.Create(OpCodes.Ldarg, Method.This)); 
            }

            // Create a call instruction
            Instructions.Add(Worker.Create(OpCodes.Call, CreateMethodReference(method)));

        }

        /// <summary>
        /// Visits the false.
        /// </summary>
        /// <remarks>
        /// Generate a 0, a false value.
        /// </remarks> 
        /// <param name="f">The f.</param>
        public void VisitFalse(False f)
        {
            Instructions.Add(Worker.Create(OpCodes.Ldc_I4_0));
        }

        /// <summary>
        /// Visits the not.
        /// </summary>
        /// <remarks>
        /// Generate the Not instruction.
        /// </remarks> 
        /// <param name="not">The not.</param>
        public void VisitNot(Not not)
        {
            Instructions.Add(Worker.Create(OpCodes.Not));
        }

        /// <summary>
        /// Visits the or.
        /// </summary>
        /// <remarks>
        /// Generate the Or operator. The two values should be placed on the stack first.
        /// </remarks> 
        /// <param name="or">The or.</param>
        public void VisitOr(Or or)
        {
            Instructions.Add(Worker.Create(OpCodes.Or));
        }

        /// <summary>
        /// Visits the true.
        /// </summary>
        /// <remarks>
        /// Generate a 1, a true value
        /// </remarks> 
        /// <param name="t">The t.</param>
        public void VisitTrue(True t)
        {
            Instructions.Add(Worker.Create(OpCodes.Ldc_I4_1));
        }

        #endregion
    
    }
}
