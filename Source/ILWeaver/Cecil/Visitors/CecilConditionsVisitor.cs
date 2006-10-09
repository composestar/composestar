using System;
using System.Collections.Generic;
using System.Text;

using Composestar.Repository.LanguageModel;
using Composestar.Repository.LanguageModel.ConditionExpressions;
using Composestar.Repository.LanguageModel.ConditionExpressions.Visitor;
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions;

using Mono.Cecil;
using Mono.Cecil.Cil;

namespace Composestar.StarLight.ILWeaver
{
    /// <summary>
    /// A visitor used to visit all the condition expressions and build an IL representation of the expressions.
    /// </summary>
    /// <remarks>
    /// The order of the ConditionExpressions is important. An And expression requires first the two values 
    /// before it can evaluate the And operator.
    /// </remarks> 
    public class CecilConditionsVisitor : IVisitor
    {

        #region Constant Values

        private const int BranchLabelOffSet = 9000;

        #endregion

        #region Private variables
        private IList<Instruction> m_Instructions = new List<Instruction>();
        private CilWorker m_Worker;
        private MethodDefinition m_Method;
        private int m_NumberOfBranches = 0;
        private AssemblyDefinition m_TargetAssemblyDefinition;
        private ILanguageModelAccessor m_LanguageModelAccessor;  // FIXME we now pass this model through the visitors because we need the repository to get the conditions. Not a good seperation...
        private Dictionary<int, Instruction> m_JumpInstructions = new Dictionary<int, Instruction>();
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
                return m_TargetAssemblyDefinition;
            }
            set
            {
                m_TargetAssemblyDefinition = value;
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
                return m_Method;
            }
            set
            {
                m_Method = value;
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
            if (!m_JumpInstructions.TryGetValue(labelId, out jumpNopInstruction))
            {
                jumpNopInstruction = Worker.Create(OpCodes.Nop);
                m_JumpInstructions.Add(labelId, jumpNopInstruction);
            }

            return jumpNopInstruction;
        }

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

        #region Variable Value

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

            MethodReference method;
            if (con.Reference.Target.Equals(Reference.INNER_TARGET) ||
                con.Reference.Target.Equals(Reference.SELF_TARGET))
            {
                method = CecilUtilities.ResolveMethod(
                    con.Reference.Selector, te.FullName,
                    te.Assembly, te.FromDLL);
            }
            else
            {
                method = CecilUtilities.ResolveMethod(
                    con.Reference.Selector,
                    (con.Reference.NameSpace.Length > 0 ? con.Reference.NameSpace + "." : "") + con.Reference.Target,
                    te.Assembly, te.FromDLL);
            }

            if (method == null)
                throw new ILWeaverException(String.Format(Properties.Resources.MethodNotFound, con.Reference.Selector, te.FullName, te.Assembly));


            if (con.Reference.Target.Equals(Reference.INNER_TARGET) ||
                con.Reference.Target.Equals(Reference.SELF_TARGET))
            {
                // Load the this pointer
                Instructions.Add(Worker.Create(OpCodes.Ldarg, Method.This));
            }

            // Create a call instruction
            Instructions.Add(Worker.Create(OpCodes.Call, m_TargetAssemblyDefinition.MainModule.Import(method)));

        }

        #endregion

        #region Boolean Values

        /// <summary>
        /// Visits the false.
        /// </summary>
        /// <remarks>
        /// Generate a 0, a false value.
        /// </remarks> 
        /// <param name="f">The f.</param>
        public void VisitFalse(False falseCondition)
        {
            Instructions.Add(Worker.Create(OpCodes.Ldc_I4_0));
        }

        /// <summary>
        /// Visits the true.
        /// </summary>
        /// <remarks>
        /// Generate a 1, a true value
        /// </remarks> 
        /// <param name="t">The t.</param>
        public void VisitTrue(True trueCondition)
        {
            Instructions.Add(Worker.Create(OpCodes.Ldc_I4_1));
        }

        #endregion

        #region Not Operator

        /// <summary>
        /// Visits the not.
        /// </summary>
        /// <remarks>
        /// Generate the !A instruction.
        /// <code>
        /// ldloc A
        /// ldc.i4.0 
        /// ceq 
        /// </code>
        /// </remarks> 
        /// <param name="not">The not.</param>
        public void VisitNot(Not not)
        {
            Instructions.Add(Worker.Create(OpCodes.Ldc_I4_0));
            Instructions.Add(Worker.Create(OpCodes.Ceq));
        }

        #endregion

        #region Or Operator

        /// <summary>
        /// Visits the or.
        /// </summary>
        /// <remarks>
        /// Generate the || operator. The two values should be placed on the stack first.
        /// <code>
        /// L_0005: ldloc.0 // left
        /// L_0006: brtrue.s L_000b
        /// L_0008: ldloc.1 // right
        /// L_0009: br.s L_000c
        /// L_000b: ldc.i4.1 
        /// L_000c: stloc.2 // result
        /// </code>
        /// </remarks>         
        /// <param name="or">The or.</param>
        public void VisitOrLeft(Or or)
        {
            or.BranchId = BranchLabelOffSet + m_NumberOfBranches;
            m_NumberOfBranches = m_NumberOfBranches + 2;

            Instructions.Add(Worker.Create(OpCodes.Brtrue_S, GetJumpLabel(or.BranchId)));
        }

        /// <summary>
        /// Visits the or right.
        /// </summary>
        /// <code>
        /// L_0008: ldloc.1 // right
        /// L_0009: br.s L_000c
        /// L_000b: ldc.i4.1 
        /// L_000c: stloc.2 // result
        /// </code>
        /// <param name="or">The or.</param>
        public void VisitOrRight(Or or)
        {
            Instructions.Add(Worker.Create(OpCodes.Br_S, GetJumpLabel(or.BranchId + 1)));
            Instructions.Add(GetJumpLabel(or.BranchId));
            Instructions.Add(Worker.Create(OpCodes.Ldc_I4_1));
            Instructions.Add(GetJumpLabel(or.BranchId + 1));
        }

        #endregion

        #region And Operator

        /// <summary>
        /// Visits the left and condition part.
        /// </summary>
        /// <remarks>
        /// Generate the &amp;&amp; instruction.
        /// <code>
        ///  L_000d: ldloc.0  // Left
        ///  L_000e: brfalse.s L_0013
        ///  L_0010: ldloc.1  // Right
        ///  L_0011: br.s L_0014
        ///  L_0013: ldc.i4.0 
        ///  L_0014: stloc.3  // Result 
        /// </code>
        /// </remarks> 
        /// <param name="and">The and.</param>
        public void VisitAndLeft(And and)
        {
            and.BranchId = BranchLabelOffSet + m_NumberOfBranches;
            m_NumberOfBranches = m_NumberOfBranches + 2;
            Instructions.Add(Worker.Create(OpCodes.Brfalse_S, GetJumpLabel(and.BranchId)));
        }

        /// <summary>
        /// Visits the right and condition part.
        /// </summary>
        /// <remarks>
        /// Generate the &amp;&amp; instruction.
        /// <code>
        ///  L_000d: ldloc.0  // Left
        ///  L_000e: brfalse.s L_0013
        ///  L_0010: ldloc.1  // Right
        ///  L_0011: br.s L_0014
        ///  L_0013: ldc.i4.0 
        ///  L_0014: stloc.3  // Result 
        /// </code>
        /// </remarks> 
        /// <param name="and">The and.</param>
        public void VisitAndRight(And and)
        {
            Instructions.Add(Worker.Create(OpCodes.Br_S, GetJumpLabel(and.BranchId + 1)));
            Instructions.Add(GetJumpLabel(and.BranchId));
            Instructions.Add(Worker.Create(OpCodes.Ldc_I4_0));
            Instructions.Add(GetJumpLabel(and.BranchId + 1));
        }


        #endregion

        #endregion

    }
}
