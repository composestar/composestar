using System;

namespace org.bouncycastle.asn1
{
    /**
     * ASN.1 TaggedObject - in ASN.1 nottation this is any object proceeded by
     * a [n] where n is some number - these are assume to follow the construction
     * rules (as with sequences).
     */
    public abstract class ASN1TaggedObject
        : ASN1Object
    {
        internal int            tagNo;
        internal bool           empty = false;
        internal bool           explicitly = true;
        internal ASN1Encodable  obj = null;
    
        static public ASN1TaggedObject getInstance(
            ASN1TaggedObject    obj,
            bool             explicitly)
        {
            if (explicitly)
            {
                return (ASN1TaggedObject)obj.getObject();
            }
    
            throw new ArgumentException("implicitly tagged tagged object");
        }
    
        /**
         * @param tagNo the tag number for this object.
         * @param obj the tagged object.
         */
        public ASN1TaggedObject(
            int             tagNo,
            ASN1Encodable   obj)
        {
            this.explicitly = true;
            this.tagNo = tagNo;
            this.obj = obj;
        }
    
        /**
         * @param explicit true if the object is explicitly tagged.
         * @param tagNo the tag number for this object.
         * @param obj the tagged object.
         */
        public ASN1TaggedObject(
            bool            explicitly,
            int             tagNo,
            ASN1Encodable   obj)
        {
            this.explicitly = explicitly;
            this.tagNo = tagNo;
            this.obj = obj;
        }
        
        public override bool Equals(
            object o)
        {
            if (o == null || !(o is ASN1TaggedObject))
            {
                return false;
            }
            
            ASN1TaggedObject other = (ASN1TaggedObject)o;
            
            if (tagNo != other.tagNo || empty != other.empty || explicitly != other.explicitly)
            {
                return false;
            }
            
            if(obj == null)
            {
                if(other.obj != null)
                {
                    return false;
                }
            }
            else
            {
                if(!(obj.Equals(other.obj)))
                {
                    return false;
                }
            }
            
            return true;
        }
        
        public override int GetHashCode()
        {
            int code = (int) tagNo;
    
            if (obj != null)
            {
                code ^= obj.GetHashCode();
            }
    
            return code;
        }
    
        public int getTagNo()
        {
            return tagNo;
        }
    
        /**
         * return whether or not the object may be explicitly tagged. 
         * <p>
         * Note: if the object has been read from an input stream, the only
         * time you can be sure if isExplicit is returning the true state of
         * affairs is if it returns false. An implicitly tagged object may appear
         * to be explicitly tagged, so you need to understand the context under
         * which the reading was done as well, see getObject below.
         */
        public bool isExplicit()
        {
            return explicitly;
        }
    
        public bool isEmpty()
        {
            return empty;
        }
    
        /**
         * return whatever was following the tag.
         * <p>
         * Note: tagged objects are generally context dependent if you're
         * trying to extract a tagged object you should be going via the
         * appropriate getInstance method.
         */
        public ASN1Object getObject()
        {
            if (obj != null)
            {
                return obj.toASN1Object();
            }
    
            return null;
        }
    }
}
