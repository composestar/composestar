using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.x509
{
    public class AttributeCertificate
        : ASN1Encodable
    {
        AttributeCertificateInfo    acinfo;
        AlgorithmIdentifier         signatureAlgorithm;
        DERBitString                signatureValue;
    
        /**
         * @param obj
         * @return
         */
        public static AttributeCertificate getInstance(object obj)
        {
            if (obj is AttributeCertificate)
            {
                return (AttributeCertificate)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new AttributeCertificate((ASN1Sequence)obj);
            }
    
            throw new System.ArgumentException("unknown object in factory");
        }
        
        public AttributeCertificate(
            AttributeCertificateInfo    acinfo,
            AlgorithmIdentifier         signatureAlgorithm,
            DERBitString                signatureValue)
        {
            this.acinfo = acinfo;
            this.signatureAlgorithm = signatureAlgorithm;
            this.signatureValue = signatureValue;
        }
        
        public AttributeCertificate(
            ASN1Sequence    seq)
        {
            this.acinfo = AttributeCertificateInfo.getInstance(seq.getObjectAt(0));
            this.signatureAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(1));
            this.signatureValue = DERBitString.getInstance(seq.getObjectAt(2));
        }
        
        public AttributeCertificateInfo getAcinfo()
        {
            return acinfo;
        }
    
        public AlgorithmIdentifier getSignatureAlgorithm()
        {
            return signatureAlgorithm;
        }

        public DERBitString getSignatureValue()
        {
            return signatureValue;
        }

        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         *  AttributeCertificate ::= SEQUENCE {
         *       acinfo               AttributeCertificateInfo,
         *       signatureAlgorithm   AlgorithmIdentifier,
         *       signatureValue       BIT STRING
         *  }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(acinfo);
            v.add(signatureAlgorithm);
            v.add(signatureValue);
    
            return new DERSequence(v);
        }
    }
}
