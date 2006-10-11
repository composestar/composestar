using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.x509
{
    public class PolicyInformation
        : ASN1Encodable
    {
        private DERObjectIdentifier   policyIdentifier;
        private ASN1Sequence          policyQualifiers;
    
        public PolicyInformation(
            ASN1Sequence seq)
        {
            policyIdentifier = (DERObjectIdentifier)seq.getObjectAt(0);
    
            if (seq.size() > 1)
            {
                policyQualifiers = (ASN1Sequence)seq.getObjectAt(1);
            }
        }
    
        public PolicyInformation(
            DERObjectIdentifier policyIdentifier)
        {
            this.policyIdentifier = policyIdentifier;
        }
    
        public PolicyInformation(
            DERObjectIdentifier policyIdentifier,
            ASN1Sequence        policyQualifiers)
        {
            this.policyIdentifier = policyIdentifier;
            this.policyQualifiers = policyQualifiers;
        }
    
        public static PolicyInformation getInstance(
            object obj)
        {
            if (obj == null || obj is PolicyInformation)
            {
                return (PolicyInformation)obj;
            }
    
            return new PolicyInformation(ASN1Sequence.getInstance(obj));
        }
    
        public DERObjectIdentifier getPolicyIdentifier()
        {
            return policyIdentifier;
        }
        
        public ASN1Sequence getPolicyQualifiers()
        {
            return policyQualifiers;
        }
        
        /* 
         * PolicyInformation ::= SEQUENCE {
         *      policyIdentifier   CertPolicyId,
         *      policyQualifiers   SEQUENCE SIZE (1..MAX) OF
         *              PolicyQualifierInfo OPTIONAL }
         */ 
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector v = new ASN1EncodableVector();
            
            v.add(policyIdentifier);
    
            if (policyQualifiers != null)
            {
                v.add(policyQualifiers);
            }
            
            return new DERSequence(v);
        }
    }
}
