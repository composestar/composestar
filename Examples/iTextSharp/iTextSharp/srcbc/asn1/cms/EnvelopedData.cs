using org.bouncycastle.asn1;

using System;
using System.Collections;

namespace org.bouncycastle.asn1.cms
{
    public class EnvelopedData
        : ASN1Encodable
    {
        private DERInteger              version;
        private OriginatorInfo          originatorInfo;
        private ASN1Set                 recipientInfos;
        private EncryptedContentInfo    encryptedContentInfo;
        private ASN1Set                 unprotectedAttrs;
    
        public EnvelopedData(
            OriginatorInfo          originatorInfo,
            ASN1Set                 recipientInfos,
            EncryptedContentInfo    encryptedContentInfo,
            ASN1Set                 unprotectedAttrs)
        {
            if (originatorInfo != null || unprotectedAttrs != null)
            {
                version = new DERInteger(2);
            }
            else
            {
                version = new DERInteger(0);
    
                IEnumerator e = recipientInfos.getObjects();
    
                while (e.MoveNext())
                {
                    RecipientInfo   ri = RecipientInfo.getInstance(e.Current);
    
                    if (!ri.getVersion().Equals(version))
                    {
                        version = new DERInteger(2);
                        break;
                    }
                }
            }
    
            this.originatorInfo = originatorInfo;
            this.recipientInfos = recipientInfos;
            this.encryptedContentInfo = encryptedContentInfo;
            this.unprotectedAttrs = unprotectedAttrs;
        }
                             
        public EnvelopedData(
            ASN1Sequence seq)
        {
            int     index = 0;
            
            version = (DERInteger)seq.getObjectAt(index++);
            
            Object  tmp = seq.getObjectAt(index++);
    
            if (tmp is ASN1TaggedObject)
            {
                originatorInfo = OriginatorInfo.getInstance((ASN1TaggedObject)tmp, false);
                tmp = seq.getObjectAt(index++);
            }
    
            recipientInfos = ASN1Set.getInstance(tmp);
            
            encryptedContentInfo = EncryptedContentInfo.getInstance(seq.getObjectAt(index++));
            
            if(seq.size() > index)
            {
                unprotectedAttrs = ASN1Set.getInstance((ASN1TaggedObject)seq.getObjectAt(index), false);
            }
        }
        
        /**
         * return an EnvelopedData object from a tagged object.
         *
         * @param obj the tagged object holding the object we want.
         * @param explicit true if the object is meant to be explicitly
         *              tagged false otherwise.
         * @exception IllegalArgumentException if the object held by the
         *          tagged object cannot be converted.
         */
        public static EnvelopedData getInstance(
            ASN1TaggedObject obj,
            bool explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
        
        /**
         * return an EnvelopedData object from the given object.
         *
         * @param obj the object we want converted.
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static EnvelopedData getInstance(
            object obj)
        {
            if (obj == null || obj is EnvelopedData)
            {
                return (EnvelopedData)obj;
            }
            
            if (obj is ASN1Sequence)
            {
                return new EnvelopedData((ASN1Sequence)obj);
            }
            
            throw new ArgumentException("Invalid EnvelopedData: " + obj.GetType().Name);
        }
    
        public DERInteger getVersion()
        {
            return version;
        }
        
        public OriginatorInfo getOriginatorInfo()
        {
            return originatorInfo;
        }
    
        public ASN1Set getRecipientInfos()
        {
            return recipientInfos;
        }
    
        public EncryptedContentInfo getEncryptedContentInfo()
        {
            return encryptedContentInfo;
        }
    
        public ASN1Set getUnprotectedAttrs()
        {
            return unprotectedAttrs;
        }
    
        /** 
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * EnvelopedData ::= SEQUENCE {
         *     version CMSVersion,
         *     originatorInfo [0] IMPLICIT OriginatorInfo OPTIONAL,
         *     recipientInfos RecipientInfos,
         *     encryptedContentInfo EncryptedContentInfo,
         *     unprotectedAttrs [1] IMPLICIT UnprotectedAttributes OPTIONAL 
         * }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
            
            v.add(version);
    
            if (originatorInfo != null)
            {
                v.add(new DERTaggedObject(false, 0, originatorInfo));
            }
    
            v.add(recipientInfos);
            v.add(encryptedContentInfo);
    
            if (unprotectedAttrs != null)
            {
                v.add(new DERTaggedObject(false, 1, unprotectedAttrs));
            }
            
            return new BERSequence(v);
        }
    }
}
