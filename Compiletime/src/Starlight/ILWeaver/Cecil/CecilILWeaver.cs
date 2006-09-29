using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Diagnostics;
using System.IO;
using System.Reflection;
using System.Text;
using System.Globalization; 

using Mono.Cecil;
using Mono.Cecil.Binary;
using Mono.Cecil.Cil;
using Mono.Cecil.Metadata;
using Mono.Cecil.Signatures;

using Composestar.Repository.LanguageModel;
using Composestar.Repository;
using Composestar.StarLight.CoreServices;

namespace Composestar.StarLight.ILWeaver
{

    /// <summary>
    /// Cecil implementation of the IL Weaver
    /// </summary>
    public class CecilILWeaver : IILWeaver
    {
        TimeSpan _lastDuration;
        CecilWeaverConfiguration _configuration;
        ILanguageModelAccessor _languageModelAccessor;

        /// <summary>
        /// Initializes a new instance of the <see cref="T:CecilILWeaver"/> class.
        /// </summary>
        /// <param name="configuration">The configuration.</param>
        /// <param name="languageModelAccessor">The language model accessor.</param>
        public CecilILWeaver(CecilWeaverConfiguration configuration, ILanguageModelAccessor languageModelAccessor)
        {
            #region Check for null values

            if (configuration == null) throw new ArgumentNullException("configuration");
            if (languageModelAccessor == null) throw new ArgumentNullException("languageModelAccessor");

            #endregion

            _configuration = configuration;
            _languageModelAccessor = languageModelAccessor;

            CecilUtilities.BinFolder = _configuration.BinFolder; 

        }

        /// <summary>
        /// Gets the duration of the last executed method.
        /// </summary>
        /// <value>The last duration.</value>
        public TimeSpan LastDuration
        {
            get
            {
                return _lastDuration;
            }
        }

        /// <summary>
        /// Returns a <see cref="T:System.String"></see> that represents the current <see cref="T:System.Object"></see>.
        /// </summary>
        /// <returns>
        /// A <see cref="T:System.String"></see> that represents the current <see cref="T:System.Object"></see>.
        /// </returns>
        public override string ToString()
        {
            return "Cecil IL Weaver";
        }

       

        /// <summary>
        /// Perform the weaving actions.
        /// </summary>
        public void DoWeave()
        {
            if (!File.Exists(_configuration.InputImagePath))
            {
                throw new InvalidOperationException(string.Format(CultureInfo.CurrentCulture, Properties.Resources.InputImageNotFound, _configuration.InputImagePath));
            }

            AssemblyDefinition targetAssembly;

            try
            {
                byte[] bFile;
                FileStream f = null;
                try
                {
                    f = new FileStream(_configuration.InputImagePath, FileMode.Open);
                    bFile = CecilUtilities.ReadFully(f, -1);
                }
                finally
                {
                    if (f != null) f.Close();
                }
                                              
                targetAssembly = AssemblyFactory.GetAssembly(bFile);
                //targetAssembly = AssemblyFactory.GetAssembly(_configuration.InputImagePath);
            }
            catch (EndOfStreamException)
            {
                throw new BadImageFormatException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.ImageIsBad, _configuration.InputImagePath));
            }


            // Start timing
            Stopwatch sw = new Stopwatch();
            sw.Start();

            // Declare typeinfo and methodinfo
            TypeElement typeElement;
            MethodElement methodElement;

            // Check if the _targetAssemblyDefinition is still available
            if (targetAssembly == null)
                throw new ArgumentNullException(Properties.Resources.AssemblyNotOpen);

            // Lets walk over all the modules in the assembly
            foreach (ModuleDefinition module in targetAssembly.Modules)
            {
                // Walk over each type in the module
                foreach (TypeDefinition type in module.Types)
                {
                    // Get the information from the repository about this type
                    typeElement = _languageModelAccessor.GetTypeElement(type.FullName);
                    // Skip this type if we do not have information about it 
                    if (typeElement == null)
                        continue;

                    // Add the externals and internals
                    WeaveExternals(targetAssembly, type, typeElement);
                    WeaveInternals(targetAssembly, type, typeElement);

                    foreach (MethodDefinition method in type.Methods)
                    {
                        // Get the methodinfo
                        methodElement = _languageModelAccessor.GetMethodElementBySignature(typeElement, method.ToString());

                        // Skip if there is no methodinfo
                        if (methodElement == null)
                            continue;

                        WeaveMethod(targetAssembly, method, methodElement);
                    }

                    //Import the modifying type into the AssemblyDefinition
                    module.Import(type);
                }
            }

            //Save the modified assembly
            try
            {
                AssemblyFactory.SaveAssembly(targetAssembly, _configuration.OutputImagePath);
            }
            catch (Exception ex)
            {
                throw new ILWeaverException(String.Format(Properties.Resources.CouldNotSaveAssembly,  _configuration.OutputImagePath), _configuration.OutputImagePath, ex);
            }

            // Stop timing
            sw.Stop();
            _lastDuration = sw.Elapsed;
        }

        /// <summary>
        /// Weaves the internals.
        /// </summary>
        /// <param name="targetAssembly">The target assembly.</param>
        /// <param name="type">The type.</param>
        /// <param name="typeElement">The type information.</param>
        public void WeaveInternals(AssemblyDefinition targetAssembly, TypeDefinition type, TypeElement typeElement)
        {
            #region Check for null
            if (targetAssembly == null)
                throw new ArgumentNullException("targetAssembly");

            if (type == null)
                throw new ArgumentNullException("type");

            if (typeElement == null)
                throw new ArgumentNullException("typeElement");

            #endregion

            #region Get the internals

            IList<Internal> internals = _languageModelAccessor.GetInternalsByTypeElement(typeElement);

            if (internals == null | internals.Count == 0)
                return;

            #endregion

            FieldDefinition internalDef;
            Type internalType;
            TypeReference internalTypeRef;
            Mono.Cecil.FieldAttributes internalAttrs;

            return;

            foreach (Internal inter in internals)
            {
                internalType = Type.ReflectionOnlyGetType(inter.Type, false, false);
                if (internalType == null) throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.TypeNotFound, inter.Type));

                internalTypeRef = targetAssembly.MainModule.Import(internalType);
                if (internalTypeRef == null) throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.TypeNotFound, inter.Type));

                internalAttrs = Mono.Cecil.FieldAttributes.Private;

                // Create the field
                internalDef = new FieldDefinition(inter.Name, internalTypeRef, internalAttrs);

                // Add the field
                type.Fields.Add(internalDef);

                // Add initialization code to type constructor(s)
                if (internalType.IsClass && internalType.Name != "String" && internalType.Name != "Array")
                {
                    // Get the .ctor() constructor for the internal type
                    MethodBase constructorMethod = (MethodBase)internalType.GetConstructor(new Type[0]);
                    if (constructorMethod == null)
                        throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.ConstructorNotFound, inter.Type));
                    MethodReference constructorReference = targetAssembly.MainModule.Import(constructorMethod);

                    foreach (MethodDefinition constructor in type.Constructors)
                    {
                        if (constructor.HasBody)
                        {
                            if (constructor.Body.Instructions.Count >= 1)
                            {
                                // Gets the CilWorker of the method for working with CIL instructions
                                CilWorker worker = constructor.Body.CilWorker;
                                
                                // Create instructions
                                IList<Instruction> instructions = new List<Instruction>();
                                instructions.Add(worker.Create(OpCodes.Ldarg_0));
                                instructions.Add(worker.Create(OpCodes.Newobj, constructorReference));
                                instructions.Add(worker.Create(OpCodes.Stfld, internalDef));

                                // Add the instructions
                                int noi = InsertInstructionList(ref worker, constructor.Body.Instructions[0], instructions);
                            }
                        }
                    }
                }
            }

        }

        /// <summary>
        /// Weaves the externals.
        /// </summary>
        /// <param name="targetAssembly">The target assembly.</param>
        /// <param name="type">The type.</param>
        /// <param name="typeElement">The type information.</param>
        public void WeaveExternals(AssemblyDefinition targetAssembly, TypeDefinition type, TypeElement typeElement)
        {

            #region Check for null
            if (targetAssembly == null)
                throw new ArgumentNullException("targetAssembly");

            if (type == null)
                throw new ArgumentNullException("type");

            if (typeElement == null)
                throw new ArgumentNullException("typeElement");

            #endregion

            #region Get the externals

            IList<External> externals = _languageModelAccessor.GetExternalsByTypeElement(typeElement);

            if (externals == null | externals.Count == 0)
                return;

            #endregion

            foreach (External external in externals)
            {

            }

        }

        /// <summary>
        /// Weaves the code into the method.
        /// </summary>
        /// <param name="targetAssembly">The target assembly.</param>
        /// <param name="method">The method definition.</param>
        /// <param name="methodElement">The method element.</param>
        public void WeaveMethod(AssemblyDefinition targetAssembly, MethodDefinition method, MethodElement methodElement)
        {

            #region Check for null
            if (targetAssembly == null)
                throw new ArgumentNullException("targetAssembly");

            if (method == null)
                throw new ArgumentNullException("method");

            if (methodElement == null)
                throw new ArgumentNullException("methodElement");

            #endregion

            // Add the inputfilters
            WeaveInputFilters(targetAssembly, method, methodElement);

            // Add the outputfilters
            WeaveOutputFilters(targetAssembly, method, methodElement);

        }

        /// <summary>
        /// Weaves the input filters.
        /// </summary>
        /// <param name="targetAssembly">The target assembly.</param>
        /// <param name="method">The method.</param>
        /// <param name="methodElement">The method element.</param>
        /// <remarks>
        /// InputFilters are added at the top of the methodbody.
        /// We call a visitor to generate IL instructions and we add those to the top of the method.
        /// </remarks>
        public void WeaveInputFilters(AssemblyDefinition targetAssembly, MethodDefinition method, MethodElement methodElement)
        {
            #region Check for null and retrieve inputFilter

            if (targetAssembly == null)
                throw new ArgumentNullException("targetAssembly");

            // Only proceed when there is a message body
            if (method.HasBody == false)
                return;

            // If the methodbody is null, then return
            if (methodElement.MethodBody == null)
                return;

            // Get the input filter
            Composestar.Repository.LanguageModel.Inlining.InlineInstruction inputFilter =
                methodElement.MethodBody.InputFilter;

            // Only proceed when we have an inputfilter
            if (inputFilter == null)
                return;

            #endregion

            // Gets the CilWorker of the method for working with CIL instructions
            CilWorker worker = method.Body.CilWorker;

            // Getting the first instruction of the current method
            Instruction ins = method.Body.Instructions[0];
            
            // Add filters using the visitor
            CecilInliningInstructionVisitor visitor = new CecilInliningInstructionVisitor();
            visitor.Method = method;
            visitor.Worker = worker;
            visitor.FilterType = CecilInliningInstructionVisitor.FilterTypes.InputFilter;
            visitor.TargetAssemblyDefinition = targetAssembly;
            visitor.RepositoryAccess = _languageModelAccessor;

            // Visit the elements in the block
            try
            {
                ((Composestar.Repository.LanguageModel.Inlining.Visitor.IVisitable)inputFilter).Accept(visitor);
            }
            catch (Exception ex)
            {
                // Close the database and throw the error wrapped in an ILWeaverException
                throw new ILWeaverException(Properties.Resources.CecilVisitorRaisedException, _configuration.OutputImagePath, ex);
            }

            // Only add instructions if we have instructions
            if (visitor.Instructions.Count > 0)
            {
                // Add the instructions
                int instructionsCount = 0;
                instructionsCount += InsertInstructionList(ref worker, ins, visitor.Instructions);
            }

            //
            // What follows are the original instructions
            //

        }

        /// <summary>
        /// Weaves the output filters.
        /// </summary>
        /// <param name="targetAssembly">The target assembly.</param>
        /// <param name="method">The method.</param>
        /// <param name="methodElement">The method element.</param>
        /// <remarks>
        /// We look for each call and see if we have an outputfilter for it.
        /// If we do, then call the visitor to generate code to replace the call.
        /// </remarks>
        public void WeaveOutputFilters(AssemblyDefinition targetAssembly, MethodDefinition method, MethodElement methodElement)
        {
            #region Check for null and retrieve calls for this method
            
            if (targetAssembly == null)
                throw new ArgumentNullException("targetAssembly");
            
            if (methodElement.MethodBody == null)
                return;

            // Only proceed when there is a message body
            if (method.HasBody == false)
                return;

            IList<CallElement> calls = _languageModelAccessor.GetCallByMethodElement(methodElement);

            if (calls == null | calls.Count == 0)
                return;

            #endregion

            // Gets the CilWorker of the method for working with CIL instructions
            CilWorker worker = method.Body.CilWorker;

            // Loop through all the instructions
            foreach (Instruction instruction in method.Body.Instructions)
            {
                // Check for a call instruction
                if (IsCallInstruction(instruction))
                {
                    // Find the corresponding call in the list of calls
                    MethodReference mr = (MethodReference)(instruction.Operand);
                    CallElement call = FindCallInList(calls, mr.ToString());

                    // If we found a CallElement in the repository, then see if we have to perform weaving
                    if (call != null)
                    {
                        // Get the outputFilter for this call
                        Composestar.Repository.LanguageModel.Inlining.InlineInstruction outputFilter =
                            call.OutputFilter;

                        // Check for null, an output filter is not required
                        if (outputFilter == null)
                            continue;

                        // Add filters using the visitor
                        CecilInliningInstructionVisitor visitor = new CecilInliningInstructionVisitor();
                        visitor.Method = method;
                        visitor.Worker = worker;
                        visitor.FilterType = CecilInliningInstructionVisitor.FilterTypes.OutputFilter;
                        visitor.TargetAssemblyDefinition = targetAssembly;

                        // Visit the elements in the block
                        try
                        {
                            outputFilter.Accept(visitor);
                        }
                        catch (Exception ex)
                        {
                            throw new ILWeaverException(Properties.Resources.CecilVisitorRaisedException, _configuration.OutputImagePath, ex);
                        }

                        // Only add instructions if we have instructions
                        if (visitor.Instructions.Count > 0)
                        {
                            int instructionsCount = 0;
                            // Add the instructions
                            instructionsCount += ReplaceAndInsertInstructionList(ref worker, instruction, visitor.Instructions);
                        }
                    }
                }
            }
        }

        /// <summary>
        /// Closes this instance.
        /// </summary>
        public void Close()
        {
            _languageModelAccessor.Close();
        }

        #region Helper functions

        /// <summary>
        /// Inserts the instruction list after a specified instruction.
        /// </summary>
        /// <param name="worker">The worker.</param>
        /// <param name="startInstruction">The start instruction.</param>
        /// <param name="instructionsToAdd">The instructions to add.</param>
        /// <returns>The number of instructions inserted.</returns>
        private int InsertInstructionList(ref CilWorker worker, Instruction startInstruction, IList<Instruction> instructionsToAdd)
        {
            foreach (Instruction instr in instructionsToAdd)
            {
                worker.InsertAfter(startInstruction, instr);
                startInstruction = instr;
            }

            return instructionsToAdd.Count;
        }

        /// <summary>
        /// Replaces the startinstruction and insert instruction list.
        /// </summary>
        /// <param name="worker">The worker.</param>
        /// <param name="startInstruction">The start instruction.</param>
        /// <param name="instructionsToAdd">The instructions to add.</param>
        /// <returns></returns>
        private int ReplaceAndInsertInstructionList(ref CilWorker worker, Instruction startInstruction, IList<Instruction> instructionsToAdd)
        {
            bool first = true;
            foreach (Instruction instr in instructionsToAdd)
            {
                if (first)
                {
                    worker.Replace(startInstruction, instr);
                    first = false;
                }
                else
                {
                    worker.InsertAfter(startInstruction, instr);
                }
                startInstruction = instr;
            }

            return instructionsToAdd.Count - 1;
        }

        /// <summary>
        /// Finds the call in list.
        /// </summary>
        /// <param name="calls">The calls.</param>
        /// <param name="callTo">The call to.</param>
        /// <returns></returns>
        private CallElement FindCallInList(IList<CallElement> calls, string callTo)
        {
            foreach (CallElement call in calls)
            {
                if (call.MethodReference.Equals(callTo, StringComparison.CurrentCultureIgnoreCase))
                    return call;
            }
            return null;
        }

        /// <summary>
        /// Determines whether the instruction is a method call instruction.
        /// </summary>
        /// <param name="instruction">The instruction.</param>
        /// <returns>
        /// 	<c>true</c> if the specified instruction is a method call instruction; otherwise, <c>false</c>.
        /// </returns>
        private bool IsCallInstruction(Instruction instruction)
        {
            return (instruction.OpCode == OpCodes.Call |
                    instruction.OpCode == OpCodes.Calli |
                    instruction.OpCode == OpCodes.Callvirt);
        }
        #endregion

    }
}
