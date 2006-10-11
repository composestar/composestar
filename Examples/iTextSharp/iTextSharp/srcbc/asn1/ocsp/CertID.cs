using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;

using System;

namespace org.bouncycastle.asn1.ocsp
{
    public class CertID
        : ASN1Encodable
    {
        AlgorithmIdentifier    hashAlgorithm;
        ASN1OctetString        issuerNameHash;
        ASN1OctetString        issuerKeyHash;
        DERInteger             serialNumber;
    
        public CertID(
            AlgorithmIdentifier hashAlgorithm,
            ASN1OctetString     issuerNameHash,
            ASN1OctetString     issuerKeyHash,
            DERInteger          serialNumber)
        {
            this.hashAlgorithm = hashAlgorithm;
            this.issuerNameHash = issuerNameHash;
            this.issuerKeyHash = issuerKeyHash;
            this.serialNumber = serialNumber;
        }
    
        public CertID(
            ASN1Sequence    seq)
        {
            hashAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(0));
            issuerNameHash = (ASN1OctetString)seq.getObjectAt(1);
            issuerKeyHash = (ASN1OctetString)seq.getObjectAt(2);
            serialNumber = (DERInteger)seq.getObjectAt(3);
        }
    
        public static CertID getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public static CertID getInstance(
            object  obj)
        {
            if (obj == null || obj is CertID)
            {
                return (CertID)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new CertID((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public AlgorithmIdentifier getHashAlgorithm()
        {
            return hashAlgorithm;
        }
    
        public ASN1OctetString getIssuerNameHash()
        {
            return issuerNameHash;
        }
    
        public ASN1OctetString getIssuerKeyHash()
        {
            return issuerKeyHash;
        }
    
        public DERInteger getSerialNumber()
        {
            return serialNumber;
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * CertID          ::=     SEQUENCE {
         *     hashAlgorithm       AlgorithmIdentifier,
         *     issuerNameHash      OCTET STRING, -- Hash of Issuer's DN
         *     issuerKeyHash       OCTET STRING, -- Hash of Issuers public key
         *     serialNumber        CertificateSerialNumber }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector    v = new ASN1EncodableVector();
    
            v.add(hashAlgorithm);
            v.add(issuerNameHash);
            v.add(issuerKeyHash);
            v.add(serialNumber);
    
            return new DERSequence(v);
        }
    }
}
