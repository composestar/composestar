using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.x509
{
    /**
     * CertPolicyId, used in the CertificatePolicies and PolicyMappings
     * X509V3 Extensions.
     *
     * <pre>
     *     CertPolicyId ::= OBJECT IDENTIFIER
     * </pre>
     */
     public class CertPolicyId : DERObjectIdentifier
    {
       public CertPolicyId (string id) : base(id)
       {
       }
    }
}
