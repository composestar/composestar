using System;
using System.Collections;

using Weavers.IlStructures;

namespace Weavers.IO
{
	/// <summary>
	/// Summary description for IlFileWriter.
	/// </summary>
	public class IlFileWriter
	{
		private bool mQuiet = false;
		private bool mDebug = false;

		public IlFileWriter(bool quiet, bool debug)
		{
			this.mQuiet = quiet;
			this.mDebug = debug;
		}

		public bool Save(ArrayList ilcodes, string file)
		{
			if (!this.mQuiet) 
			{
				Console.WriteLine("Saving modified IL code to file '" + file + "'...");
			}

			// Save IL code to file
			System.IO.StreamWriter sw = new System.IO.StreamWriter(file, false, System.Text.Encoding.UTF8);
			for (int j=0; j < ilcodes.Count; j++)
			{
				if (ilcodes[j].GetType().ToString().Equals("System.String"))
				{
					// Object in IlStructures is a string.
					sw.WriteLine(ilcodes[j].ToString());
				}
				else 
				{
					// Object in IlStructures is of type IlStructure
					IlStructure structure = (IlStructure)ilcodes[j];
					ArrayList codelines = structure.ToStringList();
					for (int k=0; k < codelines.Count; k++)
					{
						sw.WriteLine(codelines[k].ToString());
					}
				}
			}
			sw.Close();

			return true;
		}
	}
}
