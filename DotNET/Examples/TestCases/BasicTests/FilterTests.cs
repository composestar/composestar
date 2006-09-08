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
