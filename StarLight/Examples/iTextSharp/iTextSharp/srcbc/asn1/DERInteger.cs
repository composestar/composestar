using org.bouncycastle.math;

using System;

namespace org.bouncycastle.asn1
{
    public class DERInteger
        : ASN1Object
    {
        byte[]      bytes;
    
        /**
         * return an integer from the passed in object
         *
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static DERInteger getInstance(
            object  obj)
        {
            if (obj == null || obj is DERInteger)
            {
                return (DERInteger)obj;
            }
    
            if (obj is ASN1OctetString)
            {
                return new DERInteger(((ASN1OctetString)obj).getOctets());
            }
    
            if (obj is ASN1TaggedObject)
            {
                return getInstance(((ASN1TaggedObject)obj).getObject());
            }
    
            throw new ArgumentException("illegal object in getInstance: " + obj.GetType().Name);
        }
    
        /**
         * return an Integer from a tagged object.
         *
         * @param obj the tagged object holding the object we want
         * @param explicitly true if the object is meant to be explicitly
         *              tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         *               be converted.
         */
        public static DERInteger getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(obj.getObject());
        }
    
        public DERInteger(
            int         value)
        {
            bytes = BigInteger.valueOf(value).toByteArray();
        }
    
        public DERInteger(
            BigInteger   value)
        {
            bytes = value.toByteArray();
        }
    
        public DERInteger(
            byte[]   bytes)
        {
            this.bytes = bytes;
        }
    
        public BigInteger getValue()
        {
            return new BigInteger(bytes);
        }
    
        /**
         * in some cases positive values get crammed into a space,
         * that's not quite big enough...
         */
        public BigInteger getPositiveValue()
        {
            return new BigInteger(1, bytes);
        }
    
        internal override void encode(
            DEROutputStream derOut)
        {
            derOut.writeEncoded(ASN1Tags.INTEGER, bytes);
        }
        
        public override int GetHashCode()
        {
             int     value = 0;
     
             for (int i = 0; i != bytes.Length; i++)
             {
                 value ^= (bytes[i] & 0xff) << (i % 4);
             }
     
             return value;
        }
    
        public override bool Equals(
            object  o)
        {
            if (o == null || !(o is DERInteger))
            {
                return false;
            }
    
            DERInteger other = (DERInteger)o;
    
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
    }
}
