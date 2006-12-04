package ConditionTests;

public class TestsBase
{
	public TestsBase()
	{
		report("ctor");
	}

	public void report(Object arg)
	{
		System.out.println(this.getClass().getName()+"."+arg+"()");
	}
}