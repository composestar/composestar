using System;
using System.Collections;

namespace Weavers.IlStructures
{
	/// <summary>
	/// Summary description for NamespaceBlock.
	/// </summary>
	public class NamespaceBlock : IlStructure
	{
		private string mNamespace = "";
		private ArrayList mClasses;

		public NamespaceBlock(string Name)
		{
			this.mNamespace = Name;

			this.mClasses = new ArrayList();
		}

		public string Namespace
		{
			get 
			{
				return this.mNamespace;
			}
		}

		public void AddClass(ClassBlock Block)
		{
			this.mClasses.Add(Block);
		}

		public void UpdateClass(ClassBlock Block)
		{
			for (int i=0; i<this.mClasses.Count; i++)
			{
				if (((ClassBlock)this.mClasses[i]).Name.Equals(Block.Name))
				{
					this.mClasses.RemoveAt(i);
					break;
				}
			}

			this.AddClass(Block);
		}

		public IEnumerator GetClassEnumarator()
		{
			return this.mClasses.GetEnumerator();
		}

		public override ArrayList ToStringList()
		{
			ArrayList result = new ArrayList();
			
			result.Add(".namespace " + this.Namespace);
			result.Add("{");
			
			for (int i=0; i<this.mClasses.Count; i++)
			{
				result.AddRange(((ClassBlock)this.mClasses[i]).ToStringList());
			}

			result.Add("}");

			return result;
		}
	}

}
