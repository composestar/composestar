using System;
using System.Collections;
using System.IO;

namespace org.bouncycastle.asn1
{
    public class BEROctetString
        : DEROctetString
    {
        /**
         * convert a vector of octet strings into a single byte string
         */
        static private byte[] toBytes(
            ArrayList  octs)
        {
            MemoryStream   bOut = new MemoryStream();
    
            for (int i = 0; i != octs.Count; i++)
            {
                DEROctetString  o = (DEROctetString)octs[i];
    
                try
                {
                    byte[] octets = o.getOctets();
                    bOut.Write(octets, 0, octets.Length);
                }
                catch (IOException e)
                {
                    throw new Exception("exception converting octets " + e.ToString());
                }
            }
    
            return bOut.ToArray();
        }
    
        private ArrayList  octs;
    
        /**
         * @param string the octets making up the octet string.
         */
         public BEROctetString(byte[] str) : base(str)
        {
        }
    
         public BEROctetString(ArrayList octs) : base(toBytes(octs))
        {
            this.octs = octs;
        }
    
        public BEROctetString(ASN1Object obj) : base(obj)
        {
        }
    
        public BEROctetString(ASN1Encodable obj) : base(obj.toASN1Object())
        {
        }
    
        public override byte[] getOctets()
        {
            return str;
        }
    
        /**
         * return the DER octets that make up this string.
         */
        public IEnumerator getObjects()
        {
            if (octs == null)
            {
                return generateOcts().GetEnumerator();
            }

            return octs.GetEnumerator();
        }

        private ArrayList generateOcts()
        {
            int     start = 0;
            int     end = 0;
            ArrayList  vec = new ArrayList();
    
            while ((end + 1) < str.Length)
            {
                if (str[end] == 0 && str[end + 1] == 0)
                {
                    byte[]  nStr = new byte[end - start + 1];
    
                    Array.Copy(str, start, nStr, 0, nStr.Length);
    
                    vec.Add(new DEROctetString(nStr));
                    start = end + 1;
                }
                end++;
            }
    
            byte[]  nStr2 = new byte[str.Length - start];
    
            Array.Copy(str, start, nStr2, 0, nStr2.Length);
    
            vec.Add(new DEROctetString(nStr2));
    
            return vec;
        }
    
        internal override void encode(
            DEROutputStream derOut)
        {
            if (derOut is ASN1OutputStream || derOut is BEROutputStream)
            {
                derOut.WriteByte((byte)(ASN1Tags.CONSTRUCTED | ASN1Tags.OCTET_STRING));
    
                derOut.WriteByte(0x80);
    
                //
                // write out the octet array
                //
                if (octs != null)
                {
                    for (int i = 0; i != octs.Count; i++)
                    {
                        derOut.writeObject(octs[i]);
                    }
                }
                else
                {
                    int     start = 0;
                    int     end = 0;
    
                    while ((end + 1) < str.Length)
                    {
                        if (str[end] == 0 && str[end + 1] == 0)
                        {
                            byte[]  nStr = new byte[end - start + 1];
    
                            Array.Copy(str, start, nStr, 0, nStr.Length);
    
                            derOut.writeObject(new DEROctetString(nStr));
                            start = end + 1;
                        }
                        end++;
                    }
    
                    byte[]  nStr2 = new byte[str.Length - start];
    
                    Array.Copy(str, start, nStr2, 0, nStr2.Length);
    
                    derOut.writeObject(new DEROctetString(nStr2));
                }
    
                derOut.WriteByte(0x00);
                derOut.WriteByte(0x00);
            }
            else
            {
                base.encode(derOut);
            }
        }
    }
}
