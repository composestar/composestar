using System;
using System.Collections.Generic;
using System.Globalization;
using System.Text;

using Mono.Cecil;
using Mono.Cecil.Cil;

using Composestar.StarLight.CoreServices.Exceptions;

using Composestar.StarLight.Entities.Concerns;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities.Configuration; 
using Composestar.StarLight.Entities.WeaveSpec;
using Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions;
using Composestar.StarLight.Entities.WeaveSpec.Instructions;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Utilities;
using Composestar.StarLight.Utilities.Interfaces;

namespace Composestar.StarLight.Weaving.Strategies
{
    /// <summary>
    /// TODO generate comment
    /// </summary>
    public class DefaultWeaveStrategy : FilterActionWeaveStrategy
    {
        /// <summary>
        /// Returns the name of the FilterAction for which this is the 
        /// weaving strategy.
        /// </summary>
        public override String FilterActionName
        {
            get
            {
                return "Default";
            }
        }

        /// <summary>
        /// Get filter action element
        /// </summary>
        /// <param name="elements">Elements</param>
        /// <param name="fullname">The fullname.</param>
        /// <returns>Filter action element</returns>
        private FilterActionElement GetFilterActionElement(List<FilterActionElement> elements, string fullname)
        {
            foreach (FilterActionElement fae in elements)
            {
                if (fae.FullName.Equals(fullname))
                    return fae;
            }

            return null;
        } // GetFilterActionElement(elements, name)


        /// <summary>
        /// Generate the code which has to be inserted at the place of the filter specified by the visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        /// <param name="filterAction">The filter action.</param>
        /// <param name="originalCall">The original call.</param>
        public override void Weave(ICecilInliningInstructionVisitor visitor, FilterAction filterAction,
            MethodDefinition originalCall)
        {
            MethodReference methodToCall;

            // Get JoinPointContext
            VariableDefinition jpcVar = visitor.CreateJoinPointContextLocal();

            // Get the methodReference
            MethodReference methodReference = (MethodReference) originalCall;
            TypeDefinition parentType = CecilUtilities.ResolveTypeDefinition(methodReference.DeclaringType);

            // Set JoinPointContext
            WeaveStrategyUtilities.SetJoinPointContext(visitor, methodReference, filterAction);

            // Create FilterAction object:
            FilterActionElement filterActionElement;
            filterActionElement = GetFilterActionElement(visitor.WeaveConfiguration.FilterActions, filterAction.FullName);

            if (filterActionElement == null)
                throw new ILWeaverException(string.Format(Properties.Resources.CouldNotResolveFilterAction, filterAction.FullName)); 
            
            TypeReference typeRef =
                CecilUtilities.ResolveType(filterAction.FullName, filterActionElement.Assembly, null);
            TypeDefinition typeDef =
                CecilUtilities.ResolveTypeDefinition(typeRef);
            MethodReference constructor = typeDef.Constructors.GetConstructor(false, new Type[0]);
            constructor = visitor.TargetAssemblyDefinition.MainModule.Import(constructor);
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Newobj, constructor));

            // Get method to call
            methodToCall = CecilUtilities.ResolveMethod(typeDef, "Execute", new Type[] { typeof(JoinPointContext) });
            methodToCall = visitor.TargetAssemblyDefinition.MainModule.Import(methodToCall);

            // Load the JoinPointObject as the parameter
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));

            // Do the call
            // We can safely emit a callvirt here. The JITter will make the right call.
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt, methodToCall));


            // Add nop to enable debugging
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Nop));
        }
    }
}
