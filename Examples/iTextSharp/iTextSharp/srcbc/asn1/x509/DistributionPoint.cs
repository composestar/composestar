using org.bouncycastle.asn1;

using System;

namespace org.bouncycastle.asn1.x509
{
    /**
     * The DistributionPoint object.
     * <pre>
     * DistributionPoint ::= SEQUENCE {
     *      distributionPoint [0] DistributionPointName OPTIONAL,
     *      reasons           [1] ReasonFlags OPTIONAL,
     *      cRLIssuer         [2] GeneralNames OPTIONAL
     * }
     * </pre>
     */
    public class DistributionPoint
        : ASN1Encodable
    {
        DistributionPointName       distributionPoint;
        ReasonFlags                 reasons;
        GeneralNames                cRLIssuer;
    
        public static DistributionPoint getInstance(
            ASN1TaggedObject obj,
            bool         explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public static DistributionPoint getInstance(
            object obj)
        {
            if(obj == null || obj is DistributionPoint) 
            {
                return (DistributionPoint)obj;
            }
            
            if(obj is ASN1Sequence) 
            {
                return new DistributionPoint((ASN1Sequence)obj);
            }
            
            throw new ArgumentException("Invalid DistributionPoint: " + obj.GetType().Name);
        }
    
        public DistributionPoint(
            ASN1Sequence seq)
        {
            for (int i = 0; i != seq.size(); i++)
            {
                ASN1TaggedObject    t = (ASN1TaggedObject)seq.getObjectAt(i);
                switch ((int) t.getTagNo())
                {
                case 0:
                    distributionPoint = DistributionPointName.getInstance(t, true);
                    break;
                case 1:
                    reasons = new ReasonFlags(DERBitString.getInstance(t, false));
                    break;
                case 2:
                    cRLIssuer = GeneralNames.getInstance(t, false);
                    break;
                }
            }
        }
        
        public DistributionPoint(
            DistributionPointName distributionPoint,
            ReasonFlags                 reasons,
            GeneralNames            cRLIssuer)
        {
            this.distributionPoint = distributionPoint;
            this.reasons = reasons;
            this.cRLIssuer = cRLIssuer;
        }
        
        public DistributionPointName getDistributionPoint()
        {
            return distributionPoint;
        }
    
        public ReasonFlags getReasons()
        {
            return reasons;
        }
        
        public GeneralNames getCRLIssuer()
        {
            return cRLIssuer;
        }
        
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
            
            if (distributionPoint != null)
            {
                //
                // as this is a CHOICE it must be explicitly tagged
                //
                v.add(new DERTaggedObject(0, distributionPoint));
            }
    
            if (reasons != null)
            {
                v.add(new DERTaggedObject(false, 1, reasons));
            }
    
            if (cRLIssuer != null)
            {
                v.add(new DERTaggedObject(false, 2, cRLIssuer));
            }
    
            return new DERSequence(v);
        }
    }
}
