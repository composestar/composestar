using EnvDTE;
using System.IO;
using Ini;

namespace ComposestarVSAddin
{
	/// <summary>
	/// Summary description for EmbeddedSourceManager.
	/// </summary>
	public class EmbeddedSourceManager : AbstractManager
	{
		private IniFile inifile;
		private ProjectItem addedDirectory = null;

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
					bool hasDir = false;
					foreach (EnvDTE.ProjectItem item in p.ProjectItems)
					{
						hasDir |= item.Name.Equals(embFolder);
					}
					
					if(!hasDir) p.ProjectItems.AddFolder(embFolder,Constants.vsProjectItemKindPhysicalFolder);

					foreach(FileInfo src in sources)
					{
						ProjectItem pitem = p.ProjectItems.AddFromFile(src.FullName);   
					}
				}
			}
		}

		public void removeEmbeddedSources()
		{
			foreach(Project project in this.mApplicationObject.Solution.Projects)
			{
				this.removeEmbeddedSources(project);
			}	
		}

		public void removeEmbeddedSources(Project p)
		{
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
				// remove directory from project and storage
				if(this.addedDirectory!=null)
					this.addedDirectory.Delete();
				else
					di.Delete();	
			}
		}
	}
}
