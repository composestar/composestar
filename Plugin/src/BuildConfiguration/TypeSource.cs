using System;

namespace BuildConfiguration
{
	/// <summary>
	/// Summary description for TypeSource.
	/// </summary>
	public class TypeSource
	{
		public TypeSource()
		{

		}

		public TypeSource(string name, string fileName)
		{
			_fileName = fileName;
			_name = name;
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

		private string _name ="";

		public string Name
		{
			get
			{
				return _name;
			}
			set
			{
				_name = value;
			}
		}
	}
}
