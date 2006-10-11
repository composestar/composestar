using System;

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

            return s;
        }

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