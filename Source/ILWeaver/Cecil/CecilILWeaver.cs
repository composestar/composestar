#region Using directives
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

using Composestar.StarLight.Entities.WeaveSpec;
using Composestar.StarLight.Entities.WeaveSpec.Instructions;
using Composestar.StarLight.Entities.LanguageModel; 

using Composestar.Repository;

using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions; 
#endregion

namespace Composestar.StarLight.ILWeaver
{

    /// <summary>
    /// Cecil implementation of the IL Weaver.
    /// </summary>
    public class CecilILWeaver : IILWeaver
    {

        private TimeSpan m_LastDuration;
        private CecilWeaverConfiguration _configuration;
        private IEntitiesAccessor _entitiesAccessor;
        private int _internalsAdded;
        private int _externalsAdded;
        private int _outputFiltersAdded;
        private int _inputFiltersAdded;
        private bool _typeChanged;

        /// <summary>
        /// Initializes a new instance of the <see cref="T:CecilILWeaver"/> class.
        /// </summary>
        /// <param name="configuration">The configuration.</param>
        /// <param name="entitiesAccessor">The entities accessor.</param>
        public CecilILWeaver(CecilWeaverConfiguration configuration, IEntitiesAccessor entitiesAccessor)
        {
            #region Check for null values

            if (configuration == null) throw new ArgumentNullException("configuration");
            if (entitiesAccessor == null) throw new ArgumentNullException("entitiesAccessor");

            #endregion

            _configuration = configuration;
            _entitiesAccessor = entitiesAccessor;

            CecilUtilities.BinFolder = _configuration.BinFolder;

        }

        /// <summary>
        /// Gets the duration of the last executed method.
        /// </summary>
        /// <value>The last duration.</value>
        TimeSpan IILWeaver.LastDuration
        {
            get
            {
                return m_LastDuration;
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
        void IILWeaver.DoWeave()
        {
            // Check for the existens of the file
            if (!File.Exists(_configuration.InputImagePath))
            {
                throw new FileNotFoundException(string.Format(CultureInfo.CurrentCulture, Properties.Resources.InputImageNotFound, _configuration.InputImagePath));
            }

            // Reset counters
            _internalsAdded = 0;
            _externalsAdded = 0;
            _outputFiltersAdded = 0;
            _inputFiltersAdded = 0;

            // Start timing
            Stopwatch sw = new Stopwatch();
            sw.Start();

            // Load the file
            AssemblyDefinition targetAssembly;

            try
            {
                byte[] binaryFile;
                FileStream fileStream = null;
                try
                {
                    fileStream = new FileStream(_configuration.InputImagePath, FileMode.Open);
                    binaryFile = CecilUtilities.ReadFully(fileStream, -1);

                } // try
                catch (Exception ex)
                {
                    throw new ILWeaverException(String.Format(Properties.Resources.CouldNotLoadAssembly, _configuration.InputImagePath, ex.Message), ex); 

                } // catch
                finally
                {
                    if (fileStream != null) fileStream.Close();

                } // finally

                // We use a byte array to read the file, so we can close it after reading and can write to it again.  
                targetAssembly = AssemblyFactory.GetAssembly(binaryFile);
                binaryFile = null;
                 
            }
            catch (EndOfStreamException)
            {
                throw new BadImageFormatException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.ImageIsBad, _configuration.InputImagePath));
            }

            // Check if the _targetAssemblyDefinition is still available
            if (targetAssembly == null)
                throw new ArgumentNullException(Properties.Resources.AssemblyNotOpen);             
            
            // Prepare the data for this assembly (precaching)
            WeaveSpecification weaveSpec;

            // Get the weave specification
            weaveSpec = _entitiesAccessor.LoadWeaveSpecification(_configuration.AssemblyConfiguration.WeaveSpecificationFile);

            if (weaveSpec == null)
                throw new ILWeaverException(String.Format(Properties.Resources.WeavingSpecNotFound, _configuration.AssemblyConfiguration.WeaveSpecificationFile, _configuration.AssemblyConfiguration.Name));
        
            // If empty, we can quit
            if (weaveSpec.WeaveTypes.Count == 0)
            {
                // Stop timing
                sw.Stop();
                m_LastDuration = sw.Elapsed;

                // Stop the execution
                return;
            } // if

            // Get only the types we have info for
            foreach (WeaveType weaveType in weaveSpec.WeaveTypes)
            {
                TypeDefinition type = targetAssembly.MainModule.Types[weaveType.Name];
                if (type == null)
                    continue;

                _typeChanged = false;

                // Get and add the externals for this type
                if (weaveType.Externals.Count > 0)
                    WeaveExternals(targetAssembly, type, weaveType, weaveType.Externals);

                // Get and add the internals for this type
                if (weaveType.Internals.Count > 0)
                    WeaveInternals(targetAssembly, type, weaveType, weaveType.Internals);

                if (weaveType.Methods.Count > 0)
                {
                    // Loop through all the methods
                    foreach (MethodDefinition method in type.Methods)
                    {

                        // Get the methodinfo based on the signature
                        WeaveMethod weaveMethod = GetMethodFromList(weaveType.Methods, method.ToString());

                        // Skip if there is no weaveMethod
                        if (weaveMethod == null)
                            continue;

                        WeaveMethod(targetAssembly, method, weaveMethod);

                    } // foreach  (method)
                } // if

                // Import the changed type into the AssemblyDefinition
                if (_typeChanged) targetAssembly.MainModule.Import(type);

            } // foreach  (typeElement)
  
            // Save the modified assembly only if it is changed.
            if (_inputFiltersAdded > 0 || _outputFiltersAdded > 0 || _internalsAdded > 0 || _externalsAdded > 0)
            {
                try
                {
                    AssemblyFactory.SaveAssembly(targetAssembly, _configuration.OutputImagePath);
                } // try
                catch (Exception ex)
                {
                    throw new ILWeaverException(String.Format(Properties.Resources.CouldNotSaveAssembly, _configuration.OutputImagePath), _configuration.OutputImagePath, ex);
                } // catch

                // TODO remove debugging info, maybe convert to some sort of diagnostic system?
                Console.WriteLine("Added: {0} internals, {1} externals, {2} inputfilters, {3} outputfilters", _internalsAdded, _externalsAdded, _inputFiltersAdded, _outputFiltersAdded);
            } // if

            // Stop timing
            sw.Stop();
            m_LastDuration = sw.Elapsed;
        }

        /// <summary>
        /// Weaves the internals.
        /// </summary>
        /// <param name="targetAssembly">The target assembly.</param>
        /// <param name="type">The type.</param>
        /// <param name="weaveType">Type of the weave.</param>
        /// <param name="internals">The internals.</param>
        private void WeaveInternals(AssemblyDefinition targetAssembly, TypeDefinition type, WeaveType weaveType, List<Internal> internals)
        {
            #region Check the internals

            if (internals == null | internals.Count == 0)
                return;

            #endregion

            #region Check for null
            if (targetAssembly == null)
                throw new ArgumentNullException("targetAssembly");

            if (type == null)
                throw new ArgumentNullException("type");

            if (weaveType == null)
                throw new ArgumentNullException("weaveType");

            #endregion
                     
            FieldDefinition internalDef;
            TypeReference internalTypeRef;
            Mono.Cecil.FieldAttributes internalAttrs;

            foreach (Internal inter in internals)
            {
                String internalTypeString = String.Format("{0}.{1}", inter.NameSpace, inter.Type);

                TypeElement internalTypeElement = _entitiesAccessor.GetTypeElement(internalTypeString);
                if (internalTypeElement == null) throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.TypeNotFound, internalTypeString + " (step 1)"));

                internalTypeRef = CecilUtilities.ResolveType(internalTypeString, internalTypeElement.Assembly, internalTypeElement.FromDLL);
                if (internalTypeRef == null) throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.TypeNotFound, internalTypeString + " (step 2)"));

                internalAttrs = Mono.Cecil.FieldAttributes.Private;

                internalTypeRef = targetAssembly.MainModule.Import(internalTypeRef);

                // Create the field
                internalDef = new FieldDefinition(inter.Name, internalTypeRef, internalAttrs);

                // Add the field
                type.Fields.Add(internalDef);

                // Increase the number of internals
                _internalsAdded++;

                // Add initialization code to type constructor(s)
                if (internalTypeElement.IsClass && internalTypeElement.Name != "String" && internalTypeElement.Name != "Array")
                {
                    // Get the .ctor() constructor for the internal type
                    TypeDefinition internalTypeDef = CecilUtilities.ResolveTypeDefinition(internalTypeRef);
                    MethodDefinition internalConstructor = internalTypeDef.Constructors.GetConstructor(false, new Type[0]);
                    if (internalConstructor == null)
                        throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.ConstructorNotFound, internalTypeString));

                    // Initialize internal in every constructor of the parent type
                    foreach (MethodDefinition constructor in type.Constructors)
                    {
                        if (constructor.HasBody && !constructor.IsStatic && !constructor.ExplicitThis)
                        {
                            if (constructor.Body.Instructions.Count >= 1)
                            {
                                // Gets the CilWorker of the method for working with CIL instructions
                                CilWorker worker = constructor.Body.CilWorker;

                                // Create instructions
                                IList<Instruction> instructions = new List<Instruction>();
                                instructions.Add(worker.Create(OpCodes.Ldarg_0));
                                instructions.Add(worker.Create(OpCodes.Newobj, internalConstructor));
                                instructions.Add(worker.Create(OpCodes.Stfld, internalDef));

                                // Add the instructions
                                int noi = InsertBeforeInstructionList(ref worker, constructor.Body.Instructions[0], instructions);
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
        /// <param name="weaveType">Type of the weave.</param>
        /// <param name="externals">The externals.</param>
        private void WeaveExternals(AssemblyDefinition targetAssembly, TypeDefinition type, WeaveType weaveType, List<External> externals)
        {
            #region Check the externals

            if (externals == null | externals.Count == 0)
                return;

            #endregion

            #region Check for null
            if (targetAssembly == null)
                throw new ArgumentNullException("targetAssembly");

            if (type == null)
                throw new ArgumentNullException("type");

            if (weaveType == null)
                throw new ArgumentNullException("weaveType");

            #endregion
          
            FieldDefinition externalDef;
            TypeReference externalTypeRef;
            Mono.Cecil.FieldAttributes externalAttrs;

            foreach (External external in externals)
            {
                TypeElement externalTypeElement = _entitiesAccessor.GetTypeElement(external.Type);
                if (externalTypeElement == null) throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.TypeNotFound, external.Type + " (step 1)"));


                externalTypeRef = CecilUtilities.ResolveType(external.Type, externalTypeElement.Assembly, externalTypeElement.FromDLL);
                if (externalTypeRef == null) throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.TypeNotFound, external.Type + " (step 2)"));

                externalAttrs = Mono.Cecil.FieldAttributes.Private;

                externalTypeRef = targetAssembly.MainModule.Import(externalTypeRef);

                // Create the field
                externalDef = new FieldDefinition(external.Name, externalTypeRef, externalAttrs);
                
                // Add the field
                type.Fields.Add(externalDef);

                // Increase the number of externals
                _externalsAdded++;

                // Get the method referenced by the external
                TypeElement initTypeElement = _entitiesAccessor.GetTypeElement(String.Format("{0}.{1}", external.Reference.NameSpace, external.Reference.Target));
                if (initTypeElement == null) throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.TypeNotFound, String.Format("{0}.{1}", external.Reference.NameSpace, external.Reference.Target)));

                MethodDefinition initMethodDef = (MethodDefinition)CecilUtilities.ResolveMethod(external.Reference.Selector, initTypeElement.FullName, initTypeElement.Assembly, initTypeElement.FromDLL);
                if (initMethodDef == null) throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.MethodNotFound, external.Reference.Selector, external.Reference.Target, externalTypeElement.Assembly));

                MethodReference initMethodRef = targetAssembly.MainModule.Import(initMethodDef);

                // Initialize external in every constructor of the parent type
                foreach (MethodDefinition constructor in type.Constructors)
                {
                    if (constructor.HasBody && !constructor.IsStatic && !constructor.ExplicitThis)
                    {
                        if (constructor.Body.Instructions.Count >= 1)
                        {
                            // Gets the CilWorker of the method for working with CIL instructions
                            CilWorker worker = constructor.Body.CilWorker;

                            // Create instructions
                            IList<Instruction> instructions = new List<Instruction>();
                            instructions.Add(worker.Create(OpCodes.Ldarg_0));
                            instructions.Add(worker.Create(OpCodes.Call, initMethodRef));
                            instructions.Add(worker.Create(OpCodes.Stfld, externalDef));

                            // Add the instructions
                            int noi = InsertBeforeInstructionList(ref worker, constructor.Body.Instructions[0], instructions);
                        }
                    }
                }
            }

        }

        /// <summary>
        /// Weaves the code into the method.
        /// </summary>
        /// <param name="targetAssembly">The target assembly.</param>
        /// <param name="method">The method definition.</param>
        /// <param name="weaveMethod">The weave method.</param>
        private void WeaveMethod(AssemblyDefinition targetAssembly, MethodDefinition method, WeaveMethod weaveMethod)
        {

            #region Check for null
            if (targetAssembly == null)
                throw new ArgumentNullException("targetAssembly");

            if (method == null)
                throw new ArgumentNullException("method");

            if (weaveMethod == null)
                throw new ArgumentNullException("weaveMethod");

            #endregion

            // Add the inputfilters
            if (weaveMethod.HasInputFilters)
            {
                WeaveInputFilters(targetAssembly, method, weaveMethod);
            } // if

            // Add the outputfilters
            if (weaveMethod.HasOutputFilters)
            {
                WeaveOutputFilters(targetAssembly, method, weaveMethod);
            } // if
        }

        /// <summary>
        /// Weaves the input filters.
        /// </summary>
        /// <param name="targetAssembly">The target assembly.</param>
        /// <param name="method">The method we are weaving in.</param>
        /// <param name="weaveMethod">The weave method.</param>
        /// <remarks>
        /// InputFilters are added at the top of the methodbody.
        /// We call a visitor to generate IL instructions and we add those to the top of the method.
        /// </remarks>
        private void WeaveInputFilters(AssemblyDefinition targetAssembly, MethodDefinition method, WeaveMethod weaveMethod)
        {

            #region Check for null and retrieve inputFilter

            if (targetAssembly == null)
                throw new ArgumentNullException("targetAssembly");

            // Only proceed when there is a message body
            if (method.HasBody == false)
                return;

            // If the methodbody is null, then return
            if (weaveMethod.InputFilter == null)
                return;
            
            // Get the input filter
            InlineInstruction inputFilter = weaveMethod.InputFilter;
                      
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
            visitor.CalledMethod = method;
            visitor.Worker = worker;
            visitor.FilterType = CecilInliningInstructionVisitor.FilterTypes.InputFilter;
            visitor.TargetAssemblyDefinition = targetAssembly;
            visitor.RepositoryAccess = _entitiesAccessor;

            // Visit the elements in the block
            try
            {
                ((Composestar.StarLight.Entities.WeaveSpec.Instructions.Visitor.IVisitable)inputFilter).Accept(visitor);
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
                instructionsCount += InsertBeforeInstructionList(ref worker, ins, visitor.Instructions);

                // Increase the number of inputfilters added
                _inputFiltersAdded++;
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
        private void WeaveOutputFilters(AssemblyDefinition targetAssembly, MethodDefinition method, WeaveMethod weaveMethod)
        {
            #region Check for null and retrieve calls for this method

            if (targetAssembly == null)
                throw new ArgumentNullException("targetAssembly");

            if (weaveMethod.OutputFilters.Count == 0)
                return;

            // Only proceed when there is a message body
            if (method.HasBody == false)
                return;
                        
            #endregion

            // Gets the CilWorker of the method for working with CIL instructions
            CilWorker worker = method.Body.CilWorker;

            // Loop through all the instructions
            List<Instruction> callInstructions = new List<Instruction>();
            foreach (Instruction instruction in method.Body.Instructions)
            {
                // Check for a call instruction
                if (IsCallInstruction(instruction))
                {
                    callInstructions.Add(instruction);
                }
            }

            foreach(Instruction instruction in callInstructions)
            {
                // Find the corresponding call in the list of calls
                MethodReference mr = (MethodReference) (instruction.Operand);
                MethodDefinition md = CecilUtilities.ResolveMethodDefinition(mr);
                InlineInstruction outputFilter = weaveMethod.OutputFilters[md.ToString()];

                // If we found an outputFilter in the repository, then see if we have to perform weaving
                if (outputFilter != null)
                {                   
                    // Add filters using the visitor
                    CecilInliningInstructionVisitor visitor = new CecilInliningInstructionVisitor();
                    visitor.Method = method;
                    visitor.CalledMethod = md;
                    visitor.Worker = worker;
                    visitor.FilterType = CecilInliningInstructionVisitor.FilterTypes.OutputFilter;
                    visitor.TargetAssemblyDefinition = targetAssembly;

                    // Visit the elements in the block
                    try
                    {
                        outputFilter.Accept(visitor);
                    }
                    catch(Exception ex)
                    {
                        throw new ILWeaverException(Properties.Resources.CecilVisitorRaisedException, _configuration.OutputImagePath, ex);
                    }

                    // Only add instructions if we have instructions
                    if(visitor.Instructions.Count > 0)
                    {
                        int instructionsCount = 0;
                        // Add the instructions
                        instructionsCount += ReplaceAndInsertInstructionList(ref worker, instruction, visitor.Instructions);

                        // Increase the number of outputfilters added
                        _outputFiltersAdded++;
                    }
                }

            }
        }

        /// <summary>
        /// Closes this instance.
        /// </summary>
        void IILWeaver.Close()
        {
            
        }

        #region Helper functions
             
        /// <summary>
        /// Inserts the instruction list before the start instruction.
        /// </summary>
        /// <param name="worker">The worker.</param>
        /// <param name="startInstruction">The start instruction.</param>
        /// <param name="instructionsToAdd">The instructions to add.</param>
        /// <returns></returns>
        private int InsertBeforeInstructionList(ref CilWorker worker, Instruction startInstruction, IList<Instruction> instructionsToAdd)
        {
            foreach (Instruction instr in instructionsToAdd)
            {
                worker.InsertBefore(startInstruction, instr);
            }

            return instructionsToAdd.Count;
        }

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
        /// Gets the method from list.
        /// </summary>
        /// <param name="list">The list.</param>
        /// <param name="signature">The signature.</param>
        /// <returns></returns>
        public WeaveMethod GetMethodFromList(List<WeaveMethod> list, String signature)
        {
            foreach (WeaveMethod method in list)
            {
                if (method.Signature.Equals(signature))
                    return method;
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
