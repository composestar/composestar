#region License
/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * ComposeStar StarLight [http://janus.cs.utwente.nl:8000/twiki/bin/view/StarLight/WebHome]
 * Copyright (C) 2003, University of Twente.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification,are permitted provided that the following conditions
 * are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Twente nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
*/
#endregion

#region Using directives
using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Diagnostics;
using System.IO;
using System.Reflection;
using System.Text;
using System.Globalization;
using System.Diagnostics.CodeAnalysis;

using Mono.Cecil;
using Mono.Cecil.Binary;
using Mono.Cecil.Cil;
using Mono.Cecil.Metadata;
using Mono.Cecil.Signatures;
using Mono.Cecil.Pdb;

using Composestar.StarLight.Entities.WeaveSpec;
using Composestar.StarLight.Entities.WeaveSpec.Instructions;
using Composestar.StarLight.Entities.LanguageModel;

using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions;
using Composestar.StarLight.CoreServices.ILWeaver;

using Composestar.StarLight.Utilities;
using Composestar.StarLight.Utilities.Interfaces;
using Composestar.StarLight.Entities.Configuration;

#endregion

namespace Composestar.StarLight.ILWeaver
{

	/// <summary>
	/// Cecil implementation of the IL Weaver.
	/// </summary>
	public sealed class CecilILWeaver : IILWeaver
	{

		#region Private variables

		private CecilWeaverConfiguration _configuration;
		private IEntitiesAccessor _entitiesAccessor;
		private bool _typeChanged;
		private WeaveStatistics _weaveStats;

		#endregion

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
		WeaveStatistics IILWeaver.DoWeave()
		{
			// Check for the existents of the file
			if (!File.Exists(_configuration.InputImagePath))
			{
				throw new FileNotFoundException(string.Format(CultureInfo.CurrentCulture, 
					Properties.Resources.InputImageNotFound, _configuration.InputImagePath));
			}

			// Reset statistics
			_weaveStats = new WeaveStatistics();

			// Start timing
			Stopwatch sw = new Stopwatch();
			Stopwatch swType = new Stopwatch();
			Stopwatch swMethod = new Stopwatch();

			sw.Start();

			StoreTimeStamp(sw.Elapsed, "Starting weaver");

			// Prepare the data for this assembly 
			WeaveSpecification weaveSpec;

			// Get the weave specification
			StoreTimeStamp(sw.Elapsed, "Loading weave specification");
			weaveSpec = _entitiesAccessor.LoadWeaveSpecification(_configuration.AssemblyConfiguration.WeaveSpecificationFile);

			if (weaveSpec == null)
				throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
					Properties.Resources.WeavingSpecNotFound,
					_configuration.AssemblyConfiguration.WeaveSpecificationFile,
					_configuration.AssemblyConfiguration.Name));

			StoreTimeStamp(sw.Elapsed, "Loaded weave specification");

			// If empty, we can quit
			if (weaveSpec.WeaveTypes.Count == 0)
			{
				// Stop timing
				sw.Stop();
				_weaveStats.TotalWeaveTime = sw.Elapsed;

				// Stop the execution
				return _weaveStats;
			} 

			// Load the file
			AssemblyDefinition targetAssembly;
			ISymbolReader pdbReader = null;

			StoreTimeStamp(sw.Elapsed, "Loading assembly");

			targetAssembly = LoadAssembly(ref pdbReader);

			StoreTimeStamp(sw.Elapsed, "Loaded assembly");

			// Check if the _targetAssemblyDefinition is available
			if (targetAssembly == null)
				throw new ArgumentNullException(Properties.Resources.AssemblyNotOpen);							

			// Get only the types we have info for
			foreach (WeaveType weaveType in weaveSpec.WeaveTypes)
			{
				StoreTimeStamp(sw.Elapsed, "Retrieving type '{0}'", weaveType.Name);

				TypeDefinition type = targetAssembly.MainModule.Types[weaveType.Name];
				if (type == null)
					continue;

				_typeChanged = false;

				swType.Start();

				// Get and add the externals for this type
				StoreTimeStamp(sw.Elapsed, "Externals for type '{0}'", weaveType.Name);
				if (weaveType.Externals.Count > 0)
					WeaveExternals(targetAssembly, type, weaveType);

				// Get and add the internals for this type
				StoreTimeStamp(sw.Elapsed, "Internals for type '{0}'", weaveType.Name);
				if (weaveType.Internals.Count > 0)
					WeaveInternals(targetAssembly, type, weaveType);

				if (weaveType.Methods.Count > 0)
				{
					// Loop through all the methods
					foreach (MethodDefinition method in type.Methods)
					{
						StoreTimeStamp(sw.Elapsed, "Retrieving method '{0}'", method.ToString());
						// Get the methodinfo based on the signature
						WeaveMethod weaveMethod = GetMethodFromList(weaveType.Methods, method.ToString());

						// Skip if there is no weaveMethod
						if (weaveMethod == null)
							continue;

						swMethod.Start();

						StoreTimeStamp(sw.Elapsed, "Weaving method '{0}'", method.ToString());
						WeaveMethod(targetAssembly, method, weaveMethod, weaveType);

						// Update stats
						if (_configuration.WeaveDebugLevel != CecilWeaverConfiguration.WeaveDebug.None)
						{
							_weaveStats.MethodsProcessed++;
							_weaveStats.TotalMethodWeaveTime = _weaveStats.TotalMethodWeaveTime.Add(swMethod.Elapsed);
							_weaveStats.MaxWeaveTimePerMethod = TimeSpan.FromTicks(Math.Max(_weaveStats.MaxWeaveTimePerMethod.Ticks, swMethod.Elapsed.Ticks));
						}

						swMethod.Reset();

					} 
				} 

				// Import the changed type into the AssemblyDefinition
				if (_typeChanged)
				{
					StoreTimeStamp(sw.Elapsed, "Importing type '{0}' into assembly", weaveType.Name);

					targetAssembly.MainModule.Import(type);
				}

				swType.Stop();

				// Update stats
				if (_configuration.WeaveDebugLevel != CecilWeaverConfiguration.WeaveDebug.None)
				{
					_weaveStats.TypesProcessed++;
					_weaveStats.TotalTypeWeaveTime = _weaveStats.TotalTypeWeaveTime.Add(swType.Elapsed);
					_weaveStats.MaxWeaveTimePerType = TimeSpan.FromTicks(Math.Max(_weaveStats.MaxWeaveTimePerType.Ticks, swType.Elapsed.Ticks));
				}
				swType.Reset();

			} // foreach  (typeElement)

			// Save the modified assembly only if it is changed.
			if (_weaveStats.InputFiltersAdded > 0 || _weaveStats.OutputFiltersAdded > 0 || _weaveStats.InternalsAdded > 0 || _weaveStats.ExternalsAdded > 0)
			{
				StoreTimeStamp(sw.Elapsed, "Saving assembly");
				SaveAssembly(targetAssembly, pdbReader);
				StoreTimeStamp(sw.Elapsed, "Saved assembly");
			} // if

			// Stop timing
			sw.Stop();

			StoreTimeStamp(sw.Elapsed, "Weaving completed");
			_weaveStats.TotalWeaveTime = sw.Elapsed;

			return _weaveStats;
		}

		#region Loading and Saving of assembly

		/// <summary>
		/// Loads the assembly.
		/// </summary>
		/// <param name="pdbReader">The PDB reader.</param>
		/// <returns>Returns an assembly definition.</returns>
		private AssemblyDefinition LoadAssembly(ref ISymbolReader pdbReader)
		{
			AssemblyDefinition targetAssembly;
			try
			{
				byte[] binaryFile;
				FileStream fileStream = null;
				try
				{
					fileStream = new FileStream(_configuration.InputImagePath, FileMode.Open);
					binaryFile = CecilUtilities.ReadFileStream(fileStream, -1);

				} // try
				catch (Exception ex)
				{
					throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
						Properties.Resources.CouldNotLoadAssembly,
						_configuration.InputImagePath, ex.Message),
						ex);

				} // catch
				finally
				{
					if (fileStream != null) fileStream.Close();

				} // finally

				// We use a byte array to read the file, so we can close it after reading and can write to it again.
				// targetAssembly = AssemblyFactory.GetAssembly(_configuration.InputImagePath);

				targetAssembly = AssemblyFactory.GetAssembly(binaryFile);

				// Get the pdb
				if (_configuration.AssemblyConfiguration.DebugFileMode != AssemblyConfig.PdbMode.None &&  
					File.Exists(_configuration.DebugImagePath))
				{
					// pdbReader = new PdbFactory().CreateReader(targetAssembly.MainModule, _configuration.InputImagePath);

					// It is possible the file could not be read due to com exceptions. Report this, but continue.
					try
					{
						pdbReader = new PdbFactory().CreateReader(targetAssembly.MainModule, _configuration.InputImagePath, binaryFile);
						targetAssembly.MainModule.LoadSymbols(pdbReader);
					}
					catch (Exception)
					{
						_configuration.AssemblyConfiguration.DebugFileMode = AssemblyConfig.PdbMode.None;  							
					}					
				}

				binaryFile = null;

			}
			catch (EndOfStreamException)
			{
				throw new BadImageFormatException(String.Format(CultureInfo.CurrentCulture,
					Properties.Resources.ImageIsBad,
					_configuration.InputImagePath));
			}

			return targetAssembly;
		}

		/// <summary>
		/// Saves the assembly and debug file.
		/// </summary>
		/// <param name="targetAssembly">The target assembly.</param>
		/// <param name="pdbReader">The PDB reader.</param>
		private void SaveAssembly(AssemblyDefinition targetAssembly, ISymbolReader pdbReader)
		{
			// Save debug
			try
			{
				if (_configuration.AssemblyConfiguration.DebugFileMode != AssemblyConfig.PdbMode.None &&  
					File.Exists(_configuration.DebugImagePath))
				{
					if (pdbReader != null)
					{
						pdbReader.Dispose();
						pdbReader = null;
					}

					targetAssembly.MainModule.SaveSymbols(Path.GetDirectoryName(_configuration.InputImagePath));
				}
			}
			catch (Exception ex)
			{
				throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
					Properties.Resources.CouldNotSavePdb, _configuration.DebugImagePath),
					_configuration.OutputImagePath, ex);
			}

			// Save the assembly
			try
			{
				AssemblyFactory.SaveAssembly(targetAssembly, _configuration.OutputImagePath);
			}
			catch (Exception ex)
			{
				throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
					Properties.Resources.CouldNotSaveAssembly, _configuration.OutputImagePath),
					_configuration.OutputImagePath, ex);
			} 
		}


		#endregion
		/// <summary>
		/// Weaves the internals.
		/// </summary>
		/// <param name="targetAssembly">The target assembly.</param>
		/// <param name="type">The type.</param>
		/// <param name="weaveType">Type of the weave.</param>
		private void WeaveInternals(AssemblyDefinition targetAssembly, TypeDefinition type, WeaveType weaveType)
		{
			#region Check the internals

			if (weaveType == null)
				throw new ArgumentNullException("weaveType");

			if (weaveType.Internals == null | weaveType.Internals.Count == 0)
				return;

			#endregion

			#region Check for null

			if (targetAssembly == null)
				throw new ArgumentNullException("targetAssembly");

			if (type == null)
				throw new ArgumentNullException("type");

			#endregion

			FieldDefinition internalDef;
			TypeReference internalTypeRef;
			Mono.Cecil.FieldAttributes internalAttrs;

			foreach (Internal inter in weaveType.Internals)
			{
				String internalTypeString = String.Format(CultureInfo.InvariantCulture, "{0}.{1}", inter.Namespace, inter.Type);

				internalTypeRef = CecilUtilities.ResolveType(internalTypeString, inter.Assembly, "");
				if (internalTypeRef == null)
					throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.TypeNotFound, internalTypeString + " (step 2)"));

				internalAttrs = Mono.Cecil.FieldAttributes.Private;

				internalTypeRef = targetAssembly.MainModule.Import(internalTypeRef);

				// Create the field
				internalDef = new FieldDefinition(inter.Name, internalTypeRef, internalAttrs);

				// Add the field
				type.Fields.Add(internalDef);

				// Increase the number of internals
				_weaveStats.InternalsAdded++;

				// Add initialization code to type constructor(s)
				if (!internalTypeRef.IsValueType && internalTypeRef.Name != "String" && internalTypeRef.Name != "Array")
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
								instructions.Add(worker.Create(OpCodes.Newobj, targetAssembly.MainModule.Import(internalConstructor)));
								instructions.Add(worker.Create(OpCodes.Stfld, internalDef));

								// Add the instructions
								InsertBeforeInstructionList(ref worker, constructor.Body.Instructions[0], instructions);

								// Log
								StoreInstructionLog(instructions, "Internal code added to {0} for internal {1}", constructor.ToString(), internalDef.ToString());
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
		private void WeaveExternals(AssemblyDefinition targetAssembly, TypeDefinition type, WeaveType weaveType)
		{
			#region Check the externals

			if (weaveType == null)
				throw new ArgumentNullException("weaveType");

			if (weaveType.Externals == null | weaveType.Externals.Count == 0)
				return;

			#endregion

			#region Check for null

			if (targetAssembly == null)
				throw new ArgumentNullException("targetAssembly");

			if (type == null)
				throw new ArgumentNullException("type");

			#endregion

			FieldDefinition externalDef;
			TypeReference externalTypeRef;
			Mono.Cecil.FieldAttributes externalAttrs;

			foreach (External external in weaveType.Externals)
			{
				externalTypeRef = CecilUtilities.ResolveType(external.Type, external.Assembly, "");
				if (externalTypeRef == null)
					throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
						Properties.Resources.TypeNotFound, external.Type));

				externalAttrs = Mono.Cecil.FieldAttributes.Private;

				externalTypeRef = targetAssembly.MainModule.Import(externalTypeRef);

				// Create the field
				externalDef = new FieldDefinition(external.Name, externalTypeRef, externalAttrs);

				// Add the field
				type.Fields.Add(externalDef);

				// Increase the number of externals
				_weaveStats.ExternalsAdded++;

				// Get the method referenced by the external
				MethodDefinition initMethodDef = (MethodDefinition)CecilUtilities.ResolveMethod(external.Reference.Selector,
					String.Format(CultureInfo.InvariantCulture, "{0}.{1}", external.Reference.Namespace, external.Reference.Target),
					external.Assembly, "");

				if (initMethodDef == null)
					throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
						Properties.Resources.MethodNotFound,
						external.Reference.Selector,
						external.Reference.Target,
						external.Assembly));

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
							InsertBeforeInstructionList(ref worker, constructor.Body.Instructions[0], instructions);

							// Log
							StoreInstructionLog(instructions, "External code added to {0} for external {1}", constructor.ToString(), externalDef.ToString());
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
		/// <param name="weaveType">Type of the weave.</param>
		private void WeaveMethod(AssemblyDefinition targetAssembly, MethodDefinition method, WeaveMethod weaveMethod, WeaveType weaveType)
		{

			#region Check for null
			if (targetAssembly == null)
				throw new ArgumentNullException("targetAssembly");

			if (method == null)
				throw new ArgumentNullException("method");

			if (weaveMethod == null)
				throw new ArgumentNullException("weaveMethod");

			if (weaveType == null)
				throw new ArgumentNullException("weaveType");

			#endregion

			// Add the inputfilters
			if (weaveMethod.HasInputFilters)
			{
				WeaveInputFilters(targetAssembly, method, weaveMethod, weaveType);
			} // if

			// Add the outputfilters
			if (weaveMethod.HasOutputFilters)
			{
				WeaveOutputFilters(targetAssembly, method, weaveMethod, weaveType);
			} // if
		}

		/// <summary>
		/// Weaves the input filters.
		/// </summary>
		/// <param name="targetAssembly">The target assembly.</param>
		/// <param name="method">The method we are weaving in.</param>
		/// <param name="weaveMethod">The weave method.</param>
		/// <param name="weaveType">Type of the weave.</param>
		/// <remarks>
		/// InputFilters are added at the top of the methodbody.
		/// We call a visitor to generate IL instructions and we add those to the top of the method.
		/// </remarks>
		private void WeaveInputFilters(AssemblyDefinition targetAssembly, MethodDefinition method, WeaveMethod weaveMethod, WeaveType weaveType)
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
			visitor.FilterType = FilterType.InputFilter;
			visitor.TargetAssemblyDefinition = targetAssembly;
			visitor.EntitiesAccessor = _entitiesAccessor;
			visitor.WeaveConfiguration = _configuration.WeaveConfiguration;
			visitor.WeaveType = weaveType;

			// Visit the elements in the block
			try
			{
				((Composestar.StarLight.Entities.WeaveSpec.Instructions.Visitor.IVisitable)inputFilter).Accept(visitor);
			}
			catch (Exception ex)
			{
				// T the error wrapped in an ILWeaverException                 
				throw new ILWeaverException(Properties.Resources.CecilVisitorRaisedException,
											_configuration.OutputImagePath, ex);
			}

			// Only add instructions if we have instructions
			if (visitor.Instructions.Count > 0)
			{
				// Add the instructions
				int instructionsCount = 0;
				instructionsCount += InsertBeforeInstructionList(ref worker, ins, visitor.Instructions);

				// Log
				StoreInstructionLog(visitor.Instructions, "Input filters for {0}", method.ToString());

				// Increase the number of inputfilters added
				_weaveStats.InputFiltersAdded++;
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
		/// <param name="weaveMethod">The weave method.</param>
		/// <param name="weaveType">Type of the weave.</param>
		/// <remarks>
		/// We look for each call and see if we have an outputfilter for it.
		/// If we do, then call the visitor to generate code to replace the call.
		/// </remarks>
		private void WeaveOutputFilters(AssemblyDefinition targetAssembly, MethodDefinition method, WeaveMethod weaveMethod, WeaveType weaveType)
		{

			#region Check for null

			if (targetAssembly == null)
				throw new ArgumentNullException("targetAssembly");

			if (!weaveMethod.HasOutputFilters)
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

			foreach (Instruction instruction in callInstructions)
			{
				// Find the corresponding call in the list of calls
				MethodReference mr = (MethodReference)(instruction.Operand);
				MethodDefinition md = CecilUtilities.ResolveMethodDefinition(mr);
				InlineInstruction outputFilter = GetOutputFilterForCall(weaveMethod.Calls, md.ToString());

				// If we found an outputFilter in the repository, then see if we have to perform weaving
				if (outputFilter != null)
				{
					// Add filters using the visitor
					CecilInliningInstructionVisitor visitor = new CecilInliningInstructionVisitor();
					visitor.Method = method;
					visitor.CalledMethod = md;
					visitor.Worker = worker;
					visitor.FilterType = FilterType.OutputFilter;
					visitor.TargetAssemblyDefinition = targetAssembly;
					visitor.WeaveConfiguration = _configuration.WeaveConfiguration;
					visitor.WeaveType = weaveType;

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
						// Our newly added instructions must have the same sequence point as the instruction we are replacing
						visitor.Instructions[0].SequencePoint = instruction.SequencePoint;

						// Or add a new document as sequence point to link to the concern file? 
						// Difficult to do here

						int instructionsCount = 0;
						// Add the instructions
						instructionsCount += ReplaceAndInsertInstructionList(ref worker, instruction, visitor.Instructions);

						// Log
						StoreInstructionLog(visitor.Instructions, "Output filters({2}) for {0} call {1}", method.ToString(), md.ToString(), _weaveStats.OutputFiltersAdded);

						// Increase the number of output filters added
						_weaveStats.OutputFiltersAdded++;
					}
				}

			}
		}

		#region IDisposable

		/// <summary>
		/// Cleans up any resources associated with this instance.
		/// </summary>
		public void Dispose()
		{
			Dispose(true);
			GC.SuppressFinalize(this);  // Finalization is now unnecessary
		}

		/// <summary>
		/// Disposes the object.
		/// </summary>
		/// <param name="disposing">if set to <c>true</c> then the managed resources are disposed.</param>
		[SuppressMessage("Microsoft.Design", "CA1063")]
		public void Dispose(bool disposing)
		{
			if (!m_disposed)
			{
				if (disposing)
				{
					// Dispose managed resources
				}

				// Dispose unmanaged resources
			}

			m_disposed = true;
		}

		private bool m_disposed;

		#endregion

		#region Helper functions

		/// <summary>
		/// Writes the sequence points to the console.
		/// </summary>
		/// <param name="method">The method.</param>
		private static void DisplaySequencePoints(MethodDefinition method)
		{
			Console.WriteLine("Method {0}", method.ToString());

			foreach (Instruction instruction in method.Body.Instructions)
			{
				if (instruction.SequencePoint != null)
					Console.WriteLine(" {5}: {0}:{1}-{2}:{3}:{4} ", instruction.SequencePoint.StartLine,
						instruction.SequencePoint.StartColumn, instruction.SequencePoint.EndLine,
						instruction.SequencePoint.EndColumn, Path.GetFileName(instruction.SequencePoint.Document.Url),
						instruction.OpCode.ToString());

			}
			Console.WriteLine("");
		}

		/// <summary>
		/// Inserts the instruction list before the start instruction.
		/// </summary>
		/// <param name="worker">The worker.</param>
		/// <param name="startInstruction">The start instruction.</param>
		/// <param name="instructionsToAdd">The instructions to add.</param>
		/// <returns></returns>
		private static int InsertBeforeInstructionList(ref CilWorker worker, Instruction startInstruction, IList<Instruction> instructionsToAdd)
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
		[SuppressMessage("Microsoft.Performance", "CA1811:AvoidUncalledPrivateCode", Justification = "Might be used in the future.")]
		private static int InsertInstructionList(ref CilWorker worker, Instruction startInstruction, IList<Instruction> instructionsToAdd)
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
		private static int ReplaceAndInsertInstructionList(ref CilWorker worker, Instruction startInstruction, IList<Instruction> instructionsToAdd)
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
		private static WeaveMethod GetMethodFromList(List<WeaveMethod> list, String signature)
		{
			foreach (WeaveMethod method in list)
			{
				if (method.Signature.Equals(signature))
					return method;
			}
			return null;
		}

		/// <summary>
		/// Gets the output filter for call.
		/// </summary>
		/// <param name="weaveCalls">Weave calls</param>
		/// <param name="callSignature">Call signature</param>
		/// <returns>Inline instruction</returns>
		private static InlineInstruction GetOutputFilterForCall(List<WeaveCall> weaveCalls, string callSignature)
		{
			foreach (WeaveCall wc in weaveCalls)
			{
				if (wc.MethodName.Equals(callSignature))
					return wc.OutputFilter;
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
		private static bool IsCallInstruction(Instruction instruction)
		{
			return (instruction.OpCode == OpCodes.Call |
					instruction.OpCode == OpCodes.Calli |
					instruction.OpCode == OpCodes.Callvirt);
		}


		/// <summary>
		/// Stores the instruction log.
		/// </summary>
		/// <param name="instructions">The instructions.</param>
		/// <param name="caption">The caption.</param>
		/// <param name="arguments">The arguments.</param>
		private void StoreInstructionLog(IList<Instruction> instructions, string caption, params object[] arguments)
		{
			StoreInstructionLog(instructions, String.Format(CultureInfo.CurrentCulture, caption, arguments));
		}

		/// <summary>
		/// Stores the instruction log.
		/// </summary>
		/// <param name="instructions">The instructions.</param>
		/// <param name="caption">The caption.</param>
		private void StoreInstructionLog(IList<Instruction> instructions, string caption)
		{
			if (_configuration.WeaveDebugLevel == CecilWeaverConfiguration.WeaveDebug.Detailed)
			{
				List<String> formattedList = new List<string>();
				foreach (Instruction instruction in instructions)
				{
					formattedList.Add(CecilFormatter.FormatInstruction(instruction));
				}

				// Add the log
				_weaveStats.InstructionsLog.Add(caption, formattedList);
			}
		}

		/// <summary>
		/// Stores the time stamp.
		/// </summary>
		/// <param name="ts">The timestamp.</param>
		/// <param name="caption">The caption.</param>
		/// <param name="arguments">The arguments.</param>
		private void StoreTimeStamp(TimeSpan ts, string caption, params Object[] arguments)
		{
			if (_configuration.WeaveDebugLevel == CecilWeaverConfiguration.WeaveDebug.Detailed)
			{
				string item = String.Format(CultureInfo.CurrentCulture, caption, arguments);
				item = String.Format(CultureInfo.CurrentCulture, "{0}^{1}", item, ts.TotalMilliseconds);
				_weaveStats.TimingStack.Enqueue(item);
			}
		}
		#endregion

	}
}
