using System;

namespace org.bouncycastle.crypto.parameters
{
	public class DHValidationParameters
	{
		private byte[]  seed;
		private int     counter;

		public DHValidationParameters(
			byte[]  seed,
			int     counter)
		{
			this.seed = seed;
			this.counter = counter;
		}

		public override bool Equals(
			Object o)
		{
            if (o is DHValidationParameters)
            {
                return (((DHValidationParameters)o).getCounter() == counter && ((DHValidationParameters)o).getSeed().Equals(seed));
            }

            return base.Equals(o);
        }
		public override int GetHashCode()
		{
			return base.GetHashCode();
		}

        public byte[] getSeed()
        {
            return seed;
        }

        public int getCounter()
        {
            return counter;
        }

    }

}