using System;
using System.Collections.Generic;
using System.Reflection;
using System.Text;

using Mono.Cecil;
using Mono.Cecil.Cil;

using Composestar.Repository.LanguageModel.Inlining;
using Composestar.StarLight.ContextInfo;

namespace Composestar.StarLight.ILWeaver
{
    /// <summary>
    /// TODO generate comment
    /// </summary>
    class WeaveStrategyUtilities
    {
        /// <summary>
        /// Weaves the creation and initialization of the JoinPointContext object.
        /// </summary>
        /// <param name="originalCall">The originaly called method</param>
        /// <returns>The VariableDefinition of the JoinPointContext object</returns>
        internal static VariableDefinition CreateJoinPointContext(
            CecilInliningInstructionVisitor visitor,
            MethodReference originalCall, bool storeReturnValue)
        {
            // Create a new or use an existing local variable for the JoinPointContext
            VariableDefinition jpcVar = visitor.CreateJoinPointContextLocal();
            visitor.Method.Body.InitLocals = true;

            //
            // Create new joinpointcontext object
            //
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Newobj, 
                visitor.CreateMethodReference(typeof(JoinPointContext).GetConstructors()[0])));

            // Store the just created joinpointcontext object
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Stloc, jpcVar));

            // Store returnvalue
            if(storeReturnValue && !originalCall.ReturnType.ReturnType.FullName.Equals(CecilUtilities.VoidType))
            {
                // Get returnvalue field
                VariableDefinition returnValueVar = visitor.CreateReturnValueLocal(originalCall.ReturnType.ReturnType);

                // Load joinpointcontext object
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));

                // Load returnvalue
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, returnValueVar));

                // Check if returnvalue is value type, then box
                if(originalCall.ReturnType.ReturnType.IsValueType)
                {
                    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Box, originalCall.ReturnType.ReturnType));
                }

                // Determine type
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt, 
                    visitor.CreateMethodReference(typeof(System.Object).GetMethod("GetType", new Type[] { }))));

                // Call set_ReturnType in JoinPointContext
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt, visitor.CreateMethodReference(
                    typeof(JoinPointContext).GetMethod("set_ReturnType", new Type[] { typeof(System.Type) }))));

                // Load joinpointcontext object
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));

                // Again load the returnvalue
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, returnValueVar));

                // Check if returnvalue is value type, then box
                if(originalCall.ReturnType.ReturnType.IsValueType)
                {
                    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Box, originalCall.ReturnType.ReturnType));
                }

                // Call set_ReturnValue in JoinPointContext
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt, visitor.CreateMethodReference(
                    typeof(JoinPointContext).GetMethod("set_ReturnValue", new Type[] { typeof(object) }))));
            }


            //
            // Set the target
            //
            // Load the joinpointcontext object
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));

            // Load the this pointer
            if(visitor.Method.HasThis)
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldarg, visitor.Method.This));
            else
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldnull));

            // Assign to the Target property
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt,
                visitor.CreateMethodReference(typeof(JoinPointContext).GetMethod("set_Target", new Type[] { typeof(object) }))));

            int numberOfArguments = 0;

            switch(visitor.FilterType)
            {
                case CecilInliningInstructionVisitor.FilterTypes.InputFilter:
                    numberOfArguments = visitor.Method.Parameters.Count;
                    break;
                case CecilInliningInstructionVisitor.FilterTypes.OutputFilter:
                    //numberOfArguments = methodBaseDef.GetParameters().Length;
                    //// Also set the sender
                    //// Load the joinpointcontext object
                    //Instructions.Add(Worker.Create(OpCodes.Ldloc, jpcVar));

                    //// Load the this pointer
                    //if (Method.HasThis)
                    //    Instructions.Add(Worker.Create(OpCodes.Ldarg, Method.This));
                    //else
                    //    Instructions.Add(Worker.Create(OpCodes.Ldnull));

                    //// Assign to the Target property
                    //Instructions.Add(Worker.Create(OpCodes.Callvirt, CreateMethodReference(typeof(JoinPointContext).GetMethod("set_Sender", new Type[] { typeof(object) }))));

                    break;
            }

            //
            // Add the arguments, these are stored at the top of the stack
            //
            if(numberOfArguments > 0)
            {
                foreach(ParameterDefinition param in visitor.Method.Parameters)
                {
                    //methodReference.Parameters[ i ].Attributes = Mono.Cecil.ParamAttributes.Out;                    

                    // Load jpc
                    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));

                    // Load the ordinal
                    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldc_I4, param.Sequence));

                    // load the argument 
                    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldarg, param));

                    // Check if parameter is value type, then box
                    if(param.ParameterType.IsValueType)
                    {
                        visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Box, param.ParameterType));
                    }

                    // Determine type
                    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt,
                        visitor.CreateMethodReference(typeof(System.Object).GetMethod("GetType", new Type[] { }))));

                    // Again load the argument
                    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldarg, param));

                    // Check if parameter is value type, then box
                    if(param.ParameterType.IsValueType)
                    {
                        visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Box, param.ParameterType));
                    }

                    // Call the AddArgument function
                    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt,
                        visitor.CreateMethodReference(typeof(JoinPointContext).GetMethod("AddArgument", new Type[] { typeof(Int16), typeof(System.Type), typeof(object) }))));
                }
            }


            //
            // Set the selector
            //

            // Load joinpointcontext first
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));

            // Load the name onto the stack
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldstr, originalCall.Name));

            // Assign name to MethodName
            visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt,
                visitor.CreateMethodReference(typeof(JoinPointContext).GetMethod("set_MethodName", new Type[] { typeof(string) }))));

            return jpcVar;
        }

        /// <summary>
        /// Sets back the arguments and the returnvalue (for filteractions on return).
        /// </summary>
        /// <param name="originalMethod">MethodReference to the original method</param>
        /// <param name="jpcVar">VariableDefinition containing the JoinPointContext object</param>
        internal static void RestoreJoinPointContext(CecilInliningInstructionVisitor visitor,
            MethodReference originalMethod, VariableDefinition jpcVar, bool storeReturnValue)
        {

            int numberOfArguments = originalMethod.Parameters.Count;

            //
            // Retrieve the arguments
            //               
            foreach(ParameterDefinition param in originalMethod.Parameters)
            {
                // Load jpc
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));

                // Load the ordinal
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldc_I4, param.Sequence));

                // Call the GetArgumentValue(int16) function    
                MethodInfo t = typeof(JoinPointContext).GetMethod("GetArgumentValue", new Type[] { typeof(Int16) });
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt, visitor.CreateMethodReference(t)));

                // TODO Call the generic GetArgumentValue function to get directly the right type.
                // Problem is how to call the function
                //MethodReference mr = CecilUtilities.ResolveMethod("GetGenericArgumentValue", "Composestar.StarLight.ContextInfo.JoinPointContext", "Composestar.StarLight.ContextInfo");

                // Check if parameter is value type, then unbox
                if(param.ParameterType.IsValueType)
                {
                    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Unbox_Any, param.ParameterType));
                }
                else
                {
                    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Castclass, param.ParameterType));
                }

                // Store argument
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Starg_S, param));
            }

            // Retrieve returnvalue
            if(storeReturnValue && !visitor.Method.ReturnType.ReturnType.FullName.Equals(CecilUtilities.VoidType))
            {
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Ldloc, jpcVar));

                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Callvirt, visitor.CreateMethodReference(
                    typeof(JoinPointContext).GetMethod("get_ReturnValue", new Type[] { }))));

                // Check if returnvalue is value type, then unbox, else cast
                if(originalMethod.ReturnType.ReturnType.IsValueType)
                {
                    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Unbox_Any, originalMethod.ReturnType.ReturnType));
                }
                else
                {
                    visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Castclass, visitor.Method.ReturnType.ReturnType));
                }

                // Store the returnvalue
                VariableDefinition returnValueVar = visitor.CreateReturnValueLocal(originalMethod.ReturnType.ReturnType);
                visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Stloc, returnValueVar));
            }
        }
    }
}
