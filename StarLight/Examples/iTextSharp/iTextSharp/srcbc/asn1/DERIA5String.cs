using System;

namespace org.bouncycastle.asn1
{
    /**
     * DER IA5String object - this is an ascii string.
     */
    public class DERIA5String
        : ASN1Object, DERString
    {
        string  str;
    
        /**
         * return a IA5 string from the passed in object
         *
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static DERIA5String getInstance(
            object  obj)
        {
            if (obj == null || obj is DERIA5String)
            {
                return (DERIA5String)obj;
            }
    
            if (obj is ASN1OctetString)
            {
                return new DERIA5String(((ASN1OctetString)obj).getOctets());
            }
    
            if (obj is ASN1TaggedObject)
            {
                return getInstance(((ASN1TaggedObject)obj).getObject());
            }
    
            throw new ArgumentException("illegal object in getInstance: " + obj.GetType().Name);
        }
    
        /**
         * return an IA5 string from a tagged object.
         *
         * @param obj the tagged object holding the object we want
         * @param explicitly true if the object is meant to be explicitly
         *              tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         *               be converted.
         */
        public static DERIA5String getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(obj.getObject());
        }
    
        /**
         * basic constructor - with bytes.
         */
        public DERIA5String(
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
        public DERIA5String(
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
            derOut.writeEncoded(ASN1Tags.IA5_STRING, this.getOctets());
        }
    
        public override int GetHashCode()
        {
            return this.getString().GetHashCode();
        }
    
        public override bool Equals(
            object  o)
        {
            if (!(o is DERIA5String))
            {
                return false;
            }
    
            DERIA5String  s = (DERIA5String)o;
    
            return this.getString().Equals(s.getString());
        }
    }
}
