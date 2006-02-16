using EnvDTE;
using Ini;
using System;
using System.Collections;
using System.Collections.Specialized;
using System.Globalization;

namespace ComposestarVSAddin
{
	/// <summary>
	/// Summary description for AssemblyManager.
	/// </summary>
	public class AssemblyManager : AbstractManager
	{
		private bool mSuccess = false;

		public AssemblyManager(IniFile inifile) : base (inifile)
		{

		}

		public bool CompletedSuccessfully() 
		{
			return mSuccess;
		}

		public override void run(_DTE applicationObject, vsBuildScope scope, vsBuildAction action)
		{
			this.mApplicationObject = applicationObject;

			string outputPath = this.readIniValue("Common", "OutputPath");
			string tempPath = this.readIniValue("Common", "TempFolder");

			System.IO.Directory.CreateDirectory(outputPath);

			// Copy the required runtime assemblies
			string binaryPath = this.readIniValue("Global Composestar configuration", "ComposestarPath") + "binaries\\";
			string requiredDlls = this.readIniValue("Global Composestar configuration", "RequiredDlls");
			StringCollection dlls = new StringCollection();
			dlls.AddRange(requiredDlls.Split(','));
	
			foreach (string dll in dlls)
			{
				if (System.IO.File.Exists(binaryPath + dll)) 
				{
					System.IO.File.Copy(binaryPath + dll, outputPath + dll, true);

					if (this.readIniValue("Global Composestar configuration", "BuildDebugLevel").Equals("3")) 
					{
						Debug.Instance.Log(DebugModes.Information, "AssemblyManager",String.Format(CultureInfo.CurrentCulture,"  Copied assembly '{0}'.", dll));
					}
				}
			}

			string[] customfilters = this.readIniSection( "CustomFilters" );
			foreach( string filter in customfilters ) 
			{
				if( System.IO.File.Exists( filter ) )
				{
					try 
					{
						System.IO.File.Copy( filter, System.IO.Path.Combine(outputPath, System.IO.Path.GetFileName( filter )), true );
					}
					catch( System.Exception ) 
					{
						Debug.Instance.Log( DebugModes.Error, "AssemblyManager", String.Format(CultureInfo.CurrentCulture, "Could not copy Custom Filter dll '{0}'.", filter) );
					}
				}
			}

			// Copy the generated solution assemblies
			try
			{
				// Copy referenced assemblies with copy local attribute set to true
				StringCollection dependencies = DependencyHarvester.Collect(mApplicationObject.Solution.Projects, true);
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
				

				int builtAssembliesCount = int.Parse(this.readIniValue("ILICIT", "BuiltAssemblies"));
				StringCollection assemblies = new StringCollection();	
				for (int i=0; i < builtAssembliesCount; i++) 
				{
					assemblies.Add(this.readIniValue("ILICIT", "BuiltAssembly" + i));
				}
				
				foreach (string assembly in assemblies)
				{
					string unquoted_assembly = assembly.Replace("\"", "");

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

												
							if (assemblyName.EndsWith(".exe")) 
							{
								this.writeIniValue("Common", "Executable" , assemblyName);
							}

							if (this.readIniValue("Global Composestar configuration", "BuildDebugLevel").Equals("3")) 
							{
								Debug.Instance.Log(DebugModes.Information, "AssemblyManager",String.Format(CultureInfo.CurrentCulture,"  Copied assembly '{0}'.", assemblyName));
							}
						}
						else 
						{
							if (this.readIniValue("Global Composestar configuration", "BuildDebugLevel").Equals("3")) 
							{
								Debug.Instance.Log(DebugModes.Error , "AssemblyManager",String.Format(CultureInfo.CurrentCulture,"  Unable to copy assembly '{0}', file doesn't exist!", assemblyName));
							}
						}
					}
				}
			}
			catch(Exception)
			{
			}

			// Copy the repository file
			string repositoryFileName = System.IO.Path.Combine(tempPath,"repository.xml");
			string repositoryFileNameOutput = System.IO.Path.Combine(outputPath, "repository.xml");

			if( System.IO.File.Exists(repositoryFileName))
			{
				System.IO.File.Copy(repositoryFileName, repositoryFileNameOutput, true);
			}
		}
	}
}
