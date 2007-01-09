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
using Composestar.StarLight.CoreServices.Analyzer;
using Composestar.StarLight.CoreServices.Logger;
using Composestar.StarLight.Entities.Configuration;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Filters.FilterTypes;
using Mono.Cecil;
using Mono.Cecil.Binary;
using Mono.Cecil.Cil;
using Mono.Cecil.Metadata;
using Mono.Cecil.Signatures;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Diagnostics;
using System.Diagnostics.CodeAnalysis;
using System.Globalization;
using System.IO;
using System.Reflection;
using System.Security.Policy;  //for evidence objec;
using System.Text;
#endregion

namespace Composestar.StarLight.ILAnalyzer
{
	/// <summary>
	/// Visitor to visit all the relevant assembly items and returns an <see cref="AssemblyElement"></see>.
	/// </summary>
	[CLSCompliant(false)]
	public class CecilAssemblyVisitor : BaseReflectionVisitor
	{
		#region Constants

		private const string ModuleName = "<Module>";
		private const string LogSubCategory = "visitor";
		private const string AnalyzerOrigin = "analyzer";

		private string _skipWeavingAttribute = typeof(SkipWeavingAttribute).FullName;
		private string _processPropertiesAttribute = typeof(ProcessPropertiesAttribute).FullName;
 
		#endregion

		#region Private variables

		private IAnalyzerResults _results;

		private AssemblyElement _assemblyElement = new AssemblyElement();
		private AssemblyDefinition _assembly;

		private IList<string> _resolvedAssemblies = new List<string>();
		private IList<string> _unresolvedAssemblies = new List<string>();
		private IList<string> _cachedTypes = new List<string>();
		private IList<string> _resolvedTypes = new List<string>();
		private IList<string> _unresolvedTypes = new List<string>();

		private bool _saveType;
		private bool _saveInnerType;
		private bool _processMethodBody = true;
		private bool _processProperties;
		private bool _processAttributes;
		private bool _includeFields = true;
		private bool _extractUnresolvedOnly;

		private IList<FilterTypeElement> _filterTypes = new List<FilterTypeElement>();
		private IList<FilterActionElement> _filterActions = new List<FilterActionElement>();

		private TypeElement _currentType;
		#endregion

		#region Properties

		/// <summary>
		/// Gets or sets the results. Can be used to log.
		/// </summary>
		/// <value>The results.</value>
		public IAnalyzerResults Results
		{
			get { return _results; }
			set { _results = value; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether to process and extract properties.
		/// </summary>
		/// <value><c>true</c> if to process properties; otherwise, <c>false</c>.</value>
		public bool ProcessProperties
		{
			get { return _processProperties; }
			set { _processProperties = value; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether to extract unresolved only.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if only unresolved only must be extracted; otherwise, <c>false</c>.
		/// </value>
		public bool ExtractUnresolvedOnly
		{
			get { return _extractUnresolvedOnly; }
			set { _extractUnresolvedOnly = value; }
		}

		/// <summary>
		/// Gets or sets the list of resolved types.
		/// </summary>
		/// <value>The resolved types.</value>
		public IList<string> ResolvedTypes
		{
			get { return _resolvedTypes; }
			set { _resolvedTypes = value; }
		}

		/// <summary>
		/// Gets or sets the list of unresolved types.
		/// </summary>
		/// <value>The unresolved types.</value>
		public IList<string> UnresolvedTypes
		{
			get { return _unresolvedTypes; }
			set { _unresolvedTypes = value; }
		}

		/// <summary>
		/// Gets the list of filter actions.
		/// </summary>
		/// <value>The filter actions.</value>
		public IList<FilterActionElement> FilterActions
		{
			get { return _filterActions; }
		}

		/// <summary>
		/// Gets the list of filter types.
		/// </summary>
		/// <value>The filter types.</value>
		public IList<FilterTypeElement> FilterTypes
		{
			get { return _filterTypes; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether to include fields.
		/// </summary>
		/// <value><c>true</c> if fields must be included; otherwise, <c>false</c>.</value>
		public bool IncludeFields
		{
			get { return _includeFields; }
			set { _includeFields = value; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether to process attributes.
		/// </summary>
		/// <value><c>true</c> if attributes must be processed; otherwise, <c>false</c>.</value>
		public bool ProcessAttributes
		{
			get { return _processAttributes; }
			set { _processAttributes = value; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether to process method bodies.
		/// </summary>
		/// <value><c>true</c> if method bodies must be processed; otherwise, <c>false</c>.</value>
		public bool ProcessMethodBody
		{
			get { return _processMethodBody; }
			set { _processMethodBody = value; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether to save inner types.
		/// </summary>
		/// <value><c>true</c> if inner type must be saved; otherwise, <c>false</c>.</value>
		public bool SaveInnerType
		{
			get { return _saveInnerType; }
			set { _saveInnerType = value; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether to save types.
		/// </summary>
		/// <value><c>true</c> if types must be saved; otherwise, <c>false</c>.</value>
		public bool SaveType
		{
			get { return _saveType; }
			set { _saveType = value; }
		}

		/// <summary>
		/// Gets or sets a list of cached types.
		/// </summary>
		/// <value>The cached types.</value>
		public IList<string> CachedTypes
		{
			get { return _cachedTypes; }
			set { _cachedTypes = value; }
		}

		/// <summary>
		/// Gets or sets a list of unresolved assemblies.
		/// </summary>
		/// <value>The unresolved assemblies.</value>
		public IList<string> UnresolvedAssemblies
		{
			get { return _unresolvedAssemblies; }
			set { _unresolvedAssemblies = value; }
		}

		/// <summary>
		/// Gets or sets a list of resolved assemblies.
		/// </summary>
		/// <value>The resolved assemblies.</value>
		public IList<string> ResolvedAssemblies
		{
			get { return _resolvedAssemblies; }
			set { _resolvedAssemblies = value; }
		}

		#endregion

		#region Filter Type Naming

		private string _filterTypeName = typeof(Composestar.StarLight.Filters.FilterTypes.FilterType).FullName;
		private string _filterActionName = typeof(Composestar.StarLight.Filters.FilterTypes.FilterAction).FullName;

		#endregion

		#region Analyze function

		/// <summary>
		/// Analyze the specified assembly filename.
		/// </summary>
		/// <param name="assemblyFilename">The assembly filename.</param>
		/// <returns></returns>
		public AssemblyElement Analyze(string assemblyFileName)
		{
			try
			{
				// Load assembly for processing by Mono.Cecil
				_assembly = AssemblyFactory.GetAssembly(assemblyFileName);
			}
			catch (EndOfStreamException)
			{
				throw new BadImageFormatException(string.Format(CultureInfo.CurrentCulture, Properties.Resources.ImageIsBad, assemblyFileName));
			}

			AddResolvedAssemblyList(_assembly.Name.ToString());

			// Fill the assemblyElement values
			_assemblyElement.Name = _assembly.Name.ToString();
			_assemblyElement.FileName = assemblyFileName;

			if (HasEnabledProcessPropertiesAttribute(_assembly.CustomAttributes))
			{
				// override the ProcessProperties setting
				ProcessProperties = true;
				// Log this occurrence
				Results.Log.LogWarning(AnalyzerOrigin, Properties.Resources.ProcessPropertiesEnabled, LogSubCategory, "C00010", _assembly.Name); 
			}

			// Get all the types defined in the main module. Typically you won't need
			// to worry that your assembly contains more than one module
			foreach (TypeDefinition type in _assembly.MainModule.Types)
			{
				if (type.Name.Equals(ModuleName))
					continue;

				type.Accept(this);
			}

			// Remove types without a proper assembly name, e.g. the generic identifiers T, V, K, TKey, TValue, TOutput, TItem
			IList<string> unresolvedtypes = new List<string>(UnresolvedTypes);
			foreach (string type in unresolvedtypes)
			{
				if (type.EndsWith(", NULL"))
				{
					UnresolvedTypes.Remove(type);
				}
			}

			// Check assembly attributes. 
			// We do this here, because we still want to seach for FilterTypes and FilterActions
			if (SkipWeaving(_assembly.CustomAttributes))
				return null;

			// Return the assembly element
			return _assemblyElement;
		}

		#endregion

		#region Visitor Implementation

		/// <summary>
		/// Visits the type definition.
		/// </summary>
		/// <param name="type">The type.</param>
		[CLSCompliant(false)]
		public override void VisitTypeDefinition(TypeDefinition type)
		{
			// Create a new typeElement
			TypeElement typeElement = new TypeElement();

			bool processPropertiesInThisType = HasEnabledProcessPropertiesAttribute(type.CustomAttributes);

			//
			// Fill the properties
			//

			// Name
			typeElement.Name = type.Name;

			if (string.IsNullOrEmpty(type.Namespace))
			{
				if (type.DeclaringType != null)
					typeElement.Namespace = string.Concat(type.DeclaringType.FullName, "+");

			}
			else
				typeElement.Namespace = type.Namespace;

			// Properties
			typeElement.IsAbstract = type.IsAbstract;
			typeElement.IsEnum = type.IsEnum;
			typeElement.IsInterface = type.IsInterface;
			typeElement.IsSealed = type.IsSealed;
			typeElement.IsValueType = type.IsValueType;
			typeElement.IsClass = !type.IsValueType & !type.IsInterface;
			typeElement.IsPrimitive = false;
			typeElement.IsPublic = type.Attributes == Mono.Cecil.TypeAttributes.Public;

			// Interface
			foreach (TypeReference interfaceDef in type.Interfaces)
			{
				typeElement.Interfaces.Add(interfaceDef.FullName);
			}

			// Basetype
			if (type.BaseType != null)
			{
				typeElement.BaseType = type.BaseType.FullName;

				AddUnresolvedAssemblyList(type.BaseType);

				// Check whether type is a FilterType:
				if (type.BaseType.FullName.Equals(_filterTypeName))
				{
					ExtractFilterType(type);
				}

				// Check whether type is a FilterAction:
				if (type.BaseType.FullName.Equals(_filterActionName))
				{
					ExtractFilterAction(type);
				}

				// We may need the base class
				AddUnresolvedType(type.BaseType);
			}

			// Check if a SkipWeaving attribute is set. 
			// The FilterTypes and FilterActions are collected, but the type is not saved.
			if (!SkipWeaving(type.CustomAttributes))
			{				
				if (!ExtractUnresolvedOnly || _unresolvedTypes.Contains(CreateTypeName(type)))
				{
					// Get custom attributes
					if (!ExtractUnresolvedOnly)
						typeElement.Attributes.AddRange(ExtractCustomAttributes(type.CustomAttributes));

					_currentType = typeElement;

					// Properties
					if (ProcessProperties || processPropertiesInThisType)
					{
						foreach (PropertyDefinition property in type.Properties)
						{
							if (SkipWeaving(property.CustomAttributes))
								continue;

							if (property.SetMethod != null)
								property.SetMethod.Accept(this);

							if (property.GetMethod != null)
								property.GetMethod.Accept(this);
						}
					}

					// Methods
					foreach (MethodDefinition method in type.Methods)
					{
						// check if we have to skip this method
						if (SkipWeaving(method.CustomAttributes))
							continue;

						// skip unmanaged code
						if (method.ImplAttributes == Mono.Cecil.MethodImplAttributes.Unmanaged)
							continue;

						// properties are handled elsewhere
						if (method.SemanticsAttributes == MethodSemanticsAttributes.Getter ||
							method.SemanticsAttributes == MethodSemanticsAttributes.Setter)
							continue;

						method.Accept(this);
					}

					// Fields
					foreach (FieldDefinition field in type.Fields)
					{
						if (SkipWeaving(field.CustomAttributes))
							continue;

						field.Accept(this);
					}

					_assemblyElement.Types.Add(_currentType);

					// Add this type to the resolved types
					AddResolvedType(type);

					// Remove from unresolved
					_unresolvedTypes.Remove(CreateTypeName(type));
				}
			}
		}

		/// <summary>
		/// Visits the method definition.
		/// </summary>
		/// <param name="method">The method.</param>
		[CLSCompliant(false)]
		public override void VisitMethodDefinition(MethodDefinition method)
		{
			// If we only extract the unresolvedtypes then we most likely are only interested
			// in methods which can be overriden. So skip the rest.
			if (ExtractUnresolvedOnly && !method.IsVirtual)
				return;

			// Create a new method element
			MethodElement me = new MethodElement();
			me.Name = method.Name;
			me.ReturnType = method.ReturnType.ReturnType.FullName;			
			me.IsAbstract = method.IsAbstract;
			me.IsConstructor = method.IsConstructor;
			me.IsPrivate = method.Attributes == Mono.Cecil.MethodAttributes.Private;
			me.IsPublic = method.Attributes == Mono.Cecil.MethodAttributes.Public;
			me.IsStatic = method.IsStatic;
			me.IsVirtual = method.IsVirtual;

			// Add the parameters
			foreach (ParameterDefinition param in method.Parameters)
			{
				ParameterElement pe = new ParameterElement();

				pe.Name = param.Name;
				pe.Ordinal = (short)(param.Sequence);
				pe.Type = param.ParameterType.FullName;

				if ((param.Attributes & Mono.Cecil.ParameterAttributes.Out) != Mono.Cecil.ParameterAttributes.Out)
					pe.ParameterOption |= ParameterOptions.In;
				else
					pe.ParameterOption &= ~ParameterOptions.In;
				
				if ((param.Attributes & Mono.Cecil.ParameterAttributes.Out) == Mono.Cecil.ParameterAttributes.Out)
					pe.ParameterOption |= ParameterOptions.Out;
				
				if ((param.Attributes & Mono.Cecil.ParameterAttributes.Optional) == Mono.Cecil.ParameterAttributes.Optional)
					pe.ParameterOption |= ParameterOptions.Optional;

				// Remark; we do not harvest custom attributes here. 
				
				me.Parameters.Add(pe);
			}

			// Add the method body
			if (ProcessMethodBody && method.HasBody)
			{
				me.Body = new Entities.LanguageModel.MethodBody();

				List<string> callList = new List<string>();

				foreach (Instruction instr in method.Body.Instructions)
				{
					if (instr.OpCode.Value == OpCodes.Call.Value ||
						instr.OpCode.Value == OpCodes.Calli.Value ||
						instr.OpCode.Value == OpCodes.Callvirt.Value
						)
					{
						CallElement ce = new CallElement();

						// instr.Operand can be a MethodReference or a CallSite
						ce.MethodReference = instr.Operand.ToString();

						if (!callList.Contains(ce.MethodReference))
						{
							me.Body.Calls.Add(ce);
							callList.Add(ce.MethodReference);
						}
					}
				}
			}

			// Custom attributes
			me.Attributes.AddRange(ExtractCustomAttributes(method.CustomAttributes));

			// Set the signature
			string declaringTypeName = method.DeclaringType.FullName;
			me.Signature = MethodElement.GenerateSignature(declaringTypeName, me);

			// Add to the type
			_currentType.Methods.Add(me);
		}

		/// <summary>
		/// Visits the field definition.
		/// </summary>
		/// <param name="field">The field.</param>
		[CLSCompliant(false)]
		public override void VisitFieldDefinition(FieldDefinition field)
		{
			if (!_includeFields) return;

			FieldElement fe = new FieldElement();

			fe.Name = field.Name;
			fe.Type = field.FieldType.FullName;

			fe.IsPrivate = field.Attributes == Mono.Cecil.FieldAttributes.Private;
			fe.IsPublic = field.Attributes == Mono.Cecil.FieldAttributes.Public;
			fe.IsStatic = field.IsStatic;

			// Custom attributes
			fe.Attributes.AddRange(ExtractCustomAttributes(field.CustomAttributes));

			_currentType.Fields.Add(fe);
		}

		#endregion

		#region Filter Actions

		/// <summary>
		/// Extracts the filter action.
		/// </summary>
		/// <param name="type">The type.</param>       
		private void ExtractFilterAction(TypeDefinition type)
		{
			// We use .NET reflection here, because Cecil can not read the enumerations

			// TODO Loading an assembly locks the assembly for the duration of the appdomain.
			// The weaver can no loner access the file to weave in it.
			// We now use ShadowCopy to overcome this. It is an obsolete method
			// Switch to a separate appdomain.

			SetupReflectionAssembly(type.Module.Image.FileInformation.Directory.FullName);

			Assembly assembly = Assembly.LoadFrom(type.Module.Image.FileInformation.FullName);

			if (assembly == null)
			{
				throw new ILAnalyzerException(string.Format(CultureInfo.CurrentCulture, Properties.Resources.CouldNotFindAssembly, type.Module.Image.FileInformation.FullName));
			}

			Type refType = assembly.GetType(type.FullName);

			if (refType == null)
			{
				throw new ILAnalyzerException(string.Format(CultureInfo.CurrentCulture, Properties.Resources.CouldNotFindType, type.FullName));
			}

			FilterActionAttribute[] faas = (FilterActionAttribute[])refType.GetCustomAttributes(typeof(FilterActionAttribute), true);
			foreach (FilterActionAttribute faa in faas)
			{
				FilterActionElement faEl = new FilterActionElement();

				faEl.FullName = type.FullName;
				faEl.Name = faa.ActionName;
				faEl.Assembly = type.Module.Assembly.Name.ToString();

				switch (faa.FlowBehavior)
				{
					case FilterActionAttribute.FilterFlowBehavior.Continue:
						faEl.FlowBehavior = FilterActionElement.FlowContinue;
						break;
					case FilterActionAttribute.FilterFlowBehavior.Exit:
						faEl.FlowBehavior = FilterActionElement.FlowExit;
						break;
					case FilterActionAttribute.FilterFlowBehavior.Return:
						faEl.FlowBehavior = FilterActionElement.FlowReturn;
						break;
					default:
						faEl.FlowBehavior = FilterActionElement.FlowContinue;
						break;
				}

				switch (faa.SubstitutionBehavior)
				{
					case FilterActionAttribute.MessageSubstitutionBehavior.Original:
						faEl.MessageChangeBehavior = FilterActionElement.MessageOriginal;
						break;
					case FilterActionAttribute.MessageSubstitutionBehavior.Substituted:
						faEl.MessageChangeBehavior = FilterActionElement.MessageSubstituted;
						break;
					case FilterActionAttribute.MessageSubstitutionBehavior.Any:
						faEl.MessageChangeBehavior = FilterActionElement.MessageAny;
						break;
					default:
						faEl.MessageChangeBehavior = FilterActionElement.MessageOriginal;
						break;
				}

				faEl.CreateJPC = faa.CreateJoinPointContext;

				_filterActions.Add(faEl);
			}

			type = null;
			assembly = null;

			return;
		}

		private string m_rootAssembly;

		/// <summary>
		/// Mies the reflection only resolve event handler.
		/// </summary>
		/// <param name="sender">The sender.</param>
		/// <param name="args">The <see cref="T:System.ResolveEventArgs"/> instance containing the event data.</param>
		/// <returns></returns>
		private Assembly MyReflectionOnlyResolveEventHandler(object sender, ResolveEventArgs args)
		{
			AssemblyName name = new AssemblyName(args.Name);

			string asmToCheck = Path.GetDirectoryName(m_rootAssembly) + "\\" + name.Name + ".dll";

			if (File.Exists(asmToCheck))
			{
				return Assembly.ReflectionOnlyLoadFrom(asmToCheck);
			}

			return Assembly.ReflectionOnlyLoad(args.Name);
		}

		private bool _reflectionAssemblySetup;

		/// <summary>
		/// Setups the reflection assembly.
		/// </summary>
		/// <param name="rootPath">The root path.</param>
		[SuppressMessage("Microsoft.Performance", "CA1804:RemoveUnusedLocals", Target = "assmContext")]
		[SuppressMessage("Microsoft.Performance", "CA1804:RemoveUnusedLocals", Target = "assmFilters")]
		private void SetupReflectionAssembly(string rootPath)
		{
			if (_reflectionAssemblySetup) return;

			AppDomain.CurrentDomain.SetShadowCopyFiles();
			AppDomain.CurrentDomain.AppendPrivatePath(rootPath);
			m_rootAssembly = rootPath;
			AppDomain.CurrentDomain.ReflectionOnlyAssemblyResolve += new ResolveEventHandler(MyReflectionOnlyResolveEventHandler);

			// We have to inject the ContextInfo into the domain, or we cannot find the specific type.
			Assembly assmContext =
				Assembly.LoadFile(Path.Combine(rootPath, "Composestar.StarLight.ContextInfo.dll"));
			Assembly assmFilters =
			  Assembly.LoadFile(Path.Combine(rootPath, "Composestar.StarLight.Filters.dll"));

			_reflectionAssemblySetup = true;
		}

		/// <summary>
		/// Extracts the type of the filter.
		/// </summary>
		/// <param name="type">The type.</param>
		private void ExtractFilterType(TypeDefinition type)
		{
			// We use .NET reflection here, because Cecil can not read the enumerations

			// TODO Loading an assembly locks the assembly for the duration of the appdomain.
			// The weaver can no loner access the file to weave in it.
			// We now use ShadowCopy to overcome this. It is an obsolete method
			// Switch to a separate appdomain.

			SetupReflectionAssembly(type.Module.Image.FileInformation.Directory.FullName);

			Assembly assembly = Assembly.LoadFrom(type.Module.Image.FileInformation.FullName);

			if (assembly == null)
			{
				throw new ILAnalyzerException(string.Format(CultureInfo.CurrentCulture, Properties.Resources.CouldNotFindAssembly, type.Module.Image.FileInformation.FullName));
			}

			Type refType = assembly.GetType(type.FullName);

			if (refType == null)
			{
				throw new ILAnalyzerException(string.Format(CultureInfo.CurrentCulture, Properties.Resources.CouldNotFindType, type.FullName));
			}

			FilterTypeAttribute[] ftas = (FilterTypeAttribute[])refType.GetCustomAttributes(typeof(FilterTypeAttribute), true);
			foreach (FilterTypeAttribute fta in ftas)
			{
				FilterTypeElement ftEl = new FilterTypeElement();
				_filterTypes.Add(ftEl);

				ftEl.Name = fta.Name;
				ftEl.AcceptCallAction = fta.AcceptCallAction;
				ftEl.RejectCallAction = fta.RejectCallAction;
				ftEl.AcceptReturnAction = fta.AcceptReturnAction;
				ftEl.RejectReturnAction = fta.RejectReturnAction;
			}
		}

		#endregion

		#region Helper Functions

		#region Resolve and unresolved functions

		/// <summary>
		/// Adds the assembly to the resolved list.
		/// </summary>
		/// <param name="assemblyName">Assembly name</param>
		private void AddResolvedAssemblyList(string assemblyName)
		{
			// Add to resolved
			if (!_resolvedAssemblies.Contains(assemblyName))
				_resolvedAssemblies.Add(assemblyName);

			// remove from unresolved
			_unresolvedAssemblies.Remove(assemblyName);

		}

		/// <summary>
		/// Adds the assembly to the unresolved list.
		/// </summary>
		/// <param name="assemblyName">Name of the assembly.</param>
		private void AddUnresolvedAssemblyList(string assemblyName)
		{
			// Check if the assembly is not yet resolved.
			if (!_resolvedAssemblies.Contains(assemblyName))
			{
				if (!_unresolvedAssemblies.Contains(assemblyName))
				{
					_unresolvedAssemblies.Add(assemblyName);
				}
			}

		}

		/// <summary>
		/// Adds the assembly to the unresolved list.
		/// </summary>
		/// <param name="typeReference">The type reference.</param>
		private void AddUnresolvedAssemblyList(TypeReference typeReference)
		{
			if (typeReference == null)
				return;

			if (typeReference.Scope != null)
			{
				// Locally declared type
				if (typeReference.Scope is ModuleDefinition)
				{
					if (((ModuleDefinition)typeReference.Scope).Assembly != null)
					{
						AddUnresolvedAssemblyList(((ModuleDefinition)typeReference.Scope).Assembly.Name.FullName);
					}
				}

				// Referenced type
				foreach (AssemblyNameReference assembly in _assembly.MainModule.AssemblyReferences)
				{
					if (typeReference.Scope.Name == assembly.Name)
					{
						AddUnresolvedAssemblyList(assembly.FullName);
					}
				}
			}
		}

		/// <summary>
		/// Adds the type of the resolved.
		/// </summary>
		/// <param name="typeName">Name of the type.</param>
		private void AddResolvedType(string typeName)
		{
			// Add to resolved
			if (!_resolvedTypes.Contains(typeName))
				_resolvedTypes.Add(typeName);

			// remove from unresolved
			_unresolvedTypes.Remove(typeName);
		}

		/// <summary>
		/// Adds the type of the unresolved.
		/// </summary>
		/// <param name="typeName">Name of the type.</param>
		private void AddUnresolvedType(string typeName)
		{
			// Check if the assembly is not yet resolved.
			if (!_resolvedTypes.Contains(typeName))
			{
				if (!_unresolvedTypes.Contains(typeName))
				{
					_unresolvedTypes.Add(typeName);
				}
			}
		}

		/// <summary>
		/// Adds the type of the unresolved.
		/// </summary>
		/// <param name="type">The type.</param>
		private void AddUnresolvedType(TypeReference type)
		{
			AddUnresolvedType(CreateTypeName(type));
		}

		/// <summary>
		/// Adds the type of the resolved.
		/// </summary>
		/// <param name="type">The type.</param>
		private void AddResolvedType(TypeReference type)
		{
			AddResolvedType(CreateTypeName(type));
		}

		#endregion

		/// <summary>
		/// Visits the custom attributes.
		/// </summary>
		/// <param name="customAttributes">The custom attributes.</param>
		/// <returns></returns>
		private static List<AttributeElement> ExtractCustomAttributes(CustomAttributeCollection customAttributes)
		{
			List<AttributeElement> ret = new List<AttributeElement>();

			if (customAttributes == null || customAttributes.Count == 0)
				return ret;

			foreach (CustomAttribute ca in customAttributes)
			{
				AttributeElement ae = new AttributeElement();
				if (ca.Constructor.DeclaringType == null)
					continue;

				ae.AttributeType = ca.Constructor.DeclaringType.ToString();

				for (int i = 0; i < ca.ConstructorParameters.Count; i++)
				{
					AttributeValueElement ave = new AttributeValueElement();
					ave.Name = ca.Constructor.Parameters[i].Name;
					if (ca.ConstructorParameters[i] == null)
						ave.Value = null;
					else
						ave.Value = ca.ConstructorParameters[i].ToString().Replace("\0", "");
					ae.Values.Add(ave);
				}

				foreach (object propKey in ca.Properties.Keys)
				{
					AttributeValueElement ave = new AttributeValueElement();
					ave.Name = Convert.ToString(propKey, CultureInfo.InvariantCulture);
					if (ca.Properties[propKey] == null)
						ave.Value = null;
					else
						ave.Value = ca.Properties[propKey].ToString();
					ae.Values.Add(ave);
				}

				ret.Add(ae);
			}

			return ret;
		}

		/// <summary>
		/// Creates the name of the type.
		/// </summary>
		/// <param name="type">The type.</param>
		/// <returns></returns>
		[SuppressMessage("Microsoft.Performance", "CA1811:AvoidUncalledPrivateCode")]
		private static string CreateTypeName(TypeReference type)
		{
			string typeName = type.FullName;

			if (typeName.Contains("`")) typeName = string.Concat(type.Namespace, ".", type.Name);
			if (typeName.EndsWith("&")) typeName = typeName.Substring(0, typeName.Length - 1);
			if (typeName.EndsWith("**")) typeName = typeName.Substring(0, typeName.Length - 2);
			if (typeName.EndsWith("*")) typeName = typeName.Substring(0, typeName.Length - 1);
			if (typeName.Contains("[")) typeName = typeName.Substring(0, typeName.IndexOf("[", 0));
			if (typeName.Contains(" modreq(")) typeName = typeName.Substring(0, typeName.IndexOf(" modreq(", 0));
			if (typeName.Contains(" modopt(")) typeName = typeName.Substring(0, typeName.IndexOf(" modopt(", 0));

			return typeName;
		}

		/// <summary>
		/// Creates the type Assembly Fully Qualified Name.
		/// </summary>
		/// <param name="targetAssemblyDefinition">The target assembly definition.</param>
		/// <param name="type">The type.</param>
		/// <returns></returns>
		[SuppressMessage("Microsoft.Performance", "CA1811:AvoidUncalledPrivateCode")]
		private static string CreateTypeAFQN(AssemblyDefinition targetAssemblyDefinition, TypeReference type)
		{
			return string.Format(CultureInfo.CurrentCulture, "{0}, {1}", CreateTypeName(type), CreateAFQN(targetAssemblyDefinition, type));
		}

		/// <summary>
		/// Creates the Assembly Fully Qualified Name.
		/// </summary>
		/// <param name="targetAssemblyDefinition">The target assembly definition.</param>
		/// <param name="type">The type.</param>
		/// <returns></returns>
		[SuppressMessage("Microsoft.Performance", "CA1811:AvoidUncalledPrivateCode")]
		private static string CreateAFQN(AssemblyDefinition targetAssemblyDefinition, TypeReference type)
		{
			if (targetAssemblyDefinition == null)
				throw new ArgumentNullException("targetAssemblyDefinition");

			if (type == null)
				throw new ArgumentNullException("type");

			if (type.Scope != null)
			{
				// Locally declared type
				if (type.Scope is ModuleDefinition)
				{
					ModuleDefinition scope = (ModuleDefinition)type.Scope;
					if (scope.Assembly != null)
					{
						return scope.Assembly.Name.FullName;
					}
				}

				// Referenced type
				foreach (AssemblyNameReference assembly in targetAssemblyDefinition.MainModule.AssemblyReferences)
				{
					if (type.Scope.Name == assembly.Name)
					{
						return assembly.FullName;
					}
				}
			}

			return "NULL";
		}

		/// <summary>
		/// Skip weaving based on the SkipWeavingAttribute specified in the CustomAttributeCollection.
		/// </summary>
		/// <param name="attributes">The Custom Attribute Collection of an element.</param>
		/// <returns>Return <see langword="true"/> when one of the custom attributes indicates it should not be weaved on.</returns>
		private bool SkipWeaving(CustomAttributeCollection attributes)
		{
			if (attributes == null || attributes.Count == 0)
				return false;

			foreach (CustomAttribute  attribute in attributes)
			{
				if (attribute.Constructor.DeclaringType.FullName.Equals(_skipWeavingAttribute))
				{
					if (attribute.ConstructorParameters.Count == 1 && attribute.ConstructorParameters[0] != null)
						return (Convert.ToBoolean(attribute.ConstructorParameters[0], CultureInfo.InvariantCulture));

					if (attribute.Properties["Enabled"] != null)
						return Convert.ToBoolean(attribute.Properties["Enabled"], CultureInfo.InvariantCulture);

					return true;	
				}
			}

			return false; 
		}

		/// <summary>
		/// Determines whether the custom attribute collection has an enabled process properties attribute.
		/// </summary>
		/// <param name="attributes">The attributes.</param>
		/// <returns>
		/// 	<c>true</c> if the custom attribute collection has an enabled process properties attribute; otherwise, <c>false</c>.
		/// </returns>
		private bool HasEnabledProcessPropertiesAttribute(CustomAttributeCollection attributes)
		{
			if (attributes == null || attributes.Count == 0)
				return false;

			foreach (CustomAttribute attribute in attributes)
			{
				if (attribute.Constructor.DeclaringType.FullName.Equals(_processPropertiesAttribute))
				{
					if (attribute.ConstructorParameters.Count == 1 && attribute.ConstructorParameters[0] != null)
						return (Convert.ToBoolean(attribute.ConstructorParameters[0], CultureInfo.InvariantCulture));

					if (attribute.Properties["Enabled"] != null)
						return Convert.ToBoolean(attribute.Properties["Enabled"], CultureInfo.InvariantCulture);

					return true;
				}
			}

			return false;
		}

		#endregion
	}
}
