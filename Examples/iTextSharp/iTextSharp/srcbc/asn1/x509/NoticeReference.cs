using org.bouncycastle.asn1;

using System;
using System.Collections;

namespace org.bouncycastle.asn1.x509
{
    /**
     * <code>NoticeReference</code> class, used in
     * <code>CertificatePolicies</code> X509 V3 extensions
     * (in policy qualifiers).
     * 
     * <pre>
     *  NoticeReference ::= SEQUENCE {
     *      organization     DisplayText,
     *      noticeNumbers    SEQUENCE OF INTEGER }
     *
     * </pre> 
     * 
     * @see PolicyQualifierInfo
     * @see PolicyInformation
     */
    public class NoticeReference 
        : ASN1Encodable
    {
       DisplayText organization;
       ASN1Sequence noticeNumbers;
    
       /**
        * Creates a new <code>NoticeReference</code> instance.
        *
        * @param orgName a <code>String</code> value
        * @param numbers a <code>Vector</code> value
        */
       public NoticeReference (string orgName, ArrayList numbers) 
       {
          organization = new DisplayText(orgName);
    
          object o = numbers[0];
    
          ASN1EncodableVector av = new ASN1EncodableVector();
          if (o is Int32) {
              IEnumerator it = numbers.GetEnumerator();
    
             while (it.MoveNext()) {
                Int32 nm = (Int32) it.Current;
                DERInteger di = new DERInteger((int) nm);
                av.add (di);
             }
          }
    
          noticeNumbers = new DERSequence(av);
       }
    
       /**
        * Creates a new <code>NoticeReference</code> instance.
        *
        * @param orgName a <code>String</code> value
        * @param numbers an <code>ASN1EncodableVector</code> value
        */
       public NoticeReference (string orgName, ASN1Sequence numbers) 
       {
          organization = new DisplayText (orgName);
          noticeNumbers = numbers;
       }
    
       /**
        * Creates a new <code>NoticeReference</code> instance.
        *
        * @param displayTextType an <code>int</code> value
        * @param orgName a <code>String</code> value
        * @param numbers an <code>ASN1EncodableVector</code> value
        */
       public NoticeReference (int displayTextType,
                               string orgName, ASN1Sequence numbers) 
       {
          organization = new DisplayText(displayTextType, 
                                         orgName);
          noticeNumbers = numbers;
       }
    
       /**
        * Creates a new <code>NoticeReference</code> instance.
        * <p>Useful for reconstructing a <code>NoticeReference</code>
        * instance from its encodable/encoded form. 
        *
        * @param as an <code>ASN1Sequence</code> value obtained from either
        * calling @{link toASN1Object()} for a <code>NoticeReference</code>
        * instance or from parsing it from a DER-encoded stream. 
        */
       public NoticeReference (ASN1Sequence aseq) 
       {
          organization = DisplayText.getInstance(aseq.getObjectAt(0));
          noticeNumbers = (ASN1Sequence) aseq.getObjectAt(1);
       }
    
       public static NoticeReference getInstance (object aseq) 
       {
          if (aseq is NoticeReference)
          {
              return (NoticeReference)aseq;
          }
          else if (aseq is ASN1Sequence)
          {
              return new NoticeReference((ASN1Sequence)aseq);
          }
    
          throw new ArgumentException("unknown object in getInstance.");
       }
       
       /**
        * Describe <code>toASN1Object</code> method here.
        *
        * @return a <code>ASN1Object</code> value
        */
       public override ASN1Object toASN1Object() 
       {
          ASN1EncodableVector av = new ASN1EncodableVector();
          av.add (organization);
          av.add (noticeNumbers);
          return new DERSequence (av);
       }
    }
}
