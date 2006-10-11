
using System;
using System.Collections;
using System.Text;
using org.bouncycastle.asn1;
using org.bouncycastle.asn1.oiw;
using org.bouncycastle.asn1.x509;
using org.bouncycastle.asn1.x9;
using org.bouncycastle.crypto.parameters;
using org.bouncycastle.asn1.pkcs;
using org.bouncycastle.asn1.sec;
using System.IO;
using org.bouncycastle.crypto;

namespace org.bouncycastle.security
{
    public class PrivateKeyFactory
    {
        public static AsymmetricKeyParameter CreateKey(PrivateKeyInfo keyInfo)
        {
            AlgorithmIdentifier algId = keyInfo.getAlgorithmId();
            if (algId.getObjectId().Equals(PKCSObjectIdentifiers.rsaEncryption))
                {
                    RSAPrivateKeyStructure  keyStructure = new RSAPrivateKeyStructure((ASN1Sequence)keyInfo.getPrivateKey());
                    return (new RSAPrivateCrtKeyParameters(
                                        keyStructure.getModulus(),
                                        keyStructure.getPublicExponent(),
                                        keyStructure.getPrivateExponent(),
                                        keyStructure.getPrime1(),
                                        keyStructure.getPrime2(),
                                        keyStructure.getExponent1(),
                                        keyStructure.getExponent2(),
                                        keyStructure.getCoefficient()));
                }
            else if (algId.getObjectId().Equals(PKCSObjectIdentifiers.dhKeyAgreement))
            {
                DHParameter para = new DHParameter((ASN1Sequence)keyInfo.getAlgorithmId().getParameters());
                DERInteger derX = (DERInteger)keyInfo.getPrivateKey();
                return new DHPrivateKeyParameters(derX.getValue(), new DHParameters(para.getP(), para.getG()));
            }
            else if (algId.getObjectId().Equals(OIWObjectIdentifiers.elGamalAlgorithm))
            {
                ElGamalParameter  para = new ElGamalParameter((ASN1Sequence)keyInfo.getAlgorithmId().getParameters());
                DERInteger derX = (DERInteger)keyInfo.getPrivateKey();
                return new ElGamalPrivateKeyParameters(derX.getValue(), new ElGamalParameters(para.getP(), para.getG()));
            }
            else if (algId.getObjectId().Equals(X9ObjectIdentifiers.id_dsa))
            {
                DSAParameter para = new DSAParameter((ASN1Sequence)keyInfo.getAlgorithmId().getParameters());
                DERInteger derX = (DERInteger)keyInfo.getPrivateKey();
                return new DSAPrivateKeyParameters(derX.getValue(), new DSAParameters(para.getP(), para.getQ(), para.getG()));
            }
            else if (algId.getObjectId().Equals(X9ObjectIdentifiers.id_ecPublicKey))
            {
                
                X962Parameters para = new X962Parameters((ASN1Object)keyInfo.getAlgorithmId().getParameters());
                ECDomainParameters dParams = null;
                
                if (para.isNamedCurve())
                {
                    DERObjectIdentifier oid = (DERObjectIdentifier)para.getParameters();
                    X9ECParameters ecP = X962NamedCurves.getByOID(oid);
    
                    dParams = new ECDomainParameters(
                                                ecP.getCurve(),
                                                ecP.getG(),
                                                ecP.getN(),
                                                ecP.getH(),
                                                ecP.getSeed());
                }
                else
                {
                    X9ECParameters ecP = new X9ECParameters(
                                (ASN1Sequence)para.getParameters());
                    dParams = new ECDomainParameters(
                                                ecP.getCurve(),
                                                ecP.getG(),
                                                ecP.getN(),
                                                ecP.getH(),
                                                ecP.getSeed());
                }
    
                ECPrivateKeyStructure   ec = new ECPrivateKeyStructure((ASN1Sequence)keyInfo.getPrivateKey());
    
                return new ECPrivateKeyParameters(ec.getKey(), dParams);
            }
            else
            {
                throw new Exception("algorithm identifier in key not recognised");
            }
        }
    }
}
