using org.bouncycastle.asn1;
using org.bouncycastle.math;

using System;
using System.Collections;

namespace org.bouncycastle.asn1.x509
{
    public class RSAPublicKeyStructure
        : ASN1Encodable
    {
        private BigInteger  modulus;
        private BigInteger  publicExponent;
    
        public static RSAPublicKeyStructure getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public static RSAPublicKeyStructure getInstance(
            object obj)
        {
            if(obj == null || obj is RSAPublicKeyStructure) 
            {
                return (RSAPublicKeyStructure)obj;
            }
            
            if(obj is ASN1Sequence) 
            {
                return new RSAPublicKeyStructure((ASN1Sequence)obj);
            }
            
            throw new ArgumentException("Invalid RSAPublicKeyStructure: " + obj.GetType().Name);
        }
        
        public RSAPublicKeyStructure(
            BigInteger  modulus,
            BigInteger  publicExponent)
        {
            this.modulus = modulus;
            this.publicExponent = publicExponent;
        }
    
        public RSAPublicKeyStructure(
            ASN1Sequence  seq)
        {
            IEnumerator e = seq.getObjects();

            e.MoveNext();
            modulus = ((DERInteger)e.Current).getPositiveValue();
            e.MoveNext();
            publicExponent = ((DERInteger)e.Current).getPositiveValue();
        }
    
        public BigInteger getModulus()
        {
            return modulus;
        }
    
        public BigInteger getPublicExponent()
        {
            return publicExponent;
        }
    
        /**
         * This outputs the key in PKCS1v2 format.
         * <pre>
         *      RSAPublicKey ::= SEQUENCE {
         *                          modulus INTEGER, -- n
         *                          publicExponent INTEGER, -- e
         *                      }
         * </pre>
         * <p>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(new DERInteger(getModulus()));
            v.add(new DERInteger(getPublicExponent()));
    
            return new DERSequence(v);
        }
    }
}
