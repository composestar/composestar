using System;

namespace org.bouncycastle.crypto.digests
{
    /**
     * SHA-224 as described in RFC 3874
     * <pre>
     *         block  word  digest
     * SHA-1   512    32    160
     * SHA-224 512    32    224
     * SHA-256 512    32    256
     * SHA-384 1024   64    384
     * SHA-512 1024   64    512
     * </pre>
     */
    public class SHA224Digest
        : GeneralDigest
    {
        private const int    DIGEST_LENGTH = 28;
    
        private int     H1, H2, H3, H4, H5, H6, H7, H8;
    
        private int[]   X = new int[64];
        private int     xOff;
    
        /**
         * Standard constructor
         */
        public SHA224Digest()
        {
            reset();
        }
    
        /**
         * Copy constructor.  This will copy the state of the provided
         * message digest.
         */
         public SHA224Digest(SHA224Digest t) : base(t)
        {
            H1 = t.H1;
            H2 = t.H2;
            H3 = t.H3;
            H4 = t.H4;
            H5 = t.H5;
            H6 = t.H6;
            H7 = t.H7;
            H8 = t.H8;
    
            Array.Copy(t.X, 0, X, 0, t.X.Length);
            xOff = t.xOff;
        }
    
        public override string getAlgorithmName()
        {
            return "SHA-224";
        }
    
        public override int getDigestSize()
        {
            return DIGEST_LENGTH;
        }
    
        protected override void processWord(
            byte[]  inBytes,
            int     inOff)
        {
            X[xOff++] = ((inBytes[inOff] & 0xff) << 24) | ((inBytes[inOff + 1] & 0xff) << 16)
                        | ((inBytes[inOff + 2] & 0xff) << 8) | ((inBytes[inOff + 3] & 0xff)); 
    
            if (xOff == 16)
            {
                processBlock();
            }
        }
    
        private void unpackWord(
            int     word,
            byte[]  outBytes,
            int     outOff)
        {
            outBytes[outOff]     = (byte)((uint) word >> 24);
            outBytes[outOff + 1] = (byte)((uint) word >> 16);
            outBytes[outOff + 2] = (byte)((uint) word >> 8);
            outBytes[outOff + 3] = (byte)word;
        }
    
        protected override void processLength(
            long    bitLength)
        {
            if (xOff > 14)
            {
                processBlock();
            }
    
            X[14] = (int)((ulong) bitLength >> 32);
            X[15] = (int)(bitLength & 0xffffffff);
        }
    
        public override int doFinal(
            byte[]  outBytes,
            int     outOff)
        {
            finish();
    
            unpackWord(H1, outBytes, outOff);
            unpackWord(H2, outBytes, outOff + 4);
            unpackWord(H3, outBytes, outOff + 8);
            unpackWord(H4, outBytes, outOff + 12);
            unpackWord(H5, outBytes, outOff + 16);
            unpackWord(H6, outBytes, outOff + 20);
            unpackWord(H7, outBytes, outOff + 24);
    
            reset();
    
            return DIGEST_LENGTH;
        }
    
        /**
         * reset the chaining variables
         */
        public override void reset()
        {
            base.reset();
    
            /* SHA-224 initial hash value
             */

            unchecked
            {
                H1 = (int) 0xc1059ed8;
                H2 = (int) 0x367cd507;
                H3 = (int) 0x3070dd17;
                H4 = (int) 0xf70e5939;
                H5 = (int) 0xffc00b31;
                H6 = (int) 0x68581511;
                H7 = (int) 0x64f98fa7;
                H8 = (int) 0xbefa4fa4;
            }
             
            xOff = 0;
            for (int i = 0; i != X.Length; i++)
            {
                X[i] = 0;
            }
        }
    
        protected override void processBlock()
        {
            //
            // expand 16 word block into 64 word blocks.
            //
            for (int t = 16; t <= 63; t++)
            {
                X[t] = Theta1(X[t - 2]) + X[t - 7] + Theta0(X[t - 15]) + X[t - 16];
            }
    
            //
            // set up working variables.
            //
            int     a = H1;
            int     b = H2;
            int     c = H3;
            int     d = H4;
            int     e = H5;
            int     f = H6;
            int     g = H7;
            int     h = H8;
    
            for (int t = 0; t <= 63; t++)
            {
                int        T1, T2;
    
                T1 = h + Sum1(e) + Ch(e, f, g) + (int)UK[t] + X[t];
                T2 = Sum0(a) + Maj(a, b, c);
                h = g;
                g = f;
                f = e;
                e = d + T1;
                d = c;
                c = b;
                b = a;
                a = T1 + T2;
            }
    
            H1 += a;
            H2 += b;
            H3 += c;
            H4 += d;
            H5 += e;
            H6 += f;
            H7 += g;
            H8 += h;
    
            //
            // reset the offset and clean out the word buffer.
            //
            xOff = 0;
            for (int i = 0; i != X.Length; i++)
            {
                X[i] = 0;
            }
        }
    
        private int rotateRight(
            int    x,
            int    n)
        {
            return (int)((uint) x >> n) | (x << (32 - n));
        }
    
        /* SHA-224 functions */
        private int Ch(
            int    x,
            int    y,
            int    z)
        {
            return ((x & y) ^ ((~x) & z));
        }
    
        private int Maj(
            int    x,
            int    y,
            int    z)
        {
            return ((x & y) ^ (x & z) ^ (y & z));
        }
    
        private int Sum0(
            int    x)
        {
            return rotateRight(x, 2) ^ rotateRight(x, 13) ^ rotateRight(x, 22);
        }
    
        private int Sum1(
            int    x)
        {
            return rotateRight(x, 6) ^ rotateRight(x, 11) ^ rotateRight(x, 25);
        }
    
        private int Theta0(
            int    x)
        {
            return rotateRight(x, 7) ^ rotateRight(x, 18) ^ (int)((uint) x >> 3);
        }
    
        private int Theta1(
            int    x)
        {
            return rotateRight(x, 17) ^ rotateRight(x, 19) ^ (int)((uint) x >> 10);
        }
    
        /* SHA-224 Constants
         * (represent the first 32 bits of the fractional parts of the
         * cube roots of the first sixty-four prime numbers)
         */
//        static int[] K;
        static uint[] UK = {
            0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
            0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
            0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
            0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
            0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
            0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070, 0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
            0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
        };
    }
}
