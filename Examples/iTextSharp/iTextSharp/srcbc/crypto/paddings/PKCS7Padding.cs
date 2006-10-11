using System;
using org.bouncycastle.crypto;
using org.bouncycastle.security;


namespace org.bouncycastle.crypto.paddings
{
    /**
    * A padder that adds PKCS7/PKCS5 padding to a block.
    */
    public class PKCS7Padding: BlockCipherPadding
    {
        /**
        * Initialise the padder.
        *
        * @param random - a SecureRandom if available.
        */
        public void init(SecureRandom random)
            //throws IllegalArgumentException
        {
            // nothing to do.
        }

        /**
        * Return the name of the algorithm the cipher implements.
        *
        * @return the name of the algorithm the cipher implements.
        */
        public String getPaddingName()
        {
            return "PKCS7";
        }

        /**
        * add the pad bytes to the passed in block, returning the
        * number of bytes added.
        */
        public int addPadding(
            byte[]  inBytes,
            int     inOff)
        {
            byte code = (byte)(inBytes.Length - inOff);

            while (inOff < inBytes.Length)
            {
                inBytes[inOff] = code;
                inOff++;
            }

            return code;
        }

        /**
        * return the number of pad bytes present in the block.
        */
        public int padCount(byte[] inBytes)
            //throws InvalidCipherTextException
        {
            int count = inBytes[inBytes.Length - 1] & 0xff;

            if (count > inBytes.Length)
            {
                throw new InvalidCipherTextException("pad block corrupted");
            }

            for (int i = 1; i <= count; i++)
            {
                if (inBytes[inBytes.Length - i] != count)
                {
                    throw new InvalidCipherTextException("pad block corrupted");
                }
            }

            return count;
        }
    }

}
