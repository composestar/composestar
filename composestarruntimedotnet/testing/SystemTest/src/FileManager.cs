using System;
using System.IO;

namespace SystemTest
{
	/// <summary>
	/// Summary description for FileManager.
	/// </summary>
	public class FileManager
	{
		public FileManager()
		{
			
		}
	
		public string getContents(string filename)
		{
			try
			{
				//Pass the filename to the StreamReader constructor
				StreamReader sr = new StreamReader(filename);
				String line = "";
				String result = "";

				//Read the first line of text
				line = sr.ReadLine();

				//Continue to read until you reach end of file
				while (line != null)
				{
					// append line to build output
					result += line + "\n";

					//Read the next line
					line = sr.ReadLine();
				}

				//close the file
				sr.Close();
				return result;
			}
			catch(Exception e)
			{
				Console.WriteLine("Exception:" + e.Message);
				return "error";
			}
		}
	}
}