using System;
using java.util;

namespace JPInfo
{
	public class Bar
	{
		public string text = "barText@";

		public Bar()
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