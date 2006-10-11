using System.Text;

namespace org.bouncycastle.asn1.x509
{
    /**
     * class for breaking up an X500 Name into it's component tokens, ala
     * java.util.StringTokenizer. We need this class as some of the
     * lightweight Java environment don't support classes like
     * StringTokenizer.
     */
    public class X509NameTokenizer
    {
        private string          value;
        private int             index;
        private char            seperator;
        private StringBuilder    buf = new StringBuilder();
    
        public X509NameTokenizer(
            string  oid)
            : this(oid, ',')
        {
        }
        
        public X509NameTokenizer(
            string  oid,
            char    seperator)
        {
            this.value = oid;
            this.index = -1;
            this.seperator = seperator;
        }
    
        public bool hasMoreTokens()
        {
            return (index != value.Length);
        }
    
        public string nextToken()
        {
            if (index == value.Length)
            {
                return null;
            }
    
            int     end = index + 1;
            bool quoted = false;
            bool escaped = false;
    
            buf.Remove(0, buf.Length);
    
            while (end != value.Length)
            {
                char    c = value[end];
    
                if (c == '"')
                {
                    if (!escaped)
                    {
                        quoted = !quoted;
                    }
                    else
                    {
                        buf.Append(c);
                    }
                    escaped = false;
                }
                else
                {
                    if (escaped || quoted)
                    {
                        buf.Append(c);
                        escaped = false;
                    }
                    else if (c == '\\')
                    {
                        escaped = true;
                    }
                    else if (c == seperator)
                    {
                        break;
                    }
                    else
                    {
                        buf.Append(c);
                    }
                }
                end++;
            }
    
            index = end;
            return buf.ToString().Trim();
        }
    }
}
