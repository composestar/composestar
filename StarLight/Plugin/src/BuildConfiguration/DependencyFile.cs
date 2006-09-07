using System;

namespace BuildConfiguration
{
	/// <summary>
	/// Summary description for DependencyFile.
	/// </summary>
	public class DependencyFile
	{
		public DependencyFile()
		{

		}

		private string _fileName ="";

		public string FileName
		{
			get
			{
				return _fileName;
			}
			set
			{
				_fileName = value;
			}
		}
	}
}
