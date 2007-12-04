using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;

namespace RuBCoDeTest.TestCase
{
	public class TargetClass
	{
		public TargetClass()
		{ }

        public void sendMessage(string sender, string receiver, string message)
        {
            Console.WriteLine("\nSending message");
            Console.WriteLine("Sender:   {0}", sender);
            Console.WriteLine("Receiver: {0}", receiver);
            Console.WriteLine("Message:  {0}", message);
        }

        public void archiveMessage(JoinPointContext jpc)
        {
            string sender = (string)jpc.GetArgumentValue(0);
            string receiver = (string)jpc.GetArgumentValue(1);
            string message = (string)jpc.GetArgumentValue(2);
            Console.WriteLine("\nArchiving message");
            Console.WriteLine("Sender:   {0}", sender);
            Console.WriteLine("Receiver: {0}", receiver);
            Console.WriteLine("Message:  {0}", message);
        }
        
        public void discardMessage(string sender, string receiver, string message)
        {
            Console.WriteLine("\nDiscarding message");
            Console.WriteLine("Sender:   {0}", sender);
            Console.WriteLine("Receiver: {0}", receiver);
            Console.WriteLine("Message:  {0}", message);
        }            
	}
}
