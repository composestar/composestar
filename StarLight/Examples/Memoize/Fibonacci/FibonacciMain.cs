using System;
using System.Collections.Generic;
using System.Text;

namespace Fibonacci
{
    [ApplyCaching]
    public class FibonacciMain
    {
        public FibonacciMain()
        {}

        [CacheResult]
        public int fibonacci(int x)
        {
            if (x <= 0) return -1;
            if (x <= 2) return 1;
            return (fibonacci(x-1) + fibonacci(x-2));
        }

        public static int Main(string[] args)
        {
            FibonacciMain fib = new FibonacciMain();

            Console.Out.WriteLine("Calculating the 5th fibonacci number: ");
            Console.Out.WriteLine("{0}", fib.fibonacci(5));

            Console.Out.WriteLine("Calculating the 6th fibonacci number: ");
            Console.Out.WriteLine("{0}", fib.fibonacci(6));

            Console.Out.WriteLine("Calculating the 4th fibonacci number: ");
            Console.Out.WriteLine("{0}", fib.fibonacci(4));

            Console.Out.WriteLine("Calculating the 44th fibonacci number (this may take a while)");
            Console.Out.WriteLine("{0}", fib.fibonacci(44));

            return 0;
        }
    }
}
