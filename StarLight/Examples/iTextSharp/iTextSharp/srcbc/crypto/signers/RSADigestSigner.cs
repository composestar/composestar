#region Using directives

using System;
using System.IO;
using System.Collections;
using System.Text;

using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;
using org.bouncycastle.crypto.engines;
using org.bouncycastle.crypto.signers;
using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;
using org.bouncycastle.crypto.encodings;
using org.bouncycastle.security;

using org.bouncycastle.asn1.util;
using org.bouncycastle.asn1.pkcs;
using org.bouncycastle.asn1.nist;
using org.bouncycastle.asn1.teletrust;

#endregion

namespace org.bouncycastle.crypto.signers
{
    public class RSADigestSigner : Signer
    {
        private Digest digest = null;
        private bool forSigning;
        private AsymmetricBlockCipher rsaEngine = new PKCS1Encoding(new RSAEngine());
        private AlgorithmIdentifier algId;
        private static Hashtable oidMap = new Hashtable();

        /// <summary>
        /// Load oid table.
        /// </summary>
        static RSADigestSigner()
        {
            oidMap["RIPEMD128"] = TeleTrusTObjectIdentifiers.ripemd128;
            oidMap["RIPEMD160"] = TeleTrusTObjectIdentifiers.ripemd160;
            oidMap["RIPEMD256"] = TeleTrusTObjectIdentifiers.ripemd256;

            oidMap["SHA-1"] = X509ObjectIdentifiers.id_SHA1;
            oidMap["SHA-224"] = NISTObjectIdentifiers.id_sha224;
            oidMap["SHA-256"] = NISTObjectIdentifiers.id_sha256;
            oidMap["SHA-384"] = NISTObjectIdentifiers.id_sha384;
            oidMap["SHA-512"] = NISTObjectIdentifiers.id_sha512;

            oidMap["MD2"] = PKCSObjectIdentifiers.md2;
            oidMap["MD4"] = PKCSObjectIdentifiers.md4;
            oidMap["MD5"] = PKCSObjectIdentifiers.md5;
        }


        public RSADigestSigner(Digest digest)
        {
            this.digest = digest;

            algId = new AlgorithmIdentifier( (DERObjectIdentifier)oidMap[digest.getAlgorithmName()] , new DERNull());
        }

        public String getAlgorithmName()
        {
            return digest.getAlgorithmName() + "withRSA";
        }

        /**
         * Initialise the signer for signing or verification.
         * 
         * @param forSigning true if for signing, false otherwise
         * @param param necessary parameters.
         */
        public void init(bool forSigning, CipherParameters param)
        {
            this.forSigning = forSigning;
            AsymmetricKeyParameter k;

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

            rsaEngine.init(forSigning, param);
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
            if (!forSigning)
            {
                throw (new Exception("RSADigestSignature not initialised for signature generation."));
            }

            byte[] hash = new byte[digest.getDigestSize()];

            digest.doFinal(hash, 0);

            byte[] data = derEncode(hash);

           
            return rsaEngine.processBlock(data, 0, data.Length);
        }

        /**
         * return true if the internal state represents the signature described
         * in the passed in array.
         */
        public bool verifySignature(byte[] signature)
        {
            if (forSigning)
            {
                throw (new Exception("RSADigestSignature not initialised for verification"));
            }

            byte[] hash = new byte[digest.getDigestSize()];

            digest.doFinal(hash, 0);

            DigestInfo  digInfo;
            byte[]      sig;

            sig = rsaEngine.processBlock(signature, 0, signature.Length);
            digInfo = derDecode(sig);

            if (!digInfo.getAlgorithmId().getObjectId().Equals(algId.getObjectId()))
            {
              return false;
            }
        

            if (!isNull(digInfo.getAlgorithmId().getParameters()))
            {
                return false;
            }

            byte[]  sigHash = digInfo.getDigest();

            if (hash.Length != sigHash.Length)
            {
                return false;
            }

            for (int i = 0; i < hash.Length; i++)
            {
                if (sigHash[i] != hash[i])
                {
                    return false;
                }
            }

            return true;
        }

        public void reset()
        {
            digest.reset();
        }

        private bool isNull(
            ASN1Encodable    o)
        {
            return (o is ASN1Null) || (o == null);
        }

        private byte[] derEncode(
            byte[]  hash)
        {

            
            MemoryStream   bOut = new MemoryStream();
            DEROutputStream         dOut = new DEROutputStream(bOut);
            DigestInfo              dInfo = new DigestInfo(algId, hash);
    
            dOut.writeObject(dInfo);
            dOut.Flush();

            return bOut.ToArray();
        }
    
        private DigestInfo derDecode(byte[]  encoding)
        {
            if ((int)encoding[0] != (  (int)ASN1Tags.CONSTRUCTED | (int)ASN1Tags.SEQUENCE) )
            {
                throw new IOException("not a digest info object");
            }
            
            MemoryStream    bIn = new MemoryStream(encoding);
            ASN1InputStream aIn = new ASN1InputStream(bIn);



            return new DigestInfo( (ASN1Sequence)aIn.readObject());
        }
    }
}
