using System;
using org.bouncycastle.security;
using org.bouncycastle.math;
using org.bouncycastle.crypto;


namespace org.bouncycastle.crypto.parameters
{
	public class ECKeyGenerationParameters: KeyGenerationParameters
	{
		private ECDomainParameters  domainParams;

		public ECKeyGenerationParameters(
			ECDomainParameters		domainParams,
			SecureRandom            random)
		: base(random, domainParams.getN().bitLength())

		{
			this.domainParams = domainParams;
		}

		public ECDomainParameters getDomainParameters()
		{
			return domainParams;
		}
	}

}