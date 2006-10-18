#region Using directives
using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Diagnostics;
using System.IO;
using System.Reflection;
using System.Text;
using System.Globalization;
using System.Xml;

using Mono.Cecil;
using Mono.Cecil.Binary;
using Mono.Cecil.Cil;
using Mono.Cecil.Metadata;
using Mono.Cecil.Signatures;

using Composestar.Repository.LanguageModel;
using Composestar.Repository;
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions; 
using Composestar.Repository.LanguageModel.Inlining;
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
        private ILanguageModelAccessor _languageModelAccessor;
        private int _internalsAdded;
        private int _externalsAdded;
        private int _outputFiltersAdded;
        private int _inputFiltersAdded;
        private bool _typeChanged;

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
            AssemblyElement assemblyElement;
            assemblyElement = _languageModelAccessor.GetAssemblyElementByName(targetAssembly.Name.FullName);
            if (assemblyElement == null)
                throw new ILWeaverException(String.Format(Properties.Resources.AssemblyElementNotFound, targetAssembly.Name.FullName));

            // Get all the typeElements for this Assembly
            Dictionary<String, TypeElement> typeElements = _languageModelAccessor.GetTypeElementsByAssembly(assemblyElement);

            // If empty, we can quit
            if (typeElements.Count == 0)
                return;

            // Get all the externals
            Dictionary<String, List<External>> externals = _languageModelAccessor.GetExternals();

            // Get all the internals
            Dictionary<String, List<Internal>> internals = _languageModelAccessor.GetInternals();

            // Get all the methodElements
            Dictionary<String, List<MethodElement>> methodElements = _languageModelAccessor.GetMethodElements();

            // Run the optimizer
            typeElements = OptimizeTypeElements(ref typeElements, ref externals, ref internals, ref methodElements, assemblyElement);

            // If empty, we can quit
            if (typeElements.Count == 0)
            {
                // Stop timing
                sw.Stop();
                m_LastDuration = sw.Elapsed;
                // Return
                return;
            } // if

            // Get only the types we have info for
            foreach (TypeElement typeElement in typeElements.Values)
            {
                TypeDefinition type = targetAssembly.MainModule.Types[typeElement.FullName];
                if (type == null)
                    continue;

                _typeChanged = false;

                // Get and add the externals for this type
                List<External> externalsList = null;
                if (externals.TryGetValue(typeElement.Id, out externalsList) && externalsList.Count > 0)
                    WeaveExternals(targetAssembly, type, typeElement, externalsList);

                // Get and add the internals for this type
                List<Internal> internalsList = null;
                if (internals.TryGetValue(typeElement.Id, out internalsList) && internalsList.Count > 0)
                    WeaveInternals(targetAssembly, type, typeElement, internalsList);

                List<MethodElement> methodsInType = null;
                if (methodElements.TryGetValue(typeElement.Id, out methodsInType))
                {
                    foreach (MethodDefinition method in type.Methods)
                    {

                        // Get the methodinfo based on the signature
                        MethodElement methodElement = GetMethodFromList(methodsInType, method.ToString());

                        // Skip if there is no methodinfo
                        if (methodElement == null || (!methodElement.HasInputfilters & !methodElement.HasOutputFilters))
                            continue;

                        WeaveMethod(targetAssembly, method, methodElement);

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
        /// <param name="typeElement">The type information.</param>
        /// <param name="internals">The internals.</param>
        private void WeaveInternals(AssemblyDefinition targetAssembly, TypeDefinition type, TypeElement typeElement, List<Internal> internals)
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

            if (typeElement == null)
                throw new ArgumentNullException("typeElement");

            #endregion
                     
            FieldDefinition internalDef;
            TypeReference internalTypeRef;
            Mono.Cecil.FieldAttributes internalAttrs;

            foreach (Internal inter in internals)
            {
                String internalTypeString = String.Format("{0}.{1}", inter.NameSpace, inter.Type);

                TypeElement internalTypeElement = _languageModelAccessor.GetTypeElement(internalTypeString);
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
        /// <param name="typeElement">The type information.</param>
        /// <param name="externals">The externals.</param>
        private void WeaveExternals(AssemblyDefinition targetAssembly, TypeDefinition type, TypeElement typeElement, List<External> externals)
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

            if (typeElement == null)
                throw new ArgumentNullException("typeElement");

            #endregion
          
            FieldDefinition externalDef;
            TypeReference externalTypeRef;
            Mono.Cecil.FieldAttributes externalAttrs;

            foreach (External external in externals)
            {
                TypeElement externalTypeElement = _languageModelAccessor.GetTypeElement(external.Type);
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
                TypeElement initTypeElement = _languageModelAccessor.GetTypeElement(String.Format("{0}.{1}", external.Reference.NameSpace, external.Reference.Target));
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
        /// <param name="methodElement">The method element.</param>
        private void WeaveMethod(AssemblyDefinition targetAssembly, MethodDefinition method, MethodElement methodElement)
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
            if (methodElement.HasInputfilters)
            {
                WeaveInputFilters(targetAssembly, method, methodElement);
            } // if

            // Add the outputfilters
            if (methodElement.HasOutputFilters)
            {
                WeaveOutputFilters(targetAssembly, method, methodElement);
            } // if
        }

        /// <summary>
        /// Weaves the input filters.
        /// </summary>
        /// <param name="targetAssembly">The target assembly.</param>
        /// <param name="method">The method we are weaving in.</param>
        /// <param name="methodElement">The method element containing the weaving instructions.</param>
        /// <remarks>
        /// InputFilters are added at the top of the methodbody.
        /// We call a visitor to generate IL instructions and we add those to the top of the method.
        /// </remarks>
        private void WeaveInputFilters(AssemblyDefinition targetAssembly, MethodDefinition method, MethodElement methodElement)
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
            InlineInstruction inputFilter = ParseInlineInstructions(methodElement.MethodBody.InputFilter);
                      
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
        private void WeaveOutputFilters(AssemblyDefinition targetAssembly, MethodDefinition method, MethodElement methodElement)
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

            if (calls == null || calls.Count == 0)
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
                CallElement call = FindCallInList(calls, mr.ToString());

                // If we found a CallElement in the repository, then see if we have to perform weaving
                if(call != null)
                {
                    // Get the outputFilter for this call
                    InlineInstruction outputFilter = ParseInlineInstructions(call.OutputFilter);
                    
                    // Check for null, an output filter is not required
                    if(outputFilter == null)
                        continue;

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
            _languageModelAccessor.Close();
        }

        #region Helper functions

        /// <summary>
        /// Parse inline instructions
        /// </summary>
        /// <param name="xmlText">Xml text</param>
        /// <returns>Inline instruction</returns>
        private InlineInstruction ParseInlineInstructions(string xmlText)
        {
            if (string.IsNullOrEmpty(xmlText))
                return null;

            XmlTextReader reader = null;
            Block block = null;

            try
            {
                reader = new XmlTextReader(new StringReader(xmlText));

                reader.ReadStartElement("Block");
                block = new Block();

                while (reader.Read())
                {
                    ParseInstruction(block, reader);
                } // while

            }
            catch (Exception ex)
            {
                throw;
            }
            finally
            {
                if (reader != null)
                {
                    reader.Close();
                } // if
            } // finally

            return block;

        } // ParseInlineInstructions(xmlText)

        /// <summary>
        /// Parse instruction
        /// </summary>
        /// <param name="block">Block</param>
        /// <param name="reader">Reader</param>
        private void ParseInstruction(Block block, XmlTextReader reader)
        {
            string name = reader.Name;
            Console.WriteLine("name is {0}", name);
                        

        } // ParseInstruction(block, reader)

        /// <summary>
        /// Optimizes the type elements. 
        /// Only types in the specified assembly with input or output filters or externals or internals are stored. 
        /// </summary>
        /// <param name="typeElements">The type elements.</param>
        /// <param name="externals">The externals.</param>
        /// <param name="internals">The internals.</param>
        /// <param name="methodElements">The method elements.</param>
        /// <param name="assemblyElement">The assembly element.</param>
        private Dictionary<String, TypeElement> OptimizeTypeElements(ref Dictionary<String, TypeElement>  typeElements, 
                    ref Dictionary<String, List<External>> externals, 
                    ref Dictionary<String, List<Internal>> internals, 
                    ref Dictionary<String, List<MethodElement>> methodElements, 
                    AssemblyElement assemblyElement)
        {
            // Create return value
            Dictionary<String, TypeElement> ret = new Dictionary<String, TypeElement>();

            // Only add typeElements in the specified assembly
            foreach (TypeElement te in typeElements.Values)
            {
                if (te.Assembly.Equals(assemblyElement.Name) )
                {
                    if (internals.ContainsKey(te.Id) || 
                        externals.ContainsKey(te.Id) || 
                        MethodHasFilter(methodElements, te.Id))
                    {
                        ret.Add(te.Id, te); 
                    } // if
                } // if
            } // foreach 
            
            return ret;
        }

        /// <summary>
        /// Checks if the type has methods with input or output filters.
        /// </summary>
        /// <param name="methodElements">The method elements.</param>
        /// <param name="typeId">The type id.</param>
        /// <returns>Bool</returns>
        private bool MethodHasFilter(Dictionary<String, List<MethodElement>> methodElements, string typeId)
        {
            List<MethodElement> methods;
            if (methodElements.TryGetValue(typeId, out methods))
            {
                foreach (MethodElement method in methods)
                {
                    if (method.HasInputfilters || method.HasOutputFilters)
                        return true;
                } // foreach  (method)
            } // if

            return false;

        } // MethodHasFilter(methodElements, typeId)

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
        /// Gets the method from list.
        /// </summary>
        /// <param name="list">The list.</param>
        /// <param name="signature">The signature.</param>
        /// <returns></returns>
        public MethodElement GetMethodFromList(List<MethodElement> list, String signature)
        {
            foreach (MethodElement method in list)
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
