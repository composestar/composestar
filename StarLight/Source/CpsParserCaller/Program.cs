using System;
using System.Collections.Generic;
using System.Text;

using Composestar.CpsParser;
using Composestar.StarLight.CoreServices;
  
namespace Composestar.CpsParserCaller
{
    class Program
    {
        private static void Main(string[] args)
        {
            if (args.Length < 1)
            {
                Console.WriteLine("Usage: CpsParserCaller <concern>");
                return;
            }

            CpsParser.CpsFileParser cfp = new Composestar.CpsParser.CpsFileParser();

            cfp.ParseFile(args[0]);
        }


    }
}
