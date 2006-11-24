#region Using directives
using System;
using System.IO;
using System.Collections.Generic;
using System.ComponentModel.Design;
using System.Diagnostics;

using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;
using Microsoft.Practices.ObjectBuilder;

using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions;
using Composestar.StarLight.CpsParser;
using Composestar.StarLight.Entities.Concerns;
using Composestar.StarLight.Entities.Configuration;
using Composestar.Repository;

#endregion

namespace Composestar.StarLight.MSBuild.Tasks
{
	/// <summary>
	/// Task to parse the Cps files.
	/// </summary>
	public class CpsParserTask : Task
	{
		private const string EmbeddedFolderName = "Embedded";

		// inputs
		private string _baseDir;
		private string _repositoryFileName;
		private ITaskItem[] _concernFiles;
		private string _codeLanguage;

		// outputs
		private ITaskItem[] _referencedTypes;
		private IList<ITaskItem> _extraSources;
		private bool _hasOutputFilters;
		private bool _concernsDirty;

		#region Constructor

		/// <summary>
		/// Initializes a new instance of the <see cref="T:CpsParserTask"/> class.
		/// </summary>
		public CpsParserTask()
			: base(Properties.Resources.ResourceManager)
		{
			_extraSources = new List<ITaskItem>();
		}
		#endregion

		#region Properties

		/// <summary>
		/// Sets the project base directory.
		/// </summary>
		[Required]
		public string BaseDir
		{
			set { _baseDir = value; }
		}

		/// <summary>
		/// Sets the repository filename.
		/// </summary>
		[Required]
		public string RepositoryFileName
		{
			set { _repositoryFileName = value; }
		}

		/// <summary>
		/// Sets the concern files to be parsed.
		/// </summary>
		[Required]
		public ITaskItem[] ConcernFiles
		{
			set { _concernFiles = value; }
		}

		/// <summary>
		/// Sets the CodeLanguage of the project that contain the concerns to be parsed.
		/// Any embedded code must be the same language. A <ref langword="null"/> value, 
		/// which is the default, indicates that embedded code is disallowed. 
		/// </summary>
		public string CodeLanguage
		{
			set { _codeLanguage = value; }
		}

		/// <summary>
		/// Gets a list of referenced types from the parsed concerns.
		/// </summary>
		[Output]
		public ITaskItem[] ReferencedTypes
		{
			get { return _referencedTypes; }
		}

		/// <summary>
		/// Gets a list of filenames that were extracted from embedded code.
		/// </summary>
		[Output]
		public ITaskItem[] ExtraSources
		{
			get { return ToArray(_extraSources); }
		}

		/// <summary>
		/// Indicates whether the parsed concerns contain output filters.
		/// </summary>
		[Output]
		public bool HasOutputFilters
		{
			get { return _hasOutputFilters; }
		}

		/// <summary>
		/// Indicates whether one or more concern files have been changed since the last build.
		/// </summary>
		[Output]
		public bool ConcernsDirty
		{
			get { return _concernsDirty; }
		}

		#endregion

		/// <summary>
		/// When overridden in a derived class, executes the task.
		/// </summary>
		/// <returns>
		/// true if the task successfully executed; otherwise, false.
		/// </returns>
		public override bool Execute()
		{
			List<string> refTypes = new List<string>();
			Stopwatch sw = Stopwatch.StartNew();
		
			try
			{
				// Open DB
				Log.LogMessageFromResources(MessageImportance.Low, "OpenDatabase", _repositoryFileName);
				IEntitiesAccessor entitiesAccessor = EntitiesAccessor.Instance;
				ConfigurationContainer configContainer = entitiesAccessor.LoadConfiguration(_repositoryFileName);

				// Create a list with the new concerns
				List<ConcernElement> concernsToAdd = new List<ConcernElement>();

				// Parse all concern files and add to the database
				foreach (ITaskItem item in _concernFiles)
				{
					string concernFile = item.ToString();
					bool newConcern;

					ConcernElement ce = FindConcernElement(configContainer, concernFile, out newConcern);

					// Do a time check, if new or change, run the parser and store the data
					if (newConcern || File.GetLastWriteTime(concernFile).Ticks > ce.Timestamp)
					{
						Log.LogMessageFromResources("ParsingConcernFile", concernFile);

						// File is changed, we might not have the correct data
						CpsParserConfiguration config = CpsParserConfiguration.CreateDefaultConfiguration(concernFile);
						ICpsParser parser = DIHelper.CreateObject<CpsFileParser>(CreateContainer(config));

						// Parse the concern file
						parser.Parse();

						// Indicate if there are any outputfilters
						ce.HasOutputFilters = parser.HasOutputFilters;

						// Store the embedded code (if any)
						StoreEmbeddedCode(concernFile, parser.EmbeddedCode);

						// Add the referenced types
						ce.ReferencedTypes.AddRange(parser.ReferencedTypes);

						// Indicate that the concerns are most likely dirty
						_concernsDirty = true;
					}
					else
					{
						Log.LogMessageFromResources("AddingConcernFile", concernFile);
					}

					_hasOutputFilters |= ce.HasOutputFilters;

					refTypes.AddRange(ce.ReferencedTypes);

					concernsToAdd.Add(ce);
				}

				sw.Stop();

				Log.LogMessageFromResources("FoundReferenceType", refTypes.Count, _concernFiles.Length, sw.Elapsed.TotalSeconds);

				// Pass all the referenced types back to msbuild
				if (refTypes != null && refTypes.Count > 0)
				{
					int index = 0;
					_referencedTypes = new ITaskItem[refTypes.Count];
					foreach (String type in refTypes)
					{
						_referencedTypes[index] = new TaskItem(type);
						index++;
					}
				}

				// Save the configContainer
				configContainer.Concerns = concernsToAdd;
				entitiesAccessor.SaveConfiguration(_repositoryFileName, configContainer);
			}
			catch (CpsParserException ex)
			{
				Log.LogErrorFromException(ex, false);
			}
			catch (FileNotFoundException ex)
			{
				Log.LogErrorFromException(ex, false);
			}

			return !Log.HasLoggedErrors;
		}

		/// <summary>
		/// Finds the concern element with the specified path.
		/// </summary>
		/// <param name="cc">The cc.</param>
		/// <param name="concernFile">The full path of the concern to find.</param>
		/// <param name="newConcern">if set to <see langword="true"/> then the concern is new since the last build.</param>
		/// <returns></returns>
		private ConcernElement FindConcernElement(ConfigurationContainer cc, string concernFile,
			out bool newConcern)
		{
			string basePath = Path.GetDirectoryName(concernFile);
			string filename = Path.GetFileName(concernFile);
			string fullPath = Path.Combine(basePath, filename);

			ConcernElement ce = cc.Concerns.Find(delegate(ConcernElement e)
			{
				return e.FullPath.Equals(fullPath);
			});

			if (ce == null)
			{
				// create a new ConcernElement
				ce = new ConcernElement();
				ce.PathName = basePath;
				ce.FileName = filename;
				ce.Timestamp = File.GetLastWriteTime(concernFile).Ticks;
				_concernsDirty = true;
				newConcern = true;
			}
			else
				newConcern = false;

			return ce;
		}

		/// <summary>
		/// Writes the specified embedded code to a file.
		/// </summary>
		/// <param name="ec">The embedded code.</param>
		private void StoreEmbeddedCode(string concern, EmbeddedCode ec)
		{
			if (ec == null)
				return;

			if (_codeLanguage == null)
			{
				Log.LogWarningFromResources("EmbeddedCodeNotSupported", concern);
				return;
			}

			if (!_codeLanguage.Equals(ec.Language))
			{
				Log.LogWarningFromResources("EmbeddedCodeLanguageWrong", concern, ec.Language, _codeLanguage);
				return;
			}

			string embeddedDir = Path.Combine(_baseDir, EmbeddedFolderName);

			if (!Directory.Exists(embeddedDir))
				Directory.CreateDirectory(embeddedDir);

			string filename = Path.Combine(embeddedDir, ec.FileName);
			using (StreamWriter sw = File.CreateText(filename))
			{
				Log.LogMessageFromResources("WritingEmbeddedCode", filename);

				sw.Write(ec.Code);
				_extraSources.Add(new TaskItem(filename));
			}
		}

		/// <summary>
		/// Converts a list of taskitems to an array.
		/// </summary>
		/// <param name="items">The list of items.</param>
		/// <returns>Returns an ITaskItem array.</returns>
		private ITaskItem[] ToArray(IList<ITaskItem> items)
		{
			ITaskItem[] arr = new ITaskItem[_extraSources.Count];
			_extraSources.CopyTo(arr, 0);
			return arr;
		}

		/// <summary>
		/// Creates the services container.
		/// </summary>
		/// <returns></returns>
		internal static IServiceProvider CreateContainer(CpsParserConfiguration configuration)
		{
			ServiceContainer serviceContainer = new ServiceContainer();
			serviceContainer.AddService(typeof(CpsParserConfiguration), configuration);
			serviceContainer.AddService(typeof(IBuilderConfigurator<BuilderStage>), new CpsParserBuilderConfigurator());

			return serviceContainer;
		}
	}
}
