using System;
using org.bouncycastle.crypto;
using org.bouncycastle.math;
using org.bouncycastle.crypto.parameters;

namespace org.bouncycastle.crypto.modes
{
	/**
	* Implements the Segmented Integer Counter (SIC) mode on top of a simple
	* block cipher.
	*/
	public class SICBlockCipher : BlockCipher
	{
	private BlockCipher     cipher = null;
	private int             blockSize;
	private byte[]          IV;
	private byte[]          counter;
	private byte[]          counterOut;
	private bool         encrypting;

	private readonly BigInteger ONE = BigInteger.valueOf(1);


	/**
	* Basic constructor.
	*
	* @param c the block cipher to be used.
	*/
	public SICBlockCipher(BlockCipher c) {
		this.cipher = c;
		this.blockSize = cipher.getBlockSize();
		this.IV = new byte[blockSize];
		this.counter = new byte[blockSize];
		this.counterOut = new byte[blockSize];
	}


	/**
	* return the underlying block cipher that we are wrapping.
	*
	* @return the underlying block cipher that we are wrapping.
	*/
	public BlockCipher getUnderlyingCipher() {
		return cipher;
	}


	public void init(bool forEncryption, CipherParameters parameters)
		//throws IllegalArgumentException 
	{
		this.encrypting = forEncryption;

		if (typeof(ParametersWithIV).IsInstanceOfType(parameters))
		{
			ParametersWithIV ivParam = (ParametersWithIV)parameters;
			byte[]           iv      = ivParam.getIV();
			Array.Copy(iv, 0, IV, 0, IV.Length);

			reset();
			cipher.init(true, ivParam.getParameters());
		}
	}


	public String getAlgorithmName() {
		return cipher.getAlgorithmName() + "/SIC";
	}


	public int getBlockSize() {
		return cipher.getBlockSize();
	}


	public int processBlock(byte[] inBytes, int inOff, byte[] outBytes, int outOff)
		//throws DataLengthException, IllegalStateException 
	{
		cipher.processBlock(counter, 0, counterOut, 0);

		//
		// XOR the counterOut with the plaintext producing the cipher text
		//
		for (int i = 0; i < counterOut.Length; i++) {
		outBytes[outOff + i] = (byte)(counterOut[i] ^ inBytes[inOff + i]);
		}

		BigInteger bi = new BigInteger(counter);
		bi.add(ONE);
		counter = bi.toByteArray();

		return counter.Length;
	}


	public void reset() {
		Array.Copy(IV, 0, counter, 0, counter.Length);
		cipher.reset();
	}
	}

}