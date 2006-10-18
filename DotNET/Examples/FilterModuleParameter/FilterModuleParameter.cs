using System;

namespace FilterModuleParameter
{
	/// <summary>
	/// Summary description for Class1.
	/// </summary>
	class FilterModuleParameter
	{
		/// <summary>
		/// The main entry point for the application.
		/// </summary>
		[STAThread]
		static void Main(string[] args)
		{
			Cat karel = new Cat();
			Iguanidae ziggy = new Iguanidae();

			karel.feed();
			ziggy.feed();

			karel.walk();
			ziggy.walk();

			karel.makeNoise();  
		}
	}
}