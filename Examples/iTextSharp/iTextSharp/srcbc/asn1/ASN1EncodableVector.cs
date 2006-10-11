using System.Collections;

namespace org.bouncycastle.asn1
{
    /**
     * the parent class for this will eventually disappear. Use this one!
     */
    public class ASN1EncodableVector
    {
        private ArrayList  v = new ArrayList();
    
        public void add(
            ASN1Encodable   obj)
        {
            v.Add(obj);
        }
    
        public ASN1Encodable get(
            int i)
        {
            return (ASN1Encodable) v[i];
        }
    
        public int size()
        {
            return v.Count;
        }
    }
}
