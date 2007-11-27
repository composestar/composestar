using System;
using System.Collections.Generic;
using System.Text;

namespace RuBCoDeTest.TestCase
{
	public class TargetClass
	{
		public TargetClass()
		{ }

        public void sendMessage(string sender, string receiver, string message)
        {
            Console.WriteLine("Sending message to {1} from {0}, test: {3}", sender, receiver, message);
        }
	}
}
