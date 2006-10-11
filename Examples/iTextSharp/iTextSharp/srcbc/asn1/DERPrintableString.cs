using System;

namespace org.bouncycastle.asn1
{
    /**
     * DER PrintableString object.
     */
    public class DERPrintableString
        : ASN1Object, DERString
    {
        string  str;
    
        /**
         * return a printable string from the passed in object.
         * 
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static DERPrintableString getInstance(
            object  obj)
        {
            if (obj == null || obj is DERPrintableString)
            {
                return (DERPrintableString)obj;
            }
    
            if (obj is ASN1OctetString)
            {
                return new DERPrintableString(((ASN1OctetString)obj).getOctets());
            }
    
            if (obj is ASN1TaggedObject)
            {
                return getInstance(((ASN1TaggedObject)obj).getObject());
            }
    
            throw new ArgumentException("illegal object in getInstance: " + obj.GetType().Name);
        }
    
        /**
         * return a Printable string from a tagged object.
         *
         * @param obj the tagged object holding the object we want
         * @param explicitly true if the object is meant to be explicitly
         *              tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         *               be converted.
         */
        public static DERPrintableString getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(obj.getObject());
        }
    
        /**
         * basic constructor - byte encoded string.
         */
        public DERPrintableString(
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
        public DERPrintableString(
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
            derOut.writeEncoded(ASN1Tags.PRINTABLE_STRING, this.getOctets());
        }
    
        public override int GetHashCode()
        {
            return this.getString().GetHashCode();
        }
    
        public override bool Equals(
            object  o)
        {
            if (!(o is DERPrintableString))
            {
                return false;
            }
    
            DERPrintableString  s = (DERPrintableString)o;
    
            return this.getString().Equals(s.getString());
        }
    }
}
