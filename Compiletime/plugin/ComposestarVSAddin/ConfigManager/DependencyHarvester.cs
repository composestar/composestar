using System;
using System.Collections;
using System.Collections.Specialized;
using System.Xml.XPath;
using EnvDTE;
using VSLangProj;

namespace ComposestarVSAddin
{
	/// <summary>
	/// Summary description for DependencyHarvester.
	/// </summary>
	public class DependencyHarvester
	{
		private DependencyHarvester()
		{
			
		}

		public static StringCollection Collect(EnvDTE.Project project, bool copyLocalOnly) 
		{
			StringCollection references = new StringCollection();

		
			// Cast to VSProject to access the references, luckily C#, J# and VB projects are casted
			VSProject vsproj = (VSProject)project.Object;
			if (vsproj != null) 
			{
				IEnumerator enumReferences = vsproj.References.GetEnumerator();
				while (enumReferences.MoveNext()) 
				{
					if ( ((Reference)enumReferences.Current).Path != null && !((Reference)enumReferences.Current).Path.Equals(""))
						if ( (!copyLocalOnly) || (copyLocalOnly && ((Reference)enumReferences.Current).CopyLocal) ) 
						{
							references.Add( ((Reference)enumReferences.Current).Path);
						}
				}
			}
			
			return references;

		}
	}
}
