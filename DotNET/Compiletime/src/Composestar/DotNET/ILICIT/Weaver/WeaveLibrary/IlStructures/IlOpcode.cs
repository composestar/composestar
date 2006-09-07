using System;
using System.Collections;

using Weavers.WeaveSpecifications;

namespace Weavers.IlStructures
{
	public class IlOpcode : IlStructure
	{
		private string mLabel = "";
		private string mOpcode = "";
		private string mArgument = "";

		public IlOpcode(string Label, string Opcode) 
		{
			this.mLabel = Label;
			this.mOpcode = Opcode;
		}

		public IlOpcode(string Label, string Opcode, string Argument) 
		{
			this.mLabel = Label;
			this.mOpcode = Opcode;
			this.mArgument = Argument;
		}

		public string Label
		{
			get 
			{
				return this.mLabel;
			}
			set
			{
				this.mLabel = value;
			}
		}

		public string Opcode
		{
			get 
			{
				return this.mOpcode;
			}
			set
			{
				this.mOpcode = value;
			}
		}

		public string Argument
		{
			get 
			{
				return this.mArgument;
			}
			set 
			{
				this.mArgument = value;
			}
		}

		public override void Replace(ArrayList replacements, ref Hashtable assemblyReferences, WeaveSpecification ws)
		{
			base.Replace (replacements, ref assemblyReferences, ws);

			this.Argument = ReplaceIn(this.Argument, replacements, ref assemblyReferences, ws);
		}

		public override ArrayList ToStringList()
		{
			ArrayList result = new ArrayList();

			if (this.mLabel != null && this.mOpcode != null && this.mArgument != null)
			{
				string line = "";

				if (this.mLabel.Equals("NONE")) 
				{
					line = this.mArgument;
				}
				else 
				{
					line += this.mLabel + ":  " + this.mOpcode;

					if (!this.Argument.Equals("")) 
					{
						line = line.PadRight(22, ' ');
						line += this.Argument;
					}
				}

				result.Add(line);
			}
			else 
			{
				//result.Add("// illegal il statement encountered (label, opcode or argument is null, something went really bad here)");
			}
		
			return result;
		}
	}

}
