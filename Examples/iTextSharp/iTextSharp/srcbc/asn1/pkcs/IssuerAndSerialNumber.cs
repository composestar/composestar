using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;
using org.bouncycastle.math;

using System;

namespace org.bouncycastle.asn1.pkcs
{
    public class IssuerAndSerialNumber
        : ASN1Encodable
    {
        X509Name    name;
        DERInteger  certSerialNumber;
    
        public static IssuerAndSerialNumber getInstance(
            object  obj)
        {
            if (obj is IssuerAndSerialNumber)
            {
                return (IssuerAndSerialNumber)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new IssuerAndSerialNumber((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public IssuerAndSerialNumber(
            ASN1Sequence    seq)
        {
            this.name = X509Name.getInstance(seq.getObjectAt(0));
            this.certSerialNumber = (DERInteger)seq.getObjectAt(1);
        }
    
        public IssuerAndSerialNumber(
            X509Name    name,
            BigInteger  certSerialNumber)
        {
            this.name = name;
            this.certSerialNumber = new DERInteger(certSerialNumber);
        }
    
        public IssuerAndSerialNumber(
            X509Name    name,
            DERInteger  certSerialNumber)
        {
            this.name = name;
            this.certSerialNumber = certSerialNumber;
        }
    
        public X509Name getName()
        {
            return name;
        }
    
        public DERInteger getCertificateSerialNumber()
        {
            return certSerialNumber;
        }
    
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector    v = new ASN1EncodableVector();
    
            v.add(name);
            v.add(certSerialNumber);
    
            return new DERSequence(v);
        }
    }
}
