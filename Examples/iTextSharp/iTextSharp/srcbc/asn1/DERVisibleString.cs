using System;

namespace org.bouncycastle.asn1
{
    /**
     * DER VisibleString object.
     */
    public class DERVisibleString
        : ASN1Object, DERString
    {
        string  str;
    
        /**
         * return a Visible string from the passed in object.
         *
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static DERVisibleString getInstance(
            object  obj)
        {
            if (obj == null || obj is DERVisibleString)
            {
                return (DERVisibleString)obj;
            }
    
            if (obj is ASN1OctetString)
            {
                return new DERVisibleString(((ASN1OctetString)obj).getOctets());
            }
    
            if (obj is ASN1TaggedObject)
            {
                return getInstance(((ASN1TaggedObject)obj).getObject());
            }
    
            throw new ArgumentException("illegal object in getInstance: " + obj.GetType().Name);
        }
    
        /**
         * return a Visible string from a tagged object.
         *
         * @param obj the tagged object holding the object we want
         * @param explicitly true if the object is meant to be explicitly
         *              tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         *               be converted.
         */
        public static DERVisibleString getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(obj.getObject());
        }
    
        /**
         * basic constructor - byte encoded string.
         */
        public DERVisibleString(
            byte[]   str)
        {
            char[]  cs = new char[str.Length];
    
            for (int i = 0; i != cs.Length; i++)
            {
                cs[i] = (char)(str[i] & 0xff);
            }
    
            this.str = new string(cs);
        }
    
        /**
         * basic constructor
         */
        public DERVisibleString(
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
            derOut.writeEncoded(ASN1Tags.VISIBLE_STRING, this.getOctets());
        }
        
        public override bool Equals(
            object  o)
        {
            if ((o == null) || !(o is DERVisibleString))
            {
                return false;
            }
    
            return this.getString().Equals(((DERVisibleString)o).getString());
        }
        
        public override int GetHashCode()
        {
            return this.getString().GetHashCode();
        }
    }
}
