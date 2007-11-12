using System;
using System.Collections.Generic;
using System.Text;

namespace RuBCoDeTest.TestCase
{
	public class StringValue
	{
        private string _value;

		public StringValue(string value)
		{
            _value = value;
		}

        public override string ToString()
        {
            return _value;
        }

        public override int GetHashCode()
        {
            return _value.GetHashCode();
        }

        public override bool Equals(Object o)
        {
            if (o == null) return false;
            if (!(o is StringValue)) return false;
            return ((StringValue)o)._value.Equals(_value);
        }
	}
}
