using System;

namespace org.bouncycastle.asn1
{
    /**
     * DER T61String (also the teletex string)
     */
    public class DERT61String
        : ASN1Object, DERString
    {
        internal string  str;
    
        /**
         * return a T61 string from the passed in object.
         *
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static DERT61String getInstance(
            object  obj)
        {
            if (obj == null || obj is DERT61String)
            {
                return (DERT61String)obj;
            }
    
            if (obj is ASN1OctetString)
            {
                return new DERT61String(((ASN1OctetString)obj).getOctets());
            }
    
            if (obj is ASN1TaggedObject)
            {
                return getInstance(((ASN1TaggedObject)obj).getObject());
            }
    
            throw new ArgumentException("illegal object in getInstance: " + obj.GetType().Name);
        }
    
        /**
         * return an T61 string from a tagged object.
         *
         * @param obj the tagged object holding the object we want
         * @param explicitly true if the object is meant to be explicitly
         *              tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         *               be converted.
         */
        public static DERT61String getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(obj.getObject());
        }
    
        /**
         * basic constructor - with bytes.
         */
        public DERT61String(
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
        public DERT61String(
            string   str)
        {
            this.str = str;
        }
    
        public string getString()
        {
            return str;
        }
    
        internal override void encode(
            DEROutputStream  derOut)
        {
            derOut.writeEncoded(ASN1Tags.T61_STRING, this.getOctets());
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
    
        public override bool Equals(
            object  o)
        {
            if ((o == null) || !(o is DERT61String))
            {
                return false;
            }
    
            return this.getString().Equals(((DERT61String)o).getString());
        }
        
        public override int GetHashCode()
        {
            return this.getString().GetHashCode();
        }
    }
}
