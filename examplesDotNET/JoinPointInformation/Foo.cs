using System;
using java.util;

namespace JPInfo
{
	public class Foo
	{
		public string text = "fooText@";

		public Foo()
		{
			this.text = this.text + new Date();
		}

		public string sayText()
		{
			return this+" says: "+this.text;
		}
		
		public string ToString()
		{
			return this.text;
		}
	}
}