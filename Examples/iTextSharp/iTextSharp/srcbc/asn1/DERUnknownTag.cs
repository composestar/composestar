using System;

namespace org.bouncycastle.asn1
{
    /**
     * We insert one of these when we find a tag we don't recognise.
     */
    public class DERUnknownTag
        : ASN1Object
    {
        int        tag;
        byte[]     data;
    
        /**
         * @param tag the tag value.
         * @param data the octets making up the time.
         */
        public DERUnknownTag(
            int     tag,
            byte[]  data)
        {
            this.tag = tag;
            this.data = data;
        }
    
        public int getTag()
        {
            return tag;
        }
    
        public byte[] getData()
        {
            return data;
        }
    
        internal override void encode(
            DEROutputStream  derOut)
        {
            derOut.writeEncoded(tag, data);
        }
        
        public override bool Equals(
            object o)
        {
            if ((o == null) || !(o is DERUnknownTag))
            {
                return false;
            }
            
            DERUnknownTag other = (DERUnknownTag)o;
            
            if (tag != other.tag)
            {
                return false;
            }
            
            if (data.Length != other.data.Length)
            {
                return false;
            }
            
            for (int i = 0; i < data.Length; i++) 
            {
                if(data[i] != other.data[i])
                {
                    return false;
                }
            }
            
            return true;
        }
        
        public override int GetHashCode()
        {
            byte[]  b = this.getData();
            int     value = 0;
    
            for (int i = 0; i != b.Length; i++)
            {
                value ^= (b[i] & 0xff) << (i % 4);
            }
    
            return value ^ (int) this.getTag();
        }
    }
}
