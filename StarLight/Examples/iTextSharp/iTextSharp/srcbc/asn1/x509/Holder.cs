using System;

using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.x509
{
    public class Holder
        : ASN1Encodable
    {
        IssuerSerial        baseCertificateID;
        GeneralNames        entityName;
        ObjectDigestInfo    objectDigestInfo;
    
        public static Holder getInstance(
                object  obj)
        {
            if (obj is Holder)
            {
                return (Holder)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new Holder((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public Holder(
            ASN1Sequence    seq)
        {
            for (int i = 0; i != seq.size(); i++)
            {
                ASN1TaggedObject    tObj = (ASN1TaggedObject)seq.getObjectAt(i);
                
                switch (tObj.getTagNo())
                {
                case 0:
                    baseCertificateID = IssuerSerial.getInstance(tObj, false);
                    break;
                case 1:
                    entityName = GeneralNames.getInstance(tObj, false);
                    break;
                case 2:
                    objectDigestInfo = ObjectDigestInfo.getInstance(tObj, false);
                    break;
                default:
                    throw new ArgumentException("unknown tag in Holder");
                }
            }
        }
        
        public IssuerSerial getBaseCertificateID()
        {
            return baseCertificateID;
        }
        
        public GeneralNames getEntityName()
        {
            return entityName;
        }
        
        public ObjectDigestInfo getObjectDigestInfo()
        {
            return objectDigestInfo;
        }
        
        /**
         * The Holder object.
         * <pre>
         *  Holder ::= SEQUENCE {
         *        baseCertificateID   [0] IssuerSerial OPTIONAL,
         *                 -- the issuer and serial number of
         *                 -- the holder's Public Key Certificate
         *        entityName          [1] GeneralNames OPTIONAL,
         *                 -- the name of the claimant or role
         *        objectDigestInfo    [2] ObjectDigestInfo OPTIONAL
         *                 -- used to directly authenticate the holder,
         *                 -- for example, an executable
         *  }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            if (baseCertificateID != null)
            {
                v.add(new DERTaggedObject(false, 0, baseCertificateID));
            }
    
            if (entityName != null)
            {
                v.add(new DERTaggedObject(false, 1, entityName));
            }
    
            if (objectDigestInfo != null)
            {
                v.add(new DERTaggedObject(false, 2, objectDigestInfo));
            }
    
            return new DERSequence(v);
        }
    }
}
