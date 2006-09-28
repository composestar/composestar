using System;
using System.Collections.Specialized;
using System.Collections.Generic;
using System.IO;
using System.Text;
using Composestar.StarLight.ILAnalyzer;
 
namespace Composestar.StarLight.ILAnalyzerCaller
{

    class Program
    {
        static void Main(string[] args)
        {
            if (args.Length < 1)
                return;

            string file = string.Empty;
            if (args[0].StartsWith("\"") && args[0].EndsWith("\""))
            {
                file = args[0].Substring(1, args[0].Length - 2);
            }
            else
            {
                file = args[0];
            }
            
            bool resolveFromCache = true;
            if (args.Length == 2)
            {
                if (args[1].ToLower().Equals("nocache")) resolveFromCache = false;
            }

            Console.WriteLine("Analyzing '{0}':", file);
            if (file.EndsWith(".yap"))
            {
                // Read from YAP file
                int methodCount = 0;

                System.Diagnostics.Stopwatch sw = new System.Diagnostics.Stopwatch();
                sw.Start();

                Repository.RepositoryAccess repository = new Repository.RepositoryAccess(Repository.Db4oContainers.Db4oRepositoryContainer.Instance, file);

                IList<Composestar.Repository.LanguageModel.TypeElement> dbtypes = repository.GetTypeElements();
                foreach (Composestar.Repository.LanguageModel.TypeElement type in dbtypes)
                {
                    int numOfMethods = repository.GetMethodElements(type).Count;
                    methodCount = methodCount + numOfMethods;
                    Console.WriteLine("Type {0} ({1}) found with {2} methods.", type.Name, type.BaseType, numOfMethods);
                }
                sw.Stop();

                Console.WriteLine("\n{0} types with in total {1} methods found in {2:0.0000} seconds.", dbtypes.Count, methodCount, sw.Elapsed.TotalSeconds);

                return;
            }
            else if (file.EndsWith(".dll"))
            {

                NameValueCollection config = new NameValueCollection();
                config.Add("RepositoryFilename", Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "starlight.yap"));
                config.Add("ProcessMethodBody", "false");
                config.Add("CacheFolder", "C:\\temp");

                //IILAnalyzer analyzer = new ReflectionILAnalyzer();
                IILAnalyzer analyzer = new CecilILAnalyzer();
                analyzer.Initialize(config);
                
                IlAnalyzerResults result = analyzer.ExtractTypeElements(file);

                if (result == IlAnalyzerResults.FROM_ASSEMBLY)
                {
                    Console.WriteLine("Summary: {0} resolved types, {1} unresolved types, {2:0.0000} seconds", analyzer.ResolvedTypes.Count, analyzer.UnresolvedTypes.Count, analyzer.LastDuration.TotalSeconds);
                }
                else
                {
                    Console.WriteLine("Summary: Assembly has not been modified, skipping analysis. (time {0:0.0000} seconds)", analyzer.LastDuration.TotalSeconds);
                }
                
                if (analyzer.UnresolvedTypes.Count <= 20)
                {
                    foreach (String ut in analyzer.UnresolvedTypes)
                    {
                        Console.WriteLine("  {0}", ut);
                    }
                }
                if (analyzer.UnresolvedTypes.Count > 0 && resolveFromCache)
                {
                    Console.WriteLine("Accessing cache for {0} unresolved types:", analyzer.UnresolvedTypes.Count);
                    int unresolvedCount = analyzer.UnresolvedTypes.Count;
                    analyzer.ProcessUnresolvedTypes();
                    Console.WriteLine("Cache lookup summary: {0} out of {1} types found in {2:0.0000} seconds.", unresolvedCount - analyzer.UnresolvedTypes.Count, unresolvedCount, analyzer.LastDuration.TotalSeconds);
                }
                analyzer.Close();
            }
            else
            {
                Console.WriteLine("Usage: IlAnalyzerCaller <YapFile>/<assembly>");
            }

            
            //Console.ReadKey(); 
        }
    }
}
