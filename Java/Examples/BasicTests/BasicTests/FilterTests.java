package BasicTests;

public class FilterTests extends TestsBase
{
	protected FilterTestsHelper helper = new FilterTestsHelper();

	protected boolean errorCheck = false;

	public FilterTests()
	{}

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

	public void setProduceError(boolean inval)
	{
		errorCheck = inval;
	}

	public boolean getProduceError()
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
