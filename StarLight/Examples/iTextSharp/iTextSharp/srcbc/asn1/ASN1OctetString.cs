using System;
using System.Collections;
using System.IO;

namespace org.bouncycastle.asn1
{
    public abstract class ASN1OctetString
        : ASN1Object
    {
        internal byte[]  str;
    
        /**
         * return an Octet string from a tagged object.
         *
         * @param obj the tagged object holding the object we want.
         * @param explicit true if the object is meant to be explicitly
         *              tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         *              be converted.
         */
        public static ASN1OctetString getInstance(
            ASN1TaggedObject    obj,
            bool             explicitly)
        {
            return getInstance(obj.getObject());
        }
        
        /**
         * return an Octet string from the given object.
         *
         * @param obj the object we want converted.
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static ASN1OctetString getInstance(
            object  obj)
        {
            if (obj == null || obj is ASN1OctetString)
            {
                return (ASN1OctetString)obj;
            }
    
            if (obj is ASN1TaggedObject)
            {
                return getInstance(((ASN1TaggedObject)obj).getObject());
            }
    
            if (obj is ASN1Sequence)
            {
                ArrayList      v = new ArrayList();
                IEnumerator e = ((ASN1Sequence)obj).getObjects();
    
                while (e.MoveNext())
                {
                    v.Add(e.Current);
                }

                return new BEROctetString(v);
            }

            throw new ArgumentException("illegal object in getInstance: " + obj.GetType().Name);
        }
    
        /**
         * @param string the octets making up the octet string.
         */
        protected ASN1OctetString(
            byte[]  str)
        {
            this.str = str;
        }
    
        protected ASN1OctetString(
            ASN1Encodable obj)
        {
            try
            {
                MemoryStream   bOut = new MemoryStream();
                DEROutputStream         dOut = new DEROutputStream(bOut);
    
                dOut.writeObject(obj);
                dOut.Close();
    
                this.str = bOut.ToArray();
            }
            catch (IOException e)
            {
                throw new ArgumentException("Error processing object : " + e.ToString());
            }
        }
    
        public virtual byte[] getOctets()
        {
            return str;
        }
    
        public override int GetHashCode()
        {
            byte[]  b = this.getOctets();
            int     value = 0;
    
            for (int i = 0; i != b.Length; i++)
            {
                value ^= (b[i] & 0xff) << (i % 4);
            }
    
            return value;
        }
    
        public override bool Equals(
            object  o)
        {
            if (o == null || !(o is DEROctetString))
            {
                return false;
            }
    
            DEROctetString  other = (DEROctetString)o;
    
            byte[] b1 = other.getOctets();
            byte[] b2 = this.getOctets();
    
            if (b1.Length != b2.Length)
            {
                return false;
            }
    
            for (int i = 0; i != b1.Length; i++)
            {
                if (b1[i] != b2[i])
                {
                    return false;
                }
            }
    
            return true;
        }
    }
}
