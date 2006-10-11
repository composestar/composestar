#region Using directives

using System;
using System.Text;
using System.Collections;
using org.bouncycastle.crypto.digests;
using org.bouncycastle.crypto;

#endregion

namespace org.bouncycastle.security
{
    /// <summary>
    /// A class that returns new objects that perform cryptographic transformations by name.
    /// This class is experimental and should not be used.
    /// </summary>
    public class TransformationByName
    {
        static Hashtable xform = new Hashtable();

        static TransformationByName()
        {
            xform["Digest.SHA-1"] = "org.bouncycastle.crypto.digests.SHA1Digest";
            xform["Digest.SHA1"] = "Digest.SHA-1";
            xform["Digest.1.3.14.3.2.26"] = "Digest.SHA-1";
            xform["1.3.14.3.2.26"] = "Digest.SHA-1";
            xform["Digest.SHA"] = "Digest.SHA-1";
            xform["Digest.SHA-256"] = "org.bouncycastle.crypto.digests.SHA256Digest";
            xform["Digest.SHA-384"] = "org.bouncycastle.crypto.digests.SHA384Digest";
            xform["Digest.SHA-512"] = "org.bouncycastle.crypto.digests.SHA512Digest";
            xform["Digest.MD2"] = "org.bouncycastle.crypto.digests.MD2Digest";
            xform["Digest.MD4"] = "org.bouncycastle.crypto.digests.MD4Digest";
            xform["Digest.MD5"] = "org.bouncycastle.crypto.digests.MD5Digest";
            xform["Digest.1.2.840.113549.2.5"] = "Digest.MD5";
            xform["1.2.840.113549.2.5"] = "Digest.MD5";
            xform["Digest.RIPEMD128"] = "org.bouncycastle.crypto.digests.RIPEMD128Digest";
            xform["Digest.RIPEMD160"] = "org.bouncycastle.crypto.digests.RIPEMD160Digest";
            xform["Digest.RIPEMD256"] = "org.bouncycastle.crypto.digests.RIPEMD256Digest";
            xform["Digest.RIPEMD320"] = "org.bouncycastle.crypto.digests.RIPEMD320Digest";
            xform["Digest.TIGER"] = "org.bouncycastle.crypto.digests.TigerDigest";
        }



        /// <summary>
        /// Return a new digest by digest name.
        /// </summary>
        /// <param name="name">The name of the digest.</param>
        /// <returns>A new digest instance.</returns>
        /// <exception cref="ArgumentException">If name not found.</exception>
        public static Digest DigestByName(string name)
        {
            throw new NotSupportedException("Do not use this class...");

/*            name = name.ToUpper().Trim();
            object o = null;
            
            // There is a potential for an endless loop of references so this loop was used to stop that.

            for (int t = 0; t < 5; t++)
            {
                o = xform["Digest." + name];
                if (o == null) { throw new ArgumentException("Digest '" + name + "' not found."); }

                if (((string)o).StartsWith("org.bouncycastle.crypto.digests"))
                {
                    return (Digest)  Type.GetType((string)o).GetConstructor(new Type[0]).Invoke(new Object[0]);
                }

                if (o is string) { o = xform[(string)o]; }
            }
            throw new ArgumentException("Digest '" + name + "' not found.");*/
        }

    }
}
