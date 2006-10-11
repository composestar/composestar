#region Using directives

using System;
using System.Text;
using System.Collections;

using org.bouncycastle.asn1;
using org.bouncycastle.asn1.oiw;
using org.bouncycastle.asn1.pkcs;
using org.bouncycastle.asn1.nist;
using org.bouncycastle.asn1.teletrust;
using org.bouncycastle.asn1.x9;
using org.bouncycastle.security;
using org.bouncycastle.crypto.digests;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.engines;
using org.bouncycastle.crypto.macs;
using org.bouncycastle.crypto.modes;
using org.bouncycastle.crypto.paddings;
using org.bouncycastle.crypto.generators;

#endregion

namespace org.bouncycastle.security
{
   
    /// <summary>
    ///  
    /// </summary>

    public class PBEUtil
    {
        const string        PKCS5S1     = "PKCS5S1";
        const string        PKCS5S2     = "PKCS5S2";
        const string        PKCS12      = "PKCS12";

        static Hashtable    algorithms = new Hashtable();
        static Hashtable    algorithmType = new Hashtable();
        static Hashtable    oids = new Hashtable();

        static PBEUtil()
        {
            algorithms["PKCS5SCHEME1"] = "PKCS5scheme1";
            algorithms["PKCS5SCHEME2"] = "PKCS5scheme2";

            algorithms["PBEWITHMD2ANDDES-CBC"] = "PBEwithMD2andDES-CBC";
            algorithms[PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC.getId()] = "PBEwithMD2andDES-CBC";

            algorithms["PBEWITHMD2ANDRC2-CBC"] = "PBEwithMD2andRC2-CBC";
            algorithms[PKCSObjectIdentifiers.pbeWithMD2AndRC2_CBC.getId()] = "PBEwithMD2andRC2-CBC";

            algorithms["PBEWITHMD5ANDDES-CBC"] = "PBEwithMD5andDES-CBC";
            algorithms[PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC.getId()] = "PBEwithMD5andDES-CBC";

            algorithms["PBEWITHMD5ANDRC2-CBC"] = "PBEwithMD5andRC2-CBC";
            algorithms[PKCSObjectIdentifiers.pbeWithMD5AndRC2_CBC.getId()] = "PBEwithMD5andRC2-CBC";

            algorithms["PBEWITHSHA1ANDDES-CBC"] = "PBEwithSHA-1andDES-CBC";
            algorithms["PBEWITHSHA-1ANDDES-CBC"] = "PBEwithSHA-1andDES-CBC";
            algorithms[PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC.getId()] = "PBEwithSHA-1andDES-CBC";

            algorithms["PBEWITHSHA1ANDRC2-CBC"] = "PBEwithSHA-1andRC2-CBC";
            algorithms["PBEWITHSHA-1ANDRC2-CBC"] = "PBEwithSHA-1andRC2-CBC";
            algorithms[PKCSObjectIdentifiers.pbeWithSHA1AndRC2_CBC.getId()] = "PBEwithSHA-1andRC2-CBC";

            algorithms["PKCS12"] = "PKCS12";

            algorithms["PBEWITHSHAAND128BITRC4"] = "PBEwithSHA-1and128bitRC4";
            algorithms["PBEWITHSHA1AND128BITRC4"] = "PBEwithSHA-1and128bitRC4";
            algorithms["PBEWITHSHA-1AND128BITRC4"] = "PBEwithSHA-1and128bitRC4";
            algorithms[PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4.getId()] = "PBEwithSHA-1and128bitRC4";

            algorithms["PBEWITHSHAAND40BITRC4"] = "PBEwithSHA-1and40bitRC4";
            algorithms["PBEWITHSHA1AND40BITRC4"] = "PBEwithSHA-1and40bitRC4";
            algorithms["PBEWITHSHA-1AND40BITRC4"] = "PBEwithSHA-1and40bitRC4";
            algorithms[PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4.getId()] = "PBEwithSHA-1and40bitRC4";

            algorithms["PBEWITHSHAAND3-KEYDESEDE-CBC"] = "PBEwithSHA-1and3-keyDESEDE-CBC";
            algorithms["PBEWITHSHAAND3-KEYTRIPLEDES-CBC"] = "PBEwithSHA-1and3-keyDESEDE-CBC";
            algorithms["PBEWITHSHA1AND3-KEYDESEDE-CBC"] = "PBEwithSHA-1and3-keyDESEDE-CBC";
            algorithms["PBEWITHSHA1AND3-KEYTRIPLEDES-CBC"] = "PBEwithSHA-1and3-keyDESEDE-CBC";
            algorithms["PBEWITHSHA-1AND3-KEYDESEDE-CBC"] = "PBEwithSHA-1and3-keyDESEDE-CBC";
            algorithms["PBEWITHSHA-1AND3-KEYTRIPLEDES-CBC"] = "PBEwithSHA-1and3-keyDESEDE-CBC";
            algorithms[PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC.getId()] = "PBEwithSHA-1and3-keyDESEDE-CBC";

            algorithms["PBEWITHSHAAND2-KEYDESEDE-CBC"] = "PBEwithSHA-1and2-keyDESEDE-CBC";
            algorithms["PBEWITHSHAAND2-KEYTRIPLEDES-CBC"] = "PBEwithSHA-1and2-keyDESEDE-CBC";
            algorithms["PBEWITHSHA1AND2-KEYDESEDE-CBC"] = "PBEwithSHA-1and2-keyDESEDE-CBC";
            algorithms["PBEWITHSHA1AND2-KEYTRIPLEDES-CBC"] = "PBEwithSHA-1and2-keyDESEDE-CBC";
            algorithms["PBEWITHSHA-1AND2-KEYDESEDE-CBC"] = "PBEwithSHA-1and2-keyDESEDE-CBC";
            algorithms["PBEWITHSHA-1AND2-KEYTRIPLEDES-CBC"] = "PBEwithSHA-1and2-keyDESEDE-CBC";
            algorithms[PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC.getId()] = "PBEwithSHA-1and2-keyDESEDE-CBC";

            algorithms["PBEWITHSHAAND128BITRC2-CBC"] = "PBEwithSHA-1and128bitRC2-CBC";
            algorithms["PBEWITHSHA1AND128BITRC2-CBC"] = "PBEwithSHA-1and128bitRC2-CBC";
            algorithms["PBEWITHSHA-1AND128BITRC2-CBC"] = "PBEwithSHA-1and128bitRC2-CBC";
            algorithms[PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC.getId()] = "PBEwithSHA-1and128bitRC2-CBC";

            algorithms["PBEWITHSHAAND40BITRC2-CBC"] = "PBEwithSHA-1and40bitRC2-CBC";
            algorithms["PBEWITHSHA1AND40BITRC2-CBC"] = "PBEwithSHA-1and40bitRC2-CBC";
            algorithms["PBEWITHSHA-1AND40BITRC2-CBC"] = "PBEwithSHA-1and40bitRC2-CBC";
            algorithms[PKCSObjectIdentifiers.pbewithSHAAnd40BitRC2_CBC.getId()] = "PBEwithSHA-1and40bitRC2-CBC";

            algorithms["PBEWITHHMACSHA1"] = "PBEwithHmacSHA-1";
            algorithms["PBEWITHHMACSHA-1"] = "PBEwithHmacSHA-1";
            algorithms[OIWObjectIdentifiers.id_SHA1.getId()] = "PBEwithHmacSHA-1";
            algorithms["PBEWITHHMACSHA224"] = "PBEwithHmacSHA-224";
            algorithms["PBEWITHHMACSHA-224"] = "PBEwithHmacSHA-224";
            algorithms[NISTObjectIdentifiers.id_sha224.getId()] = "PBEwithHmacSHA-224";
            algorithms["PBEWITHHMACSHA256"] = "PBEwithHmacSHA-256";
            algorithms["PBEWITHHMACSHA-256"] = "PBEwithHmacSHA-256";
            algorithms[NISTObjectIdentifiers.id_sha256.getId()] = "PBEwithHmacSHA-256";
            algorithms["PBEWITHHMACRIPEMD128"] = "PBEwithHmacRIPEMD128";
            algorithms[TeleTrusTObjectIdentifiers.ripemd128.getId()] = "PBEwithHmacRIPEMD128";
            algorithms["PBEWITHHMACRIPEMD160"] = "PBEwithHmacRIPEMD160";
            algorithms[TeleTrusTObjectIdentifiers.ripemd160.getId()] = "PBEwithHmacRIPEMD160";
            algorithms["PBEWITHHMACRIPEMD256"] = "PBEwithHmacRIPEMD256";
            algorithms[TeleTrusTObjectIdentifiers.ripemd256.getId()] = "PBEwithHmacRIPEMD256";

            algorithmType["PKCS5scheme1"] = PKCS5S1;
            algorithmType["PKCS5scheme2"] = PKCS5S2;

            algorithmType["PBEwithMD2andDES-CBC"] = PKCS5S1;
            algorithmType["PBEwithMD2andRC2-CBC"] = PKCS5S1;
            algorithmType["PBEwithMD5andDES-CBC"] = PKCS5S1;
            algorithmType["PBEwithMD5andRC2-CBC"] = PKCS5S1;
            algorithmType["PBEwithSHA1andDES-CBC"] = PKCS5S1;
            algorithmType["PBEwithSHA1andRC2-CBC"] = PKCS5S1;

            algorithmType["PKCS12"] = PKCS12;
            algorithmType["PBEwithSHA-1and128bitRC4"] = PKCS12;
            algorithmType["PBEwithSHA-1and40bitRC4"] = PKCS12;
            algorithmType["PBEwithSHA-1and3-keyDESEDE-CBC"] = PKCS12;
            algorithmType["PBEwithSHA-1and2-keyDESEDE-CBC"] = PKCS12;
            algorithmType["PBEwithSHA-1and128bitRC2-CBC"] = PKCS12;
            algorithmType["PBEwithSHA-1and40bitRC2-CBC"] = PKCS12;

            algorithmType["PBEwithHmacSHA-1"] = PKCS12;
            algorithmType["PBEwithHmacSHA-224"] = PKCS12;
            algorithmType["PBEwithHmacSHA-256"] = PKCS12;
            algorithmType["PBEwithHmacRIPEMD128"] = PKCS12;
            algorithmType["PBEwithHmacRIPEMD160"] = PKCS12;
            algorithmType["PBEwithHmacRIPEMD256"] = PKCS12;

            oids["PBEwithMD2andDES-CBC"] = PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC;
            oids["PBEwithMD2andRC2-CBC"] = PKCSObjectIdentifiers.pbeWithMD2AndRC2_CBC;
            oids["PBEwithMD5andDES-CBC"] = PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC;
            oids["PBEwithMD5andRC2-CBC"] = PKCSObjectIdentifiers.pbeWithMD5AndRC2_CBC;
            oids["PBEwithSHA-1andDES-CBC"] = PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC;
            oids["PBEwithSHA-1andRC2-CBC"] = PKCSObjectIdentifiers.pbeWithSHA1AndRC2_CBC;
            oids["PBEwithSHA-1and128bitRC4"] = PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4;
            oids["PBEwithSHA-1and40bitRC4"] = PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4;
            oids["PBEwithSHA-1and3-keyDESEDE-CBC"] = PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC;
            oids["PBEwithSHA-1and2-keyDESEDE-CBC"] = PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC;
            oids["PBEwithSHA-1and128bitRC2-CBC"] = PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC;
            oids["PBEwithSHA-1and40bitRC2-CBC"] = PKCSObjectIdentifiers.pbewithSHAAnd40BitRC2_CBC;

            oids["PBEwithHmacSHA-1"] = OIWObjectIdentifiers.id_SHA1;
            oids["PBEwithHmacSHA-224"] = NISTObjectIdentifiers.id_sha224;
            oids["PBEwithHmacSHA-256"] = NISTObjectIdentifiers.id_sha256;
            oids["PBEwithHmacRIPEMD128"] = TeleTrusTObjectIdentifiers.ripemd128;
            oids["PBEwithHmacRIPEMD160"] = TeleTrusTObjectIdentifiers.ripemd160;
            oids["PBEwithHmacRIPEMD256"] = TeleTrusTObjectIdentifiers.ripemd256;
        }
         
     
        
        static PBEParametersGenerator makePBEGenerator(
            String                  type,
            Digest                  digest,
            byte[]                  key,
            byte[]                  salt,
            int                     iterationCount)
        {
            PBEParametersGenerator  generator;
    
            if (type.Equals(PKCS5S1))
            {
                generator = new PKCS5S1ParametersGenerator(digest);
            }
            else if (type.Equals(PKCS5S2))
            {
                generator = new PKCS5S2ParametersGenerator();
            }
            else
            {
                generator = new PKCS12ParametersGenerator(digest);
            }
    
            generator.init(key, salt, iterationCount);

            return generator;
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

        public static bool isPKCS12(
            String algorithm)
        {
            String mechanism = (string)algorithms[algorithm.ToUpper()];
                                                                                
            return algorithmType[mechanism].Equals(PKCS12);
        }

        public static bool isPKCS5Scheme2(
            String algorithm)
        {
            String mechanism = (string)algorithms[algorithm.ToUpper()];
                                                                                
            return algorithmType[mechanism].Equals(PKCS5S2);
        }

        public static bool isPBEAlgorithm(
            String algorithm)
        {
            String mechanism = (string)algorithms[algorithm.ToUpper()];

            return algorithmType[mechanism] != null;
        }

        public static ASN1Encodable generateAlgorithmParameters(
            DERObjectIdentifier algorithmOID,
            byte[]              salt,
            int                 iterationCount)
        {
            return generateAlgorithmParameters(algorithmOID.getId(), salt, iterationCount);
        }

        public static ASN1Encodable generateAlgorithmParameters(
            String  algorithm,
            byte[]  salt,
            int     iterationCount)
        {
            if (isPKCS12(algorithm))
            {
                return new PKCS12PBEParams(salt, iterationCount);
            }
            else if (isPKCS5Scheme2(algorithm))
            {
                return new PBKDF2Params(salt, iterationCount);
            }
            else
            {
                return new PBEParameter(salt, iterationCount);
            }
        }

        public static CipherParameters generateCipherParameters(
            DERObjectIdentifier algorithmOID,
            char[]              password,
            ASN1Encodable       pbeParameters)
        {
            return generateCipherParameters(algorithmOID.getId(), password, pbeParameters);
        }

        public static CipherParameters generateCipherParameters(
            String          algorithm,
            char[]          password,
            ASN1Encodable   pbeParameters)
        {
            String                  mechanism = (string)algorithms[algorithm.ToUpper()];
            byte[]                  key;
            CipherParameters        parameters = null;
            String                  type = (String)algorithmType[mechanism];
            byte[]                  salt = null;
            int                     iterationCount = 0;

            if (isPKCS12(mechanism))
            {
                PKCS12PBEParams pbeParams = PKCS12PBEParams.getInstance(pbeParameters);

                salt = pbeParams.getIV();
                iterationCount = pbeParams.getIterations().intValue();
                key = PBEParametersGenerator.PKCS12PasswordToBytes(password);
            }
            else if (isPKCS5Scheme2(mechanism))
            {
                PBKDF2Params pbeParams = PBKDF2Params.getInstance(pbeParameters);

                salt = pbeParams.getSalt();
                iterationCount = pbeParams.getIterationCount().intValue();
                key = PBEParametersGenerator.PKCS5PasswordToBytes(password);
            }
            else
            {
                PBEParameter pbeParams = PBEParameter.getInstance(pbeParameters);

                salt = pbeParams.getSalt();
                iterationCount = pbeParams.getIterationCount().intValue();
                key = PBEParametersGenerator.PKCS5PasswordToBytes(password);
            }

            if (mechanism.StartsWith("PBEwithSHA-1"))
            {
                PBEParametersGenerator generator = makePBEGenerator((String)algorithmType[mechanism], new SHA1Digest(), key, salt, iterationCount);

                if (mechanism.Equals("PBEwithSHA-1and128bitRC4"))
                {
                    parameters = generator.generateDerivedParameters(128);
                }
                else if (mechanism.Equals("PBEwithSHA-1and40bitRC4"))
                {
                    parameters = generator.generateDerivedParameters(40);
                }
                else if (mechanism.Equals("PBEwithSHA-1and3-keyDESEDE-CBC"))
                {
                    parameters = generator.generateDerivedParameters(192, 64);
                }
                else if (mechanism.Equals("PBEwithSHA-1and2-keyDESEDE-CBC"))
                {
                    parameters = generator.generateDerivedParameters(128, 64);
                }
                else if (mechanism.Equals("PBEwithSHA-1and128bitRC2-CBC"))
                {
                    parameters = generator.generateDerivedParameters(128, 64);
                }
                else if (mechanism.Equals("PBEwithSHA-1and40bitRC2-CBC"))
                {
                    parameters = generator.generateDerivedParameters(40, 64);
                }
                else if (mechanism.Equals("PBEwithSHA1andDES-CBC"))
                {
                    parameters = generator.generateDerivedParameters(64, 64);
                }
                else if (mechanism.Equals("PBEwithSHA1andRC2-CBC"))
                {
                    parameters = generator.generateDerivedParameters(128, 64);
                }
            }
            else if (mechanism.StartsWith("PBEwithMD5"))
            {
                PBEParametersGenerator generator = makePBEGenerator((String)algorithmType[mechanism], new MD5Digest(), key, salt, iterationCount);

                if (mechanism.Equals("PBEwithMD5andDES-CBC"))
                {
                    parameters = generator.generateDerivedParameters(64, 64);
                }
                else if (mechanism.Equals("PBEwithMD5andRC2-CBC"))
                {
                    parameters = generator.generateDerivedParameters(64, 64);
                }
            }
            else if (mechanism.StartsWith("PBEwithMD2"))
            {
                PBEParametersGenerator generator = makePBEGenerator((String)algorithmType[mechanism], new MD2Digest(), key, salt, iterationCount);

                if (mechanism.Equals("PBEwithMD2andDES-CBC"))
                {
                    parameters = generator.generateDerivedParameters(64, 64);
                }
                else if (mechanism.Equals("PBEwithMD2andRC2-CBC"))
                {
                    parameters = generator.generateDerivedParameters(64, 64);
                }
            }
            else if (mechanism.StartsWith("PBEwithHmac"))
            {
                if (mechanism.Equals("PBEwithHmacSHA-1"))
                {
                    PBEParametersGenerator generator = makePBEGenerator((String)algorithmType[mechanism], new SHA1Digest(), key, salt, iterationCount);

                    parameters = generator.generateDerivedMacParameters(160);
                }
                else if (mechanism.Equals("PBEwithHmacSHA-224"))
                {
                    PBEParametersGenerator generator = makePBEGenerator((String)algorithmType[mechanism], new SHA224Digest(), key, salt, iterationCount);

                    parameters = generator.generateDerivedMacParameters(224);
                }
                else if (mechanism.Equals("PBEwithHmacSHA-256"))
                {
                    PBEParametersGenerator generator = makePBEGenerator((String)algorithmType[mechanism], new SHA256Digest(), key, salt, iterationCount);

                    parameters = generator.generateDerivedMacParameters(256);
                }
                else if (mechanism.Equals("PBEwithHmacRIPEMD128"))
                {
                    PBEParametersGenerator generator = makePBEGenerator((String)algorithmType[mechanism], new RIPEMD128Digest(), key, salt, iterationCount);

                    parameters = generator.generateDerivedMacParameters(128);
                }
                else if (mechanism.Equals("PBEwithHmacRIPEMD160"))
                {
                    PBEParametersGenerator generator = makePBEGenerator((String)algorithmType[mechanism], new RIPEMD160Digest(), key, salt, iterationCount);

                    parameters = generator.generateDerivedMacParameters(160);
                }
                else if (mechanism.Equals("PBEwithHmacRIPEMD256"))
                {
                    PBEParametersGenerator generator = makePBEGenerator((String)algorithmType[mechanism], new RIPEMD256Digest(), key, salt, iterationCount);

                    parameters = generator.generateDerivedMacParameters(256);
                }
            }

            for (int i = 0; i != key.Length; i++)
            {
                key[i] = 0;
            }

            return parameters;
        }

        public static Object createEngine(
            DERObjectIdentifier algorithmOID)
        {
            return createEngine(algorithmOID.getId());
        }

        public static Object createEngine(
            String algorithm)
        {
            String mechanism = (string)algorithms[algorithm.ToUpper()];

            if (mechanism.StartsWith("PBEwithSHA-1"))
            {
                if (mechanism.Equals("PBEwithSHA-1and128bitRC4"))
                {
                    return new RC4Engine();
                }
                else if (mechanism.Equals("PBEwithSHA-1and40bitRC4"))
                {
                    return new RC4Engine();
                }
                else if (mechanism.Equals("PBEwithSHA-1and3-keyDESEDE-CBC"))
                {
                    return new PaddedBufferedBlockCipher(new CBCBlockCipher(new DESedeEngine()), new PKCS7Padding());
                }
                else if (mechanism.Equals("PBEwithSHA-1and2-keyDESEDE-CBC"))
                {
                    return new PaddedBufferedBlockCipher(new CBCBlockCipher(new DESedeEngine()), new PKCS7Padding());
                }
                else if (mechanism.Equals("PBEwithSHA-1and128bitRC2-CBC"))
                {
                    return new PaddedBufferedBlockCipher(new CBCBlockCipher(new RC2Engine()), new PKCS7Padding());
                }
                else if (mechanism.Equals("PBEwithSHA-1and40bitRC2-CBC"))
                {
                    return new PaddedBufferedBlockCipher(new CBCBlockCipher(new RC2Engine()), new PKCS7Padding());
                }
                else if (mechanism.Equals("PBEwithSHA1andDES-CBC"))
                {
                    return new PaddedBufferedBlockCipher(new CBCBlockCipher(new DESEngine()), new PKCS7Padding());
                }
                else if (mechanism.Equals("PBEwithSHA1andRC2-CBC"))
                {
                    return new PaddedBufferedBlockCipher(new CBCBlockCipher(new RC2Engine()), new PKCS7Padding());
                }
            }
            else if (mechanism.StartsWith("PBEwithMD5"))
            {
                if (mechanism.Equals("PBEwithMD5andDES-CBC"))
                {
                    return new PaddedBufferedBlockCipher(new CBCBlockCipher(new DESEngine()), new PKCS7Padding());
                }
                else if (mechanism.Equals("PBEwithMD5andRC2-CBC"))
                {
                    return new PaddedBufferedBlockCipher(new CBCBlockCipher(new RC2Engine()), new PKCS7Padding());
                }
            }
            else if (mechanism.StartsWith("PBEwithMD2"))
            {
                if (mechanism.Equals("PBEwithMD2andDES-CBC"))
                {
                    return new PaddedBufferedBlockCipher(new CBCBlockCipher(new DESEngine()), new PKCS7Padding());
                }
                else if (mechanism.Equals("PBEwithMD2andRC2-CBC"))
                {
                    return new PaddedBufferedBlockCipher(new CBCBlockCipher(new RC2Engine()), new PKCS7Padding());
                }
            }
            else if (mechanism.StartsWith("PBEwithHmac"))
            {
                if (mechanism.Equals("PBEwithHmacSHA-1"))
                {
                    return new HMac(new SHA1Digest());
                }
                else if (mechanism.Equals("PBEwithHmacSHA-224"))
                {
                    return new HMac(new SHA224Digest());
                }
                else if (mechanism.Equals("PBEwithHmacSHA-256"))
                {
                    return new HMac(new SHA256Digest());
                }
                else if (mechanism.Equals("PBEwithHmacRIPEMD128"))
                {
                    return new HMac(new RIPEMD128Digest());
                }
                else if (mechanism.Equals("PBEwithHmacRIPEMD160"))
                {
                    return new HMac(new RIPEMD160Digest());
                }
                else if (mechanism.Equals("PBEwithHmacRIPEMD256"))
                {
                    return new HMac(new RIPEMD256Digest());
                }
            }

            return null;
        }

        public static String getEncodingName(DERObjectIdentifier oid)
        {
            return (string)algorithms[oid.getId()];
        }
    }
}
