using org.bouncycastle.math;

using System;

namespace org.bouncycastle.asn1
{
    public class DEREnumerated
        : ASN1Object
    {
        byte[]      bytes;
    
        /**
         * return an integer from the passed in object
         *
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static DEREnumerated getInstance(
            object  obj)
        {
            if (obj == null || obj is DEREnumerated)
            {
                return (DEREnumerated)obj;
            }
    
            if (obj is ASN1OctetString)
            {
                return new DEREnumerated(((ASN1OctetString)obj).getOctets());
            }
    
            if (obj is ASN1TaggedObject)
            {
                return getInstance(((ASN1TaggedObject)obj).getObject());
            }
    
            throw new ArgumentException("illegal object in getInstance: " + obj.GetType().Name);
        }
    
        /**
         * return an Enumerated from a tagged object.
         *
         * @param obj the tagged object holding the object we want
         * @param explicitly true if the object is meant to be explicitly
         *              tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         *               be converted.
         */
        public static DEREnumerated getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(obj.getObject());
        }
    
        public DEREnumerated(
            int         value)
        {
            bytes = BigInteger.valueOf(value).toByteArray();
        }
    
        public DEREnumerated(
            BigInteger   value)
        {
            bytes = value.toByteArray();
        }
    
        public DEREnumerated(
            byte[]   bytes)
        {
            this.bytes = bytes;
        }
    
        public BigInteger getValue()
        {
            return new BigInteger(bytes);
        }
    
        internal override void encode(
            DEROutputStream derOut)
        {
            derOut.writeEncoded(ASN1Tags.ENUMERATED, bytes);
        }
        
        public override bool Equals(
            object  o)
        {
            if (o == null || !(o is DEREnumerated))
            {
                return false;
            }
    
            DEREnumerated other = (DEREnumerated)o;
    
            if (bytes.Length != other.bytes.Length)
            {
                return false;
            }
    
            for (int i = 0; i != bytes.Length; i++)
            {
                if (bytes[i] != other.bytes[i])
                {
                    return false;
                }
            }
    
            return true;
        }
        
        public override int GetHashCode()
        {
            return this.getValue().GetHashCode();
        }
    }
}
