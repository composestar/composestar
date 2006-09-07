using System;

namespace FilterModuleParameter
{
	/// <summary>
	/// Summary description for Animal.
	/// </summary>
	public class Animal
	{
		private String name;

		public Animal()
		{
		}

		public void setName(String aName)
		{
			name = aName;
		}

		public String getName()
		{
			return name;
		}
		
		public void walk()
		{
			Console.Out.WriteLine("walking");
		}
	}
}
