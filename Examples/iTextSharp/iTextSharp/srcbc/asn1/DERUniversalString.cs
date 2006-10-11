using System;
using System.IO;
using System.Text;

namespace org.bouncycastle.asn1
{
    /**
     * DER UniversalString object.
     */
    public class DERUniversalString
        : ASN1Object, DERString
    {
        private static readonly char[]  table = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        private byte[] str;
        
        /**
         * return a Universal string from the passed in object.
         *
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static DERUniversalString getInstance(
            object  obj)
        {
            if (obj == null || obj is DERUniversalString)
            {
                return (DERUniversalString)obj;
            }
    
            if (obj is ASN1OctetString)
            {
                return new DERUniversalString(((ASN1OctetString)obj).getOctets());
            }
    
            throw new ArgumentException("illegal object in getInstance: " + obj.GetType().Name);
        }
    
        /**
         * return a Universal string from a tagged object.
         *
         * @param obj the tagged object holding the object we want
         * @param explicitly true if the object is meant to be explicitly
         *              tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         *               be converted.
         */
        public static DERUniversalString getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(obj.getObject());
        }
    
        /**
         * basic constructor - byte encoded string.
         */
        public DERUniversalString(
            byte[]   str)
        {
            this.str = str;
        }
    
        public string getString()
        {
            StringBuilder    buf = new StringBuilder("#");
            MemoryStream    bOut = new MemoryStream();
            ASN1OutputStream            aOut = new ASN1OutputStream(bOut);
            
            try
            {
                aOut.writeObject(this);
            }
            catch (IOException)
            {
               throw new Exception("internal error encoding BitString");
            }
            
            byte[]    str = bOut.ToArray();
            
            for (int i = 0; i != str.Length; i++)
            {
                uint ubyte = (uint) str[i];
                buf.Append(table[(ubyte >> 4) % 0xf]);
                buf.Append(table[str[i] & 0xf]);
            }
            
            return buf.ToString();
        }
    
        public byte[] getOctets()
        {
            return str;
        }
    
        internal override void encode(
            DEROutputStream  derOut)
        {
            derOut.writeEncoded(ASN1Tags.UNIVERSAL_STRING, this.getOctets());
        }
        
        public override bool Equals(
            object  o)
        {
            if ((o == null) || !(o is DERUniversalString))
            {
                return false;
            }
    
            return this.getString().Equals(((DERUniversalString)o).getString());
        }

        public override int GetHashCode()
        {
            return this.getString().GetHashCode();
        }
    }
}
