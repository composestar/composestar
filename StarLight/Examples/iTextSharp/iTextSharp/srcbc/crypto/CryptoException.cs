using System;

namespace org.bouncycastle.crypto
{
	/**
	 * the foundation class for the hard exceptions thrown by the crypto packages.
	 */
	public class CryptoException : Exception
	{
		/**
			* base constructor.
			*/
		public CryptoException()
		{
		}

		/**
		 * create a CryptoException with the given message.
		 *
		 * @param message the message to be carried with the exception.
		 */
		public CryptoException(
			String  message) : base(message)
		{
			
		}
	}

}