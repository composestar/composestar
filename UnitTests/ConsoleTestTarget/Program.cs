using System;
using System.Collections.Generic;
using System.Text;

namespace ConsoleTestTarget
{
    class HelloWorld
    {
        
        private String messageEN;
        private String messageDU;

        public HelloWorld()
        {
            messageEN = "Hello World";
            messageDU = "Hallo Wereld";
        }

        public String GetMessage()
        {
           // string a = GetMessage();
            return messageEN;
        }

        public String GetMessage(int language)
        {
            if (language == 1) return messageDU;

            return messageEN;
        }

    }

    class Program
    {
        static void Main(string[] args)
        {
            HelloWorld hw = new HelloWorld();
            Console.WriteLine(hw.GetMessage() + "  |  " + hw.GetMessage(1));
        }
    }
}
