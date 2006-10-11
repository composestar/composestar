using org.bouncycastle.asn1;
using org.bouncycastle.math;

using System;

namespace org.bouncycastle.asn1.x509
{
    public class GeneralSubtree
        : ASN1Encodable
    {
        private GeneralName _base;
        private DERInteger minimum;
        private DERInteger maximum;
    
        public GeneralSubtree(
            ASN1Sequence seq)
        {
            _base = GeneralName.getInstance(seq.getObjectAt(0));
            
            switch (seq.size())
            {
            case 1:
                break;
            case 2:
                ASN1TaggedObject o = (ASN1TaggedObject)seq.getObjectAt(1);
                switch ((int) o.getTagNo())
                {
                case 0 :
                    minimum = DERInteger.getInstance(o, false);
                    break;
                case 1 :
                    maximum = DERInteger.getInstance(o, false);
                    break;
                default:
                    throw new ArgumentException("Bad tag number: " + o.getTagNo());
                }
                break;
            case 3 :
                minimum = DERInteger.getInstance((ASN1TaggedObject)seq.getObjectAt(1), false);
                maximum = DERInteger.getInstance((ASN1TaggedObject)seq.getObjectAt(2), false);
                break;
            default:
                throw new ArgumentException("Bad sequence size: " + seq.size());
            }
        }
    
        public static GeneralSubtree getInstance(
            ASN1TaggedObject    o,
            bool             explicitly)
        {
            return new GeneralSubtree(ASN1Sequence.getInstance(o, explicitly));
        }
    
        public static GeneralSubtree getInstance(
            object obj)
        {
            if (obj == null)
            {
                return null;
            }
    
            if (obj is GeneralSubtree)
            {
                return (GeneralSubtree)obj;
            }
    
            return new GeneralSubtree(ASN1Sequence.getInstance(obj));
        }
    
        public GeneralName getBase()
        {
            return _base;
        }
        
        public BigInteger getMinimum()
        {
            if (minimum == null)
            {
                return BigInteger.valueOf(0);
            }
            
            return minimum.getValue();
        }
        
        public BigInteger getMaximum()
        {
            if (maximum == null)
            {
                return null;
            }
    
            return maximum.getValue();
        }
        
        /* 
         * GeneralSubtree ::= SEQUENCE {
         *      base                    GeneralName,
         *      minimum         [0]     BaseDistance DEFAULT 0,
         *      maximum         [1]     BaseDistance OPTIONAL }
         */ 
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector v = new ASN1EncodableVector();
    
            v.add(_base);
            
            if (minimum != null)
            {
                v.add(new DERTaggedObject(false, 0, minimum));
            }
            
            if (maximum != null)
            {
                v.add(new DERTaggedObject(false, 1, maximum));
            }
    
            return new DERSequence(v);
        }
    }
}
