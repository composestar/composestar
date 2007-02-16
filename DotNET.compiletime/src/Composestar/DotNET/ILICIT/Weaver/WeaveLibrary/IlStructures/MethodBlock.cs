using System;
using System.Collections;

using Weavers.WeaveSpecifications;

namespace Weavers.IlStructures
{
	public class MethodBlock : IlStructure
	{
		private string mName = "";
		private string mFullMethodDefinition = "";
		private bool mEntryPoint = false;
		private int mMaxStack = 0;
		private bool mInitLocals = false;
		private bool mHasParamterLocals = false;
		private ArrayList mLocals = null;
		private ArrayList mIlOpcodes;
		private ArrayList mTryLines;
		private ArrayList mAttributes = null;

		public MethodBlock(string Name, string Definition)
		{
			this.mName = Name;
			this.mFullMethodDefinition = Definition;

			this.mLocals = new ArrayList();
			this.mIlOpcodes = new ArrayList();
			this.mTryLines = new ArrayList();
			this.mAttributes = new ArrayList();
		}

		public string Name
		{
			get 
			{
				return this.mName;
			}
		}

		public bool EntryPoint
		{
			get
			{
				return this.mEntryPoint;
			}
			set 
			{
				this.mEntryPoint = value;
			}
		}

		public int MaxStack
		{
			get 
			{
				return this.mMaxStack;
			}
			set
			{
				this.mMaxStack = value;
			}
		}
		
		public bool InitLocals
		{
			get 
			{
				return this.mInitLocals;
			}
			set 
			{
				this.mInitLocals = value;
			}
		}

		public bool IsStatic()
		{
			if (this.mFullMethodDefinition.IndexOf("static") > 0)
			{
				return true;
			}

			return false;
		}

		public void AddLocal(string local)
		{
			this.mLocals.Add(local);
		}

		public void AddLocals(string[] locals)
		{
			this.mLocals.AddRange(locals);
		}

		public int CountLocals()
		{
			return this.mLocals.Count;
		}

		public bool HasParameterLocals
		{
			get 
			{
				return this.mHasParamterLocals;
			}
			set
			{
				this.mHasParamterLocals = value;
			}
		}

		public void AddAttribute(string line)
		{
			this.mAttributes.Add(line);
		}

		public void AddAttributes(ArrayList lines)
		{
			this.mAttributes.AddRange(lines);
		}

		public void AddTryCodeLine(string line)
		{
			this.mTryLines.Add(line);
		}

		public void AddIlCode(IlOpcode Opcode)
		{
			this.mIlOpcodes.Add(Opcode);
		}

		public void ReplaceIlCode(ArrayList Opcodes)
		{
			this.mIlOpcodes.Clear();
			this.mIlOpcodes.AddRange(Opcodes);
		}

		public IEnumerator GetIlCodeEnumerator()
		{
			return this.mIlOpcodes.GetEnumerator();
		}

		public override void Replace(ArrayList replacements, ref Hashtable assemblyReferences, WeaveSpecification ws)
		{
			base.Replace (replacements, ref assemblyReferences, ws);

			this.mFullMethodDefinition = ReplaceIn(this.mFullMethodDefinition, replacements, ref assemblyReferences, ws);

			for (int i=0; i<this.mLocals.Count; i++)
			{
				this.mLocals[i] = ReplaceIn(this.mLocals[i].ToString(), replacements, ref assemblyReferences, ws);
			}

			for (int i=0; i<this.mAttributes.Count; i++)
			{
				this.mAttributes[i] = ReplaceIn(this.mAttributes[i].ToString(), replacements, ref assemblyReferences, ws);
			}
			
			for (int i=0; i<this.mIlOpcodes.Count; i++)
			{
				((IlStructure)this.mIlOpcodes[i]).Replace(replacements, ref assemblyReferences, ws);
			}
		}

		public override ArrayList ToStringList()
		{
			ArrayList result = new ArrayList();

			result.Add(".method " + this.mFullMethodDefinition);
			result.Add("{");

			result.AddRange(this.GetAdditionalCodeLines());

			if (this.EntryPoint) 
			{
				result.Add(".entrypoint");
			}
			result.Add(".maxstack " + this.MaxStack.ToString());
			
			if (this.mAttributes.Count > 0 )
			{
				result.AddRange(this.mAttributes);
			}

			if (this.mLocals.Count > 0) 
			{
				string locals = ".locals";
				if (this.InitLocals) locals += " init";
				locals += " (";
				result.Add(locals);

				for (int i=0; i<this.mLocals.Count; i++)
				{
					string local = "  " + this.mLocals[i].ToString();
					if (i < this.mLocals.Count-1) local += ",";
					result.Add(local);
				}

				result.Add(")");
			}

			for (int i=0; i<this.mIlOpcodes.Count; i++)
			{
				result.AddRange(((IlStructure)this.mIlOpcodes[i]).ToStringList());
			}

			result.AddRange(this.mTryLines);
			
			result.Add("} // end of method " + this.mName);

			return result;
		}
	}

}
