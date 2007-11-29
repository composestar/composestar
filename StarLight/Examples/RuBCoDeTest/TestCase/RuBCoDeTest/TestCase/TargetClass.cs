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
            Console.WriteLine("Sending message to {1} from {0}, test: {2}", sender, receiver, message);
        }

        public void archiveMessage(string sender, string receiver, string message)
        {
            Console.WriteLine("Archiving message to {1} from {0}, test: {2}", sender, receiver, message);
        }
        
        public void discardMessage(string sender, string receiver, string message)
        {
            Console.WriteLine("Discarding message to {1} from {0}, test: {2}", sender, receiver, message);
        }            
	}
}
