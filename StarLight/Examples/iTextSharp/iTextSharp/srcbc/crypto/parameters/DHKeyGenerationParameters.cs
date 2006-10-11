using System;
using org.bouncycastle.security;
using org.bouncycastle.math;
using org.bouncycastle.crypto;


namespace org.bouncycastle.crypto.parameters
{
	public class DHKeyGenerationParameters : KeyGenerationParameters
	{
		private DHParameters    parameters;

		public DHKeyGenerationParameters(
			SecureRandom    random,
			DHParameters    parameters) : base(random, parameters.getP().bitLength() - 1)
		{
			this.parameters = parameters;
		}

		public DHParameters getParameters()
		{
			return parameters;
		}
	}

}