using System;
using System.Collections.Generic;
using System.Text;

namespace RuBCoDeTest.TestCase
{
    public class MainClass
    {
        public MainClass()
	    {
            Console.WriteLine("\n==> Creating TargetClass");
            TargetClass tc = new TargetClass();

            Console.WriteLine("");
            Console.WriteLine("\n==> no args");
            tc.noargs();

            Console.WriteLine("");
            Console.WriteLine("\n==> simpleint");
            tc.simpleint(12345);
            Console.WriteLine("\n==> simpleint same intvalue");
            tc.simpleint(12345);

            Console.WriteLine("");
            Console.WriteLine("\n==> simplestring");
            tc.simplestring("a string");
            Console.WriteLine("\n==> simplestring same stringvalue");
            tc.simplestring("a string");

            Console.WriteLine("\n==> simpleobject");
            tc.simpleobject(new StringValue(""));
            Console.WriteLine("\n==> simpleobject identical object");
            tc.simpleobject(new StringValue(""));

            int i = 54321;
            string s = "an other string";
            StringValue obj = new StringValue("string value instance");

            Console.WriteLine("\n==> simpleint with variable");
            tc.simpleint(i);
            Console.WriteLine("\n==> simpleint with same variable");
            tc.simpleint(i);

            Console.WriteLine("\n==> simplestring with variable");
            tc.simplestring(s);
            Console.WriteLine("\n==> simplestring with same variable");
            tc.simplestring(s);

            Console.WriteLine("\n==> simpleobject with variable");
            tc.simpleobject(obj);
            Console.WriteLine("\n==> simpleobject with same variable");
            tc.simpleobject(obj);

            /*
            Console.WriteLine("\n==> outint with variable");
            tc.outint(out i);
            Console.WriteLine("\n==> outint with same variable");
            tc.outint(out i);

            Console.WriteLine("\n==> outstring with variable");
            tc.outstring(out s);
            Console.WriteLine("\n==> outstring with same variable");
            tc.outstring(out s);
            */

            Console.WriteLine("\n==> outobject with variable");
            tc.outobject(out obj);
            Console.WriteLine("\n==> outobject with same variable");
            tc.outobject(out obj);


            Console.WriteLine("\n==> refint with variable");
            tc.refint(ref i);
            Console.WriteLine("\n==> refint with same variable");
            tc.refint(ref i);

            Console.WriteLine("\n==> refstring with variable");
            tc.refstring(ref s);
            Console.WriteLine("\n==> refstring with same variable");
            tc.refstring(ref s);

            Console.WriteLine("\n==> refobject with variable");
            tc.refobject(ref obj);
            Console.WriteLine("\n==> refobject with same variable");
            tc.refobject(ref obj);

	    }

        public static bool doEncrypt()
        {
            return true;
        }

        public static int Main(string[] args)
        {
            new MainClass();
            return 0;
        }
    }
}
