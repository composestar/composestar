using org.bouncycastle.asn1;
using org.bouncycastle.math;

namespace org.bouncycastle.asn1.x9
{
    /**
     * ASN.1 def for Elliptic-Curve Field ID structure. See
     * X9.62, for further details.
     */
    public class X9FieldID
        : ASN1Encodable//, X9ObjectIdentifiers
    {
        private DERObjectIdentifier     id;
        private ASN1Object               parameters;
    
        public X9FieldID(
            DERObjectIdentifier id,
            BigInteger          primeP)
        {
            this.id = id;
            this.parameters = new DERInteger(primeP);
        }
    
        public X9FieldID(
            ASN1Sequence  seq)
        {
            this.id = (DERObjectIdentifier)seq.getObjectAt(0);
            this.parameters = (ASN1Object)seq.getObjectAt(1);
        }
    
        public DERObjectIdentifier getIdentifier()
        {
            return id;
        }
    
        public ASN1Object getParameters()
        {
            return parameters;
        }
    
        /**
         * Produce a DER encoding of the following structure.
         * <pre>
         *  FieldID ::= SEQUENCE {
         *      fieldType       FIELD-ID.&amp;id({IOSet}),
         *      parameters      FIELD-ID.&amp;Type({IOSet}{&#64;fieldType})
         *  }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector v = new ASN1EncodableVector();
    
            v.add(this.id);
            v.add(this.parameters);
    
            return new DERSequence(v);
        }
    }
}
