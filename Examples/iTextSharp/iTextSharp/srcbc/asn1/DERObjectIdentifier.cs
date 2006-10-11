using System;
using System.IO;
using System.Text;

namespace org.bouncycastle.asn1
{
    public class DERObjectIdentifier
        : ASN1Object
    {
        string      identifier;
    
        /**
         * return an OID from the passed in object
         *
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static DERObjectIdentifier getInstance(
            object  obj)
        {
            if (obj == null || obj is DERObjectIdentifier)
            {
                return (DERObjectIdentifier)obj;
            }
    
            if (obj is ASN1OctetString)
            {
                return new DERObjectIdentifier(((ASN1OctetString)obj).getOctets());
            }
    
            if (obj is ASN1TaggedObject)
            {
                return getInstance(((ASN1TaggedObject)obj).getObject());
            }
    
            throw new ArgumentException("illegal object in getInstance: " + obj.GetType().Name);
        }
    
        /**
         * return an object Identifier from a tagged object.
         *
         * @param obj the tagged object holding the object we want
         * @param explicitly true if the object is meant to be explicitly
         *              tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         *               be converted.
         */
        public static DERObjectIdentifier getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(obj.getObject());
        }
        
    
        internal DERObjectIdentifier(
            byte[]  bytes)
        {
            StringBuilder    objId = new StringBuilder();
            long            value = 0;
            bool         first = true;
    
            for (int i = 0; i != bytes.Length; i++)
            {
                int b = bytes[i] & 0xff;
    
                value = value * 128 + (b & 0x7f);
                if ((b & 0x80) == 0)             // end of number reached
                {
                    if (first)
                    {
                        switch ((int)value / 40)
                        {
                        case 0:
                            objId.Append('0');
                            break;
                        case 1:
                            objId.Append('1');
                            value -= 40;
                            break;
                        default:
                            objId.Append('2');
                            value -= 80;
                            break;
                        }
                        first = false;
                    }
    
                    objId.Append('.');
                    objId.Append(value.ToString());
                    value = 0;
                }
            }
    
            this.identifier = objId.ToString();
        }
    
        public DERObjectIdentifier(
            string  identifier)
        {
            this.identifier = identifier;
        }
    
        public string getId()
        {
            return identifier;
        }
    
        private void writeField(
            Stream    outputStream,
            long            fieldValue)
        {
            if (fieldValue >= (1 << 7))
            {
                if (fieldValue >= (1 << 14))
                {
                    if (fieldValue >= (1 << 21))
                    {
                        if (fieldValue >= (1 << 28))
                        {
                            if (fieldValue >= (1 << 35))
                            {
                                if (fieldValue >= (1 << 42))
                                {
                                    if (fieldValue >= (1 << 49))
                                    {
                                        if (fieldValue >= (1 << 56))
                                        {
                                            outputStream.WriteByte((byte)((fieldValue >> 56) | 0x80));
                                        }
                                        outputStream.WriteByte((byte)((fieldValue >> 49) | 0x80));
                                    }
                                    outputStream.WriteByte((byte)((fieldValue >> 42) | 0x80));
                                }
                                outputStream.WriteByte((byte)((fieldValue >> 35) | 0x80));
                            }
                            outputStream.WriteByte((byte)((fieldValue >> 28) | 0x80));
                        }
                        outputStream.WriteByte((byte)((fieldValue >> 21) | 0x80));
                    }
                    outputStream.WriteByte((byte)((fieldValue >> 14) | 0x80));
                }
                outputStream.WriteByte((byte)((fieldValue >> 7) | 0x80));
            }
            outputStream.WriteByte((byte)(fieldValue & 0x7f));
        }
    
        internal override void encode(
            DEROutputStream derOut)
        {
            OIDTokenizer            tok = new OIDTokenizer(identifier);
            MemoryStream   bOut = new MemoryStream();
            DEROutputStream         dOut = new DEROutputStream(bOut);

            string token = "";
            try
            {
                token = tok.nextToken();
                int first = System.Int32.Parse(token);

                token = tok.nextToken();
                int second = System.Int32.Parse(token);
                
                writeField(bOut, first * 40 + second);
            }
            catch(Exception)
            {
                throw new Exception("Problems parsing identifier: " + identifier + ", " + token);
            }
    
            while (tok.hasMoreTokens())
            {
                writeField(bOut, System.Int64.Parse(tok.nextToken()));
            }
    
            dOut.Close();
    
            byte[]  bytes = bOut.ToArray();
    
            derOut.writeEncoded(ASN1Tags.OBJECT_IDENTIFIER, bytes);
        }
    
        public override int GetHashCode()
        {
            return identifier.GetHashCode();
        }
    
        public override bool Equals(
            object  o)
        {
            if ((o == null) || !(o is DERObjectIdentifier))
            {
                return false;
            }
    
            return identifier.Equals(((DERObjectIdentifier)o).identifier);
        }
    }
}
