using System;
using System.Collections.Generic;
using System.Text;

using Mono.Cecil;
using Mono.Cecil.Cil;

using Composestar.StarLight.Entities.Concerns;
using Composestar.StarLight.Entities.Configuration;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities.WeaveSpec;
using Composestar.StarLight.Entities.WeaveSpec.Instructions;
using Composestar.StarLight.CoreServices;
  
namespace Composestar.StarLight.Utilities.Interfaces
{
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
    
    /// <summary>
    /// Interface for the Cecil inlining instructions.
    /// </summary>
    [CLSCompliant(false)]
    public interface ICecilInliningInstructionVisitor
    {
        /// <summary>
        /// Creates the local variable.
        /// </summary>
        /// <param name="type">The type of the variable to create.</param>
        /// <returns>Returns the variable as a <see cref="T:Mono.Cecil.VariableDefinition"></see>.</returns>
        [CLSCompliant(false)]
        VariableDefinition CreateLocalVariable(Type type);

         /// <summary>
        /// Creates the local variable.
        /// </summary>
        /// <param name="type">The typereference to create.</param>
        /// <returns>Returns the variable as a <see cref="T:Mono.Cecil.VariableDefinition"></see>.</returns>
        [CLSCompliant(false)]
        VariableDefinition CreateLocalVariable(TypeReference type);

        /// <summary>
        /// Creates the join point context variable.
        /// </summary>
        /// <returns>Returns the <see cref="T:Composestar.StarLight.ContextInfo.JoinPointContext"></see> as a <see cref="T:Mono.Cecil.VariableDefinition"></see>.</returns>
        [CLSCompliant(false)]
        VariableDefinition CreateJoinPointContextLocal();

        /// <summary>
        /// Gets or sets the WeaveType object.
        /// </summary>
        /// <value>The type of the weave.</value>
        WeaveType WeaveType { get;set;}
        /// <summary>
        /// Gets or sets the type of the filter.
        /// </summary>
        /// <value>The type of the filter.</value>
        FilterTypes FilterType { get;set;}
        /// <summary>
        /// Gets or sets the entities accessor.
        /// </summary>
        /// <value>The entities accessor.</value>
        IEntitiesAccessor EntitiesAccessor { get;set;}
        /// <summary>
        /// Weave configuration
        /// </summary>
        /// <returns>Configuration container</returns>
        ConfigurationContainer WeaveConfiguration { get;set;}
        /// <summary>
        /// Gets or sets the target assembly definition.
        /// </summary>
        /// <value>The target assembly definition.</value>
        [CLSCompliant(false)]
        AssemblyDefinition TargetAssemblyDefinition { get;set;}
        /// <summary>
        /// Gets or sets the containing method.
        /// </summary>
        /// <value>The method.</value>
        [CLSCompliant(false)]
        MethodDefinition Method { get;set;}
        /// <summary>
        /// Gets or sets the called method. For inputfilters this is the containing method.
        /// For outputfilters this is the method to which the outgoing call is targeted
        /// </summary>
        /// <value>The method.</value>
        [CLSCompliant(false)]
        MethodDefinition CalledMethod { get;set;}
        /// <summary>
        /// Gets or sets the worker.
        /// </summary>
        /// <value>The worker.</value>
        [CLSCompliant(false)]
        CilWorker Worker { get;set;}
        /// <summary>
        /// Gets or sets the instructions.
        /// </summary>
        /// <value>The instructions.</value>
        IList<Instruction> Instructions { get;set;}   
    }
    
}
