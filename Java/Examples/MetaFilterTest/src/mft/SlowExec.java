package mft;

public class SlowExec {
	public static final String RETURN_VALUE = "This was slow";

	public String beSlow() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		System.out.println("We just performed a slow operation");
		return RETURN_VALUE;
	}
}
