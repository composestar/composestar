using System;

namespace Rot13Filter
{
	class Rot13Example
	{
		[STAThread]
		static void Main(string[] args)
		{
			try
			{
				using (MyReader reader = new MyReader("input.txt"))
				{
					string line;
					// Read until EOF.
					while ((line = reader.ReadLine()) != null)
					{
						Console.WriteLine(line);
					}
				}
			}
			catch (Exception e)
			{
				Console.WriteLine("The file could not be read:");
				Console.WriteLine(e.Message);
			}

			Console.Write("\nPress enter to exit...");
			Console.ReadLine();
		}
	}
}
