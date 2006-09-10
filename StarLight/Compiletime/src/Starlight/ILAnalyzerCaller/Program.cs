using System;
using System.Collections.Generic;
using System.Text;
using Composestar.StarLight.ILAnalyzer;
 
namespace Composestar.StarLight.ILAnalyzerCaller
{

    class Program
    {
        static void Main(string[] args)
        {
            if (args.Length != 1)
                return;
            
            //IILAnalyzer analyzer = new ReflectionILAnalyzer();
            IILAnalyzer analyzer = new CecilILAnalyzer();
            analyzer.Initialize(args[0], null);
            
            List<MethodElement> methods = analyzer.ExtractMethods();

            Console.WriteLine("{0} methods found in {1} seconds.", methods.Count, analyzer.LastDuration.TotalSeconds);
            Console.ReadKey(); 
        }
    }
}
