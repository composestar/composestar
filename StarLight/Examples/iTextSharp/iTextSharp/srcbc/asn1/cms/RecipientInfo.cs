using org.bouncycastle.asn1;

using System;

namespace org.bouncycastle.asn1.cms
{
    public class RecipientInfo
        : ASN1Encodable
    {
        ASN1Encodable    info;
    
        public RecipientInfo(
            KeyTransRecipientInfo info)
        {
            this.info = info;
        }
    
        public RecipientInfo(
            KeyAgreeRecipientInfo info)
        {
            this.info = new DERTaggedObject(true, 1, info);
        }
    
        public RecipientInfo(
            KEKRecipientInfo info)
        {
            this.info = new DERTaggedObject(true, 2, info);
        }
    
        public RecipientInfo(
            PasswordRecipientInfo info)
        {
            this.info = new DERTaggedObject(true, 3, info);
        }
    
        public RecipientInfo(
            OtherRecipientInfo info)
        {
            this.info = new DERTaggedObject(true, 4, info);
        }
    
        public RecipientInfo(
            ASN1Object   info)
        {
            this.info = info;
        }
    
        public static RecipientInfo getInstance(
            object  o)
        {
            if (o == null || o is RecipientInfo)
            {
                return (RecipientInfo)o;
            }
            else if (o is ASN1Sequence)
            {
                return new RecipientInfo((ASN1Sequence)o);
            }
            else if (o is ASN1TaggedObject)
            {
                return new RecipientInfo((ASN1TaggedObject)o);
            }
    
            throw new ArgumentException("unknown object in factory: "
                                                        + o.GetType().Name);
        }
    
        public DERInteger getVersion()
        {
            if (info is ASN1TaggedObject)
            {
                ASN1TaggedObject o = (ASN1TaggedObject)info;
    
                switch ((int) o.getTagNo())
                {
                case 1:
                    return KeyAgreeRecipientInfo.getInstance(o, true).getVersion();
                case 2:
                    return KEKRecipientInfo.getInstance(o, true).getVersion();
                case 3:
                    return PasswordRecipientInfo.getInstance(o, true).getVersion();
                case 4:
                    return new DERInteger(0);    // no syntax version for OtherRecipientInfo
                default:
                    throw new InvalidOperationException("unknown tag");
                }
            }
    
            return KeyTransRecipientInfo.getInstance(info).getVersion();
        }
    
        public bool isTagged()
        {
            return (info is ASN1TaggedObject);
        }
    
        public ASN1Encodable getInfo()
        {
            if (info is ASN1TaggedObject)
            {
                ASN1TaggedObject o = (ASN1TaggedObject)info;
    
                switch ((int) o.getTagNo())
                {
                case 1:
                    return KeyAgreeRecipientInfo.getInstance(o, true);
                case 2:
                    return KEKRecipientInfo.getInstance(o, true);
                case 3:
                    return PasswordRecipientInfo.getInstance(o, true);
                case 4:
                    return OtherRecipientInfo.getInstance(o, true);
                default:
                    throw new InvalidOperationException("unknown tag");
                }
            }
    
            return KeyTransRecipientInfo.getInstance(info);
        }
    
        /** 
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * RecipientInfo ::= CHOICE {
         *     ktri KeyTransRecipientInfo,
         *     kari [1] KeyAgreeRecipientInfo,
         *     kekri [2] KEKRecipientInfo,
         *     pwri [3] PasswordRecipientInfo,
         *     ori [4] OtherRecipientInfo }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            return info.toASN1Object();
        }
    }
}
