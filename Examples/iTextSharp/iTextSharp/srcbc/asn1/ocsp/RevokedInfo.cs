using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;

using System;

namespace org.bouncycastle.asn1.ocsp
{
    public class RevokedInfo
        : ASN1Encodable
    {
        private DERGeneralizedTime  revocationTime;
        private CRLReason           revocationReason;
    
        public RevokedInfo(
            DERGeneralizedTime  revocationTime,
            CRLReason           revocationReason)
        {
            this.revocationTime = revocationTime;
            this.revocationReason = revocationReason;
        }
    
        public RevokedInfo(
            ASN1Sequence    seq)
        {
            this.revocationTime = (DERGeneralizedTime)seq.getObjectAt(0);
    
            if (seq.size() > 1)
            {
                this.revocationReason = new CRLReason(DEREnumerated.getInstance(
                                    (ASN1TaggedObject)seq.getObjectAt(1), true));
            }
        }
    
        public static RevokedInfo getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public static RevokedInfo getInstance(
            object  obj)
        {
            if (obj == null || obj is RevokedInfo)
            {
                return (RevokedInfo)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new RevokedInfo((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public DERGeneralizedTime getRevocationTime()
        {
            return revocationTime;
        }
    
        public CRLReason getRevocationReason()
        {
            return revocationReason;
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * RevokedInfo ::= SEQUENCE {
         *      revocationTime              GeneralizedTime,
         *      revocationReason    [0]     EXPLICIT CRLReason OPTIONAL }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector v = new ASN1EncodableVector();
    
            v.add(revocationTime);
            if (revocationReason != null)
            {
                v.add(new DERTaggedObject(true, 0, revocationReason));
            }
    
            return new DERSequence(v);
        }
    }
}
