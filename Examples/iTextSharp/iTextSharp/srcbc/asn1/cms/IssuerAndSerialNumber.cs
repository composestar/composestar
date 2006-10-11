using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;
using org.bouncycastle.math;

using System;

namespace org.bouncycastle.asn1.cms
{
    public class IssuerAndSerialNumber
        : ASN1Encodable
    {
        X509Name    name;
        DERInteger  serialNumber;
    
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
    
            throw new ArgumentException(
                "Illegal object in IssuerAndSerialNumber: " + obj.GetType().Name);
        }
    
        public IssuerAndSerialNumber(
            ASN1Sequence    seq)
        {
            this.name = X509Name.getInstance(seq.getObjectAt(0));
            this.serialNumber = (DERInteger)seq.getObjectAt(1);
        }
    
        public IssuerAndSerialNumber(
            X509Name    name,
            BigInteger  serialNumber)
        {
            this.name = name;
            this.serialNumber = new DERInteger(serialNumber);
        }
    
        public IssuerAndSerialNumber(
            X509Name    name,
            DERInteger  serialNumber)
        {
            this.name = name;
            this.serialNumber = serialNumber;
        }
    
        public X509Name getName()
        {
            return name;
        }
    
        public DERInteger getSerialNumber()
        {
            return serialNumber;
        }
    
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector    v = new ASN1EncodableVector();
    
            v.add(name);
            v.add(serialNumber);
    
            return new DERSequence(v);
        }
    }
}
