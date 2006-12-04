package ErrorFilterTest;

public class MainClass
{
	
	public static void main(String[] args)
	{
		try
		{
			Subject s = new Subject();
				
			s.f1();				
			s.f2(); // should fail at runtime
		}
		catch (Exception e)
		{
			System.out.println("Exception caught with message: " + e.getMessage());
		}
	}
}
