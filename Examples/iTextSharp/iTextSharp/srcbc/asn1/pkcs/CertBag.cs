using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.pkcs
{
    public class CertBag
        : ASN1Encodable
    {
        ASN1Sequence        seq;
        DERObjectIdentifier certId;
        ASN1Object           certValue;
    
        public CertBag(
            ASN1Sequence    seq)
        {
            this.seq = seq;
            this.certId = (DERObjectIdentifier)seq.getObjectAt(0);
            this.certValue = ((DERTaggedObject)seq.getObjectAt(1)).getObject();
        }
    
        public CertBag(
            DERObjectIdentifier certId,
            ASN1Object           certValue)
        {
            this.certId = certId;
            this.certValue = certValue;
        }
    
        public DERObjectIdentifier getCertId()
        {
            return certId;
        }
    
        public ASN1Object getCertValue()
        {
            return certValue;
        }
    
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(certId);
            v.add(new DERTaggedObject(0, certValue));
    
            return new DERSequence(v);
        }
    }
}
