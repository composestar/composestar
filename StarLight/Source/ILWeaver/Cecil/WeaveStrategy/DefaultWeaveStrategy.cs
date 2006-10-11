using System;
using System.Collections.Generic;
using System.Globalization;
using System.Text;

using Mono.Cecil;
using Mono.Cecil.Cil;

using Composestar.StarLight.CoreServices.Exceptions;

using Composestar.Repository.LanguageModel;
using Composestar.Repository.LanguageModel.Inlining;

using Composestar.StarLight.ContextInfo;

namespace Composestar.StarLight.ILWeaver
{
    /// <summary>
    /// TODO generate comment
    /// </summary>
    class DefaultWeaveStrategy : FilterActionWeaveStrategy
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
        /// Generate the code which has to be inserted at the place of the filter specified by the visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        /// <param name="filterAction">The filter action.</param>
        /// <param name="originalCall">The original call.</param>
        public override void Weave(CecilInliningInstructionVisitor visitor, FilterAction filterAction,
            MethodDefinition originalCall)
        {
            MethodReference methodToCall;

            // Get the methodReference
            MethodReference methodReference = (MethodReference) originalCall;
            TypeDefinition parentType = CecilUtilities.ResolveTypeDefinition(methodReference.DeclaringType);

            // Create JoinPointContext
            VariableDefinition jpcVar = WeaveStrategyUtilities.CreateJoinPointContext(visitor, methodReference, true);

            // Create FilterAction object:
            TypeElement typeElement = visitor.RepositoryAccess.GetTypeElement(filterAction.FullName);
            TypeReference typeRef =
                CecilUtilities.ResolveType(filterAction.FullName, typeElement.Assembly, null);
            TypeDefinition typeDef =
                CecilUtilities.ResolveTypeDefinition(typeRef);
            MethodDefinition constructor = typeDef.Constructors.GetConstructor(false, new Type[0]);
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Newobj, constructor));

            // Get method to call
            methodToCall = typeDef.Methods.GetMethod("Execute")[0];

            // Load the JoinPointObject as the parameter
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));

            // Do the call
            // We can safely emit a callvirt here. The JITter will make the right call.
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt, methodToCall));

            // Restore the JoinPointContext:
            WeaveStrategyUtilities.RestoreJoinPointContext(visitor, methodReference, jpcVar, true);


            // Add nop to enable debugging
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Nop));
        }
    }
}
