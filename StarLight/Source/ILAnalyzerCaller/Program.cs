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
    class AssemblyInfo
    {
        private int _typeCount = 0;

        public int TypeCount
        {
            get { return _typeCount; }
            set { _typeCount = value; }
        }

        private int _fieldCount = 0;

        public int FieldCount
        {
            get { return _fieldCount; }
            set { _fieldCount = value; }
        }

        private int _methodCount = 0;

        public int MethodCount
        {
            get { return _methodCount; }
            set { _methodCount = value; }
        }

        private int _parameterCount = 0;

        public int ParameterCount
        {
            get { return _parameterCount; }
            set { _parameterCount = value; }
        }	

        private int _methodCallCount = 0;

        public int MethodCallCount
        {
            get { return _methodCallCount; }
            set { _methodCallCount = value; }
        }

        private double _elapsedTime = 0;

        public double ElapsedTime
        {
            get { return _elapsedTime; }
            set { _elapsedTime = value; }
        }

        private int _unresolvedTypes = 0;

        public int UnresolvedTypes
        {
            get { return _unresolvedTypes; }
            set { _unresolvedTypes = value; }
        }
	
    }

    class Program
    {
        IILAnalyzer analyzer = null;
        String file;
        bool resolve;
        String outputFile;

        private Program(String path, bool shouldResolve, String outfile)
        {
            file = path;
            resolve = shouldResolve;
            outputFile = outfile;
        }

        private static void Main(string[] args)
        {
            if (args.Length < 1)
            {
                Console.WriteLine("Usage: IlAnalyzerCaller [-resolve] [-out=<filename>] <assembly>/<folder>");
                return;
            }

            string file = string.Empty;
            bool resolve = false;
            string outfile=  string.Empty;

            foreach (string arg in args)
            {
                // Parse options
                if (arg.StartsWith("-"))
                {
                    if (arg.ToLower().Equals("-resolve")) resolve = true;

                    if (arg.ToLower().StartsWith("-out=")) outfile = arg.Substring(5);
                    
                }
                else
                {
                    if (arg.StartsWith("\"") && arg.EndsWith("\""))
                    {
                        file = arg.Substring(1, args.Length - 2);
                    }
                    else
                    {
                        file = arg;
                    }
                }

            }

            Program p = new Program(file, resolve, outfile);
            p.Execute();
        }

        private void Execute()
        {
            if (Directory.Exists(file))
            {
                List<AssemblyInfo> info = new List<AssemblyInfo>();

                // Store output to file
                StreamWriter writer = null;
                if (!String.IsNullOrEmpty(outputFile))
                {
                    writer = File.CreateText(outputFile);
                    writer.WriteLine("Analysis report for '{0}':", file);
                }

                // Analyse entire folder
                DirectoryInfo di = new DirectoryInfo(file);
                foreach (FileInfo fi in di.GetFiles("*.dll"))
                {
                    try
                    {
                        Console.WriteLine("Analyzing '{0}'...", fi.FullName);
                        if (writer != null) writer.WriteLine("Analyzing '{0}'...", fi.FullName);

                        AssemblyInfo ai = AnalyzeFile(fi.FullName);

                        Console.WriteLine("File summary: {0} resolved types ({3} fields, {4} methods, {5} parameters, {6} methodcalls), {1} unresolved types, {2:0} ms", ai.TypeCount, ai.UnresolvedTypes, ai.ElapsedTime, ai.FieldCount, ai.MethodCount, ai.ParameterCount, ai.MethodCallCount);
                        if (writer != null) writer.WriteLine("File summary: {0} resolved types ({3} fields, {4} methods, {5} parameters, {6} methodcalls), {1} unresolved types, {2:0} ms", ai.TypeCount, ai.UnresolvedTypes, ai.ElapsedTime, ai.FieldCount, ai.MethodCount, ai.ParameterCount, ai.MethodCallCount);

                        info.Add(ai);
                    }
                    catch (BadImageFormatException)
                    {
                        Console.WriteLine("Not a valid assembly file, skipped.");
                        if (writer != null) writer.WriteLine("Not a valid assembly file, skipped.");
                    }
                    catch (Exception e)
                    {
                        Console.WriteLine("Exception: {0}", e.Message, e.StackTrace);
                        if (writer != null) writer.WriteLine("Exception: {0}", e.Message, e.StackTrace);
                    }

                    Console.WriteLine();
                    if (writer != null) writer.WriteLine();

                }

                // Summary
                int totaltypes = 0;
                int totalfields = 0;
                int totalmethods = 0;
                int totalparameters = 0;
                int totalmethodcalls = 0;
                double totaltime = 0;
                
                Console.WriteLine("Summary for {0} analyzed assemblies:", info.Count);
                if (writer != null) writer.WriteLine("Summary for {0} analyzed assemblies:", info.Count);

                foreach (AssemblyInfo ai in info)
                {
                    totaltypes += ai.TypeCount;
                    totalfields += ai.FieldCount;
                    totalmethods += ai.MethodCount;
                    totalparameters += ai.ParameterCount;
                    totalmethodcalls += ai.MethodCallCount;
                    totaltime += ai.ElapsedTime;
                }
                TimeSpan ts = TimeSpan.FromMilliseconds(totaltime);
                
                Console.WriteLine("total types: {0} (avg. types per assembly {1:0.0})", totaltypes, totaltypes / (float)info.Count);
                Console.WriteLine("total fields: {0} (avg. per assembly {1:0.0}, avg. per type {2:0.0})", totalfields, totalfields / (float)info.Count, totalfields / (float)totaltypes);
                Console.WriteLine("total methods: {0} (avg. per assembly {1:0.0}, avg. per type {2:0.0})", totalmethods, totalmethods / (float)info.Count, totalmethods / (float)totaltypes);
                Console.WriteLine("total parameters: {0} (avg. per method {1:0.0})", totalparameters, totalparameters / (float)totalmethods);
                Console.WriteLine("total methodcalls: {0} (avg. per type {1:0.0}. avg. per method {2:0.0})", totalmethodcalls, totalmethodcalls / (float)totaltypes, totalmethodcalls / (float)totalmethods);
                Console.WriteLine("total analysis time: {0:00}:{1:00}.{2:00} minutes", ts.Minutes, ts.Seconds, ts.Milliseconds);

                if (writer != null) writer.WriteLine("total types: {0} (avg. types per assembly {1:0.0})", totaltypes, totaltypes / (float)info.Count);
                if (writer != null) writer.WriteLine("total fields: {0} (avg. per assembly {1:0.0}, avg. per type {2:0.0})", totalfields, totalfields / (float)info.Count, totalfields / (float)totaltypes);
                if (writer != null) writer.WriteLine("total methods: {0} (avg. per assembly {1:0.0}, avg. per type {2:0.0})", totalmethods, totalmethods / (float)info.Count, totalmethods / (float)totaltypes);
                if (writer != null) writer.WriteLine("total parameters: {0} (avg. per method {1:0.0})", totalparameters, totalparameters / (float)totalmethods);
                if (writer != null) writer.WriteLine("total methodcalls: {0} (avg. per type {1:0.0}. avg. per method {2:0.0})", totalmethodcalls, totalmethodcalls / (float)totaltypes, totalmethodcalls / (float)totalmethods);
                if (writer != null) writer.WriteLine("total analysis time: {0:00}:{1:00}.{2:00} minutes", ts.Minutes, ts.Seconds, ts.Milliseconds);


                if (writer != null) writer.Close();
            }
            else if (file.EndsWith(".dll"))
            {
                try
                {
                    Console.WriteLine("Analyzing '{0}'...", file);

                    AssemblyInfo ai = AnalyzeFile(file);

                    Console.WriteLine("File summary: {0} resolved types ({3} fields, {4} methods, {5} parameters, {6} methodcalls), {1} unresolved types, {2:0} ms", ai.TypeCount, ai.UnresolvedTypes, ai.ElapsedTime, ai.FieldCount, ai.MethodCount, ai.ParameterCount, ai.MethodCallCount);
                }
                catch (BadImageFormatException)
                {
                    Console.WriteLine("Not a valid assembly file, skipped.");
                }
                catch (Exception e)
                {
                    Console.WriteLine("Exception: {0}", e.Message, e.StackTrace);
                }
            }
            //else if (file.EndsWith(".yap"))
            //{
            //    Console.WriteLine("Dumping contents of '{0}'", file);

            //    // Read from YAP file
            //    int methodCount = 0;

            //    System.Diagnostics.Stopwatch sw = new System.Diagnostics.Stopwatch();
            //    sw.Start();

            //    Repository.RepositoryAccess repository = new Repository.RepositoryAccess(Repository.Db4oContainers.Db4oRepositoryContainer.Instance, file);

            //    IList<Composestar.Repository.LanguageModel.TypeElement> dbtypes = repository.GetTypeElements();
            //    foreach (Composestar.Repository.LanguageModel.TypeElement type in dbtypes)
            //    {
            //        int numOfMethods = repository.GetMethodElements(type).Count;
            //        methodCount = methodCount + numOfMethods;
            //        Console.WriteLine("Type {0} ({1}) found with {2} methods.", type.Name, type.BaseType, numOfMethods);
            //    }
            //    sw.Stop();

            //    Console.WriteLine("\n{0} types with in total {1} methods found in {2:0.0000} seconds.", dbtypes.Count, methodCount, sw.Elapsed.TotalSeconds);

            //    repository.Close();
            //}


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

        private AssemblyInfo AnalyzeFile(String file)
        {
            AssemblyInfo result = new AssemblyInfo();

            ILanguageModelAccessor langModelAccessor = null;
            try
            {
                langModelAccessor = new RepositoryAccess(Db4oRepositoryContainer.Instance, Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "starlight.yap"));
                CecilAnalyzerConfiguration configuration = new CecilAnalyzerConfiguration(Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "starlight.yap"));

                NameValueCollection config = new NameValueCollection();
                config.Add("ProcessMethodBody", "true");

                analyzer = DIHelper.CreateObject<CecilILAnalyzer>(CreateContainer(langModelAccessor, configuration));

                System.Diagnostics.Stopwatch sw = new System.Diagnostics.Stopwatch();
                sw.Start();

                AssemblyElement assembly = analyzer.ExtractAllTypes(file);

                sw.Stop();

                // Create statistics
                int fields = 0;
                int methods = 0;
                int parameters = 0;
                int methodcalls = 0;
                int attributes = 0;
                foreach (TypeElement te in assembly.TypeElements)
                {
                    fields += te.FieldElements.Length;
                    methods += te.MethodElements.Length;
                    foreach (MethodElement me in te.MethodElements)
                    {
                        parameters += me.ParameterElements.Length;

                        if (me.HasMethodBody && me.MethodBody.CallElements != null)
                        {

                            methodcalls += me.MethodBody.CallElements.Length;
                        }
                    }
                }
                
                result.TypeCount = assembly.TypeElements.Length;
                result.FieldCount = fields;
                result.MethodCount = methods;
                result.ParameterCount = parameters;
                result.MethodCallCount = methodcalls;
                result.ElapsedTime = sw.Elapsed.TotalMilliseconds;
                result.UnresolvedTypes = analyzer.UnresolvedTypes.Count;

                sw.Reset();

                List<AssemblyElement> assemblies = new List<AssemblyElement>();
                assemblies.Add(assembly);

                if (analyzer.UnresolvedTypes.Count > 0 && resolve)
                {
//                    Console.WriteLine("Processing {0} unresolved types...", analyzer.UnresolvedTypes.Count);
                    foreach (String ut in analyzer.UnresolvedTypes)
                    {
                        Console.WriteLine("  {0}", ut);
                    }

                    if (analyzer.UnresolvedTypes.Count > 0)
                    {
                        foreach (String ut in analyzer.UnresolvedTypes)
                        {
                            Console.WriteLine("  {0}", ut);
                        }
                    }
                }
            }
            finally
            {
                analyzer.Close();
                langModelAccessor.Close();
            }

            return result;
        }
    }
}
