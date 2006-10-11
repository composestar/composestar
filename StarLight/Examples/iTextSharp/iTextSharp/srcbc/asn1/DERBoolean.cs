using System;

namespace org.bouncycastle.asn1
{
    public class DERBoolean
        : ASN1Object
    {
        byte         value;
    
        public static readonly DERBoolean FALSE = new DERBoolean(false);
        public static readonly DERBoolean TRUE  = new DERBoolean(true);
    
        /**
         * return a bool from the passed in object.
         *
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static DERBoolean getInstance(
            object  obj)
        {
            if (obj == null || obj is DERBoolean)
            {
                return (DERBoolean)obj;
            }
    
            if (obj is ASN1OctetString)
            {
                return new DERBoolean(((ASN1OctetString)obj).getOctets());
            }
    
            if (obj is ASN1TaggedObject)
            {
                return getInstance(((ASN1TaggedObject)obj).getObject());
            }
    
            throw new ArgumentException("illegal object in getInstance: " + obj.GetType().Name);
        }
    
        /**
         * return a DERBoolean from the passed in boolean.
         */
        public static DERBoolean getInstance(
            bool  value)
        {
            return (value ? TRUE : FALSE);
        }
    
        /**
         * return a Boolean from a tagged object.
         *
         * @param obj the tagged object holding the object we want
         * @param explicit true if the object is meant to be explicitly
         *              tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         *               be converted.
         */
        public static DERBoolean getInstance(
            ASN1TaggedObject obj,
            bool          isExplicit)
        {
            return getInstance(obj.getObject());
        }
        
        public DERBoolean(
            byte[]       value)
        {
            this.value = value[0];
        }
    
        public DERBoolean(
            bool     value)
        {
            this.value = (value) ? (byte)0xff : (byte)0;
        }
    
        public bool isTrue()
        {
            return (value != 0);
        }
    
        internal override void encode(
            DEROutputStream derOut)
        {
            byte[]  bytes = new byte[1];
    
            bytes[0] = value;
    
            derOut.writeEncoded(ASN1Tags.BOOLEAN, bytes);
        }
        
        public override bool Equals(
            object  o)
        {
            if ((o == null) || !(o is DERBoolean))
            {
                return false;
            }
    
            return (value == ((DERBoolean)o).value);
        }
        
        public override int GetHashCode()
        {
            return value;
        }
    
    }
}
