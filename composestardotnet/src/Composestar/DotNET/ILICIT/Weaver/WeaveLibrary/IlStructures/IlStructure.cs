using System;
using System.Collections;

using Weavers.WeaveSpecifications;

namespace Weavers.IlStructures
{
	/// <summary>
	/// Summary description for IlStructure.
	/// </summary>
	public abstract class IlStructure
	{
		private ArrayList mAdditionalCode;

		public IlStructure()
		{
			mAdditionalCode = new ArrayList();
		}

		public void AddAdditionalCodeLine(string codeline)
		{
			this.mAdditionalCode.Add(codeline);
		}

		public ArrayList GetAdditionalCodeLines()
		{
			return this.mAdditionalCode ;
		}

		protected string ReplaceIn(String line, ArrayList replacements, ref Hashtable assemblyReferences, WeaveSpecification ws)
		{
			string newLine = line;

			// Search for replacement definitions
			if ( replacements.Count > 0 ) 
			{
				IEnumerator enumReplacementDefinitions = replacements.GetEnumerator();
				while (enumReplacementDefinitions.MoveNext())
				{
					ReplacementInformation ri = (ReplacementInformation)enumReplacementDefinitions.Current;

					if (!ri.ReplacementAssemblyName.Equals("")) 
					{
						newLine = newLine.Replace("[" + ri.AssemblyName + "]" + ri.ClassName, "[" + ri.ReplacementAssemblyName + "]" + ri.ReplacementClassName);
					}
					else 
					{
						newLine = newLine.Replace("[" + ri.AssemblyName + "]" + ri.ClassName, ri.ReplacementClassName);
					}

					if ( !assemblyReferences.ContainsKey(ri.ReplacementAssemblyName) && newLine.IndexOf("[" + ri.ReplacementAssemblyName + "]") >= 0 )
					{
						AssemblyInformation ai = ws.GetAssemblyInformation(ri.ReplacementAssemblyName);
				
						if (ai != null) 
						{
							assemblyReferences.Add(ri.ReplacementAssemblyName, ai);
						}
					}
				}
			}

			return newLine;
		}

		public virtual void Replace(ArrayList replacements, ref Hashtable assemblyReferences, WeaveSpecification ws)
		{
			ArrayList newCodeLines = new ArrayList();

			IEnumerator enumAdditionalCodeLines = this.mAdditionalCode.GetEnumerator();
			while (enumAdditionalCodeLines.MoveNext())
			{
				newCodeLines.Add(ReplaceIn((String)enumAdditionalCodeLines.Current, replacements, ref assemblyReferences, ws));			
			}

			this.mAdditionalCode = newCodeLines;
		}

		public virtual ArrayList ToStringList()
		{
			return new ArrayList();
		}
	}
}
