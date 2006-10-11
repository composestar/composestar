using System;
using System.IO;

namespace org.bouncycastle.asn1
{
    /**
     * Base class for an application specific object
     */
    public class DERApplicationSpecific 
        : ASN1Object
    {
        private int       tag;
        private byte[]    octets;
        
        public DERApplicationSpecific(
            int        tag,
            byte[]    octets)
        {
            this.tag = tag;
            this.octets = octets;
        }
        
        public DERApplicationSpecific(
            int                  tag, 
            ASN1Encodable        obj) 
        {
            this.tag = tag | ASN1Tags.CONSTRUCTED;
            
            MemoryStream baos = new MemoryStream();
            DEROutputStream dos = new DEROutputStream(baos);
            
            dos.writeObject(obj);
            
            this.octets = baos.ToArray();
        }
        
        public bool isConstructed()
        {
            return (tag & ASN1Tags.CONSTRUCTED) != 0;
        }
        
        public byte[] getContents()
        {
            return octets;
        }
        
        public int getApplicationTag() 
        {
            return (int) tag & 0x1F;
        }
         
        public ASN1Encodable getObject() 
        {
            return new ASN1InputStream(new MemoryStream(getContents())).readObject();
        }
        
        internal override void encode(DEROutputStream derOut)
        {
            derOut.writeEncoded(ASN1Tags.APPLICATION | tag, octets);
        }
        
        public override bool Equals(
                object o)
        {
            if ((o == null) || !(o is DERApplicationSpecific))
            {
                return false;
            }
            
            DERApplicationSpecific other = (DERApplicationSpecific)o;
            
            if (tag != other.tag)
            {
                return false;
            }
            
            if (octets.Length != other.octets.Length)
            {
                return false;
            }
            
            for (int i = 0; i < octets.Length; i++) 
            {
                if (octets[i] != other.octets[i])
                {
                    return false;
                }
            }
            
            return true;
        }
        
        public override int GetHashCode()
        {
            byte[]  b = this.getContents();
            int     value = 0;
    
            for (int i = 0; i != b.Length; i++)
            {
                value ^= (b[i] & 0xff) << (i % 4);
            }
    
            return value ^ this.getApplicationTag();
        }
    }
}
