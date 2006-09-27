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
    /// A visitor used to visit all the condition expressions and build a IL representation of the expressions.
    /// </summary>
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

        private MethodReference CreateMethodReference(System.Reflection.MethodBase methodBase)
        {            
            return TargetAssemblyDefinition.MainModule.Import(methodBase);
        }

        public void VisitAnd(And and)
        {
            Instructions.Add(Worker.Create(OpCodes.And)); 
        }
        public void VisitConditionLiteral(ConditionLiteral conditionLiteral)
        {
            // Get the condition
            Composestar.Repository.LanguageModel.Condition con = RepositoryAccess.GetConditionByName(conditionLiteral.Name);
            if (con == null)
                throw new ILWeaverException(String.Format(Properties.Resources.ConditionNotFound, conditionLiteral.Name));

            // Get the type
            Composestar.Repository.LanguageModel.TypeElement te = RepositoryAccess.GetTypeElementById(con.ParentTypeId);
             
            System.Reflection.MethodBase method = CecilUtilities.ResolveMethod(con.Reference.Selector, te.FullName, te.FromDLL);

            if (method == null)
                throw new ILWeaverException(String.Format(Properties.Resources.MethodNotFound, con.Reference.Selector, te.Name, te.AFQN));

            Instructions.Add(Worker.Create(OpCodes.Call, CreateMethodReference(method)));

        }
        public void VisitFalse(False f)
        {
            Instructions.Add(Worker.Create(OpCodes.Ldc_I4_0));
        }
        public void VisitNot(Not not)
        {
            Instructions.Add(Worker.Create(OpCodes.Not));
        }
        public void VisitOr(Or or)
        {
            Instructions.Add(Worker.Create(OpCodes.And));
        }
        public void VisitTrue(True t)
        {
            Instructions.Add(Worker.Create(OpCodes.Ldc_I4_1));
        }
    }
}
