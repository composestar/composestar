package BasicTests;

public class TypeTests extends TestsBase
{
	public TypeTests()
	{}

	public boolean dummyBool()
	{
		return false;
	}

	public boolean realBool()
	{
		return true;
	}

	public byte dummyByte()
	{
		return 0;
	}

	public byte realByte()
	{
		return 123;
	}

	public char dummyChar()
	{
		return ' ';
	}

	public char realChar()
	{
		return 'c';
	}
	
	public double dummyDouble()
	{
		return 0;
	}

	public double realDouble()
	{
		return 1234.5;
	}
	
	public float dummyFloat()
	{
		return 0;
	}

	public float realFloat()
	{
		return 1234.5f;
	}
	
	public int dummyInt()
	{
		return 0;
	}

	public int realInt()
	{
		return 12345;
	}

	public String dummyString()
	{
		return "";
	}

	public String realString()
	{
		return "12345";
	}

	public short dummyShort()
	{
		return 0;
	}

	public short realShort()
	{
		return -31876;
	}
	
	public long dummyLong()
	{
		return 0;
	}

	public long realLong()
	{
		return 0xf011223344L;
	}

	public Comparable dummyInterface()
	{
		return null;
	}

	public Comparable realInterface()
	{
		return new Character('c');
	}
}
