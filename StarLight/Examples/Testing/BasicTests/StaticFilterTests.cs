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
