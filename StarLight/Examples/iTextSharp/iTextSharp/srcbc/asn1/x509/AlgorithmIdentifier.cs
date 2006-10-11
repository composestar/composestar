using System;

namespace org.bouncycastle.asn1.x509
{
    public class AlgorithmIdentifier
        : ASN1Encodable
    {
        private DERObjectIdentifier objectId;
        private ASN1Encodable       parameters;
    
        public static AlgorithmIdentifier getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
        
        public static AlgorithmIdentifier getInstance(
            object  obj)
        {
            if (obj is AlgorithmIdentifier)
            {
                return (AlgorithmIdentifier)obj;
            }
            
            if (obj is DERObjectIdentifier)
            {
                return new AlgorithmIdentifier((DERObjectIdentifier)obj);
            }
    
            if (obj is string)
            {
                return new AlgorithmIdentifier((string)obj);
            }
    
            if (obj is ASN1Sequence)
            {
                return new AlgorithmIdentifier((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public AlgorithmIdentifier(
            DERObjectIdentifier     objectId)
        {
            this.objectId = objectId;
        }
    
        public AlgorithmIdentifier(
            string     objectId)
        {
            this.objectId = new DERObjectIdentifier(objectId);
        }
    
        public AlgorithmIdentifier(
            DERObjectIdentifier     objectId,
            ASN1Encodable            parameters)
        {
            this.objectId = objectId;
            this.parameters = parameters;
        }
    
        public AlgorithmIdentifier(
            ASN1Sequence   seq)
        {
           
                objectId = (DERObjectIdentifier)seq.getObjectAt(0);

            if (seq.size() == 2)
            {
                parameters = seq.getObjectAt(1);
            }
            else
            {
                parameters = null;
            }
        }
    
        public virtual DERObjectIdentifier getObjectId()
        {
            return objectId;
        }
    
        public ASN1Encodable getParameters()
        {
            return parameters;
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         *      AlgorithmIdentifier ::= SEQUENCE {
         *                            algorithm OBJECT IDENTIFIER,
         *                            parameters ANY DEFINED BY algorithm OPTIONAL }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(objectId);
    
            if (parameters != null)
            {
                v.add(parameters);
            }
    
            return new DERSequence(v);
        }
    }
}
