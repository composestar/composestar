using org.bouncycastle.asn1;
using org.bouncycastle.math;

using System.Collections;

namespace org.bouncycastle.asn1.oiw
{
    public class ElGamalParameter
        : ASN1Encodable
    {
        DERInteger      p, g;
    
        public ElGamalParameter(
            BigInteger  p,
            BigInteger  g)
        {
            this.p = new DERInteger(p);
            this.g = new DERInteger(g);
        }
    
        public ElGamalParameter(
            ASN1Sequence  seq)
        {
            IEnumerator     e = seq.getObjects();

            e.MoveNext();
            p = (DERInteger)e.Current;
            e.MoveNext();
            g = (DERInteger)e.Current;
        }
    
        public BigInteger getP()
        {
            return p.getPositiveValue();
        }
    
        public BigInteger getG()
        {
            return g.getPositiveValue();
        }
    
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(p);
            v.add(g);
    
            return new DERSequence(v);
        }
    }
}
