using org.bouncycastle.asn1;

using System.Collections;

namespace org.bouncycastle.asn1.x509
{
    public class AttributeTable
    {
        private Hashtable attributes = new Hashtable();
    
        public AttributeTable(
            Hashtable  attrs)
        {
            attributes = new Hashtable(attrs);
        }
    
        public AttributeTable(
            ASN1EncodableVector v)
        {
            for (int i = 0; i != v.size(); i++)
            {
                Attribute   a = Attribute.getInstance(v.get(i));
    
                attributes.Add(a.getAttrType(), a);
            }
        }
    
        public AttributeTable(
            ASN1Set    s)
        {
            for (int i = 0; i != s.size(); i++)
            {
                Attribute   a = Attribute.getInstance(s.getObjectAt(i));
    
                attributes.Add(a.getAttrType(), a);
            }
        }
    
        public Attribute get(
            DERObjectIdentifier oid)
        {
            return (Attribute)attributes[oid];
        }
    
        public Hashtable toHashtable()
        {
            return new Hashtable(attributes);
        }
    }
}
