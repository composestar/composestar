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
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions;
using Composestar.StarLight.CoreServices.Weaver;
using Composestar.StarLight.CoreServices.Logger;
using Composestar.StarLight.Entities.Configuration;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities.WeaveSpec;
using Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions;
using Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions.Visitor;
using Composestar.StarLight.Entities.WeaveSpec.Instructions;
using Composestar.StarLight.Utilities;
using Composestar.StarLight.Utilities.Interfaces;
using Mono.Cecil;
using Mono.Cecil.Binary;
using Mono.Cecil.Cil;
using Mono.Cecil.Metadata;
using Mono.Cecil.Pdb;
using Mono.Cecil.Signatures;
using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Diagnostics;
using System.Diagnostics.CodeAnalysis;
using System.Globalization;
using System.IO;
using System.Reflection;
using System.Text;
using Composestar.StarLight.ContextInfo.RuBCoDe;
#endregion

namespace Composestar.StarLight.ILWeaver
{

	/// <summary>
	/// Cecil implementation of the IL Weaver.
	/// </summary>
	public sealed class CecilILWeaver : IILWeaver
	{
		#region Constant values

		private const string LogOriginName = "weaver";
		private const string DebuggerSubcategoryName = "debugger";
		private const string ErrorReadingPdbCode = "D10000";
		private const string CheckerSubCategory = "checker";
		private const string StaticConCode = "C10000";
		private const string PreProcessingSubCategory = "preprocessing";
		private const string FileNotFoundCode = "C10001";
		private const string WeaveSpecNotFound = "C10002";
		private const string CouldNotLoadAssemblyCode = "C10004";
		private const string ImageIsBadCode = "C10005";
		private const string CouldNotSavePdbCode = "C10006";
		private const string CouldNotSaveAssemblyCode = "C10007";
		private const string AssemblySubCategory = "assembly";

		#endregion

		#region Private variables

		/// <summary>
		/// _configuration
		/// </summary>
		private CecilWeaverConfiguration _configuration;
		/// <summary>
		/// _entities accessor
		/// </summary>
		private IEntitiesAccessor _entitiesAccessor;
		/// <summary>
		/// _type changed
		/// </summary>
		private bool _typeChanged;
		/// <summary>
		/// Stores the weave results.
		/// </summary>
		private IWeaveResults _weaveResults;

		/// <summary>
		/// The current weave specification being processed.
		/// </summary>
		private WeaveSpecification _currentWeaveSpec;

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
		IWeaveResults IILWeaver.DoWeave()
		{
			// Create new weave results
			_weaveResults = new GenericWeaveResults();

			// Check for the existence of the file
			if (!File.Exists(_configuration.InputImagePath))
			{
				_weaveResults.Log.LogError(LogOriginName, Properties.Resources.InputImageNotFound, PreProcessingSubCategory, FileNotFoundCode, _configuration.InputImagePath);
				_weaveResults.SetWeaveResult(WeaveResult.Error);
				return _weaveResults; 
			}
			
			// Start timing
			Stopwatch swTotal = Stopwatch.StartNew();
			StoreTimeStamp(swTotal.Elapsed, "Starting weaver");

			// Get the weave specification
			StoreTimeStamp(swTotal.Elapsed, "Loading weave specification");
			_currentWeaveSpec = _entitiesAccessor.LoadWeaveSpecification(_configuration.AssemblyConfiguration.WeaveSpecificationFile);

			if (_currentWeaveSpec == null)
			{
				_weaveResults.SetWeaveResult(WeaveResult.Error);
				_weaveResults.Log.LogError(LogOriginName, Properties.Resources.WeavingSpecNotFound, 
					PreProcessingSubCategory, 
					WeaveSpecNotFound,
					_configuration.AssemblyConfiguration.WeaveSpecificationFile,
					_configuration.AssemblyConfiguration.Name);

				return _weaveResults; 
			}

			StoreTimeStamp(swTotal.Elapsed, "Loaded weave specification");

			// If empty, we can quit
			if (_currentWeaveSpec.WeaveTypes.Count == 0)
			{
				// Stop timing
				swTotal.Stop();
				_weaveResults.WeaveStatistics.TotalWeaveTime = swTotal.Elapsed;

				// Stop the execution
				return _weaveResults;
			}

			// Load the file
			StoreTimeStamp(swTotal.Elapsed, "Loading assembly");

			ISymbolReader pdbReader = null;
			AssemblyDefinition targetAssembly = LoadAssembly(ref pdbReader);

			if (targetAssembly == null)
			{
				// Stop timing
				swTotal.Stop();
				_weaveResults.WeaveStatistics.TotalWeaveTime = swTotal.Elapsed;
				_weaveResults.SetWeaveResult(WeaveResult.Error);

				// Stop the execution
				return _weaveResults;
			}

			StoreTimeStamp(swTotal.Elapsed, "Loaded assembly");

            WeaveAssembly(targetAssembly);
					
			// Get only the types we have info for
			foreach (WeaveType weaveType in _currentWeaveSpec.WeaveTypes)
			{
				StoreTimeStamp(swTotal.Elapsed, "Retrieving type '{0}'", weaveType.Name);

				TypeDefinition type = targetAssembly.MainModule.Types[weaveType.Name];
				if (type == null)
					continue;

				_typeChanged = false;

				Stopwatch swType = Stopwatch.StartNew();

				// Get and add the externals for this type
				StoreTimeStamp(swTotal.Elapsed, "Externals for type '{0}'", weaveType.Name);
				if (weaveType.Externals.Count > 0)
					WeaveExternals(targetAssembly, type, weaveType);

				// Get and add the internals for this type
				StoreTimeStamp(swTotal.Elapsed, "Internals for type '{0}'", weaveType.Name);
				if (weaveType.Internals.Count > 0)
					WeaveInternals(targetAssembly, type, weaveType);

				if (weaveType.Methods.Count > 0)
				{
					// Loop through all the methods
					foreach (MethodDefinition method in type.Methods)
					{
						StoreTimeStamp(swTotal.Elapsed, "Retrieving method '{0}'", method.ToString());

						// Get the methodinfo based on the signature
						WeaveMethod weaveMethod = GetMethodFromList(weaveType.Methods, method.ToString());

						// Skip if there is no weaveMethod
						if (weaveMethod == null)
							continue;

						Stopwatch swMethod = Stopwatch.StartNew();

						StoreTimeStamp(swTotal.Elapsed, "Weaving method '{0}'", method.ToString());
						WeaveMethod(targetAssembly, method, weaveMethod, weaveType);

						// Update stats
						if (_configuration.WeaveDebugLevel != CecilWeaverConfiguration.WeaveDebug.None)
						{
							_weaveResults.WeaveStatistics.MethodsProcessed++;
							_weaveResults.WeaveStatistics.TotalMethodWeaveTime = _weaveResults.WeaveStatistics.TotalMethodWeaveTime.Add(swMethod.Elapsed);
							_weaveResults.WeaveStatistics.MaxWeaveTimePerMethod = TimeSpan.FromTicks(Math.Max(_weaveResults.WeaveStatistics.MaxWeaveTimePerMethod.Ticks, swMethod.Elapsed.Ticks));
						}
					}
				}

				// Import the changed type into the AssemblyDefinition
				if (_typeChanged)
				{
					StoreTimeStamp(swTotal.Elapsed, "Importing type '{0}' into assembly", weaveType.Name);

					targetAssembly.MainModule.Import(type);
				}

				swType.Stop();

				// Update stats
				if (_configuration.WeaveDebugLevel != CecilWeaverConfiguration.WeaveDebug.None)
				{
					_weaveResults.WeaveStatistics.TypesProcessed++;
					_weaveResults.WeaveStatistics.TotalTypeWeaveTime = _weaveResults.WeaveStatistics.TotalTypeWeaveTime.Add(swType.Elapsed);
					_weaveResults.WeaveStatistics.MaxWeaveTimePerType = TimeSpan.FromTicks(Math.Max(_weaveResults.WeaveStatistics.MaxWeaveTimePerType.Ticks, swType.Elapsed.Ticks));
				}
			}

			// Save the modified assembly only if it is changed.
			if (_weaveResults.WeaveStatistics.InputFiltersAdded > 0 || _weaveResults.WeaveStatistics.OutputFiltersAdded > 0 || _weaveResults.WeaveStatistics.InternalsAdded > 0 || _weaveResults.WeaveStatistics.ExternalsAdded > 0)
			{
				// Add the SkipWeave attribute to indicate we have already weaved on this assembly
				StoreTimeStamp(swTotal.Elapsed, "Applying SkipWeave attribute");
				CustomAttribute skipWeave = new CustomAttribute(CecilUtilities.CreateMethodReference(targetAssembly, CachedMethodDefinition.SkipWeaveConstructor));
				targetAssembly.CustomAttributes.Add(skipWeave);

				_weaveResults.SetWeaveResult(WeaveResult.Success);

				StoreTimeStamp(swTotal.Elapsed, "Saving assembly");
				SaveAssembly(targetAssembly, pdbReader);
				StoreTimeStamp(swTotal.Elapsed, "Saved assembly");
			}

			// Stop timing
			swTotal.Stop();
			StoreTimeStamp(swTotal.Elapsed, "Weaving completed");
			_weaveResults.WeaveStatistics.TotalWeaveTime = swTotal.Elapsed;

			return _weaveResults;
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

				}
				catch (Exception ex)
				{
					_weaveResults.Log.LogError(LogOriginName, Properties.Resources.CouldNotLoadAssembly, AssemblySubCategory, CouldNotLoadAssemblyCode,
											_configuration.InputImagePath, ex.Message);
					return null;

				}
				finally
				{
					if (fileStream != null) 
						fileStream.Close();

				}

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
						_weaveResults.Log.LogWarning(LogOriginName, Properties.Resources.ErrorReadingPdb, DebuggerSubcategoryName, ErrorReadingPdbCode, _configuration.DebugImagePath);
					}
				}

				binaryFile = null;

			}
			catch (EndOfStreamException)
			{
				_weaveResults.Log.LogError(LogOriginName, Properties.Resources.ImageIsBad, AssemblySubCategory, ImageIsBadCode,
											_configuration.InputImagePath);
				return null;
				
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
				_weaveResults.Log.LogError(LogOriginName, Properties.Resources.CouldNotSavePdb, AssemblySubCategory, CouldNotSavePdbCode,
											_configuration.DebugImagePath, ex.ToString() );
				_weaveResults.SetWeaveResult(WeaveResult.Error);
			}

			// Save the assembly
			try
			{
				AssemblyFactory.SaveAssembly(targetAssembly, _configuration.OutputImagePath);
			}
			catch (Exception ex)
			{
				_weaveResults.Log.LogError(LogOriginName, Properties.Resources.CouldNotSaveAssembly, AssemblySubCategory, CouldNotSaveAssemblyCode,
											_configuration.OutputImagePath, ex.ToString());
				_weaveResults.SetWeaveResult(WeaveResult.Error);
			}
		}


		#endregion

        /// <summary>
        /// Weave elements on assembly level.
        /// </summary>
        /// <param name="targetAssembly"></param>
        private void WeaveAssembly(AssemblyDefinition targetAssembly)
        {
            #region Check for null
            if (targetAssembly == null)
                throw new ArgumentNullException("targetAssembly");
            #endregion

            MethodReference tr = targetAssembly.MainModule.Import(typeof(ConflictRuleAttribute).GetConstructor(new Type[] { typeof(string), typeof(string), typeof(bool) }));

            foreach (ConflictRule rule in _currentWeaveSpec.ConflictRules)
            {
                CustomAttribute ca = new CustomAttribute(tr);
                ca.ConstructorParameters.Add(rule.Resource);
                ca.ConstructorParameters.Add(rule.Expression);
                ca.ConstructorParameters.Add(rule.Constraint);
                targetAssembly.CustomAttributes.Add(ca);
            }      
        }

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

			foreach (Internal inter in weaveType.Internals)
			{
				string internalTypeString = string.Concat(inter.Namespace, ".", inter.Type);

				TypeReference internalTypeRef = CecilUtilities.ResolveType(internalTypeString, inter.Assembly, "");
				if (internalTypeRef == null)
					throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.TypeNotFound, internalTypeString + " (step 2)"));

				internalTypeRef = targetAssembly.MainModule.Import(internalTypeRef);

				// Create and add the field
				Mono.Cecil.FieldAttributes internalAttrs = Mono.Cecil.FieldAttributes.Private;
				FieldDefinition internalDef = new FieldDefinition(inter.Name, internalTypeRef, internalAttrs);
				type.Fields.Add(internalDef);

				// Increase the number of internals
				_weaveResults.WeaveStatistics.InternalsAdded++;

				// FIXME: shouldn't this throw an exception?
				// FIXME: arrays and strings can't and/or shouldn't be used as internals, 
				//        so why check for these special cases?
				if (internalTypeRef.IsValueType || internalTypeRef.Name == "String" || internalTypeRef.Name == "Array")
				{
					return;
				}

				// Add initialization code to type constructor(s)
				// Get the .ctor() constructor for the internal type
				TypeDefinition internalTypeDef = CecilUtilities.ResolveTypeDefinition(internalTypeRef);
				MethodDefinition internalConstructor = FindInternalConstructor(type, internalTypeDef);

				if (internalConstructor == null)
				{
					throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture, 
						Properties.Resources.NoSuitableInternalConstructor, internalTypeString));
				}

				// Initialize internal in every constructor of the inner type 
				// that does not call another constructor in the same type (ExplicitThis),
				// ensuring we do not initialize the internal twice.
				foreach (MethodDefinition constructor in type.Constructors)
				{
					if (constructor.HasBody && constructor.Body.Instructions.Count > 0
						&& !constructor.IsStatic && !constructor.ExplicitThis)
					{
						// Gets the CilWorker of the method for working with CIL instructions
						CilWorker worker = constructor.Body.CilWorker;

						// Create instructions for invoking the constructor of the internal
						IList<Instruction> instructions = new List<Instruction>();
						instructions.Add(worker.Create(OpCodes.Ldarg_0));
						instructions.Add(worker.Create(OpCodes.Newobj, targetAssembly.MainModule.Import(internalConstructor)));
						instructions.Add(worker.Create(OpCodes.Stfld, internalDef));

						// Add the instructions at the very start of the constructor
						Instruction first = constructor.Body.Instructions[0];
						PrependInstructionList(worker, first, instructions);

						// Log
						StoreInstructionLog(instructions, "Internal code added to {0} for internal {1}", constructor.ToString(), internalDef.ToString());
					}
				}
			}
		}

		/// <summary>
		/// Finds a suitable constructor in the internal for use in the inner.
		/// </summary>
		/// <param name="innerType">The type of the inner that the internal will be used in.</param>
		/// <param name="internalType">The type of the internal that will be added to the inner.</param>
		/// <returns>A suitable constructor definition.</returns>
		private static MethodDefinition FindInternalConstructor(TypeDefinition innerType, TypeDefinition internalType)
		{
			MethodDefinition constructor = null;
		/*
			// first check for a constructor with one argument that has the same type as inner
			constructor = internalType.Constructors.GetConstructor(false, new TypeReference[] { innerType });
			if (constructor != null) return constructor;
		*/
			// then check for a no-arg constructor
			constructor = internalType.Constructors.GetConstructor(false, new TypeReference[] { });
			if (constructor != null) return constructor;

			return null;
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

			foreach (External external in weaveType.Externals)
			{
				TypeReference externalTypeRef = CecilUtilities.ResolveType(external.Type, external.Assembly, "");
				if (externalTypeRef == null)
					throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
						Properties.Resources.TypeNotFound, external.Type));

				externalTypeRef = targetAssembly.MainModule.Import(externalTypeRef);

				// Create the field
				Mono.Cecil.FieldAttributes externalAttrs = Mono.Cecil.FieldAttributes.Private;
				FieldDefinition externalDef = new FieldDefinition(external.Name, externalTypeRef, externalAttrs);

				// Add the field
				type.Fields.Add(externalDef);

				// Increase the number of externals
				_weaveResults.WeaveStatistics.ExternalsAdded++;

				// Get the method referenced by the external
				MethodDefinition initMethodDef = (MethodDefinition)CecilUtilities.ResolveMethod(external.Reference.Selector,
					string.Concat(external.Reference.Namespace, ".", external.Reference.Target),
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
					if (constructor.HasBody && constructor.Body.Instructions.Count > 0
						&& !constructor.IsStatic && !constructor.ExplicitThis)
					{
						// Gets the CilWorker of the method for working with CIL instructions
						CilWorker worker = constructor.Body.CilWorker;

						// Create instructions
						IList<Instruction> instructions = new List<Instruction>();
						instructions.Add(worker.Create(OpCodes.Ldarg_0));
						instructions.Add(worker.Create(OpCodes.Call, initMethodRef));
						instructions.Add(worker.Create(OpCodes.Stfld, externalDef));

						// Add the instructions after the call to the base constructor
						Instruction baseCall = FindBaseConstructorCall(constructor);
						AppendInstructionList(worker, baseCall, instructions);

						// Log
						StoreInstructionLog(instructions, "External code added to {0} for external {1}", constructor.ToString(), externalDef.ToString());
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

            // Do not add filter code to static methods when internals, externals or non-static conditions are used 
            bool hasNonStaticConditions = false;
            if (weaveType.HasConditions)
			{
                foreach (Condition c in weaveType.Conditions)
                {
                    if (c.Reference.Target.Equals(Reference.InnerTarget))
                    {
                        hasNonStaticConditions = true;
                        break;
                    }
                }
            }

			if (method.IsStatic && weaveMethod.HasInputFilters &&
				(weaveType.HasInternals || weaveType.HasExternals || hasNonStaticConditions))
			{
				_weaveResults.Log.LogWarning(LogOriginName, Properties.Resources.NonStaticContextInStaticMethod, CheckerSubCategory, StaticConCode,
					weaveType.Name, method.Name);
				return;
			}
            
			// Add the inputfilters
			if (weaveMethod.HasInputFilters)
				WeaveInputFilters(targetAssembly, method, weaveMethod, weaveType);

			// Add the outputfilters
			if (weaveMethod.HasOutputFilters)
				WeaveOutputFilters(targetAssembly, method, weaveMethod, weaveType);
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
			if (!weaveMethod.HasInputFilters)
				return;

			// Get the input filter code
			int id = weaveMethod.FilterCodeId;
			FilterCode filterCode = _currentWeaveSpec.GeneralizedFilterCodes[id];

			// Only proceed when we have filtercode
			if (filterCode == null)
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
			visitor.WeaveConfiguration = _configuration.WeaveConfiguration;
			visitor.WeaveType = weaveType;
			List<Instruction> instructions = new List<Instruction>();
			visitor.Instructions = instructions;

			// Weave the check conditions:
			Instruction endJump = WeaveCheckCondition(filterCode, visitor);

			// Weave the innercall check task:
			Instruction resetInstruction = WeaveCheckInnerCall(targetAssembly, method, weaveMethod, worker, instructions);

			// Visit the elements in the block
			try
			{
				visitor.DoWeave(filterCode);
			}
			catch (Exception ex)
			{
				// The error wrapped in an ILWeaverException
				throw new ILWeaverException(Properties.Resources.CecilVisitorRaisedException,
											_configuration.OutputImagePath, ex);
			}

			// Add the reset instruction:
			instructions.Add(resetInstruction);

			// Add endJump instruction:
			if (endJump != null)
			{
				instructions.Add(endJump);
			}

			// Only add instructions if we have instructions
			if (visitor.Instructions.Count > 0)
			{
				// Add the instructions
				int instructionsCount = 0;
				instructionsCount += PrependInstructionList(worker, ins, visitor.Instructions);

				// Log
				StoreInstructionLog(visitor.Instructions, "Input filters for {0}", method.ToString());

				// Increase the number of inputfilters added
				_weaveResults.WeaveStatistics.InputFiltersAdded++;
			}

			//
			// What follows are the original instructions
			//
		}

		private Instruction WeaveCheckCondition(FilterCode filterCode, CecilInliningInstructionVisitor instrVisitor)
		{
			ConditionExpression condExpr = filterCode.CheckCondition;
			if (condExpr == null)
			{
				return null;
			}

			CecilConditionsVisitor condVisitor = new CecilConditionsVisitor(instrVisitor);
			((IVisitable) condExpr).Accept(condVisitor);

			// If expression is false, do not execute filter code:
			Instruction endJump = instrVisitor.Worker.Create(OpCodes.Nop);
			instrVisitor.Instructions.Add(instrVisitor.Worker.Create(OpCodes.Brfalse, endJump));
			
			return endJump;
		}

		/// <summary>
		/// Generates the filter context is inner call check.
		/// </summary>
		/// <param name="weaveMethod">The weavespec of the method that is currently weaved</param>
		/// <param name="instructions">The instructionlist to add the instructions to</param>
		/// <returns>The reset instruction</returns>
		/// <example>
		/// Generate the following code:
		/// <code>
		/// if (!FilterContext.IsInnerCall(this, methodName))
		/// {
		/// <b>filtercode</b>
		/// }
		/// FilterContext.ResetInnerCall();
		/// </code>
		/// The <b>filtercode</b> are the inputfilters added to the method.
		/// </example>
		private static Instruction WeaveCheckInnerCall(AssemblyDefinition targetAssembly, MethodDefinition method,
			WeaveMethod weaveMethod, CilWorker worker, List<Instruction> instructions)
		{
			// Load the this parameter
			if (!method.HasThis || method.DeclaringType.IsValueType)
				instructions.Add(worker.Create(OpCodes.Ldnull));
			else
				instructions.Add(worker.Create(OpCodes.Ldarg, method.This));

			// Load the methodId
			instructions.Add(worker.Create(OpCodes.Ldc_I4, weaveMethod.Id));

			// Call the IsInnerCall
			instructions.Add(worker.Create(OpCodes.Call, CecilUtilities.CreateMethodReference(targetAssembly, CachedMethodDefinition.IsInnerCall)));

			// Create the call instruction
			Instruction resetInstruction = worker.Create(OpCodes.Call, CecilUtilities.CreateMethodReference(targetAssembly, CachedMethodDefinition.ResetInnerCall));

			// Result is placed on the stack, so use it to branch to the skipFiltersInstruction
			instructions.Add(worker.Create(OpCodes.Brtrue, resetInstruction));

			// Return the reset instruction
			return resetInstruction;
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
				// Check for a call instruction
				if (IsCallInstruction(instruction))
					callInstructions.Add(instruction);

			foreach (Instruction instruction in callInstructions)
			{
				// Find the corresponding call in the list of calls
				MethodReference mr = (MethodReference)(instruction.Operand);
				MethodDefinition md = CecilUtilities.ResolveMethodDefinition(mr);
				FilterCode outputFilter = GetOutputFilterForCall(weaveMethod.Calls, md.ToString());

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
						instructionsCount += ReplaceWithInstructionList(worker, instruction, visitor.Instructions);

						// Log
						StoreInstructionLog(visitor.Instructions, "Output filters({2}) for {0} call {1}", method.ToString(), md.ToString(), _weaveResults.WeaveStatistics.OutputFiltersAdded);

						// Increase the number of output filters added
						_weaveResults.WeaveStatistics.OutputFiltersAdded++;
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
			if (!_disposed)
			{
				if (disposing)
				{
					// Dispose managed resources
				}

				// Dispose unmanaged resources
			}

			_disposed = true;
		}

		private bool _disposed;

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
					Console.WriteLine(" {5}: {0}:{1}-{2}:{3}:{4} ", 
						instruction.SequencePoint.StartLine,
						instruction.SequencePoint.StartColumn, 
						instruction.SequencePoint.EndLine,
						instruction.SequencePoint.EndColumn, 
						Path.GetFileName(instruction.SequencePoint.Document.Url),
						instruction.OpCode.ToString());
			}
			Console.WriteLine();
		}

		/// <summary>
		/// Returns the instruction of the base call in the specified constructor.
		/// </summary>
		/// <param name="constructor"></param>
		/// <returns></returns>
		private Instruction FindBaseConstructorCall(MethodDefinition constructor)
		{
			if (constructor.ExplicitThis)
			{
				throw new ILWeaverException(
					string.Format("Constructor {0} does not contain a base call.", constructor));
			}
			
			// the first call instruction should be it
			foreach (Instruction instr in constructor.Body.Instructions)
			{
				if (instr.OpCode == OpCodes.Call)
				{
					MethodReference target = (MethodReference)instr.Operand;
					if (target.Name != MethodDefinition.Ctor)
					{
						throw new ILWeaverException(
							string.Format("Expected base constructor call, but encountered call to {0}.", target));
					}
					
					return instr;
				}
			}

			throw new ILWeaverException(
				string.Format("Could not find base constructor call in {0}.", constructor));
		}

		/// <summary>
		/// Inserts a list of instructions before the specified instruction.
		/// </summary>
		/// <param name="worker">The worker.</param>
		/// <param name="startInstruction">The start instruction.</param>
		/// <param name="instructionsToAdd">The instructions to add.</param>
		/// <returns>The number of instructions that were added.</returns>
		private static int PrependInstructionList(CilWorker worker, Instruction startInstruction, IList<Instruction> instructionsToAdd)
		{
			foreach (Instruction instr in instructionsToAdd)
			{
				worker.InsertBefore(startInstruction, instr);
			}

			return instructionsToAdd.Count;
		}

		/// <summary>
		/// Inserts a list of instructions after a specified instruction.
		/// </summary>
		/// <param name="worker">The worker.</param>
		/// <param name="startInstruction">The start instruction.</param>
		/// <param name="instructionsToAdd">The instructions to add.</param>
		/// <returns>The number of instructions that were added.</returns>
		[SuppressMessage("Microsoft.Performance", "CA1811:AvoidUncalledPrivateCode", Justification = "Might be used in the future.")]
		private static int AppendInstructionList(CilWorker worker, Instruction startInstruction, IList<Instruction> instructionsToAdd)
		{
			foreach (Instruction instr in instructionsToAdd)
			{
				worker.InsertAfter(startInstruction, instr);
				startInstruction = instr;
			}

			return instructionsToAdd.Count;
		}

		/// <summary>
		/// Replaces the specified instruction with a list of instructions.
		/// </summary>
		/// <param name="worker">The worker.</param>
		/// <param name="startInstruction">The instruction to replace.</param>
		/// <param name="instructionsToAdd">The instructions to add.</param>
		/// <returns>
		/// The number of instructions that were added.
		/// This is one less than the length of the instruction list because one existing instruction was replaced.
		/// </returns>
		private static int ReplaceWithInstructionList(CilWorker worker, Instruction startInstruction, IList<Instruction> instructionsToAdd)
		{
			// FIXME: what if startInstruction is used as a jump target?

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
		/// Gets the output filter FilterCode for call.
		/// </summary>
		/// <param name="weaveCalls">Weave calls</param>
		/// <param name="callSignature">Call signature</param>
		/// <returns>The output filter FilterCode.</returns>
		private FilterCode GetOutputFilterForCall(List<WeaveCall> weaveCalls, string callSignature)
		{
			foreach (WeaveCall wc in weaveCalls)
			{
				if (wc.MethodName.Equals(callSignature))
				{
					int id = wc.FilterCodeId;
					if (id < 0)
					{
						return null;
					}

					return _currentWeaveSpec.GeneralizedFilterCodes[id];
				}
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
				_weaveResults.WeaveStatistics.InstructionsLog.Add(caption, formattedList);
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
				_weaveResults.WeaveStatistics.TimingStack.Enqueue(item);
			}
		}
		#endregion
	}
}
