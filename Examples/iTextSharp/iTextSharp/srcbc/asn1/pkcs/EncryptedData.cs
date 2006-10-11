using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;

using System;

namespace org.bouncycastle.asn1.pkcs
{
    /**
     * The EncryptedData object.
     * <pre>
     *      EncryptedData ::= SEQUENCE {
     *           version Version,
     *           encryptedContentInfo EncryptedContentInfo
     *      }
     *
     *
     *      EncryptedContentInfo ::= SEQUENCE {
     *          contentType ContentType,
     *          contentEncryptionAlgorithm  ContentEncryptionAlgorithmIdentifier,
     *          encryptedContent [0] IMPLICIT EncryptedContent OPTIONAL
     *    }
     *
     *    EncryptedContent ::= OCTET STRING
     * </pre>
     */
    public class EncryptedData
        : ASN1Encodable
    {
        internal ASN1Sequence                data;
//        internal DERObjectIdentifier         bagId;
//        internal ASN1Object                   bagValue;
    
        public static EncryptedData getInstance(
             object  obj)
        {
             if (obj is EncryptedData)
             {
                 return (EncryptedData)obj;
             }
             else if (obj is ASN1Sequence)
             {
                 return new EncryptedData((ASN1Sequence)obj);
             }
    
             throw new ArgumentException("unknown object in factory");
        }
         
        public EncryptedData(
            ASN1Sequence seq)
        {
            int version = ((DERInteger)seq.getObjectAt(0)).getValue().intValue();
    
            if (version != 0)
            {
                throw new ArgumentException("sequence not version 0");
            }
    
            this.data = (ASN1Sequence)seq.getObjectAt(1);
        }
    
        public EncryptedData(
            DERObjectIdentifier     contentType,
            AlgorithmIdentifier     encryptionAlgorithm,
            ASN1Encodable            content)
        {
            ASN1EncodableVector v = new ASN1EncodableVector();
    
            v.add(contentType);
            v.add(encryptionAlgorithm.toASN1Object());
            v.add(new BERTaggedObject(false, 0, content));
    
            data = new BERSequence(v);
        }
            
        public DERObjectIdentifier getContentType()
        {
            return (DERObjectIdentifier)data.getObjectAt(0);
        }
    
        public AlgorithmIdentifier getEncryptionAlgorithm()
        {
            return AlgorithmIdentifier.getInstance(data.getObjectAt(1));
        }
    
        public ASN1OctetString getContent()
        {
            if (data.size() == 3)
            {
                DERTaggedObject o = (DERTaggedObject)data.getObjectAt(2);
    
                return ASN1OctetString.getInstance(o.getObject());
            }
    
            return null;
        }
    
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(new DERInteger(0));
            v.add(data);
    
            return new BERSequence(v);
        }
    }
}
