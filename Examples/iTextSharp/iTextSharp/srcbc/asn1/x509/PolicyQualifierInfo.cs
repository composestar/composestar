using org.bouncycastle.asn1;

using System;

namespace org.bouncycastle.asn1.x509
{
    /**
     * Policy qualifiers, used in the X509V3 CertificatePolicies
     * extension.
     * 
     * <pre>
     *   PolicyQualifierInfo ::= SEQUENCE {
     *       policyQualifierId  PolicyQualifierId,
     *       qualifier          ANY DEFINED BY policyQualifierId }
     * </pre>
     */
    public class PolicyQualifierInfo
        : ASN1Encodable
    {
       DERObjectIdentifier policyQualifierId;
       ASN1Encodable qualifier;
    
       /**
        * Creates a new <code>PolicyQualifierInfo</code> instance.
        *
        * @param policyQualifierId a <code>PolicyQualifierId</code> value
        * @param qualifier the qualifier, defined by the above field.
        */
       public PolicyQualifierInfo (DERObjectIdentifier policyQualifierId,
                                   ASN1Encodable qualifier) 
       {
          this.policyQualifierId = policyQualifierId;
          this.qualifier = qualifier;
       }
    
       /**
        * Creates a new <code>PolicyQualifierInfo</code> containing a
        * cPSuri qualifier.
        *
        * @param cps the CPS (certification practice statement) uri as a
        * <code>String</code>.
        */
       public PolicyQualifierInfo (String cps) 
       {
          policyQualifierId = PolicyQualifierId.id_qt_cps;
          qualifier = new DERIA5String (cps);
       }
    
       /**
        * Creates a new <code>PolicyQualifierInfo</code> instance.
        *
        * @param as <code>PolicyQualifierInfo</code> X509 structure
        * encoded as an ASN1Sequence. 
        */
       public PolicyQualifierInfo (ASN1Sequence aseq)
       {
            policyQualifierId = (DERObjectIdentifier) aseq.getObjectAt(0);
            qualifier = aseq.getObjectAt(1);
        }
    
       public static PolicyQualifierInfo getInstance (object aseq) 
       {
            if (aseq is PolicyQualifierInfo)
            {
                return (PolicyQualifierInfo)aseq;
            }
            else if (aseq is ASN1Sequence)
            {
                return new PolicyQualifierInfo((ASN1Sequence)aseq);
            }
    
            throw new ArgumentException("unknown object in getInstance.");
       }
    
       /**
        * Returns a DER-encodable representation of this instance. 
        *
        * @return a <code>ASN1Object</code> value
        */
       public override ASN1Object toASN1Object() 
       {
          ASN1EncodableVector dev = new ASN1EncodableVector();
          dev.add(policyQualifierId);
          dev.add(qualifier);
    
          return new DERSequence(dev);
       }
    }
}
