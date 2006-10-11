#region Using directives

using System;
using System.Collections;
using System.Text;
using org.bouncycastle.asn1.x509;
using org.bouncycastle.asn1;
using org.bouncycastle.math;
using org.bouncycastle.security;
using org.bouncycastle.crypto.parameters;
using org.bouncycastle.crypto;
using System.IO;



#endregion

namespace org.bouncycastle.x509
{
    /// <summary>
    /// Class to generate X509V1 Certificates.
    /// </summary>
    public class X509V1CertificateGenerator
    {
        private V1TBSCertificateGenerator tbsGen;
        private DERObjectIdentifier sigOID;
        private AlgorithmIdentifier sigAlgId;

        /// <summary>
        /// Defaut Constructor.
        /// </summary>
        public X509V1CertificateGenerator()
        {
            tbsGen = new V1TBSCertificateGenerator();
        }

        /// <summary>
        /// Set the Certificates serial number.
        /// </summary>
        /// <remarks>Make serial numbers long, if you have no serial number policy make sure the number is at least 16 bytes of secure random data.
        /// You will be surprised how ugly a serial number colision can get.</remarks>
        /// <param name="serialNumber">The serial number.</param>
        public void setSerialNumber(BigInteger serialNumber)
        {
            tbsGen.setSerialNumber(new DERInteger(serialNumber));
        }

        /// <summary>
        /// Set the Distinguised name of the issuer.
        /// </summary>
        /// <param name="issuer">The issuers DN.</param>
        public void setIssuerDN(X509Name issuer)
        {
            tbsGen.setIssuer(issuer);
        }

        /// <summary>
        /// Set the date that this certificate is to be valid after.
        /// </summary>
        /// <param name="date"></param>
        public void setNotBefore(DateTime date)
        {
            tbsGen.setStartDate(new Time(date));
        }

        /// <summary>
        /// Set the date that this certificate will no longer be valid from.
        /// </summary>
        /// <param name="date"></param>
        public void setNotAfter(DateTime date)
        {
            tbsGen.setEndDate(new Time(date));
        }


        /// <summary>
        /// Set the public key that this certificate identifies.
        /// </summary>
        /// <param name="key"></param>
        public void setPublicKey(AsymmetricKeyParameter key)
        {
            tbsGen.setSubjectPublicKeyInfo(SubjectPublicKeyInfoFactory.CreateSubjectPublicKeyInfo(key));
        }


        /// <summary>
        /// Set the signature algorithm that will be used to sign this certificate.
        /// </summary>
        /// <param name="signatureAlgorithm"></param>
        public void setSignatureAlgorithm(String signatureAlgorithm)
        {
            sigOID = SignerUtil.getObjectIdentifier(signatureAlgorithm);

            if (sigOID == null)
            {
                throw new Exception("Unknown signature type requested");
            }

            sigAlgId = new AlgorithmIdentifier(this.sigOID, new DERNull());

            tbsGen.setSignature(sigAlgId);
        }

        

    /// <summary>
    /// Generate a new X509Certificate.
    /// </summary>
    /// <param name="privateKey">The private key of the issuer used to sign this certificate.</param>
    /// <returns>An X509Certificate.</returns>
    public X509CertificateStructure generateX509Certificate(AsymmetricKeyParameter privateKey)
    {
        return generateX509Certificate(privateKey, null);
    }

        /// <summary>
        /// Generate a new X509Certificate specifying a SecureRandom instance that you would like to use.
        /// </summary>
        /// <param name="privateKey">The private key of the issuer used to sign this certificate.</param>
        /// <param name="random">The Secure Random you want to use.</param>
        /// <returns></returns>
    public X509CertificateStructure generateX509Certificate(
        AsymmetricKeyParameter      privateKey,
                SecureRandom    random)
       
    {
        Signer sig = null;

       try {
            sig = SignerUtil.getSigner(sigOID);
        }
        catch (Exception ex)
        {
                throw new Exception("exception creating signature: " + ex.Message);   
        }

        if (random != null)
        {

            sig.init(true,privateKey);
        }
        else
        {
            sig.init(true,new ParametersWithRandom(privateKey,random));
        }

        TBSCertificateStructure tbsCert = tbsGen.generateTBSCertificate();

        try
        {
            MemoryStream mStr = new MemoryStream();
            DEROutputStream         dOut = new DEROutputStream(mStr);
            dOut.writeObject(tbsCert);
            mStr.Flush();
            byte[] b = mStr.ToArray();
            sig.update(b,0,b.Length);
        }
        catch (Exception e)
        {
            throw new Exception("exception encoding TBS cert - " + e);
        }

        ASN1EncodableVector  v = new ASN1EncodableVector();

        v.add(tbsCert);
        v.add(sigAlgId);
        v.add(new DERBitString(sig.generateSignature()));

        return (new X509CertificateStructure(new DERSequence(v)));
    }
}
}
