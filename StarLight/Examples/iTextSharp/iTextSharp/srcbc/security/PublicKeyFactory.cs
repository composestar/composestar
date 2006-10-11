
using System;
using System.Collections;
using System.Text;
using org.bouncycastle.asn1.x509;
using org.bouncycastle.crypto.parameters;
using org.bouncycastle.asn1.pkcs;
using org.bouncycastle.asn1.x9;
using org.bouncycastle.asn1;
using org.bouncycastle.asn1.oiw;
using System.IO;
using org.bouncycastle.crypto;

namespace org.bouncycastle.security
{
    public class PublicKeyFactory
    {

       public static AsymmetricKeyParameter CreateKey(SubjectPublicKeyInfo keyInfo)
       {
            AlgorithmIdentifier     algId = keyInfo.getAlgorithmId();
            if (algId.getObjectId().Equals(PKCSObjectIdentifiers.rsaEncryption)
                || algId.getObjectId().Equals(X509ObjectIdentifiers.id_ea_rsa))
            {
                RSAPublicKeyStructure   pubKey = new RSAPublicKeyStructure((ASN1Sequence)keyInfo.getPublicKey());

                return new RSAKeyParameters(false, pubKey.getModulus(), pubKey.getPublicExponent());
            }
            else if (algId.getObjectId().Equals(PKCSObjectIdentifiers.dhKeyAgreement)
                     || algId.getObjectId().Equals(X9ObjectIdentifiers.dhpublicnumber))
            {
                DHParameter para = new DHParameter((ASN1Sequence)keyInfo.getAlgorithmId().getParameters());
                DERInteger  derY = (DERInteger)keyInfo.getPublicKey();
                
                return new DHPublicKeyParameters(derY.getValue(), new DHParameters(para.getP(), para.getG()));
            }
            else if (algId.getObjectId().Equals(OIWObjectIdentifiers.elGamalAlgorithm))
            {
                ElGamalParameter    para = new ElGamalParameter((ASN1Sequence)keyInfo.getAlgorithmId().getParameters());
                DERInteger          derY = (DERInteger)keyInfo.getPublicKey();
    
                return new ElGamalPublicKeyParameters(derY.getValue(), new ElGamalParameters(para.getP(), para.getG()));
            }
            else if (algId.getObjectId().Equals(X9ObjectIdentifiers.id_dsa)
                     || algId.getObjectId().Equals(OIWObjectIdentifiers.dsaWithSHA1))
            {
                DSAParameter    para = new DSAParameter((ASN1Sequence)keyInfo.getAlgorithmId().getParameters());
                DERInteger      derY = (DERInteger)keyInfo.getPublicKey();
    
                return new DSAPublicKeyParameters(derY.getValue(), new DSAParameters(para.getP(), para.getQ(), para.getG()));
            }
            else if (algId.getObjectId().Equals(X9ObjectIdentifiers.id_ecPublicKey))
            {
                X962Parameters      para = new X962Parameters((ASN1Object)keyInfo.getAlgorithmId().getParameters());
                ECDomainParameters  dParams = null;
    
                if (para.isNamedCurve())
                {
                    DERObjectIdentifier oid = (DERObjectIdentifier)para.getParameters();
                    X9ECParameters      ecP = X962NamedCurves.getByOID(oid);
    
                    dParams = new ECDomainParameters(
                                                ecP.getCurve(),
                                                ecP.getG(),
                                                ecP.getN(),
                                                ecP.getH(),
                                                ecP.getSeed());
                }
                else
                {
                    X9ECParameters ecP = new X9ECParameters((ASN1Sequence)para.getParameters().toASN1Object());
    
    
                    dParams = new ECDomainParameters(
                                             ecP.getCurve(),
                                             ecP.getG(),
                                             ecP.getN(),
                                             ecP.getH(),
                                             ecP.getSeed());
                }
    
                DERBitString    bits = keyInfo.getPublicKeyData();
                byte[]          data = bits.getBytes();
                ASN1OctetString key = new DEROctetString(data);
    
                X9ECPoint       derQ = new X9ECPoint(dParams.getCurve(), key);
                
                return new ECPublicKeyParameters(derQ.getPoint(), dParams);
            }
            else
            {
                throw new Exception("algorithm identifier in key not recognised");
            }
        }
    }
}
