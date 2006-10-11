using System;

namespace org.bouncycastle.crypto
{
	/**
	 * interface that a public/private key pair generator should conform to.
	 */
	public interface AsymmetricCipherKeyPairGenerator
	{
		/**
		 * intialise the key pair generator.
		 *
		 * @param the parameters the key pair is to be initialised with.
		 */
		void init(KeyGenerationParameters param);

		/**
		 * return an AsymmetricCipherKeyPair containing the generated keys.
		 *
		 * @return an AsymmetricCipherKeyPair containing the generated keys.
		 */
		AsymmetricCipherKeyPair generateKeyPair();
	}
}