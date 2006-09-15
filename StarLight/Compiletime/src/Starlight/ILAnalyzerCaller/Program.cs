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
            
            if (args[0].EndsWith(".yap"))
            {
                // Read from YAP file
                int methodCount = 0;

                System.Diagnostics.Stopwatch sw = new System.Diagnostics.Stopwatch();
                sw.Start();

                Repository.RepositoryAccess repository = new Repository.RepositoryAccess(args[0]);

                IList<Composestar.Repository.LanguageModel.TypeElement> dbtypes = repository.GetTypeElements();
                foreach (Composestar.Repository.LanguageModel.TypeElement type in dbtypes)
                {
                    int numOfMethods = repository.GetMethodElements(type).Count;
                    methodCount = methodCount + numOfMethods;
                    Console.WriteLine("Type {0} ({1}) found with {2} methods.", type.Name, type.BaseType, numOfMethods);
                }
                sw.Stop();

                Console.WriteLine("\n{0} types with in total {1} methods found in {2} seconds.", dbtypes.Count, methodCount, sw.Elapsed.TotalSeconds);

                return;
            }

             //IILAnalyzer analyzer = new ReflectionILAnalyzer();
            IILAnalyzer analyzer = new CecilILAnalyzer();
            analyzer.Initialize(args[0], null);
            
            //List<MethodElement> methods = analyzer.ExtractMethods();
            //IList<Composestar.Repository.LanguageModel.TypeElement> types = ((CecilILAnalyzer)analyzer).ExtractTypeInformation();

            //Console.WriteLine("{0} types found in {1} seconds.", types.Count, analyzer.LastDuration.TotalSeconds);
            Console.ReadKey(); 
        }
    }
}
