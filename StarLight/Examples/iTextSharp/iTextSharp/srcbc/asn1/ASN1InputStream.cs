using System;
using System.Collections;
using System.IO;

namespace org.bouncycastle.asn1
{
    /**
     * a general purpose ASN.1 decoder - note: this class differs from the
     * others in that it returns null after it has read the last object in
     * the stream. If an ASN.1 NULL is encountered a DER/BER Null object is
     * returned.
     */
    public class ASN1InputStream
        : FilterStream
    {
        private class EOS_ASN1Object : ASN1Object
        {
            internal override void encode(DEROutputStream derOut)
            {
                throw new IOException("Eeek!");
            }

            public override int GetHashCode()
            {
                return 0;
            }

            public override bool Equals(object o)
            {
                return o == this;
            }
        }

        private ASN1Object END_OF_STREAM = new EOS_ASN1Object();
        internal bool eofFound = false;
    
        public ASN1InputStream(Stream inputStream) : base(inputStream)
        {
        }
    
        protected int readLength()
        {
            int length = ReadByte();
            if (length < 0)
            {
                throw new IOException("EOF found when length expected");
            }
    
            if (length == 0x80)
            {
                return -1;      // indefinite-length encoding
            }
    
            if (length > 127)
            {
                int size = length & 0x7f;

                if (size > 4)
                {
                    throw new IOException("DER length more than 4 bytes");
                }
                
                length = 0;
                for (int i = 0; i < size; i++)
                {
                    int next = ReadByte();
    
                    if (next < 0)
                    {
                        throw new IOException("EOF found reading length");
                    }
    
                    length = (length << 8) + next;
                }

                if (length < 0)
                {
                    throw new IOException("corrupted steam - negative length found");
                }
            }
    
            return length;
        }
    
        protected void readFully(
            byte[]  bytes)
        {
            int     left = bytes.Length;
            int     len;
    
            if (left == 0)
            {
                return;
            }
    
            while ((len = Read(bytes, bytes.Length - left, left)) > 0)
            {
                if ((left -= len) == 0)
                {
                    return;
                }
            }
    
            if (left != 0)
            {
                throw new EndOfStreamException("EOF encountered in middle of object");
            }
        }
    
        /**
         * build an object given its tag and a byte stream to construct it
         * from.
         */
        protected ASN1Object buildObject(
            int       derTags,
            byte[]    bytes)
        {
            int tag = derTags;

            if ((tag & ASN1Tags.APPLICATION) != 0)
            {
                return new DERApplicationSpecific(derTags, bytes);
            }
            
            switch (tag)
            {
            case ASN1Tags.NULL:
                return new DERNull();   
            case ASN1Tags.SEQUENCE | ASN1Tags.CONSTRUCTED:
                MemoryStream bIn = new MemoryStream(bytes);
                ASN1InputStream         aIn = new ASN1InputStream(bIn);
                ASN1EncodableVector     v = new ASN1EncodableVector();
    
                ASN1Object   obj = aIn.readObject();
    
                while (obj != null)
                {
                    v.add(obj);
                    obj = aIn.readObject();
                }
    
                return new DERSequence(v);
            case ASN1Tags.SET | ASN1Tags.CONSTRUCTED:
                bIn = new MemoryStream(bytes);
                aIn = new ASN1InputStream(bIn);
                v = new ASN1EncodableVector();
    
                obj = aIn.readObject();
    
                while (obj != null)
                {
                    v.add(obj);
                    obj = aIn.readObject();
                }
    
                return new DERSet(v, false);
            case ASN1Tags.BOOLEAN:
                return new DERBoolean(bytes);
            case ASN1Tags.INTEGER:
                return new DERInteger(bytes);
            case ASN1Tags.ENUMERATED:
                return new DEREnumerated(bytes);
            case ASN1Tags.OBJECT_IDENTIFIER:
                return new DERObjectIdentifier(bytes);
            case ASN1Tags.BIT_STRING:
                int     padBits = bytes[0];
                byte[]  data = new byte[bytes.Length - 1];
    
                Array.Copy(bytes, 1, data, 0, bytes.Length - 1);
    
                return new DERBitString(data, padBits);
            case ASN1Tags.NUMERIC_STRING:
                return new DERNumericString(bytes);
            case ASN1Tags.UTF8_STRING:
                return new DERUTF8String(bytes);
            case ASN1Tags.PRINTABLE_STRING:
                return new DERPrintableString(bytes);
            case ASN1Tags.IA5_STRING:
                return new DERIA5String(bytes);
            case ASN1Tags.T61_STRING:
                return new DERT61String(bytes);
            case ASN1Tags.VISIBLE_STRING:
                return new DERVisibleString(bytes);
            case ASN1Tags.GENERAL_STRING:
                return new DERGeneralString(bytes);
            case ASN1Tags.UNIVERSAL_STRING:
                return new DERUniversalString(bytes);
            case ASN1Tags.BMP_STRING:
                return new DERBMPString(bytes);
            case ASN1Tags.OCTET_STRING:
                return new DEROctetString(bytes);
            case ASN1Tags.UTC_TIME:
                return new DERUTCTime(bytes);
            case ASN1Tags.GENERALIZED_TIME:
                return new DERGeneralizedTime(bytes);
            default:
                //
                // with tagged object tag number is bottom 5 bits
                //
                if ((tag & (int) ASN1Tags.TAGGED) != 0)
                {
                    int tagNo = tag & 0x1f;
    
                    if (tagNo == 0x1f)
                    {
                        int idx = 0;
    
                        tagNo = 0;
    
                        while ((bytes[idx] & 0x80) != 0)
                        {
                            tagNo |= (bytes[idx++] & 0x7f);
                            tagNo <<= 7;
                        }
    
                        tagNo |= (bytes[idx] & 0x7f);
    
                        byte[]  tmp = bytes;
    
                        bytes = new byte[tmp.Length - (idx + 1)];
                        Array.Copy(tmp, idx + 1, bytes, 0, bytes.Length);
                    }
    
                    if (bytes.Length == 0)        // empty tag!
                    {
                        if ((tag & (int) ASN1Tags.CONSTRUCTED) == 0)
                        {
                            return new DERTaggedObject(false, tagNo, new DERNull());
                        }
                        else
                        {
                            return new DERTaggedObject(false, tagNo, new DERSequence());
                        }
                    }
    
                    //
                    // simple type - implicit... return an octet string
                    //
                    if ((tag & (int) ASN1Tags.CONSTRUCTED) == 0)
                    {
                        return new DERTaggedObject(false, tagNo, new DEROctetString(bytes));
                    }
    
                    bIn = new MemoryStream(bytes);
                    aIn = new ASN1InputStream(bIn);
    
                    ASN1Encodable dObj = aIn.readObject();
    
                    //
                    // explicitly tagged (probably!) - if it isn't we'd have to
                    // tell from the context
                    //
                    
//                    if (aIn.available() == 0)
                    if (aIn.Position == bytes.Length) //FIXME?
                    {
                        return new DERTaggedObject(tagNo, dObj);
                    }
    
                    //
                    // another implicit object, we'll create a sequence...
                    //
                    v = new ASN1EncodableVector();
    
                    while (dObj != null)
                    {
                        v.add(dObj);
                        dObj = aIn.readObject();
                    }
    
                    return new DERTaggedObject(false, tagNo, new DERSequence(v));
                }
    
                return new DERUnknownTag(tag, bytes);
            }
        }
    
        /**
         * read a string of bytes representing an indefinite length object.
         */
        private byte[] readIndefiniteLengthFully()
        {
            MemoryStream   bOut = new MemoryStream();
            int                     b, b1;
    
            b1 = ReadByte();
    
            while ((b = ReadByte()) >= 0)
            {
                if (b1 == 0 && b == 0)
                {
                    break;
                }
    
                bOut.WriteByte((byte) b1);
                b1 = b;
            }
    
            return bOut.ToArray();
        }
    
        private BEROctetString buildConstructedOctetString()
        {
            ArrayList               octs = new ArrayList();
    
            for (;;)
            {
                ASN1Object        o = readObject();
    
                if (o == END_OF_STREAM)
                {
                    break;
                }
    
                octs.Add(o);
            }
    
            return new BEROctetString(octs);
        }
    
        public ASN1Object readObject()
        {
            int tag = ReadByte();
            if (tag == -1)
            {
                if (eofFound)
                {
                    throw new EndOfStreamException("attempt to read past end of file.");
                }
    
                eofFound = true;
    
                return null;
            }
        
            int     length = readLength();
    
            if (length < 0)    // indefinite length method
            {
                switch (tag)
                {
                case ASN1Tags.NULL:
                    return new BERNull();
                case ASN1Tags.SEQUENCE | ASN1Tags.CONSTRUCTED:
                    ASN1EncodableVector  v = new ASN1EncodableVector();
        
                    for (;;)
                    {
                        ASN1Object   obj = readObject();
    
                        if (obj == END_OF_STREAM)
                        {
                            break;
                        }
    
                        v.add(obj);
                    }
                    return new BERSequence(v);
                case ASN1Tags.SET | ASN1Tags.CONSTRUCTED:
                    v = new ASN1EncodableVector();
        
                    for (;;)
                    {
                        ASN1Object   obj = readObject();
    
                        if (obj == END_OF_STREAM)
                        {
                            break;
                        }
    
                        v.add(obj);
                    }
                    return new BERSet(v, false);
                case ASN1Tags.OCTET_STRING | ASN1Tags.CONSTRUCTED:
                    return buildConstructedOctetString();
                default:
                    //
                    // with tagged object tag number is bottom 5 bits
                    //
                    if ((tag & (int) ASN1Tags.TAGGED) != 0)
                    {
                        int tagNo = tag & 0x1f;
    
                        if (tagNo == 0x1f)
                        {
                            int b = ReadByte();
    
                            tagNo = 0;
    
                            while ((b >= 0) && ((b & 0x80) != 0))
                            {
                                tagNo |= (b & 0x7f);
                                tagNo <<= 7;
                                b = ReadByte();
                            }
    
                            tagNo |= (b & 0x7f);
                        }
    
                        //
                        // simple type - implicit... return an octet string
                        //
                        if ((tag & (int) ASN1Tags.CONSTRUCTED) == 0)
                        {
                            byte[]  bytes = readIndefiniteLengthFully();
    
                            return new BERTaggedObject(false, tagNo, new DEROctetString(bytes));
                        }
    
                        //
                        // either constructed or explicitly tagged
                        //
                        ASN1Object        dObj = readObject();
    
                        if (dObj == END_OF_STREAM)     // empty tag!
                        {
                            return new DERTaggedObject(tagNo);
                        }
    
                        ASN1Object       next = readObject();
    
                        //
                        // explicitly tagged (probably!) - if it isn't we'd have to
                        // tell from the context
                        //
                        if (next == END_OF_STREAM)
                        {
                            return new BERTaggedObject(tagNo, dObj);
                        }
    
                        //
                        // another implicit object, we'll create a sequence...
                        //
                        v = new ASN1EncodableVector();
    
                        v.add(dObj);
    
                        do
                        {
                            v.add(next);
                            next = readObject();
                        }
                        while (next != END_OF_STREAM);
    
                        return new BERTaggedObject(false, tagNo, new BERSequence(v));
                    }
    
                    throw new IOException("unknown BER object encountered");
                }
            }
            else
            {
                if (tag == 0 && length == 0)    // end of contents marker.
                {
                    return END_OF_STREAM;
                }
    
                byte[]  bytes = new byte[length];
        
                readFully(bytes);
        
                return buildObject(tag, bytes);
            }
        }
    }
}
