using System;
using System.IO;
using System.Collections;
using System.Text;

using org.bouncycastle.asn1.pkcs;
using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;
using org.bouncycastle.asn1.x9;
using org.bouncycastle.asn1.sec;
using org.bouncycastle.asn1.oiw;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.paddings;
using org.bouncycastle.crypto.parameters;
using org.bouncycastle.security;

namespace org.bouncycastle.pkcs
{
    public class EncryptedPrivateKeyInfoFactory
    {
        public static EncryptedPrivateKeyInfo createEncryptedPrivateKeyInfo(
            DERObjectIdentifier algorithm,
            char[] passPhrase,
            byte[] salt, 
            int iterationCount,  
            AsymmetricKeyParameter key)
        {
            return createEncryptedPrivateKeyInfo(algorithm.getId(), passPhrase, salt, iterationCount, PrivateKeyInfoFactory.createPrivateKeyInfo(key));
        }

        public static EncryptedPrivateKeyInfo createEncryptedPrivateKeyInfo(
            String algorithm,
            char[] passPhrase,
            byte[] salt, 
            int iterationCount,  
            PrivateKeyInfo keyInfo)
        {
            if (!PBEUtil.isPBEAlgorithm(algorithm))
            {
                throw new Exception("attempt to use non-PBE algorithm with PBE EncryptedPrivateKeyInfo generation");
            }

            ASN1Encodable       parameters = PBEUtil.generateAlgorithmParameters(algorithm, salt, iterationCount);
            CipherParameters    keyParameters = PBEUtil.generateCipherParameters(algorithm, passPhrase, parameters);

            byte[]         encoding = null;
            Object         engine = PBEUtil.createEngine(algorithm);

            if (engine is BufferedBlockCipher)
            {
                BufferedBlockCipher cipher = (BufferedBlockCipher)engine;

                cipher.init(true, keyParameters);

                byte[] keyBytes = keyInfo.getEncoded();

                int encLen = cipher.getOutputSize(keyBytes.Length);

                encoding = new byte[encLen];

                int off = cipher.processBytes(keyBytes, 0, keyBytes.Length, encoding, 0);

                cipher.doFinal(encoding, off);
            }
            else if (engine is StreamCipher)
            {
                StreamCipher cipher = (StreamCipher)engine;

                cipher.init(true, keyParameters);

                byte[] keyBytes = keyInfo.getEncoded();

                encoding = new byte[keyBytes.Length];

                cipher.processBytes(keyBytes, 0, keyBytes.Length, encoding, 0);
            }

            return new EncryptedPrivateKeyInfo(new AlgorithmIdentifier(PBEUtil.getObjectIdentifier(algorithm), parameters), encoding);
        }
    }
}
