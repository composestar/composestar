class varinit
{
	static final String[] foo = "foo".split(".");
	
	final String bar;
	final String x,y,z;
	
	final int i;
	final byte b;
	final short s;
	final long l;
	final double d;
	final float f;
	final boolean bl;
	final String str;
	final Object obj;
	
	{
		System.out.print(bar = "bar");
		System.out.print(bar);
		System.out.print(y = (x = bar));
	}
	
	class Stub
	{
		int quux;
	}
	
	varinit()
	{
		super();
		z = foo[0];
		Stub st = new Stub();
		st.quux = 1;
	}
}