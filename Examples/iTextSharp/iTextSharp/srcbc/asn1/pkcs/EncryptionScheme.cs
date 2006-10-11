using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;

namespace org.bouncycastle.asn1.pkcs
{
    public class EncryptionScheme
        : AlgorithmIdentifier
    {   
        ASN1Object   objectId;
        ASN1Object   obj;
    
        internal EncryptionScheme(ASN1Sequence  seq) : base(seq)
        {   
            objectId = (ASN1Object)seq.getObjectAt(0);
            obj = (ASN1Object)seq.getObjectAt(1);
        }
    
        public ASN1Object getObject()
        {
            return obj;
        }
    
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(objectId);
            v.add(obj);
    
            return new DERSequence(v);
        }
    }
}
