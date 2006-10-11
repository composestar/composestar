using System;

namespace org.bouncycastle.crypto.parameters
{
	public class DSAValidationParameters
	{
		private byte[]  seed;
		private int     counter;

		public DSAValidationParameters(
			byte[]  seed,
			int     counter)
		{
			this.seed = seed;
			this.counter = counter;
		}

		public int getCounter()
		{
			return counter;
		}

		public byte[] getSeed()
		{
			return seed;
		}

		public override bool Equals(
			Object o)
		{
			if ((o == null) || !(typeof(DSAValidationParameters).IsInstanceOfType(o)))
			{
				return false;
			}
			DSAValidationParameters  other = (DSAValidationParameters)o;

			if (other.counter != this.counter)
			{
				return false;
			}

			if (other.seed.Length != this.seed.Length)
			{
				return false;
			}

			for (int i = 0; i != other.seed.Length; i++)
			{
				if (other.seed[i] != this.seed[i])
				{
					return false;
				}
			}

			return true;
		}

		public override int GetHashCode()
		{
			return base.GetHashCode();
		}

	}
}