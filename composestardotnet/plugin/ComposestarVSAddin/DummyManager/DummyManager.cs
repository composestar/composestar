using System;
using System.Collections;
using System.Collections.Specialized;
using EnvDTE;
using Ini;
using VSLangProj;
using BuildConfiguration;

namespace ComposestarVSAddin 
{
	/// <summary>
	/// Summary description for DummyGenerator.
	/// </summary>
	public class DummyManager : AbstractManager
	{
		private SupportedLanguages languages = null;

		private DummyGenerator generator = null;

		private string ExecSource;
		
		private string ProjectStartupObject;

		private ArrayList TypeSources;
	
		private bool mSuccess = false;

		public bool CompletedSuccessfully() 
		{
			return mSuccess;
		}

		public DummyManager() : base ()
		{
			languages = new SupportedLanguages();

			TypeSources = new ArrayList();

		}

		public override void run(_DTE applicationObject, vsBuildScope scope, vsBuildAction action) 
		{
			this.mApplicationObject = applicationObject;

			System.Collections.ArrayList dllNames = new System.Collections.ArrayList();
			
			string referencedAssemblies = "";
			foreach(EnvDTE.Project project in applicationObject.Solution.Projects ) 
			{
				if( project != null  && project.Properties != null)
				{
					System.Collections.ArrayList dependencies = new System.Collections.ArrayList();
					// Cast to VSProject to access the references, luckily C#, J# and VB projects are casted
					VSProject vsproj = (VSProject)project.Object;
				
					if (vsproj != null) 
					{
						IEnumerator enumReferences = vsproj.References.GetEnumerator();
						while (enumReferences.MoveNext())
						{
							dependencies.Add("\""+((Reference)enumReferences.Current).Path+"\"");
						}
					}
					if(dependencies.Count > 0)
					{
						if( referencedAssemblies.Length == 0 )
							referencedAssemblies = "/r:"+ string.Join(",",(string[])dependencies.ToArray(typeof(string)));
						else
							referencedAssemblies += "," + string.Join(",",(string[])dependencies.ToArray(typeof(string)));
					}

					if(project.Properties.Item("StartupObject") != null && !"".Equals(project.Properties.Item("StartupObject").Value.ToString().Trim()))
					{
						this.ProjectStartupObject = project.Properties.Item("StartupObject").Value.ToString().Trim();
					}

					ProcessProjectFiles(project.ProjectItems,project,applicationObject.Solution);
					
					//Try to fix things the system doesn't know
					if(this.ProjectStartupObject != null)
					{
						if(project.Properties.Item("StartupObject") == null || "".Equals(project.Properties.Item("StartupObject").Value.ToString().Trim()))
						{
							project.Properties.Item("StartupObject").Value = this.ProjectStartupObject;
						}

						if(this.ExecSource == null)
						{
							string projectName = project.FullName.Replace("\\","/");
							string fileName = (this.ProjectStartupObject.Substring(this.ProjectStartupObject.IndexOf(".") + 1,this.ProjectStartupObject.Length - (this.ProjectStartupObject.IndexOf(".") +1))).Replace(".","/");
							this.ExecSource = projectName.Substring(0,projectName.LastIndexOf("/"))+ "/" +  fileName;

							switch(project.Kind)
							{				
								case PrjKind.prjKindVSAProject:
								default:
									this.generator = new DummyGenerator("JS");
									this.ExecSource += ".jsl";
									break;

								case PrjKind.prjKindVBProject:
									this.generator = new DummyGenerator("CS");
									this.ExecSource += ".vb";
									break;

								case PrjKind.prjKindCSharpProject:
									this.generator = new DummyGenerator("CS");
									this.ExecSource += ".cs";
									break;
							}
						}
						Debug.Instance.Log(DebugModes.Information,"DummyManager","Startup object filename not found, gambling on " + this.ExecSource);
					}
				}
			

				System.CodeDom.Compiler.CompilerResults compilerResults = null;
				string dllPath = generator.generateDummies(BuildConfigurationManager.Instance.Settings.Paths["Base"]  , referencedAssemblies, out compilerResults);
		
				if ((dllPath != null) && (compilerResults != null) && (compilerResults.Errors.Count > 0) )
				{
					IEnumerator errorResults = compilerResults.Errors.GetEnumerator();
					System.CodeDom.Compiler.CompilerError err;
					while( errorResults.MoveNext() )
					{
						err = (System.CodeDom.Compiler.CompilerError) errorResults.Current;
					
						Debug.Instance.AddTaskItem(String.Format("Compose* Dummy Generation; {0}",  err.ErrorText),
							EnvDTE.vsTaskPriority.vsTaskPriorityMedium, 
							EnvDTE.vsTaskIcon.vsTaskIconCompile,
							err.FileName, err.Line);  
					}
				
					mSuccess = false;
					return;
				}

				if( dllPath != null )
					dllNames.Add(dllPath.Replace("\\", "/"));

				// Check whether startup object has been set!
				const string NoStartupObjectSet = "Compose* Dummy Manager; No startup object has been set. (see Project properties/Common properties/General/Startup object)";
				if (null == this.ProjectStartupObject)
				{
					mSuccess = false;
					Debug.Instance.AddTaskItem(NoStartupObjectSet, vsTaskPriority.vsTaskPriorityHigh, vsTaskIcon.vsTaskIconCompile);  
					return; // Stop execution
				}

				// Check whether startup object is in a file!
				const string NoStartupObjectFileSet = "Compose* Dummy Manager; File which contains startup object has not been found. (see Project properties/Common properties/General/Startup object)";
				if (null == this.ExecSource)
				{
					mSuccess = false;
					Debug.Instance.AddTaskItem(NoStartupObjectFileSet, vsTaskPriority.vsTaskPriorityHigh, vsTaskIcon.vsTaskIconCompile);  
					return; // Stop execution
				}

				DependencyInserter.Equals(applicationObject.Solution.Projects, dllNames);

				// Store dll path's in configuration file
				string dummypaths = String.Join(",", (string[])dllNames.ToArray(typeof(string)));
				ModuleSetting ilicitSettings = new ModuleSetting ();
				ilicitSettings.Name = "ILICIT";
				ilicitSettings.Elements.Add("assemblies", dummypaths)  ;
				BuildConfigurationManager.Instance.Settings.SetModule(ilicitSettings); 
			
				BuildConfigurationManager.Instance.Executable = this.ExecSource;

				BuildConfiguration.Project p = BuildConfigurationManager.Instance.GetProjectByName(project.Name);

				p.TypeSources.AddRange(this.TypeSources);  
			
			}
			mSuccess = true;
		}
		
		private void ProcessProjectFiles(ProjectItems projectitems, EnvDTE.Project project, Solution solution) 
		{
			foreach (ProjectItem projectitem in projectitems)
			{
				// this is a folder, process the files in it
				if (projectitem.ProjectItems.Count > 0) 
				{
					ProcessProjectFiles(projectitem.ProjectItems, project, solution);
				}
				else if(projectitem.Name.ToUpper().EndsWith(".RESX"))
				{
					if(this.ProjectStartupObject == null)
					{
						string newFileName = projectitem.get_FileNames(0).Replace("\\", "/");
						string packageName = solution.FullName.Substring(solution.FullName.LastIndexOf("\\") + 1,solution.FullName.Length - (solution.FullName.LastIndexOf("\\") + 5));
						
						this.ExecSource = newFileName.Substring(0,newFileName.Length - 5);
						this.ProjectStartupObject = packageName + "." + projectitem.Name.Substring(0,projectitem.Name.Length - 5);
			
                        switch(project.Kind)
						{				
							case PrjKind.prjKindVSAProject:
							default:
								this.generator = new DummyGenerator("JS");
								this.ExecSource += ".jsl";
								break;

							case PrjKind.prjKindVBProject:
								this.generator = new DummyGenerator("CS");
								this.ExecSource += ".vb";
								break;

							case PrjKind.prjKindCSharpProject:
								this.generator = new DummyGenerator("CS");
								this.ExecSource += ".cs";
								break;
						}
					}
				}
				else // this is a file
				{
					// fetch the filecodemodel
					FileCodeModel cm = projectitem.FileCodeModel;
					
					if( cm != null ) // check if it is a source file
					{
						if (generator == null) 
						{
							switch(cm.Language)
							{
								case EnvDTE.CodeModelLanguageConstants.vsCMLanguageCSharp:
								case EnvDTE.CodeModelLanguageConstants.vsCMLanguageVB:
								case EnvDTE.CodeModelLanguageConstants.vsCMLanguageVC:
									generator = new DummyGenerator("CS");
									break;
								default:
									generator = new DummyGenerator("JS");
									break;
							}
						}

						 recurseElements(projectitem,cm.CodeElements);
					}
				}
			}
		}

		public void recurseElements(ProjectItem projectItem, CodeElements codeElements) 
		{
			foreach(CodeElement codeElement in codeElements) 
			{
				switch(codeElement.Kind)
				{
					case vsCMElement.vsCMElementNamespace:
						CodeNamespace nameSpace = (CodeNamespace)codeElement;
						recurseElements(projectItem, nameSpace.Members);
						break;

					case vsCMElement.vsCMElementClass:
					case vsCMElement.vsCMElementModule:
						registerType(codeElement);
						CodeClass codeClass = (CodeClass) codeElement;

						// Found the file of the start object
						if( this.ProjectStartupObject != null && this.ProjectStartupObject.Equals(codeClass.FullName.ToString().Trim()) )
						{
							this.ExecSource = projectItem.get_FileNames(0).Replace("\\", "/");
						}
						generator.createDummy(codeClass);
						break;

					case vsCMElement.vsCMElementEnum:
						registerType(codeElement);
						EnvDTE.CodeEnum codeEnum = (CodeEnum) codeElement;
						generator.createDummy(codeEnum);
						break;

					case vsCMElement.vsCMElementInterface:
						registerType(codeElement);
						CodeInterface codeInterface = (CodeInterface) codeElement;
						generator.createDummy(codeInterface);
						break;

					default:
						//unknown element kind
						break;
				}
			}
		}

		public void registerType(CodeElement ce)
		{
			string fullname = ce.FullName;
			string filename = ce.ProjectItem.get_FileNames(0);

			this.TypeSources.Add(new TypeSource(fullname, filename));
		}

	}
}
