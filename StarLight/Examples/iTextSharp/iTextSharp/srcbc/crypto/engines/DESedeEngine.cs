using System;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;

namespace org.bouncycastle.crypto.engines
{
	/**
	* a class that provides a basic DESede (or Triple DES) engine.
	*/
	public class DESedeEngine : DESEngine
	{
		new protected static readonly int  BLOCK_SIZE = 8;

		private int[]               workingKey1 = null;
		private int[]               workingKey2 = null;
		private int[]               workingKey3 = null;

		private bool             forEncryption;

		/**
		* standard constructor.
		*/
		public DESedeEngine()
		{
		}

		/**
		* initialise a DESede cipher.
		*
		* @param forEncryption whether or not we are for encryption.
		* @param params the parameters required to set up the cipher.
		* @exception IllegalArgumentException if the params argument is
		* inappropriate.
		*/
		public override void init(
			bool           encrypting,
			CipherParameters  parameters)
		{
			if (!(typeof(KeyParameter).IsInstanceOfType(parameters)))
			{
				throw new ArgumentException("invalid parameter passed to DESede init - " + parameters.GetType().ToString());
			}

			byte[]      keyMaster = ((KeyParameter)parameters).getKey();
			byte[]      key1 = new byte[8], key2 = new byte[8], key3 = new byte[8];

			this.forEncryption = encrypting;

			if (keyMaster.Length == 24)
			{
				Array.Copy(keyMaster, 0, key1, 0, key1.Length);
				Array.Copy(keyMaster, 8, key2, 0, key2.Length);
				Array.Copy(keyMaster, 16, key3, 0, key3.Length);

				workingKey1 = generateWorkingKey(encrypting, key1);
				workingKey2 = generateWorkingKey(!encrypting, key2);
				workingKey3 = generateWorkingKey(encrypting, key3);
			}
			else        // 16 byte key
			{
				Array.Copy(keyMaster, 0, key1, 0, key1.Length);
				Array.Copy(keyMaster, 8, key2, 0, key2.Length);

				workingKey1 = generateWorkingKey(encrypting, key1);
				workingKey2 = generateWorkingKey(!encrypting, key2);
				workingKey3 = workingKey1;
			}
		}

		public override String getAlgorithmName()
		{
			return "DESede";
		}

		public override int getBlockSize()
		{
			return BLOCK_SIZE;
		}

		public override int processBlock(
			byte[] inBytes,
			int inOff,
			byte[] outBytes,
			int outOff)
		{
			if (workingKey1 == null)
			{
				throw new Exception("DESede engine not initialised");
			}

			if ((inOff + BLOCK_SIZE) > inBytes.Length)
			{
				throw new DataLengthException("input buffer too short");
			}

			if ((outOff + BLOCK_SIZE) > outBytes.Length)
			{
				throw new DataLengthException("output buffer too short");
			}

			if (forEncryption)
			{
				desFunc(workingKey1, inBytes, inOff, outBytes, outOff);
				desFunc(workingKey2, outBytes, outOff, outBytes, outOff);
				desFunc(workingKey3, outBytes, outOff, outBytes, outOff);
			}
			else
			{
				desFunc(workingKey3, inBytes, inOff, outBytes, outOff);
				desFunc(workingKey2, outBytes, outOff, outBytes, outOff);
				desFunc(workingKey1, outBytes, outOff, outBytes, outOff);
			}

			return BLOCK_SIZE;
		}

		public override void reset()
		{
		}
	}

}