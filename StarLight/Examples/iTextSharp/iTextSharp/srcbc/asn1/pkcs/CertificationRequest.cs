using org.bouncycastle.asn1.x509;

namespace org.bouncycastle.asn1.pkcs
{
    /**
     * PKCS10 Certfication request object.
     * <pre>
     * CertificationRequest ::= SEQUENCE {
     *   certificationRequestInfo  CertificationRequestInfo,
     *   signatureAlgorithm        AlgorithmIdentifier{{ SignatureAlgorithms }},
     *   signature                 BIT STRING
     * }
     * </pre>
     */
    public class CertificationRequest
        : ASN1Encodable
    {
        protected CertificationRequestInfo reqInfo = null;
        protected AlgorithmIdentifier sigAlgId = null;
        protected DERBitString sigBits = null;
    
        protected CertificationRequest()
        {
        }
    
        public CertificationRequest(
            CertificationRequestInfo requestInfo,
            AlgorithmIdentifier     algorithm,
            DERBitString            signature)
        {
            this.reqInfo = requestInfo;
            this.sigAlgId = algorithm;
            this.sigBits = signature;
        }
    
        public CertificationRequest(
            ASN1Sequence seq)
        {
            reqInfo = CertificationRequestInfo.getInstance(seq.getObjectAt(0));
            sigAlgId = AlgorithmIdentifier.getInstance(seq.getObjectAt(1));
            sigBits = (DERBitString)seq.getObjectAt(2);
        }
    
        public CertificationRequestInfo getCertificationRequestInfo()
        {
            return reqInfo;
        }
    
        public AlgorithmIdentifier getSignatureAlgorithm()
        {
            return sigAlgId;
        }
    
        public DERBitString getSignature()
        {
            return sigBits;
        }
    
        public override ASN1Object toASN1Object()
        {
            // Construct the CertificateRequest
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(reqInfo);
            v.add(sigAlgId);
            v.add(sigBits);
    
            return new DERSequence(v);
        }
    }
}
