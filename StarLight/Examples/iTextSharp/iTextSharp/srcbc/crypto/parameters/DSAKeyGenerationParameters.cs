using System;
using org.bouncycastle.security;
using org.bouncycastle.math;
using org.bouncycastle.crypto;


namespace org.bouncycastle.crypto.parameters
{
	public class DSAKeyGenerationParameters: KeyGenerationParameters
	{
		private DSAParameters    parameters;

		public DSAKeyGenerationParameters(
			SecureRandom    random,
			DSAParameters   parameters): base(random, parameters.getP().bitLength() - 1)
		{
			this.parameters = parameters;
		}

		public DSAParameters getParameters()
		{
			return parameters;
		}
	}

}