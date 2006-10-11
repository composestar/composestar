using System;

using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.x509
{
    public class IssuerSerial
        : ASN1Encodable
    {
        GeneralNames            issuer;
        DERInteger              serial;
        DERBitString            issuerUID;

        public static IssuerSerial getInstance(
            object o)
        {
            if (o == null || o is IssuerSerial)
            {
                return (IssuerSerial)o;
            }

            if (o is ASN1Sequence)
            {
                return new IssuerSerial((ASN1Sequence)o);
            }

            throw new ArgumentException("unknown object in factory");
        }

        public static IssuerSerial getInstance(
            ASN1TaggedObject obj,
            bool explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
        
        public IssuerSerial(
            ASN1Sequence    seq)
        {
            issuer = GeneralNames.getInstance(seq.getObjectAt(0));
            serial = (DERInteger)seq.getObjectAt(1);
    
            if (seq.size() == 3)
            {
                issuerUID = (DERBitString)seq.getObjectAt(2);
            }
        }
    
        public GeneralNames getIssuer()
        {
            return issuer;
        }
    
        public DERInteger getSerial()
        {
            return serial;
        }
    
        public DERBitString getIssuerUID()
        {
            return issuerUID;
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         *  IssuerSerial  ::=  SEQUENCE {
         *       issuer         GeneralNames,
         *       serial         CertificateSerialNumber,
         *       issuerUID      UniqueIdentifier OPTIONAL
         *  }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(issuer);
            v.add(serial);
    
            if (issuerUID != null)
            {
                v.add(issuerUID);
            }
    
            return new DERSequence(v);
        }
    }
}
