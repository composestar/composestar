using System;
using System.IO;

namespace org.bouncycastle.asn1
{
    /**
     * DER UTF8String object.
     */
    public class DERUTF8String
        : ASN1Object, DERString
    {
        string  str;
    
        /**
         * return an UTF8 string from the passed in object.
         *
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static DERUTF8String getInstance(
            object  obj)
        {
            if (obj == null || obj is DERUTF8String)
            {
                return (DERUTF8String)obj;
            }
    
            if (obj is ASN1OctetString)
            {
                return new DERUTF8String(((ASN1OctetString)obj).getOctets());
            }
    
            if (obj is ASN1TaggedObject)
            {
                return getInstance(((ASN1TaggedObject)obj).getObject());
            }
    
            throw new ArgumentException("illegal object in getInstance: " + obj.GetType().Name);
        }
    
        /**
         * return an UTF8 string from a tagged object.
         *
         * @param obj the tagged object holding the object we want
         * @param explicitly true if the object is meant to be explicitly
         *              tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         *               be converted.
         */
        public static DERUTF8String getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(obj.getObject());
        }
    
        /**
         * basic constructor - byte encoded string.
         */
        internal DERUTF8String(
            byte[]   str)
        {
            int i = 0;
            int length = 0;
    
            while (i < str.Length)
            {
                length++;
                if ((str[i] & 0xe0) == 0xe0)
                {
                    i += 3;
                }
                else if ((str[i] & 0xc0) == 0xc0)
                {
                    i += 2;
                }
                else
                {
                    i += 1;
                }
            }
    
            char[]  cs = new char[length];
    
            i = 0;
            length = 0;
    
            while (i < str.Length)
            {
                char    ch;
    
                if ((str[i] & 0xe0) == 0xe0)
                {
                    ch = (char)(((str[i] & 0x1f) << 12)
                          | ((str[i + 1] & 0x3f) << 6) | (str[i + 2] & 0x3f));
                    i += 3;
                }
                else if ((str[i] & 0xc0) == 0xc0)
                {
                    ch = (char)(((str[i] & 0x3f) << 6) | (str[i + 1] & 0x3f));
                    i += 2;
                }
                else
                {
                    ch = (char)(str[i] & 0xff);
                    i += 1;
                }
    
                cs[length++] = ch;
            }
    
            this.str = new string(cs);
        }
    
        /**
         * basic constructor
         */
        public DERUTF8String(
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
            if (!(o is DERUTF8String))
            {
                return false;
            }
    
            DERUTF8String  s = (DERUTF8String)o;
    
            return this.getString().Equals(s.getString());
        }
    
        internal override void encode(
            DEROutputStream  derOut)
        {
            char[]                  c = str.ToCharArray();
            MemoryStream bOut = new MemoryStream();
    
            for (int i = 0; i != c.Length; i++)
            {
                char    ch = c[i];
    
                if (ch < 0x0080)
                {
                    bOut.WriteByte((byte) ch);
                }
                else if (ch < 0x0800)
                {
                    bOut.WriteByte((byte) (0xc0 | (ch >> 6)));
                    bOut.WriteByte((byte) (0x80 | (ch & 0x3f)));
                }
                else
                {
                    bOut.WriteByte((byte) (0xe0 | (ch >> 12)));
                    bOut.WriteByte((byte) (0x80 | ((ch >> 6) & 0x3F)));
                    bOut.WriteByte((byte) (0x80 | (ch & 0x3F)));
                }
            }
    
            derOut.writeEncoded(ASN1Tags.UTF8_STRING, bOut.ToArray());
        }
    }
}
