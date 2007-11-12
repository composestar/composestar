using System;
using System.Collections.Generic;
using System.Text;

namespace RuBCoDeTest.TestCase
{
	public class TargetClass
	{
		public TargetClass()
		{ }

        public void noargs()
        { }

        public void simpleint(int value)
        { }

        public void simplestring(string value)
        { }

        public void simpleobject(StringValue value)
        { }

        public void outint(out int value)
        {
            value = 1;
        }

        public void outstring(out string value)
        {
            value = "1";
        }

        public void outobject(out StringValue value)
        {
            value = new StringValue("1");
        }

        public void refint(ref int value)
        { }

        public void refstring(ref string value)
        { }

        public void refobject(ref StringValue value)
        { }

        public void combo(int v1, string v2, StringValue v3, StringValue v4)
        { }
	}
}
