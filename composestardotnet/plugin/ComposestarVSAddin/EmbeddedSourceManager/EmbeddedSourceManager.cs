using EnvDTE;
using Ini;
using System;
using System.IO;
using System.Collections;
using BuildConfiguration;

namespace ComposestarVSAddin
{
	/// <summary>
	/// Summary description for EmbeddedSourceManager.
	/// </summary>
	public class EmbeddedSourceManager : AbstractManager
	{

		private ProjectItem addedDirectory= null;

		public EmbeddedSourceManager() : base ()
		{

		}

		public override void run(_DTE applicationObject, vsBuildScope scope, vsBuildAction action)
		{
			this.mApplicationObject = applicationObject;

			foreach(EnvDTE.Project project in this.mApplicationObject.Solution.Projects)
			{
				this.addEmbeddedSources(project);
			}
		}	

		private void addEmbeddedSources(EnvDTE.Project p)
		{
			// adding embedded sources to project
			// get embedded source folder from inifile
			
			string tempfolder = BuildConfigurationManager.Instance.Settings.Paths["Temp"];
			tempfolder = tempfolder.Replace("/","\\");
			string embFolder = BuildConfigurationManager.Instance.Settings.Paths["EmbeddedSources"];
			// remove \ character
			embFolder = embFolder.Substring(0,embFolder.Length-1);

			// path to embedded sources
			string embPath = Path.Combine(tempfolder , embFolder);
			
			DirectoryInfo di = new DirectoryInfo(embPath);
			
			// TODO could be removed since this can be done directly in the master. This also leads to only one phase

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
			string tempfolder = BuildConfigurationManager.Instance.Settings.Paths["Temp"];
			tempfolder = tempfolder.Replace("/","\\");
			string embFolder = BuildConfigurationManager.Instance.Settings.Paths["EmbeddedSources"];
			// remove \ character
			embFolder = embFolder.Substring(0,embFolder.Length-1);

			// path to embedded sources
			string embPath =  Path.Combine(tempfolder, embFolder);

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