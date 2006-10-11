using System;

namespace org.bouncycastle.asn1
{
    public class DERGeneralString 
        : ASN1Object, DERString
    {
        private string str;
    
        public static DERGeneralString getInstance(
            object obj) 
        {
            if (obj == null || obj is DERGeneralString) 
            {
                return (DERGeneralString) obj;
            }
            if (obj is ASN1OctetString) 
            {
                return new DERGeneralString(((ASN1OctetString) obj).getOctets());
            }
            if (obj is ASN1TaggedObject) 
            {
                return getInstance(((ASN1TaggedObject) obj).getObject());
            }
            throw new ArgumentException("illegal object in getInstance: "
                    + obj.GetType().Name);
        }
    
        public static DERGeneralString getInstance(
            ASN1TaggedObject obj, 
            bool explicitly) 
        {
            return getInstance(obj.getObject());
        }
    
        public DERGeneralString(byte[] str) 
        {
            char[] cs = new char[str.Length];
            for (int i = 0; i != cs.Length; i++)
            {
                cs[i] = (char)(str[i] & 0xff);
            }
            this.str = new String(cs);
        }
    
        public DERGeneralString(String str) 
        {
            this.str = str;
        }
        
        public string getString() 
        {
            return str;
        }
        
        public byte[] getOctets() 
        {
            char[] cs = str.ToCharArray();
            byte[] bs = new byte[cs.Length];
            for (int i = 0; i != cs.Length; i++) 
            {
                bs[i] = (byte) cs[i];
            }
            return bs;
        }
        
        internal override void encode(DEROutputStream derOut) 
        {
            derOut.writeEncoded(ASN1Tags.GENERAL_STRING, this.getOctets());
        }
        
        public override int GetHashCode() 
        {
            return this.getString().GetHashCode();
        }
        
        public override bool Equals(Object o) 
        {
            if (!(o is DERGeneralString)) 
            {
                return false;
            }
            DERGeneralString s = (DERGeneralString) o;
            return this.getString().Equals(s.getString());
        }
    }
}
