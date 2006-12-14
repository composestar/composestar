using System;
using System.Collections.Generic;
using System.Text;

namespace BasicTests
{
	public static class StaticFilterTests<T>
	{
		public static T func1(T value)
		{

			return value;
		}

		public static void func11(out int x)
		{
			Console.WriteLine("Func11: set value of x to 3");
			x = 3;
		}

		public static void func12(ref int x)
		{
			Console.WriteLine("Func12: current value of x: " + x + " Add two to value.");
			x = x + 2;
		}
	}

    public class MyLogger
    {
        private static MyLogger _logger = new MyLogger();

        private MyLogger()
        {
        }

        public static MyLogger GetInstance()
        {
            return _logger;
        }

        public void Write(String text)
        {

        }

        public void Write(String text, int code)
        {
        }

        public static void WriteLine(String text)
        {
        }

        public static void WriteLine(String text, int code)
        {
        }
    }
}
