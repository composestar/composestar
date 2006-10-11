using org.bouncycastle.asn1;

using System;

namespace org.bouncycastle.asn1.x509
{
    public class CRLDistPoint
        : ASN1Encodable
    {
        ASN1Sequence  seq = null;
    
        public static CRLDistPoint getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public static CRLDistPoint getInstance(
            object  obj)
        {
            if (obj is CRLDistPoint)
            {
                return (CRLDistPoint)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new CRLDistPoint((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public CRLDistPoint(
            ASN1Sequence seq)
        {
            this.seq = seq;
        }
        
        public CRLDistPoint(
            DistributionPoint[] points)
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            for (int i = 0; i != points.Length; i++)
            {
                v.add(points[i]);
            }
    
            seq = new DERSequence(v);
        }
    
        /**
         * Return the distribution points making up the sequence.
         * 
         * @return DistributionPoint[]
         */
        public DistributionPoint[] getDistributionPoints()
        {
            DistributionPoint[]    dp = new DistributionPoint[seq.size()];
            
            for (int i = 0; i != seq.size(); i++)
            {
                dp[i] = DistributionPoint.getInstance(seq.getObjectAt(i));
            }
            
            return dp;
        }
        
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * CRLDistPoint ::= SEQUENCE SIZE {1..MAX} OF DistributionPoint
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            return seq;
        }
    }
}
