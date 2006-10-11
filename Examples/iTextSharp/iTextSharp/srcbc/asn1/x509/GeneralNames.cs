using org.bouncycastle.asn1;

using System;

namespace org.bouncycastle.asn1.x509
{
    public class GeneralNames
        : ASN1Encodable
    {
        ASN1Sequence            seq;
    
        public static GeneralNames getInstance(
            object  obj)
        {
            if (obj == null || obj is GeneralNames)
            {
                return (GeneralNames)obj;
            }
    
            if (obj is ASN1Sequence)
            {
                return new GeneralNames((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("illegal object in getInstance: " + obj.GetType().Name);
        }
    
        public static GeneralNames getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public GeneralNames(
            ASN1Sequence  seq)
        {
            this.seq = seq;
        }

        public GeneralName[] getNames()
        {
            GeneralName[]   names = new GeneralName[seq.size()];

            for (int i = 0; i != seq.size(); i++)
            {
                names[i] = GeneralName.getInstance(seq.getObjectAt(i));
            }

            return names;
        }

        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * GeneralNames ::= SEQUENCE SIZE {1..MAX} OF GeneralName
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            return seq;
        }
    }
}
