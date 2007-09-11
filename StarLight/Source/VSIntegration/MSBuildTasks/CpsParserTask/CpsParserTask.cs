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
using System.ComponentModel.Design;
using System.Diagnostics;
using System.Diagnostics.CodeAnalysis;  
using System.IO;

using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;

using Composestar.Repository;
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions;
using Composestar.StarLight.CpsParser;
using Composestar.StarLight.Entities.Concerns;
using Composestar.StarLight.Entities.Configuration;
#endregion

namespace Composestar.StarLight.MSBuild.Tasks
{
	/// <summary>
	/// Task to parse the Cps files.
	/// </summary>
	public class CpsParserTask : Task
	{
		private const string EmbeddedFolderName = "Embedded";

		#region Task properties

		/// inputs:
		private string _baseDir;
		private string _repositoryFileName;
		private ITaskItem[] _concernFiles;
		private string _codeLanguage;

		/// outputs:
		private ITaskItem[] _referencedTypes;
		private IList<ITaskItem> _extraSources;
		private bool _hasOutputFilters;
		private bool _concernsDirty;

		/// <summary>
		/// Sets the project base directory.
		/// </summary>
		[Required]
		[SuppressMessage("Microsoft.Design", "CA1044:PropertiesShouldNotBeWriteOnly")]
		public string BaseDir
		{
			set { _baseDir = value; }
		}

		/// <summary>
		/// Sets the repository filename.
		/// </summary>
		[Required]
		[SuppressMessage("Microsoft.Design", "CA1044:PropertiesShouldNotBeWriteOnly")]
		public string RepositoryFileName
		{
			set { _repositoryFileName = value; }
		}

		/// <summary>
		/// Sets the concern files to be parsed.
		/// </summary>
		[Required]
		[SuppressMessage("Microsoft.Design", "CA1044:PropertiesShouldNotBeWriteOnly")]
		public ITaskItem[] ConcernFiles
		{
			set { _concernFiles = value; }
		}

		/// <summary>
		/// Sets the CodeLanguage of the project that contain the concerns to be parsed.
		/// Any embedded code must be the same language. A <ref langword="null"/> value, 
		/// which is the default, indicates that embedded code is disallowed. 
		/// </summary>
		[SuppressMessage("Microsoft.Design", "CA1044:PropertiesShouldNotBeWriteOnly")]
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
						ICpsParser parser = new CpsFileParser(concernFile);

						// Parse the concern file
						parser.Parse();

						// Indicate if there are any outputfilters
						ce.HasOutputFilters = parser.HasOutputFilters;

						// Store the embedded code (if any)
						ce.EmbeddedFileName = StoreEmbeddedCode(concernFile, parser.EmbeddedCode);

						// Add the referenced types
						ce.ReferencedTypes.AddRange(parser.ReferencedTypes);

						// Update timestamp
						ce.Timestamp = File.GetLastWriteTime(concernFile).Ticks;

						// Indicate that the concerns are most likely dirty
						_concernsDirty = true;
					}
					else
					{
						Log.LogMessageFromResources("AddingConcernFile", concernFile);
					}

                    if (!string.IsNullOrEmpty(ce.EmbeddedFileName))
                    {
                        _extraSources.Add(new TaskItem(ce.EmbeddedFileName));
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
		/// <param name="cc">The configcontainer with all the concerns.</param>
		/// <param name="concernFile">The full path of the concern to find.</param>
		/// <param name="newConcern">if set to <see langword="true"/> then the concern is new since the last build.</param>
		/// <returns>returns <see langword="null"/> when the concern is not in the list or a <see cref="T:ConcernElement">ConcernElement</see> for the concern file.</returns>
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
		private string StoreEmbeddedCode(string concern, EmbeddedCode ec)
		{
			if (ec == null)
				return null;

			if (_codeLanguage == null)
			{
				Log.LogWarningFromResources("EmbeddedCodeNotSupported", concern);
				return null;
			}

			if (!_codeLanguage.Equals(ec.Language))
			{
				Log.LogWarningFromResources("EmbeddedCodeLanguageWrong", concern, ec.Language, _codeLanguage);
                return null;
			}

			string embeddedDir = Path.Combine(_baseDir, EmbeddedFolderName);

			if (!Directory.Exists(embeddedDir))
				Directory.CreateDirectory(embeddedDir);

			string filename = Path.Combine(embeddedDir, ec.FileName);
			using (StreamWriter sw = File.CreateText(filename))
			{
				Log.LogMessageFromResources("WritingEmbeddedCode", filename);

				sw.Write(ec.Code);
				//_extraSources.Add(new TaskItem(filename));
                return filename;
			}
            return null;
		}

		/// <summary>
		/// Converts a list of taskitems to an array.
		/// </summary>
		/// <param name="items">The list of items.</param>
		/// <returns>Returns an ITaskItem array.</returns>
		private static ITaskItem[] ToArray(IList<ITaskItem> items)
		{
			ITaskItem[] arr = new ITaskItem[items.Count];
			items.CopyTo(arr, 0);
			return arr;
		}
/*
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
		}*/
	}
}
