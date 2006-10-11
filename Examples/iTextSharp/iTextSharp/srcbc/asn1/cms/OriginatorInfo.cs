using org.bouncycastle.asn1;

using System;

namespace org.bouncycastle.asn1.cms
{
    public class OriginatorInfo
        : ASN1Encodable
    {
        private ASN1Set certs;
        private ASN1Set crls;
        
        public OriginatorInfo(
            ASN1Set certs,
            ASN1Set crls)
        {
            this.certs = certs;
            this.crls = crls;
        }
        
        public OriginatorInfo(
            ASN1Sequence seq)
        {
            switch (seq.size())
            {
            case 0:     // empty
                break;
            case 1:
                ASN1TaggedObject o = (ASN1TaggedObject)seq.getObjectAt(0);
                switch ((int)o.getTagNo())
                {
                case 0 :
                    certs = ASN1Set.getInstance(o, false);
                    break;
                case 1 :
                    crls = ASN1Set.getInstance(o, false);
                    break;
                default:
                    throw new ArgumentException("Bad tag in OriginatorInfo: " + o.getTagNo());
                }
                break;
            case 2:
                certs = ASN1Set.getInstance((ASN1TaggedObject)seq.getObjectAt(0), false);
                crls  = ASN1Set.getInstance((ASN1TaggedObject)seq.getObjectAt(1), false);
                break;
            default:
                throw new ArgumentException("OriginatorInfo too big");
            }
        }
        
        /**
         * return an OriginatorInfo object from a tagged object.
         *
         * @param obj the tagged object holding the object we want.
         * @param explicit true if the object is meant to be explicitly
         *              tagged false otherwise.
         * @exception IllegalArgumentException if the object held by the
         *          tagged object cannot be converted.
         */
        public static OriginatorInfo getInstance(
            ASN1TaggedObject    obj,
            bool             explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
        
        /**
         * return an OriginatorInfo object from the given object.
         *
         * @param obj the object we want converted.
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static OriginatorInfo getInstance(
            object obj)
        {
            if (obj == null || obj is OriginatorInfo)
            {
                return (OriginatorInfo)obj;
            }
            
            if (obj is ASN1Sequence)
            {
                return new OriginatorInfo((ASN1Sequence)obj);
            }
            
            throw new ArgumentException("Invalid OriginatorInfo: " + obj.GetType().Name);
        }
        
        public ASN1Set getCertificates()
        {
            return certs;
        }
    
        public ASN1Set getCRLs()
        {
            return crls;
        }
    
        /** 
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * OriginatorInfo ::= SEQUENCE {
         *     certs [0] IMPLICIT CertificateSet OPTIONAL,
         *     crls [1] IMPLICIT CertificateRevocationLists OPTIONAL 
         * }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            if (certs != null)
            {
                v.add(new DERTaggedObject(false, 0, certs));
            }
            
            if (crls != null)
            {
                v.add(new DERTaggedObject(false, 1, crls));
            }
            
            return new DERSequence(v);
        }
    }
}
