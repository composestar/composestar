using org.bouncycastle.asn1;

using System;

namespace org.bouncycastle.asn1.x509
{
    /**
     * IssuingDistributionPoint ::= SEQUENCE {
     *      distributionPoint          [0] DistributionPointName OPTIONAL,
     *      onlyContainsUserCerts      [1] BOOLEAN DEFAULT FALSE,
     *      onlyContainsCACerts        [2] BOOLEAN DEFAULT FALSE,
     *      onlySomeReasons            [3] ReasonFlags OPTIONAL,
     *      indirectCRL                [4] BOOLEAN DEFAULT FALSE,
     *      onlyContainsAttributeCerts [5] BOOLEAN DEFAULT FALSE }
     */
    public class IssuingDistributionPoint
        : ASN1Encodable
    {
        private bool         _onlyContainsUserCerts;
        private bool         _onlyContainsCACerts;
        private bool         _indirectCRL;
        private bool         _onlyContainsAttributeCerts;
    
        private ASN1Sequence    seq;
    
        public static IssuingDistributionPoint getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public static IssuingDistributionPoint getInstance(
            object  obj)
        {
            if (obj == null || obj is IssuingDistributionPoint)
            {
                return (IssuingDistributionPoint)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new IssuingDistributionPoint((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        /**
         * Constructor from ASN1Sequence
         */
        public IssuingDistributionPoint(
            ASN1Sequence  seq)
        {
            this.seq = seq;
    
            for (int i = 0; i != seq.size(); i++)
            {
                ASN1TaggedObject  o = (ASN1TaggedObject)seq.getObjectAt(i);
    
                switch ((int) o.getTagNo())
                {
                case 0:
                    break;
                case 1:
                    _onlyContainsUserCerts = DERBoolean.getInstance(o, false).isTrue();
                    break;
                case 2:
                    _onlyContainsCACerts = DERBoolean.getInstance(o, false).isTrue();
                    break;
                case 3:
                    break;
                case 4:
                    _indirectCRL = DERBoolean.getInstance(o, false).isTrue();
                    break;
                case 5:
                    _onlyContainsAttributeCerts = DERBoolean.getInstance(o, false).isTrue();
                    break;
                default:
                    throw new ArgumentException("unknown tag in IssuingDistributionPoint");
                }
            }
        }
    
        public bool onlyContainsUserCerts()
        {
            return _onlyContainsUserCerts;
        }
    
        public bool onlyContainsCACerts()
        {
            return _onlyContainsCACerts;
        }
    
        public bool isIndirectCRL()
        {
            return _indirectCRL;
        }
    
        public bool onlyContainsAttributeCerts()
        {
            return _onlyContainsAttributeCerts;
        }
    
        public override ASN1Object toASN1Object()
        {
            return seq;
        }
    }
}
