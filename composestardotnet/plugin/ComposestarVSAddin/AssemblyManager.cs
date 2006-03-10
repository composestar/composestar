using EnvDTE;
using Ini;
using System;
using System.Collections;
using System.Collections.Specialized;
using System.Globalization;
using BuildConfiguration;

namespace ComposestarVSAddin
{
	/// <summary>
	/// Summary description for AssemblyManager.
	/// </summary>
	public class AssemblyManager : AbstractManager
	{
		private bool mSuccess = false;

		public AssemblyManager() : base ()
		{

		}

		public bool CompletedSuccessfully() 
		{
			return mSuccess;
		}

		public override void run(_DTE applicationObject, vsBuildScope scope, vsBuildAction action)
		{
			this.mApplicationObject = applicationObject;
			string tempPath = BuildConfigurationManager.Instance.Settings.Paths["Temp"];  

			foreach (BuildConfiguration.Project p in BuildConfigurationManager.Instance.Projects)
			{

				string outputPath = p.OutputPath; 
			
				System.IO.Directory.CreateDirectory(outputPath);

				// Copy the required runtime assemblies
				string binaryPath = System.IO.Path.Combine(BuildConfigurationManager.Instance.Settings.Paths["Composestar"], "binaries\\");
				StringCollection dlls = new StringCollection();

				foreach (String str in BuildConfigurationManager.Instance.DotNetPlatform.RequiredFiles)
				{
					dlls.Add(str);
				}
	
				foreach (string dll in dlls)
				{
					if (System.IO.File.Exists(binaryPath + dll)) 
					{
						System.IO.File.Copy(binaryPath + dll, outputPath + dll, true);

						if (BuildConfigurationManager.Instance.Settings.BuildDebugLevel == DebugModes.Information ) 
						{
							Debug.Instance.Log(DebugModes.Information, "AssemblyManager",String.Format(CultureInfo.CurrentCulture,"  Copied assembly '{0}'.", dll));
						}
					}
				}

				// TODO custom filters should also be included in the list
//				string[] customfilters = this.readIniSection( "CustomFilters" );
//				foreach( string filter in customfilters ) 
//				{
//					if( System.IO.File.Exists( filter ) )
//					{
//						try 
//						{
//							System.IO.File.Copy( filter, System.IO.Path.Combine(outputPath, System.IO.Path.GetFileName( filter )), true );
//						}
//						catch( System.Exception ) 
//						{
//							Debug.Instance.Log( DebugModes.Error, "AssemblyManager", String.Format(CultureInfo.CurrentCulture, "Could not copy Custom Filter dll '{0}'.", filter) );
//						}
//					}
//				}

				// Copy the generated solution assemblies
				try
				{
					// Copy referenced assemblies with copy local attribute set to true
					foreach (EnvDTE.Project project in mApplicationObject.Solution.Projects )
					{
						StringCollection dependencies = DependencyHarvester.Collect(project, true);
						foreach (string dependency in dependencies)
						{
							// Format of dependency: "D:/examples/Jukebox/jukeboxframe.dll"
							Debug.Instance.Log(DebugModes.Debug,"AddIn","Copying dependecy: "+dependency);
							string unquoted_assembly = dependency.Replace("\"", "");
							int index = unquoted_assembly.LastIndexOf("\\");
					
							if (index < 0) 
							{
								index = unquoted_assembly.LastIndexOf("/");
							}
					
							if (index >= 0) 
							{
								string assemblyName = unquoted_assembly.Substring(index+1);
								if (System.IO.File.Exists(unquoted_assembly)) 
								{
									System.IO.File.Copy(unquoted_assembly, outputPath + assemblyName, true);
								}
							}
						}
					}

					// TODO retrieve from the seperate file
//					int builtAssembliesCount = int.Parse(this.readIniValue("ILICIT", "BuiltAssemblies"));
//					StringCollection assemblies = new StringCollection();	
//					for (int i=0; i < builtAssembliesCount; i++) 
//					{
//						assemblies.Add(this.readIniValue("ILICIT", "BuiltAssembly" + i));
//					}
				
//					foreach (string assembly in assemblies)
//					{
//						string unquoted_assembly = assembly.Replace("\"", "");
//
//						int index = unquoted_assembly.LastIndexOf("\\");
//					
//						if (index < 0) 
//						{
//							index = unquoted_assembly.LastIndexOf("/");
//						}
//					
//						if (index >= 0) 
//						{
//							string assemblyName = unquoted_assembly.Substring(index+1);
//							if (System.IO.File.Exists(unquoted_assembly)) 
//							{
//								System.IO.File.Copy(unquoted_assembly, outputPath + assemblyName, true);
//
//												
//								if (assemblyName.EndsWith(".exe")) 
//								{
//									this.writeIniValue("Common", "Executable" , assemblyName);
//								}
//
//								if (BuildConfigurationManager.Instance.Settings.BuildDebugLevel == DebugModes.Information) 
//								{
//									Debug.Instance.Log(DebugModes.Information, "AssemblyManager",String.Format(CultureInfo.CurrentCulture,"  Copied assembly '{0}'.", assemblyName));
//								}
//							}
//							else 
//							{
//								if (BuildConfigurationManager.Instance.Settings.BuildDebugLevel == DebugModes.Information) 
//								{
//									Debug.Instance.Log(DebugModes.Error , "AssemblyManager",String.Format(CultureInfo.CurrentCulture,"  Unable to copy assembly '{0}', file doesn't exist!", assemblyName));
//								}
//							}
//						}
//					}
				}
				catch(Exception)
				{
				}

			}

			// Copy the repository file
			string repositoryFileName = System.IO.Path.Combine(tempPath,"repository.xml");
			//string repositoryFileNameOutput = System.IO.Path.Combine(outputPath, "repository.xml");

//			if( System.IO.File.Exists(repositoryFileName))
//			{
//				System.IO.File.Copy(repositoryFileName, repositoryFileNameOutput, true);
//			}
		}
	}
}
