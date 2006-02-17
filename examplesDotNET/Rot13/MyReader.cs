namespace Rot13Filter
{
	using System;
	using System.IO;

	/// <summary>
	/// Create a wrapper around StreamReader. Filters cannot be superimposed on .NET libraries.
	/// </summary>
	public class MyReader : IDisposable
	{
		private StreamReader stream;

		public MyReader(string filename)
		{
			stream = new StreamReader(filename);
		}

		public string ReadLine()
		{
			return stream.ReadLine();
		}

		#region IDisposable Members

		public void Dispose()
		{
			stream.Close();
		}

		#endregion
	}
}
