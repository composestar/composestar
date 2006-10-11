using org.bouncycastle.asn1;
using org.bouncycastle.math;

using System;
using System.Collections;

namespace org.bouncycastle.asn1.x509
{
    public class DSAParameter
        : ASN1Encodable
    {
        DERInteger      p, q, g;
    
        public static DSAParameter getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public static DSAParameter getInstance(
            object obj)
        {
            if(obj == null || obj is DSAParameter) 
            {
                return (DSAParameter)obj;
            }
            
            if(obj is ASN1Sequence) 
            {
                return new DSAParameter((ASN1Sequence)obj);
            }
            
            throw new ArgumentException("Invalid DSAParameter: " + obj.GetType().Name);
        }
    
        public DSAParameter(
            BigInteger  p,
            BigInteger  q,
            BigInteger  g)
        {
            this.p = new DERInteger(p);
            this.q = new DERInteger(q);
            this.g = new DERInteger(g);
        }
    
        public DSAParameter(
            ASN1Sequence  seq)
        {
            IEnumerator     e = seq.getObjects();

            e.MoveNext();
            p = (DERInteger)e.Current;
            e.MoveNext();
            q = (DERInteger)e.Current;
            e.MoveNext();
            g = (DERInteger)e.Current;
        }
    
        public BigInteger getP()
        {
            return p.getPositiveValue();
        }
    
        public BigInteger getQ()
        {
            return q.getPositiveValue();
        }
    
        public BigInteger getG()
        {
            return g.getPositiveValue();
        }
    
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(p);
            v.add(q);
            v.add(g);
    
            return new DERSequence(v);
        }
    }
}
