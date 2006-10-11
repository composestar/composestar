using System;
using System.Collections;

namespace org.bouncycastle.asn1
{
    abstract public class ASN1Set
        : ASN1Object
    {
        protected ArrayList set = new ArrayList();
    
        /**
         * return an ASN1Set from the given object.
         *
         * @param obj the object we want converted.
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static ASN1Set getInstance(
            object  obj)
        {
            if (obj == null || obj is ASN1Set)
            {
                return (ASN1Set)obj;
            }
    
            throw new ArgumentException("unknown object in getInstance");
        }
    
        /**
         * Return an ASN1 set from a tagged object. There is a special
         * case here, if an object appears to have been explicitly tagged on 
         * reading but we were expecting it to be implictly tagged in the 
         * normal course of events it indicates that we lost the surrounding
         * set - so we need to add it back (this will happen if the tagged
         * object is a sequence that contains other sequences). If you are
         * dealing with implicitly tagged sets you really <b>should</b>
         * be using this method.
         *
         * @param obj the tagged object.
         * @param explicit true if the object is meant to be explicitly tagged
         *          false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         *          be converted.
         */
        public static ASN1Set getInstance(
            ASN1TaggedObject    obj,
            bool             explicitly)
        {
            if (explicitly)
            {
                if (!obj.isExplicit())
                {
                    throw new ArgumentException("object implicit - explicit expected.");
                }
    
                return (ASN1Set)obj.getObject();
            }
            else
            {
                //
                // constructed object which appears to be explicitly tagged
                // and it's really implicit means we have to add the
                // surrounding sequence.
                //
                if (obj.isExplicit())
                {
                    ASN1Set    set = new DERSet(obj.getObject());
    
                    return set;
                }
                else
                {
                    if (obj.getObject() is ASN1Set)
                    {
                        return (ASN1Set)obj.getObject();
                    }
    
                    //
                    // in this case the parser returns a sequence, convert it
                    // into a set.
                    //
                    ASN1EncodableVector  v = new ASN1EncodableVector();
    
                    if (obj.getObject() is ASN1Sequence)
                    {
                        ASN1Sequence s = (ASN1Sequence)obj.getObject();
                        IEnumerator e = s.getObjects();
    
                        while (e.MoveNext())
                        {
                            v.add((ASN1Encodable)e.Current);
                        }
    
                        return new DERSet(v);
                    }
                }
            }
    
            throw new ArgumentException(
                        "unknown object in getInstanceFromTagged");
        }
    
        public ASN1Set()
        {
        }
    
        public IEnumerator getObjects()
        {
            return set.GetEnumerator();
        }
    
        /**
         * return the object at the set postion indicated by index.
         *
         * @param index the set number (starting at zero) of the object
         * @return the object at the set postion indicated by index.
         */
        public ASN1Encodable getObjectAt(
            int index)
        {
             return (ASN1Encodable)set[index];
        }
    
        /**
         * return the number of objects in this set.
         *
         * @return the number of objects in this set.
         */
        public int size()
        {
            return set.Count;
        }

        public override int GetHashCode()
        {
            IEnumerator             e = this.getObjects();
            int                     GetHashCode = 0;
    
            while (e.MoveNext())
            {
                GetHashCode ^= e.Current.GetHashCode();
            }
    
            return GetHashCode;
        }
    
        public override bool Equals(
            object  o)
        {
            if (o == null || !(o is ASN1Set))
            {
                return false;
            }
    
            ASN1Set   other = (ASN1Set)o;
    
            if (this.size() != other.size())
            {
                return false;
            }
    
            IEnumerator s1 = this.getObjects();
            IEnumerator s2 = other.getObjects();

            while (s1.MoveNext() && s2.MoveNext())
            {
                if (!s1.Current.Equals(s2.Current))
                {
                    return false;
                }
            }
    
            return true;
        }
    

        /**
         * return true if a <= b (arrays are assumed padded with zeros).
         */
        private bool lessThanOrEqual(
             byte[] a,
             byte[] b)
        {
             if (a.Length <= b.Length)
             {
                 for (int i = 0; i != a.Length; i++)
                 {
                     int    l = a[i] & 0xff;
                     int    r = b[i] & 0xff;
                     
                     if (r > l)
                     {
                         return true;
                     }
                     else if (l > r)
                     {
                         return false;
                     }
                 }
    
                 return true;
             }
             else
             {
                 for (int i = 0; i != b.Length; i++)
                 {
                     int    l = a[i] & 0xff;
                     int    r = b[i] & 0xff;
                     
                     if (r > l)
                     {
                         return true;
                     }
                     else if (l > r)
                     {
                         return false;
                     }
                 }
    
                 return false;
             }
        }
    
        protected void sort()
        {
            if (set.Count > 1)
            {
                bool	swapped = true;
    
                while (swapped)
                {
                    int    index = 0;
                    byte[] a = ((ASN1Encodable)set[0]).getEncoded();
                    
                    swapped = false;
                    
                    while (index != set.Count - 1)
                    {
                        byte[] b = ((ASN1Encodable)set[index + 1]).getEncoded();
    
                        if (lessThanOrEqual(a, b))
                        {
                            a = b;
                        }
                        else
                        {
                            Object  o = set[index];
    
                            set[index] = set[index + 1];
                            set[index + 1] = o;
    
                            swapped = true;
                        }
    
                        index++;
                    }
                }
            }
        }

        protected void addObject(
            ASN1Encodable obj)
        {
            set.Add(obj);
        }
    }
}
