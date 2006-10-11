using System;
using System.IO;
using System.Text;

namespace org.bouncycastle.asn1
{
    public class DERBitString
        : ASN1Object, DERString
    {
        private static readonly char[]  table = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        
        protected byte[]      data;
        protected int         padBits;
    
        /**
         * return the correct number of pad bits for a bit string defined in
         * a 32 bit constant
         */
        static protected int getPadBits(
            int bitString)
        {
            int val = 0;
            for (int i = 3; i >= 0; i--) 
            {
                //
                // this may look a little odd, but if it isn't done like this pre jdk1.2
                // JVM's break!
                //
                if (i != 0)
                {
                    if ((bitString >> (i * 8)) != 0) 
                    {
                        val = (bitString >> (i * 8)) & 0xFF;
                        break;
                    }
                }
                else
                {
                    if (bitString != 0)
                    {
                        val = bitString & 0xFF;
                        break;
                    }
                }
            }
     
            if (val == 0)
            {
                return 7;
            }
    
    
            int bits = 1;
    
            while (((val <<= 1) & 0xFF) != 0)
            {
                bits++;
            }
    
            return 8 - bits;
        }
    
        /**
         * return the correct number of bytes for a bit string defined in
         * a 32 bit constant
         */
        static protected byte[] getBytes(int bitString)
        {
            int bytes = 4;
            for (int i = 3; i >= 1; i--)
            {
                if ((bitString & (0xFF << (i * 8))) != 0)
                {
                    break;
                }
                bytes--;
            }
            
            byte[] result = new byte[bytes];
            for (int i = 0; i < bytes; i++)
            {
                result[i] = (byte) ((bitString >> (i * 8)) & 0xFF);
            }
    
            return result;
        }
    
        /**
         * return a Bit string from the passed in object
         *
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static DERBitString getInstance(
            object  obj)
        {
            if (obj == null || obj is DERBitString)
            {
                return (DERBitString)obj;
            }
    
            if (obj is ASN1OctetString)
            {
                byte[]  bytes = ((ASN1OctetString)obj).getOctets();
                int     padBits = bytes[0];
                byte[]  data = new byte[bytes.Length - 1];
    
                Array.Copy(bytes, 1, data, 0, bytes.Length - 1);
    
                return new DERBitString(data, padBits);
            }
    
            if (obj is ASN1TaggedObject)
            {
                return getInstance(((ASN1TaggedObject)obj).getObject());
            }
    
            throw new ArgumentException("illegal object in getInstance: " + obj.GetType().Name);
        }
    
        /**
         * return a Bit string from a tagged object.
         *
         * @param obj the tagged object holding the object we want
         * @param explicitly true if the object is meant to be explicitly
         *              tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         *               be converted.
         */
        public static DERBitString getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(obj.getObject());
        }
        
        protected DERBitString(
            byte    data,
            int     padBits)
        {
            this.data = new byte[1];
            this.data[0] = data;
            this.padBits = padBits;
        }
    
        /**
         * @param data the octets making up the bit string.
         * @param padBits the number of extra bits at the end of the string.
         */
        public DERBitString(
            byte[]  data,
            int     padBits)
        {
            this.data = data;
            this.padBits = padBits;
        }
    
        public DERBitString(
            byte[]  data)
        {
            this.data = data;
            this.padBits = 0;
        }
    
        public DERBitString(
            ASN1Encodable  obj)
        {
            try
            {
                MemoryStream   bOut = new MemoryStream();
                DEROutputStream         dOut = new DEROutputStream(bOut);
    
                dOut.writeObject(obj);
                dOut.Close();
    
                this.data = bOut.ToArray();
                this.padBits = 0;
            }
            catch (IOException e)
            {
                throw new ArgumentException("Error processing object : " + e.ToString());
            }
        }
    
        public byte[] getBytes()
        {
            return data;
        }
    
        public int getPadBits()
        {
            return padBits;
        }
    
        internal override void encode(
            DEROutputStream  derOut)
        {
            byte[]  bytes = new byte[getBytes().Length + 1];
    
            bytes[0] = (byte)getPadBits();
            Array.Copy(getBytes(), 0, bytes, 1, bytes.Length - 1);
    
            derOut.writeEncoded(ASN1Tags.BIT_STRING, bytes);
        }
    
        public override int GetHashCode()
        {
            int     value = 0;
    
            for (int i = 0; i != data.Length; i++)
            {
                value ^= (data[i] & 0xff) << (i % 4);
            }
    
            return value;
        }
        
        public override bool Equals(
            object  o)
        {
            if (o == null || !(o is DERBitString))
            {
                return false;
            }
    
            DERBitString  other = (DERBitString)o;
    
            if (data.Length != other.data.Length)
            {
                return false;
            }
    
            for (int i = 0; i != data.Length; i++)
            {
                if (data[i] != other.data[i])
                {
                    return false;
                }
            }
    
            return (padBits == other.padBits);
        }
    
        public string getString()
        {
            StringBuilder          buf = new StringBuilder("#");
            MemoryStream bOut = new MemoryStream();
            ASN1OutputStream      aOut = new ASN1OutputStream(bOut);
            
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
    }
}
