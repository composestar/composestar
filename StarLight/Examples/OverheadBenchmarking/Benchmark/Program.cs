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

        private void ExecuteTracing(int number)
        {
        }

        private void ExecuteReflectionTracing(int number)
        {
        }

        public void Run(int tests, bool details)
        {
            Stopwatch sw = new Stopwatch();
            sw.Start();
            sw.Stop();
            sw.Reset();

            List<TimeSpan> baseTimings = new List<TimeSpan>();
            List<TimeSpan> emptymethodTimings = new List<TimeSpan>();
            List<TimeSpan> filteredTimings = new List<TimeSpan>();
            List<TimeSpan> tracingTimings = new List<TimeSpan>();
            List<TimeSpan> reflectiontracingTimings = new List<TimeSpan>();

            Console.WriteLine("Executing a {0} run benchmark, please wait...", tests);

            // Make sure both methods are JIT-Compiled before we start benchmarking
            Execute();
            ExecuteFiltered();
            ExecuteTracing(-1);
            ExecuteReflectionTracing(-1);

            tests = tests * 1000;

            int dotter = 1;
            if (tests >= 100) dotter = tests / 100;

            for (int i = 0; i < tests; i++)
            {
                if (i % 2 == 0)
                {
                    sw.Reset();
                    sw.Start();
                    sw.Stop();
                    baseTimings.Add(sw.Elapsed);

                    sw.Reset();
                    sw.Start();
                    Execute();
                    sw.Stop();
                    emptymethodTimings.Add(sw.Elapsed);

                    sw.Reset();
                    sw.Start();
                    ExecuteFiltered();
                    sw.Stop();
                    filteredTimings.Add(sw.Elapsed);

                    sw.Reset();
                    sw.Start();
                    ExecuteTracing(i);
                    sw.Stop();
                    tracingTimings.Add(sw.Elapsed);

                    sw.Reset();
                    sw.Start();
                    ExecuteReflectionTracing(i);
                    sw.Stop();
                    reflectiontracingTimings.Add(sw.Elapsed);
                }
                else
                {
                    sw.Reset();
                    sw.Start();
                    ExecuteReflectionTracing(i);
                    sw.Stop();
                    reflectiontracingTimings.Add(sw.Elapsed);

                    sw.Reset();
                    sw.Start();
                    ExecuteTracing(i);
                    sw.Stop();
                    tracingTimings.Add(sw.Elapsed);

                    sw.Reset();
                    sw.Start();
                    ExecuteFiltered();
                    sw.Stop();
                    filteredTimings.Add(sw.Elapsed);

                    sw.Reset();
                    sw.Start();
                    Execute();
                    sw.Stop();
                    emptymethodTimings.Add(sw.Elapsed);

                    sw.Reset();
                    sw.Start();
                    sw.Stop();
                    baseTimings.Add(sw.Elapsed);
                }

                FilterTypes.TraceBuffer.Flush();

                if (i % dotter == 0) Console.Write(".");
            }

            Console.WriteLine("\n");
            Console.WriteLine("Benchmark results (in ms):");

            if (details) Console.WriteLine("{0,3}   {1,10}   {2,10}   {3,10}   {4,10}   {5,10}", "Run", "Normal", "WithFilter", "Tracing", "Overhead", "% overhead");

            TimeSpan baseElapsedTotal = TimeSpan.Zero;
            TimeSpan emptymethodElapsedTotal = TimeSpan.Zero;
            TimeSpan filteredElapsedTotal = TimeSpan.Zero;
            TimeSpan tracingElapsedTotal = TimeSpan.Zero;
            TimeSpan reflectiontracingElapsedTotal = TimeSpan.Zero;

            for (int i = 0; i < tests; i++)
            {
                TimeSpan baseElapsed = baseTimings[i];
                TimeSpan emptymethodElapsed = emptymethodTimings[i];
                TimeSpan filteredElapsed = filteredTimings[i];
                TimeSpan tracingElapsed = tracingTimings[i];
                TimeSpan reflectiontracingElapsed = reflectiontracingTimings[i];
                TimeSpan overhead = filteredElapsed.Subtract(baseElapsed);
                baseElapsedTotal = baseElapsedTotal.Add(baseElapsed);
                emptymethodElapsedTotal = emptymethodElapsedTotal.Add(emptymethodElapsed);
                filteredElapsedTotal = filteredElapsedTotal.Add(filteredElapsed);
                tracingElapsedTotal = tracingElapsedTotal.Add(tracingElapsed);
                reflectiontracingElapsedTotal = reflectiontracingElapsedTotal.Add(reflectiontracingElapsed);

                if (details) Console.WriteLine("{0,3}   {1,10:0.0000}   {2,10:0.0000}   {3,10:0.0000}   {4,10:0.00%}", i, baseElapsed.TotalMilliseconds, filteredElapsed.TotalMilliseconds, overhead.TotalMilliseconds, overhead.TotalMilliseconds / baseElapsed.TotalMilliseconds);
            }

            TimeSpan overheadTotal = filteredElapsedTotal.Subtract(baseElapsedTotal);

            if (details) Console.WriteLine();

            Console.WriteLine("Timing overhead Avg.: {0:0.0000} ms", baseElapsedTotal.TotalMilliseconds / tests);
            Console.WriteLine("Normal Avg.: {0:0.0000} ms    (corrected {1:0.0000} ms)", emptymethodElapsedTotal.TotalMilliseconds / tests, emptymethodElapsedTotal.Subtract(baseElapsedTotal).TotalMilliseconds / tests);
            Console.WriteLine("WithFilter Avg.: {0:0.0000} ms    (corrected {1:0.0000} ms)", filteredElapsedTotal.TotalMilliseconds / tests, filteredElapsedTotal.Subtract(baseElapsedTotal).TotalMilliseconds / tests);
            Console.WriteLine("Tracing Avg.: {0:0.0000} ms    (corrected {1:0.0000} ms)", tracingElapsedTotal.TotalMilliseconds / tests, tracingElapsedTotal.Subtract(baseElapsedTotal).TotalMilliseconds / tests);
            Console.WriteLine("Tracing with reflection Avg.: {0:0.0000} ms    (corrected {1:0.0000} ms)", reflectiontracingElapsedTotal.TotalMilliseconds / tests, reflectiontracingElapsedTotal.Subtract(baseElapsedTotal).TotalMilliseconds / tests);
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
