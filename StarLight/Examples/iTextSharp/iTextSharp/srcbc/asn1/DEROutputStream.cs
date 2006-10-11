using System;
using System.IO;

namespace org.bouncycastle.asn1
{
    public class DEROutputStream
        : FilterStream
    {
        public DEROutputStream(Stream os) : base(os)
        {
        }
    
        private void writeLength(
            int length)
        {
            if (length > 127)
            {
                int size = 1;
                uint val = (uint) length;
    
                while ((val >>= 8) != 0)
                {
                    size++;
                }
    
                WriteByte((byte)(size | 0x80));
    
                for (int i = (size - 1) * 8; i >= 0; i -= 8)
                {
                    WriteByte((byte)(length >> i));
                }
            }
            else
            {
                WriteByte((byte)length);
            }
        }
    
        internal void writeEncoded(
            int     tag,
            byte[]  bytes)
        {
            WriteByte((byte) tag);
            writeLength(bytes.Length);
            Write(bytes, 0, bytes.Length);
        }
    
        protected void writeNull()
        {
            WriteByte((byte) ASN1Tags.NULL);
            WriteByte(0x00);
        }
    
        public virtual void writeObject(
            object    obj)
        {
            if (obj == null)
            {
                writeNull();
            }
            else if (obj is ASN1Object)
            {
                ((ASN1Object)obj).encode(this);
            }
            else if (obj is ASN1Encodable)
            {
                ((ASN1Encodable)obj).toASN1Object().encode(this);
            }
            else 
            {
                throw new IOException("object not ASN1Object");
            }
        }
    }
}
