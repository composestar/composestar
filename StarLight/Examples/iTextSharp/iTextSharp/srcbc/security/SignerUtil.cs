#region Using directives

using System;
using System.Text;
using System.Collections;

using org.bouncycastle.asn1;
using org.bouncycastle.asn1.pkcs;
using org.bouncycastle.asn1.teletrust;
using org.bouncycastle.asn1.x9;
using org.bouncycastle.security;
using org.bouncycastle.crypto.digests;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.engines;
using org.bouncycastle.crypto.signers;

#endregion

namespace org.bouncycastle.security
{
   
    /// <summary>
    ///  Signer Utility class contains methods that can not be specifically grouped into other classes.
    /// </summary>

    public class SignerUtil
    {
        static Hashtable    algorithms = new Hashtable();
        static Hashtable    oids = new Hashtable();

        static SignerUtil()
        {
            algorithms["MD2WITHRSA"] = "MD2withRSA";
            algorithms["MD2WITHRSAENCRYPTION"] = "MD2withRSA";
            algorithms[PKCSObjectIdentifiers.md2WithRSAEncryption.getId()] = "MD2withRSA";

            algorithms["MD4WITHRSA"] = "MD4withRSA";
            algorithms["MD4WITHRSAENCRYPTION"] = "MD4withRSA";
            algorithms[PKCSObjectIdentifiers.md4WithRSAEncryption.getId()] = "MD4withRSA";

            algorithms["MD5WITHRSA"] = "MD5withRSA";
            algorithms["MD5WITHRSAENCRYPTION"] = "MD5withRSA";
            algorithms[PKCSObjectIdentifiers.md5WithRSAEncryption.getId()] = "MD5withRSA";

            algorithms["SHA1WITHRSA"] = "SHA-1withRSA";
            algorithms["SHA1WITHRSAENCRYPTION"] = "SHA-1withRSA";
            algorithms[PKCSObjectIdentifiers.sha1WithRSAEncryption.getId()] = "SHA-1withRSA";
            algorithms["SHA-1WITHRSA"] = "SHA-1withRSA";

            algorithms["SHA224WITHRSA"] = "SHA-224withRSA";
            algorithms["SHA224WITHRSAENCRYPTION"] = "SHA-224withRSA";
            algorithms[PKCSObjectIdentifiers.sha224WithRSAEncryption.getId()] = "SHA-224withRSA";
            algorithms["SHA-224WITHRSA"] = "SHA-224withRSA";

            algorithms["SHA256WITHRSA"] = "SHA-256withRSA";
            algorithms["SHA256WITHRSAENCRYPTION"] = "SHA-256withRSA";
            algorithms[PKCSObjectIdentifiers.sha256WithRSAEncryption.getId()] = "SHA-256withRSA";
            algorithms["SHA-256WITHRSA"] = "SHA-256withRSA";

            algorithms["SHA384WITHRSA"] = "SHA-384withRSA";
            algorithms["SHA384WITHRSAENCRYPTION"] = "SHA-384withRSA";
            algorithms[PKCSObjectIdentifiers.sha384WithRSAEncryption.getId()] = "SHA-384withRSA";
            algorithms["SHA-384WITHRSA"] = "SHA-384withRSA";

            algorithms["SHA512WITHRSA"] = "SHA-512withRSA";
            algorithms["SHA512WITHRSAENCRYPTION"] = "SHA-512withRSA";
            algorithms[PKCSObjectIdentifiers.sha512WithRSAEncryption.getId()] = "SHA-512withRSA";
            algorithms["SHA-512WITHRSA"] = "SHA-512withRSA";

            algorithms["SHA256WITHRSAANDMGF1"] = "SHA-256withRSAandMGF1";
            algorithms["SHA-256WITHRSAANDMGF1"] = "SHA-256withRSAandMGF1";

            algorithms["SHA384WITHRSAANDMGF1"] = "SHA-384withRSAandMGF1";
            algorithms["SHA-384WITHRSAANDMGF1"] = "SHA-384withRSAandMGF1";

            algorithms["SHA512WITHRSAANDMGF1"] = "SHA-512withRSAandMGF1";
            algorithms["SHA-512WITHRSAANDMGF1"] = "SHA-512withRSAandMGF1";

            algorithms["RIPEMD128WITHRSA"] = "RIPEMD128withRSA";
            algorithms["RIPEMD128WITHRSAENCRYPTION"] = "RIPEMD128withRSA";
            algorithms[TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128.getId()] = "RIPEMD128withRSA";

            algorithms["RIPEMD160WITHRSA"] = "RIPEMD160withRSA";
            algorithms["RIPEMD160WITHRSAENCRYPTION"] = "RIPEMD160withRSA";
            algorithms[TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160.getId()] = "RIPEMD160withRSA";

            algorithms["RIPEMD256WITHRSA"] = "RIPEMD256withRSA";
            algorithms["RIPEMD256WITHRSAENCRYPTION"] = "RIPEMD256withRSA";
            algorithms[TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256.getId()] = "RIPEMD256withRSA";

            algorithms["SHA1WITHDSA"] = "SHA-1withDSA";
            algorithms["SHA-1WITHDSA"] = "SHA-1withDSA";
            algorithms[X9ObjectIdentifiers.id_dsa_with_sha1.getId()] = "SHA-1withDSA";
            algorithms["SHA1WITHECDSA"] = "SHA-1withECDSA";
            algorithms["SHA-1WITHECDSA"] = "SHA-1withECDSA";
            algorithms[X9ObjectIdentifiers.ecdsa_with_SHA1.getId()] = "SHA-1withECDSA";

            oids["MD2withRSA"] = PKCSObjectIdentifiers.md2WithRSAEncryption;
            oids["MD4withRSA"] = PKCSObjectIdentifiers.md4WithRSAEncryption;
            oids["MD5withRSA"] = PKCSObjectIdentifiers.md5WithRSAEncryption;
            oids["RIPEMD128withRSA"] = TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128;
            oids["RIPEMD160withRSA"] = TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160;
            oids["RIPEMD256withRSA"] = TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256;
            oids["SHA-1withRSA"] = PKCSObjectIdentifiers.sha1WithRSAEncryption;
            oids["SHA-224withRSA"] = PKCSObjectIdentifiers.sha224WithRSAEncryption;
            oids["SHA-256withRSA"] = PKCSObjectIdentifiers.sha256WithRSAEncryption;
            oids["SHA-384withRSA"] = PKCSObjectIdentifiers.sha384WithRSAEncryption;
            oids["SHA-512withRSA"] = PKCSObjectIdentifiers.sha512WithRSAEncryption;
            oids["SHA-1withDSA"] = X9ObjectIdentifiers.id_dsa_with_sha1;
            oids["SHA-1withECDSA"] = X9ObjectIdentifiers.ecdsa_with_SHA1;
        }
              
        /// <summary>
        /// Returns a ObjectIdentifier for a give encoding.
        /// </summary>
        /// <param name="mechanism">A String representation of the encoding.</param>
        /// <returns>A DERObjectIdentifier, null if the OID is not available.</returns>

        public static DERObjectIdentifier getObjectIdentifier(string mechanism)
        {
            mechanism = (string)algorithms[mechanism.ToUpper()];

            if (mechanism != null)
            {
                return (DERObjectIdentifier)oids[mechanism];
            }

            return null;
        }

        public static ICollection getAlgorithms()
        {
            return oids.Keys;
        }

        public static Signer getSigner(DERObjectIdentifier id)
        {
            return getSigner(id.getId());
        }

        public static Signer getSigner(String algorithm)
        {
            String mechanism = (string)algorithms[algorithm.ToUpper()];

            if (mechanism.Equals("MD2withRSA"))
            {
                return (new RSADigestSigner(new MD2Digest()));
            }
            if (mechanism.Equals("MD4withRSA"))
            {
                return (new RSADigestSigner(new MD5Digest()));
            }
            if (mechanism.Equals("MD5withRSA"))
            {
                return (new RSADigestSigner(new MD5Digest()));
            }
            if (mechanism.Equals("SHA-1withRSA"))
            {
                return (new RSADigestSigner(new SHA1Digest()));
            }
            if (mechanism.Equals("SHA-224withRSA"))
            {
                return (new RSADigestSigner(new SHA224Digest()));
            }
            if (mechanism.Equals("SHA-256withRSA"))
            {
                return (new RSADigestSigner(new SHA256Digest()));
            }
            if (mechanism.Equals("SHA-384withRSA"))
            {
                return (new RSADigestSigner(new SHA384Digest()));
            }
            if (mechanism.Equals("SHA-512withRSA"))
            {
                return (new RSADigestSigner(new SHA512Digest()));
            }
            if (mechanism.Equals("SHA-256withRSAandMGF1"))
            {
                return (new PSSSigner(new RSAEngine(), new SHA256Digest()));
            }
            if (mechanism.Equals("SHA-384withRSAandMGF1"))
            {
                return (new PSSSigner(new RSAEngine(), new SHA384Digest()));
            }
            if (mechanism.Equals("SHA-512withRSAandMGF1"))
            {
                return (new PSSSigner(new RSAEngine(), new SHA512Digest()));
            }
            if (mechanism.Equals("RIPEMD128withRSA"))
            {
                return (new RSADigestSigner(new RIPEMD160Digest()));
            }
            if (mechanism.Equals("RIPEMD160withRSA"))
            {
                return (new RSADigestSigner(new RIPEMD160Digest()));
            }
            if (mechanism.Equals("RIPEMD256withRSA"))
            {
                return (new RSADigestSigner(new RIPEMD160Digest()));
            }
            if (mechanism.Equals("SHA-1withDSA"))
            {
                return (new DSADigestSigner(new DSASigner(), new SHA1Digest()));
            }
            if (mechanism.Equals("SHA-1withECDSA"))
            {
                return (new DSADigestSigner(new ECDSASigner(), new SHA1Digest()));
            }

            throw new Exception("Signature " + mechanism + " not recognised.");
        }

        public static String getEncodingName(DERObjectIdentifier oid)
        {
            return (string)algorithms[oid.getId()];
        }
    }
}
