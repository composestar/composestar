using System;
using org.bouncycastle.crypto.digests;

namespace org.bouncycastle.security
{

    /**
     * An implementation of SecureRandom specifically for the
     * light-weight API, JDK 1.0, and the J2ME. Random generation is 
     * based on the traditional SHA1 with counter. Calling setSeed
     * will always increase the entropy of the hash.
     */
    public class SecureRandom : System.Random
    {
        private static  SecureRandom rand = new SecureRandom();
        private long        counter = 1;
        private SHA1Digest  digest = new SHA1Digest();
        private byte[]      state;

        public SecureRandom() : base(0)
        {
            state = new byte[digest.getDigestSize()];
            setSeed(DateTime.Now.Ticks);
        }

        public SecureRandom(byte[] inSeed)
        {
            state = new byte[digest.getDigestSize()];
            setSeed(inSeed);
        }


        public static SecureRandom getInstance(String algorithm)
        {
            return new SecureRandom();
        }

        public static SecureRandom getInstance(String algorithm, String provider)
        {
            return new SecureRandom();
        }

        public virtual void setSeed(byte[] inSeed)
        {
            digest.update(inSeed, 0, inSeed.Length);
        }

        public virtual void setSeed(long rSeed)
        {
            if (rSeed != 0) setSeed(longToBytes(rSeed));
        }

        public static byte[] getSeed(int numBytes)
        {
            byte[] rv = new byte[numBytes];

            rand.setSeed(DateTime.Now.Ticks);
            rand.nextBytes(rv);
            return rv;
        }

        // public instance methods
        public virtual byte[] generateSeed(int numBytes)
        {
            byte[] rv = new byte[numBytes];
            nextBytes(rv);    
            return rv;
        }


        // public methods overriding random
        public virtual void nextBytes(byte[] bytes)
        {
            int     stateOff = 0;

            digest.doFinal(state, 0);

            for (int i = 0; i != bytes.Length; i++)
            {
                if (stateOff == state.Length)
                {
                    byte[]  b = longToBytes(counter++);

                    digest.update(b, 0, b.Length);
                    digest.update(state, 0, state.Length);
                    digest.doFinal(state, 0);
                    stateOff = 0;
                }
                bytes[i] = state[stateOff++];
            }

            byte[]  bb = longToBytes(counter++);

            digest.update(bb, 0, bb.Length);
            digest.update(state, 0, state.Length);
        }


        private byte[]  intBytes = new byte[4];

        public virtual int nextInt()
        {
            nextBytes(intBytes);

            int result = 0;

            for (int i = 0; i < 4; i++)
            {
                result = (result << 8) + (intBytes[i] & 0xff);
            }

            return result;
        }

        protected int next(int numBits)
        {
            int     size = (numBits + 7) / 8;
            byte[]  bytes = new byte[size];

            nextBytes(bytes);

            int result = 0;

            for (int i = 0; i < size; i++)
            {
                result = (result << 8) + (bytes[i] & 0xff);
            }

            return result & ((1 << numBits) - 1);
        }

        private byte[]  longBytes = new byte[8];

        private byte[] longToBytes(long    val)
        {
            for (int i = 0; i != 8; i++)
            {
                longBytes[i] = (byte)val;
                val =  (long) ((ulong) val >> 8);
            }
            return longBytes;
        }
    }
}
