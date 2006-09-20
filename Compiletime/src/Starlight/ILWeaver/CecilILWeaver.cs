using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Diagnostics;
using System.IO;
using System.Reflection;
using System.Text;

using Mono.Cecil;
using Mono.Cecil.Binary;
using Mono.Cecil.Cil;
using Mono.Cecil.Metadata;
using Mono.Cecil.Signatures;

using Composestar.Repository.LanguageModel;
using Composestar.Repository;

namespace Composestar.StarLight.ILWeaver
{

    /// <summary>
    /// Cecil implementation of the IL Weaver
    /// </summary>
    public class CecilILWeaver : IILWeaver
    {
        private AssemblyDefinition _targetAssemblyDefinition;
        private WeaverConfiguration _configuration;
        private RepositoryAccess _repositoryAccess;

        private bool _isInitialized = false;
        private TimeSpan _lastDuration = TimeSpan.MinValue;

        /// <summary>
        /// Initializes the analyzer with the specified assembly name.
        /// </summary>
        /// <param name="fileName">Name of the file.</param>
        /// <param name="config">The config.</param>
        public void Initialize(string inputImage, NameValueCollection config)
        {
            #region fileName
            if (String.IsNullOrEmpty(inputImage))
                throw new ArgumentNullException("inputImage", Properties.Resources.FileNameNullOrEmpty);

            if (!File.Exists(inputImage))
                throw new ArgumentException(String.Format(Properties.Resources.FileNotFound, inputImage), "inputImage");

            try
            {
                _targetAssemblyDefinition = AssemblyFactory.GetAssembly(inputImage);
            }
            catch (EndOfStreamException)
            {
                throw new BadImageFormatException(String.Format(Properties.Resources.ImageIsBad, inputImage));
            }
            #endregion

            #region config

            if (null == config)
            {
                _configuration = WeaverConfiguration.CreateDefaultConfiguration(inputImage);
            }
            else
            {
                string outputImagePath = config.Get("OutputImagePath");
                string shouldSignAssembly = config.Get("ShouldSignAssembly");
                string outputImageSNK = config.Get("OutputImageSNK");
                string outputFilename = config.Get("OutputFilename");

                if (string.IsNullOrEmpty(outputImagePath))
                {
                    outputImagePath = Path.GetFullPath(inputImage);
                }

                if (!string.IsNullOrEmpty(shouldSignAssembly))
                {
                    bool shouldSignAssemblyB = false;
                    Boolean.TryParse(shouldSignAssembly, out shouldSignAssemblyB);

                    if (shouldSignAssemblyB)
                    {
                        if (string.IsNullOrEmpty(outputImageSNK))
                        {
                            throw new ArgumentException(Properties.Resources.NoSNKSpecified, "config");
                        }

                        if (File.Exists(outputImageSNK))
                        {
                            throw new ArgumentException(string.Format(Properties.Resources.SNKFileNotFound, outputImageSNK), "config");
                        }
                        _configuration = new WeaverConfiguration(outputImagePath, shouldSignAssemblyB, outputImageSNK);
                    }
                    else
                    {
                        _configuration = new WeaverConfiguration(outputImagePath, false, string.Empty);
                    }
                }

                if (String.IsNullOrEmpty(outputFilename))
                    _configuration.OutputFilename = Path.GetFileName(inputImage);
                else
                    _configuration.OutputFilename = outputFilename;

            }

            // Get the repositoryfilename
            string repositoryFilename = string.Empty;
            if (config != null)
            {
                repositoryFilename = config.Get("RepositoryFilename");
            }

            if (string.IsNullOrEmpty(repositoryFilename))
                throw new ArgumentException(Properties.Resources.RepositoryFilenameNotSpecified, "RepositoryFilename");

            _repositoryAccess = new RepositoryAccess(repositoryFilename);

            #endregion

            _isInitialized = true;

        }

        /// <summary>
        /// Gets the repository retriever.
        /// </summary>
        /// <value>The repository retriever.</value>
        public RepositoryAccess RepositoryAccess
        {
            get { return _repositoryAccess; }
        }

        /// <summary>
        /// Checks for initialization. Throw exception when not inited.
        /// </summary>
        private void CheckForInit()
        {
            if (!_isInitialized)
                throw new ApplicationException(Properties.Resources.NotYetInitialized);

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
            // See if we are initialized.
            CheckForInit();

            // Start timing
            Stopwatch sw = new Stopwatch();
            sw.Start();

            // Declare typeinfo and methodinfo
            TypeElement typeElement;
            MethodElement methodElement;

            // Check if the _targetAssemblyDefinition is still available
            if (_targetAssemblyDefinition == null)
                throw new ArgumentNullException(Properties.Resources.AssemblyNotOpen);

            // Lets walk over all the modules in the assembly
            foreach (ModuleDefinition module in _targetAssemblyDefinition.Modules)
            {
                // Walk over each type in the module
                foreach (TypeDefinition type in module.Types)
                {
                    // Get the information from the repository about this type
                    typeElement = RepositoryAccess.GetTypeElement(type.FullName);
                    // Skip this type if we do not have information about it 
                    if (typeElement == null)
                        continue;

                    // Add the externals and internals
                    WeaveExternals(type, typeElement);
                    WeaveInternals(type, typeElement);

                    foreach (MethodDefinition method in type.Methods)
                    {
                        // Get the methodinfo
                        methodElement = RepositoryAccess.GetMethodElement(typeElement, method.Name);
                        // Skip if there is no methodinfo
                        if (methodElement == null)
                            continue;

                        WeaveMethod(method, methodElement);
                    }

                    //Import the modifying type into the AssemblyDefinition
                    module.Import(type);
                }
            }

            //Save the modified assembly
            try
            {
                AssemblyFactory.SaveAssembly(_targetAssemblyDefinition, _configuration.OutputFile);
            }
            catch (Exception ex)
            {
                throw new ILWeaverException(Properties.Resources.CouldNotSaveAssembly, _configuration.OutputFile, ex);
            }
            
            // Stop timing
            sw.Stop();
            _lastDuration = sw.Elapsed;

            // Close DB
            _repositoryAccess.CloseDatabase();
        }

        /// <summary>
        /// Weaves the internals.
        /// </summary>
        /// <param name="type">The type.</param>
        /// <param name="typeElement">The type information.</param>
        public void WeaveInternals(TypeDefinition type, TypeElement typeElement)
        {
            #region Check for null

            if (type == null)
                return;

            if (typeElement == null)
                return;

            #endregion

            #region Get the internals

            IList<Internal> internals = _repositoryAccess.GetInternalsByTypeElement(typeElement);

            if (internals == null | internals.Count == 0)
                return;
            
            #endregion

            foreach (Internal inter in internals)
            {
                FieldDefinition internalDef;
                Type internalType;
                TypeReference internalTypeRef;
                Mono.Cecil.FieldAttributes internalAttrs;

                internalType = Type.ReflectionOnlyGetType(inter.Type, false, false);
                if (internalType == null) throw new ILWeaverException(String.Format(Properties.Resources.TypeNotFound, inter.Type));
                internalTypeRef = _targetAssemblyDefinition.MainModule.Import(internalType);
                if (internalTypeRef == null) throw new ILWeaverException(String.Format(Properties.Resources.TypeNotFound, inter.Type));

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

                    foreach (MethodDefinition constructor in type.Constructors)
                    {
                        Instruction hookInstruction;
                        if (constructor.HasBody)
                        {
                            if (constructor.Body.Instructions.Count >= 1)
                            {
                                hookInstruction = constructor.Body.Instructions[0];
                                MethodReference constructorReference = _targetAssemblyDefinition.MainModule.Import(constructorMethod);
                                constructor.Body.CilWorker.InsertBefore(hookInstruction, constructor.Body.CilWorker.Create(OpCodes.Ldarg_0));
                                constructor.Body.CilWorker.InsertBefore(hookInstruction, constructor.Body.CilWorker.Create(OpCodes.Newobj, constructorReference));
                                constructor.Body.CilWorker.InsertBefore(hookInstruction, constructor.Body.CilWorker.Create(OpCodes.Stfld, internalDef));
                            }
                        }
                    }
                }
            }

            #region Test code, must be removed
            //// Declarations
            //FieldDefinition internalDef;
            //String name;
            //TypeReference fieldType;
            //Mono.Cecil.FieldAttributes attrs;

            //// Prepare the data
            //name = "test";
            //fieldType = _targetAssemblyDefinition.MainModule.Import(typeof(String));
            //attrs = Mono.Cecil.FieldAttributes.Static;

            //// Create the field
            //internalDef = new FieldDefinition(name, fieldType, attrs);

            //// Add the field
            //type.Fields.Add(internalDef);
            #endregion
        }

        /// <summary>
        /// Weaves the externals.
        /// </summary>
        /// <param name="type">The type.</param>
        /// <param name="typeElement">The type information.</param>
        public void WeaveExternals(TypeDefinition type, TypeElement typeElement)
        {
            #region Check for null

            if (type == null)
                return;

            if (typeElement == null)
                return;

            #endregion

            #region Get the externals

            IList<External> externals = _repositoryAccess.GetExternalsByTypeElement(typeElement);

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
        /// <param name="method">The method definition.</param>
        public void WeaveMethod(MethodDefinition method, MethodElement methodElement)
        {
            #region Check for null values

            if (method == null)
                return;

            if (methodElement == null)
                return;
            
            #endregion

            // Add the inputfilters
            WeaveInputFilters(method, methodElement);

            // Add the outputfilters
            WeaveOutputFilters(method, methodElement);

        }

        /// <summary>
        /// Weaves the input filters.
        /// </summary>
        /// <param name="method">The method.</param>
        /// <param name="methodElement">The method element.</param>
        /// <remarks>
        /// InputFilters are added at the top of the methodbody.
        /// </remarks>        
        public void WeaveInputFilters(MethodDefinition method, MethodElement methodElement)
        {
            #region Check for null and retrieve inputFilter
            
            // Only proceed when there is a message body
            if (method.HasBody == false )
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

            #region Convert inputfilter to a block

            Composestar.Repository.LanguageModel.Inlining.Block block;
            block = inputFilter as Composestar.Repository.LanguageModel.Inlining.Block;
            if (block == null)
                return;
            
            #endregion

            // Gets the CilWorker of the method for working with CIL instructions
            CilWorker worker = method.Body.CilWorker;

            // Get the instructions for the IsInnerCall check
            Instruction branchToInstruction = worker.Create(OpCodes.Nop); 
            String methodSignature = method.Name; // TODO get the correct signature from the contextinstruction
            IList<Instruction> inInnerCallInstructions =
                GenerateFilterContextIsInnerCallCheck(method, methodSignature, ref worker, branchToInstruction);
                        
            // Getting the first instruction of the current method
            Instruction ins = method.Body.Instructions[0];

            // Add the check for IsInnerCall
            int instructionsCount = InsertInstructionList(ref worker, ins, inInnerCallInstructions);
            
            // Get the new location for the next instructions
            ins = method.Body.Instructions[instructionsCount]; 

            // Add filters using the visitor
            CecilInliningInstructionVisitor visitor = new CecilInliningInstructionVisitor();
            visitor.Method = method;
            visitor.Worker = worker;
            visitor.TargetAssemblyDefinition = _targetAssemblyDefinition;

            // Visit the elements in the block
            block.Accept(visitor);

            // Only add instructions if we have instructions
            if (visitor.Instructions.Count > 0)
            {
                // Add the instructions
                instructionsCount = InsertInstructionList(ref worker, ins, visitor.Instructions);
                ins = method.Body.Instructions[instructionsCount];
            }

            // Add the end of the filter code marker, the IsInnerCall branch will jump to this location
            worker.InsertAfter(ins, branchToInstruction);          

            //
            // What follows are the original instructions
            //

        }

        /// <summary>
        /// Weaves the output filters.
        /// </summary>
        /// <param name="method">The method.</param>
        /// <param name="methodElement">The method element.</param>
        public void WeaveOutputFilters(MethodDefinition method, MethodElement methodElement)
        {
            #region Check for null and retrieve calls for this method

            if (methodElement.MethodBody == null)
                return;

            // Only proceed when there is a message body
            if (method.HasBody == false )
                return;

            IList<CallElement> calls = _repositoryAccess.GetCallByMethodElement(methodElement);

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

                        
                        //worker.Replace(instruction, instruction); 
                    }
                }
            }            
        }

        /// <summary>
        /// Closes this instance. Cleanup any used resources.
        /// </summary>
        public void Close()
        {
            if (_repositoryAccess != null)
                _repositoryAccess.CloseDatabase();
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
        /// Generates the filter context is inner call check.
        /// </summary>
        /// <param name="method">The method.</param>
        /// <param name="methodSignature">The method signature.</param>
        /// <param name="worker">The worker.</param>
        /// <param name="branchToInstruction">The branch to instruction.</param>
        /// <returns>A list of <see cref="T:Instruction"/>s to add to the current output.</returns>
        /// <example>
        /// Generate the following code:
        /// <code>        
        /// if (!FilterContext.IsInnerCall(this, methodName)) 
        /// {
        ///   <b>filtercode</b>
        /// }
        /// </code>
        /// The <b>filtercode</b> are the inputfilters added to the method.
        /// </example> 
        private IList<Instruction> GenerateFilterContextIsInnerCallCheck(MethodDefinition method, string methodSignature, ref CilWorker worker, Instruction branchToInstruction)
        {
            IList<Instruction> instructions = new List<Instruction>();

            // Get a methodinfo to the IsInnerCall check
            MethodInfo checkInnerCallInfo = typeof(Composestar.StarLight.ContextInfo.FilterContext).GetMethod("IsInnerCall", new Type[] { typeof(object), typeof(string) });
           
            // Create a methodreference for the IsInnerCall
            MethodReference checkInnerCall = _targetAssemblyDefinition.MainModule.Import(checkInnerCallInfo);

            // Create instructions to load the arguments for the IsInnerCall on the stack
            Instruction loadThis;
            if (method.HasThis)
                loadThis = worker.Create(OpCodes.Ldarg, method.This);
            else
                loadThis = worker.Create(OpCodes.Ldnull);
            Instruction loadMethodName = worker.Create(OpCodes.Ldstr, methodSignature);

            // Create the call instruction
            Instruction callIsInnerCall = worker.Create(OpCodes.Call, checkInnerCall);

            // Result is placed on the stack, so use it to branch to the skipFiltersInstruction
            Instruction  branchInstruction = worker.Create(OpCodes.Brtrue, branchToInstruction);

            // Add to the list
            instructions.Add(loadThis);
            instructions.Add(loadMethodName);
            instructions.Add(callIsInnerCall);
            instructions.Add(branchToInstruction); 

            return instructions;
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
             
        #region nested class WeaverConfiguration
        /// <summary>
        /// Contains the configuration for the weaver.
        /// </summary>
        private sealed class WeaverConfiguration
        {
            private string _outputImageSNK;
            private bool _shouldSignOutput;
            private string _outputImagePath;
            private string _outputFilename;

            /// <summary>
            /// Initializes a new instance of the <see cref="T:WeaverConfiguration"/> class.
            /// </summary>
            /// <param name="outputImagePath">The output image path.</param>
            /// <param name="shouldSignOutput">if set to <c>true</c> [should sign output].</param>
            /// <param name="outputImageSNK">The output image SNK.</param>
            public WeaverConfiguration(string outputImagePath, bool shouldSignOutput, string outputImageSNK)
            {
                _outputImageSNK = outputImageSNK;
                _shouldSignOutput = shouldSignOutput;
                _outputImagePath = outputImagePath;
            }

            /// <summary>
            /// Initializes a new instance of the <see cref="T:WeaverConfiguration"/> class.
            /// </summary>
            /// <param name="outputImagePath">The output image path.</param>
            /// <param name="outputFilename">The output filename.</param>
            /// <param name="shouldSignOutput">if set to <c>true</c> [should sign output].</param>
            /// <param name="outputImageSNK">The output image SNK.</param>
            public WeaverConfiguration(string outputImagePath, string outputFilename, bool shouldSignOutput, string outputImageSNK)
            {
                _outputImageSNK = outputImageSNK;
                _outputFilename = outputFilename;
                _shouldSignOutput = shouldSignOutput;
                _outputImagePath = outputImagePath;
            }

            /// <summary>
            /// Gets or sets the output filename.
            /// </summary>
            /// <value>The output filename.</value>
            public string OutputFilename
            {
                get
                {
                    return _outputFilename;
                }
                set
                {
                    _outputFilename = value;
                }
            }

            /// <summary>
            /// Gets the output file.
            /// </summary>
            /// <value>The output file.</value>
            public string OutputFile
            {
                get { return Path.Combine(_outputImagePath, _outputFilename); }
            }

            /// <summary>
            /// Gets the output image SNK.
            /// </summary>
            /// <value>The output image SNK.</value>
            public string OutputImageSNK
            {
                get { return _outputImageSNK; }
            }

            /// <summary>
            /// Gets a value indicating whether the output should be signed.
            /// </summary>
            /// <value><c>true</c> if should sign output; otherwise, <c>false</c>.</value>
            public bool ShouldSignOutput
            {
                get { return _shouldSignOutput; }
            }

            /// <summary>
            /// Gets the output image path.
            /// </summary>
            /// <value>The output image path.</value>
            public string OutputImagePath
            {
                get { return _outputImagePath; }
            }

            /// <summary>
            /// Creates the default configuration.
            /// </summary>
            /// <param name="inputImagePath">The input image path.</param>
            /// <returns></returns>
            public static WeaverConfiguration CreateDefaultConfiguration(string inputImagePath)
            {
                return new WeaverConfiguration(inputImagePath, false, string.Empty);
            }
        }
        #endregion
    }
}
