using System;
using System.Collections.Generic;
using System.Text;

namespace RuBCoDeTest.TestCase
{
    public class MainClass
    {
        private static bool encrypt1 = true;
        private static bool encrypt2 = false;

        public MainClass()
	    {
            Console.WriteLine("RuBCoDe Test");
            Console.WriteLine("------------");
            TargetClass t = new TargetClass();

            Console.WriteLine("Sending a message with security");
            t.sendMessage("foo", "bar", "Hello World!");

            Console.WriteLine("Enabling encrypt2");
            MainClass.encrypt2 = true;
            t.sendMessage("foo", "bar", "Hello World!");
	    }

        public static bool encrypt1()
        {
            return encrypt1;
        }

        public static bool encrypt1()
        {
            return encrypt2;
        }

        public static int Main(string[] args)
        {
            if (args.Length > 0)
            {
                encrypt1 = false;
            }
            new MainClass();
            return 0;
        }
    }
}
