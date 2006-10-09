using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.ComponentModel.Design;
using System.Globalization;
using System.IO;
using System.Reflection;
using System.Text;

using Microsoft.Practices.ObjectBuilder;

using Composestar.Repository.LanguageModel;
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.ILAnalyzer;
using Composestar.Repository.Db4oContainers;
using Composestar.Repository; 
 
namespace Composestar.StarLight.ILAnalyzerCaller
{

    class Program
    {
        private static void Main(string[] args)
        {
            if (args.Length < 1)
            {
                Console.WriteLine("Usage: IlAnalyzerCaller <YapFile>/<assembly>/<folder> [nocache]");
                return;
            }

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

                                        IILAnalyzer analyzer = null;
       
            if (Directory.Exists(file))
            {
                // Analyse entire folder
                DirectoryInfo di = new DirectoryInfo(file);
                foreach (FileInfo fi in di.GetFiles("*.dll"))
                {
                    Console.WriteLine("Analyzing file '{0}'", fi.FullName);

                    try
                    {
                        System.Reflection.Assembly assembly = System.Reflection.Assembly.LoadFile(fi.FullName);

                        String yap = assembly.GetName().FullName.Replace(", Version=", "_");
                        yap = yap.Replace(", Culture=", "_");
                        yap = yap.Replace(", PublicKeyToken=", "_");
                        yap = yap + ".yap";

             ILanguageModelAccessor langModelAccessor = new RepositoryAccess(Db4oRepositoryContainer.Instance, Path.Combine("c:\\temp", yap));
            CecilAnalyzerConfiguration configuration = new CecilAnalyzerConfiguration("", Path.Combine("c:\\temp", yap)); 

                        NameValueCollection config = new NameValueCollection();
                        config.Add("RepositoryFilename", Path.Combine("c:\\temp", yap));
                        config.Add("ProcessMethodBody", "false");
                        config.Add("CacheFolder", "D:\\ComposestarRepository\\trunk\\StarLight\\installed");

                        analyzer = DIHelper.CreateObject<CecilILAnalyzer>(CreateContainer(langModelAccessor, configuration));
                        
                        AssemblyElement result = analyzer.ExtractAllTypes(fi.FullName);

                        Console.WriteLine("Summary: {0} resolved types, {1} unresolved types, {2:0.0000} seconds", analyzer.ResolvedTypes.Count, analyzer.UnresolvedTypes.Count, analyzer.LastDuration.TotalSeconds);
                        
                        analyzer.Close();
                    }
                    catch (BadImageFormatException) {
                        Console.WriteLine("Not a valid assembly file, skipped.");
                    }
                    catch (Exception e)
                    {
                        Console.WriteLine("{0}\n{1}", e.Message, e.StackTrace);
                    }




                }



            }
            else if (file.EndsWith(".yap"))
            {
                Console.WriteLine("Dumping contents of '{0}'", file);

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

                repository.Close();
            }
            else if (file.EndsWith(".dll"))
            {
                Console.WriteLine("Analyzing '{0}'...", file);

                if (!resolveFromCache && File.Exists("starlight.yap")) File.Delete("starlight.yap");

                       ILanguageModelAccessor langModelAccessor = new RepositoryAccess(Db4oRepositoryContainer.Instance, Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "starlight.yap"));
            CecilAnalyzerConfiguration configuration = new CecilAnalyzerConfiguration("", Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "starlight.yap")); 
                
                NameValueCollection config = new NameValueCollection();
                config.Add("RepositoryFilename", Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "starlight.yap"));
                config.Add("ProcessMethodBody", "true");
                config.Add("CacheFolder", "D:\\ComposestarRepository\\trunk\\StarLight\\installed");

                RepositoryAccess ra = new RepositoryAccess("starlight.yap");

                //IILAnalyzer analyzer = new ReflectionILAnalyzer();
                  analyzer = DIHelper.CreateObject<CecilILAnalyzer>(CreateContainer(langModelAccessor, configuration));
                    
                
                ra.DeleteTypeElements(file);

                //if (resolveFromCache)
                //{
                //    foreach (TypeElement t in ra.GetTypeElements())
                //    {
                //        analyzer.CachedTypes.Add(String.Format("{0}, {1}", t.FullName, t.Assembly));
                //    }

                    
                //}

                System.Diagnostics.Stopwatch sw = new System.Diagnostics.Stopwatch();
                sw.Start();

                AssemblyElement assembly = analyzer.ExtractAllTypes(file);

                sw.Stop();
                
                //Console.WriteLine("File summary: {0} resolved types, {1} unresolved types, {2} cached types, {3:0.0000} seconds", assembly.TypeElements.Length, analyzer.UnresolvedTypes.Count, analyzer.CachedTypes.Count, sw.Elapsed.TotalSeconds);

                sw.Reset();

                List<AssemblyElement> assemblies = new List<AssemblyElement>();
                assemblies.Add(assembly);

                if (analyzer.UnresolvedTypes.Count > 0 && resolveFromCache)
                {
                    Console.WriteLine("Processing {0} unresolved types...", analyzer.UnresolvedTypes.Count);
                    foreach (String ut in analyzer.UnresolvedTypes)
                    {
                        Console.WriteLine("  {0}", ut);
                    }

                    sw.Start();

                    //assemblies.AddRange(analyzer.ProcessUnresolvedTypes());

                    sw.Stop();

//                    Console.WriteLine("Resolving summary: {0} total types resolved, {1} unresolvable types, {2} total cached types, {3:0.0000} seconds", analyzer.ResolvedTypes.Count, analyzer.UnresolvedTypes.Count, analyzer.CachedTypes.Count, sw.Elapsed.TotalSeconds);

                    if (analyzer.UnresolvedTypes.Count > 0)
                    {
                        foreach (String ut in analyzer.UnresolvedTypes)
                        {
                            Console.WriteLine("  {0}", ut);
                        }
                    }
                }

                sw.Reset();

                Console.WriteLine("Storing type information in database...");
                
                

                sw.Start();
                
                ra.AddAssemblies(assemblies, analyzer.ResolvedTypes);

                ra.Close();

                sw.Stop();


                Console.WriteLine("Storage summary: {0} assemblies stored in {1:0.0000} seconds.", assemblies.Count, sw.Elapsed.TotalSeconds);


                //Console.WriteLine("Extracted {1} types from '{0}' in {2} seconds.", assembly.Name, assembly.TypeElements.Length, sw.Elapsed.TotalSeconds);
                //IlAnalyzerResults result = analyzer.ExtractTypeElements(file);


                //if (result == IlAnalyzerResults.FROM_ASSEMBLY)
                //{
                //    Console.WriteLine("Summary: {0} resolved types, {1} unresolved types, {2:0.0000} seconds", analyzer.ResolvedTypes.Count, analyzer.UnresolvedTypes.Count, analyzer.LastDuration.TotalSeconds);
                //}
                //else
                //{
                //    Console.WriteLine("Summary: Assembly has not been modified, skipping analysis. (time {0:0.0000} seconds)", analyzer.LastDuration.TotalSeconds);
                //}

                //if (analyzer.UnresolvedTypes.Count > 0)
                //{
                //    foreach (String ut in analyzer.UnresolvedTypes)
                //    {
                //        Console.WriteLine("  {0}", ut);
                //    }
                //}
                //if (analyzer.UnresolvedTypes.Count > 0 && resolveFromCache)
                //{
                //    Console.WriteLine("Accessing cache for {0} unresolved types:", analyzer.UnresolvedTypes.Count);
                //    int unresolvedCount = analyzer.UnresolvedTypes.Count;
                //    analyzer.ProcessUnresolvedTypes();
                //    Console.WriteLine("Cache lookup summary: {0} out of {1} types found in {2:0.0000} seconds.", unresolvedCount - analyzer.UnresolvedTypes.Count, unresolvedCount, analyzer.LastDuration.TotalSeconds);
                //}
                //analyzer.Close();
            }
            
            //Console.ReadKey(); 
        }

        /// <summary>
        /// Creates the services container.
        /// </summary>
        /// <param name="languageModel">The language model.</param>
        /// <param name="configuration">The configuration.</param>
        /// <returns></returns>
        internal static IServiceProvider CreateContainer(ILanguageModelAccessor languageModel, CecilAnalyzerConfiguration configuration)
        {
            ServiceContainer serviceContainer = new ServiceContainer();
            serviceContainer.AddService(typeof(ILanguageModelAccessor), languageModel);
            serviceContainer.AddService(typeof(CecilAnalyzerConfiguration), configuration);
      
            return serviceContainer;
        }
    }
}
