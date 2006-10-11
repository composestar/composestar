#region Using directives

using System;
using System.Text;
using org.bouncycastle.asn1.ocsp;
using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;

#endregion

namespace org.bouncycastle.ocsp
{
    public class RespData
    {
        public ResponseData data = null;

        /// <summary>
        /// Create using a asn1 ResponseData object as a reference.
        /// </summary>
        /// <param name="data">The reference ResponseData object.</param>
        public RespData(ResponseData data)
        {
            this.data = data;
        }

        /// <summary>
        /// The response version.
        /// </summary>
        /// <returns>The Version number.</returns>
        public int getVersion()
        {
            return data.getVersion().getValue().intValue()+1;
        }

        /// <summary>
        /// Return the Response ID.
        /// </summary>
        /// <returns>RespID</returns>
        public RespID getResponderId()
        {
            return new RespID(data.getResponderID());
        }

        /// <summary>
        /// The time the response was produced.
        /// </summary>
        /// <returns>Produced time.</returns>
        public DateTime getProducedAt()
        {
            return data.getProducedAt().toDateTime();
        }


        /// <summary>
        /// Return the Single Responses.
        /// </summary>
        /// <returns>An array..</returns>
        public SingleResp[] getResponses()
        {
            ASN1Sequence s = data.getResponses();
            SingleResp[] rs = new SingleResp[s.size()];

            for (int i = 0; i != rs.Length; i++)
            {
                rs[i] = new SingleResp(SingleResponse.getInstance(s.getObjectAt(i)));
            }

            return rs;
        }

        /// <summary>
        /// Return any exceptions.
        /// </summary>
        /// <returns>An X509Extensions object.</returns>
        public X509Extensions getResponseExtensions()
        {
            return data.getResponseExtensions();
        }
    }
}
