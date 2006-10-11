using System;
using org.bouncycastle.security;
using org.bouncycastle.math;
using org.bouncycastle.crypto;


namespace org.bouncycastle.crypto.parameters
{
	public class ElGamalKeyGenerationParameters : KeyGenerationParameters
	{
		private ElGamalParameters    parameters;

		public ElGamalKeyGenerationParameters(
			SecureRandom        random,
			ElGamalParameters   parameters) : base(random, parameters.getP().bitLength() - 1)
		{
			this.parameters = parameters;
		}

		public ElGamalParameters getParameters()
		{
			return parameters;
		}
	}

}