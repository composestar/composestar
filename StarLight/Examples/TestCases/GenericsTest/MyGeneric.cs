using System;

namespace GenericsTest
{
    public class GenTestBase
    {
    }

    public class MyGeneric<T> : GenTestBase
    {
        virtual public void Add(T input)
        {
            Console.Out.WriteLine("{0}.Add({1} {2})", GetType().Name, input.GetType().Name, input.ToString());
        }

        virtual public void AddString(String input)
        {
            Console.Out.WriteLine("{0}.AddString({1} {2})", GetType().Name, input.GetType().Name, input.ToString());
        }

        virtual public void Remove(T input)
        {
            Console.Out.WriteLine("{0}.Remove({1} {2})", GetType().Name, input.GetType().Name, input.ToString());
        }
    }

    public class MyStringGeneric : MyGeneric<String>
    {
        override public void Add(String input)
        {
            Console.Out.WriteLine("{0}.Add({1} {2})", GetType().Name, input.GetType().Name, input.ToString());
        }

        override public void Remove(String input)
        {
            Console.Out.WriteLine("{0}.Remove({1} {2})", GetType().Name, input.GetType().Name, input.ToString());
        }
    }

    public class MyIntGeneric : MyGeneric<int>
    {
        override public void Add(int input)
        {
            Console.Out.WriteLine("{0}.Add({1} {2})", GetType().Name, input.GetType().Name, input.ToString());
        }

        override public void Remove(int input)
        {
            Console.Out.WriteLine("{0}.Remove({1} {2})", GetType().Name, input.GetType().Name, input.ToString());
        }
    }
}
