using System;

namespace org.bouncycastle.asn1
{
    /**
     * class for breaking up an OID into it's component tokens, ala
     * java.util.StringTokenizer. We need this class as some of the
     * lightweight Java environment don't support classes like
     * StringTokenizer.
     */
    public class OIDTokenizer
    {
        private string  oid;
        private int     index;
    
        public OIDTokenizer(
            string oid)
        {
            this.oid = oid;
            this.index = 0;
        }
    
        public bool hasMoreTokens()
        {
            return (index != -1);
        }
    
        public string nextToken()
        {
            if (index == -1)
            {
                return null;
            }
    
            string  token;
            int     end = oid.IndexOf('.', index);
    
            if (end == -1)
            {
                token = oid.Substring(index);
                index = -1;
                return token;
            }

//            token = oid.Substring(index, end);
            token = oid.Substring(index, end - index);
    
            index = end + 1;
            return token;
        }
    }
}
