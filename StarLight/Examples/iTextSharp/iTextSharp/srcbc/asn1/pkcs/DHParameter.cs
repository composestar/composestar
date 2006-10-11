using org.bouncycastle.asn1;
using org.bouncycastle.math;

using System.Collections;

namespace org.bouncycastle.asn1.pkcs
{
    public class DHParameter
        : ASN1Encodable
    {
        DERInteger      p, g, l;
    
        public DHParameter(
            BigInteger  p,
            BigInteger  g,
            int         l)
        {
            this.p = new DERInteger(p);
            this.g = new DERInteger(g);
    
            if (l != 0)
            {
                this.l = new DERInteger(l);
            }
            else
            {
                this.l = null;
            }
        }
    
        public DHParameter(
            ASN1Sequence  seq)
        {
            IEnumerator     e = seq.getObjects();

            e.MoveNext();
            p = (DERInteger)e.Current;
            e.MoveNext();
            g = (DERInteger)e.Current;
    
            if (e.MoveNext())
            {
                l = (DERInteger)e.Current;
            }
            else
            {
                l = null;
            }
        }
    
        public BigInteger getP()
        {
            return p.getPositiveValue();
        }
    
        public BigInteger getG()
        {
            return g.getPositiveValue();
        }
    
        public BigInteger getL()
        {
            if (l == null)
            {
                return null;
            }
    
            return l.getPositiveValue();
        }

        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(p);
            v.add(g);
    
            if (this.getL() != null)
            {
                v.add(l);
            }
    
            return new DERSequence(v);
        }
    }
}
