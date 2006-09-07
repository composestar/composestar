using System;
using System.Collections;
using System.Xml.XPath;
using EnvDTE;
using VSLangProj;

namespace ComposestarVSAddin
{
	/// <summary>
	/// Summary description for DependencyInserter.
	/// </summary>
	public class DependencyInserter
	{
		private DependencyInserter()
		{
		}

		public static void Insert(Projects projects, ArrayList references) 
		{
			foreach (Project project in projects) 
			{
				// Cast to VSProject to access the references, luckily C#, J# and VB projects are casted
				VSProject vsproj = (VSProject)project.Object;
				if (vsproj != null) 
				{
					IEnumerator enumReferencesToInsert = references.GetEnumerator();
					while (enumReferencesToInsert.MoveNext())
					{
						vsproj.References.Add(enumReferencesToInsert.Current.ToString());
					}

					project.Save(project.FullName);
				}
			}

		}
	}
}
