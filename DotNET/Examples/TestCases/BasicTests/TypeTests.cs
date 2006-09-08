using System;

namespace BasicTests
{
	public class TypeTests: TestsBase
	{
		public TypeTests()
		{
		}

		public bool dummyBool()
		{
			return false;
		}

		public bool realBool()
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

		public int dummyInt()
		{
			return 0;
		}

		public int realInt()
		{
			return 12345;
		}

		public float dummyFloat()
		{
			return 0;
		}

		public float realFloat()
		{
			return 1234.5f;
		}

		public double dummyDouble()
		{
			return 0;
		}

		public double realDouble()
		{
			return 1234.5;
		}

		public string dummyString()
		{
			return "";
		}

		public string realString()
		{
			return "12345";
		}

		public UInt64 dummyUInt64()
		{
			return 0;
		}

		public UInt64 realUInt64()
		{
			return 123456789012345;
		}

		public IComparable dummyInterface()
		{
			return null;
		}

		public IComparable realInterface()
		{
			return new Version();
		}
	}
}
