using System;

namespace org.bouncycastle.asn1
{
    /**
     * DER BMPString object.
     */
    public class DERBMPString
        : ASN1Object, DERString
    {
        string  str;
    
        /**
         * return a BMP string from the given object.
         *
         * @param obj the object we want converted.
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static DERBMPString getInstance(
            object  obj)
        {
            if (obj == null || obj is DERBMPString)
            {
                return (DERBMPString)obj;
            }
    
            if (obj is ASN1OctetString)
            {
                return new DERBMPString(((ASN1OctetString)obj).getOctets());
            }
    
            if (obj is ASN1TaggedObject)
            {
                return getInstance(((ASN1TaggedObject)obj).getObject());
            }
    
            throw new ArgumentException("illegal object in getInstance: " + obj.GetType().Name);
        }
    
        /**
         * return a BMP string from a tagged object.
         *
         * @param obj the tagged object holding the object we want
         * @param explicitly true if the object is meant to be explicitly
         *              tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         *              be converted.
         */
        public static DERBMPString getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(obj.getObject());
        }
        
    
        /**
         * basic constructor - byte encoded string.
         */
        public DERBMPString(
            byte[]   str)
        {
            char[]  cs = new char[str.Length / 2];
    
            for (int i = 0; i != cs.Length; i++)
            {
                cs[i] = (char)((str[2 * i] << 8) | (str[2 * i + 1] & 0xff));
            }
    
            this.str = new String(cs);
        }
    
        /**
         * basic constructor
         */
        public DERBMPString(
            string   str)
        {
            this.str = str;
        }
    
        public string getString()
        {
            return str;
        }
    
        public override int GetHashCode()
        {
            return this.getString().GetHashCode();
        }
    
        public override bool Equals(
            object  o)
        {
            if (!(o is DERBMPString))
            {
                return false;
            }
    
            DERBMPString  s = (DERBMPString)o;
    
            return this.getString().Equals(s.getString());
        }
    
        internal override void encode(
            DEROutputStream  derOut)
        {
            char[]  c = str.ToCharArray();
            byte[]  b = new byte[c.Length * 2];
    
            for (int i = 0; i != c.Length; i++)
            {
                b[2 * i] = (byte)(c[i] >> 8);
                b[2 * i + 1] = (byte)c[i];
            }
    
            derOut.writeEncoded(ASN1Tags.BMP_STRING, b);
        }
    }
}
