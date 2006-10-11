#region Using directives

using System;
using System.Collections;
using System.Text;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.signers;
using org.bouncycastle.crypto.parameters;
using System.IO;
using org.bouncycastle.asn1;
using org.bouncycastle.math;

#endregion

namespace org.bouncycastle.crypto.signers
{
    public class DSADigestSigner :Signer
    {
        private Digest digest = null;
        private DSA    dsaSigner = null;
        private bool   forSigning;

        public DSADigestSigner(DSA signer, Digest digest)
        {
            this.digest = digest;
            this.dsaSigner = signer;
        }

        public String getAlgorithmName()
        {
            return digest.getAlgorithmName() + "withDSA";
        }

        public void init(bool forSigning, CipherParameters param)
        {
            this.forSigning = forSigning;

            AsymmetricKeyParameter    k;

            if (param is ParametersWithRandom)
            {
                k = (AsymmetricKeyParameter)((ParametersWithRandom)param).getParameters();
            }
            else
            {
                k = (AsymmetricKeyParameter)param;
            }

            if (forSigning && !k.isPrivate())
            {
                throw (new Exception("Signing Requires Private Key."));
            }

            if (!forSigning && k.isPrivate())
            {
                throw (new Exception("Verification Requires Public Key."));
            }

            reset();

            dsaSigner.init(forSigning, param);
        }

        /**
         * update the internal digest with the byte b
         */
        public void update(byte b)
        {
            digest.update(b);
        }

        /**
         * update the internal digest with the byte array in
         */
        public void update(byte[] input, int off, int length)
        {
            digest.update(input, off, length);
        }

        /**
         * generate a signature for the message we've been loaded with using
         * the key we were initialised with.
         */
        public byte[] generateSignature()
        {
            if (forSigning)
            {

                MemoryStream bOut = new MemoryStream();
                DEROutputStream dOut = new DEROutputStream(bOut);
                ASN1EncodableVector v = new ASN1EncodableVector();

                byte[] dig = new byte[digest.getDigestSize()];
                digest.doFinal(dig, 0);

                BigInteger[] sig = dsaSigner.generateSignature(dig);

                v.add(new DERInteger(sig[0]));
                v.add(new DERInteger(sig[1]));

                dOut.writeObject(new DERSequence(v));
                dOut.Flush();

                byte[] output = bOut.ToArray();
                dOut.Close();
                return output;
            }
            throw new Exception("DSADigestSigner initialised for verification..");
        }
        
        /**
         * return true if the internal state represents the signature described
         * in the passed in array.
         */
        public bool verifySignature(byte[] signature)
        {
            byte[] hash = new byte[digest.getDigestSize()];
            digest.doFinal(hash, 0);

            BigInteger[] sig;

            try
            {
                sig = derDecode(signature);
            } catch 
            {
                throw new Exception("Error decoding signature bytes.");
            }
            return dsaSigner.verifySignature(hash, sig[0], sig[1]);
        }

        /**
         * reset the internal state
         */
        public void reset()
        {
            digest.reset();
        }

        private byte[] derEncode(
            BigInteger  r,
            BigInteger  s)
        {
            MemoryStream   bOut = new MemoryStream();
            DEROutputStream         dOut = new DEROutputStream(bOut);
            ASN1EncodableVector     v = new ASN1EncodableVector();
    
            v.add(new DERInteger(r));
            v.add(new DERInteger(s));
    
            dOut.writeObject(new DERSequence(v));
    
            return bOut.ToArray();
        }
    
        private BigInteger[] derDecode(byte[]  encoding)
        {
            MemoryStream    bIn = new MemoryStream(encoding);
            ASN1InputStream          dIn = new ASN1InputStream(bIn);
            ASN1Sequence            s = (ASN1Sequence)dIn.readObject();
    
            BigInteger[]            sig = new BigInteger[2];
    
            sig[0] = ((DERInteger)s.getObjectAt(0)).getValue();
            sig[1] = ((DERInteger)s.getObjectAt(1)).getValue();
    
            return sig;
        }
    }
}
