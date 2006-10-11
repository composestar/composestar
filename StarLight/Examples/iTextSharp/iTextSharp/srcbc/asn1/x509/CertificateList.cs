using org.bouncycastle.asn1;

using System;

namespace org.bouncycastle.asn1.x509
{
    /**
     * PKIX RFC-2459
     *
     * The X.509 v2 CRL syntax is as follows.  For signature calculation,
     * the data that is to be signed is ASN.1 DER encoded.
     *
     * <pre>
     * CertificateList  ::=  SEQUENCE  {
     *      tbsCertList          TBSCertList,
     *      signatureAlgorithm   AlgorithmIdentifier,
     *      signatureValue       BIT STRING  }
     * </pre>
     */
    public class CertificateList
        : ASN1Encodable
    {
        TBSCertList            tbsCertList;
        AlgorithmIdentifier    sigAlgId;
        DERBitString           sig;
    
        public static CertificateList getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public static CertificateList getInstance(
            object  obj)
        {
            if (obj is CertificateList)
            {
                return (CertificateList)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new CertificateList((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public CertificateList(
            ASN1Sequence seq)
        {
            tbsCertList = TBSCertList.getInstance(seq.getObjectAt(0));
            sigAlgId = AlgorithmIdentifier.getInstance(seq.getObjectAt(1));
            sig = (DERBitString)seq.getObjectAt(2);
        }
    
        public TBSCertList getTBSCertList()
        {
            return tbsCertList;
        }
    
        public TBSCertList.CRLEntry[] getRevokedCertificates()
        {
            return tbsCertList.getRevokedCertificates();
        }
    
        public AlgorithmIdentifier getSignatureAlgorithm()
        {
            return sigAlgId;
        }
    
        public DERBitString getSignature()
        {
            return sig;
        }
    
        public int getVersion()
        {
            return tbsCertList.getVersion();
        }
    
        public X509Name getIssuer()
        {
            return tbsCertList.getIssuer();
        }
    
        public Time getThisUpdate()
        {
            return tbsCertList.getThisUpdate();
        }
    
        public Time getNextUpdate()
        {
            return tbsCertList.getNextUpdate();
        }
    
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector v = new ASN1EncodableVector();
    
            v.add(tbsCertList);
            v.add(sigAlgId);
            v.add(sig);
    
            return new DERSequence(v);
        }
    }
}
