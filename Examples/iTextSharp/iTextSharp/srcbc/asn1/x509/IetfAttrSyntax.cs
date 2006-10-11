using System;
using System.Collections;

using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.x509
{
    /**
     * Implementation of <code>IetfAttrSyntax</code> as specified by RFC3281.
     */
    public class IetfAttrSyntax
        : ASN1Encodable
    {
        public const int VALUE_OCTETS    = 1;
        public const int VALUE_OID       = 2;
        public const int VALUE_UTF8      = 3;
        GeneralNames            policyAuthority = null;
        ArrayList               values          = new ArrayList();
        int                     valueChoice     = -1;
    
        /**
         *  
         */
        public IetfAttrSyntax(ASN1Sequence seq)
        {
            int i = 0;
    
            if (seq.getObjectAt(0) is ASN1TaggedObject)
            {
                policyAuthority = GeneralNames.getInstance(((ASN1TaggedObject)seq.getObjectAt(0)), false);
                i++;
            }
            else if (seq.size() == 2)
            { // VOMS fix
                policyAuthority = GeneralNames.getInstance(seq.getObjectAt(0));
                i++;
            }
    
            if (!(seq.getObjectAt(i) is ASN1Sequence))
            {
                throw new ArgumentException("Non-IetfAttrSyntax encoding");
            }
    
            seq = (ASN1Sequence)seq.getObjectAt(i);
    
            for (IEnumerator e = seq.getObjects(); e.MoveNext();)
            {
                ASN1Object obj = (ASN1Object)e.Current;
                int type;
    
                if (obj is DERObjectIdentifier)
                {
                    type = VALUE_OID;
                }
                else if (obj is DERUTF8String)
                {
                    type = VALUE_UTF8;
                }
                else if (obj is DEROctetString)
                {
                    type = VALUE_OCTETS;
                }
                else
                {
                    throw new ArgumentException("Bad value type encoding IetfAttrSyntax");
                }
    
                if (valueChoice < 0)
                {
                    valueChoice = type;
                }
    
                if (type != valueChoice)
                {
                    throw new ArgumentException("Mix of value types in IetfAttrSyntax");
                }
    
                values.Add(obj);
            }
        }
    
        public GeneralNames getPolicyAuthority()
        {
            return policyAuthority;
        }
    
        public int getValueType()
        {
            return valueChoice;
        }
    
        public Object[] getValues()
        {
            if (this.getValueType() == VALUE_OCTETS)
            {
                ASN1OctetString[] tmp = new ASN1OctetString[values.Count];
                
                for (int i = 0; i != tmp.Length; i++)
                {
                    tmp[i] = (ASN1OctetString)values[i];
                }
                
                return tmp;
            }
            else if (this.getValueType() == VALUE_OID)
            {
                DERObjectIdentifier[] tmp = new DERObjectIdentifier[values.Count];
                
                for (int i = 0; i != tmp.Length; i++)
                {
                    tmp[i] = (DERObjectIdentifier)values[i];
                }
                
                return tmp;
            }
            else
            {
                DERUTF8String[] tmp = new DERUTF8String[values.Count];
                
                for (int i = 0; i != tmp.Length; i++)
                {
                    tmp[i] = (DERUTF8String)values[i];
                }
                
                return tmp;
            }
        }
    
        /**
         * 
         * <pre>
         * 
         *  IetfAttrSyntax ::= SEQUENCE {
         *    policyAuthority [0] GeneralNames OPTIONAL,
         *    values SEQUENCE OF CHOICE {
         *      octets OCTET STRING,
         *      oid OBJECT IDENTIFIER,
         *      string UTF8String
         *    }
         *  }
         *  
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector v = new ASN1EncodableVector();
    
            if (policyAuthority != null)
            {
                v.add(new DERTaggedObject(0, policyAuthority));
            }
    
            ASN1EncodableVector v2 = new ASN1EncodableVector();
    
            for (IEnumerator i = values.GetEnumerator(); i.MoveNext();)
            {
                v2.add((ASN1Encodable)i.Current);
            }
    
            v.add(new DERSequence(v2));
    
            return new DERSequence(v);
        }
    }
}
