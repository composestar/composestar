using System;
using System.IO;
using System.Collections;
using System.Text;

using org.bouncycastle.crypto.parameters;
using org.bouncycastle.asn1.pkcs;
using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;
using org.bouncycastle.asn1.x9;
using org.bouncycastle.asn1.sec;
using org.bouncycastle.asn1.oiw;
using org.bouncycastle.crypto;
using org.bouncycastle.security;

namespace org.bouncycastle.pkcs
{
    public class PrivateKeyInfoFactory
    {
        public static PrivateKeyInfo createPrivateKeyInfo(AsymmetricKeyParameter key)
        {
            
            /*
                Process DH private key.
                The value for L was set to zero implicitly.
                This is the same action as found in JCEDHPrivateKey getEncoded method.
            */

            if (key is ElGamalPrivateKeyParameters)
            {
                ElGamalPrivateKeyParameters _key = (ElGamalPrivateKeyParameters)key;
                PrivateKeyInfo info =
                    new PrivateKeyInfo(
                        new AlgorithmIdentifier(
                            OIWObjectIdentifiers.elGamalAlgorithm,
                            new ElGamalParameter(
                                _key.getParameters().getP(),
                                _key.getParameters().getG()).toASN1Object()), new DERInteger(_key.getX()));
                                return info;
                            }


            if (key is DSAPrivateKeyParameters)
            {
                DSAPrivateKeyParameters _key = (DSAPrivateKeyParameters)key;
                PrivateKeyInfo info =
                    new PrivateKeyInfo(
                        new AlgorithmIdentifier(
                            X9ObjectIdentifiers.id_dsa,
                                new DSAParameter(
                                    _key.getParameters().getP(),
                                    _key.getParameters().getQ(),
                                    _key.getParameters().getG()).toASN1Object()), new DERInteger(_key.getX()));

                return info;
            }


            if (key is DHPrivateKeyParameters)
            {
                DHPrivateKeyParameters _key = (DHPrivateKeyParameters)key;
               
                
                PrivateKeyInfo info = new PrivateKeyInfo(
                    new AlgorithmIdentifier(
                    PKCSObjectIdentifiers.dhKeyAgreement,new DHParameter(
                            _key.getParameters().getP(),
                            _key.getParameters().getG(),
                            0
                                ).toASN1Object()
                            )
                            , new DERInteger(_key.getX()));

                   return info;
            }


            if (key is RSAPrivateCrtKeyParameters)
            {
                RSAPrivateCrtKeyParameters _key = (RSAPrivateCrtKeyParameters)key;
                PrivateKeyInfo info = new PrivateKeyInfo(
                     new AlgorithmIdentifier(
                     PKCSObjectIdentifiers.rsaEncryption, new DERNull()),
                     new RSAPrivateKeyStructure(
                     _key.getModulus(),
                     _key.getPublicExponent(),
                     _key.getExponent(),
                     _key.getP(),
                     _key.getQ(),
                     _key.getDP(),
                     _key.getDQ(),
                     _key.getQInv()).toASN1Object());

                    return info;
             }

            if (key is ECPrivateKeyParameters)
            {

                ECPrivateKeyParameters _key = (ECPrivateKeyParameters)key;

                X9ECParameters ecP = new X9ECParameters(
                                            _key.getParameters().getCurve(),
                                            _key.getParameters().getG(),
                                            _key.getParameters().getN(),
                                            _key.getParameters().getH(),
                                            _key.getParameters().getSeed());
                X962Parameters x962 = new X962Parameters(ecP);
            

                PrivateKeyInfo info = new PrivateKeyInfo(
                    new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, x962.toASN1Object()),
                    new ECPrivateKeyStructure(_key.getD()).toASN1Object());
                
         
                return info;
            }

            throw (new Exception("Class provided is not convertable:" + key.GetType()));
        }

        public static PrivateKeyInfo createPrivateKeyInfo(
            char[] passPhrase,
            EncryptedPrivateKeyInfo encInfo)
        {
            CipherParameters keyParameters = PBEUtil.generateCipherParameters(encInfo.getEncryptionAlgorithm().getObjectId(), passPhrase, encInfo.getEncryptionAlgorithm().getParameters());
            Object engine = PBEUtil.createEngine(encInfo.getEncryptionAlgorithm().getObjectId());
            byte[] encoding = null;

            if (engine is BufferedBlockCipher)
            {
                BufferedBlockCipher cipher = (BufferedBlockCipher)engine;

                cipher.init(false, keyParameters);

                byte[] keyBytes = encInfo.getEncryptedData();

                int encLen = cipher.getOutputSize(keyBytes.Length);

                encoding = new byte[encLen];

                int off = cipher.processBytes(keyBytes, 0, keyBytes.Length, encoding, 0);

                cipher.doFinal(encoding, off);
            }
            else if (engine is StreamCipher)
            {
                StreamCipher cipher = (StreamCipher)engine;

                cipher.init(false, keyParameters);

                byte[] keyBytes = encInfo.getEncryptedData();

                encoding = new byte[keyBytes.Length];

                cipher.processBytes(keyBytes, 0, keyBytes.Length, encoding, 0);
            }

            ASN1InputStream aIn = new ASN1InputStream(new MemoryStream(encoding));

            return PrivateKeyInfo.getInstance(aIn.readObject());
        }
    }
}
