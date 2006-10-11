using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.x509
{
    /**
     * <code>UserNotice</code> class, used in
     * <code>CertificatePolicies</code> X509 extensions (in policy
     * qualifiers).
     * <pre>
     * UserNotice ::= SEQUENCE {
     *      noticeRef        NoticeReference OPTIONAL,
     *      explicitText     DisplayText OPTIONAL}
     *
     * </pre>
     * 
     * @see PolicyQualifierId
     * @see PolicyInformation
     */
    public class UserNotice 
        : ASN1Encodable
    {
        NoticeReference noticeRef;
        DisplayText     explicitText;
       
        /**
         * Creates a new <code>UserNotice</code> instance.
         *
         * @param noticeRef a <code>NoticeReference</code> value
         * @param explicitText a <code>DisplayText</code> value
         */
        public UserNotice(
            NoticeReference noticeRef, 
            DisplayText explicitText) 
        {
            this.noticeRef = noticeRef;
            this.explicitText = explicitText;
        }
    
        /**
         * Creates a new <code>UserNotice</code> instance.
         *
         * @param noticeRef a <code>NoticeReference</code> value
         * @param str the explicitText field as a String. 
         */
        public UserNotice(
            NoticeReference noticeRef, 
            string str) 
        {
            this.noticeRef = noticeRef;
            this.explicitText = new DisplayText(str);
        }
    
       /**
        * Creates a new <code>UserNotice</code> instance.
        * <p>Useful from reconstructing a <code>UserNotice</code> instance
        * from its encodable/encoded form. 
        *
        * @param as an <code>ASN1Sequence</code> value obtained from either
        * calling @{link toASN1Object()} for a <code>UserNotice</code>
        * instance or from parsing it from a DER-encoded stream. 
        */
       public UserNotice(
           ASN1Sequence aseq) 
       {
           if (aseq.size() == 2)
           {
               noticeRef = NoticeReference.getInstance(aseq.getObjectAt(0));
               explicitText = DisplayText.getInstance(aseq.getObjectAt(1));
           }
           else if (aseq.size() == 1)
           {
               if (aseq.getObjectAt(0).toASN1Object() is ASN1Sequence)
               {
                   noticeRef = NoticeReference.getInstance(aseq.getObjectAt(0));
               }
               else
               {
                   explicitText = DisplayText.getInstance(aseq.getObjectAt(0));
               }
           }
        }
       
        public override ASN1Object toASN1Object() 
        {
            ASN1EncodableVector av = new ASN1EncodableVector();
          
            if (noticeRef != null)
            {
                av.add(noticeRef);
            }
            
            if (explicitText != null)
            {
                av.add(explicitText);
            }
             
            return new DERSequence(av);
        }
    }
}
