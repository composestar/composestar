using System;
using System.Collections.Generic;
using System.Text;
using System.Diagnostics;

namespace Benchmark
{
    class Benchmark
    {
        private void Execute()
        {

        }

        private void ExecuteFiltered()
        {
        }

        public void Run(int tests, bool details)
        {
            Stopwatch sw = new Stopwatch();

            List<TimeSpan> baseTimings = new List<TimeSpan>();
            List<TimeSpan> filteredTimings = new List<TimeSpan>();

            Console.WriteLine("Executing a {0} run benchmark, please wait...", tests);

            // Make sure both methods are JIT-Compiled before we start benchmarking
            Execute();
            ExecuteFiltered();

            tests = tests * 1000;

            int dotter = 1;
            if (tests >= 100) dotter = tests / 100;

            for (int i = 0; i < tests; i++)
            {
                if (i % 2 == 0)
                {
                    sw.Reset();
                    sw.Start();
                    Execute();
                    sw.Stop();
                    baseTimings.Add(sw.Elapsed);

                    sw.Reset();
                    sw.Start();
                    ExecuteFiltered();
                    sw.Stop();
                    filteredTimings.Add(sw.Elapsed);
                }
                else
                {
                    sw.Reset();
                    sw.Start();
                    Execute();
                    sw.Stop();
                    baseTimings.Add(sw.Elapsed);

                    sw.Reset();
                    sw.Start();
                    ExecuteFiltered();
                    sw.Stop();
                    filteredTimings.Add(sw.Elapsed);
                }

                if (i % dotter == 0) Console.Write(".");
            }

            Console.WriteLine("\n");
            Console.WriteLine("Benchmark results (in ms):");

            if (details) Console.WriteLine("{0,3}   {1,10}   {2,10}   {3,10}   {4,10}", "Run", "Normal", "WithFilter", "Overhead", "% overhead");

            TimeSpan baseElapsedTotal = TimeSpan.Zero;
            TimeSpan filteredElapsedTotal = TimeSpan.Zero;
            for (int i = 0; i < tests; i++)
            {
                TimeSpan baseElapsed = baseTimings[i];
                TimeSpan filteredElapsed = filteredTimings[i];
                TimeSpan overhead = filteredElapsed.Subtract(baseElapsed);
                baseElapsedTotal = baseElapsedTotal.Add(baseElapsed);
                filteredElapsedTotal = filteredElapsedTotal.Add(filteredElapsed);

                if (details) Console.WriteLine("{0,3}   {1,10:0.0000}   {2,10:0.0000}   {3,10:0.0000}   {4,10:0.00%}", i, baseElapsed.TotalMilliseconds, filteredElapsed.TotalMilliseconds, overhead.TotalMilliseconds, overhead.TotalMilliseconds / baseElapsed.TotalMilliseconds);
            }

            TimeSpan overheadTotal = filteredElapsedTotal.Subtract(baseElapsedTotal);

            if (details) Console.WriteLine();
            
            Console.WriteLine("Normal Avg.: {0:0.0000} ms", baseElapsedTotal.TotalMilliseconds / tests);
            Console.WriteLine("WithFilter Avg.: {0:0.0000} ms", filteredElapsedTotal.TotalMilliseconds / tests);
            Console.WriteLine("Overhead Avg.: {0:0.0000} ms   (equivalent to {1:0.00} method invocations)", overheadTotal.TotalMilliseconds / tests, overheadTotal.TotalMilliseconds / baseElapsedTotal.TotalMilliseconds);
        }
    }

    class Program
    {

        static void Main(string[] args)
        {
            int runs = 10;
            bool details = false;

            foreach (string arg in args)
            {
                if (arg.StartsWith("-runs=")) runs = int.Parse(arg.Substring(6));
                if (arg.Equals("-details")) details = true;
            }

            Benchmark b = new Benchmark();

            b.Run(runs, details);
        }
    }
}
