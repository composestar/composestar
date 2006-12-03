using System;
using System.Collections.Generic;
using System.Text;

namespace BasicTests
{
	struct MyValueType
	{
		private int _minimum, _maximum;

		public MyValueType(int min, int max)
		{
			_minimum = min;
			_maximum = max;
		}

		public int Minimum
		{
			get { return _minimum; }
		}

		public int Maximum
		{
			get { return _maximum; }
		}
	}
}
