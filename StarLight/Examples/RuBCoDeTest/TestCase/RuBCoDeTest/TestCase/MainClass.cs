using System;
using System.Collections.Generic;
using System.Text;

namespace RuBCoDeTest.TestCase
{
    public class MainClass
    {
        private static bool _encrypt1 = false;
        private static bool _encrypt2 = false;

        public MainClass()
	    {
            TargetClass t = new TargetClass();

            t.sendMessage("alice@example.org", "bob@example.org", "Hello World!");

            Console.WriteLine("\n[ Increasing security ]");
            MainClass._encrypt2 = true;

            t.sendMessage("alice@example.org", "bob@example.org", "Hello World!");            
	    }

        public static bool encrypt1()
        {
            return _encrypt1;
        }

        public static bool encrypt2()
        {
            return _encrypt2;
        }

        public static int Main(string[] args)
        {
            if (args.Length > 0)
            {
                _encrypt1 = true;
            }
            new MainClass();
            return 0;
        }
    }
}
