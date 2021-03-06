package BasicTests;

import Composestar.Java.FLIRT.Env.ReifiedMessage;

public class FilterTestsHelper extends TestsBase {
	public FilterTestsHelper() {
	}

	public void helpMe() {
		report("helpMe");
	}

	public void helped() {
		report("helped");
	}

	public void doStuffAround(ReifiedMessage rm) {
		report("doStuffAround::before");
		rm.proceed();
		report("doStuffAround::after");
	}

	public void makeTrip() {
		report("makeTrip");
	}
}
