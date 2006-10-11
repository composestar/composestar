using org.bouncycastle.asn1;
using org.bouncycastle.math;

using System;

namespace org.bouncycastle.asn1.x509
{
    public class BasicConstraints
        : ASN1Encodable
    {
        DERBoolean  cA = new DERBoolean(false);
        DERInteger  pathLenConstraint = null;
    
        public static BasicConstraints getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public static BasicConstraints getInstance(
            object  obj)
        {
            if (obj == null || obj is BasicConstraints)
            {
                return (BasicConstraints)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new BasicConstraints((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
        
        public BasicConstraints(
            ASN1Sequence   seq)
        {
            if (seq.size() == 0)
            {
                this.cA = null;
                this.pathLenConstraint = null;
            }
            else
            {
                this.cA = (DERBoolean)seq.getObjectAt(0);
                if (seq.size() > 1)
                {
                    this.pathLenConstraint = (DERInteger)seq.getObjectAt(1);
                }
            }
        }
    
        public BasicConstraints(
            bool cA)
        {
            if (cA)
            {
                this.cA = new DERBoolean(true);
            }
            else
            {
                this.cA = null;
            }
            this.pathLenConstraint = null;
        }
    
        /**
         * create a cA=true object for the given path length constraint.
         * 
         * @param pathLenConstraint
         */
        public BasicConstraints(
            int     pathLenConstraint)
        {
            this.cA = new DERBoolean(true);
            this.pathLenConstraint = new DERInteger(pathLenConstraint);
        }
    
        public bool isCA()
        {
            return (cA != null) && cA.isTrue();
        }
    
        public BigInteger getPathLenConstraint()
        {
            if (pathLenConstraint != null)
            {
                return pathLenConstraint.getValue();
            }
    
            return null;
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * BasicConstraints := SEQUENCE {
         *    cA                  BOOLEAN DEFAULT FALSE,
         *    pathLenConstraint   INTEGER (0..MAX) OPTIONAL
         * }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            if (cA != null)
            {
                v.add(cA);
        
                if (pathLenConstraint != null)
                {
                    v.add(pathLenConstraint);
                }
            }
    
            return new DERSequence(v);
        }
    
        public override string ToString()
        {
            if (pathLenConstraint == null)
            {
                if (cA == null)
                {
                    return "BasicConstraints: isCa(false)";
                }
                return "BasicConstraints: isCa(" + this.isCA() + ")";
            }
            return "BasicConstraints: isCa(" + this.isCA() + "), pathLenConstraint = " + pathLenConstraint.getValue();
        }
    }
}
