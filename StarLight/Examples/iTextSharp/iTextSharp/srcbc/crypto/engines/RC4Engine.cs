using System;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;

namespace org.bouncycastle.crypto.engines
{
	public class RC4Engine : StreamCipher
	{
		private readonly static int STATE_LENGTH = 256;

		/*
		* variables to hold the state of the RC4 engine
		* during encryption and decryption
		*/

		private byte[]      engineState = null;
		private int         x = 0;
		private int         y = 0;
		private byte[]      workingKey = null;

		/**
		* initialise a RC4 cipher.
		*
		* @param forEncryption whether or not we are for encryption.
		* @param params the parameters required to set up the cipher.
		* @exception ArgumentException if the params argument is
		* inappropriate.
		*/
		public void init(
			bool             forEncryption, 
			CipherParameters     parameters
		)
		{
			if (typeof(KeyParameter).IsInstanceOfType(parameters))
			{
				/* 
				* RC4 encryption and decryption is completely
				* symmetrical, so the 'forEncryption' is 
				* irrelevant.
				*/
				workingKey = ((KeyParameter)parameters).getKey();
				setKey(workingKey);

				return;
			}

			throw new ArgumentException("invalid parameter passed to RC4 init - " + parameters.GetType().ToString());
		}

		public String getAlgorithmName()
		{
			return "RC4";
		}

		public byte returnByte(byte inByte)
		{
			x = (x + 1) & 0xff;
			y = (engineState[x] + y) & 0xff;

			// swap
			byte tmp = engineState[x];
			engineState[x] = engineState[y];
			engineState[y] = tmp;

			// xor
			return (byte)(inByte ^ engineState[(engineState[x] + engineState[y]) & 0xff]);
		}

		public void processBytes(
			byte[]     inBytes, 
			int     inOff, 
			int     len, 
			byte[]     outBytes, 
			int     outOff
		)
		{
			if ((inOff + len) > inBytes.Length)
			{
				throw new DataLengthException("input buffer too short");
			}

			if ((outOff + len) > outBytes.Length)
			{
				throw new DataLengthException("output buffer too short");
			}

			for (int i = 0; i < len ; i++)
			{
				x = (x + 1) & 0xff;
				y = (engineState[x] + y) & 0xff;

				// swap
				byte tmp = engineState[x];
				engineState[x] = engineState[y];
				engineState[y] = tmp;

				// xor
				outBytes[i+outOff] = (byte)(inBytes[i + inOff]
						^ engineState[(engineState[x] + engineState[y]) & 0xff]);
			}
		}

		public void reset()
		{
			setKey(workingKey);
		}

		// Private implementation

		private void setKey(byte[] keyBytes)
		{
			workingKey = keyBytes;

			// System.out.println("the key length is ; "+ workingKey.Length);

			x = 0;
			y = 0;

			if (engineState == null)
			{
				engineState = new byte[STATE_LENGTH];
			}

			// reset the state of the engine
			for (int i=0; i < STATE_LENGTH; i++)
			{
				engineState[i] = (byte)i;
			}
	        
			int i1 = 0;
			int i2 = 0;

			for (int i=0; i < STATE_LENGTH; i++)
			{
				i2 = ((keyBytes[i1] & 0xff) + engineState[i] + i2) & 0xff;
				// do the byte-swap inline
				byte tmp = engineState[i];
				engineState[i] = engineState[i2];
				engineState[i2] = tmp;
				i1 = (i1+1) % keyBytes.Length; 
			}
		}
	}

}