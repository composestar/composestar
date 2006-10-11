using System;

namespace org.bouncycastle.crypto.digests
{
    
    /**
     * implementation of SHA-1 as outlined in "Handbook of Applied Cryptography", pages 346 - 349.
     *
     * It is interesting to ponder why the, apart from the extra IV, the other difference here from MD5
     * is the "endienness" of the word processing!
     */
    public class SHA1Digest : GeneralDigest
    {
        private static readonly int    DIGEST_LENGTH = 20;
    
        private int     H1, H2, H3, H4, H5;
    
        private int[]   X = new int[80];
        private int     xOff;
    
        public SHA1Digest()
        {
            reset();
        }
    
        /**
         * Copy constructor.  This will copy the state of the provided
         * message digest.
         */
        public SHA1Digest(SHA1Digest t) : base(t)
        {
            H1 = t.H1;
            H2 = t.H2;
            H3 = t.H3;
            H4 = t.H4;
            H5 = t.H5;
    
            Array.Copy(t.X, 0, X, 0, t.X.Length);
            xOff = t.xOff;
        }
    
        public override String getAlgorithmName()
        {
            return "SHA-1";
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
    
            if (xOff == 16) processBlock();
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
    
        protected override void processLength(long    bitLength)
        {
            if (xOff > 14) processBlock();
    
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
    
            reset();
    
            return DIGEST_LENGTH;
        }
    
        /**
         * reset the chaining variables
         */
        public override void reset()
        {
            base.reset();
    
            H1 = unchecked( (int) 0x67452301 );
            H2 = unchecked( (int) 0xefcdab89 );
            H3 = unchecked( (int) 0x98badcfe );
            H4 = unchecked( (int) 0x10325476 );
            H5 = unchecked( (int) 0xc3d2e1f0 );

            xOff = 0;
            for (int i = 0; i != X.Length; i++) X[i] = 0;
        }
    
        //
        // Additive constants
        //
        private static readonly int    Y1 = unchecked( (int) 0x5a827999);
        private static readonly int    Y2 = unchecked( (int) 0x6ed9eba1);
        private static readonly int    Y3 = unchecked( (int) 0x8f1bbcdc);
        private static readonly int    Y4 = unchecked( (int) 0xca62c1d6);

        private int f(int    u, int    v, int    w)
        {
            return ((u & v) | ((~u) & w));
        }
    
        private int h(int    u, int    v, int    w)
        {
            return (u ^ v ^ w);
        }
    
        private int g(int    u, int    v, int    w)
        {
            return ((u & v) | (u & w) | (v & w));
        }
    
        private int rotateLeft(int    x, int    n)
        {
            return (x << n) | (int) ((uint) x >> (32 - n));
        }
    
        protected override void processBlock()
        {
            //
            // expand 16 word block into 80 word block.
            //
            for (int i = 16; i <= 79; i++)
            {
                X[i] = rotateLeft((X[i - 3] ^ X[i - 8] ^ X[i - 14] ^ X[i - 16]), 1);
            }
    
            //
            // set up working variables.
            //
            int     A = H1;
            int     B = H2;
            int     C = H3;
            int     D = H4;
            int     E = H5;
    
            //
            // round 1
            //
            for (int j = 0; j <= 19; j++)
            {
                int     t = rotateLeft(A, 5) + f(B, C, D) + E + X[j] + Y1;
    
                E = D;
                D = C;
                C = rotateLeft(B, 30);
                B = A;
                A = t;
            }
    
            //
            // round 2
            //
            for (int j = 20; j <= 39; j++)
            {
                int     t = rotateLeft(A, 5) + h(B, C, D) + E + X[j] + Y2;
    
                E = D;
                D = C;
                C = rotateLeft(B, 30);
                B = A;
                A = t;
            }
    
            //
            // round 3
            //
            for (int j = 40; j <= 59; j++)
            {
                int     t = rotateLeft(A, 5) + g(B, C, D) + E + X[j] + Y3;
    
                E = D;
                D = C;
                C = rotateLeft(B, 30);
                B = A;
                A = t;
            }
    
            //
            // round 4
            //
            for (int j = 60; j <= 79; j++)
            {
                int     t = rotateLeft(A, 5) + h(B, C, D) + E + X[j] + Y4;
    
                E = D;
                D = C;
                C = rotateLeft(B, 30);
                B = A;
                A = t;
            }
    
            H1 += A;
            H2 += B;
            H3 += C;
            H4 += D;
            H5 += E;
    
            //
            // reset the offset and clean out the word buffer.
            //
            xOff = 0;
            for (int i = 0; i != X.Length; i++)
            {
                X[i] = 0;
            }
        }
    }
}
