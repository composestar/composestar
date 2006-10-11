using org.bouncycastle.asn1;

using System.Collections;

namespace org.bouncycastle.asn1.x509
{
    public class NameConstraints
        : ASN1Encodable
    {
        ASN1Sequence    permitted, excluded;
    
        public NameConstraints(
            ASN1Sequence    seq)
        {
            IEnumerator e = seq.getObjects();
            while (e.MoveNext())
            {
                ASN1TaggedObject    o = (ASN1TaggedObject)e.Current;
                switch ((int) o.getTagNo())
                {
                case 0:
                    permitted = ASN1Sequence.getInstance(o, false);
                    break;
                case 1:
                    excluded = ASN1Sequence.getInstance(o, false);
                    break;
                }
            }
        }
    
        public ASN1Sequence getPermittedSubtrees()
        {
            return permitted;
        }
    
        public ASN1Sequence getExcludedSubtrees()
        {
            return excluded;
        }
    
        /*
         * NameConstraints ::= SEQUENCE {
         *      permittedSubtrees       [0]     GeneralSubtrees OPTIONAL,
         *      excludedSubtrees        [1]     GeneralSubtrees OPTIONAL }
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector   v = new ASN1EncodableVector();
    
            if (permitted != null)
            {
                v.add(new DERTaggedObject(false, 0, permitted));
            }
    
            if (excluded != null)
            {
                v.add(new DERTaggedObject(false, 1, excluded));
            }
    
            return new DERSequence(v);
        }
    }
}
