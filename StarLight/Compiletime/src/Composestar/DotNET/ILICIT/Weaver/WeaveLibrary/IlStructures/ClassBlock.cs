using System;
using System.Collections;

using Weavers.WeaveSpecifications;

namespace Weavers.IlStructures
{
	public class ClassBlock : IlStructure
	{
		private string mNamespace = "";
		private string mName = "";
		private string mAttributes = "";
		private string mExtends = "";
		private ArrayList mChilds;

		public ClassBlock(string Name)
		{
			this.mName = Name;

			this.mChilds = new ArrayList();
		}

		public string Name
		{
			get 
			{
				return this.mName;
			}
		}

		public string Namespace
		{
			set 
			{
				this.mNamespace = value;
			}
		}

		public string FullName
		{
			get
			{
				if (this.mNamespace.Equals(""))
				{
					return this.mName;
				}
				return this.mNamespace + "." + this.mName;
			}
		}

		public string Attributes
		{
			get
			{
				return this.mAttributes;
			}
			set 
			{
				this.mAttributes = value;
			}
		}

		public string Extends
		{
			get 
			{
				return this.mExtends;
			}
			set 
			{
				this.mExtends = value;
			}
		}

		public bool HasMethodDeclarations()
		{
			for (int i=0; i<this.mChilds.Count; i++)
			{
				if (this.mChilds[i].GetType().ToString().Equals("Weavers.IlStructures.MethodBlock"))
				{
					return true;
				}
			}
			
			return false;
		}

		public void AddChild(Object block)
		{
			this.mChilds.Add(block);
		}

		public ArrayList GetMethodDeclarations()
		{
			ArrayList result = new ArrayList();

			for (int i=0; i<this.mChilds.Count; i++)
			{
				if (this.mChilds[i].GetType().ToString().Equals("Weavers.IlStructures.MethodBlock"))
				{
					result.Add(this.mChilds[i]);
				}
			}

			return result;
		}

		public override void Replace(ArrayList replacements, ref Hashtable assemblyReferences, WeaveSpecification ws)
		{
			base.Replace (replacements, ref assemblyReferences, ws);

			this.Extends = ReplaceIn(this.Extends, replacements, ref assemblyReferences, ws);

			for (int i=0; i<this.mChilds.Count; i++)
			{
				if (this.mChilds[i].GetType().ToString().Equals("System.String"))
				{
					this.mChilds[i] = ReplaceIn(this.mChilds[i].ToString(), replacements, ref assemblyReferences, ws);
				}
				else 
				{
					((IlStructure)this.mChilds[i]).Replace(replacements, ref assemblyReferences, ws);
				}
			}
		}

		public override ArrayList ToStringList()
		{
			ArrayList result = new ArrayList();
			
			if (this.Attributes.Equals("")) 
			{
				result.Add(".class " + this.mName);
			}
			else 
			{
				result.Add(".class " + this.Attributes + " " + this.mName);
			}

			if (!this.Extends.Equals("")) result.Add("extends " + this.Extends);
			
			for (int i=0; i<this.mChilds.Count; i++)
			{
				if (this.mChilds[i].GetType().ToString().Equals("System.String"))
				{
					result.Add(this.mChilds[i].ToString());
				}
				else 
				{
					result.AddRange(((IlStructure)this.mChilds[i]).ToStringList());
				}
			}

			return result;
		}     
	}
}
