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
using System.IO;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel.Design;
using System.Diagnostics;
using System.Diagnostics.CodeAnalysis;
using System.Globalization;

using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;
using Microsoft.Practices.ObjectBuilder;

using Composestar.Repository;
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Analyzer;
using Composestar.StarLight.CoreServices.Exceptions;
using Composestar.StarLight.CoreServices.Logger;
using Composestar.StarLight.Entities.Configuration;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.ILAnalyzer;
#endregion

namespace Composestar.StarLight.MSBuild.Tasks
{
	/// <summary>
	/// MSBuild tasks to start the analyzer.
	/// </summary>
	public class ILAnalyzerTask : Task
	{
		#region Constants
		
		// The analyzer removes the Composestar.StarLight dlls from the list
		private const string ComposeStarDlls = "Composestar.StarLight";

		/// <summary>
		/// Except the dlls listed below
		/// </summary>
		private static readonly string[] ComposeStarFilterDlls =
			new string[] { 
				"Composestar.StarLight.Filters", 
				"Composestar.StarLight.CustomFilters" 
			};
		
		#endregion
		#region Instance variables
		
		private ConfigurationContainer _configContainer;
		private List<FilterActionElement> _filterActions;
		private List<FilterTypeElement> _filterTypes;
		private List<AssemblyConfig> _assembliesInConfig;
		private List<AssemblyConfig> _assembliesToStore;

		#endregion
		#region Task properties

		// inputs:
		private string _repositoryFileName;
		private ITaskItem[] _weavableAssemblies;
		private ITaskItem[] _referencedAssemblies;
		private ITaskItem[] _referencedTypes;
		private bool _doMethodCallAnalysis = true;
		private string _binFolder;
		private string _intermediateOutputPath;

		// outputs:
		private bool _assembliesDirty;

		/// <summary>
		/// Sets the repository filename.
		/// </summary>
		/// <value>The repository filename.</value>
		[Required]
		public string RepositoryFileName
		{
			set { _repositoryFileName = value; }
		}

		/// <summary>
		/// Sets the assembly files to analyze.
		/// </summary>
		/// <value>The assembly files.</value>
		[Required]
		public ITaskItem[] WeavableAssemblies
		{
			set { _weavableAssemblies = value; }
		}

		/// <summary>
		/// Sets a list of assemblies that are referenced from the project.
		/// </summary>
		[Required]
		public ITaskItem[] ReferencedAssemblies
		{
			set { _referencedAssemblies = value; }
		}

		/// <summary>
		/// Sets a list of types that are referenced from the project.
		/// </summary>
		[Required]
		public ITaskItem[] ReferencedTypes
		{
			set { _referencedTypes = value; }
		}

		/// <summary>
		/// Sets whether to do method call analysis.
		/// </summary>
		public bool DoMethodCallAnalysis
		{
			set { _doMethodCallAnalysis = value; }
		}

		/// <summary>
		/// Sets the bin folder.
		/// </summary>
		/// <value>The bin folder.</value>
		public string BinFolder
		{
			set { _binFolder = value; }
		}

		/// <summary>
		/// Sets the intermediate output path.
		/// </summary>
		/// <value>The intermediate output path.</value>
		[Required]
		public string IntermediateOutputPath
		{
			set { _intermediateOutputPath = value; }
		}

		/// <summary>
		/// Gets a value indicating whether one or more assemblies are dirty.
		/// </summary>
		/// <value><c>true</c> if dirty assemblies were found; otherwise, <c>false</c>.</value>
		[Output]
		public bool AssembliesDirty
		{
			get { return _assembliesDirty; }
		}

		#endregion

		#region ctor

		/// <summary>
		/// Initializes a new instance of the <see cref="T:AnalyzerTask"/> class.
		/// </summary>
		public ILAnalyzerTask()
			: base(Properties.Resources.ResourceManager)
		{
		}

		#endregion

		/// <summary>
		/// Executes the ILAnalyser task.
		/// </summary>
		/// <returns>
		///	<c>true</c> if the task successfully executed; otherwise, <c>false</c>.
		/// </returns>
		public override bool Execute()
		{
			Log.LogMessageFromResources("AnalyzerStartText");

			//
			// Setup of configuration and lists
			//
			IEntitiesAccessor entitiesAccessor = EntitiesAccessor.Instance;
			CecilAnalyzerConfiguration configuration = new CecilAnalyzerConfiguration(_repositoryFileName);
			configuration.BinFolder = _binFolder;
			configuration.DoMethodCallAnalysis = _doMethodCallAnalysis;

			// Initialize instance variables
			_configContainer = entitiesAccessor.LoadConfiguration(_repositoryFileName);
			_filterActions = _configContainer.FilterActions;
			_filterTypes = _configContainer.FilterTypes;
			_assembliesInConfig = _configContainer.Assemblies;
			_assembliesToStore = new List<AssemblyConfig>();

			// Create the analyzer using the object builder
			using (IILAnalyzer analyzer = DIHelper.CreateObject<CecilILAnalyzer>(CreateContainer(entitiesAccessor, configuration)))
			{
				//
				// Find the assemblies to analyze
				//
				List<string> assemblyFiles = FindAssembliesToAnalyze();
				Dictionary<string, string> refAssemblies = FindReferencedAssemblies();

				// Add all the unresolved types (used in the concern files) to the analyser
				AddUnresolvedTypes(analyzer);

				//
				// Analyze the assemblies in the output folder
				//
				List<AssemblyElement> assemblies = new List<AssemblyElement>();
				AnalyzeAllAssembliesInOutputFolder(analyzer, assemblies, assemblyFiles);

				//
				// Analyze the assemblies referenced to this project or subprojects
				//
				AnalyzeReferencedAssemblies(analyzer, refAssemblies, assemblies, assemblyFiles);

				//
				// Store the found assemblies in the configuration container
				//
				StoreAssemblies(analyzer, assemblies);
			}

			return !Log.HasLoggedErrors;
		}

		#region Supporting functions

		/// <summary>
		/// Determines whether the specified filename is a filter file.
		/// </summary>
		/// <param name="filename">The filename.</param>
		/// <returns>
		/// 	<c>true</c> if the specified filename is a filter file; otherwise, <c>false</c>.
		/// </returns>
		private static bool IsFilterFile(string filename)
		{
			foreach (string filterFile in ComposeStarFilterDlls)
			{
				if (filename.StartsWith(filterFile))
				{
					return true;
				}
			}

			return false;
		}

		/// <summary>
		/// Finds the assemblies to analyze.
		/// </summary>
		/// <param name="assemblyFileList">The assembly file list.</param>
		/// <param name="refAssemblies">The ref assemblies.</param>
		private List<string> FindAssembliesToAnalyze()
		{
			// Create a list of all the referenced assemblies (complete list is supplied by the msbuild file)
			List<string> assemblyFiles = new List<string>();

			foreach (ITaskItem item in _weavableAssemblies)
			{
				// Skip composestar files
				string filename = Path.GetFileNameWithoutExtension(item.ToString());

				if (filename.StartsWith(ComposeStarDlls) && !IsFilterFile(filename))
					continue;

				// We are only interested in assembly files.
				string extension = Path.GetExtension(item.ToString()).ToLower(CultureInfo.InvariantCulture);
				if (extension.Equals(".dll") || extension.Equals(".exe"))
				{
					assemblyFiles.Add(item.ToString());
				}
			}

			return assemblyFiles;
		}

		private Dictionary<string, string> FindReferencedAssemblies()
		{
			// Create a list of all the referenced assemblies, which are not copied local for complete analysis
			// We cannot weave on these files, so we only use them for lookup of base types etc.
			Dictionary<string, string> refAssemblies = new Dictionary<string, string>();

			foreach (ITaskItem item in _referencedAssemblies)
			{
				if (item.GetMetadata("CopyLocal") == "false")
				{
					refAssemblies.Add(item.GetMetadata("FusionName"), item.GetMetadata("Identity"));
				}
			}

			return refAssemblies;
		}

		/// <summary>
		/// Stores the filters.
		/// </summary>
		/// <param name="results">The results.</param>
		private void StoreFilters(IAnalyzerResults results)
		{
			if (results.FilterTypes.Count == 0 && results.FilterActions.Count == 0)
				return;

			// Add FilterTypes
			foreach (FilterTypeElement ft in results.FilterTypes)
			{
				bool canAdd = true;
				foreach (FilterTypeElement ftConfig in _filterTypes)
				{
					if (ft.Name.Equals(ftConfig.Name))
					{
						canAdd = false;
						continue;
					}
				}
				if (canAdd)
					_filterTypes.Add(ft);
			}

			// Add FilterActions
			foreach (FilterActionElement fa in results.FilterActions)
			{
				bool canAdd = true;
				foreach (FilterActionElement faConfig in _filterActions)
				{
					if (fa.Name.Equals(faConfig.Name))
					{
						canAdd = false;
						continue;
					}
				}
				if (canAdd)
					_filterActions.Add(fa);
			}

			// TODO: we miss a cleanup of the filtertypes and actions no longer in the assemblies. User has to use a rebuild.

			Log.LogMessageFromResources("FiltersAnalyzed", results.FilterTypes.Count, results.FilterActions.Count);
		}

		/// <summary>
		/// Analyzes all assemblies in output folder.
		/// </summary>
		/// <param name="analyzer">The analyzer.</param>
		/// <param name="assemblies">The assemblies.</param>
		/// <param name="assemblyFileList">The assembly file list.</param>
		/// <returns><see langword="true"/> when one or more assemblies are changed.</returns>
		private void AnalyzeAllAssembliesInOutputFolder(IILAnalyzer analyzer, List<AssemblyElement> assemblies, List<string> assemblyFileList)
		{
			// Analyze all assemblies in the output folder
			foreach (string item in assemblyFileList)
			{
				try
				{
					// See if we already have this assembly in the list
					AssemblyConfig asmConfig = _assembliesInConfig.Find(delegate(AssemblyConfig ac)
					{
						return ac.FileName.Equals(item);
					});

					if (asmConfig != null && File.Exists(asmConfig.SerializedFileName))
					{
						// Already in the config. Check the last modification date.
						if (asmConfig.Timestamp == File.GetLastWriteTime(item).Ticks)
						{
							// Assembly has not been modified, skipping analysis
							Log.LogMessageFromResources("AssemblyNotModified", asmConfig.Name);

							// We still have to store this assembly, but we do not analyze it
							_assembliesToStore.Add(asmConfig);
							continue;
						}
					}

					// Either we could not find the assembly in the config or it was changed.

					AssemblyElement assembly = null;
					IAnalyzerResults results;
					Stopwatch sw = new Stopwatch();

					Log.LogMessageFromResources("AnalyzingFile", item);

					sw.Start();

					results = analyzer.ExtractAllTypes(item);

					ShowLogItems(results.Log.LogItems);

					// Get the assembly from the results.
					assembly = results.Assembly;

					// Store the filters
					StoreFilters(results);

					sw.Stop();
					sw.Reset();

					if (assembly != null && !IsFilterFile(Path.GetFileName(item)))
					{
						// Create a new AssemblyConfig object
						asmConfig = new AssemblyConfig();

						asmConfig.FileName = item;
						asmConfig.Name = assembly.Name;
						asmConfig.Timestamp = File.GetLastWriteTime(item).Ticks;
						asmConfig.Assembly = assembly;

						// Generate a unique filename
						asmConfig.GenerateSerializedFileName(_intermediateOutputPath);

						_assembliesDirty = true;
						_assembliesToStore.Add(asmConfig);
						assemblies.Add(assembly);

						Log.LogMessageFromResources("AssemblyAnalyzed", assembly.Types.Count, analyzer.UnresolvedAssemblies.Count, sw.Elapsed.TotalSeconds);
					}
					else
					{
						Log.LogMessageFromResources("AssemblyAnalyzedSkipped");
					}
				}
				catch (ILAnalyzerException ex)
				{
					Log.LogErrorFromException(ex, true);
				}
				catch (ArgumentException ex)
				{
					Log.LogErrorFromException(ex, true);
				}
				catch (FileNotFoundException ex)
				{
					Log.LogErrorFromException(ex, true);
				}
				catch (BadImageFormatException ex)
				{
					Log.LogErrorFromException(ex, false);
				}

			}
		}

		/// <summary>
		/// Analyzes the referenced assemblies.
		/// </summary>
		/// <param name="analyzer">The analyzer.</param>
		/// <param name="assemblyChanged">if set to <c>true</c> one or more assemblies are changed.</param>
		/// <param name="refAssemblies">The ref assemblies.</param>
		/// <param name="assemblies">The assemblies.</param>
		/// <param name="assemblyFileList">The assembly file list.</param>
		private void AnalyzeReferencedAssemblies(IILAnalyzer analyzer, Dictionary<string, string> refAssemblies, List<AssemblyElement> assemblies, List<string> assemblyFiles)
		{
			// Only if we have unresolved types
			if (analyzer.UnresolvedTypes.Count == 0)
				return;

			Log.LogMessageFromResources("NumberOfReferencesToResolve", analyzer.UnresolvedTypes.Count);

			assemblyFiles.Clear();

			// The previous step could introduce new assemblies. So add those to the list
			assemblyFiles.AddRange(analyzer.ResolveAssemblyLocations());

			// Create new config
			CecilAnalyzerConfiguration configuration = new CecilAnalyzerConfiguration(_repositoryFileName);

			// Disable some options
			configuration.DoFieldAnalysis = false;
			configuration.DoMethodCallAnalysis = false;
			configuration.ExtractUnresolvedOnly = true;
			configuration.BinFolder = _binFolder;

			// Store before reinit
			List<string> tempUnresolvedTypes = analyzer.UnresolvedTypes;

			// Create the analyzer using the object builder
			IEntitiesAccessor entitiesAccessor = EntitiesAccessor.Instance;
			analyzer = DIHelper.CreateObject<CecilILAnalyzer>(CreateContainer(entitiesAccessor, configuration));

			// Set the unresolved types (because we reinit the analyzer)
			analyzer.UnresolvedTypes.AddRange(tempUnresolvedTypes);

			// Add the assemblies to analyze.
			foreach (string al in refAssemblies.Values)
			{
				if (!assemblyFiles.Contains(al))
					assemblyFiles.Add(al);
			}

			// Try to resolve all the references.
			do
			{
				// Loop through all the referenced assemblies.
				foreach (string item in assemblyFiles)
				{
					try
					{
						// See if we already have this assembly in the list
						AssemblyConfig asmConfig = _assembliesInConfig.Find(delegate(AssemblyConfig ac)
						{
							return ac.FileName.Equals(item);
						});

						// If a source assembly has changed, then new unresolved types can be introduced.
						// So we must rescan the library based on the value of assemblyChanged.
						// TODO: can this be optimized?
						if (!_assembliesDirty && asmConfig != null && File.Exists(asmConfig.SerializedFileName))
						{
							// Already in the config. Check the last modification date.
							if (asmConfig.Timestamp == File.GetLastWriteTime(item).Ticks)
							{
								// Assembly has not been modified, skipping analysis
								Log.LogMessageFromResources("AssemblyNotModified", asmConfig.Name);

								_assembliesToStore.Add(asmConfig);
								continue;
							}
						}

						// Either we could not find the assembly in the config or it was changed.

						Log.LogMessageFromResources("AnalyzingFile", item);
						Stopwatch sw = Stopwatch.StartNew();
						
						IAnalyzerResults results = analyzer.ExtractAllTypes(item);
						ShowLogItems(results.Log.LogItems);

						// Store the filters
						StoreFilters(results);

						// Create a new AssemblyConfig object
						AssemblyElement assembly = results.Assembly;
						if (assembly != null)
						{
							asmConfig = new AssemblyConfig();

							asmConfig.FileName = item;
							asmConfig.Name = assembly.Name;
							asmConfig.Timestamp = File.GetLastWriteTime(item).Ticks;
							asmConfig.Assembly = assembly;
							asmConfig.IsReference = true;

							// Generate a unique filename
							asmConfig.GenerateSerializedFileName(_intermediateOutputPath);

							_assembliesToStore.Add(asmConfig);
							assemblies.Add(assembly);
						}

						sw.Stop();
						Log.LogMessageFromResources("AssemblyAnalyzed", assembly.Types.Count, analyzer.UnresolvedAssemblies.Count, sw.Elapsed.TotalSeconds);

						StoreFilters(results);
					}
					catch (ILAnalyzerException ex)
					{
						Log.LogErrorFromException(ex, true);
					}
					catch (ArgumentException ex)
					{
						Log.LogErrorFromException(ex, true);
					}
					catch (FileNotFoundException ex)
					{
						Log.LogErrorFromException(ex, true);
					}
					catch (BadImageFormatException ex)
					{
						Log.LogErrorFromException(ex, false);
					}
				}

				// Clear the already analyzed assemblies
				assemblyFiles.Clear();

				// Get the unresolved
				assemblyFiles.AddRange(analyzer.ResolveAssemblyLocations());
			}
			while (analyzer.UnresolvedTypes.Count > 0 && assemblyFiles.Count > 0);
		}

		/// <summary>
		/// Stores the assemblies to the configuration container and, when needed, to assemblyElements on disk.
		/// </summary>
		/// <param name="analyzer">The analyzer.</param>
		/// <param name="assemblies">The assemblies.</param>
		private void StoreAssemblies(IILAnalyzer analyzer, List<AssemblyElement> assemblies)
		{
			if (assemblies.Count == 0 || Log.HasLoggedErrors)
				return;

			Log.LogMessageFromResources("StoreInDatabase", assemblies.Count);
			Stopwatch sw = Stopwatch.StartNew();

			// Check if we have to save the assembly data
			foreach (AssemblyConfig assembly in _assembliesToStore)
			{
				// save each assembly if needed (there must be an assemblyElement)
				if (assembly.Assembly != null)
					EntitiesAccessor.Instance.SaveAssemblyElement(assembly.SerializedFileName, assembly.Assembly);
			}

			// Set the assemblies to store
			_configContainer.Assemblies = _assembliesToStore;

			// Add the filtertypes and actions
			_configContainer.FilterTypes = _filterTypes;
			_configContainer.FilterActions = _filterActions;

			// Save the config
			EntitiesAccessor.Instance.SaveConfiguration(_repositoryFileName, _configContainer);

			sw.Stop();
			Log.LogMessageFromResources("StoreInDatabaseCompleted", assemblies.Count, analyzer.ResolvedAssemblies.Count, sw.Elapsed.TotalSeconds);
		}

		/// <summary>
		/// Adds all unresolved types from the concern files to the analyzer.
		/// </summary>
		/// <param name="analyzer">The analyzer.</param>
		private void AddUnresolvedTypes(IILAnalyzer analyzer)
		{
			if (_referencedTypes.Length == 0)
				return;

			Log.LogMessageFromResources(MessageImportance.Low, "NumberOfReferencesToResolve", _referencedTypes.Length);
			foreach (ITaskItem item in _referencedTypes)
			{
				analyzer.UnresolvedTypes.Add(item.ToString());
			}
		}

		/// <summary>
		/// Show log items
		/// </summary>
		/// <param name="items">Items</param>
		private void ShowLogItems(ReadOnlyCollection<LogItem> items)
		{
			if (items == null || items.Count == 0)
				return;

			// Output the logitems
			foreach (LogItem item in items)
			{
				Log.LogMessageFromText(item.ToString(), MessageImportance.Normal);
			}
		}
		#endregion

		/// <summary>
		/// Creates the services container.
		/// </summary>
		/// <param name="languageModel">The language model.</param>
		/// <param name="configuration">The configuration.</param>
		/// <returns></returns>
		internal static IServiceProvider CreateContainer(IEntitiesAccessor languageModel, CecilAnalyzerConfiguration configuration)
		{
			ServiceContainer serviceContainer = new ServiceContainer();
			serviceContainer.AddService(typeof(IEntitiesAccessor), languageModel);
			serviceContainer.AddService(typeof(CecilAnalyzerConfiguration), configuration);
			serviceContainer.AddService(typeof(IBuilderConfigurator<BuilderStage>), new ILAnalyzerBuilderConfigurator());

			return serviceContainer;
		}
	}
}
