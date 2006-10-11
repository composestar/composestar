using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.x509
{
    /**
     * PolicyQualifierId, used in the CertificatePolicies
     * X509V3 extension.
     * 
     * <pre>
     *    id-qt          OBJECT IDENTIFIER ::=  { id-pkix 2 }
     *    id-qt-cps      OBJECT IDENTIFIER ::=  { id-qt 1 }
     *    id-qt-unotice  OBJECT IDENTIFIER ::=  { id-qt 2 }
     *  PolicyQualifierId ::=
     *       OBJECT IDENTIFIER ( id-qt-cps | id-qt-unotice )
     * </pre>
     */
     public class PolicyQualifierId : DERObjectIdentifier
    {
       private const string id_qt = "1.3.6.1.5.5.7.2";
    
       private PolicyQualifierId(string id) : base(id)
          {
          }
       
       public static readonly PolicyQualifierId id_qt_cps =
           new PolicyQualifierId(id_qt + ".1");
       public static readonly PolicyQualifierId id_qt_unotice =
           new PolicyQualifierId(id_qt + ".2");
    }
}
