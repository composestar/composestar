using EnvDTE;
using Ini;
using System;
using System.IO;
using System.Collections;

namespace ComposestarVSAddin
{
	/// <summary>
	/// Summary description for EmbeddedSourceManager.
	/// </summary>
	public class EmbeddedSourceManager : AbstractManager
	{
		private IniFile inifile;
		private ProjectItem addedDirectory= null;

		public EmbeddedSourceManager(IniFile inifile) : base (inifile)
		{
			this.inifile = inifile;
		}

		public override void run(_DTE applicationObject, vsBuildScope scope, vsBuildAction action)
		{
			this.mApplicationObject = applicationObject;

			foreach(Project project in this.mApplicationObject.Solution.Projects)
			{
				this.addEmbeddedSources(project);
			}
		}	

		private void addEmbeddedSources(Project p)
		{
			// adding embedded sources to project
			// get embedded source folder from inifile
			
			string tempfolder = inifile.IniReadValue("COMMON", "TempFolder");
			tempfolder = tempfolder.Replace("/","\\");
			string embFolder = inifile.IniReadValue("Global Composestar configuration", "EmbeddedSourcesFolder");
			// remove \ character
			embFolder = embFolder.Substring(0,embFolder.Length-1);

			// path to embedded sources
			string embPath = tempfolder + embFolder ;
			
			DirectoryInfo di = new DirectoryInfo(embPath);
			
			if(di.Exists)
			{
				FileInfo[] sources = di.GetFiles("*.*");
				if(sources.Length>0)
				{
					try
					{
						addedDirectory = p.ProjectItems.AddFolder(embFolder,Constants.vsProjectItemKindPhysicalFolder);
					} 
					catch (Exception)
					{
						//Cannot check it because a bug in visual studio 2003
					}

					foreach(FileInfo src in sources)
					{
						try
						{
							p.ProjectItems.AddFromFile(src.FullName);
						}
						catch (Exception)
						{
						}
					}
				}
			}
		}

		public void removeEmbeddedSources()
		{
			string tempfolder = inifile.IniReadValue("COMMON", "TempFolder");
			tempfolder = tempfolder.Replace("/","\\");
			string embFolder = inifile.IniReadValue("Global Composestar configuration", "EmbeddedSourcesFolder");
			// remove \ character
			embFolder = embFolder.Substring(0,embFolder.Length-1);

			// path to embedded sources
			string embPath = tempfolder + embFolder ;

			DirectoryInfo di = new DirectoryInfo(embPath);

			if(addedDirectory != null)
			{
				addedDirectory.Delete();
			}
			else
			{
				deleteDirectory(di);
			}
		}

		public void deleteDirectory(DirectoryInfo di)
		{
			if(!di.Exists)
			{
				return;
			}

			foreach(DirectoryInfo subdir in di.GetDirectories())
			{
				deleteDirectory(subdir);
			}
			foreach(FileInfo file in di.GetFiles())
			{
				try
				{
					file.Delete();
				}
				catch(Exception)
				{
				}
			}
			try
			{
				di.Delete();
			}
			catch(Exception)
			{
			}		
		}
	}
}