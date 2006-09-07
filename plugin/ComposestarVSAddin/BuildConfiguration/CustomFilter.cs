using System;

namespace BuildConfiguration
{
	/// <summary>
	/// Summary description for CustomFilter.
	/// </summary>
	public class CustomFilter
	{
		public CustomFilter()
		{

		}

		public CustomFilter(string filterName, string assemblyName)
		{
			_filterName = filterName;
			_assemblyName = assemblyName;
		}


		private string _filterName = "";

		public string FilterName
		{
			get
			{
				return _filterName;
			}
			set
			{
				_filterName = value;
			}
		}
		private string _assemblyName = "";
		public string AssemblyName
		{
			get
			{
				return _assemblyName;
			}
			set
			{
				_assemblyName = value;
			}
		}

	}
}
