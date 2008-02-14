using System;
using System.Collections.Generic;
using System.Text;

namespace GenericsTest
{
    /*
     * THIS IS NOT A WORKING EXAMPLE
     * Generics are not supported by Compose*, when they are this example 
     * should be changed to something working.
     */

    public class MainClass
    {
        public static int Main(string[] args)
        {
            MyGeneric<String> mgs2 = new MyStringGeneric();
            mgs2.Add("Test");
            mgs2.Remove("Test");

            MyGeneric<int> mgi2 = new MyIntGeneric();
            mgi2.Add(12345);
            mgi2.Remove(12345);

            MyGeneric<String> mgs = new MyGeneric<String>();
            mgs.Add("Test");
            mgs.Remove("Test");

            MyGeneric<int> mgi = new MyGeneric<int>();
            mgi.Add(12345);
            mgi.Remove(12345);

            return 0;
        }
    }
}
