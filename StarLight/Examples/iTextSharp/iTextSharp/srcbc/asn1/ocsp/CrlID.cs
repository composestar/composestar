using org.bouncycastle.asn1;

using System;
using System.Collections;

namespace org.bouncycastle.asn1.ocsp
{
    public class CrlID
        : ASN1Encodable
    {
        DERIA5String        crlUrl;
        DERInteger          crlNum;
        DERGeneralizedTime  crlTime;
    
        public CrlID(
            ASN1Sequence    seq)
        {
            IEnumerator    e = seq.getObjects();
    
            while (e.MoveNext())
            {
                ASN1TaggedObject    o = (ASN1TaggedObject)e.Current;
    
                switch ((int)o.getTagNo())
                {
                case 0:
                    crlUrl = DERIA5String.getInstance(o, true);
                    break;
                case 1:
                    crlNum = DERInteger.getInstance(o, true);
                    break;
                case 2:
                    crlTime = DERGeneralizedTime.getInstance(o, true);
                    break;
                default:
                    throw new ArgumentException(
                            "unknown tag number: " + o.getTagNo());
                }
            }
        }
    
        public DERIA5String getCrlUrl()
        {
            return crlUrl;
        }
    
        public DERInteger getCrlNum()
        {
            return crlNum;
        }
    
        public DERGeneralizedTime getCrlTime()
        {
            return crlTime;
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * CrlID ::= SEQUENCE {
         *     crlUrl               [0]     EXPLICIT IA5String OPTIONAL,
         *     crlNum               [1]     EXPLICIT INTEGER OPTIONAL,
         *     crlTime              [2]     EXPLICIT GeneralizedTime OPTIONAL }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector    v = new ASN1EncodableVector();
    
            if (crlUrl != null)
            {
                v.add(new DERTaggedObject(true, 0, crlUrl));
            }
    
            if (crlNum != null)
            {
                v.add(new DERTaggedObject(true, 1, crlNum));
            }
    
            if (crlTime != null)
            {
                v.add(new DERTaggedObject(true, 2, crlTime));
            }
    
            return new DERSequence(v);
        }
    }
}
