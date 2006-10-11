#region Using directives

using System;

using System.Text;
using org.bouncycastle.asn1.ocsp;
using org.bouncycastle.asn1;
using System.IO;

#endregion

namespace org.bouncycastle.ocsp
{
    public class OCSPResp
    {
        private OCSPResponse    resp;

        public OCSPResp(OCSPResponse    resp)
        {
            this.resp = resp;
        }

        public OCSPResp(byte[] resp) : this(new MemoryStream(resp))
        {
          
        }
    
        public OCSPResp(Stream stream)
        {
            this.resp = OCSPResponse.getInstance(new ASN1InputStream(stream).readObject());
        }
    
         public Object getResponseObject()
            {
                ResponseBytes   rb = this.resp.getResponseBytes();

                if (rb == null)
                {
                    return null;
                }

                if (rb.getResponseType().Equals(OCSPObjectIdentifiers.id_pkix_ocsp_basic))
                {
                    try
                    {
                        ASN1InputStream aIn = new ASN1InputStream(new MemoryStream(rb.getResponse().getOctets()));
                        return new BasicOCSPResp(BasicOCSPResponse.getInstance(aIn.readObject()));
                    }
                catch (Exception e)
                {
                    throw new OCSPException("problem decoding object: " + e, e);
                }
            }

        return rb.getResponse();
    }

    /// <summary>
    /// Return a DER encoded OCSPResponse Object.
    /// </summary>
    /// <returns>Byte array.</returns>
    public byte[] getEncoded()
       {
        MemoryStream   bOut = new MemoryStream();
        ASN1OutputStream aOut = new ASN1OutputStream(bOut);
        aOut.writeObject(resp);
        return bOut.ToArray();
    }


    } // end of class OCSPResp.
}
