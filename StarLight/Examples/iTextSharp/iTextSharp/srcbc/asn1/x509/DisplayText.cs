using org.bouncycastle.asn1;

using System;

namespace org.bouncycastle.asn1.x509
{
    /**
     * <code>DisplayText</code> class, used in
     * <code>CertificatePolicies</code> X509 V3 extensions (in policy qualifiers).
     *
     * <p>It stores a string in a chosen encoding. 
     * <pre>
     * DisplayText ::= CHOICE {
     *      ia5String        IA5String      (SIZE (1..200)),
     *      visibleString    VisibleString  (SIZE (1..200)),
     *      bmpString        BMPString      (SIZE (1..200)),
     *      utf8String       UTF8String     (SIZE (1..200)) }
     * </pre>
     * @see PolicyQualifierInfo
     * @see PolicyInformation
     */
    public class DisplayText 
        : ASN1Encodable
    {
       /**
        * Constant corresponding to ia5String encoding. 
        *
        */
       public const int CONTENT_TYPE_IA5STRING = 0;
       /**
        * Constant corresponding to bmpString encoding. 
        *
        */
       public const int CONTENT_TYPE_BMPSTRING = 1;
       /**
        * Constant corresponding to utf8String encoding. 
        *
        */
       public const int CONTENT_TYPE_UTF8STRING = 2;
       /**
        * Constant corresponding to visibleString encoding. 
        *
        */
       public const int CONTENT_TYPE_VISIBLESTRING = 3;
    
       /**
        * Describe constant <code>DISPLAY_TEXT_MAXIMUM_SIZE</code> here.
        *
        */
       public const int DISPLAY_TEXT_MAXIMUM_SIZE = 200;
       
       int contentType;
       DERString contents;
       
       /**
        * Creates a new <code>DisplayText</code> instance.
        *
        * @param type the desired encoding type for the text. 
        * @param text the text to store. Strings longer than 200
        * characters are truncated. 
        */
       public DisplayText (int type, string text) 
       {
          if (text.Length > DISPLAY_TEXT_MAXIMUM_SIZE) {
             // RFC3280 limits these strings to 200 chars
             // truncate the string
             text = text.Substring (0, DISPLAY_TEXT_MAXIMUM_SIZE);
          }
         
          contentType = type;
          switch (type) {
             case CONTENT_TYPE_IA5STRING:
                contents = (DERString)new DERIA5String (text);
                break;
             case CONTENT_TYPE_UTF8STRING:
                contents = (DERString)new DERUTF8String(text);
                break;
             case CONTENT_TYPE_VISIBLESTRING:
                contents = (DERString)new DERVisibleString(text);
                break;
             case CONTENT_TYPE_BMPSTRING:
                contents = (DERString)new DERBMPString(text);
                break;
             default:
                contents = (DERString)new DERUTF8String(text);
                break;
          }
       }
       
       /**
        * return true if the passed in String can be represented without
        * loss as a PrintableString, false otherwise.
        */
       private bool canBePrintable(
           string  str)
       {
           for (int i = str.Length - 1; i >= 0; i--)
           {
               if (str[i] > 0x007f)
               {
                   return false;
               }
           }
    
           return true;
       }
    
       /**
        * return true if the passed in String can be represented without
        * loss as a UTF8String, false otherwise.
        */
       private bool canBeUTF8(
           string  str)
       {
           for (int i = str.Length - 1; i >= 0; i--)
           {
               if (str[i] > 0x00ff)
               {
                   return false;
               }
           }
    
           return true;
       }
       
       /**
        * Creates a new <code>DisplayText</code> instance.
        *
        * @param text the text to encapsulate. Strings longer than 200
        * characters are truncated. 
        */
       public DisplayText (string text) 
       {
          // by default use UTF8String
          if (text.Length > DISPLAY_TEXT_MAXIMUM_SIZE) {
             text = text.Substring(0, DISPLAY_TEXT_MAXIMUM_SIZE);
          }
          
          if (canBeUTF8(text))
          {
              contentType = CONTENT_TYPE_UTF8STRING;
              contents = new DERUTF8String(text);
          }
          else
          {
              contentType = CONTENT_TYPE_BMPSTRING;
              contents = new DERBMPString(text);
          }
       }
    
       /**
        * Creates a new <code>DisplayText</code> instance.
        * <p>Useful when reading back a <code>DisplayText</code> class
        * from it's ASN1Encodable form. 
        *
        * @param de a <code>ASN1Encodable</code> instance. 
        */
       public DisplayText(DERString de)
       {
          contents = de;
       }
    
       public static DisplayText getInstance(object de) 
       {
          if (de is DERString)
          {
              return new DisplayText((DERString)de);
          }
          else if (de is DisplayText)
          {
              return (DisplayText)de;
          }
    
          throw new ArgumentException("illegal object in getInstance");
       }
    
       public override ASN1Object toASN1Object() 
       {
          return (ASN1Object)contents;
       }
    
       /**
        * Returns the stored <code>String</code> object. 
        *
        * @return the stored text as a <code>String</code>. 
        */
       public string getString() 
       {
          return contents.getString();
       }   
    }
}
