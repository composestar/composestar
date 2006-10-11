#region Using directives

using System;
using System.Collections;
using System.Text;
using org.bouncycastle.asn1.x509;
using org.bouncycastle.asn1;
using org.bouncycastle.math;
using org.bouncycastle.crypto.parameters;
using org.bouncycastle.crypto;
using System.IO;
using org.bouncycastle.security;

#endregion

namespace org.bouncycastle.x509
{
    /// <summary>
    /// A class to generate Version 3 X509Certificates.
    /// </summary>
    public class X509V3CertificateGenerator
    {

        private V3TBSCertificateGenerator tbsGen;
        private DERObjectIdentifier sigOID;
        private AlgorithmIdentifier sigAlgId;
        private String signatureAlgorithm;
        private Hashtable extensions = null;
        private ArrayList extOrdering = null;

        public X509V3CertificateGenerator()
        {
            tbsGen = new V3TBSCertificateGenerator();
        }

      /// <summary>
      /// Reset the Generator.
      /// </summary>
         public void reset()
        {
            tbsGen = new V3TBSCertificateGenerator();
            extensions = null;
            extOrdering = null;
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
        /// Set the Distinguised name of the issuer. The issuer is the entity which is signing the certificate.
        /// </summary>
        /// <param name="issuer">The issuers DN.</param>
        public void setIssuerDN(
            X509Name issuer)
        {
            tbsGen.setIssuer(issuer);
        }


        /// <summary>
        /// Set the date that this certificate is to be valid after.
        /// </summary>
        /// <param name="date"></param>
        public void setNotBefore(
            DateTime date)
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
    /// Set the DN of the entity that this certificate is about.
    /// </summary>
    /// <param name="subject"></param>
        public void setSubjectDN(X509Name subject)
        {
            tbsGen.setSubject(subject);
        }


        /// <summary>
        /// Set the public key that this certificate identifies.
        /// </summary>
        /// <param name="key"></param>
        public void setPublicKey(AsymmetricKeyParameter key)
        {
            try
            {
                tbsGen.setSubjectPublicKeyInfo(SubjectPublicKeyInfoFactory.CreateSubjectPublicKeyInfo(key));
            }
            catch (Exception e)
            {
                throw new Exception("unable to process key - " + e.Message);
            }
        }

        /// <summary>
        /// Set the signature algorithm that will be used to sign this certificate.
        /// </summary>
        /// <param name="signatureAlgorithm"></param>
        public void setSignatureAlgorithm(String signatureAlgorithm)
        {
            this.signatureAlgorithm = signatureAlgorithm;

            sigOID = SignerUtil.getObjectIdentifier(signatureAlgorithm.ToUpper());

            if (sigOID == null)
            {
                throw new Exception("Unknown signature type requested");
            }

            
            sigAlgId = new AlgorithmIdentifier(sigOID, new DERNull());

            tbsGen.setSignature(sigAlgId);
        }

        /// <summary>
        /// Add an extension to this certificate.
        /// </summary>
        /// <param name="OID">Its Object identifier.</param>
        /// <param name="critical">Is it crtical.</param>
        /// <param name="value">The value.</param>
        public void addExtension(DERObjectIdentifier OID,bool critical, ASN1Encodable value)
        {

            MemoryStream mStr = new MemoryStream();
            DEROutputStream dOut = new DEROutputStream(mStr);

            try
            {
                dOut.writeObject(value);
            }
            catch (IOException e)
            {
                throw new Exception("error encoding value: " + e);
            }

            this.addExtension(OID, critical, mStr.ToArray());
        }

        /// <summary>
        /// Add and extension to this certificate.
        /// </summary>
        /// <param name="OID">The object identifier.</param>
        /// <param name="critical">Is it critical.</param>
        /// <param name="value">byte[] containing the value of this extension.</param>
        public void addExtension(DERObjectIdentifier OID,bool critical,byte[] value)
        {
            if (extensions == null)
            {
                extensions = new Hashtable();
                extOrdering = new ArrayList();
            }
            extensions[OID]= new X509Extension(critical, new DEROctetString(value));
            extOrdering.Add(OID);
        }

        /// <summary>
        /// Add an extension using a string with a dotted decimal OID.
        /// </summary>
        /// <param name="OID">String containing a dotted decimal OID.</param>
        /// <param name="critical">Is it critical.</param>
        /// <param name="value">The value.</param>
        public void addExtension(String OID, bool critical,byte[] value)
        {
            this.addExtension(new DERObjectIdentifier(OID), critical, value);
        }

        /// <summary>
        /// Generate an X509Certificate.
        /// </summary>
        /// <param name="key">The private key of the issuer that is signing this certificate.</param>
        /// <returns>An X509Certificate.</returns>
        public X509Certificate generateX509Certificate(AsymmetricKeyParameter key)
        {
            return generateX509Certificate(key, null);
        }


    /// <summary>
    /// Generate an X509Certificate using your own SecureRandom.
    /// </summary>
    /// <param name="key">The private key of the issuer that is signing this certificate.</param>
    /// <param name="random">You Secure Random instance.</param>
    /// <returns>An X509Certificate.</returns>
    public X509Certificate generateX509Certificate(AsymmetricKeyParameter key, SecureRandom random)
    {
        Signer sig = null;

        if (sigOID == null)
        {
            throw new Exception("no signature algorithm specified");
        }

        try
        {
            sig = SignerUtil.getSigner(sigOID);
        }
        catch
        {
            try
            {
                sig = SignerUtil.getSigner(signatureAlgorithm);
            }
            catch (Exception e)
            {
                throw new Exception("exception creating signature: " + e.Message);
            }
        }

        if (random != null)
        {
            sig.init(true, new ParametersWithRandom(key, random));
        }
        else
        {
           // Console.WriteLine("**" + sig.GetType());
            sig.init(true, key);            
        }
        
        if (extensions != null)
        {
            tbsGen.setExtensions(new X509Extensions(extOrdering, extensions));
        }
        TBSCertificateStructure tbsCert = tbsGen.generateTBSCertificate();
        try
        {
            MemoryStream mStr = new MemoryStream();
            DEROutputStream         dOut = new DEROutputStream(mStr);
            dOut.writeObject(tbsCert);
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

        return new X509Certificate(new DERSequence(v));
    }


    }
}
