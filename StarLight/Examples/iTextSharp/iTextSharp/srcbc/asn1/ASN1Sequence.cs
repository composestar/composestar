using System;
using System.Collections;

namespace org.bouncycastle.asn1
{
    public abstract class ASN1Sequence : ASN1Object
    {
        private ArrayList seq = new ArrayList();
    
        /**
         * return an ASN1Sequence from the given object.
         *
         * @param obj the object we want converted.
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static ASN1Sequence getInstance(
            object  obj)
        {
            if (obj == null || obj is ASN1Sequence)
            {
                return (ASN1Sequence)obj;
            }
    
            throw new ArgumentException("unknown object in getInstance");
        }
    
        /**
         * Return an ASN1 sequence from a tagged object. There is a special
         * case here, if an object appears to have been explicitly tagged on 
         * reading but we were expecting it to be implictly tagged in the 
         * normal course of events it indicates that we lost the surrounding
         * sequence - so we need to add it back (this will happen if the tagged
         * object is a sequence that contains other sequences). If you are
         * dealing with implicitly tagged sequences you really <b>should</b>
         * be using this method.
         *
         * @param obj the tagged object.
         * @param explicit true if the object is meant to be explicitly tagged,
         *          false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         *          be converted.
         */
        public static ASN1Sequence getInstance(
            ASN1TaggedObject    obj,
            bool             explicitly)
        {
            if (explicitly)
            {
                if (!obj.isExplicit())
                {
                    throw new ArgumentException("object implicit - explicit expected.");
                }
    
                return (ASN1Sequence)obj.getObject();
            }
            else
            {
                //
                // constructed object which appears to be explicitly tagged
                // when it should be implicit means we have to add the
                // surrounding sequence.
                //
                if (obj.isExplicit())
                {
                    if (obj is BERTaggedObject)
                    {
                        return new BERSequence(obj.getObject());
                    }
                    else
                    {
                        return new DERSequence(obj.getObject());
                    }
                }
                else
                {
                    if (obj.getObject() is ASN1Sequence)
                    {
                        return (ASN1Sequence)obj.getObject();
                    }
                }
            }
    
            throw new ArgumentException(
                    "unknown object in getInstanceFromTagged");
        }
    
        public IEnumerator getObjects()
        {
            return seq.GetEnumerator();
        }
    
        /**
         * return the object at the sequence postion indicated by index.
         *
         * @param index the sequence number (starting at zero) of the object
         * @return the object at the sequence postion indicated by index.
         */
        public ASN1Encodable getObjectAt(
            int index)
        {
             return (ASN1Encodable)seq[index];
        }
    
        /**
         * return the number of objects in this sequence.
         *
         * @return the number of objects in this sequence.
         */
        public int size()
        {
            return seq.Count;
        }
    
        public override int GetHashCode()
        {
            IEnumerator             e = this.getObjects();
            int                     GetHashCode = 0;
    
            while (e.MoveNext())
            {
                object    o = e.Current;
                
                if (o != null)
                {
                    GetHashCode ^= o.GetHashCode();
                }
            }
    
            return GetHashCode;
        }
    
        public override bool Equals(
            object  o)
        {
            if (o == null || !(o is ASN1Sequence))
            {
                return false;
            }
    
            ASN1Sequence   other = (ASN1Sequence)o;
    
            if (this.size() != other.size())
            {
                return false;
            }
    
            IEnumerator s1 = this.getObjects();
            IEnumerator s2 = other.getObjects();

            while (s1.MoveNext())
            {
                object o1 = s1.Current;
                object o2 = s2.MoveNext() ? s2.Current : null;
                
                if (o1 != null && o2 != null)
                {
                    if (!o1.Equals(o2))
                    {
                        return false;
                    }
                }
                else if (o1 == null && o2 == null)
                {
                    continue;
                }
                else
                {
                    return false;
                }
            }
    
            return true;
        }
    
        protected void addObject(
            ASN1Encodable obj)
        {
            seq.Add(obj);
        }
    }
}
