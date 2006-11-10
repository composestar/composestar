#region Using directives
using System;
using System.Collections.Generic;
using System.Text;
using System.Globalization;
using System.Diagnostics.CodeAnalysis;
 
using Composestar.StarLight.Entities.Concerns;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities.WeaveSpec;
using Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions;
using Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions.Visitor;
using Composestar.StarLight.Entities.WeaveSpec.Instructions;
using Composestar.StarLight.Entities.Configuration;
 
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Utilities;

using Mono.Cecil;
using Mono.Cecil.Cil;
#endregion

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
        private int m_NumberOfBranches;
        private Dictionary<int, Instruction> m_JumpInstructions = new Dictionary<int, Instruction>();
        private CecilInliningInstructionVisitor _visitor;
        private Type[] m_JpcTypes = new Type[1] { typeof(JoinPointContext) };
        #endregion

        #region Properties

        /// <summary>
        /// Gets the target assembly definition.
        /// </summary>
        /// <value>The target assembly definition.</value>
         private AssemblyDefinition TargetAssemblyDefinition
        {
            get
            {
                return _visitor.TargetAssemblyDefinition;
            }
        }

        /// <summary>
        /// Gets the method.
        /// </summary>
        /// <value>The method.</value>
        private MethodDefinition Method
        {
            get
            {
                return _visitor.Method;
            }
        }


        /// <summary>
        /// Gets the worker.
        /// </summary>
        /// <value>The worker.</value>
        private CilWorker Worker
        {
            get
            {
                return _visitor.Worker;
            }
        }

        /// <summary>
        /// Gets the conditions.
        /// </summary>
        /// <value>The conditions.</value>
        private List<Condition> Conditions
        {
            get
            {
                return _visitor.WeaveType.Conditions;  
            }
        }

        /// <summary>
        /// Weave configuration
        /// </summary>
        /// <returns>Configuration container</returns>
        [SuppressMessage("Microsoft.Performance", "CA1811:AvoidUncalledPrivateCode")]
        private ConfigurationContainer WeaveConfiguration
        {
            get
            {
                return _visitor.WeaveConfiguration;
            } 
        } // WeaveConfiguration

        /// <summary>
        /// Weave type
        /// </summary>
        /// <returns>Weave type</returns>
        private WeaveType WeaveType
        {
            get
            {
                return _visitor.WeaveType;
            } // get
        }
        
         /// <summary>
         /// Gets the instructions.
         /// </summary>
         /// <value>The instructions.</value>
        private IList<Instruction> Instructions
        {
            get
            {
                return _visitor.Instructions;
            }
        }

        /// <summary>
        /// Gets the entities accessor.
        /// </summary>
        /// <value>The entities accessor.</value>
        public IEntitiesAccessor EntitiesAccessor
        {
            get
            {
                return _visitor.EntitiesAccessor;
            }
        }
        #endregion

        #region Helper functions

        /// <summary>
        /// Gets the condition by its name.
        /// </summary>
        /// <param name="name">Name of the condition to retrieve from the weavetype in the parent visitor.</param>
        /// <returns>The <see cref="T:Condition"></see> object or <see langword="null" /> if not found in the weave type.</returns>
        private Condition GetConditionByName(string name)
        {
            foreach (Condition con in Conditions)
            {
                if (con.Name.Equals(name))
                    return con;
            } // foreach  (con)

            return null;
        } // GetConditionByName(name)

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

        #region ctor

        /// <summary>
        /// Initializes a new instance of the <see cref="T:CecilConditionsVisitor"/> class.
        /// </summary>
        /// <param name="visitor">The parent visitor.</param>
        public CecilConditionsVisitor(CecilInliningInstructionVisitor visitor)
        {
            if (visitor == null)
                throw new ArgumentNullException("visitor");
             
            _visitor = visitor;
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
                        
            Condition con = GetConditionByName(conditionLiteral.Name);
            if (con == null)
                throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.ConditionNotFound, conditionLiteral.Name));

            // Get the parenttype
            TypeDefinition parentType = CecilUtilities.ResolveTypeDefinition(_visitor.Method.DeclaringType);

            // Get the method
            MethodReference method;
            FieldDefinition target = null;
            if (con.Reference.Target.Equals(Reference.InnerTarget) ||
                con.Reference.Target.Equals(Reference.SelfTarget))
            {
                method = CecilUtilities.ResolveMethod(
                    con.Reference.Selector, WeaveType.Name,
                    con.Reference.Assembly, "");
            }
            else if(isInternal(con.Reference.Target) || isExternal(con.Reference.Target))
            {
                if(!Method.HasThis)
                {
                    throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture, 
                        Properties.Resources.StaticReferenceInternalExternal, 
                        Method.Name, 
                        Method.DeclaringType.FullName));
                }

                target = parentType.Fields.GetField(con.Reference.Target);
                
                TypeDefinition fieldType = CecilUtilities.ResolveTypeDefinition(target.FieldType);
                MethodDefinition md = CecilUtilities.ResolveMethod(fieldType, con.Reference.Selector, new Type[0]);

                // If method with no parameters cannot be found, try to find method with JPC context as parameter
                if(md == null)
                {
                    md = CecilUtilities.ResolveMethod(fieldType, con.Reference.Selector, m_JpcTypes);
                }

                if(md == null)
                    throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.MethodNotFound2, con.Reference.Selector, con.Reference.Target, fieldType.FullName));

                method = _visitor.TargetAssemblyDefinition.MainModule.Import(md);
            }
            else
            {
                method = CecilUtilities.ResolveMethod(
                    con.Reference.Selector,
                    (con.Reference.NameSpace.Length > 0 ? con.Reference.NameSpace + "." : "") + con.Reference.Target,
                    con.Reference.Assembly, "");
            }

            if (method == null)
                throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture, 
                    Properties.Resources.MethodNotFound, 
                    con.Reference.Selector, 
                    con.Reference.Fullname, 
                    con.Reference.Assembly));


            if (con.Reference.Target.Equals(Reference.InnerTarget) ||
                con.Reference.Target.Equals(Reference.SelfTarget))
            {
                // Set innercall context
                if(con.Reference.InnerCallContext >= 0)
                {
                    // Load the this parameter
                    if(!Method.HasThis)
                        Instructions.Add(Worker.Create(OpCodes.Ldnull));
                    else
                        Instructions.Add(Worker.Create(OpCodes.Ldarg, Method.This));

                    // Load the methodId
                    Instructions.Add(Worker.Create(OpCodes.Ldc_I4, con.Reference.InnerCallContext));

                    // Call the SetInnerCall
                    Instructions.Add(Worker.Create(OpCodes.Call, 
                        CreateMethodReference(typeof(FilterContext).GetMethod("SetInnerCall", 
                        new Type[] { typeof(object), typeof(int) }))));
                }


                // Load the this pointer
                Instructions.Add(Worker.Create(OpCodes.Ldarg, Method.This));
            }
            // internal/external and not static:
            else if(target != null  &&  method.HasThis)
            {
                // Load the this pointer
                Instructions.Add(Worker.Create(OpCodes.Ldarg, Method.This));

                // Load the field
                Instructions.Add(Worker.Create(OpCodes.Ldfld, target));
            }
            //else do nothing, because of static call

            // Check if we have to add the JPC
            if (method.Parameters.Count == 1)
            {
                if (method.Parameters[0].ParameterType.FullName.Equals(typeof(JoinPointContext ).FullName))
                {
                    VariableDefinition jpcVar = _visitor.CreateJoinPointContextLocal();
                    Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar)); 
                } // VisitConditionLiteral(conditionLiteral)
            } // if

            // Create a call instruction
            Instructions.Add(Worker.Create(OpCodes.Call, TargetAssemblyDefinition.MainModule.Import(method)));

        }

        private bool isInternal(string target)
        {
            foreach(Internal i in _visitor.WeaveType.Internals)
            {
                if(i.Name.Equals(target))
                {
                    return true;
                }
            }

            return false;
        }

        
        private bool isExternal(string target)
        {
            foreach(External e in _visitor.WeaveType.Externals)
            {
                if(e.Name.Equals(target))
                {
                    return true;
                }
            }

            return false;
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
        public void VisitFalse(FalseCondition falseObject)
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
        public void VisitTrue(TrueCondition trueObject)
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
        public void VisitNot(NotCondition not)
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
        public void VisitOrLeft(OrCondition or)
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
        public void VisitOrRight(OrCondition or)
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
        public void VisitAndLeft(AndCondition and)
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
        public void VisitAndRight(AndCondition and)
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
