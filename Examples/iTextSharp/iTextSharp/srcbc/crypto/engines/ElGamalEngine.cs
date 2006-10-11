using System;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;
using org.bouncycastle.math;
using org.bouncycastle.security;

namespace org.bouncycastle.crypto.engines
{
	/**
	* this does your basic ElGamal algorithm.
	*/
	public class ElGamalEngine: AsymmetricBlockCipher
	{
		private ElGamalKeyParameters    key;
		private SecureRandom            random;
		private bool                 forEncryption;

		private static BigInteger       ZERO = BigInteger.valueOf(0);
		private static BigInteger       ONE = BigInteger.valueOf(1);
		private static BigInteger       TWO = BigInteger.valueOf(2);

        public String getAlgorithmName()
        {
            return "ElGamal";
        }
        
        /**
		* initialise the ElGamal engine.
		*
		* @param forEncryption true if we are encrypting, false otherwise.
		* @param param the necessary ElGamal key parameters.
		*/
		public void init(
			bool             forEncryption,
			CipherParameters    param)
		{
			if (typeof(ParametersWithRandom).IsInstanceOfType(param))
			{
				ParametersWithRandom    p = (ParametersWithRandom)param;

				this.key = (ElGamalKeyParameters)p.getParameters();
				this.random = p.getRandom();
			}
			else
			{
				this.key = (ElGamalKeyParameters)param;
				this.random = new SecureRandom();
			}

			this.forEncryption = forEncryption;
		}

		/**
		* Return the maximum size for an input block to this engine.
		* For ElGamal this is always one byte less than the size of P on
		* encryption, and twice the length as the size of P on decryption.
		*
		* @return maximum size for an input block.
		*/
		public int getInputBlockSize()
		{
			int     bitSize = key.getParameters().getP().bitLength();

			if (forEncryption)
			{
				if ((bitSize % 8) == 0)
				{
					return bitSize / 8 - 1;
				}

				return bitSize / 8;
			}
			else
			{
				return 2 * (((bitSize - 1) + 7) / 8);
			}
		}

		/**
		* Return the maximum size for an output block to this engine.
		* For ElGamal this is always one byte less than the size of P on
		* decryption, and twice the length as the size of P on encryption.
		*
		* @return maximum size for an output block.
		*/
		public int getOutputBlockSize()
		{
			int     bitSize = key.getParameters().getP().bitLength();

			if (forEncryption)
			{
				return 2 * (((bitSize - 1) + 7) / 8);
			}
			else
			{
				return (bitSize - 7) / 8;
			}
		}

		/**
		* Process a single block using the basic ElGamal algorithm.
		*
		* @param in the input array.
		* @param inOff the offset into the input buffer where the data starts.
		* @param inLen the length of the data to be processed.
		* @return the result of the ElGamal process.
		* @exception DataLengthException the input block is too large.
		*/
		public byte[] processBlock(
			byte[]  inBytes,
			int     inOff,
			int     inLen)
		{
			if (inLen > (getInputBlockSize() + 1))
			{
				throw new DataLengthException("input too large for ElGamal cipher.\n");
			}
			else if (inLen == (getInputBlockSize() + 1) && (inBytes[inOff] & 0x80) != 0)
			{
				throw new DataLengthException("input too large for ElGamal cipher.\n");
			}

			byte[]  block;

			if (inOff != 0 || inLen != inBytes.Length)
			{
				block = new byte[inLen];

				Array.Copy(inBytes, inOff, block, 0, inLen);
			}
			else
			{
				block = inBytes;
			}

			BigInteger  input = new BigInteger(1, block);
			BigInteger  g = key.getParameters().getG();
			BigInteger  p = key.getParameters().getP();

			if (typeof(ElGamalPrivateKeyParameters).IsInstanceOfType(key))
			{
				byte[]  in1 = new byte[inBytes.Length / 2];
				byte[]  in2 = new byte[inBytes.Length / 2];

				Array.Copy(inBytes, 0, in1, 0, in1.Length);
				Array.Copy(inBytes, in1.Length, in2, 0, in2.Length);

				BigInteger  gamma = new BigInteger(1, in1);
				BigInteger  phi = new BigInteger(1, in2);

				ElGamalPrivateKeyParameters  priv = (ElGamalPrivateKeyParameters)key;

				BigInteger  m = gamma.modPow(p.subtract(ONE).subtract(priv.getX()), p).multiply(phi).mod(p);

				byte[]      outBytes = m.toByteArray();

				if (outBytes[0] != 0)
				{
					return outBytes;
				}
				else
				{
					byte[]  output = new byte[outBytes.Length - 1];
					Array.Copy(outBytes, 1, output, 0, output.Length);

					return output;
				}
			}
			else
			{
				ElGamalPublicKeyParameters  pub = (ElGamalPublicKeyParameters)key;

				BigInteger  k = new BigInteger(p.bitLength(), random);

				while (k.Equals(ZERO) || (k.compareTo(p.subtract(TWO)) > 0))
				{
					k = new BigInteger(p.bitLength(), random);
				}

				BigInteger  gamma = g.modPow(k, p);
				BigInteger  phi = input.multiply(pub.getY().modPow(k, p)).mod(p);

				byte[]  out1 = gamma.toByteArray();
				byte[]  out2 = phi.toByteArray();
				byte[]  output = new byte[this.getOutputBlockSize()];

				if (out1[0] == 0)
				{
					Array.Copy(out1, 1, output, output.Length / 2 - (out1.Length - 1), out1.Length - 1);
				}
				else
				{
					Array.Copy(out1, 0, output, output.Length / 2 - out1.Length, out1.Length);
				}

				if (out2[0] == 0)
				{
					Array.Copy(out2, 1, output, output.Length - (out2.Length - 1), out2.Length - 1);
				}
				else
				{
					Array.Copy(out2, 0, output, output.Length - out2.Length, out2.Length);
				}

				return output;
			}
		}
	}

}