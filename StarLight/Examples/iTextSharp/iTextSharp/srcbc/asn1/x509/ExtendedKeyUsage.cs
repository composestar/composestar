using org.bouncycastle.asn1;

using System;
using System.Collections;

namespace org.bouncycastle.asn1.x509
{
    /**
     * The extendedKeyUsage object.
     * <pre>
     *      extendedKeyUsage ::= SEQUENCE SIZE (1..MAX) OF KeyPurposeId
     * </pre>
     */
    public class ExtendedKeyUsage
        : ASN1Encodable
    {
        Hashtable     usageTable = new Hashtable();
        ASN1Sequence  seq;
    
        public static ExtendedKeyUsage getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public static ExtendedKeyUsage getInstance(
            object obj)
        {
            if(obj == null || obj is ExtendedKeyUsage) 
            {
                return (ExtendedKeyUsage)obj;
            }
            
            if(obj is ASN1Sequence) 
            {
                return new ExtendedKeyUsage((ASN1Sequence)obj);
            }
            
            throw new ArgumentException("Invalid ExtendedKeyUsage: " + obj.GetType().Name);
        }
    
        public ExtendedKeyUsage(
            ASN1Sequence  seq)
        {
            this.seq = seq;
    
            IEnumerator e = seq.getObjects();
    
            while (e.MoveNext())
            {
                object  o = e.Current;
    
                this.usageTable.Add(o, o);
            }
        }
    
        public ExtendedKeyUsage(
            ArrayList  usages)
        {
            ASN1EncodableVector v = new ASN1EncodableVector();
            IEnumerator         e = usages.GetEnumerator();
    
            while (e.MoveNext())
            {
                ASN1Object  o = (ASN1Object)e.Current;
    
                v.add(o);
                this.usageTable.Add(o, o);
            }
    
            this.seq = new DERSequence(v);
        }
    
        public bool hasKeyPurposeId(
            KeyPurposeId keyPurposeId)
        {
            return (usageTable[keyPurposeId] != null);
        }
    
        public override ASN1Object toASN1Object()
        {
            return seq;
        }
    }
}
