#region Using directives

using System;
using System.Text;
using org.bouncycastle.asn1.ocsp;
using org.bouncycastle.crypto;
using org.bouncycastle.asn1.x509;
using org.bouncycastle.x509;
using org.bouncycastle.crypto.digests;
using org.bouncycastle.asn1;
#endregion

namespace org.bouncycastle.ocsp
{
    public class RespID
    {
        ResponderID id;

        /// <summary>
        /// Create a new RespID using a .asn1.ResponderID as a reference.
        /// </summary>
        /// <param name="id">The reference ResponderID.</param>
        public RespID(ResponderID id)
        {
            this.id = id;
        }

       
        /// <summary>
        /// Create using data from an X509Name.
        /// </summary>
        /// <param name="name">The reference X509Name </param>
        public RespID(X509Name name)
        {
            
                this.id = new ResponderID(name);
                        
        }

        /// <summary>
        /// Create using an public Asymmetric Key.
        /// </summary>
        /// <param name="key">A public Asymmetric key.</param>
        public RespID(AsymmetricKeyParameter key)
        {
            SubjectPublicKeyInfo info = SubjectPublicKeyInfoFactory.CreateSubjectPublicKeyInfo(key);
            byte[] b = info.getEncoded();
            SHA1Digest sha1 = new SHA1Digest();
            sha1.update(b, 0, b.Length);

            b = new byte[sha1.getDigestSize()];
            sha1.doFinal(b, 0);

            ASN1OctetString keyHash = new DEROctetString(b);
            this.id = new ResponderID(keyHash);
        }


        /// <summary>
        /// Returns a Reponder ID.
        /// </summary>
        /// <returns>An ASN1 Object.</returns>
        public ResponderID toASN1Object()
        {
            return id;
        }

        public override bool Equals(Object o)
        {
            if (!(o is RespID))
            {
                return false;
            }

            RespID obj = (RespID)o;

            return id.Equals(obj.id);
        }


        public override int GetHashCode()
        {
            return id.GetHashCode();
        }


    }
}
