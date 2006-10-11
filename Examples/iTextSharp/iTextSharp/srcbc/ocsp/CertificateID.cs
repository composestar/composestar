#region Using directives

using System;
using System.Text;
using org.bouncycastle.asn1.ocsp;
using org.bouncycastle.crypto;
using org.bouncycastle.security;
using org.bouncycastle.asn1.x509;
using org.bouncycastle.asn1;
using org.bouncycastle.x509;
using org.bouncycastle.math;

#endregion

namespace org.bouncycastle.ocsp
{
    public class CertificateID
    {
        public static String HASH_SHA1 = "1.3.14.3.2.26";

        private CertID id;

        public CertificateID(CertID id)
        {
            this.id = id;
        }
    

        /// <summary>
        /// Generate a CertificateID.
        /// </summary>
        /// <param name="hashAlgorithm"></param>
        /// <param name="issuerCert"></param>
        /// <param name="number"></param>
        /// <param name="provider"></param>
        public CertificateID(string hashAlgorithm, X509Certificate issuerCert, BigInteger number,string provider)
        {
            try
            {
                Digest digest = TransformationByName.DigestByName(hashAlgorithm);
                AlgorithmIdentifier hashAlg = new AlgorithmIdentifier(new DERObjectIdentifier(hashAlgorithm), new DERNull());

                X509Name issuerName = issuerCert.getSubjectDN();
           
                byte[] b = issuerName.getEncoded();
                digest.update(b,0,b.Length);

                b = new byte[digest.getDigestSize()];
                digest.doFinal(b, 0);

                ASN1OctetString issuerNameHash = new DEROctetString(b);     
                AsymmetricKeyParameter issuerKey = issuerCert.getPublicKey();
           
                SubjectPublicKeyInfo info = SubjectPublicKeyInfoFactory.CreateSubjectPublicKeyInfo(issuerKey);

                b = info.getEncoded();
                digest.update(b,0,b.Length);

                b = new byte[digest.getDigestSize()];
                digest.doFinal(b,0);

                ASN1OctetString issuerKeyHash = new DEROctetString(b);
                DERInteger serialNumber = new DERInteger(number);
                this.id = new CertID(hashAlg,issuerNameHash,issuerKeyHash,serialNumber);
        }
        catch (Exception e)
        {
            throw new OCSPException("problem creating ID: " + e, e);
        }
    }


        /// <summary>
        /// 
        /// </summary>
        /// <returns>The hash algorithm id.</returns>
        public String getHashAlgOID()
        {
            return id.getHashAlgorithm().getObjectId().getId();
        }


        /// <summary>
        /// 
        /// </summary>
        /// <value>The Hash algorithm id.</value>
        public String HashAlgOID
        {
            get
            {
                return id.getHashAlgorithm().getObjectId().getId();
            }
        }



        /// <summary>
        /// 
        /// </summary>
        /// <returns>The issuer derial number.</returns>
        public byte[] getIssuerNameHash()
        {
            return id.getIssuerNameHash().getOctets();
        }


        /// <summary>
        /// 
        /// </summary>
        /// <value>Issuers Name Hash</value>
        public byte[] IssuerNameHash
        {
            get { return id.getIssuerNameHash().getOctets(); }
        }


        /// <summary>
        /// 
        /// </summary>
        /// <returns>The issuer key hash.</returns>
        public byte[] getIssuerKeyHash()
        {
            return id.getIssuerKeyHash().getOctets();
        }

    /// <summary>
    /// 
    /// </summary>
    /// <value>The issuer key hash.</value>
        public byte[] IssuerKeyHash
        {
            get
            {
                return id.getIssuerKeyHash().getOctets();
            }
        }



        /// <summary>
        /// 
        /// </summary>
        /// <returns>The serial number associated with this request.</returns>
        public BigInteger getSerialNumber()
        {
            return id.getSerialNumber().getValue();
        }


        /// <summary>
        /// 
        /// </summary>
        /// <value>Serial number value.</value>
        public BigInteger SerialNumber
        {
            get
            {
                return id.getSerialNumber().getValue();
            }
        }


        /// <summary>
        /// 
        /// </summary>
        /// <returns>An ASN1 object for this type. (CertID)</returns>
        public CertID toASN1Object()
        {
            return id;
        }


    /// <summary>
    ///  Is it equal.
    /// </summary>
    /// <param name="o"></param>
    /// <returns></returns>
        public override bool Equals(Object o)
        {
            if (!(o is CertificateID))
            {
                return false;
            }
            CertificateID   obj = (CertificateID)o;
            return id.toASN1Object().Equals(obj.id.toASN1Object());
        }


    /// <summary>
    /// 
    /// </summary>
    /// <returns>The hash code.</returns>
        public override int GetHashCode()
        {
            return id.toASN1Object().GetHashCode();
        }



    }
}
