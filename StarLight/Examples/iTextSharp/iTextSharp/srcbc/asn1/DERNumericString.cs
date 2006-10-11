using System;

namespace org.bouncycastle.asn1
{
    /**
     * DER NumericString object - this is an ascii string of characters {0,1,2,3,4,5,6,7,8,9, }.
     */
    public class DERNumericString
        : ASN1Object, DERString
    {
        string  str;
    
        /**
         * return a Numeric string from the passed in object
         *
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static DERNumericString getInstance(
            object  obj)
        {
            if (obj == null || obj is DERNumericString)
            {
                return (DERNumericString)obj;
            }
    
            if (obj is ASN1OctetString)
            {
                return new DERNumericString(((ASN1OctetString)obj).getOctets());
            }
    
            if (obj is ASN1TaggedObject)
            {
                return getInstance(((ASN1TaggedObject)obj).getObject());
            }
    
            throw new ArgumentException("illegal object in getInstance: " + obj.GetType().Name);
        }
    
        /**
         * return an Numeric string from a tagged object.
         *
         * @param obj the tagged object holding the object we want
         * @param explicitly true if the object is meant to be explicitly
         *              tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         *               be converted.
         */
        public static DERNumericString getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(obj.getObject());
        }
    
        /**
         * basic constructor - with bytes.
         */
        public DERNumericString(
            byte[]   str)
        {
            char[]  cs = new char[str.Length];
    
            for (int i = 0; i != cs.Length; i++)
            {
                cs[i] = (char)(str[i] & 0xff);
            }
    
            this.str = new String(cs);
        }
    
        /**
         * basic constructor - with string.
         */
        public DERNumericString(
            string   str)
        {
            this.str = str;
        }
    
        public string getString()
        {
            return str;
        }
    
        public byte[] getOctets()
        {
            char[]  cs = str.ToCharArray();
            byte[]  bs = new byte[cs.Length];
    
            for (int i = 0; i != cs.Length; i++)
            {
                bs[i] = (byte)cs[i];
            }
    
            return bs; 
        }
    
        internal override void encode(
            DEROutputStream  derOut)
        {
            derOut.writeEncoded(ASN1Tags.NUMERIC_STRING, this.getOctets());
        }
    
        public override int GetHashCode()
        {
            return this.getString().GetHashCode();
        }
    
        public override bool Equals(
            object  o)
        {
            if (!(o is DERNumericString))
            {
                return false;
            }
    
            DERNumericString  s = (DERNumericString)o;
    
            return this.getString().Equals(s.getString());
        }
    }
}
