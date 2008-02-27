@ApplyCaching
public class Fibonacci {
	
	/* Return the xth number in the fibonacci sequence */
	@CacheResult
	public long fibonacci(int x)
	{
		if (x <= 0) return -1;
		if (x <= 2) return 1;
		return (fibonacci(x-1) + fibonacci(x-2));
	}

	public static void main(String arg[])
	{
		Fibonacci a = new Fibonacci();

		System.out.println("Calculating the 5th fibonacci number: ");
		System.out.println(a.fibonacci(5));

		System.out.println("Calculating the 6th fibonacci number: "); 
		System.out.println(a.fibonacci(6));

		System.out.println("Calculating the 4th fibonacci number: "); 
		System.out.println(a.fibonacci(4));

		System.out.println("Calculating the 44th fibonacci number (this may take a while)");
		System.out.println(a.fibonacci(44));
	}
}
