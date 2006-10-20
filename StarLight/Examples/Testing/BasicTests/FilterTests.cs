#region Using directives
using System;
#endregion

namespace BasicTests
{
	public class FilterTests: TestsBase
	{
		protected FilterTestsHelper helper = new FilterTestsHelper();
		protected bool errorCheck = false;

		public FilterTests()
		{
		}

		public void func1()
		{
			report("func1");
		}

		public void func2()
		{
			report("func2");
		}

		public void func3()
		{
			report("func3");
		}

        public String func4(int number)
        {
            report("func4: " + number);
            String s = "" + number;

            if (s == null)
            {
                s = "";
            }

            Type type = typeof(System.Buffer);

            return s;
        }

        /// <summary>
        /// Func out test
        /// </summary>
        /// <param name="number">Number</param>
        /// <param name="refnumber">Refnumber</param>
        /// <returns>String</returns>
        public string funcOutTest(out int number, ref int refnumber)
        {
            number = 3;
            refnumber = 4;
            return number.ToString(); 
        } // funcOutTest(number, refnumber)

        public void func5(int number)
        {
            while (number > 0)
            {
                switch (number)
                {
                    case 10:
                        Console.WriteLine("10");
                        break;
                    case 15:
                        Console.WriteLine("15");
                        break;
                    case 456:
                        Console.WriteLine("456");
                        break;
                    case 97638:
                        Console.WriteLine("97638");
                        break;
                }

                number--;
            }
        }

        public void func6()
        {
            Console.WriteLine("func4 returns: " + func4(1));
            Console.WriteLine("second call to func4 returns: " + func4(2));
            Console.WriteLine("third call to func4 returns: " + func4(3));
            Console.WriteLine("fourth call to func4 returns: " + func4(4));
        }

		public void askForHelp()
		{
			helper.helpMe();
		}

		public void doStuff()
		{
			report("doStuff");
		}

		public void setProduceError(bool inval)
		{
			errorCheck = inval;
		}

		public bool getProduceError()
		{
			return errorCheck;
		}

		public void makeError()
		{
			report("makeError");
		}

		public void makeTrip()
		{
			report("makeTrip");
		}

		public void doVisit()
		{
			report("doVisit");
		}

		public void makeOutsideTrip()
		{
			report("makeOutsideTrip");
			helper.makeTrip();
		}

		public void doOutsideVisit()
		{
			report("doVisit");
		}
	}
}
