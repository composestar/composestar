using org.bouncycastle.asn1;

using System;

namespace org.bouncycastle.asn1.ocsp
{
    public class CertStatus
        : ASN1Encodable
    {
        private int             tagNo;
        private ASN1Encodable   value;
    
        /**
         * create a CertStatus object with a tag of zero.
         */
        public CertStatus()
        {
            tagNo = 0;
            value = new DERNull();
        }
    
        public CertStatus(
            RevokedInfo info)
        {
            tagNo = 1;
            value = info;
        }
    
        public CertStatus(
            int            tagNo,
            ASN1Encodable  value)
        {
            this.tagNo = tagNo;
            this.value = value;
        }
    
        public CertStatus(
            ASN1TaggedObject    choice)
        {
            this.tagNo = choice.getTagNo();
    
            switch ((int) choice.getTagNo())
            {
            case 0:
                value = new DERNull();
                break;
            case 1:
                value = RevokedInfo.getInstance(choice, false);
                break;
            case 2:
                value = new DERNull();
                break;
            }
        }
    
        public static CertStatus getInstance(
            object  obj)
        {
            if (obj == null || obj is CertStatus)
            {
                return (CertStatus)obj;
            }
            else if (obj is ASN1TaggedObject)
            {
                return new CertStatus((ASN1TaggedObject)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public int getTagNo()
        {
            return tagNo;
        }
    
        public ASN1Encodable getStatus()
        {
            return value;
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         *  CertStatus ::= CHOICE {
         *                  good        [0]     IMPLICIT NULL,
         *                  revoked     [1]     IMPLICIT RevokedInfo,
         *                  unknown     [2]     IMPLICIT UnknownInfo }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            return new DERTaggedObject(false, tagNo, value);
        }
    }
}
