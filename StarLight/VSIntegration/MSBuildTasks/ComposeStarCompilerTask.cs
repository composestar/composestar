using System;
using System.IO;
using System.Collections.Generic;
using System.Globalization;
using System.Reflection;
using Microsoft.Build.Utilities;
using Microsoft.Build.Framework;
using ComposeStar.MSBuild.Tasks.Compiler;

namespace ComposeStar.MSBuild.Tasks
{
    public class ComposeStarCompilerTask : Task 
    {
      	private ICompiler compiler = null;

		#region Constructors
		/// <summary>
		/// Constructor. This is the constructor that will be used
		/// when the task run.
		/// </summary>
		public ComposeStarCompilerTask()
		{
		}

		/// <summary>
		/// Constructor. The goal of this constructor is to make
		/// it easy to test the task.
		/// </summary>
        public ComposeStarCompilerTask(ICompiler compilerToUse)
		{
			compiler = compilerToUse;
		}
		#endregion

		#region Public Properties and related Fields
		private string[] sourceFiles;
		/// <summary>
		/// List of Python source files that should be compiled into the assembly
		/// </summary>
		[Required()]
		public string[] SourceFiles
		{
			get { return sourceFiles; }
			set { sourceFiles = value; }
		}

		private string outputAssembly;
		/// <summary>
		/// Output Assembly (including extension)
		/// </summary>
		[Required()]
		public string OutputAssembly
		{
			get { return outputAssembly; }
			set { outputAssembly = value; }
		}

		private ITaskItem[] referencedAssemblies = new ITaskItem[0];
		/// <summary>
		/// List of dependent assemblies
		/// </summary>
		public ITaskItem[] ReferencedAssemblies
		{
			get { return referencedAssemblies; }
			set
			{
				if (value != null)
				{
					referencedAssemblies = value;
				}
				else
				{
					referencedAssemblies = new ITaskItem[0];
				}

			}
		}

		private ITaskItem[] resourceFiles = new ITaskItem[0];
		/// <summary>
		/// List of resource files
		/// </summary>
		public ITaskItem[] ResourceFiles
		{
			get { return resourceFiles; }
			set
			{
				if (value != null)
				{
					resourceFiles = value;
				}
				else
				{
					resourceFiles = new ITaskItem[0];
				}

			}
		}

		private string mainFile;
		/// <summary>
		/// For applications, which file is the entry point
		/// </summary>
		[Required()]
		public string MainFile
		{
			get { return mainFile; }
			set { mainFile = value; }
		}

		private string targetKind;
		/// <summary>
		/// Target type (exe, winexe, library)
		/// These will be mapped to System.Reflection.Emit.PEFileKinds
		/// </summary>
		public string TargetKind
		{
			get { return targetKind; }
			set { targetKind = value.ToLower(CultureInfo.InvariantCulture); }
		}
		private bool includeDebugInformation = true;
		/// <summary>
		/// Generate debug information
		/// </summary>
		public bool IncludeDebugInformation
		{
			get { return includeDebugInformation; }
			set { includeDebugInformation = value; }
		}
		private string projectPath = null;
		/// <summary>
		/// This should be set to $(MSBuildProjectDirectory)
		/// </summary>
		public string ProjectPath
		{
			get { return projectPath; }
			set { projectPath = value; }
		}

		#endregion

		/// <summary>
		/// Main entry point for the task
		/// </summary>
		/// <returns></returns>
		public override bool Execute()
		{
			Log.LogMessage(MessageImportance.Normal, "ComposeStar Compilation Task");

			// Create the compiler if it does not already exist
			CompilerErrorSink errorSink = new CompilerErrorSink(this.Log);
			errorSink.ProjectDirectory = ProjectPath;
			if (compiler == null)
			{
				compiler = new Compiler.Compiler(this.SourceFiles, this.OutputAssembly);
			}

			if (!InitializeCompiler())
				return false;

			// Call the compiler and report errors and warnings
			compiler.Compile();

			return errorSink.BuildSucceeded;
		}

		/// <summary>
		/// Initialize compiler options based on task parameters
		/// </summary>
		/// <returns>false if failed</returns>
		private bool InitializeCompiler()
		{
			switch (TargetKind)
			{
				case "exe":
					{
						compiler.TargetKind = System.Reflection.Emit.PEFileKinds.ConsoleApplication;
						break;
					}
				case "winexe":
					{
						compiler.TargetKind = System.Reflection.Emit.PEFileKinds.WindowApplication;
						break;
					}
				case "library":
					{
						compiler.TargetKind = System.Reflection.Emit.PEFileKinds.Dll;
						break;
					}
				default:
					{
						this.Log.LogError(Properties.Resources.InvalidTargetType, TargetKind);
						return false;
					}
			}
			compiler.IncludeDebugInformation = this.IncludeDebugInformation;
			compiler.MainFile = this.MainFile;
			compiler.SourceFiles = new List<string>(this.SourceFiles);

			// References require a bit more work since our compiler expect us to pass the Assemblies (and not just paths)
			compiler.ReferencedAssemblies = new List<string>();
			foreach (ITaskItem assemblyReference in this.ReferencedAssemblies)
			{
				compiler.ReferencedAssemblies.Add(assemblyReference.ItemSpec);
			}

			// Add each resource
            //List<IronPython.Hosting.ResourceFile> resourcesList = new List<IronPython.Hosting.ResourceFile>();
            //foreach (ITaskItem resource in this.ResourceFiles)
            //{
            //    bool publicVisibility = true;
            //    string access = resource.GetMetadata("Access");
            //    if (String.CompareOrdinal("Private", access) == 0)
            //        publicVisibility = false;
            //    string filename = resource.ItemSpec;
            //    string logicalName = resource.GetMetadata("LogicalName");
            //    if (String.IsNullOrEmpty(logicalName))
            //        logicalName = Path.GetFileName(resource.ItemSpec);

            //    IronPython.Hosting.ResourceFile resourceFile = new IronPython.Hosting.ResourceFile(logicalName, filename, publicVisibility);
            //    resourcesList.Add(resourceFile);
            //}
            //compiler.ResourceFiles = resourcesList;

			return true;
		}
    }
}
