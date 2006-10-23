using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.CpsParser;
using Composestar.StarLight.CoreServices;
  
namespace Composestar.StarLight.CpsParserCaller
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

            CpsParserConfiguration config = new CpsParserConfiguration(args[0]);

            CpsParser.CpsFileParser cfp = new CpsFileParser(config);

            cfp.Parse();
        }


    }
}
