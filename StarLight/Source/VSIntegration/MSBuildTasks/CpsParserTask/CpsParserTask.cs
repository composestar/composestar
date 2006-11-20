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
		private string _repositoryFileName;
		private ITaskItem[] _concernFiles;
		private ITaskItem[] _referencedTypes;
		private bool _hasOutputFilters;
		private bool _concernsDirty;

		#region Constructor

		/// <summary>
		/// Initializes a new instance of the <see cref="T:CpsParserTask"/> class.
		/// </summary>
		public CpsParserTask() : base(Properties.Resources.ResourceManager)
		{
		}
		#endregion

		#region Properties

		/// <summary>
		/// Gets or sets the repository filename.
		/// </summary>
		/// <value>The repository filename.</value>
		[Required()]
		public string RepositoryFileName
		{
			get { return _repositoryFileName; }
			set { _repositoryFileName = value; }
		}

		/// <summary>
		/// Gets or sets the concern files.
		/// </summary>
		/// <value>The concern files.</value>
		[Required()]
		public ITaskItem[] ConcernFiles
		{
			get { return _concernFiles; }
			set { _concernFiles = value; }
		}

		/// <summary>
		/// Gets or sets the referenced types.
		/// </summary>
		/// <value>The referenced types.</value>
		[Output()]
		public ITaskItem[] ReferencedTypes
		{
			get { return _referencedTypes; }
			set { _referencedTypes = value; }
		}

		[Output()]
		public bool HasOutputFilters
		{
			get { return _hasOutputFilters; }
			set { _hasOutputFilters = value; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether oen or more concern files are changed.
		/// </summary>
		/// <value><c>true</c> if concerns dirty; otherwise, <c>false</c>.</value>
		[Output]
		public bool ConcernsDirty
		{
			get { return _concernsDirty; }
			set { _concernsDirty = value; }
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
				Log.LogMessageFromResources(MessageImportance.Low, "OpenDatabase", RepositoryFileName);
				IEntitiesAccessor entitiesAccessor = EntitiesAccessor.Instance; 

				ConfigurationContainer configContainer = entitiesAccessor.LoadConfiguration(RepositoryFileName);               

				// Create a list with all current concerns
				List<ConcernElement> concernsInConfig = configContainer.Concerns;   
				
				// Create a list with the new concerns
				List<ConcernElement> concernsToAdd = new List<ConcernElement>(); 

				// Parse all concern files and add to the database
				foreach (ITaskItem item in ConcernFiles)
				{
					String concernFile = item.ToString();
					string path = Path.GetDirectoryName(concernFile);
					string filename = Path.GetFileName(concernFile);
					string fullPath = Path.Combine(path, filename);
				
					// Find the concernElement
					ConcernElement ce = concernsInConfig.Find(delegate(ConcernElement e)
					{
						return e.FullPath.Equals(fullPath);
					});

					bool newConcern = false;
					if (ce == null)
					{
						// create a new ConcernElement
						ce = new ConcernElement();
						ce.PathName = path;
						ce.FileName = filename;
						ce.Timestamp = File.GetLastWriteTime(concernFile).Ticks;
						ConcernsDirty = true;
						newConcern = true;
					}

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

						// Indicate if there is embedded code
						ce.HasEmbeddedCode = parser.HasEmbeddedCode;

						// Add the referenced types
                        ce.ReferencedTypes.AddRange(parser.ReferencedTypes);  						

						// Indicate that the concerns are most likely dirty
						ConcernsDirty = true;
					}
					else
					{
						Log.LogMessageFromResources("AddingConcernFile", concernFile);
					}

					// If this concern has output filters, then enable (do not override a previously set true value)
					HasOutputFilters |= ce.HasOutputFilters;

                    refTypes.AddRange(ce.ReferencedTypes);

					concernsToAdd.Add(ce);
				}

				sw.Stop();

				Log.LogMessageFromResources("FoundReferenceType", refTypes.Count, ConcernFiles.Length, sw.Elapsed.TotalSeconds);

				// Pass all the referenced types back to msbuild
				if (refTypes != null && refTypes.Count > 0)
				{
					int index = 0;
					ReferencedTypes = new ITaskItem[refTypes.Count];
					foreach (String type in refTypes)
					{
						ReferencedTypes[index] = new TaskItem(type);
						index++;
					}                    
				}

				// Save the configContainer
				configContainer.Concerns = concernsToAdd;  
				entitiesAccessor.SaveConfiguration(RepositoryFileName, configContainer); 
			}
			catch (CpsParserException ex)
			{
				Log.LogErrorFromException(ex, false);
			}
			catch (System.IO.FileNotFoundException ex)
			{
				Log.LogErrorFromException(ex, false);
			}
				
			return !Log.HasLoggedErrors;
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
