using org.bouncycastle.asn1;
using org.bouncycastle.asn1.pkcs;

using System;

namespace org.bouncycastle.asn1.x509
{
    /**
     * an X509Certificate structure.
     * <pre>
     *  Certificate ::= SEQUENCE {
     *      tbsCertificate          TBSCertificate,
     *      signatureAlgorithm      AlgorithmIdentifier,
     *      signature               BIT STRING
     *  }
     * </pre>
     */
    public class X509CertificateStructure
        : ASN1Encodable //, X509ObjectIdentifiers, PKCSObjectIdentifiers
    {
        ASN1Sequence  seq;
        TBSCertificateStructure tbsCert;
        AlgorithmIdentifier     sigAlgId;
        DERBitString            sig;
    
        public static X509CertificateStructure getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public static X509CertificateStructure getInstance(
            object  obj)
        {
            if (obj is X509CertificateStructure)
            {
                return (X509CertificateStructure)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new X509CertificateStructure((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public X509CertificateStructure(
            ASN1Sequence  seq)
        {
            this.seq = seq;
    
            //
            // correct x509 certficate
            //
            if (seq.size() == 3)
            {
                tbsCert = TBSCertificateStructure.getInstance(seq.getObjectAt(0));
                sigAlgId = AlgorithmIdentifier.getInstance(seq.getObjectAt(1));
    
                sig = (DERBitString)seq.getObjectAt(2);
            }
        }
    
        public TBSCertificateStructure getTBSCertificate()
        {
            return tbsCert;
        }
    
        public int getVersion()
        {
            return tbsCert.getVersion();
        }
    
        public DERInteger getSerialNumber()
        {
            return tbsCert.getSerialNumber();
        }
    
        public X509Name getIssuer()
        {
            return tbsCert.getIssuer();
        }
    
        public Time getStartDate()
        {
            return tbsCert.getStartDate();
        }
    
        public Time getEndDate()
        {
            return tbsCert.getEndDate();
        }
    
        public X509Name getSubject()
        {
            return tbsCert.getSubject();
        }
    
        public SubjectPublicKeyInfo getSubjectPublicKeyInfo()
        {
            return tbsCert.getSubjectPublicKeyInfo();
        }
    
        public AlgorithmIdentifier getSignatureAlgorithm()
        {
            return sigAlgId;
        }
    
        public DERBitString getSignature()
        {
            return sig;
        }
    
        public override ASN1Object toASN1Object()
        {
            return seq;
        }
    }
}
