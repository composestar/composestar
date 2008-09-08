package Composestar.Core.CpsRepository2Impl.FilterElements;

import Composestar.Core.CpsRepository2.FilterElements.MECompareStatementTestBase;

/**
 * @author Michiel Hendriks
 */
public class CompatibilityMatchingTest extends MECompareStatementTestBase
{
	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		mecs = new CompatibilityMatching();
		re = mecs;
	}

}
